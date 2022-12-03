package re;

import fa.nfa.NFA;

public class RE implements re.REInterface {

    private String inputString;
    private NFA nfa;

    public RE(String inputString) {
        this.inputString = inputString;
        nfa = new NFA();
    }

    private NFA choice(NFA thisOne, NFA thatOne) {

        return nfa;
    }

    private NFA sequence(NFA first, NFA second) {

        return nfa;
    }

    private NFA repetition(NFA internal) {

        return nfa;
    }

    private NFA primitive(char c)
    {

        return nfa;
    }

    public NFA getNFA() {
        NFA ret = regex();
        return ret;
    }

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

    private NFA term() {
        //needs to be static constant... not quite sure how to implement atm
        //Could possibly just be a blank node or something
        NFA factorNFA = null;

        while (more() && peek() != ')' && peek() != '|') {
            NFA nextFactorNFA = factor();
            factorNFA = sequence(factorNFA, nextFactorNFA);
        }
        return factorNFA;
    }

    private NFA factor() {
        NFA baseNFA = base();

        while (more() && peek() == '*') {
            eat('*');
            baseNFA = repetition(baseNFA);
        }
        return baseNFA;
    }

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