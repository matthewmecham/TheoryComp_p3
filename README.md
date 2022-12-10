# Project #: 3
* Author: Matt Mecham and Gary Brusse
* Class: CS121 Section 2
* Semester: Fall 2022
## Overview
This program reads a basic regex string from a file and builds an NFA
that represents that regex. It can take additional strings as input from
a file and use the NFA to determine wether or now the string is is the language
described by the regex.
## Reflection

Overall this project was not too bad. The information on Matt Might's website
was incredibly clear and easy to understand - that and the fact that between
OS, Programming Languages, and now Theory of Computation, recursive descent 
parsers have shown up a few times now. Also, the two hints provided, not needing
subclasses and having NFA objects instead of RegEx objects during descent, were
plenty helpful to get this program started.

The main hiccup, was forgetting a fairly fundamental property of NFAs. That being
the fact that one or more NFAs can be concatonated together with epsilon transitions.
Too long was spent thinking, "How do we combine these NFAs in a non-tedious way." Once
that was realized it was just a few paths through the debugger to watch if our logic
made sense as well as a couple hand-drawn NFAs following along. 

## Compiling and Using
This code was compiled using JDK 17. It requires CS361NFA.jar to be included in the 
classpath. Something like:
javac -cp ".:./CS361FA.jar" re/REDriver.java 

and to run the program:
java -cp ".:./CS361FA.jar" re.REDriver ./tests/p3tc1.txt

## Sources used
- http://matt.might.net/articles/parsing-regex-with-recursive-descent/
- The provied NFA Docs
