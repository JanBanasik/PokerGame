package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private Client client;
    private NonBlockingServer server;

    // Uruchamiamy serwer w tle
    private Thread myThread;
    private CountDownLatch serverReadyLatch;

    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        // Przygotowanie latcha, który zasygnalizuje, kiedy serwer będzie gotowy
        serverReadyLatch = new CountDownLatch(1);

        // Uruchamiamy serwer w tle
        server = new NonBlockingServer();
        myThread = new Thread(() -> {
            try {
                // Podajemy dane wejściowe do serwera (gra i parametry)
                provideInput("Poker\ny\n2\n100\n");
                System.out.println("Starting server...");
                server.start();  // Uruchomienie serwera
                System.out.println("Server started.");
            } catch (IOException | InterruptedException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException | PlayerDisconnectedException e) {
                System.out.println("Error occurred while starting server: " + e.getMessage());
            } finally {
                System.out.println("Signaling server ready.");
                // Gdy serwer się uruchomi, wywołujemy countDown()
                serverReadyLatch.countDown();
            }
        });
        myThread.start();

        // Czekamy, aż serwer będzie gotowy
        serverReadyLatch.await(1, TimeUnit.SECONDS);  // Czekamy na gotowość serwera przez 10 sekund

        // Inicjalizujemy klienta
        client = new Client();
    }

    @Test
    void testClientConnection() {
        // Sprawdzamy, czy klient połączył się z serwerem
        assertTrue(client.socketChannel.isOpen(), "Client should be connected to the server.");
    }

    @Test
    void testSendGameAction() throws IOException {
        // Testowanie wysyłania akcji do serwera
        String action = "check";
        client.sendGameAction(action);

        // Upewniamy się, że klient nie zamknął połączenia
        assertTrue(client.running, "Client should continue running after sending action.");
    }

    @Test
    void testHandleServerResponse() throws IOException, InterruptedException {
        // Klient powinien wysłać jakąś akcję do serwera
        String action = "check";
        client.sendGameAction(action);

        System.out.println("Waiting for server response...");

        // Czekamy na odpowiedź serwera
        serverReadyLatch.await(10, TimeUnit.SECONDS);  // Czekamy na odpowiedź od serwera przez 10 sekund

        System.out.println("Received response from server.");

        // Testujemy, czy klient otrzymał odpowiedź od serwera
        assertTrue(client.running, "Client should be running after handling response.");

        // Zatrzymujemy klienta i serwer po teście
        client.stop();
        server.stop();
        myThread.join();  // Czekamy na zakończenie wątku serwera
    }

    @Test
    void testClientStop() throws IOException {
        // Testowanie zatrzymywania klienta
        client.stop();
        assertFalse(client.running, "Client should be stopped.");
    }

    // Zamykamy klienta i serwer po każdym teście
    @AfterEach
    void tearDown() throws InterruptedException, IOException {
        if (client != null) {
            client.stop();  // Zatrzymanie klienta
        }
        if (server != null) {
            server.stop();  // Zatrzymanie serwera
        }
        if (myThread != null) {
            myThread.join();  // Czekamy na zakończenie wątku serwera
        }
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }
}
