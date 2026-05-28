from abc import ABC
from parts import Gem, Card, TradeCard, UpgradeCard, PointCard, Player
from copy import deepcopy


class Move(ABC):
    pass


class Play(Move):

    def __init__(self, pos: int) -> None:
        self.pos = pos

    def position(self) -> int:
        return self.pos

    def __str__(self) -> str:
        return "Play " + str(self.pos)

    def __repr__(self) -> str:
        return self.__str__()

class Rest(Move):

    def __str__(self) -> str:
        return "Rest"

    def __repr__(self) -> str:
        return self.__str__()


def plan(player: "Player", card: "PointCard") -> list["Move"]:
    stack = [(player.copy(),[])]
    trace = []

    while stack:
        current_position, moves_done = stack.pop()

        if (current_position.has_gems(card.cost())):
            return moves_done # if the guy can buy the ting, then the moves worked, return the moves done

        to_move = [] # the list of possible moves to be done

        caravan = []
        for g in Gem:
            caravan.append(current_position.caravan[g])
        caravan = tuple(caravan)

        played = tuple(current_position.used)

        footprint = (caravan,played)
        if footprint in trace:
            continue
        else: trace.append(footprint)


        for i in range(player.hand_size()): # for every card currently in possession
            if current_position.can_play(i): # if that card is playable
                new_position = current_position.copy() # copy the current position - this is to branch the posistion out, testing the outcome of playing that card while maintaining the current position - kinda like git =))))
                new_position.play(i) # let that copy play the card, see what happens
                to_move.append((new_position,moves_done + [Play(i)])) # keep a record ot that, in case it works and we need to return it

        if any(current_position.used): # the condition is obvious - but the purpose of it is to make sure the player always have the most cards to play with, which means the widest range of possible solutions
            new_position = current_position.copy() 
            new_position.rest()
            to_move.append((new_position,moves_done + [Rest()]))
        
        stack.extend(to_move)
    
    return []