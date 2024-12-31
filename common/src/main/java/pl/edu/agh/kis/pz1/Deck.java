package pl.edu.agh.kis.pz1;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards used in the game.
 * This class allows creating a standard deck of 52 cards, shuffling the deck,
 * and drawing cards from it.
 *
 * @see Card
 * @see Suit
 * @see Rank
 */
@Getter
public class Deck {
    /**
     * The list representing the deck of cards.
     * Contains a collection of all 52 cards (13 ranks for each of the 4 suits).
     */
    private final List<Card> gameDeck;

    /**
     * Constructs a new deck of cards.
     * Initializes the deck by creating a standard set of 52 cards.
     */
    public Deck(){
        gameDeck = new ArrayList<>();
        createGameDeck(gameDeck);
    }

    /**
     * Creates a standard deck of 52 cards, consisting of 13 ranks for each of the 4 suits.
     *
     * @param deck The list in which the cards will be stored.
     */
    private void createGameDeck(List<Card> deck){
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){
                deck.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Shuffles the deck of cards.
     * Uses the `Collections.shuffle()` method to randomize the order of the cards.
     */
    public void shuffle(){
        Collections.shuffle(gameDeck);
    }

    /**
     * Draws (removes) the top card from the deck and returns it.
     * After this method is called, the deck will have one less card.
     *
     * @return The top card of the deck.
     */
    Card getCard(){
        Card value = gameDeck.get(0);
        gameDeck.remove(0);
        return value;
    }

    /**
     * Returns a string representation of the deck.
     * Displays all cards in the deck as a list of strings.
     *
     * @return A string representation of all cards in the deck.
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Card card : gameDeck){
            sb.append(card.toString()).append("\n");
        }
        return sb.toString();
    }
}
