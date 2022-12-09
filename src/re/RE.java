package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

import java.util.*;

// Our alphabet: { a, b }
// General CFG for this regex
// regex:   term '|' regex
//          | term
// term:    { factor } "possibly empty sequence of factors"
// factor:  base '*'
//          | base
// base:    char
//          | '(' regex ')'

/**
 * Matt Mecham and Gary Brusse
 * This class converts a regex string to an NFA using a recursive descent parser
 * While parsing the regex, this class makes a series of smaller NFAs. While returning
 * through the recursion, these smaller NFAs are concatenated together.
 */

public class RE implements re.REInterface {

    private String inputString;
    private String stateName;
    private int stateNum;

    public RE(String inputString) {
        Set<Character> abc = new LinkedHashSet<Character>();
        this.inputString = inputString;
        this.stateName = "q";
        this.stateNum = 0;
    }


    /**
     * This method handles the case of '|' in our CFG
     * @param thisOne - complete term
     * @param thatOne - first term of regex still being parsed
     * @return the concatenation of those two NFAs
     */
    private NFA choice(NFA thisOne, NFA thatOne) {
        NFA resultNFA = new NFA();
        //New start state from which we transition to thisOne OR thatOne
        resultNFA.addStartState(stateName + stateNum++);
        //Copy over all states
        resultNFA.addNFAStates(thisOne.getStates());
        resultNFA.addNFAStates(thatOne.getStates());
        //The new start state has two e transitions to the start of both thisOne and thatOne
        resultNFA.addTransition(resultNFA.getStartState().getName(), 'e', thisOne.getStartState().getName());
        resultNFA.addTransition(resultNFA.getStartState().getName(), 'e', thatOne.getStartState().getName());
        return resultNFA;
    }

    /**
     * This method handles the case of sequential NFAs
     * For example ab: The NFA representing a gets concatenated with the
     * NFA representing b
     * @param first - first NFA in a sequence
     * @param second - second NFA in a sequence
     * @return the concatenation of the two NFAs
     */
    private NFA sequence(NFA first, NFA second) {
        NFA resultNFA = new NFA();
        resultNFA.addNFAStates(first.getStates());
        resultNFA.addNFAStates(second.getStates());
        //make e transitions from first final states to what was the second start state
        //This is acting as concatenation between two NFAs
        for (State f: first.getFinalStates()) {
            resultNFA.addTransition(f.getName(), 'e', second.getStartState().getName());
        }

        //Now clear the final state status from the states grabbed from first
        for(State f: first.getFinalStates())
            for(State s: resultNFA.getFinalStates())
            {
                if(f.getName() == s.getName())
                {
                    ((NFAState)s).setNonFinal();
                }
            }
        //Now make sure resultNFA start state is the right one:
        resultNFA.addStartState(first.getStartState().getName());

        return resultNFA;
    }

    /**
     * Handles the case '*' in our CFG
     * @param internal - the NFA that needs to be repeated 0 or more times
     * @return the NFA with e transitions from start to final and from final to start
     */
    private NFA repetition(NFA internal) {

        //create e transtions from the final state back to the start and from the
        //start state to the final
        for(State f : internal.getFinalStates())
        {
            internal.addTransition(f.getName() , 'e', internal.getStartState().getName());
            internal.addTransition(internal.getStartState().getName(), 'e', f.getName());
        }

        return internal;
    }

    /**
     * Handles the most basic case, a single transition
     * @param c - the char on which we transition
     * @return the most simple NFA: start -- c --> final
     */
    private NFA primitive(char c)
    {
        NFA resultNFA = new NFA();
        int first;
        int second;
        first = stateNum;
        resultNFA.addStartState(stateName + stateNum++);
        second = stateNum;
        resultNFA.addFinalState(stateName + stateNum++);
        resultNFA.addTransition(stateName + first, c, stateName + second);
        return resultNFA;
    }

    /**
     * The client call to convert the Regex to an NFA
     * @return an NFA representing the Regex
     */
    public NFA getNFA() {
        NFA resultNFA = regex(); //recursive descent call
        //Populate NFA params
        Set<Character> alphabet = new HashSet<Character>();
        alphabet.add('a');
        alphabet.add('b');
        resultNFA.addAbc(alphabet);
        return resultNFA;
    }

    //The lead nonterminal of our CFG
    //Regex:    term '|' regex
    //          | term
    /**
     * Parses first LHS nonterminal - handles OR
     * @return a termNFA
     */
    private NFA regex() {
        NFA termNFA = term();

        if (more() && peek() == '|') { //See a '|' need to hand OR case
            eat('|');
            NFA regexNFA = regex();
            return choice(termNFA, regexNFA);
        } else {
            return termNFA; //else plain ol term
        }
    }

    // Next level of CFG:
    // term:    { factor } "Possibly empty sequence of factors"
    /**
     * Parses second nonterminal - accounts for sequences
     * @return a factorNFA
     */
    private NFA term() {
        NFA factorNFA = new NFA(); //Since it could possibly be empty? Not sure about this one
        factorNFA.addStartState(stateName + stateNum++);
        ((NFAState)factorNFA.getStartState()).setFinal();

        while (more() && peek() != ')' && peek() != '|') { //if no funny business - make plain ol sequence
            NFA nextFactorNFA = factor();
            factorNFA = sequence(factorNFA, nextFactorNFA);
        }
        return factorNFA;
    }

    // Next level of CFG:
    // factor:  base '*'
    //          | base
    /**
     * Parses factor nonterminal - accounts for repetition
     * @return baseNFA
     */
    private NFA factor() {
        NFA baseNFA = base();

        while (more() && peek() == '*') { //if see *, need to repeat NFA
            eat('*');
            baseNFA = repetition(baseNFA);
        }
        return baseNFA;
    }

    // Final level of CFG:
    // base:    char
    //          | '(' regex ')'
    /**
     * Parses final level of CFG - either primitive or a
     * parenthesized regex
     * @return a primitive or another regex
     */
    private NFA base() {

        switch (peek()) {
            case '(':
                eat('(');
                NFA ret = regex();
                eat(')');
                return ret;

            default:
                return primitive(next());
        }
    }

    /**
     * Method to look at first index of the current input string
     * @return returns the char at index 0 of current of input string
     */
    private char peek() {
        return inputString.charAt(0);
    }

    /**
     * Method to return the next char in the input string AND removes it from inputString.
     * peek() returns the char at 0, then eat(ret) removes that char from the inputString
     * @return returns char at index 0
     */
    private char next() {
        char ret = peek();
        eat(ret);
        return ret;
    }

    /**
     * If the item matches the first char in the inputSTring, remove it.
     * This could be made more efficient walking a pointer through the inputString
     * instead of calculating substring at every step. Maybe cross that bridge some day.
     * @param item - the char we are trying to match at the beginning of current inputString
     */
    private void eat(char item) {
        if (peek() == item) {
            inputString = inputString.substring(1); //returns substring starting from index 1 to end of string
        }
        else {
            throw new RuntimeException("Expected: " + item + "; got: " + peek());
        }
    }

    /**
     * method used to indicate if there is any more chars in the inputString
     * @return true if more chars in inputString, false otherwise
     */
    private boolean more() {
        return inputString.length() > 0;
    }
};