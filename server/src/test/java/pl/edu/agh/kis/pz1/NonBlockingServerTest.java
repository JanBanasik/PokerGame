package pl.edu.agh.kis.pz1;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class NonBlockingServerTest {
    private NonBlockingServer server;
    private Client c1;
    @BeforeEach
    void setUp() throws Exception {
        server = new NonBlockingServer();

        c1 = new Client();

    }


    @AfterEach
    void tearDown() throws IOException {
        if (server != null) {
            server.stop();
            server = null;
        }
        if (c1 != null) {
            c1.stop();
            c1 = null;
        }

        System.gc();
    }

    @Test
    void testServerWithTimeout() throws Exception {
        Thread serverThread = new Thread(() -> {
            try {
                provideInput("Poker\ny\n2\n100\n");
                server.start();
            } catch (IOException | InterruptedException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException | PlayerDisconnectedException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        server.stop();
        serverThread.join();
        server.getGame().startGame();
        assertTrue(server.getGame().isGameStarted());
    }
    @Test
    void testServerConnect1PlayerWithTimeout() throws Exception {

        Thread serverThread = new Thread(() -> {
            try {
                provideInput("Poker\ny\n2\n100\n");
                server.start();
            } catch (IOException | InterruptedException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException | PlayerDisconnectedException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
        Thread client1Thread = new Thread(() -> {
            try {
                c1.start();
                c1.provideInput("bet 10");
            } catch (IOException e) {
                System.err.println("Client 1 error: " + e.getMessage());
            }
        });
        client1Thread.start();

        c1.stop();
        client1Thread.join(1000);
        serverThread.join(1000);
        server.getGame().resetGame();
        assertFalse(server.getGame().isGameServerRunning());

    }
    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}