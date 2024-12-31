package pl.edu.agh.kis.pz1;

import lombok.Getter;
import lombok.Setter;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The Player class represents a player in the poker game.
 * It stores information about the player's current hand of cards, their bet for the current round,
 * and provides methods for receiving cards from a deck and managing the player's game state.
 * <p>
 * Each player has an associated {@link SelectionKey} (likely for handling network communication) and a unique ID.
 * The class uses Lombok annotations to generate getter and setter methods for the fields.
 */
@Getter
@Setter
public class Player {

    /** The selection key associated with the player, likely for network communication. */
    private SelectionKey key;

    /** A list of cards that the player currently holds. */
    private List<Card> cards = new ArrayList<>();

    /** The amount of money or chips the player has bet during the current round. */
    private int betInRound = 0;

    /** A unique identifier for the player. */
    private int id;

    /**
     * Constructor that initializes the Player object with a given {@link SelectionKey} and an ID.
     *
     * @param key The selection key associated with the player.
     * @param id The unique ID of the player.
     */
    public Player(SelectionKey key, int id) {
        this.key = key;
        this.id = id;
    }

    /**
     * Deals a specified number of cards to the player from a given deck.
     * The cards are added to the player's hand.
     *
     * @param deck The deck from which to deal the cards.
     * @param howMany The number of cards to deal to the player.
     */
    public void receiveCards(Deck deck, int howMany){
        for(int i = 0; i < howMany; ++i){
            this.cards.add(deck.getCard()); // Adds a card from the deck to the player's hand
        }
    }
}
