// SPDX-License-Identifier: CC-BY-SA-4.0
// This program has no boundedness bug
pragma solidity >=0.8.0 <0.9.0;

contract Lottery2 {

    // a round of the lottery: ticket sales can be added until the end time
    // has expired, after which a winner of the round can be drawn
    struct Round {
        uint endTime;
        TicketSale[] sales;
        uint totalTickets;
        address winner;
    }

    struct TicketSale {
        address buyer;
        uint tickets;
    }

    uint constant public TICKET_PRICE = 1000;
    uint constant public MAX_SALES = 5000;

    // all rounds created so far
    Round[] public rounds;

    // duration of each round (in number of blocks)
    uint public duration;

    // duration is a time specification
    constructor (uint _duration) {
        duration = _duration;
        Round storage round = rounds.push();
        round.endTime = block.timestamp + _duration;
    }

    function buy() payable public {
        require(msg.value > 0 && msg.value % TICKET_PRICE == 0, "you can only buy a positive integral number of tickets");

        Round storage last = rounds[rounds.length - 1];
        if (block.timestamp > last.endTime) {
            last = rounds.push();
            last.endTime = block.timestamp + duration;
        }
 
        if (last.sales.length < MAX_SALES) {
            uint quantity = msg.value / TICKET_PRICE;
            TicketSale memory sale = TicketSale(msg.sender, quantity);
            last.sales.push(sale);
            last.totalTickets += quantity;
        }
    }

    function drawWinner(uint roundNumber) public {
        // by using memory below, the assignment would create a copy in RAM of the round:
        // its update, inside the for loop, would be lost at the end of the function
        Round storage round = rounds[roundNumber];
        require(round.winner == address(0), "the winner has already been drawn");
        require(block.timestamp > round.endTime, "too early");
        require(round.totalTickets > 0, "no tickets have been sold for this round");

        uint numWinningTicket = random(round.totalTickets, 0xbeaf);

        for (uint i = 0; i < round.sales.length; i++) {
            TicketSale memory sale = round.sales[i];
            if (sale.tickets > numWinningTicket) {
                round.winner = sale.buyer;
                (bool success, ) = round.winner.call{value : TICKET_PRICE * round.totalTickets}("");
            }
            else
                numWinningTicket -= sale.tickets;
        }
    }

    // Yields a "random" number between 0 and max-1; the seed makes manipulations
    // by miners slightly more difficult
    function random(uint max, uint seed) view private returns (uint) {
        return uint(keccak256(bytes.concat(bytes32(seed), blockhash(block.number - 1)))) % max;
    }
}
