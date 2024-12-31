package pl.edu.agh.kis.pz1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The Client class represents a client that connects to the NonBlockingServer to participate in games.
 * It communicates with the server using Java NIO and handles user input and server responses.
 *
 * <p>The client lifecycle consists of:</p>
 * <ul>
 *     <li>Connecting to the server</li>
 *     <li>Sending game actions based on user input</li>
 *     <li>Receiving and processing server responses</li>
 *     <li>Gracefully shutting down</li>
 * </ul>
 */
public class Client {

    /**
     * The host address of the server to connect to.
     */
    private static final String HOST = "localhost";

    /**
     * The port number of the server to connect to.
     */
    private static final int PORT = 8080;

    /**
     * The {@link Selector} instance used for managing channels.
     */
    private final Selector selector;

    /**
     * The {@link SocketChannel} instance used for communication with the server.
     */
    final SocketChannel socketChannel;

    /**
     * A flag indicating whether the client is running.
     */
    volatile boolean running = true;

    /**
     * The thread handling user input.
     */
    private Thread inputthread;

    /**
     * Constructs a new Client instance and establishes a connection to the server.
     *
     * @throws IOException if an I/O error occurs while connecting to the server.
     */
    public Client() throws IOException {
        selector = Selector.open();
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    /**
     * Starts the client, handling user input and server responses.
     *
     * @throws IOException if an I/O error occurs while communicating with the server.
     */
    public void start() throws IOException {
        System.out.println("Connecting to poker table...");

        // Thread to handle user input
        inputthread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (running) {
                int res = handleInput(scanner);
                if (res == -1) break;
            }
            scanner.close();
        });
        inputthread.start();

        while (running && !Thread.currentThread().isInterrupted()) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isReadable()) {
                    handleServerResponse(key);
                }
            }
        }
        System.out.println("Client exiting...");
        socketChannel.close();
        selector.close();
    }

    /**
     * Handles user input by reading a line from the console and sending it to the server.
     *
     * @param scanner the {@link Scanner} used to read user input.
     * @return 1 if input was handled successfully, or -1 if an error occurred.
     */
    private int handleInput(Scanner scanner) {
        try {
            String action = scanner.nextLine();
            Thread.sleep(500);
            sendGameAction(action);
            return 1;
        } catch (IOException | NoSuchElementException e) {
            System.out.println("Exiting");
        } catch (InterruptedException e) {
            System.out.println("Interrupted!");
            Thread.currentThread().interrupt();
        }
        return -1;
    }

    /**
     * Sends a game action to the server.
     *
     * @param action the action to send to the server.
     * @throws IOException if an I/O error occurs while sending the action.
     */
    void sendGameAction(String action) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(action.getBytes());
        socketChannel.write(buffer);
    }

    /**
     * Handles a response from the server by reading and processing data from the server channel.
     *
     * @param key the {@link SelectionKey} representing the server channel.
     * @throws IOException if an I/O error occurs while reading the response.
     */
    void handleServerResponse(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            System.out.println("Server closed the connection.");
            running = false;
            channel.close();
        } else if (bytesRead > 0) {
            buffer.flip();
            String response = new String(buffer.array(), 0, buffer.limit());
            System.out.println(response);

            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (response.contains("Maksymalna liczba graczy została osiągnięta") ||
                    response.contains("Session terminated")) {
                System.out.println("Exiting client as requested by server.");
                running = false;
            }
        }
    }

    /**
     * Stops the client by shutting down its thread and closing connections.
     *
     * @throws IOException if an I/O error occurs during shutdown.
     */
    public void stop() throws IOException {
        running = false;
        if (inputthread != null) {
            inputthread.interrupt(); // Interrupt the input thread
        }
        selector.wakeup(); // Wake up the selector to stop its loop
        socketChannel.close();
    }

    /**
     * Provides input to the client programmatically for testing or automation purposes.
     *
     * @param input the input string to send to the server.
     */
    public void provideInput(String input) {
        Thread inputThread = new Thread(() -> {
            try {
                sendGameAction(input);
                running = false; // Stop the client after sending input
            } catch (IOException e) {
                System.err.println("Error sending input: " + e.getMessage());
            }
        });
        inputThread.start();
    }

    /**
     * The main method to start the client.
     *
     * @param args command-line arguments.
     * @throws IOException if an I/O error occurs while starting the client.
     */
    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.start();
    }
}
