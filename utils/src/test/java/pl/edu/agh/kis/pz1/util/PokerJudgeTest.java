package pl.edu.agh.kis.pz1.util;


import org.junit.jupiter.api.BeforeEach;
import pl.edu.agh.kis.pz1.*;

import org.junit.jupiter.api.Test;


import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PokerJudgeTest {

    private SelectionKey mockSelectionKey1;

    @BeforeEach
    void setUp() {

        // Mocking SelectionKey and SocketChannel
        mockSelectionKey1 = mock(SelectionKey.class);
        SelectionKey mockSelectionKey2 = mock(SelectionKey.class);
        SocketChannel mockSocketChannel1 = mock(SocketChannel.class);
        SocketChannel mockSocketChannel2 = mock(SocketChannel.class);

        // Mocking Players
        new Player(mockSelectionKey1, 0);
        new Player(mockSelectionKey2, 1);


        // Assigning SocketChannels to SelectionKeys
        when(mockSelectionKey1.channel()).thenReturn(mockSocketChannel1);
        when(mockSelectionKey2.channel()).thenReturn(mockSocketChannel2);
    }

    @Test
    void decideWinnerBetweenTwoPlayersTest(){
        List<Card> cards1 = new ArrayList<>();
        cards1.add(new Card(Rank.TWO, Suit.CLUBS));
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.NINE, Suit.HEARTS));
        cards1.add(new Card(Rank.TEN, Suit.SPADES));
        cards1.add(new Card(Rank.KING, Suit.CLUBS));
        List<Card> cards2 = new ArrayList<>();
        cards2.add(new Card(Rank.THREE, Suit.CLUBS));
        cards2.add(new Card(Rank.THREE, Suit.DIAMONDS));
        cards2.add(new Card(Rank.ACE, Suit.HEARTS));
        cards2.add(new Card(Rank.QUEEN, Suit.SPADES));
        cards2.add(new Card(Rank.EIGHT, Suit.CLUBS));
        HandRanker r = new HandRanker();
        assertEquals(-1, PokerJudge.decideWinnerBetweenTwoPlayers(r.distinguishmentCards(cards1),
                r.distinguishmentCards(cards2)));
    }


    @Test
    void decideWinnerBetweenTwoPlayersTest2(){
        List<Card> cards1 = new ArrayList<>();
        cards1.add(new Card(Rank.TWO, Suit.CLUBS));
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.NINE, Suit.HEARTS));
        cards1.add(new Card(Rank.TEN, Suit.SPADES));
        cards1.add(new Card(Rank.KING, Suit.CLUBS));
        List<Card> cards2 = new ArrayList<>();
        cards2.add(new Card(Rank.THREE, Suit.CLUBS));
        cards2.add(new Card(Rank.THREE, Suit.DIAMONDS));
        cards2.add(new Card(Rank.ACE, Suit.HEARTS));
        cards2.add(new Card(Rank.QUEEN, Suit.SPADES));
        cards2.add(new Card(Rank.EIGHT, Suit.CLUBS));
        HandRanker r = new HandRanker();
        assertEquals(1, PokerJudge.decideWinnerBetweenTwoPlayers(r.distinguishmentCards(cards2),
                r.distinguishmentCards(cards1)));
    }

    @Test
    void drawTest() {
        List<Card> cards1 = new ArrayList<>();
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.NINE, Suit.DIAMONDS));
        cards1.add(new Card(Rank.TEN, Suit.DIAMONDS));
        cards1.add(new Card(Rank.KING, Suit.DIAMONDS));
        List<Card> cards2 = new ArrayList<>();
        cards2.add(new Card(Rank.TWO, Suit.HEARTS));
        cards2.add(new Card(Rank.TWO, Suit.HEARTS));
        cards2.add(new Card(Rank.NINE, Suit.HEARTS));
        cards2.add(new Card(Rank.TEN, Suit.HEARTS));
        cards2.add(new Card(Rank.KING, Suit.HEARTS));
        HandRanker r = new HandRanker();
        assertEquals(0,  PokerJudge.decideWinnerBetweenTwoPlayers(r.distinguishmentCards(cards1),
                r.distinguishmentCards(cards2)));
    }


    @Test
    void testDecideWinner(){
        List<Card> cards1 = new ArrayList<>();
        List<Card> cards2 = new ArrayList<>();
        cards1.add(new Card(Rank.TWO, Suit.DIAMONDS));
        cards1.add(new Card(Rank.TWO, Suit.CLUBS));
        cards1.add(new Card(Rank.FOUR, Suit.DIAMONDS));
        cards1.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        cards1.add(new Card(Rank.SIX, Suit.DIAMONDS));

        cards2.add(new Card(Rank.ACE, Suit.CLUBS));
        cards2.add(new Card(Rank.THREE, Suit.CLUBS));
        cards2.add(new Card(Rank.FOUR, Suit.CLUBS));
        cards2.add(new Card(Rank.FIVE, Suit.CLUBS));
        cards2.add(new Card(Rank.ACE, Suit.SPADES));

        HandRanker r = new HandRanker();
        assertEquals(-1, PokerJudge.decideWinnerBetweenTwoPlayers(r.distinguishmentCards(cards1),
                r.distinguishmentCards(cards2)));
    }

    @Test
    void bettingEndedTest1() {
        Player p1 = new Player(mockSelectionKey1, 0);
        Player p2 = new Player(mockSelectionKey1, 0);
        p1.setBetInRound(20);
        p2.setBetInRound(10);
        assertFalse(PokerJudge.verifyWhetherTheBettingEnded(List.of(p1, p2), List.of()));
    }

    @Test
    void bettingEndedTest2() {
        Player p1 = new Player(mockSelectionKey1, 0);
        Player p2 = new Player(mockSelectionKey1, 0);
        p1.setBetInRound(20);
        p2.setBetInRound(20);
        assertTrue(PokerJudge.verifyWhetherTheBettingEnded(List.of(p1, p2), List.of()));
    }

}