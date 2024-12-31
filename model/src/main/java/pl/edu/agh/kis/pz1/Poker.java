package pl.edu.agh.kis.pz1;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.agh.kis.pz1.exceptions.CardsParsingException;
import pl.edu.agh.kis.pz1.exceptions.PlayerDisconnectedException;
import pl.edu.agh.kis.pz1.util.PokerMessenger;
import pl.edu.agh.kis.pz1.util.PokerJudge;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Represents the Poker game implementation, extending the base Game class.
 * Manages game phases, players, betting, card dealing, and winner determination.
 */
@Getter
@Setter
@NoArgsConstructor
public class Poker extends Game {

    /**
     * A map that defines the hierarchy of poker hands, from highest to lowest.
     * Key is the hand type (e.g., "Royal Flush"), value is the corresponding rank.
     */
    private final Map<String, Integer> hierarchy = Map.of(
            "High Card", 1,
            "Pair", 2,
            "Two Pair", 3,
            "Three Of A Kind", 4,
            "Straight", 5,
            "Flush", 6,
            "Full House", 7,
            "Four Of A Kind", 8,
            "Straight Flush", 9,
            "Royal Flush", 10
    );

    /**
     * A deck of cards used in the poker game.
     */
    private final Deck deck = new Deck();

    /**
     * The total amount of money in the pot.
     */
    private int pot;

    /**
     * A list of players participating in the game.
     */
    private List<Player> players = new ArrayList<>();

    /**
     * The current bet amount for the round.
     */
    private int currentBet = 0;

    /**
     * The current player ID for assigning to new players.
     */
    private int pId = 0;

    /**
     * The ante value (the initial bet placed by each player before cards are dealt).
     */
    private int ante;

    /**
     * Flag indicating whether the game server is running.
     */
    private boolean isGameServerRunning = false;

    /**
     * A list of players who have folded during the round.
     */
    private List<Integer> foldedPlayers = new ArrayList<>();

    /**
     * The number of players currently in the game.
     */
    private int currentPlayers = 0;

    /**
     * A literal used to identify players in messages (e.g., "Player 1").
     */
    private static final String PLAYER_LITERAL = "Player ";

    /**
     * A literal message sent to players who have folded.
     */
    private static final String FOLD_PLAYER_LITERAL = "You folded, you cannot perform an action!";

    /**
     * A literal message sent to players who are waiting for their turn.
     */
    private static final String WAITING_PLAYER_LITERAL = "Waiting for your turn...";

    /**
     * The current phase of the game (e.g., card dealing, betting).
     */
    private int phase = 0;

    /**
     * The index of the current player whose turn it is.
     */
    private int currentPlayerIndex = 0;

    /**
     * The maximum number of players allowed in the game.
     */
    private int maxPlayers;

    /**
     * Flag indicating whether the game has started.
     */
    private boolean gameStarted = false;

    /**
     * Flag indicating whether the betting phase has possibly ended.
     */
    private boolean isRoundPossiblyEnded = false;

    /**
     * A messenger object used to communicate with players.
     */
    private PokerMessenger gameMessenger = new PokerMessenger();

    /**
     * An object responsible for evaluating poker hands and determining the winner.
     */
    private HandRanker ranker = new HandRanker();

    /**
     * The index of the first player who has not folded.
     */
    private int firstNonFoldedPlayer = 0;


    /**
     * Sets up a new game with player limits and ante value.
     *
     * @param sc Scanner to read user input.
     */
    @Override
    public void getNewGameInfo(Scanner sc) {
        System.out.println("Enter maximum number of players: ");
        setMaxPlayers(Integer.parseInt(sc.nextLine()));
        System.out.println("Maximum number of players has been set to: " + getMaxPlayers());
        System.out.println("Enter ante: ");
        setAnte(Integer.parseInt(sc.nextLine()));
        setPot(getMaxPlayers() * getAnte());
        System.out.println("Ante has been set to: " + getAnte());
        setPhase(1);
        if(getPhase() == 1 && !isGameStarted()) System.out.println("Looking for players...");
    }

    /**
     * Adds a new player to the game.
     *
     * @param key The selection key of the new player.
     */
    @Override
    public void addPlayer(SelectionKey key) {
        players.add(new Player(key, pId));
        ++pId;
    }

    /**
     * Starts the game by dealing cards and notifying players about the start of betting.
     *
     * @throws IOException if an I/O error occurs during card dealing.
     * @throws InterruptedException if the thread is interrupted during the game setup.
     */
    @Override
    public void startGame() throws IOException, InterruptedException {
        dealCards();
        notifyPlayersAboutTheirCards();
        Thread.sleep(2000);
        gameMessenger.sendMessageToAll(getPlayers(), "Gentlemen, we are starting our bets!");
        Thread.sleep(1500);
        setPhase(2);
        performBetting();
        setGameStarted(true);
    }


    /**
     * Deals 5 cards to each player from the deck.
     */
    public void dealCards(){
        deck.shuffle();
        System.out.println("Dealing Cards!");
        for(Player player : players){
            player.receiveCards(deck, 5);
        }
    }

    /**
     * Notifies players about their dealt cards.
     *
     * @throws IOException if an I/O error occurs while sending the message to the player.
     */
    public void notifyPlayersAboutTheirCards() throws IOException {
        for(Player player : players){
            SocketChannel clientChannel = (SocketChannel) player.getKey().channel();
            gameMessenger.sendResponse(clientChannel, "Your cards: " + player.getCards().toString());
        }
    }

    /**
     * Manages the betting process for each player.
     *
     * @throws IOException if an I/O error occurs while sending responses to players.
     */
    public void performBetting() throws IOException {
        for (Player p : players) {
            SocketChannel playerChannel = (SocketChannel) players.get(p.getId()).getKey().channel();
            if(foldedPlayers.contains(p.getId())) {
                gameMessenger.sendResponse(playerChannel, FOLD_PLAYER_LITERAL);
            } else if (p.getId() == getCurrentPlayerIndex()) {
                gameMessenger.sendResponse(playerChannel, "Your turn! What do you want to do?\nPossible operations are:" +
                        "\n-check" +
                        "\n-bet (in format like bet #amountofMoney)" +
                        "\n-fold" +
                        "\n-raise (in format like raise #amountofMoney)" +
                        "\n-call" +
                        "\n\n*Current bet is: " + getCurrentBet() + "*" +
                        "\n\nChoice: ");
            } else {
                gameMessenger.sendResponse(playerChannel, WAITING_PLAYER_LITERAL);
            }
        }
    }

    /**
     * Manages a player's betting action (check, bet, fold, raise, or call).
     *
     * @param clientChannel  The client's channel.
     * @param splitted       The split user input containing the action and amount (if applicable).
     * @param currentPlayerIndex The index of the current player.
     * @return -1 if the action is valid, -2 if invalid, or the winner's index if all players folded.
     * @throws IOException if an I/O error occurs during the process.
     */
    public int managePlayerBettingTurn(SocketChannel clientChannel, String[] splitted, int currentPlayerIndex) throws IOException {
        int validAction; //validAction =  -1 -> valid, -2 -> invalid, >= 0 all players folded : returns winner
        try {
            if (Objects.equals(splitted[0], "check")) {
                validAction = manageCheck(clientChannel, currentPlayerIndex);
            } else if (Objects.equals(splitted[0], "bet")) {
                int betAmount = Integer.parseInt(splitted[1]);
                validAction = manageBet(clientChannel, currentPlayerIndex, betAmount);

            } else if (Objects.equals(splitted[0], "fold")) {
                validAction = manageFold(currentPlayerIndex);
                findFirstIndexOfNonFoldedPlayer();

            } else if (Objects.equals(splitted[0], "raise")) {
                int raiseAmount = Integer.parseInt(splitted[1]);
                validAction = manageRaise(clientChannel, raiseAmount, currentPlayerIndex);
            } else if (Objects.equals(splitted[0], "call")) {
                int difference = currentBet - players.get(currentPlayerIndex).getBetInRound();
                validAction = manageCall(clientChannel, currentPlayerIndex, difference);

            } else {
                gameMessenger.sendResponse(clientChannel, "Invalid action. Possible actions are: check, bet, raise, call, or fold.");
                validAction = -2;

            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | IOException e) {
            gameMessenger.sendResponse(clientChannel, "Invalid request, please try again");
            validAction = -2;
        }
        return validAction;

    }

    /**
     * Finds the first player who has not folded and updates the index.
     */
    private void findFirstIndexOfNonFoldedPlayer() {
        for(int i = getFirstNonFoldedPlayer(); i < players.size(); ++i){
            if(!foldedPlayers.contains(i)){
                setFirstNonFoldedPlayer(i);
                break;
            }
        }
    }

    /**
     * Allows a player to exchange cards.
     *
     * @param cardNumbers      The indices of the cards to be exchanged.
     * @param currentPlayerIndex The index of the current player.
     * @throws CardsParsingException if an error occurs while parsing the card exchange request.
     */
    public void exchangeCards(List<Integer> cardNumbers, int currentPlayerIndex) throws CardsParsingException {
        try{
            List<Card> cards = players.get(currentPlayerIndex).getCards();
            cardNumbers.sort(Integer::compareTo);
            for(int i = cardNumbers.size() - 1; i >= 0; --i){
                cards.remove(cardNumbers.get(i) - 1);
            }
            players.get(currentPlayerIndex).receiveCards(deck, 5 - cards.size());
        } catch(IndexOutOfBoundsException e){
            throw new CardsParsingException("Invalid input!");
        }

    }

    /**
     * Determines the winner based on the best hand among remaining players.
     *
     * @return A string describing the winner(s) and the amount won.
     */
    public String decideWinner() {
        IntAndString result = getBestHandIndexAndBestHand();
        int bestHandIndex = result.number();
        String currentBestHand = result.text();
        List<Integer> winners = getWinners(bestHandIndex, currentBestHand);
        StringBuilder res = new StringBuilder("The winner ");
        if(winners.size() == 1){
            res.append("is:\n" + "-Player ").append(winners.get(0));
            res.append("\nHe won: ").append(getPot());
        }
        else{
            res.append("are:");
            for(int value : winners){
                res.append("\n" + "-Player ").append(value);
            }
            res.append("\nThey split the pot of: ").append(getPot()).append(" equally");
        }
        return res.toString();
    }

    /**
     * Determines the best hand among all players who have not folded.
     *
     * @return an IntAndString object containing the index of the player with the best hand and the description of that hand.
     */
    public IntAndString getBestHandIndexAndBestHand(){
        int bestHandIndex = -1;
        String currentBestHand = "";
        for(int i = 0; i < players.size(); i++){
            if(!foldedPlayers.contains(i)){
                if(bestHandIndex == -1){
                    bestHandIndex = i;
                    currentBestHand = ranker.rank(players.get(i).getCards());
                }
                else{
                    if(hierarchy.get(ranker.rank(players.get(i).getCards())).compareTo(hierarchy.get(currentBestHand)) > 0){
                        bestHandIndex = i;
                        currentBestHand = ranker.rank(players.get(i).getCards());
                    }
                }
            }
        }
        return new IntAndString(bestHandIndex, currentBestHand);
    }


    /**
     * Determines the list of winners among players with the best hand.
     *
     * @param bestHandIndex   The index of the player with the best hand.
     * @param currentBestHand The description of the current best hand.
     * @return a list of player indices who are winners.
     */
    private List<Integer> getWinners(int bestHandIndex, String currentBestHand) {
        List<Integer> winners = new ArrayList<>();
        winners.add(bestHandIndex);
        for(int i = 0; i < players.size(); i++){
            if(!winners.contains(i) && !foldedPlayers.contains(i) && hierarchy.get(ranker.rank(players.get(i).getCards())).
                    compareTo(hierarchy.get(currentBestHand)) == 0) {
                List<Integer> cards1 = ranker.distinguishmentCards(players.get(winners.get(0)).getCards());
                List<Integer> cards2 = ranker.distinguishmentCards(players.get(i).getCards());
                int flag = PokerJudge.decideWinnerBetweenTwoPlayers(cards1, cards2); // 0 - draw, 1 - cards1 won, -1 - cards2 won
                if(flag == 0){
                    winners.add(i);
                }
                else if(flag == -1){
                    winners.clear();
                    winners.add(i);
                }
            }

        }
        return winners;
    }


    /**
     * Manages the "call" action for a player.
     *
     * @param clientChannel        The channel of the client making the call.
     * @param currentPlayerIndex   The index of the current player.
     * @param difference           The difference between the player's current bet and the highest bet.
     * @return -2 if no call is needed, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private int manageCall(SocketChannel clientChannel, int currentPlayerIndex, int difference) throws IOException {
        if (difference <= 0) {
            gameMessenger.sendResponse(clientChannel, "Nothing to call, you are already at the current bet.");
            return -2;
        } else {
            players.get(currentPlayerIndex).setBetInRound(currentBet);
            gameMessenger.sendMessageToAll(getPlayers(), PLAYER_LITERAL + currentPlayerIndex + " calls");
        }
        return -1;
    }


    /**
     * Handles the "raise" action for a player.
     *
     * @param clientChannel        The channel of the client raising the bet.
     * @param raiseAmount          The amount the player raises.
     * @param currentPlayerIndex   The index of the current player.
     * @return -2 if the raise is invalid, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private int manageRaise(SocketChannel clientChannel, int raiseAmount, int currentPlayerIndex) throws IOException {
        if (raiseAmount <= currentBet) {
            gameMessenger.sendResponse(clientChannel, "Raise amount must exceed the bet!");
            return -2;
        } else if (currentBet == 0){
            gameMessenger.sendResponse(clientChannel, "There is no bet currently!");
            return -2;
        } else {
            currentBet = raiseAmount;
            players.get(currentPlayerIndex).setBetInRound(raiseAmount);
            gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + currentPlayerIndex + " raises to " + currentBet);
        }
        return -1;
    }

    /**
     * Handles the "fold" action for a player.
     *
     * @param currentPlayerIndex The index of the current player folding.
     * @return The index of the winner if only one player remains, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    int manageFold(int currentPlayerIndex) throws IOException {
        foldedPlayers.add(currentPlayerIndex);
        gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + currentPlayerIndex + " folded");
        if (players.size() - foldedPlayers.size() == 1) {
            int winner = -1;
            for (Player p : players) {
                if (!foldedPlayers.contains(p.getId())) {
                    winner = p.getId();
                    break;
                }
            }
            return winner;
        }
        return -1;
    }


    /**
     * Handles the "check" action for a player.
     *
     * @param clientChannel      The channel of the client performing the check.
     * @param currentPlayerIndex The index of the current player checking.
     * @return -2 if checking is invalid, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    protected int manageCheck(SocketChannel clientChannel, int currentPlayerIndex) throws IOException {
        if (currentBet > 0) {
            gameMessenger.sendResponse(clientChannel, "You cannot check, there's a bet in play!");
            return -2;
        } else {
            gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + currentPlayerIndex + " checks");
        }
        return -1;
    }

    /**
     * Handles the "bet" action for a player.
     *
     * @param clientChannel      The channel of the client placing the bet.
     * @param currentPlayerIndex The index of the current player betting.
     * @param betAmount          The amount being bet.
     * @return -2 if betting is invalid, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private int manageBet(SocketChannel clientChannel, int currentPlayerIndex, int betAmount) throws IOException {
        if (currentBet != 0) {
            gameMessenger.sendResponse(clientChannel, "Bet has already begun, you can only raise!");
            return -2;
        } else {
            currentBet = betAmount;
            players.get(currentPlayerIndex).setBetInRound(betAmount);
            gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + currentPlayerIndex + " bets " + betAmount);
        }
        return -1;
    }


    /**
     * Handles the card exchange phase for a player.
     *
     * @param clientChannel      The channel of the client requesting the card exchange.
     * @param request            The client's input specifying which cards to exchange.
     * @param currentPlayerIndex The index of the current player exchanging cards.
     * @return -2 if the request is invalid, -1 otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public int manageCardExchange(SocketChannel clientChannel, String request, int currentPlayerIndex) throws IOException {
        int validAction = -1;
        try{
            List<Integer> cardNumbers = new ArrayList<>();
            for(String num : request.split(" ")){
                cardNumbers.add(Integer.parseInt(num));
            }
            exchangeCards(cardNumbers, currentPlayerIndex);
        } catch(CardsParsingException | NumberFormatException e){
            gameMessenger.sendResponse(clientChannel, "Invalid input!");
            validAction = -2;
        }
        return validAction;
    }


    /**
     * Disconnects all players and resets the game state.
     *
     * @throws PlayerDisconnectedException if an error occurs during player disconnection.
     */
    public void disconnectAllPlayersAndResetGame() throws PlayerDisconnectedException {
        for (Player p : players) {
            SelectionKey key = p.getKey();
            try {
                SocketChannel channel = (SocketChannel) key.channel();
                channel.close();
                key.cancel();
            } catch (IOException e) {
                throw new PlayerDisconnectedException("Error disconnecting player: " + e.getMessage());
            }
        }
        System.out.println("All players have been disconnected.");
        resetGame();
    }


    /**
     * Handles a betting request from a client.
     *
     * @param request            The betting request received from the client.
     * @param clientChannel      The client's channel.
     * @param currentPlayerIndex The index of the current player.
     * @return The index of the winner if all other players folded, or -1 otherwise.
     * @throws IOException if an I/O error occurs.
     * @throws PlayerDisconnectedException if a player is disconnected during betting.
     */
    public int handleBetting(String request, SocketChannel clientChannel, int currentPlayerIndex) throws IOException, PlayerDisconnectedException {
        String[] splitted = request.split(" ");
        int validAction = managePlayerBettingTurn(clientChannel, splitted, currentPlayerIndex);
        if(validAction >= 0){
            gameMessenger.sendMessageToAll(getPlayers(),"The winner is: " + validAction + " as all players except him folded!");
            gameMessenger.sendMessageToAll(getPlayers(),decideWinner());
            disconnectAllPlayersAndResetGame();
        }
        return validAction;
    }

    /**
     * Removes a player from the game based on their selection key.
     *
     * @param key The selection key of the player to be removed.
     */
    public void remove(SelectionKey key) {
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getKey() == key){
                players.remove(i);
                break;
            }
        }
    }


    /**
     * Resets the game state, clearing players, bets, and other game variables.
     */
    @Override
    public void resetGame() {
        setPot(0);
        players.clear();
        setCurrentBet(0);
        setAnte(0);
        foldedPlayers.clear();
        setCurrentPlayers(0);
        setPhase(0);
        setCurrentPlayerIndex(0);
        setMaxPlayers(0);
        setPId(0);
        setGameServerRunning(false);
        setGameStarted(false);
    }


    /**
     * Handles the card exchange phase for all players.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void performCardExchange() throws IOException {
        for (Player p : players) {
            SocketChannel playerChannel = (SocketChannel) players.get(p.getId()).getKey().channel();
            if(foldedPlayers.contains(p.getId())) {
                gameMessenger.sendResponse(playerChannel, FOLD_PLAYER_LITERAL);
            } else if (p.getId() == currentPlayerIndex) {
                gameMessenger.sendResponse(playerChannel, "What cards do you want to exchange?" +
                        "\nProvide your input in a way like: #number #number #number eg. 1 2 3" +
                        "\nJust to remind you, your cards are: " + players.get(currentPlayerIndex).getCards());
            } else {
                gameMessenger.sendResponse(playerChannel, WAITING_PLAYER_LITERAL);
            }
        }
    }

    /**
     * Executes the showdown phase, revealing all player hands and determining the winner.
     *
     * @throws IOException if an I/O error occurs.
     * @throws PlayerDisconnectedException if a player is disconnected during the showdown.
     */
    public void performShowDown() throws IOException, PlayerDisconnectedException {
        for(Player p : players) {
            if(foldedPlayers.contains(p.getId())) {
                gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + p.getId() + " is already folded");
            } else {
                gameMessenger.sendMessageToAll(getPlayers(),PLAYER_LITERAL + p.getId() + " cards are: " + players.get(p.getId()).getCards() +
                        "\nWhich gives them: " + ranker.rank(players.get(p.getId()).getCards()));

            }
        }
        gameMessenger.sendMessageToAll(getPlayers(),decideWinner());
        disconnectAllPlayersAndResetGame();
    }


    /**
     * Processes a client's turn based on the current phase of the game.
     *
     * @param key          The selection key of the current client.
     * @param request      The request received from the client.
     * @param clientChannel The channel of the current client.
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the thread is interrupted during processing.
     * @throws PlayerDisconnectedException if a player is disconnected during their turn.
     */
    public void handleClientTurn(SelectionKey key, String request, SocketChannel clientChannel) throws IOException, InterruptedException, PlayerDisconnectedException {
        if (key == players.get(getCurrentPlayerIndex()).getKey()) {
            System.out.println("Received from player: " + request);
            int validAction = -1;
            if (getPhase() == 2 || getPhase() == 4) {
                validAction = handleBetting(request, clientChannel, getCurrentPlayerIndex());
                if(validAction >= 0) return;

            } else if (getPhase() == 3){
                validAction = manageCardExchange(clientChannel, request, getCurrentPlayerIndex());
            }
            Thread.sleep(500);
            if(validAction == -1) {
                performValidAction();
            }
        } else {
            gameMessenger.sendResponse(clientChannel, "It's not your turn.");
        }
    }

    /**
     * Moves to the next valid player who has not folded and continues the game flow.
     *
     * @throws IOException if an I/O error occurs.
     * @throws PlayerDisconnectedException if a player is disconnected during action execution.
     */
    public void performValidAction() throws IOException, PlayerDisconnectedException {
        int temp = getCurrentPlayerIndex();
        setNextValidPlayerIndex();
        if(getCurrentPlayerIndex() < temp) setRoundPossiblyEnded(true);
        System.out.println("Current player: " + currentPlayerIndex);
        if (isRoundPossiblyEnded()) {
            if (getPhase() == 2 || getPhase() == 4) {
                if(endBetting()) setPhase(3);

            } else if(getPhase() == 3){
                notifyPlayersAboutTheirCards();
                gameMessenger.sendMessageToAll(getPlayers(),
                        "Gentlemen, as the card exchange has come to an end, \n" +
                                "we will now proceed with the second betting round!");
                setRoundPossiblyEnded(false);
                setPhase(4);
            }

        }
        if ((getPhase() == 2 || getPhase() == 4) && !isRoundPossiblyEnded) {
            performBetting();
        } else if (getPhase() == 3) {
            performCardExchange();
        }
    }
    /**
     * Updates the current player index.
     */
    private void setNextValidPlayerIndex() {
        do {
            setCurrentPlayerIndex((getCurrentPlayerIndex() + 1) % getPlayers().size());
        } while (foldedPlayers.contains(getCurrentPlayerIndex()));
    }

    /**
     * Ends the current betting phase and progresses to the next game phase.
     *
     * @return true if everyone betted the same, false otherwise
     * @throws IOException if an I/O error occurs.
     */
    protected boolean endBetting() throws IOException, PlayerDisconnectedException {
        if(!PokerJudge.verifyWhetherTheBettingEnded(getPlayers(), getFoldedPlayers())) {
            forceBetting();
            return false;
        }else{
            for (Player p : getPlayers()) {
                setPot(getPot() + p.getBetInRound());
                p.setBetInRound(0);
            }
            setCurrentBet(0);
            String message1 = String.format(
                    "Gentlemen, as the first betting has come to an end, " +
                            "you can now exchange your cards. " +
                            "Current pot is now: %s", getPot());

            String message2 = String.format(
                    "Gentlemen, as the second betting has come to an end, " +
                            "we will now proceed with a showdown! " +
                            "Current pot is now: %s", getPot());


            System.out.println("Folded: " + foldedPlayers);
            for(int i = 0; i < players.size(); i++) {
                System.out.println(i);
                if(!foldedPlayers.contains(i)) {
                    setCurrentPlayerIndex(i);
                    break;
                }
            }
            gameMessenger.sendMessageToAll(getPlayers(),getPhase() == 2 ? message1 : message2);
            System.out.println("Game pot: " + getPot());
            setRoundPossiblyEnded(false);
            if(getPhase() == 4) performShowDown();
        }
        return true;
    }

    /**
     * Forces players who have not met the current bet to make a decision.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected void forceBetting() throws IOException {
        boolean foundPlayer = false;
        for(Player p : players){
            SocketChannel currCh = (SocketChannel) players.get(p.getId()).getKey().channel();
            if(getFoldedPlayers().contains(p.getId())){
                gameMessenger.sendResponse(currCh, FOLD_PLAYER_LITERAL);
            }else if (p.getBetInRound() < getCurrentBet() && !foundPlayer){
                setCurrentPlayerIndex(p.getId());
                foundPlayer = true;
                gameMessenger.sendResponse(currCh,
                        "You need to call, raise or fold\n" +
                                "Current bet is " + getCurrentBet() +
                                "\nChoice: ");
            } else{
                gameMessenger.sendResponse(currCh, WAITING_PLAYER_LITERAL);
            }
        }
    }


    /**
     * Handles a new client connection, adding them to the game if possible.
     *
     * @param sc       The socket channel of the connecting client.
     * @param selector The selector managing the client's channel.
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the thread is interrupted during connection handling.
     */
    @Override
    public void handleClientConnection(SocketChannel sc, Selector selector) throws IOException, InterruptedException {
        if (getCurrentPlayers() >= getMaxPlayers()) {
            if (sc != null) {
                String errorMessage = "Maximum number of players reached. Try again later.";
                ByteBuffer buffer = ByteBuffer.wrap(errorMessage.getBytes());
                sc.write(buffer);
                sc.shutdownOutput();
                sc.close();
            }
            return;
        }
        if (sc != null) {
            sc.configureBlocking(false);
            SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
            setCurrentPlayers(getCurrentPlayers() + 1);
            System.out.println("Client " + (getCurrentPlayers()) + " connected: " + sc.getRemoteAddress());
            System.out.println(getCurrentPlayers() + " " + getMaxPlayers());
            addPlayer(key);
            if(!isGameServerRunning) setGameServerRunning(true);
            if(getCurrentPlayers() == getMaxPlayers() && !gameStarted) {
                startGame();
                gameStarted = true;
            }
        }
    }

    /**
     * Processes a request from a connected client.
     *
     * @param clientChannel The client's channel.
     * @param buffer        The buffer containing the client's request.
     * @param key           The selection key of the client.
     * @throws PlayerDisconnectedException if a player disconnects during request handling.
     */
    @Override
    public void handleClientRequest(SocketChannel clientChannel, ByteBuffer buffer, SelectionKey key) throws PlayerDisconnectedException {
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
                clientChannel.close();
                key.cancel();
                remove(key);
                setCurrentPlayers(getCurrentPlayers() - 1);
                if (getCurrentPlayerIndex() >= getPlayers().size()) {
                    setCurrentPlayerIndex(0);
                }

            } else if (bytesRead > 0) {
                buffer.flip();
                String request = new String(buffer.array(), 0, buffer.limit());
                System.out.println("Processing action...");
                handleClientTurn(key, request, clientChannel);

            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error reading from client: " + e.getMessage());
            Thread.currentThread().interrupt();
            try {
                clientChannel.close();
                key.cancel();
                remove(key);
                setCurrentPlayers(getCurrentPlayers() - 1);
            } catch (IOException closeException) {
                System.err.println("Error closing client channel: " + closeException.getMessage());
            }
        } catch (PlayerDisconnectedException e) {
            throw new PlayerDisconnectedException("Player disconnected");
        }
    }

}


