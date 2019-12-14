/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Yasira Younus
 * yy7446
 * 16200
 * Zane Freeman
 * zcf222
 * 16200
 * Slip days used: <0>
 * Git URL: https://github.com/EE422C/project-3-wordladder-pair-29
 * Spring 2019
 */


package assignment3;
import java.util.*;
import java.io.*;

public class Main {
	
	public static ArrayList<String> inputWords = new ArrayList<String>();// static variables and constants only here.

	
	public static void main(String[] args) throws Exception {
		
		Scanner kb;	// input Scanner for commands
		PrintStream ps;	// output file, for student testing and grading only
		if (args.length != 0) {
			kb = new Scanner(new File(args[0]));
			ps = new PrintStream(new File(args[1]));
			System.setOut(ps);			// redirect output to ps
		} else {
			kb = new Scanner(System.in);// default input from Stdin
			ps = System.out;			// default output to Stdout
		}
		initialize();
		inputWords = parse(kb);
		if (inputWords.isEmpty()){
			return;
		}
		printLadder(getWordLadderDFS(inputWords.get(0), inputWords.get(1)));
		printLadder(getWordLadderBFS(inputWords.get(0), inputWords.get(1)));
	}
	
	public static void initialize() {
		inputWords = new ArrayList<String>();// static variables and constants only here.

 		// initialize your static variables or constants here.
		// We will call this method before running our JUNIT tests.  So call it 
		// only once at the start of main.
	}
	
	/**
	 * @param keyboard Scanner connected to System.in
	 * @return ArrayList of Strings containing start word and end word. 
	 * If command is /quit, return empty ArrayList. 
	 */
	public static ArrayList<String> parse(Scanner keyboard) {
		ArrayList<String> words = new ArrayList<String>();
		words.clear();					//clear any words that are in there already
		words.add(keyboard.next());
		if (words.get(0).equals("/quit")){
			words.clear();
			return words;
		}
		words.add(keyboard.next());
		
		return words;
	}
	
	public static ArrayList<String> getWordLadderDFS(String start, String end) {
		// Returned list should be ordered start to end.  Include start and end.
		// If ladder is empty, return list with just start and end.
		Set<String> dict = makeDictionary();
		ArrayList<String> seenAlready = new ArrayList<String>();
		ArrayList<String> ladder = new ArrayList<String>();
		if (start.equals("/quit") || end.equals("/quit")){		//checks for quit command
			return null;
		}
		if (inputWords.isEmpty() && !start.equals("/quit")){
			inputWords.add(start);
			inputWords.add(end);
		}
		seenAlready.add(start.toUpperCase());
		recursiveDFS(start, end, dict, seenAlready, ladder);		//go to the recursive helper function
		if (ladder.size() > 1) {
			ladder.add(start);
			Collections.reverse(ladder);
		}
		if (ladder.isEmpty()){
			ladder.add(start);
			ladder.add(end);
		}
		return ladder;
	}
	
	private static void recursiveDFS(String start, String end, Set<String> dict, ArrayList<String> seenAlready, ArrayList<String> ladder){
		if (!ladder.isEmpty()){
			ladder.add(start.toLowerCase());		//end word has been found, just go back and get the ladder
			return;
		}
		ArrayList<String> neighbors = getNeighbors(start, seenAlready, dict);
		while (!neighbors.isEmpty()){
			if (neighbors.contains(end.toUpperCase())){
				ladder.add(end.toLowerCase());
				return;
			}
			start = bestNeighborOption(neighbors, end);			//explore the best option, not every option
			neighbors.remove(neighbors.indexOf(start));
			recursiveDFS(start, end, dict, seenAlready, ladder);
			
			if (!ladder.isEmpty()){
				ladder.add(start.toLowerCase());
				return;
			}
			
		}
		
	}
	
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
    	Set<String> dict = makeDictionary(); // Makes a dictionary from an Input File
		ArrayList<String> seenAlready = new ArrayList<String>(); //Keeps track of the words that have already been seen
		Queue<Node> exploreQueue = new LinkedList<Node>(); //Keeps track of the words that need to be explored
		ArrayList<String> ladder = new ArrayList<String>(); //This will contain the ladder if it exists;
		if (start.equals("/quit") || end.equals("/quit")){
			return null;
		}
		if (inputWords.isEmpty() && !start.equals("/quit")){
			inputWords.add(start);
			inputWords.add(end);
		}
		
		Node current = new Node(start.toUpperCase()); //Initial Node creation
		current.prev = null; //Previous is null because this is the starting word
		Node endWord = null; //This keeps track of the word to see if it is found
		
		exploreQueue.add(current); //Adding the current word to the "Need-To-Explore" list
		seenAlready.add(start.toUpperCase());
		//this while loop executes all of the possible neighbors to try to find the word, if the word is found wordNode changes from null to the word
		while (!exploreQueue.isEmpty()) {
			current = exploreQueue.remove();
			ArrayList<String> neighbors = new ArrayList<String>();
			neighbors = getNeighbors(current.word, seenAlready, dict);
			for (int i = 0; i < neighbors.size(); i++) {
				Node node = new Node(neighbors.get(i));
				node.prev = current;						//remember the previous word so we can traverse back
				exploreQueue.add(node);
				if(node.word.equals(end.toUpperCase())) {
					while(!exploreQueue.isEmpty()) {
						exploreQueue.remove();
						}
					endWord = node;
				}
			}
		}
		//if the word is found, we will add the path to the ladder 
		if (endWord != null) { 
			ladder.add(endWord.word.toLowerCase());	
			while (endWord.prev != null) {
				endWord = endWord.prev;
				ladder.add(endWord.word.toLowerCase());
			}
			Collections.reverse(ladder);
		}
		if (ladder.isEmpty()){					//make sure you add the start and end word if there is no ladder
			ladder.add(start);
			ladder.add(end);
		}
		return ladder;
	}
	
	public static void printLadder(ArrayList<String> ladder) {
		if (ladder == null){
			return;
		}
		int count = ladder.size();
		if (ladder.size() < 3) {			//print everything lower case
			System.out.println("no word ladder can be found between " + inputWords.get(0).toLowerCase() + " and " + inputWords.get(1).toLowerCase());
			return;
			
		}else {								//print everything lower case
			System.out.println("a " + count + "-rung word ladder exists between " + inputWords.get(0).toLowerCase() + " and " + inputWords.get(1).toLowerCase() + ".");
			for (int i = 0; i < ladder.size(); i++) {
				System.out.println(ladder.get(i));	
			}
		}
		return;
	}
	
	
	private static ArrayList<String> getNeighbors(String current, ArrayList<String> seenAlready, Set<String> dict){
		char[] modCharArray = new char[current.length()];
		ArrayList<String> neighbors = new ArrayList<String>();
		for (int i = 0; i < current.length(); i++){
			modCharArray = current.toUpperCase().toCharArray().clone();
			for (char j = 'A'; j <= 'Z'; j++){
				if (current.charAt(i) == j){	//if the letter it is checking is the same as the start word
					j++;
				}
				if (j == 'Z'+1){				//if the increment makes it go after Z
					break;
				}
				modCharArray[i] = j;
				String modWord = new String(modCharArray);
				if (dict.contains(modWord) && !seenAlready.contains(modWord)){
					neighbors.add(modWord);
					seenAlready.add(modWord);
				}
			}
		}		
		return neighbors;
	}
	
	
	private static String bestNeighborOption(ArrayList<String> neighbors, String end){
		ArrayList<Integer> best = new ArrayList<Integer>();
		for (int i = 0; i < neighbors.size(); i++){		//iterating through the neighbors array list
			int counter = 0;
			for(int j = 0; j < end.length(); j++){
				if (neighbors.get(i).charAt(j) == end.toUpperCase().charAt(j)){
					counter++;			//keeps track of same letters in same place
				}
			}
			best.add(counter);			//index is the same as the index of the neighbor
		}
		return neighbors.get(best.indexOf(Collections.max(best)));
	
	}

	/* Do not modify makeDictionary */
	public static Set<String>  makeDictionary () {
		Set<String> words = new HashSet<String>();
		Scanner infile = null;
		try {
			infile = new Scanner (new File("five_letter_words.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("Dictionary File not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		while (infile.hasNext()) {
			words.add(infile.next().toUpperCase());
		}
		return words;
	}
}