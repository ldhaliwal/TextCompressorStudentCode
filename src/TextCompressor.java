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

    private static void compress() {
        // TODO: Complete the compress() method

        StringBuilder input = new StringBuilder();

        // Read input data
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            input.append(c);
        }

        StringBuilder compressed = new StringBuilder();
        int count = 1;


        // Go through the text and compress when you find runs of the same letter
        for (int i = 1; i < input.length(); i++) {
            if (count >= 255){
                compressed.append(count);
                compressed.append(input.charAt(i - 1));
                count = 1;
            }
            else if (input.charAt(i) == input.charAt(i - 1)) {
                count++;
            }
            else {
                compressed.append(count);
                compressed.append(input.charAt(i - 1));
                count = 1;
            }
        }

        // add the last character & its count
        compressed.append(count);
        compressed.append(input.charAt(input.length() - 1));

        // loop through the text with a window of set a size (test later to see what works best)
            // check each window possibility, shifting by one and save to a dictionary if its not already ther
            // if it is there, increment count of that sequence

        // order the dictionary by most occurrences
        // take the 204(?) most common and give them keys

        // write out length of dictionary
        // write out dictionary as the header

        // loop through the text again
            // if the window matches one of the most common, write out with its key
            // else loop through the three and add each one to output

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
