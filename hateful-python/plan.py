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
    pass
