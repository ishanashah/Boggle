package BogglePlayer;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GameManager implements BoggleGame {
    private int size;
    //private int numPlayers;
    private Map<String, Integer> playerLists = new HashMap<>();
    private String cubeFile;
    private BoardGraph board;
    private SearchTactic tactic = BoggleGame.SEARCH_DEFAULT;
    private List<Point> lastAddedWord;
    private int[] scores;

    //Creates new Boggle game using specified parameters
    @Override
    public void newGame(int size, int numPlayers, String cubeFile, BoggleDictionary dict) throws IOException {
        this.size = size;
        //this.numPlayers = numPlayers;
        this.scores = new int[numPlayers];
        for(int i = 0; i < scores.length; i++){
            scores[i] = 0;
        }
        this.cubeFile = cubeFile;
        this.board = new BoardGraph(shuffleBoard(), dict);
        this.lastAddedWord = null;
    }

    //Shuffles board
    private char[][] shuffleBoard() throws FileNotFoundException {
        char[][] board = new char[size][size];
        Scanner in;
        Stack<Character> chars = new Stack<>();
        Random rand = new Random();

        while(chars.size() < size * size){
            in = new Scanner(new File(cubeFile));
            while(in.hasNext()){
                String cube = in.next();
                chars.add(cube.charAt(rand.nextInt(cube.length())));
            }
        }
        Collections.shuffle(chars);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                board[row][col] = chars.pop();
            }
        }
        return board;
    }

    @Override
    public char[][] getBoard() {
        return board.getBoard();
    }

    //Adds word, and updates scores and lists accordingly
    @Override
    public int addWord(String word, int player) {
        word = word.toLowerCase();
        if (!board.getDict().contains(word) || word.length() < 4 || playerLists.keySet().contains(word)) {
            return 0;
        }
        List<Point> list = searchWord(word);
        if(list != null){
            lastAddedWord = list;
            playerLists.put(word, player);
            scores[player] += word.length() - 3;
            return word.length() - 3;
        }
        return 0;
    }

    @Override
    public List<Point> getLastAddedWord() {
        return lastAddedWord;
    }

    @Override
    public void setGame(char[][] board) {
        this.board = new BoardGraph(board, this.board.getDict());
        for(int i = 0; i < scores.length; i++){
            scores[i] = 0;
        }
        playerLists.clear();
    }
    
    @Override
    public Collection<String> getAllWords() {
        Collection<String> output;
        if (tactic == SearchTactic.SEARCH_BOARD) {
            output = searchBoard();
        } else if (tactic == SearchTactic.SEARCH_DICT) {
            output = searchDict();
        } else {
            return null;
        }
        output.removeIf((String word) -> playerLists.containsKey(word));
        return output;
    }

    private Collection<String> searchBoard() {
        return board.searchBoard();
    }

    private Collection<String> searchDict() {
        return board.searchDict();
    }

    private List<Point> searchWord(String word){
        return board.searchWord(word);
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
        this.tactic = tactic;
    }

    @Override
    public int[] getScores() {
        return scores.clone();
    }

    private static class BoardGraph {
        private BoardNode[][] graph;
        private BoggleDictionary dict;
        private Collection<String> allWords;

        private BoardGraph(char[][] board, BoggleDictionary dict){
            graph = new BoardNode[board.length][board[0].length];
            for(int row = 0; row < graph.length; row++){
                for(int col = 0; col < graph[row].length; col++){
                    graph[row][col] = new BoardNode(Character.toLowerCase(board[row][col]), new Point(row, col));
                }
            }

            for(int row = 0; row < graph.length; row++){
                for(int col = 0; col < graph[row].length; col++){
                    Stack<BoardNode> adjacency = new Stack<>();
                    for(int x = row -1; x <= row + 1; x++){
                        if(x < 0 || x >= graph.length){
                            continue;
                        }
                        for(int y = col - 1; y <= col + 1; y++){
                            if(y < 0 || y >= graph[x].length ||
                                    (x == row && y == col)){
                                continue;
                            }
                            adjacency.push(graph[x][y]);
                        }
                    }
                    BoardNode[] adjacencyArray = new BoardNode[adjacency.size()];
                    for(int i = 0; i < adjacencyArray.length; i++){
                        adjacencyArray[i] = adjacency.pop();
                    }
                    graph[row][col].setAdjacency(adjacencyArray);
                }
            }
            this.dict = dict;
            allWords = null;
        }

        private Collection<String> searchBoard(){
            allWords = new HashSet<>();
            for(BoardNode[] row: graph){
                for(BoardNode node: row){
                    searchNode(node, "");
                    resetVisited();
                }
            }
            return allWords;
        }

        private void searchNode(BoardNode node, String prefix){
            if(!dict.isPrefix(prefix)){
                return;
            }
            if(prefix.length() >= 4 && dict.contains(prefix)){
                allWords.add(prefix);
            }
            node.setVisited(true);
            BoardNode[] adjacency = node.getAdjacency();
            if(adjacency.length == 0 && dict.contains(prefix + node.getCharacter())){
                allWords.add(prefix + node.getCharacter());
            }
            for(BoardNode adj: adjacency){
                searchNode(adj, prefix + node.getCharacter());
            }
            node.setVisited(false);
        }

        private Collection<String> searchDict(){
            allWords = new HashSet<>();
            String prefix = null;
            mainSearch: for(String word: dict){
                if(word.length() < 4){
                    continue;
                }
                if(prefix != null && word.startsWith(prefix)){
                    continue;
                }
                prefix = null;
                for(BoardNode[] row: graph){
                    for(BoardNode node: row){
                        if(searchWord(node, word)){
                            allWords.add(word);
                            resetVisited();
                            continue mainSearch;
                        } else {
                            resetVisited();
                        }
                    }
                }
                prefix = word;
            }
            return allWords;
        }

        private boolean searchWord(BoardNode node, String word){
            if(word.length() == 0){
                return true;
            }
            if(node.getCharacter() != word.charAt(0)){
                return false;
            }
            if(word.length() == 1){
                return true;
            }
            word = word.substring(1);
            node.setVisited(true);
            BoardNode[] adjacency = node.getAdjacency();
            for(BoardNode adj: adjacency){
                if(searchWord(adj, word)){
                    return true;
                }
            }
            node.setVisited(false);
            return false;
        }

        private boolean searchWord(BoardNode node, String word, List<Point> list){
            if(word.length() == 0){
                return true;
            }
            if(node.getCharacter() != word.charAt(0)){
                return false;
            }
            if(word.length() == 1){
                list.add(node.getPosition());
                return true;
            }
            word = word.substring(1);
            node.setVisited(true);
            list.add(node.getPosition());
            BoardNode[] adjacency = node.getAdjacency();
            for(BoardNode adj: adjacency){
                if(searchWord(adj, word, list)){
                    return true;
                }
            }
            node.setVisited(false);
            list.remove(node.getPosition());
            return false;
        }

        private List<Point> searchWord(String word){
            List<Point> list = new LinkedList<>();
            for(BoardNode[] row: graph){
                for(BoardNode node: row){
                    if(searchWord(node, word, list)){
                        return list;
                    }
                    resetVisited();
                }
            }
            return null;
        }

        private void resetVisited(){
            for(BoardNode[] row: graph){
                for(BoardNode node: row){
                    node.setVisited(false);
                }
            }
        }

        private BoggleDictionary getDict(){
            return dict;
        }

        private char[][] getBoard(){
            char[][] output = new char[graph.length][graph[0].length];
            for(int i = 0; i < output.length; i++){
                for(int j = 0; j < output[i].length; j++){
                    output[i][j] = Character.toUpperCase(graph[i][j].getCharacter());
                }
            }
            return output;
        }

        private static class BoardNode {
            private char character;
            private BoardNode[] adjacency;

            private boolean visited;
            private Point position;

            private BoardNode(char character, Point position){
                this.character = character;
                visited = false;
                this.position = position;
            }

            private void setAdjacency(BoardNode[] adjacency){
                this.adjacency = adjacency.clone();
            }

            private BoardNode[] getAdjacency(){
                Stack<BoardNode> adjacency = new Stack<>();
                for(BoardNode node: this.adjacency){
                    if(!node.isVisited()){
                        adjacency.push(node);
                    }
                }
                BoardNode[] adjacencyArray = new BoardNode[adjacency.size()];
                for(int i = 0; i < adjacencyArray.length; i++){
                    adjacencyArray[i] = adjacency.pop();
                }
                return adjacencyArray;
            }

            private char getCharacter(){
                return character;
            }

            private boolean isVisited(){
                return visited;
            }

            private void setVisited(boolean visited){
                this.visited = visited;
            }

            private Point getPosition(){
                return new Point((int) position.getX(), (int) position.getY());
            }
        }
    }
}
