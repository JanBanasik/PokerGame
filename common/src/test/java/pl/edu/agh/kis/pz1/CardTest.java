package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotEquals;

public class CardTest extends TestCase {

    @Test
    public void testTestClone() {
        Card card = new Card(Rank.FIVE, Suit.CLUBS);
        Card clone = new Card(card);
        assertNotSame(card, clone);
    }

    @Test
    public void equalsTest(){
        Card card1 = new Card(Rank.FIVE, Suit.CLUBS);
        Card card2 = new Card(Rank.FIVE, Suit.CLUBS);
        assertEquals(card1, card2);
    }

    @Test
    public void copyConstructorTest(){
        Card card = new Card(Rank.FIVE, Suit.CLUBS);
        Card clone = new Card(card);
        assertEquals(card, clone);
    }

    @Test
    public void hashCodeTest(){
        Card card1 = new Card(Rank.FIVE, Suit.CLUBS);
        Card card2 = new Card(Rank.FIVE, Suit.CLUBS);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    public void hashCodeTest2(){
        Card card1 = new Card(Rank.TWO, Suit.CLUBS);
        Card card2 = new Card(Rank.FIVE, Suit.CLUBS);
        assertNotEquals(card1.hashCode(), card2.hashCode());
    }
}