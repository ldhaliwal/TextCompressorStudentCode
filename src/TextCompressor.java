/******************************************************************************
 *  Compilation:  javac TextCompressor.java
 *  Execution:    java TextCompressor - < input.txt   (compress)
 *  Execution:    java TextCompressor + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   abra.txt
 *                jabberwocky.txt
 *                shakespeare.txt
 *                virus.txt
 *
 *  % java DumpBinary 0 < abra.txt
 *  136 bits
 *
 *  % java TextCompressor - < abra.txt | java DumpBinary 0
 *  104 bits    (when using 8-bit codes)
 *
 *  % java DumpBinary 0 < alice.txt
 *  1104064 bits
 *  % java TextCompressor - < alice.txt | java DumpBinary 0
 *  480760 bits
 *  = 43.54% compression ratio!
 ******************************************************************************/

/**
 *  The {@code TextCompressor} class provides static methods for compressing
 *  and expanding natural language through textfile input.
 *
 *  @author Zach Blick, Liliana Dhaliwal
 */
public class TextCompressor {
    public static final int NUM_BITS = 12;
    public static final int EOF = 256;
    public static final int MAX_CODES = 4096;

    private static void compress() {
        // Create TST
        TST trie = new TST();

        // Fill TST with existing alphabet
        for (int i = 0; i < EOF; i++){
            trie.insert(("" + (char)i), i);
        }

        // Read data into a String
        String text = BinaryStdIn.readString();

        // Set the first possible code
        int code = EOF + 1;

        int i = 0;

        while (i < text.length()){
            // Get the prefix and its code representation
            String prefix = trie.getLongestPrefix(text, i);
            int prefixCode = trie.lookup(prefix);

            // Add to the output file
            BinaryStdOut.write(prefixCode, NUM_BITS);

            // If we can look at the next character, add it to the prefix
            if (i + prefix.length() < text.length() && code < MAX_CODES){
                trie.insert(prefix + text.charAt(i + prefix.length()), code);
                code++;
            }

            // Moves index forward
            i += prefix.length();
        }

        // Write out EOF indicator
        BinaryStdOut.write(EOF, NUM_BITS);
        BinaryStdOut.close();
    }

    private static void expand() {
        // Create a map for easy code/value lookup
        String[] codes = new String[MAX_CODES];
        int nextCode = EOF + 1;

        // Fill map with ascii values
        for (int i = 0; i < EOF; i++){
            codes[i] = "" + (char) i;
        }

        int lookaheadCode;
        String nextTextValue;

        // Read in the first code
        int currentCode = BinaryStdIn.readInt(12);

        // Get the text value of the first code
        String textValue = codes[currentCode];

        while (true) {
            // Write out the current code
            BinaryStdOut.write(textValue);

            // Read the lookahead code
            lookaheadCode = BinaryStdIn.readInt(12);

            // Checks if we are at the end of the file
            if (lookaheadCode == EOF){
                break;
            }

            // Checks for the edge case: If the lookahead code is not yet defined, make a new code using the current code's value
            if (nextCode == lookaheadCode){
                codes[nextCode] = textValue + textValue.charAt(0);
            }

            // Get the string value of the lookahead code
            nextTextValue = codes[lookaheadCode];

            // As long as we haven't filled all the codes, create another one and write it out
            if (nextCode < MAX_CODES && nextCode != EOF){
                codes[nextCode] = textValue + nextTextValue.charAt(0);
                nextCode++;
            }

            // Updates the values of the current code and text value;
            currentCode = lookaheadCode;
            textValue = nextTextValue;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
