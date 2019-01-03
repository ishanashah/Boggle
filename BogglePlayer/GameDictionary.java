package BogglePlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GameDictionary implements BoggleDictionary{
    private CharacterNode head;
    private List<String> words = new LinkedList<>();

    public GameDictionary(){
        head = new CharacterNode();
    }

    //Scans each word in the given file and adds it to the dictionary
    public void loadDictionary(String filename) throws IOException {
        Scanner in = new Scanner(new File(filename));
        while(in.hasNext()){
            String next = in.next().toLowerCase();
            head.addString(next);
            words.add(next);
        }
        Collections.sort(words);
    }

    //Uses our tree's searchPrefix() method
    public boolean isPrefix(String prefix) {
        return head.searchPrefix(prefix.toLowerCase());
    }

    //Uses our tree's contains() method
    public boolean contains(String word) {
        return head.searchWord(word.toLowerCase());
    }

    //Returns the iterator of the List representing our dictionary
    public Iterator<String> iterator(){
        return words.iterator();
    }

    //Tree containing all the words in the dictionary
    //Uses only a boolean marking the end of a word
    //and an array of references to other CharacterNodes,
    //allowing us to represent words without using any
    //characters or strings, and helping us minimize space
    //complexity.
    private static class CharacterNode {
        private BogglePlayer.GameDictionary.CharacterNode[] children = null;
        private boolean endCharacter = false;

        //the addition operation to our tree data structure
        //uses an array to recursively add each character from parent to child,
        //starting at the root of the tree.
        private void addString(String input){
            if(input.length() == 0){
                endCharacter = true;
                return;
            }
            if(children == null){
                children = new BogglePlayer.GameDictionary.CharacterNode[26];
                for(int i = 0; i < children.length; i++){
                    children[i] = null;
                }
            }
            if(children[input.charAt(0) - 97] == null){
                children[input.charAt(0) - 97] = new BogglePlayer.GameDictionary.CharacterNode();
            }
            children[input.charAt(0) - 97].addString(input.substring(1));
        }

        //recursively searches starting from the root off the tree
        //whether each node has a child representing the next character in the prefix.
        private boolean searchPrefix(String prefix){
            if(prefix.length() == 0){
                return true;
            }
            if(children == null || children[prefix.charAt(0) - 97] == null) {
                return false;
            }
            return children[prefix.charAt(0) - 97].searchPrefix(prefix.substring(1));
        }

        //recursively searches starting from the root off the tree
        // whether each node has a child representing the next character in the prefix.
        private boolean searchWord(String word) {
            if(word.length() == 0) {
                //requires the final node to mark the end of a word.
                return endCharacter;
            }
            if(children == null || children[word.charAt(0) - 97] == null) {
                return false;
            }
            return children[word.charAt(0) - 97].searchWord(word.substring(1));
        }
    }
}
