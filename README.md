# Poker Game

## Game Rules

This project implements a **Poker** game where players engage in a series of rounds involving betting, card exchanges, and showdowns to determine the winner. The rules follow traditional 5-card draw poker with two betting rounds.

### Round 1: Initial Betting

- All players are dealt 5 cards.
- The first betting round begins, where players can:
  - Place bets
  - Check
  - Fold
- Once all bets are placed, the first betting round ends, and the players proceed to the **Card Exchange Phase**.

### Card Exchange Phase

- Each player may discard any number of their cards and request an equal number of new cards from the deck.
- After the card exchange, the **Second Betting Round** begins.

### Round 2: Second Betting

- The second round of betting occurs, where players can:
  - Place bets again
  - Fold
- Once all bets are placed, the game moves to the **Showdown Phase**.

### Showdown Phase

- The players reveal their cards.
- The winner is determined by comparing the hands of the remaining players.

### Hand Rankings

The hands are ranked according to standard poker rules:

- **Royal Flush**: A, K, Q, J, 10, all of the same suit.
- **Straight Flush**: Five consecutive cards of the same suit.
- **Four of a Kind**: Four cards of the same rank.
- **Full House**: Three of a kind and a pair.
- **Flush**: Five cards of the same suit, not in order.
- **Straight**: Five consecutive cards, not of the same suit.
- **Three of a Kind**: Three cards of the same rank.
- **Two Pair**: Two pairs of cards of the same rank.
- **Pair**: Two cards of the same rank.
- **High Card**: The highest card in the hand if no other combination is made.

The player with the best hand wins the pot.

### Important Notes

- Players have an **unlimited amount of money**, so feel free to spend millions of $$$.
- The game ends after the winner is decided, and players are kicked out, ending their session.
- Hands of players are distinguishable. If two players have the same hand rank, they are compared based on the values of their cards in hand. If that fails, they are compared based on distinguishable cards. If all these fail, the draw is declared.
- **You cannot bet floating-point numbers.**

## Running the Program

To run the program, follow these steps:

1. Run the **NonBlockingServerClass** and type: `"Poker"`. You will be guided to type additional information essential for starting the game.
2. Once you are finished, run the **Client Class**.
3. Once the required number of players is reached, the game will begin, and you will be guided through the rest of it.
4. After the game ends, you will be asked if you want to continue the game or shut down the server.

## Communication Protocol

Every message sent by the server is transmitted using the **GameMessenger** class (see Javadoc for details). It has two static methods:

- `sendResponse(playerChannel, message)`
- `sendMessageToAll(List players, message)`

These methods are described in detail in the Javadoc.

### Messages Sent by the Server

- **When a player folds or spectates**:  
  `gameMessenger.sendResponse(playerChannel, FOLD_PLAYER_LITERAL);`
  
- **When the game is running but it’s not the player's turn**:  
  `gameMessenger.sendResponse(playerChannel, WAITING_PLAYER_LITERAL);`

- **When a player needs to call, raise, or fold**:  
  `gameMessenger.sendResponse(currCh, "You need to call, raise or fold\nCurrent bet is " + getCurrentBet() + "\nChoice: ");`

- **When cards are dealt**:  
  `gameMessenger.sendResponse(clientChannel, "Your cards: " + player.getCards().toString());`

- **When it’s a player’s turn to make a move**:  
  `gameMessenger.sendResponse(playerChannel, "Your turn! What do you want to do?\nPossible operations are:\n-check\n-bet (in format like bet #amount)\n-fold\n-raise (in format like raise #amount)\n-call\nCurrent bet is: " + getCurrentBet() + "\n\nChoice: ");`

- **When an invalid action is made**:  
  `gameMessenger.sendResponse(clientChannel, "Invalid action. Possible actions are: check, bet, raise, call, or fold.");`

- **When an unexpected error occurs**:  
  `gameMessenger.sendResponse(clientChannel, "Invalid request, please try again");`

- **When a player tries to call, but they already did**:  
  `gameMessenger.sendResponse(clientChannel, "Nothing to call, you are already at the current bet.");`

- **When a player tries to raise for less than the current bet**:  
  `gameMessenger.sendResponse(clientChannel, "Raise amount must exceed the bet!");`

- **When a player tries to raise when there is no bet**:  
  `gameMessenger.sendResponse(clientChannel, "There is no bet currently!");`

- **When a player successfully raises the current bet**:  
  `gameMessenger.sendMessageToAll(getPlayers(), PLAYER_LITERAL + currentPlayerIndex + " raises to " + currentBet);`

- **When a player tries to check, but a bet has already been placed**:  
  `gameMessenger.sendResponse(clientChannel, "You cannot check, there's a bet in play!");`

- **When a player tries to bet, but a bet is already in place**:  
  `gameMessenger.sendResponse(clientChannel, "Bet has already begun, you can only raise!");`

- **When the player enters input in an incorrect format**:  
  `gameMessenger.sendResponse(clientChannel, "Invalid input!");`

- **Card exchange phase**:  
  `gameMessenger.sendResponse(playerChannel, "What cards do you want to exchange?" + "\nProvide your input in a way like: #number #number #number e.g., 1 2 3" + "\nYour cards are: " + players.get(currentPlayerIndex).getCards());`

- **Ending phase when a player folds before the showdown**:  
  `gameMessenger.sendMessageToAll(getPlayers(), PLAYER_LITERAL + p.getId() + " is already folded");`

- **Evaluating a player's hand during the showdown phase**:  
  `gameMessenger.sendMessageToAll(getPlayers(), PLAYER_LITERAL + p.getId() + " cards are: " + players.get(p.getId()).getCards() + "\nWhich gives them: " + ranker.rank(players.get(p.getId()).getCards()));`

- **When a player acts out of turn**:  
  `gameMessenger.sendResponse(clientChannel, "It's not your turn.");`

### Messages Players Can Send

#### Betting Phase
- **call**: To match the current bet.
- **check**: To do nothing if no bet is placed.
- **bet #amount**: To place a new bet.
- **raise #amount**: To raise the current bet.
- **fold**: To fold and spectate.

#### Card Exchange Phase
- Provide numbers in the format `#number #number ...` (1-5) to specify which cards to exchange.
- If a player doesn’t want to exchange any cards, they can type an empty space.

If a player uses an invalid command or sends a message out of turn, they will be prompted with an error message.

## Additional Information

### Server
- The game is hosted on a server that accepts multiple client connections. Players connect to the server to participate in the game.

### Clients
- Players interact with the server via the client application. Each player can place bets, exchange cards, and reveal their hand during the showdown.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
