package BogglePlayer;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.io.IOException;

/**
 * An interface which represents the entirety of a game of Boggle.
 */
public interface BoggleGame {

    /**
     * An enum for the possible search tactics when searching for words.
     */
    public static enum SearchTactic {
        /**
         * Search through the board in a depth-first search, checking for valid words.
         */
        SEARCH_BOARD,

        /**
         * Go through every dictionary word, and see if it exists on the board.
         */
        SEARCH_DICT
    }

    /**
     * The default search tactic to use if none are provided.
     */
    public static final SearchTactic SEARCH_DEFAULT = SearchTactic.SEARCH_BOARD;

    /**
     * Creates a new Boggle game using a size x size board and the
     * cubes specified in the file cubeFile.
     */
    void newGame(int size, int numPlayers,
                 String cubeFile, BoggleDictionary dict) throws IOException;


    /**
     * @return A size x size character array representing the Boggle
     * board, in row-major order.
     */
    char[][] getBoard();


    /**
     * Adds a word to the player's list and returns the point value
     * of the word.  If the word is invalid or the player cannot add
     * the word, it is worth zero points and is not actually added to
     * the player's list.  Player should not be able to add the same
     * word multiple times.
     */
    int addWord(String word, int player);


    /**
     * @return A list of Points (with respect to the board array)
     * showing the previous successfully added word.  If there is no
     * previous word, return null.
     *
     * The coordinates are listed by letter, then row, then column.
     */
    List<Point> getLastAddedWord();


    /**
     * Sets the game board to the given board (also in row-major
     * order, sets all player scores and lists to zero/empty.  Very
     * useful for debugging or playing a previous game.  The board
     * must be square.  Other game-related parameters (like the
     * dictionary) should be left as is.
     */
    void setGame(char[][] board);


    /**
     * @return A collection containing all valid words in the current
     * Boggle board. Uses the current search tactic.
     */
    Collection<String> getAllWords();


    /**
     * Sets the search tactic (used by getAllWords()) to the given
     * tactic.
     */
    void setSearchTactic(SearchTactic tactic);

    /**
     * @return The current scores for all players.
     */
    int[] getScores();

}
