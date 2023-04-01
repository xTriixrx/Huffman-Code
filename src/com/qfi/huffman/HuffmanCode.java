package com.qfi.huffman;

import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 * This program is a Huffman Encoding Implementation using the HuffmanNode object. The program prompts the user to
 * choose whether to compress or decompress a file.
 *
 * COMPRESSION:
 * The compression algorithm finds the frequency of each character, creates the HuffmanNodes for each character
 * then builds a tree composing of the HuffmanNode class created. After creating the tree, the compression algorithm
 * then creates a statistics file of the same path and name of the input file but as a hidden file and the uncompressed
 * input file will be compressed within the same path that was given to the application.
 *
 * DECOMPRESSION:
 * The decompression algorithm ingests the previously compressed file path and assumes the statistics file to be in the
 * same path as where the compress file path is. The algorithm first reads from the statistics file and generates a map
 * associated with each character and the string based binary encoding that was used for that character. Once the map is
 * created, the statistics file will be deleted from the file system, the compressed file will be read completely and each
 * bit will be iterated over in order to determine the appropriate characters that need to be rewritten back to the file.
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

	public static void main(String[] args)
	{
		if (args.length != 1 || !isValidMode(MODE))
		{
			m_logger.error("Invalid arguments provided, expected exactly 1 argument & a valid mode.");
			m_logger.error("The mode should be set as a system property: -Dmode=\"COMPRESS\" or -Dmode=\"DECOMPRESS\"");
			m_logger.error("The compressed file will be the same name as the initial input file but hidden: input.txt -> .input.txt");
			m_logger.error("For compression: ./huffman [inputFile]");
			m_logger.error("For decompression: ./huffman [compressedFile]");
			return;
		}

		if (!isValidFile(args[0]))
		{
			m_logger.error("The file at path " + args[0] + " does not exist.");
			return;
		}

		HuffmanExecution executor = new HuffmanExecution(MODE, args[0]);
		Thread executorThread = new Thread(executor);
		executorThread.start();
	}

	/**
	 * Checks and returns a flag representing if the provided file path is valid.
	 *
	 * @param filePath - A String representing some path to a file to be compressed or decompressed.
	 * @return boolean - A flag representing if the provided path is a valid file.
	 */
	private static boolean isValidFile(String filePath)
	{
		File f = new File(filePath);
		return f.exists();
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
