from enum import Enum
from abc import ABC, abstractmethod
from copy import deepcopy

class Gem(Enum):

    YELLOW = 0
    GREEN = 1
    BLUE = 2
    PINK = 3

    def next_gem(self) -> "Gem":
        match self:
            case Gem.YELLOW:
                return Gem.GREEN
            case Gem.GREEN:
                return Gem.BLUE
            case Gem.BLUE:
                return Gem.PINK
            case Gem.PINK:
                return Gem.PINK

    def __str__(self) -> str:
        match self:
            case Gem.YELLOW:
                return "Y"
            case Gem.GREEN:
                return "G"
            case Gem.BLUE:
                return "B"
            case Gem.PINK:
                return "P"

    def __repr__(self) -> str:
        return self.__str__()


class Card(ABC):

    @abstractmethod
    def __init__(self, gem_cost: dict["Gem", int]) -> None:
        self.gem_cost = gem_cost

    def cost(self) -> dict["Gem", int]:
        return self.gem_cost


class TradeCard(Card):

    def __init__(self, gem_cost: dict["Gem", int], gem_value: dict["Gem", int]) -> None:
        super().__init__(gem_cost)
        self.gem_value = gem_value

    def value(self) -> dict["Gem", int]:
        return self.gem_value

    def __str__(self) -> str:
        return f"Trade[{super().cost()}, {self.gem_value}]"

    def __repr__(self) -> str:
        return self.__str__()


class UpgradeCard(Card):

    def __init__(self, upgrade_count: int) -> None:
        super().__init__(dict())
        self.upgrade_count = upgrade_count

    def upgrades(self) -> int:
        return self.upgrade_count

    def __str__(self) -> str:
        return f"Upgrade[{self.upgrade_count}]"

    def __repr__(self) -> str:
        return self.__str__()


class PointCard(Card):

    def __init__(self, cost: dict["Gem", int], point_value: int) -> None:
        super().__init__(cost)
        self.point_value = point_value

    def value(self) -> int:
        return self.point_value

    def __str__(self) -> str:
        return f"Point[{super().cost()},{self.point_value}]"

    def __repr__(self) -> str:
        return self.__str__()


class Player:

    def __init__(self, hand: list["Card"], caravan: dict["Gem", int]) -> None:
        self.hand = hand
        self.caravan = caravan
        self.used = [False] * len(self.hand)

    def has_gems(self, cost: dict["Gem", int]) -> bool:

        for gem in cost.keys():
            if cost[gem] > self.caravan[gem]:
                return False

        return True
    
    def can_play(self, position: int) -> bool:
        if position < 0 or position >= len(self.hand):
            return False

        if self.used[position]:
            return False

        if isinstance(self.hand[position], TradeCard):
            return self.has_gems(self.hand[position].cost())

        return True

    def play(self, position: int) -> None:
        if self.can_play(position):
            self.used[position] = True
            if isinstance(self.hand[position], TradeCard):
                for gem in self.hand[position].cost().keys():
                    self.caravan[gem] -= self.hand[position].cost()[gem]
                for gem in self.hand[position].value().keys():
                    self.caravan[gem] += self.hand[position].value()[gem]
            elif isinstance(self.hand[position], UpgradeCard):
                upgrades_to_go = self.hand[position].upgrades()
                for gem in Gem:
                    m = min(upgrades_to_go, self.caravan[gem])
                    self.caravan[gem] -= m
                    self.caravan[gem.next_gem()] += m
                    upgrades_to_go -= m

    def rest(self) -> None:
        for i in range(len(self.used)):
            self.used[i] = False

    def copy(self) -> "Player":
        return deepcopy(self)

    def hand_size(self) -> int:
        return len(self.hand)

    def __str__(self) -> str:
        return f"Player[{self.hand}, {self.caravan}]"

    def __repr__(self) -> str:
        return self.__str__()
