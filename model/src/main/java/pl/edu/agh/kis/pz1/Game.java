package pl.edu.agh.kis.pz1;

import lombok.Getter;
import lombok.Setter;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Represents a generic game class. This is a base class for various types of games.
 * It includes methods to manage the game state, add players, handle connections, and process client requests.
 */
@Getter
@Setter
public abstract class Game {

    /**
     * Flag indicating whether the game server is running.
     * If true, the game server is operational; otherwise, it's not.
     */
    private boolean isGameServerRunning;

    /**
     * Flag indicating whether the game has started.
     * If true, the game has started; otherwise, it hasn't.
     */
    private boolean gameStarted;

    /**
     * Starts the game.
     * This method is intended to be overridden in subclasses to implement specific game start logic.
     *
     * @throws IOException if there is an issue with input/output during game startup.
     * @throws InterruptedException if the game startup is interrupted.
     */
    void startGame() throws IOException, InterruptedException {}

    /**
     * Adds a player to the game using the given selection key.
     * This method should be implemented in subclasses to define how a player is added.
     *
     * @param key The selection key associated with the player.
     */
    public void addPlayer(SelectionKey key) {}

    /**
     * Sets up the game by obtaining necessary information (e.g., maximum players, ante).
     * This method should be implemented in subclasses to handle game-specific setup.
     *
     * @param sc The scanner to read user input.
     */
    public void getNewGameInfo(Scanner sc) {}

    /**
     * Resets the game to its initial state.
     * This method should be implemented in subclasses to clear all game-related variables and prepare for a new game.
     */
    public void resetGame() {}

    /**
     * Handles a client connection, allowing the server to manage multiple client connections.
     * This method processes the connection and prepares the game to interact with the client.
     *
     * @param sc       The socket channel associated with the client.
     * @param selector The selector used for multiplexing client channels.
     * @throws IOException            if there is an I/O error during connection handling.
     * @throws InterruptedException   if the process is interrupted during client connection handling.
     */
    public void handleClientConnection(SocketChannel sc, Selector selector) throws IOException, InterruptedException {}

    /**
     * Handles a client request by processing the provided buffer and performing appropriate actions.
     * This method should be implemented in subclasses to handle game-specific requests from clients.
     *
     * @param clientChannel The channel associated with the client sending the request.
     * @param buffer        The buffer containing the data sent by the client.
     * @param key           The selection key associated with the client.
     * @throws PlayerDisconnectedException if the player disconnects unexpectedly.
     */
    public void handleClientRequest(SocketChannel clientChannel, ByteBuffer buffer, SelectionKey key) throws PlayerDisconnectedException {}
}
