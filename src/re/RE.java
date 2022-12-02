package re;

import fa.nfa.NFA;

public class RE implements re.REInterface {

    private String inputString;

    public RE(String inputString) {
        this.inputString = inputString;
    }

    //Maybe needed.... Not quite sure at this point to be honest
    //Probably needed
    class Choice extends NFA {
        private NFA thisOne;
        private NFA thatOne;

        public Choice(NFA thisOne, NFA thatOne) {
            this.thisOne = thisOne;
            this.thatOne = thatOne;
        }
    };

    //Also maybe need. This might be the tree aspect though, and instead
    //will need to build NFA node relationships with these
    class Sequence extends NFA {
        private NFA first;
        private NFA second;

        public Sequence (NFA first, NFA second) {
            this.first = first;
            this.second = second;
        }
    }

    //Represents empty regex
    //Maybe need? I dunno
    class Blank extends NFA {
    }

    class Repetition extends NFA {
        private NFA internal;

        public Repetition(NFA internal) {
            this.internal = internal;
        }
    }

    class Primitive extends NFA {
        private char c;

        public Primitive(char c) {
            this.c = c;
        }
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
            return new Choice(termNFA, regexNFA);
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
            factorNFA = new Sequence(factorNFA, nextFactorNFA);
        }
        return factorNFA;
    }

    private NFA factor() {
        NFA baseNFA = base();

        while (more() && peek() == '*') {
            eat('*');
            baseNFA = new Repetition(baseNFA);
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
                return new Primitive(next());
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