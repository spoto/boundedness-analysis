// SPDX-License-Identifier: CC-BY-SA-4.0
// This program has a boundedness bug inside invest()
pragma solidity ^0.4.24;

contract Ponzi1 {
    address[] public investors; // dynamic array
    uint public minimumInvestment;

    constructor () public {
        investors.push(msg.sender);
        minimumInvestment = 1000;
    }

    function invest() payable public {
        require(msg.value >= minimumInvestment, "too small investment");

        // investors.length is never zero because the constructor
        // populates it with at least a first investor
        uint eachInvestorGets = msg.value / investors.length;
        // if there are too many investors, the subsequent loop would
        // require too much gas, more than the maximum allowed by Ethereum,
        // effectively making it impossible for new investors to join
        // BOUNDEDNESS BUG
        for (uint i = 0; i < investors.length; i++)
            investors[i].call.value(eachInvestorGets)("");

        investors.push(msg.sender);
	minimumInvestment = msg.value * 11 / 10;
    }
}
