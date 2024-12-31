package pl.edu.agh.kis.pz1.exceptions;

/**
 * The {@code InvalidMoveException} class represents an exception that is thrown
 * when a player attempts to make an invalid move in the game.
 *
 * <p>This exception is typically used to indicate that a player has performed
 * an action that is not allowed according to the rules of the game. For example,
 * this exception may be thrown if a player tries to make a move that is not permitted
 * during the current phase of the game or makes an illegal move based on the game rules.</p>
 *
 * <p>It extends the {@code Exception} class, so it is a checked exception that must be
 * either caught or declared in the method signature where it may occur.</p>
 */
public class InvalidMoveException extends Exception {

    /**
     * Constructs a new {@code InvalidMoveException} with the specified detail message.
     *
     * @param message the detail message, which provides additional information about
     *                the invalid move (for example, why the move is considered invalid).
     */
    public InvalidMoveException(String message) {
        super(message);
    }
}
