package BogglePlayer;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Boggle {
    private static final String CUBE_FILE = "cubes.txt";
    private static final String DICT_FILE = "words.txt";


    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to Boggle!");
        System.out.println("Please enter the size of the Boggle board:");
        int size = in.nextInt();
        System.out.println("Please enter the number of players:");
        int numPlayers = in.nextInt();
        String cubeFile = CUBE_FILE;

        GameManager gm = new GameManager();
        BoggleDictionary dict = new GameDictionary();
        dict.loadDictionary(DICT_FILE);
        gm.newGame(size, numPlayers, cubeFile, dict);
        gm.setSearchTactic(BoggleGame.SearchTactic.SEARCH_DICT);

        System.out.println("Enter the words you find and type '/exit' when you want to stop. ");
        System.out.println("Type '/done' to finish your turn. ");
        int player = 1;
        int[] scores = new int[numPlayers];
        printBoard(gm);
        while(in.hasNext()){
            String input = in.next().toLowerCase();
            if(input.equals("/exit")){
                printScores(scores);
                System.exit(0);
            }
            if(input.equals("/done")){
                System.out.println("Player " + player + " Score: " + scores[player - 1]);
                player++;
                if(player > numPlayers){
                    player = 1;
                    printScores(scores, printAllWords(gm));
                    gm.newGame(size, numPlayers, cubeFile, dict);
                    System.out.println("Play Again?");
                    if(in.next().toLowerCase().equals("no")){
                        System.exit(0);
                    }
                }
                printBoard(gm);
                continue;
            }
            int points = gm.addWord(input, player - 1);
            scores[player - 1] += points;
            if(points == 0){
                System.out.println("Invalid Word");
            } else {
                System.out.println("Player " + player + " Received " + points + " Points");
            }
            printBoard(gm);
        }
    }

    private static void printBoard(GameManager gm){
        char[][] board = gm.getBoard();
        for(int row = 0; row < board.length; row++){
            for(int col = 0; col < board[row].length; col++){
                if(gm.getLastAddedWord() != null && gm.getLastAddedWord().contains(new Point(row, col))){
                    System.out.print(Character.toUpperCase(board[row][col]));
                } else {
                    System.out.print(Character.toLowerCase(board[row][col]));
                }
                System.out.print("  ");
            }
            System.out.println();
            System.out.println();
        }
    }

    private static void printScores(int[] scores){
        for(int player = 1; player <= scores.length; player++){
            System.out.println("Player " + player + ": " + scores[player - 1]);
        }
    }

    private static void printScores(int[] scores, int computerScore){
        for(int player = 1; player <= scores.length; player++){
            System.out.println("Player " + player + ": " + scores[player - 1]);
        }
        System.out.println("Computer: " + computerScore);
    }

    private static int printAllWords(GameManager gm){
        int score = 0;
        System.out.println("All Words Missed By Players:");
        Collection<String> allWords = gm.getAllWords();
        for(String word: allWords){
            System.out.println(word);
            score += word.length() - 3;
        }
        return score;
    }
}
