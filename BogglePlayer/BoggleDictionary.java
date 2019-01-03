package BogglePlayer;

import java.io.IOException;

/**
 * This interface defines the basic operations of a dictionary (collection of
 * words) for use with a game.
 */
public interface BoggleDictionary extends Iterable<String> {

    /**
     * Adds words from a file to this dictionary.  The file should contain
     * one word per line, with the words in ascending lexicographic order.
     */
    void loadDictionary(String filename) throws IOException;

    /**
     * Tests whether a String is the prefix of some word in this
     * dictionary.
     */
    boolean isPrefix(String prefix);

    /**
     * Tests whether a String is a word in this dictionary.
     */
    boolean contains(String word);
}
