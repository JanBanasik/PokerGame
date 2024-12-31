package pl.edu.agh.kis.pz1.util;

import pl.edu.agh.kis.pz1.Player;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * The {@code PokerMessenger} class provides utility methods for sending messages
 * to players in a poker game. It allows for sending messages to all players
 * or to a specific player through a {@code SocketChannel}.
 * <p>
 * This class handles communication between the server and the players via sockets.
 * </p>
 */
public class PokerMessenger {

    /**
     * Sends a message to all players in the game.
     * The message is sent to each player's {@code SocketChannel}.
     *
     * @param players the list of players to send the message to
     * @param message the message to be sent
     * @throws IOException if an I/O error occurs while sending the message
     */
    public void sendMessageToAll(List<Player> players, String message) throws IOException {
        for(Player player : players){
            SocketChannel clientChannel = (SocketChannel) player.getKey().channel();
            sendResponse(clientChannel, message + '\n');
        }
    }

    /**
     * Sends a message to a specific player through their {@code SocketChannel}.
     * The message is wrapped in a {@code ByteBuffer} and written to the channel.
     *
     * @param clientChannel the {@code SocketChannel} of the player
     * @param message the message to be sent
     * @throws IOException if an I/O error occurs while sending the message
     */
    public void sendResponse(SocketChannel clientChannel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        clientChannel.write(buffer);
    }
}
