package pl.edu.agh.kis.pz1.exceptions;

/**
 * The {@code CardsParsingException} class represents an exception that is thrown
 * when there is an error parsing cards in the application.
 *
 * <p>This exception is typically used to signal that there was an issue while
 * attempting to parse or process a set of cards, such as when cards are being read from a file,
 * parsed from a string, or generated incorrectly.</p>
 *
 * <p>It extends the {@code Exception} class, so it is a checked exception that must be
 * either caught or declared in the method signature where it may occur.</p>
 */
public class CardsParsingException extends Exception {

    /**
     * Constructs a new {@code CardsParsingException} with the specified detail message.
     *
     * @param message the detail message, which provides additional information about
     *                the error (for example, the cause of the card parsing failure).
     */
    public CardsParsingException(String message) {
        super(message);
    }
}
