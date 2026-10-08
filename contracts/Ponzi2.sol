// SPDX-License-Identifier: CC-BY-SA-4.0
// This program has no boundedness bugs
pragma solidity >=0.8.0 <0.9.0;

contract Ponzi2 {
    address[] public investors; // dynamic array
    uint constant public MINIMUM_INVESTMENT = 1000;

    constructor () {
        investors.push(msg.sender);
    }

    function invest() payable public {
        require(msg.value >= MINIMUM_INVESTMENT, "too small investment");
        // innvestors.length is never zero because the constructor
        // populates it with at least a first investor
        uint eachInvestorGets = msg.value / investors.length;
        // if there are too many investors, the subsequent loop would
        // require too much gas, more than the maximum allowed by Ethereum,
        // effectively making it impossible for new investors to join
        for (uint i = 0; i < investors.length; i++) {
            (bool done, ) = payable(investors[i]).call{value:eachInvestorGets}("");
        }

        investors.push(msg.sender);
    }
}
