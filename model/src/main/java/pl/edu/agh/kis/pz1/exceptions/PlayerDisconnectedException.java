package pl.edu.agh.kis.pz1.exceptions;

/**
 * The {@code PlayerDisconnectedException} class represents an exception that is thrown
 * when a player disconnects unexpectedly from the game.
 *
 * <p>This exception is typically used to indicate that a player has lost their connection
 * to the game server, which may affect the ongoing game. It could be triggered by network
 * issues, the player quitting the game, or any other circumstances that result in the player
 * no longer being able to participate in the game.</p>
 *
 * <p>It extends the {@code Exception} class, meaning it is a checked exception that must be
 * either caught or declared in the method signature where it might occur.</p>
 */
public class PlayerDisconnectedException extends Exception {

    /**
     * Constructs a new {@code PlayerDisconnectedException} with the specified detail message.
     *
     * @param message the detail message, which provides additional information about
     *                the player's disconnection (for example, why the player disconnected).
     */
    public PlayerDisconnectedException(String message) {
        super(message);
    }
}
