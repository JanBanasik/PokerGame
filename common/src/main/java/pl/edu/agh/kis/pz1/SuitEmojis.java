package pl.edu.agh.kis.pz1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * The {@code SuitEmojis} enum represents the emoji symbols for the four suits in a deck of cards.
 * Each suit is associated with its corresponding emoji character used for visual representation.
 * <p>
 * The four suits in a deck of cards and their emoji representations are:
 * - Clubs (♣)
 * - Diamonds (♦)
 * - Spades (♠)
 * - Hearts (♥)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SuitEmojis {

    /** The emoji for Clubs (♣). */
    CLUBS("♣"),

    /** The emoji for Diamonds (♦). */
    DIAMONDS("♦"),

    /** The emoji for Spades (♠). */
    SPADES("♠"),

    /** The emoji for Hearts (♥). */
    HEARTS("♥");

    /** The emoji character associated with the suit. */
    private String emoji;
}
