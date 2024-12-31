package pl.edu.agh.kis.pz1;

import java.util.*;

/**
 * This class is responsible for determining the rank of a hand of cards in poker.
 * It evaluates a given list of cards and returns the corresponding hand rank, such as
 * "Royal Flush", "Full House", "Pair", etc.
 * It also identifies the distinguishing cards in the hand that are used to determine the winner.
 */
public class HandRanker {

    /**
     * Evaluates the rank of a hand of cards.
     * It checks various hand types in a specific order and returns the highest ranking hand found.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A string representing the rank of the hand (e.g., "Royal Flush", "Pair").
     */
    String rank(List<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getRankValue));
        if(!checkForRoyalFlush(cards).isEmpty()){
            return "Royal Flush";
        }
        if(!checkForStraightFlush(cards).isEmpty()){
            return "Straight Flush";
        }
        if (!checkForFourOfAKind(cards).isEmpty()) {
            return "Four Of A Kind";
        }
        if (!checkForFullHouse(cards).isEmpty()) {
            return "Full House";
        }
        if (!checkForFlush(cards).isEmpty()) {
            return "Flush";
        }
        if (!checkForStraight(cards).isEmpty()) {
            return "Straight";
        }
        if (!checkForThreeOfAKind(cards).isEmpty()) {
            return "Three Of A Kind";
        }
        if (!checkForTwoPair(cards).isEmpty()) {
            return "Two Pair";
        }
        if (!checkForPair(cards).isEmpty()) {
            return "Pair";
        }
        return "High Card"; // If no hand is found, return "High Card"
    }

    /**
     * Determines the distinguishing cards of a hand, which are used to break ties or determine the winner.
     * This method returns the most significant card(s) for each hand rank.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list of integers representing the rank value(s) of the distinguishing card(s).
     */
    public List<Integer> distinguishmentCards(List<Card> cards) {
        cards.sort(Comparator.comparingInt(Card::getRankValue));
        if(!checkForRoyalFlush(cards).isEmpty()){
            return checkForRoyalFlush(cards);
        }
        if(!checkForStraightFlush(cards).isEmpty()){
            return checkForStraightFlush(cards);
        }
        if (!checkForFourOfAKind(cards).isEmpty()) {
            return checkForFourOfAKind(cards);
        }
        if (!checkForFullHouse(cards).isEmpty()) {
            return checkForFullHouse(cards);
        }
        if (!checkForFlush(cards).isEmpty()) {
            return checkForFlush(cards);
        }
        if (!checkForStraight(cards).isEmpty()) {
            return checkForStraight(cards);
        }
        if (!checkForThreeOfAKind(cards).isEmpty()) {
            return checkForThreeOfAKind(cards);
        }
        if (!checkForTwoPair(cards).isEmpty()) {
            return checkForTwoPair(cards);
        }
        if (!checkForPair(cards).isEmpty()) {
            return checkForPair(cards);
        }
        return checkForHighCard(cards);
    }

    /**
     * Checks if the hand is a Royal Flush. A Royal Flush is a Straight Flush with the highest cards (10, J, Q, K, A).
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the highest card in the Royal Flush (if present), otherwise an empty list.
     */
    List<Integer> checkForRoyalFlush(List<Card> cards) {
        if (!checkForStraightFlush(cards).isEmpty() && cards.get(4).getRankValue() == 14) {
            return List.of(14); // Highest card in the Royal Flush (Ace)
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is a Straight Flush. A Straight Flush is a sequence of 5 cards of the same suit.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the highest card in the Straight Flush (if present), otherwise an empty list.
     */
    List<Integer> checkForStraightFlush(List<Card> cards) {
        List<Integer> straight = checkForStraight(cards);
        List<Integer> flush = checkForFlush(cards);
        if (!straight.isEmpty() && !flush.isEmpty()) {
            return List.of(cards.get(4).getRankValue()); // Highest card in the Straight Flush
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is Four of a Kind. Four of a Kind occurs when 4 cards of the same rank are present.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the 4-of-a-kind cards and the rank of the kicker (5th card).
     */
    List<Integer> checkForFourOfAKind(List<Card> cards) {
        Map<Rank, Integer> hashmap = new EnumMap<>(Rank.class);
        for (Card card : cards) {
            if (hashmap.containsKey(card.getRank())) {
                hashmap.put(card.getRank(), hashmap.get(card.getRank()) + 1);
                if (hashmap.get(card.getRank()) == 4) {
                    return List.of(card.getRankValue(), findMissingCard(cards, card.getRankValue()));
                }
            } else hashmap.put(card.getRank(), 1);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is a Full House. A Full House is a combination of a Three of a Kind and a Pair.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank values of the Three of a Kind and the Pair.
     */
    List<Integer> checkForFullHouse(List<Card> cards) {
        List<Integer> twoPairCards = checkForTwoPair(cards);
        List<Integer> threeOfAKindCards = checkForThreeOfAKind(cards);
        if (!twoPairCards.isEmpty() && !threeOfAKindCards.isEmpty()) {
            return List.of(threeOfAKindCards.get(0),
                    !Objects.equals(threeOfAKindCards.get(0), twoPairCards.get(0)) ? twoPairCards.get(0) : twoPairCards.get(1));
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is a Flush. A Flush is 5 cards of the same suit, but not in sequence.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the highest card in the Flush (if present), otherwise an empty list.
     */
    List<Integer> checkForFlush(List<Card> cards) {
        Set<Suit> suits = new HashSet<>();
        for (Card card : cards) {
            suits.add(card.getSuit());
        }
        if (suits.size() == 1) {
            return List.of(cards.get(4).getRankValue()); // Highest card in the flush
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is a Straight. A Straight is 5 cards in sequential order, regardless of suit.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the highest card in the Straight (if present), otherwise an empty list.
     */
    List<Integer> checkForStraight(List<Card> cards) {
        if (cards.get(0).getRankValue() == 2 &&
                cards.get(1).getRankValue() == 3 &&
                cards.get(2).getRankValue() == 4 &&
                cards.get(3).getRankValue() == 5 &&
                cards.get(4).getRankValue() == 14) {
            return List.of(5); // Special case: Ace counts as 1 in a straight
        }

        for (int i = 0; i < 4; i++) {
            if (cards.get(i).getRankValue() != cards.get(i + 1).getRankValue() - 1) {
                return new ArrayList<>();
            }
        }

        return List.of(cards.get(4).getRankValue()); // Highest card in the straight
    }

    /**
     * Checks if the hand is Three of a Kind. Three of a Kind occurs when 3 cards of the same rank are present.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the Three of a Kind (if present), otherwise an empty list.
     */
    List<Integer> checkForThreeOfAKind(List<Card> cards) {
        Map<Rank, Integer> hashmap = new EnumMap<>(Rank.class);
        for (Card card : cards) {
            if (hashmap.containsKey(card.getRank())) {
                hashmap.put(card.getRank(), hashmap.get(card.getRank()) + 1);
                if (hashmap.get(card.getRank()) == 3) {
                    List<Integer> res = new ArrayList<>();
                    res.add(cards.get(0).getRankValue());
                    return res;
                }
            } else hashmap.put(card.getRank(), 1);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand is Two Pair. Two Pair occurs when there are two pairs of cards with the same rank.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank values of the two pairs (if present), otherwise an empty list.
     */
    List<Integer> checkForTwoPair(List<Card> cards) {
        int counterOfPairs = 0;
        Map<Rank, Integer> hashmap = new EnumMap<>(Rank.class);
        for (Card card : cards) {
            if (hashmap.containsKey(card.getRank())) {
                hashmap.put(card.getRank(), hashmap.get(card.getRank()) + 1);
                if (hashmap.get(card.getRank()) == 2) {
                    counterOfPairs++;
                }
                if (counterOfPairs == 2) {
                    return findPairs(hashmap);
                }
            } else hashmap.put(card.getRank(), 1);
        }
        return new ArrayList<>();
    }

    /**
     * Checks if the hand contains a Pair. A Pair occurs when there are two cards of the same rank.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list containing the rank value of the Pair (if present), otherwise an empty list.
     */
    List<Integer> checkForPair(List<Card> cards) {
        Map<Rank, Integer> hashmap = new EnumMap<>(Rank.class);
        for (Card card : cards) {
            if (hashmap.containsKey(card.getRank())) {
                return List.of(card.getRankValue()); // Return the rank value of the pair
            } else hashmap.put(card.getRank(), 1);
        }
        return new ArrayList<>();
    }

    /**
     * Determines the distinguishing cards for a "High Card" hand.
     * The highest card is returned as the distinguishing card.
     *
     * @param cards A list of 5 cards to evaluate.
     * @return A list of rank values sorted in descending order.
     */
    List<Integer> checkForHighCard(List<Card> cards) {
        List<Integer> res = new ArrayList<>();
        for(Card card: cards) {
            res.add(card.getRankValue());
        }
        Collections.reverse(res); // Sort in descending order
        return res;
    }

    /**
     * Finds the rank value of the missing card when there are 4 of the same rank in the hand.
     *
     * @param cards A list of 5 cards to evaluate.
     * @param rankValue The rank value of the 4 of a kind.
     * @return The rank value of the missing card.
     */
    private int findMissingCard(List<Card> cards, int rankValue) {
        for(Card card2: cards) {
            if(card2.getRankValue() != rankValue) {
                return card2.getRankValue();
            }
        }
        return -1; // In case no missing card is found
    }

    /**
     * Finds the rank values of the two pairs in a Two Pair hand.
     *
     * @param hashmap A map of card ranks and their respective frequencies.
     * @return A list of rank values for the two pairs.
     */
    private List<Integer> findPairs(Map<Rank, Integer> hashmap) {
        List<Integer> res = new ArrayList<>();
        for (Map.Entry<Rank, Integer> entry : hashmap.entrySet()) {
            Rank key = entry.getKey();
            Integer value = entry.getValue();
            if (value == 2) {
                res.add(key.getCardRank());
            }
        }
        res.sort(Collections.reverseOrder()); // Sort in descending order
        return res;
    }
}
