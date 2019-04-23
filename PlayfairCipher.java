package playfaircipher;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author Sam Brighton                 Last Modified: September 20, 2016
 * @author Marietta E. Cameron          Last Modified: September 20, 2016
 * 
 * Purpose:
 * This code takes a file and secret code from the user and returns either an
 * encrypted or decrypted version of that file under a user specified name
 * specific to the code supplied by the user. 
 * To encrypt enter "E"
 *
 */
public class PlayfairCipher {

    static final String ALPHABET
            = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 '()*+,-./:;<=>[]^?";

    static Scanner inputFile;                                                   //construct files after file names are received
    static PrintWriter outputFile;
    static char command;                                                        //'E' for encode 'D' for decode
    static String secretPhrase;
                                                                               
    static char[][] codeTable = new char[9][9];                                 // codeTable a 2D 9x9 array of char
    static int[] rowIndex = new int[81];                                        // rowIndex a 1D array of int size 81
    static int[] colIndex = new int[81];                                        // colIndex a 1D array of int size 81

    static public void getUserInput() throws Exception {

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Do you wish to encode or decode: ");                // Ask user for command
        String comm = keyboard.nextLine();
        command = comm.charAt(0);

        System.out.println("Enter key phrase: ");                               // Ask user for secretPhrase
        secretPhrase = keyboard.nextLine();

        System.out.println("Enter input File: ");                               // Ask user for name of input file
        String inputFileName = keyboard.nextLine();
        String encodeLine = "";
        File inFile = new File(inputFileName);                                  // construct inputFile
        inputFile = new Scanner(inFile);

        System.out.println("Enter output File: ");                              // Ask user for name of output file
        String destFileName = keyboard.next();
        outputFile = new PrintWriter(new FileWriter(destFileName));             // construct outputFile

    }//getUserInput

    static public void buildCodeTable() {

        for (int p = 0; p < 81; p++) {                                          // initialize row and column indecies to -1
            rowIndex[p] = -1;
            colIndex[p] = -1;
        } 
        String currentLine = "";                                                // initialize current line to an empty string
        int row = 0;                                                            // initialize row to zero
        int col = 0;                                                            // initialize column to zero

        String phrase = secretPhrase + ALPHABET;                                // concatenate the secret phrase to the whole alphabet
        for (int q = 0; q < phrase.length(); q++) {                             // for each letter from position 0 to end of phrase
            boolean dup = false;                                                // flag for duplicate
            if (row < 9) {                                                      // for each row
                for (int i = 0; i < currentLine.length(); i++) {                // check for duplicate
                    if (currentLine.charAt(i) == phrase.charAt(q))              // if so
                    {
                        dup = true;                                             // flag
                    }
                }
                if (!dup) {
                    codeTable[row][col] = phrase.charAt(q);                     // begin filling code Table with the (q)th element
                    rowIndex[ALPHABET.indexOf(phrase.charAt(q))] = row;         // save the index of that element in the code table
                    colIndex[ALPHABET.indexOf(phrase.charAt(q))] = col;         // for reference generically
                    currentLine += phrase.charAt(q);                            // concatenate (q)th element to current line
                    col++;                                                      // increment column
                }
                if (col > 8) {                                                  // check to see if we're at the end of a row
                    row++;                                                      // if so increment row
                    col = 0;                                                    // reset column
                }
            }
        }
    }//buildCodeTable

    static public void encryptFile(Scanner plainTextFile, PrintWriter cipherFile) {

        while (plainTextFile.hasNextLine()) {                                   // while there is more to read in the file to encrypt

            String currLine = plainTextFile.nextLine();                         // pull next line
            String encoded = "";                                                // initialize current string empty
            int currRowX;                                                       // place
            int currRowY;                                                       //      holders
            int currColX;                                                       //              yay
            int currColY;                                                       //  more
            char x;                                                             //       place
            char y;                                                             //             holders

            if (currLine.length() % 2 == 1)                                     // if the line is odd
            {
                currLine += " ";                                                // add a space
            }
            for (int i = 0; i < currLine.length(); i += 2) {                    // for each pair of characters
                x = currLine.charAt(i);
                y = currLine.charAt(i + 1);                                     // initialize place holders
                currRowX = rowIndex[ALPHABET.indexOf(x)];
                currColX = colIndex[ALPHABET.indexOf(x)];
                currRowY = rowIndex[ALPHABET.indexOf(y)];
                currColY = colIndex[ALPHABET.indexOf(y)];

                if (x == y) {                                                   // case 1: the same character
                    if (currColX == 8) {                                        // check to see if it's at the end of the row
                        encoded += codeTable[currRowX][0];                      // if so assign to value
                        encoded += codeTable[currRowX][0];                      // at the beginning of the row
                    } else {
                        encoded += codeTable[currRowX][currColX + 1];           // if not output the character
                        encoded += codeTable[currRowX][currColX + 1];           // to the immediate right of the input
                    }
                } else if (currRowX == currRowY) {                              // case 2: the same row
                    if (currColX == 8) {
                        encoded += codeTable[currRowX][0];                      // check to see if it's at the end of the row if so assign to value at the beginning of the row
                    } else {
                        encoded += codeTable[currRowX][currColX + 1];           // if not concatenate the char to the right of the first input char
                    }
                    if (currColY == 8) {
                        encoded += codeTable[currRowY][0];                      // same check and same result
                    } else {
                        encoded += codeTable[currRowY][currColY + 1];           // same result for the second char
                    }
                } else if (currColX == currColY) {                              // case 3: same same column
                    if (currRowX == 8) {
                        encoded += codeTable[0][currColX];                      // check to see if it's at the end of the column 
                    } else {                                                    // if so assign to value at the beginning of the column
                        encoded += codeTable[currRowX + 1][currColX];           // if not concatenate the char to the right of the first input char
                    }
                    if (currRowY == 8) {
                        encoded += codeTable[0][currColX];                      // same check and same result
                    } else {
                        encoded += codeTable[currRowY + 1][currColY];           // same result for the second char
                    }
                } else {                                                        // case 4: opposite corners
                    encoded += codeTable[currRowX][currColY];                   // swap corner for first char
                    encoded += codeTable[currRowY][currColX];                   // swap corner for second char
                }
            }
            cipherFile.println(encoded);                                        // write to out file
        }
        cipherFile.close();                                                     // close print writer
    }//encryptFile

    static public void decryptFile(Scanner cipherFile, PrintWriter plainTextFile) {

        while (cipherFile.hasNextLine()) {                                      // while there is more to read in the file to encrypt
            String currLine = cipherFile.nextLine();                            // pull next line
            String encoded = "";
            int currRowX;
            int currRowY;
            int currColX;                                                       // place holders
            int currColY;
            char x;
            char y;

            if (currLine.length() % 2 == 1)                                     // check for odd length
            {
                currLine += " ";                                                // add space to make even
            }
            for (int i = 0; i < currLine.length(); i += 2) {
                x = currLine.charAt(i);
                y = currLine.charAt(i + 1);
                currRowX = rowIndex[ALPHABET.indexOf(x)];
                currColX = colIndex[ALPHABET.indexOf(x)];
                currRowY = rowIndex[ALPHABET.indexOf(y)];
                currColY = colIndex[ALPHABET.indexOf(y)];

                if (x == y) {                                                   // case 1
                    if (currColX == 0) {                                        // check for end of row
                        encoded += codeTable[currRowX][8];
                        encoded += codeTable[currRowX][8];
                    } 
                    else {
                        encoded += codeTable[currRowX][currColX - 1];           // add to current line
                        encoded += codeTable[currRowX][currColX - 1];
                    }
                } else if (currRowX == currRowY) {                              // case 2
                    if (currColX == 0) {
                        encoded += codeTable[currRowX][8];                      // check for the end of the row
                    } else {
                        encoded += codeTable[currRowX][currColX - 1];           // add to current line
                    }
                    if (currColY == 0) {
                        encoded += codeTable[currRowY][8];
                    } else {
                        encoded += codeTable[currRowY][currColY - 1];
                    }
                } else if (currColX == currColY) {                              // case 3
                    if (currRowX == 0) {
                        encoded += codeTable[8][currColX];                      // check for end of column
                    } else {
                        encoded += codeTable[currRowX - 1][currColX];           // add to current line
                    }
                    if (currRowY == 0) {
                        encoded += codeTable[8][currColX];
                    } else {
                        encoded += codeTable[currRowY - 1][currColY];
                    }
                } else {                                                        // case 4
                    encoded += codeTable[currRowX][currColY];                   // sawp corners
                    encoded += codeTable[currRowY][currColX];
                }
            }
            plainTextFile.println(encoded);                                     // write to decrypt file
        }
        plainTextFile.close();                                                  // close print writer
    }//decryptFile

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        getUserInput();                                                         // GetUserInput (Encode or Decode, secretPhrase, Inputfile, OutputFile) and Setup Files
        buildCodeTable();                                                       // Literally build the code table from the user entered code
        if (command == 'E') {                                                   // if command is Encode
            encryptFile(inputFile, outputFile);
        } else {
            decryptFile(inputFile, outputFile);
        }
    }//main
}//PlayFairCipher
