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
    public static int EOF = 256;
    public static int MAX_CODES = 4096;

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
        for (int i = 0; i < EOF; i++){
            trie.insert(String.valueOf(i), i);
        }

        // Read data into a String
        String text = BinaryStdIn.readString();

        // Set the first possible code
        int code = EOF + 1;

        while (!text.isEmpty()){
            // Get the prefix
            String prefix = trie.getLongestPrefix(text);

            // Add to the output file
            BinaryStdOut.write(prefix, 12);

            // If we can look at the next character, add it to the prefix
            if ((prefix.length() + 1) < text.length() && code < MAX_CODES){
                trie.insert(text.substring(0, prefix.length() + 1), code);
                code++;
            }

            // Move the text forward to start where the previous prefix ended
            text = text.substring(prefix.length());

        }

        // Write out EOF indicator
        BinaryStdOut.write(EOF, 12);
        BinaryStdOut.close();
    }

    private static void expand() {
        String[] codes = new String[MAX_CODES];
        int index = EOF + 1;

        // Fill TST with ascii values
        for (int i = 0; i < EOF; i++){
            codes[i] = String.valueOf(i);
        }

        // Read in the first code
        int currentCode = BinaryStdIn.readInt(12);

        // Get the text value of the first code
        String textValue = codes[currentCode];

        while (currentCode != EOF) {
            // Write out the current code
            BinaryStdOut.write(textValue);

            // Read the lookahead code
            currentCode = BinaryStdIn.readInt(12);

            // Get the string value of the lookahead code
            textValue = codes[currentCode];

            // Edge case: If the lookahead code is not yet defined, make a new code using the current code
            if (index == currentCode){
                textValue = textValue + textValue.charAt(0);
            }

            // As long as we haven't filled all the codes, create another one and write it out
            if (index < MAX_CODES){
                index++;
                codes[index] = textValue + textValue.charAt(0); // Add new entry to code table.
            }

            //textValue = nextCode; // Update current codeword.
        }
        // Write out the EOF value
        BinaryStdOut.write(textValue);

        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
