README
1. Game Rules
This project implements a Poker game where players engage in a series of rounds involving betting, card exchanges, and showdowns to determine the winner. The rules follow traditional 5-card draw poker with two betting rounds.

Round 1: Initial Betting
All players are dealt 5 cards.
The first betting round begins, where players can place their bets, check, or fold.
Once all bets are placed, the first betting round ends, and the players proceed to the card exchange phase.

Card Exchange Phase
Each player may discard any number of their cards and request an equal number of new cards from the deck.
After the card exchange, the second betting round begins.

Round 2: Second Betting
The second round of betting occurs, where players can place their bets again or choose to fold.
Once all bets are placed, the game moves to the showdown phase.
Showdown Phase
The players reveal their cards.

The winner is determined by comparing the hands of the remaining players. The hand rankings follow the standard poker rules:

Royal Flush: A, K, Q, J, 10, all of the same suit.
Straight Flush: Five consecutive cards of the same suit.
Four of a Kind: Four cards of the same rank.
Full House: Three of a kind and a pair.
Flush: Five cards of the same suit, not in order.
Straight: Five consecutive cards, not of the same suit.
Three of a Kind: Three cards of the same rank.
Two Pair: Two pairs of cards of the same rank.
Pair: Two cards of the same rank.
High Card: The highest card in the hand if no other combination is made.
The player with the best hand wins the pot.

The game follows the standard poker rules, with some adaptations to handle communication between the server and multiple clients.
What's important: players have unlimited amount of money, so feel free to spend millions of $$$.
Additionally, the game ends after the winner is decided, so players are kicked out and their session is ended.

NOTE: Hands of players are distinguishable, that means that if both players have the same hank rank, they are then compared based on the values of their cards in hand,
if that fails, they are compared based on distinguishableCards. If all these fail, then the draw is declared.

NOTE: YOU CANNOT BET FLOATING POINT NUMBERS.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

2. Running the Program
To run the program, follow these steps:
Run the NonBlockingServerClass and simply type: "Poker".
You will be then guided to type additional information essential for the start of a game.
Once you are finished, you can run the Client class.
Once certain amount of players is reached, the game will Begin and you will be guided through the rest of it.
After the game ends, you will be asked if you want to continue the game or simply shut down the server.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
3. Communication Protocol

EVERY MESSAGE SENT BY SERVER IS SENT USING GameMessenger class: ( see Javadoc )
It has 2 static methods:
sendResponse(playerChannel, message);
sendMessageToAll(List<Player> ,message);
They are both highly decribed in Javadoc.

Messages Sent by the Server:

When player folds, or when he spectates the game they will get a message:
gameMessenger.sendResponse(playerChannel, FOLD_PLAYER_LITERAL);

If it's not someone's turn but we want to inform them that game is running:
gameMessenger.sendResponse(playerChannel, WAITING_PLAYER_LITERAL);


If player betted less than some other player, they will get:
gameMessenger.sendResponse(currCh,
                        "You need to call, raise or fold\n" +
                                "Current bet is " + getCurrentBet() +
                                "\nChoice: ");

If cards are dealt, player will get:
gameMessenger.sendResponse(clientChannel, "Your cards: " + player.getCards().toString());


When there is a betting round:
gameMessenger.sendResponse(playerChannel, "Your turn! What do you want to do?\nPossible operations are:" +
                        "\n-check" +
                        "\n-bet (in format like bet #amountofMoney)" +
                        "\n-fold" +
                        "\n-raise (in format like raise #amountofMoney)" +
                        "\n-call" +
                        "\n\n*Current bet is: " + getCurrentBet() + "*" +
                        "\n\nChoice: ");


When there is a betting round and a player tries to write something that is not valid:
gameMessenger.sendResponse(clientChannel, "Invalid action. Possible actions are: check, bet, raise, call, or fold.");
                

When an unexpected error occurs:
gameMessenger.sendResponse(clientChannel, "Invalid request, please try again");
            

When player wants to call, but he already did.
gameMessenger.sendResponse(clientChannel, "Nothing to call, you are already at the current bet.");
            

When player tries to raise for less than the current bet:
gameMessenger.sendResponse(clientChannel, "Raise amount must exceed the bet!");
            


When player tries to raise, when there is no bet:
gameMessenger.sendResponse(clientChannel, "There is no bet currently!");
            

When player correctly raised current bet:
gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + currentPlayerIndex + " raises to " + currentBet);
        

When there is a bet and player tries to check:
gameMessenger.sendResponse(clientChannel, "You cannot check, there's a bet in play!");


When player tries to bet, even though there is a bet currently: 
gameMessenger.sendResponse(clientChannel, "Bet has already begun, you can only raise!");
            
            
When player enters input in incorrect format, that was specified earlier:
gameMessenger.sendResponse(clientChannel, "Invalid input!");
            

Stage 3 of game message, card exchange phase beggining message:
gameMessenger.sendResponse(playerChannel, "What cards do you want to exchange?" +
                        "\nProvide your input in a way like: #number #number #number eg. 1 2 3" +
                        "\nJust to remind you, your cards are: " + players.get(currentPlayerIndex).getCards());
            

Ending phase, informs all the players, that certain player isn't participating in showdown:
gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + p.getId() + " is already folded");
           

Evaluating player hand in showdown phase:
gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + p.getId() + " cards are: " + players.get(p.getId()).getCards() +
                        "\nWhich gives them: " + ranker.rank(players.get(p.getId()).getCards()));

When player tries to type something not on their turn:
gameMessenger.sendResponse(clientChannel, "It's not your turn.");
        

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Messages that players can send:

Betting phase:

call - used to bet the same as current bet
check - used to literally do nothing, if there is no bet
bet #amountOfMoney - used to initiate the bet
raise #amountOfMoney - used to raise the bet

fold - used to fold, and go into spectate mode

Card Exchange phase:
Numbers range 1-5 formatted like: #number #number …
If player doesn't want to exchange any card, then he can just type in an empty spacer

If player uses a command that is not valid for a given turn, he is prompted with error.
If player sends a message to server, not on his turn, then they are given a prompt that the move was invalid.




---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

4. Additional Information
Server: The game is hosted on a server that accepts multiple client connections. Players connect to the server to participate in the game.
Clients: Players interact with the server via the client application. Each player can place bets, exchange cards, and reveal their hand during the showdown.

