package pl.edu.agh.kis.pz1.util;

import pl.edu.agh.kis.pz1.Player;

import java.util.List;

/**
 * The {@code PokerJudge} class provides utility methods to judge poker game situations.
 * It includes methods for deciding the winner between two players based on their card rankings
 * and verifying whether the betting round has ended.
 * <p>
 * This class cannot be instantiated, as it is a utility class containing only static methods.
 * </p>
 */
public class PokerJudge {

    /**
     * Private constructor to prevent instantiation of the utility class.
     */
    private PokerJudge(){
        throw new IllegalStateException("Utility class");
    }

    /**
     * Decides the winner between two players based on their card rankings.
     * The cards are compared in order, and the player with the higher card wins.
     * If the cards are equal, the next card is compared.
     * If all cards are equal, the result is a tie (0).
     * <p>
     * If {@code cards1} is greater, player 1 is declared the winner (return value 1),
     * and if {@code cards2} is greater, player 2 wins (return value -1).
     * </p>
     *
     * @param cards1 a list of integers representing the card ranks of player 1
     * @param cards2 a list of integers representing the card ranks of player 2
     * @return 1 if player 1 wins, -1 if player 2 wins, 0 if it's a tie
     */
    public static int decideWinnerBetweenTwoPlayers(List<Integer> cards1, List<Integer> cards2) {
        for(int j = 0; j < cards1.size(); j++){
            if(!cards1.get(j).equals(cards2.get(j))){
                if(cards1.get(j) > cards2.get(j)){
                    return 1;
                }
                else {
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * Verifies whether the betting round has ended for all players.
     * The round is considered over if all non-folded players have placed the same bet.
     * <p>
     * This method checks each player's bet and compares it with the current bet.
     * If a player's bet differs from the current bet, the method returns {@code false},
     * indicating that the round has not ended. If all active players have placed the same bet,
     * the method returns {@code true}.
     * </p>
     *
     * @param players the list of players in the current round
     * @param foldedPlayers the list of players who have folded and should not be considered
     * @return {@code true} if the betting has ended, {@code false} otherwise
     */
    public static boolean verifyWhetherTheBettingEnded(List<Player> players, List<Integer> foldedPlayers) {
        int currBet = -1;
        for(Player p : players){
            System.out.println("Player " + p.getId() + " betted: " + p.getBetInRound());
        }
        for (Player p : players) {
            if (!foldedPlayers.contains(p.getId())) {
                if (currBet == -1) currBet = p.getBetInRound();
                if (currBet != p.getBetInRound()) return false;
            }
        }
        return true;
    }
}
