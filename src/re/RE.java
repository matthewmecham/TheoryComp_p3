package re;

import fa.nfa.NFA;

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
    private NFA nfa;

    public RE(String inputString) {
        this.inputString = inputString;
        nfa = new NFA();
    }

    //TODO: Method needs to account for a '|" situation
    // Needs to appropriately call NFA class methods to create
    // a state that has an OR transition, basically. I am not
    // positive on parameter types
    private NFA choice(NFA thisOne, NFA thatOne) {

        return nfa;
    }

    //TODO: Method needs to account for when when first state leads to just another
    // Possibly like, aabb.. Might go something like 0 -a-> 1 -a-> 2 or something like that
    // Not sure atm if a sequence is also something like (a|b)a where (a|b) is "first" and
    // a is "second"... Probably, though?
    private NFA sequence(NFA first, NFA second) {

        return nfa;
    }

    //TODO: Method needs to account for '*' case
    private NFA repetition(NFA internal) {

        return nfa;
    }

    //TODO: The linked algorithm has this and not quite sure what to do with it.
    // Perhaps how we know the name of the transition. This is the most basic piece
    // or the recursive parse.
    private NFA primitive(char c)
    {

        return nfa;
    }

    public NFA getNFA() {
        NFA ret = regex();
        return ret;
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
        NFA factorNFA = null; //Since it could possibly be empty? Not sure about this one

        while (more() && peek() != ')' && peek() != '|') {
            NFA nextFactorNFA = factor();
            factorNFA = sequence(factorNFA, nextFactorNFA);
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