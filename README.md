# Boundedness Analysis

This article defines the notion of boundedness for containers used in smart contracts
and provides a sound static analysis, based on abstract interpretation, to identify potential
boundedness bugs in the code of the contracts. The analysis is language-agnostic, working
on a high-level definition of the programming language used for the smart contracts.

You can compile the article with Latex:

```
pdflatex main.tex
bibtex main
pdflatex main.tex
pdflatex main.tex
```

The implementation is inside the **it-univr-smartcontracts** Java project. It can be
compiled with Maven:

```
cd it-univr-smartcontracts
mvn clean install
```

You can also run the testcases as

```
mvn clean test
```

Finally, you can run specific testcases and print the result of the static analysis as

```
mvn clean test -Dtest=Ponzi1Tests -Dprint
mvn clean test -Dtest=Ponzi2Tests -Dprint
mvn clean test -Dtest=Lottery1Tests -Dprint
mvn clean test -Dtest=Lottery2Tests -Dprint
```
