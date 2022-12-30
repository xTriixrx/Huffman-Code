package com.qfi.huffman;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * This program is a Huffman Encoding Implementation using HNodes and a HNTree class which joins
 * all HNodes together. The program prompts the user to choose whether to Compress or Decompress a file.
 * COMPRESSION:
 * The Compression algorithm finds the frequency of each character, creates the HNodes for each character
 * then builds the Huffman Tree composing of the HNTree class. To move to the HNode level, simply from a HNTree
 * object move to its root data value EX: HNode r = HNTree.root; After creating the tree, the Compression algorithm
 * then creates a statistics file and compressed file into the Documents folder.
 * DECOMPRESSION:
 * The Decompression algorithm takes both a statistics file and compressed file from the Documents folder. The 
 * algorithm first reads from the compressed file by each binary value that corresponds to a letter value. Then
 * it reads from the statistics file and finds the letter that corresponds to the binary number found in the 
 * compressed file by comparing the binary numbers of each letter in the statistics file. Once a match is found
 * the character is printed out to standard output.
 * NOTE: If you do not have statistics.txt and compressed.txt files to begin with in Documents, the program will
 * crash since the files do not exist.
 * 
 * @author Vincent.Nigro
 * @version 1.0.0
 */
public class HuffmanCode
{
	private static final String COMPRESS = "COMPRESS";
	private static final String DECOMPRESS = "DECOMPRESS";
	private static final String MODE = System.getProperty("mode");
	private static final Logger m_logger = LogManager.getLogger(HuffmanCode.class);

	public static void main(String[] args) throws IOException
	{
		Thread executorThread = null;
		HuffmanExecution executor = null;

		if (args.length != 2 || !isValidMode(MODE))
		{
			m_logger.error("Invalid arguments provided, expected exactly 2 arguments & a valid mode.");
			m_logger.error("The mode should be set as a system property: -Dmode=\"COMPRESS\" or -Dmode=\"DECOMPRESS\"");
			m_logger.error("For compression: ./huffman [inputFile] [outputFile]");
			m_logger.error("For decompression: ./huffman [compressedFile] [statisticsFile]");
			return;
		}

		executor = new HuffmanExecution(MODE, args[0], args[1]);
		executorThread = new Thread(executor);
		executorThread.start();
	}

	/**
	 * Checks and returns a flag representing if the provided mode is valid.
	 *
	 * @param mode - Some mode string that was provided by the "mode" system property.
	 * @return boolean - A flag representing if the provided mode is valid.
	 */
	private static boolean isValidMode(String mode)
	{
		return (mode.equalsIgnoreCase(COMPRESS) || mode.equalsIgnoreCase(DECOMPRESS));
	}
}
