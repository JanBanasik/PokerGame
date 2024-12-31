package pl.edu.agh.kis.pz1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The {@code Suit} enum represents the four possible suits of a standard deck of cards.
 * Each suit is associated with a corresponding emoji representation for visual display.
 * <p>
 * The four suits in a deck of cards are:
 * - Spades
 * - Hearts
 * - Diamonds
 * - Clubs
 * <p>
 * The {@code Suit} enum also links each suit to an emoji from the {@code SuitEmojis} class.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Suit {

    /** The suit for Spades (♠). */
    SPADES(SuitEmojis.SPADES),

    /** The suit for Hearts (♥). */
    HEARTS(SuitEmojis.HEARTS),

    /** The suit for Diamonds (♦). */
    DIAMONDS(SuitEmojis.DIAMONDS),

    /** The suit for Clubs (♣). */
    CLUBS(SuitEmojis.CLUBS);

    /** The emoji representation of the suit. */
    private SuitEmojis emoji;

}
