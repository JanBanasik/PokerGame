package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class HandRankerTest extends TestCase {

    @Test
    public void testRoyalFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.TEN, Suit.SPADES));
        myCards.add(new Card(Rank.JACK, Suit.SPADES));
        myCards.add(new Card(Rank.QUEEN, Suit.SPADES));
        myCards.add(new Card(Rank.KING, Suit.SPADES));
        myCards.add(new Card(Rank.ACE, Suit.SPADES));
        assertEquals("Royal Flush", hr.rank(myCards));
    }


    @Test
    public void testStraightFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.NINE, Suit.HEARTS));
        myCards.add(new Card(Rank.TEN, Suit.HEARTS));
        myCards.add(new Card(Rank.JACK, Suit.HEARTS));
        myCards.add(new Card(Rank.QUEEN, Suit.HEARTS));
        myCards.add(new Card(Rank.KING, Suit.HEARTS));
        assertEquals("Straight Flush", hr.rank(myCards));
    }

    // Test dla układu "Four of a Kind"
    @Test
    public void testFourOfAKind() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards1 = new ArrayList<>();
        myCards1.add(new Card(Rank.ACE, Suit.CLUBS));
        myCards1.add(new Card(Rank.ACE, Suit.DIAMONDS));
        myCards1.add(new Card(Rank.ACE, Suit.SPADES));
        myCards1.add(new Card(Rank.ACE, Suit.HEARTS));
        myCards1.add(new Card(Rank.QUEEN, Suit.CLUBS));
        assertEquals("Four Of A Kind", hr.rank(myCards1));
    }

    // Test dla układu "Full House"
    @Test
    public void testFullHouse() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards2 = new ArrayList<>();
        myCards2.add(new Card(Rank.KING, Suit.CLUBS));
        myCards2.add(new Card(Rank.KING, Suit.DIAMONDS));
        myCards2.add(new Card(Rank.KING, Suit.SPADES));
        myCards2.add(new Card(Rank.QUEEN, Suit.HEARTS));
        myCards2.add(new Card(Rank.QUEEN, Suit.CLUBS));
        assertEquals("Full House", hr.rank(myCards2));
    }

    // Test dla układu "Flush"
    @Test
    public void testFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards3 = new ArrayList<>();
        myCards3.add(new Card(Rank.TWO, Suit.CLUBS));
        myCards3.add(new Card(Rank.JACK, Suit.CLUBS));
        myCards3.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards3.add(new Card(Rank.ACE, Suit.CLUBS));
        myCards3.add(new Card(Rank.QUEEN, Suit.CLUBS));
        assertEquals("Flush", hr.rank(myCards3));
    }

    // Test dla układu "Straight"
    @Test
    public void testStraight() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards4 = new ArrayList<>();
        myCards4.add(new Card(Rank.TEN, Suit.CLUBS));
        myCards4.add(new Card(Rank.JACK, Suit.HEARTS));
        myCards4.add(new Card(Rank.QUEEN, Suit.DIAMONDS));
        myCards4.add(new Card(Rank.KING, Suit.SPADES));
        myCards4.add(new Card(Rank.ACE, Suit.CLUBS));

        ArrayList<Card> myCards5 = new ArrayList<>();
        myCards5.add(new Card(Rank.ACE, Suit.CLUBS));
        myCards5.add(new Card(Rank.TWO, Suit.HEARTS));
        myCards5.add(new Card(Rank.THREE, Suit.DIAMONDS));
        myCards5.add(new Card(Rank.FOUR, Suit.SPADES));
        myCards5.add(new Card(Rank.FIVE, Suit.CLUBS));

        assertEquals("Straight", hr.rank(myCards4));
        assertEquals("Straight", hr.rank(myCards5));
    }

    // Test dla układu "Three of a Kind"
    @Test
    public void testThreeOfAKind() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards5 = new ArrayList<>();
        myCards5.add(new Card(Rank.TWO, Suit.CLUBS));
        myCards5.add(new Card(Rank.TWO, Suit.DIAMONDS));
        myCards5.add(new Card(Rank.TWO, Suit.SPADES));
        myCards5.add(new Card(Rank.FIVE, Suit.HEARTS));
        myCards5.add(new Card(Rank.SEVEN, Suit.CLUBS));
        assertEquals("Three Of A Kind", hr.rank(myCards5));
    }

    // Test dla układu "Two Pair"
    @Test
    public void testTwoPair() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards6 = new ArrayList<>();
        myCards6.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards6.add(new Card(Rank.THREE, Suit.DIAMONDS));
        myCards6.add(new Card(Rank.FIVE, Suit.SPADES));
        myCards6.add(new Card(Rank.FIVE, Suit.HEARTS));
        myCards6.add(new Card(Rank.SEVEN, Suit.CLUBS));
        assertEquals("Two Pair", hr.rank(myCards6));
    }

    // Test dla układu "Pair"
    @Test
    public void testPair() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards7 = new ArrayList<>();
        myCards7.add(new Card(Rank.SIX, Suit.CLUBS));
        myCards7.add(new Card(Rank.SIX, Suit.DIAMONDS));
        myCards7.add(new Card(Rank.KING, Suit.SPADES));
        myCards7.add(new Card(Rank.QUEEN, Suit.HEARTS));
        myCards7.add(new Card(Rank.TEN, Suit.CLUBS));
        assertEquals("Pair", hr.rank(myCards7));
    }

    // Test dla układu "High card" (brak par, prostych itp.)
    @Test
    public void testHighCard() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards8 = new ArrayList<>();
        myCards8.add(new Card(Rank.TWO, Suit.CLUBS));
        myCards8.add(new Card(Rank.FIVE, Suit.HEARTS));
        myCards8.add(new Card(Rank.NINE, Suit.DIAMONDS));
        myCards8.add(new Card(Rank.JACK, Suit.SPADES));
        myCards8.add(new Card(Rank.KING, Suit.CLUBS));
        assertEquals("High Card", hr.rank(myCards8));
    }

    @Test
    public void testCheckForRoyalFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.TEN, Suit.HEARTS));
        myCards.add(new Card(Rank.JACK, Suit.HEARTS));
        myCards.add(new Card(Rank.QUEEN, Suit.HEARTS));
        myCards.add(new Card(Rank.KING, Suit.HEARTS));
        myCards.add(new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(List.of(14), hr.checkForRoyalFlush(myCards));

        // Negative case
        myCards.set(4, new Card(Rank.NINE, Suit.HEARTS));
        assertEquals(new ArrayList<>(), hr.checkForRoyalFlush(myCards));
    }

    // Test dla metody checkForStraightFlush
    @Test
    public void testCheckForStraightFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.SIX, Suit.DIAMONDS));
        myCards.add(new Card(Rank.SEVEN, Suit.DIAMONDS));
        myCards.add(new Card(Rank.EIGHT, Suit.DIAMONDS));
        myCards.add(new Card(Rank.NINE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.TEN, Suit.DIAMONDS));
        assertEquals(List.of(10), hr.checkForStraightFlush(myCards));

        // Negative case
        myCards.set(4, new Card(Rank.TEN, Suit.HEARTS));
        assertEquals(new ArrayList<>(), hr.checkForStraightFlush(myCards));
    }

    // Test dla metody checkForFourOfAKind
    @Test
    public void testCheckForFourOfAKind() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards.add(new Card(Rank.THREE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.THREE, Suit.HEARTS));
        myCards.add(new Card(Rank.THREE, Suit.SPADES));
        myCards.add(new Card(Rank.FOUR, Suit.CLUBS));
        assertEquals(List.of(3, 4), hr.checkForFourOfAKind(myCards));

        // Negative case
        myCards.set(3, new Card(Rank.FIVE, Suit.SPADES));
        assertEquals(new ArrayList<>(), hr.checkForFourOfAKind(myCards));
    }

    // Test dla metody checkForFullHouse
    @Test
    public void testCheckForFullHouse() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.FIVE, Suit.CLUBS));
        myCards.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.FIVE, Suit.HEARTS));
        myCards.add(new Card(Rank.SEVEN, Suit.SPADES));
        myCards.add(new Card(Rank.SEVEN, Suit.CLUBS));
        assertEquals(List.of(5, 7), hr.checkForFullHouse(myCards));

        // Negative case
        myCards.set(4, new Card(Rank.EIGHT, Suit.CLUBS));
        assertEquals(new ArrayList<>(), hr.checkForFullHouse(myCards));
    }

    // Test dla metody checkForFlush
    @Test
    public void testCheckForFlush() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.TWO, Suit.HEARTS));
        myCards.add(new Card(Rank.FIVE, Suit.HEARTS));
        myCards.add(new Card(Rank.NINE, Suit.HEARTS));
        myCards.add(new Card(Rank.JACK, Suit.HEARTS));
        myCards.add(new Card(Rank.KING, Suit.HEARTS));
        assertEquals(List.of(13), hr.checkForFlush(myCards));

        // Negative case
        myCards.set(4, new Card(Rank.KING, Suit.CLUBS));
        assertEquals(new ArrayList<>(), hr.checkForFlush(myCards));
    }

    // Test dla metody checkForStraight
    @Test
    public void testCheckForStraight() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.TWO, Suit.HEARTS));
        myCards.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards.add(new Card(Rank.FOUR, Suit.SPADES));
        myCards.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.SIX, Suit.HEARTS));
        assertEquals(List.of(6), hr.checkForStraight(myCards));

        // Ace-low straight
        myCards.clear();
        myCards.add(new Card(Rank.TWO, Suit.HEARTS));
        myCards.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards.add(new Card(Rank.FOUR, Suit.SPADES));
        myCards.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(List.of(5), hr.checkForStraight(myCards));
    }

    // Test dla metody checkForHighCard
    @Test
    public void testCheckForHighCard() {
        HandRanker hr = new HandRanker();
        ArrayList<Card> myCards = new ArrayList<>();
        myCards.add(new Card(Rank.THREE, Suit.CLUBS));
        myCards.add(new Card(Rank.FIVE, Suit.DIAMONDS));
        myCards.add(new Card(Rank.NINE, Suit.HEARTS));
        myCards.add(new Card(Rank.TEN, Suit.SPADES));
        myCards.add(new Card(Rank.KING, Suit.CLUBS));
        assertEquals(List.of(13, 10, 9, 5, 3), hr.checkForHighCard(myCards));
    }
}
