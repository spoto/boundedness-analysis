// SPDX-License-Identifier: CC-BY-SA-4.0
// This program has no boundedness bug inside invest()
pragma solidity ^0.8.4;

contract Ponzi2 {
    address[] public investors; // dynamic array
    uint public start;
    uint public minimumInvestment;

    constructor () {
        investors.push(msg.sender);
        start = 0;
        minimumInvestment = 1000;
    }

    function invest() payable public {
        require(msg.value >= minimumInvestment, "too small investment");

        while (investors.length - start > 100)
            start++;

        // investors.length is never zero because the constructor
        // populates it with at least a first investor
        uint eachInvestorGets = msg.value / investors.length;
        // the subsequent loop considers 100 investors at most:
        // NO BOUNDEDNESS BUG
        for (uint i = start; i < investors.length; i++)
            investors[i].call{value:eachInvestorGets}("");

        investors.push(msg.sender);
        minimumInvestment = msg.value * 11 / 10;
    }
}
