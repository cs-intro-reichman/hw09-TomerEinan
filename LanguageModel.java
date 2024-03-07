import java.util.HashMap;
import java.util.Random;


public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). 
     * @throws Exception */
    public void train(String fileName) { 
        String window = ""; 
        char c; 
        In in = new In(fileName); 
        // 
        for (int i = 0; i < windowLength; i++) { 
            char tempChar = in.readChar(); 
            window += tempChar; 
        } 
        while (!in.isEmpty()) { 
            c = in.readChar(); 
            List probs = CharDataMap.get(window); 
            if (probs == null) { 
                probs = new List(); 
                CharDataMap.put(window, probs); 
            } 
            probs.update(c); 
            window = (window + c).substring(1); 
        } 
        for (List probs : CharDataMap.values()) { 
            calculateProbabilities(probs); 
        } 
    } 
    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {
        if (probs == null || probs.getFirst() == null) {
            throw new IllegalArgumentException("The probability list is null or empty");
        }
        // Calculate the total number of characters
        int totalCounter = 0;
        Node current = probs.getFirstNode();
        while (current != null) {
            totalCounter += current.cp.count;
            current = current.next;
        }
    
        // Calculate and set the probabilities (p and cp fields) of all the characters in the list
        current = probs.getFirstNode();
        double cumulativeProbability = 0.0;
        while (current != null) {
            // Calculate probability (p field)
            current.cp.p = (double) current.cp.count / totalCounter;
    
            // Calculate cumulative probability (cp field)
            cumulativeProbability += current.cp.p;
            current.cp.cp = cumulativeProbability;
    
            current = current.next;
        }
    }
    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
        if (probs == null || probs.getFirst() == null) {
            throw new IllegalArgumentException("The probability list is null or empty");
        }
    
        double randomValue = randomGenerator.nextDouble();
        char result = 0;  // default value
    
        Node current = probs.getFirstNode();
        while (current != null) {
            if (randomValue < current.cp.cp) {
                result = current.cp.chr;
                break;
            }
            current = current.next;
        }
    
        if (result == 0) {
            throw new IllegalStateException("Unable to determine a random character");
        }
    
        return result;
    }
