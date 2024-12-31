package pl.edu.agh.kis.pz1;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;


/**
 * The NonBlockingServer class represents a non-blocking server that uses Java NIO
 * to manage multiple client connections concurrently.
 * The server allows clients to play various games defined in the {@link GameDictionary}.
 *
 * <p>The server lifecycle consists of:</p>
 * <ul>
 *     <li>Starting the server</li>
 *     <li>Accepting client connections</li>
 *     <li>Processing client requests</li>
 *     <li>Shutting down the server</li>
 * </ul>
 *
 * <p>Server state is managed using the {@link Selector} and {@link ServerSocketChannel} APIs.</p>
 */
@Getter
@Setter
public class NonBlockingServer {

    /**
     * The port on which the server listens for client connections.
     */
    private static final int PORT = 8080;

    /**
     * The {@link Selector} instance used for managing channels.
     */
    private final Selector selector;

    /**
     * The {@link ServerSocketChannel} instance for accepting client connections.
     */
    private final ServerSocketChannel serverSocketChannel;

    /**
     * The current {@link Game} being played on the server.
     */
    private Game game;

    /**
     * Flag indicating whether the server is running.
     */
    private boolean running = true;

    /**
     * Constructs a new NonBlockingServer instance, initializing the selector
     * and server socket channel.
     *
     * @throws IOException if an I/O error occurs during initialization.
     */
    public NonBlockingServer() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Resets the current game state to allow for a new game to start.
     */
    private void resetGameState() {
        game.resetGame();
        System.out.println("Game state has been reset. Ready for a new game.");
    }

    /**
     * Starts the server, allowing clients to connect and play the selected game.
     * The server prompts the administrator to choose a game and handles game state transitions.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if interrupted while waiting.
     * @throws InvocationTargetException if an exception occurs while invoking game methods.
     * @throws NoSuchMethodException if a method required by the game is not found.
     * @throws InstantiationException if a game instance cannot be created.
     * @throws IllegalAccessException if access to a game method is not allowed.
     * @throws PlayerDisconnectedException if a client disconnects unexpectedly.
     */
    public void start() throws IOException, InterruptedException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException,
            PlayerDisconnectedException {
        Logger logger = (Logger) LoggerFactory.getLogger("org.reflections.Reflections");
        logger.setLevel(Level.WARN);
        System.out.println("Server started on port " + PORT);
        Scanner sc = new Scanner(System.in);
        String response;
        System.out.println(
                "What game do you want to play?\n" +
                        "Here is a list of available ones:");
        Map<String, Game> games = GameDictionary.getGameDictionary();
        for (String key : games.keySet()) {
            System.out.println('-' + key);
        }
        System.out.println("Enter game name: ");
        String gameName = sc.nextLine();
        game = games.get(gameName);
        while (running) {
            if (!game.isGameServerRunning()) {
                resetGameState();
                System.out.println("Do you want to start a game? (y/n)");
                response = sc.nextLine();
                if (response.equals("n")) {
                    break;
                }
                game.getNewGameInfo(sc);
            }
            listenToClients();
        }
    }

    /**
     * Listens for incoming client connections and requests, dispatching them
     * to the appropriate handlers.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if interrupted while listening.
     * @throws PlayerDisconnectedException if a client disconnects unexpectedly.
     */
    private void listenToClients() throws IOException, InterruptedException, PlayerDisconnectedException {
        try {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isValid() && key.isAcceptable()) {
                    acceptConnection();
                } else if (key.isValid() && key.isReadable()) {
                    handleClientRequest(key);
                }
            }
        } catch (ClosedSelectorException e) {
            System.out.println("Selector has been closed. Server shutting down gracefully.");
        }
    }

    /**
     * Accepts a new client connection, registering the client channel with the selector.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if interrupted while accepting connections.
     */
    private void acceptConnection() throws IOException, InterruptedException {
        System.out.println("accepting connection");
        SocketChannel sc = serverSocketChannel.accept();
        game.handleClientConnection(sc, selector);
    }

    /**
     * Handles a client request by reading data from the client channel and
     * delegating the request to the game logic.
     *
     * @param key the {@link SelectionKey} representing the client channel.
     * @throws PlayerDisconnectedException if the client disconnects unexpectedly.
     */
    private void handleClientRequest(SelectionKey key) throws PlayerDisconnectedException {
        System.out.println("handling request");
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        game.handleClientRequest(clientChannel, buffer, key);
    }

    /**
     * Stops the server by closing the selector and server socket channel.
     */
    public void stop() {
        setRunning(false);
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            System.err.println("Error during server shutdown: " + e.getMessage());
        }
    }

    /**
     * The main method to start the server.
     *
     * @param args command-line arguments.
     * @throws Exception if any exception occurs during server execution.
     */
    public static void main(String[] args) throws Exception {
        NonBlockingServer server = new NonBlockingServer();
        server.start();
    }
}
