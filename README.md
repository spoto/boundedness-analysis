# Boundedness Analysis

This article defines the notion of boundedness for containers used in smart contracts
and provides a sound static analysis, based on abstract interpretation, to identify potential
boundedness bugs in the code of the contracts. The analysis is language-agnostic, working
on a high-level definition of the programming language used for the smart contracts.

The implementation is inside the *it-univr-smartcontracts* Java project, that can be
compiled with Maven (*mvn clean install*).
