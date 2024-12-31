package pl.edu.agh.kis.pz1;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class DeckTest {

    @Test
    public void testShuffle() {
        Deck deck = new Deck();
        deck.shuffle();
        assertEquals(52, deck.getGameDeck().size());

    }

    @Test
    public void testGetCard() {
        Deck deck = new Deck();
        Card c = deck.getCard();
        assertNotNull(c);
    }

    @Test
    public void toStringTest() {
        Deck deck = new Deck();
        deck.shuffle();
        String res = deck.toString();
        assertNotNull(res);
    }


}