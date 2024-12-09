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
    public static int EOF = 128;

    //while index < text.length:
    //	prefix = longest coded word that matches text @ index
    //	write out that code
    //	if possible, look ahead to the next character
    //	append that character to prefix
    //	associate prefix with the next code (if available)
    //	index += prefix.length
    //write out EOF and close

    private static void compress() {
        // Create TST
        TST trie = new TST();

        // Fill TST with existing alphabet
        for (int i = 0; i < 128; i++){
            trie.insert(String.valueOf(i), i);
        }

        // Read data into a String
        String text = BinaryStdIn.readString();

        int index = 0;
        int code = 129;

        while (index < text.length()){
            // Get the prefix
            String prefix = trie.getLongestPrefix(text);

            // Add to the output file
            BinaryStdOut.write(prefix, 12);

            // If we can look at the next character, add it to the prefix
            if ((prefix.length()) < text.length()){
                trie.insert(text.substring(0, prefix.length() + 1), code);
                code++;
            }

            // Move the text forward to start where the previous prefix ended
            text = text.substring(prefix.length());
        }

        // Write out
        BinaryStdOut.write(EOF, 12);
        BinaryStdOut.close();
    }

    private static void expand() {

        // TODO: Complete the expand() method

        StringBuilder input = new StringBuilder();

        // Read input data
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            input.append(c);
        }

        StringBuilder expanded = new StringBuilder();

        for(int i = 0; i < input.length(); i++) {
            int count = Integer.parseInt(String.valueOf(input.charAt(i)));
            char c = input.charAt(i);
            for (int j = 0; j < count; j++){
                expanded.append(c);
            }
        }

        for (int i = 0; i < expanded.length(); i++){
            BinaryStdOut.write(expanded.charAt(i));
        }

        // read in the header and save to dictionary as an actual dictionary

        // read in the rest of the data 8 bits at a time
        // if the 8 bits match something in the dictionary, replace it and write it out
        // else write it out as is


        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
