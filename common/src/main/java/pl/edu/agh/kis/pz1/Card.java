package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents a single card in a card game.
 * Each card consists of a rank and a suit.
 * This class provides methods for representing a card as a string, comparing cards, and calculating the rank value.
 *
 * @see Rank
 * @see Suit
 */
@Getter
public class Card {
    /**
     * The rank of the card, e.g., "Ace", "King", "10".
     */
    private final Rank rank;

    /**
     * The suit of the card, e.g., "♣", "♦", "♠", "♥".
     */
    private final Suit suit;

    /**
     * A map that associates each suit with its corresponding emoji symbol.
     */
    static Map<Suit, String> suiEmojiMap = new EnumMap<>(Suit.class);

    static {
        suiEmojiMap.put(Suit.CLUBS, "♣");
        suiEmojiMap.put(Suit.DIAMONDS, "♦");
        suiEmojiMap.put(Suit.SPADES, "♠");
        suiEmojiMap.put(Suit.HEARTS, "♥");
    }

    /**
     * Constructs a card with the specified rank and suit.
     *
     * @param rank The rank of the card.
     * @param suit The suit of the card.
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Constructs a new card by copying another card.
     *
     * @param other The card to copy.
     */
    public Card(Card other) {
        this.rank = other.rank;
        this.suit = other.suit;
    }

    /**
     * Returns the numerical value of the card's rank.
     * The value corresponds to the rank's assigned value, e.g., 14 for Ace, 13 for King, etc.
     *
     * @return The numerical value of the card's rank.
     */
    public int getRankValue() {
        return rank.getCardRank();
    }

    /**
     * Returns a string representation of the card.
     * For example: "King of ♠".
     *
     * @return A string representation of the card.
     */
    @Override
    public String toString() {
        return this.rank + " of " + suiEmojiMap.get(this.suit);
    }

    /**
     * Checks whether the given object is equal to this card.
     * Two cards are considered equal if they have the same suit and rank.
     *
     * @param obj The object to compare with this card.
     * @return True if the object is a card with the same suit and rank, otherwise false.
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Card){
            Card card = (Card)obj;
            return (suit == card.suit) && (rank == card.rank);
        }
        return false;
    }

    /**
     * Returns the hash code for this card.
     * The hash code is computed based on the card's rank and suit.
     *
     * @return The hash code of the card.
     */
    @Override
    public int hashCode(){
        return rank.hashCode() + suit.hashCode();
    }
}
