package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.exceptions.CardsParsingException;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

class PokerTest {

    private Poker game;
    private SelectionKey mockSelectionKey1;
    private SelectionKey mockSelectionKey2;
    private SocketChannel mockSocketChannel1;
    private SocketChannel mockSocketChannel2;
    private Selector mockSelector;
    @BeforeEach
    void setUp() throws IOException, InterruptedException {
        game = new Poker();

        // Mocking SelectionKey and SocketChannel
        mockSelectionKey1 = mock(SelectionKey.class);
        mockSelectionKey2 = mock(SelectionKey.class);
        mockSocketChannel1 = mock(SocketChannel.class);
        mockSocketChannel2 = mock(SocketChannel.class);

        // Mocking Players
        new Player(mockSelectionKey1, 0);
        new Player(mockSelectionKey2, 1);


        // Assigning SocketChannels to SelectionKeys
        when(mockSelectionKey1.channel()).thenReturn(mockSocketChannel1);
        when(mockSelectionKey2.channel()).thenReturn(mockSocketChannel2);
        game.addPlayer(mockSelectionKey1);
        game.addPlayer(mockSelectionKey2);
        game.startGame();
    }

    @Test
    void testDealCards() {
        assertEquals(5, game.getPlayers().get(0).getCards().size());
        assertEquals(5, game.getPlayers().get(1).getCards().size());
    }

    @Test
    void testExchangeCards() throws CardsParsingException {
        List<Card> copiedCards = new ArrayList<>();
        for (Card c : game.getPlayers().get(0).getCards()) {
            copiedCards.add(new Card(c));
        }
        List<Integer> cardExchange = new ArrayList<>();
        cardExchange.add(1);
        cardExchange.add(2);
        game.exchangeCards(cardExchange, 0);
        assert copiedCards != game.getPlayers().get(0).getCards();
        game.setCurrentPlayers(2);
        assertEquals(2, game.getCurrentPlayers());
    }


    @Test
    void testDecideWinnerWithMultiplePlayers() {
        game.dealCards();
        String result = game.decideWinner();
        assertNotNull(result);
    }

    @Test
    void testDecideWinnerEqualHand(){
        game.dealCards();
        List<Card> cards1 = new ArrayList<>();
        cards1.add(new Card(Rank.TWO, Suit.CLUBS));
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.NINE, Suit.HEARTS));
        cards1.add(new Card(Rank.TEN, Suit.SPADES));
        cards1.add(new Card(Rank.KING, Suit.CLUBS));
        game.getPlayers().get(0).setCards(cards1);
        List<Card> cards2 = new ArrayList<>();
        cards2.add(new Card(Rank.THREE, Suit.CLUBS));
        cards2.add(new Card(Rank.THREE, Suit.DIAMONDS));
        cards2.add(new Card(Rank.ACE, Suit.HEARTS));
        cards2.add(new Card(Rank.QUEEN, Suit.SPADES));
        cards2.add(new Card(Rank.EIGHT, Suit.CLUBS));
        game.getPlayers().get(1).setCards(cards2);
        assertEquals("The winner is:\n-Player 1\nHe won: 0", game.decideWinner());
    }

    @Test
    void testNotifyPlayersAboutFirstBetting() throws IOException {
        game.dealCards();
        verify(mockSocketChannel1, times(3)).write((ByteBuffer) any());
        verify(mockSocketChannel2, times(3)).write((ByteBuffer) any());
        assertEquals(0, game.getCurrentPlayers());
    }

    @Test
    void testPotAfterBettingRound() throws IOException, PlayerDisconnectedException {
        game.dealCards();
        game.handleBetting("bet 50", mockSocketChannel1, 0);
        game.handleBetting("bet 50", mockSocketChannel2, 0);
        assertEquals(50, game.getCurrentBet());
    }

    @Test
    void testHandleInvalidRequestDuringTurn() throws IOException, InterruptedException, PlayerDisconnectedException {
        String invalidRequest = "raise 200";
        game.handleClientTurn(mockSelectionKey1, invalidRequest, mockSocketChannel1);
        assertEquals(0, game.getCurrentPlayers());
    }

    @Test
    void testHandleInvalidBetAmount() throws IOException, PlayerDisconnectedException {
        String invalidBet = "bet -100";
        int result = game.handleBetting(invalidBet, mockSocketChannel1, 0);
        assertEquals(-1, result);
    }

    @Test
    void testPlayerCannotRaiseWithoutPreviousBet() throws IOException, PlayerDisconnectedException {
        String raiseAttempt = "raise 100";
        int result = game.handleBetting(raiseAttempt, mockSocketChannel1, 0);
        assertEquals(-2, result);
    }

    @Test
    void testHandleRaiseWhenCurrentBetIsZero() throws IOException, PlayerDisconnectedException {
        game.handleBetting("bet 50", mockSocketChannel1, 0);
        String raiseAttempt = "raise 100";
        int result = game.handleBetting(raiseAttempt, mockSocketChannel2, 0);
        assertEquals(-1, result);
    }

    @Test
    void testInvalidBettingAction() throws IOException, PlayerDisconnectedException {
        String invalidAction = "bet abc";
        int result = game.handleBetting(invalidAction, mockSocketChannel1, 0);
        assertEquals(-2, result);
    }

    @Test
    void testSetPhase() {
        assertEquals(2, game.getPhase());
    }

    @Test
    void testSetPlayerCards() {
        Player player = game.getPlayers().get(0);
        List<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.NINE, Suit.HEARTS));
        myCards.add(new Card(Rank.TEN, Suit.SPADES));
        myCards.add(new Card(Rank.KING, Suit.CLUBS));
        player.setCards(myCards);
        assertEquals(myCards, player.getCards());
    }

    @Test
    void testCardExchangeDoesNotChangeCardCount() throws CardsParsingException {
        Player player = game.getPlayers().get(0);
        int initialCardCount = player.getCards().size();
        List<Integer> cardsToExchange = new ArrayList<>();
        cardsToExchange.add(1);
        cardsToExchange.add(2);
        game.exchangeCards(cardsToExchange, 0);
        assertEquals(initialCardCount, player.getCards().size());
    }

    @Test
    void testEndGame() {
        game.resetGame();
        assertEquals(0, game.getPlayers().size());
        assertEquals(0, game.getPot());
        assertEquals(0, game.getCurrentBet());
    }


    @Test
    void testHandleBettingInBettingPhase() throws IOException, PlayerDisconnectedException {
        game.dealCards();
        game.setPhase(2);
        String betRequest = "bet 100";
        game.handleBetting(betRequest, mockSocketChannel1, 0);
        assertEquals(100, game.getCurrentBet());
    }



    @Test
    void manageFoldTest() throws IOException {
        int res = game.manageFold(0);
        assertEquals(1, res);
    }

    @Test
    void manageCardExchangeTest() throws IOException {
        List<Card> originalCards = new ArrayList<>();
        for(Card c : game.getPlayers().get(0).getCards()) {
            originalCards.add(new Card(c));
        }
        game.manageCardExchange(mockSocketChannel1, "1 2", 0);
        assertEquals(originalCards.get(2), game.getPlayers().get(0).getCards().get(0));
    }

    @Test
    void disconnectAllPlayersTest() throws PlayerDisconnectedException {
        game.disconnectAllPlayersAndResetGame();
        assertEquals(0, game.getPlayers().size());
        assertEquals(0, game.getCurrentPlayers());
    }

    @Test
    void handleBettingFold() throws IOException, PlayerDisconnectedException {
        assertEquals(1, game.handleBetting("fold", mockSocketChannel1, 0));
    }

    @Test
    void removeTest() {
        game.remove(mockSelectionKey1);
        assertEquals(1, game.getPlayers().size());
        game.remove(mockSelectionKey2);
        assertEquals(0, game.getPlayers().size());
    }

    @Test
    void performShowdownTest() throws IOException, PlayerDisconnectedException {
        game.getFoldedPlayers().add(0);
        game.performShowDown();
        assertEquals(0, game.getPlayers().size());
        assertFalse(game.isGameStarted());
        assertEquals(0, game.getPhase());
    }

    @Test
    void manageBettingTurnCheckTest() throws IOException {
        String temp = "check";
        int res = game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        assertEquals(-1, res);
        assertEquals(0, game.getCurrentBet());
    }

    @Test
    void manageBettingTurnBetTest() throws IOException {
        String temp = "bet 10";
        int res = game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        assertEquals(-1, res);
        assertEquals(10, game.getPlayers().get(0).getBetInRound());
    }

    @Test
    void manageBettingTurnBetCallTest() throws IOException {
        String temp = "bet 10";
        game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        String temp2 = "call";
        int res2 = game.managePlayerBettingTurn(mockSocketChannel2, temp2.split(" "), 1);
        assertEquals(-1, res2);
        assertEquals(10, game.getPlayers().get(0).getBetInRound());
    }

    @Test
    void invalidInputForBettingTest() throws IOException {
        String temp = "idk";
        int res = game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        assertEquals(-2, res);
    }

    @Test
    void validActionTest() throws IOException, PlayerDisconnectedException {
        game.performValidAction();
        assertEquals(1, game.getCurrentPlayerIndex());
    }


    @Test
    void endBettingTest() throws IOException, PlayerDisconnectedException {
        String temp = "bet 10";
        game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        String temp2 = "call";
        int res2 = game.managePlayerBettingTurn(mockSocketChannel2, temp2.split(" "), 1);
        assertEquals(-1, res2);
        assertEquals(10, game.getPlayers().get(0).getBetInRound());
        game.endBetting();
        assertEquals(0, game.getCurrentBet());
        assertEquals(20, game.getPot());
    }

    @Test
    void testforceBetting() throws IOException {
        String temp = "bet 10";
        game.managePlayerBettingTurn(mockSocketChannel1, temp.split(" "), 0);
        String temp2 = "raise 20";
        game.managePlayerBettingTurn(mockSocketChannel2, temp2.split(" "), 1);
        game.forceBetting();
        assertEquals(0, game.getCurrentPlayerIndex());
    }

    @Test
    void testAcceptingConnection() throws IOException, InterruptedException {
        game.setMaxPlayers(2);
        game.handleClientConnection(mockSocketChannel1, mockSelector);
        game.handleClientConnection(mockSocketChannel2, mockSelector);
        assertEquals(4, game.getPlayers().size());
    }

    @Test
    void handleClientRequestWhenInvalidGamePhaseTest() throws PlayerDisconnectedException {
        game.handleClientRequest(mockSocketChannel1, ByteBuffer.wrap("bet 10".getBytes()), mockSelectionKey1);
        assertEquals(0, game.getPlayers().get(0).getBetInRound());
    }

    @Test
    void getGameInfoTest() {
        provideInput("2\n100\n");
        Scanner sc = new Scanner(System.in);
        game.getNewGameInfo(sc);
        assertEquals(100, game.getAnte());
        assertEquals(2, game.getMaxPlayers());
    }

    private void provideInput(String data) {
        System.setIn(new ByteArrayInputStream(data.getBytes()));
    }


    @Test
    void cardExchangeTest() throws IOException {
        List<Card> cards1 = game.getPlayers().get(0).getCards();
        game.performCardExchange();
        assertEquals(cards1, game.getPlayers().get(0).getCards());
    }

    @Test
    void testValidAction() throws IOException, PlayerDisconnectedException {
        game.setCurrentPlayerIndex(1);
        game.setPhase(2);
        game.performValidAction();
        assertEquals(3, game.getPhase());
    }

    @Test
    void manageInvalidCheck() throws IOException {
        game.setCurrentBet(20);
        int res = game.manageCheck(mockSocketChannel1, 0);
        assertEquals(-2, res);
    }

    @Test
    void handleClientConnectionWithTooManyPlayers() throws IOException, InterruptedException {
        game.setCurrentPlayers(2);
        game.setMaxPlayers(2);
        game.handleClientConnection(mockSocketChannel1, mockSelector);
        assertEquals(2, game.getCurrentPlayers());
    }

}
