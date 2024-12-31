package pl.edu.agh.kis.pz1;

import lombok.Getter;

/**
 * The {@code Rank} enum represents the possible ranks (values) of playing cards.
 * It defines the standard ranks used in a deck of cards, from 2 to Ace.
 * Each rank is associated with a numeric value used in card comparisons.
 * <p>
 * The rank values follow the standard card rankings, where Ace is the highest and 2 is the lowest.
 */
@Getter
public enum Rank {

    /** The rank for 2. */
    TWO(2),

    /** The rank for 3. */
    THREE(3),

    /** The rank for 4. */
    FOUR(4),

    /** The rank for 5. */
    FIVE(5),

    /** The rank for 6. */
    SIX(6),

    /** The rank for 7. */
    SEVEN(7),

    /** The rank for 8. */
    EIGHT(8),

    /** The rank for 9. */
    NINE(9),

    /** The rank for 10. */
    TEN(10),

    /** The rank for Jack. */
    JACK(11),

    /** The rank for Queen. */
    QUEEN(12),

    /** The rank for King. */
    KING(13),

    /** The rank for Ace. */
    ACE(14);

    /** The numeric value associated with each card rank. */
    private final int cardRank;

    /**
     * Constructor that associates a numeric value with each rank.
     *
     * @param cardRank The numeric value of the rank.
     */
    Rank(int cardRank) {
        this.cardRank = cardRank;
    }
}
