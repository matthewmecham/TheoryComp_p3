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

    //TODO: Method needs to account for a '|" situation
    private NFA choice(NFA thisOne, NFA thatOne) {
        NFA resultNFA = new NFA();

        for(State f : thisOne.getFinalStates())
        {
        }
        return resultNFA;
    }

    //TODO: Javadoc this sucka
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

    //TODO: JavaDoc this sucka
    private NFA repetition(NFA internal) {

        for(State f : internal.getFinalStates())
        {
            internal.addTransition(f.getName() , 'e', internal.getStartState().getName());
            internal.addTransition(internal.getStartState().getName(), 'e', f.getName());
        }

        return internal;
    }

    //TODO: The linked algorithm has this and not quite sure what to do with it.
    // Perhaps how we know the name of the transition. This is the most basic piece
    // or the recursive parse.
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

    public NFA getNFA() {
        NFA resultNFA = regex();
        Set<Character> alphabet = new HashSet<Character>();
        alphabet.add('a');
        alphabet.add('b');
        resultNFA.addAbc(alphabet);
        return resultNFA;
    }

    //The lead terminal of our CFG
    //Regex:    term '|' regex
    //          | term
    private NFA regex() {
        NFA termNFA = term();

        if (more() && peek() == '|') {
            eat('|');
            NFA regexNFA = regex();
            return choice(termNFA, regexNFA);
        } else {
            return termNFA;
        }
    }

    // Next level of CFG:
    // term:    { factor } "Possibly empty sequence of factors"
    private NFA term() {
        NFA factorNFA = new NFA(); //Since it could possibly be empty? Not sure about this one
        factorNFA.addStartState(stateName + stateNum++);
        ((NFAState)factorNFA.getStartState()).setFinal();

        while (more() && peek() != ')' && peek() != '|') {
            NFA nextFactorNFA = factor();
            factorNFA = sequence(factorNFA, nextFactorNFA); //I believe a good place to add transitions
        }
        return factorNFA;
    }

    // Next level of CFG:
    // factor:  base '*'
    //          | base
    private NFA factor() {
        NFA baseNFA = base();

        while (more() && peek() == '*') {
            eat('*');
            baseNFA = repetition(baseNFA);
        }
        return baseNFA;
    }

    // Final level of CFG:
    // base:    char
    //          | '(' regex ')'
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