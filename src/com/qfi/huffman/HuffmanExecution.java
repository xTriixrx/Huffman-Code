package com.qfi.huffman;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

/**
 *
 * @author Vincent.Nigro
 * @version 1.0.0
 */
public class HuffmanExecution implements Runnable
{
	private String m_mode = "";
	private String m_inputPath = "";
	private static final String COMPRESS = "COMPRESS";
	private static final String STATISTICS_APPEND = ".";
	private static final String DECOMPRESS = "DECOMPRESS";
	private static final Logger m_logger = LogManager.getLogger(HuffmanExecution.class);

	public HuffmanExecution(String mode, String firstPath)
	{
		m_mode = mode;

		if (m_mode.equalsIgnoreCase(COMPRESS))
		{
			m_inputPath = firstPath;
			m_logger.debug("Input file path: " + m_inputPath);
		}
		else
		{
			m_inputPath = firstPath;
			m_logger.debug("Compressed file path: " + m_inputPath);
		}
	}

	@Override
	public void run()
	{
		if (m_mode.equalsIgnoreCase(COMPRESS))
		{
			String[] codes = null;

			//compression
			File inputFile = new File(m_inputPath);
			m_logger.info("Compressing...");

			int[] counts = new int[0];

			try {
				counts = getCharFrequencies(inputFile);
			} catch (IOException e) {

				e.printStackTrace();
			}

			HNTree T = getHTree(counts);
			codes = getCode(T.root);

			HNode r = T.root;

			//InOrderPrint(T);

			BufferedWriter bw = null;

			try{
				m_logger.info("Creating Statistics File...");
				String statPath = "";
				int lastSepIndex = m_inputPath.lastIndexOf(File.separator);

				if (lastSepIndex != -1)
				{
					statPath = m_inputPath.substring(0, lastSepIndex) + STATISTICS_APPEND + m_inputPath.substring(lastSepIndex, m_inputPath.length() - 1);
				}
				else
				{
					statPath = STATISTICS_APPEND + m_inputPath;
				}

				m_logger.debug("Statistics file path: " + statPath);

				bw = new BufferedWriter(new FileWriter(statPath));
				StatFile(T, bw);
			}catch(IOException e){
				e.printStackTrace();

			}finally{
				try{

					if(bw != null)
						bw.close();

				}catch(IOException ex){
					ex.printStackTrace();
				}
			}

			try{
				m_logger.info("Creating Compressed File...");

				char character;
				HNode temp = T.root;
				StringBuilder sb = new StringBuilder();

				try (BufferedReader inputStream = new BufferedReader(new FileReader(inputFile)))
				{
					while ((character = (char) inputStream.read()) != (char) -1)
					{
						HNode n = inOrderSearch(temp, character);
						if (n != null)
						{
							sb.append(n.code);
						}
					}

					HNode end = inOrderSearch(temp, (char) 0);

					if (end != null)
					{
						sb.append(end.code);
					}
				}
				catch (Exception e)
				{
					m_logger.error(e, e);
				}

				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(inputFile));
				createCompressedFile(sb, bos);
				bos.close();

			}catch(IOException e){
				e.printStackTrace();
			}finally{

				try{
					if(bw != null)
						bw.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}


		}
		else
		{
			m_logger.info("Decompressing...");
			File compressedFile = new File(m_inputPath);

			String statPath = "";
			int lastSepIndex = m_inputPath.lastIndexOf(File.separator);

			if (lastSepIndex != -1)
			{
				statPath = m_inputPath.substring(0, lastSepIndex) + STATISTICS_APPEND + m_inputPath.substring(lastSepIndex, m_inputPath.length() - 1);
			}
			else
			{
				statPath = STATISTICS_APPEND + m_inputPath;
			}

			m_logger.debug("Statistics file path: " + statPath);

			File statFile = new File(statPath);

			byte[] fileContent = null;

			Map<String, Character> freqMap = readStatFrequency(statFile);
			statFile.delete();
			
			try
			{
				fileContent = Files.readAllBytes(compressedFile.toPath());
			}
			catch (Exception e)
			{
				m_logger.error(e, e);
			}

			try
			{
				DecompressFile(freqMap, m_inputPath, fileContent);
			}
			catch (Exception e)
			{
				m_logger.error(e, e);
			}
		}

		m_logger.info("Done!");
	}

	/*
	 * Description: Scans the compressed file for each binary code until the EOF. As each line is read,
	 * the string is passed to parseFile along with the statistics file.
	 * Input: File (statisics.txt), File (compressed.txt)
	 * Output: Check parseFile function
	 * Return: void (null)
	 *
	 */
	public void DecompressFile(Map<String, Character> freqMap, String filePath, byte[] compressedContent) throws IOException
	{
		PrintWriter decompressedFile = new PrintWriter(new FileWriter(filePath));

		IterableBitArray itrArr = new IterableBitArray(compressedContent);

		String code = "";
		for (boolean b : itrArr)
		{
			code += b ? "1" : "0";

			if (freqMap.containsKey(code))
			{
				char c = freqMap.get(code);

				if (c == (char) 0)
				{
					return;
				}
				decompressedFile.write(c);
				decompressedFile.flush();

				System.out.print(c);

				code = "";
			}
		}

		decompressedFile.flush();
		decompressedFile.close();
	}

	/**
	 *
	 * @param statFile -
	 * @return {@code Map<String, Character>}
	 */
	private Map<String, Character> readStatFrequency(File statFile)
	{
		Map<String, Character> freqMap = new HashMap<>();

		try
		{
			Scanner fin = new Scanner(statFile);

			while (fin.hasNextLine())
			{
				String line = fin.nextLine();
				String[] parts = line.split("\\s+");
				freqMap.put(parts[5], parts[1].charAt(0));
			}

			fin.close();
		}
		catch (Exception e)
		{
			m_logger.error(e, e);
		}

		return freqMap;
	}

	/*
	 * Description: This function creates a BufferedReader that reads from the uncompressed file. The function
	 * will read until EOF and read each character at a time passing each character to the Compress function.
	 * Input: HNode (root of tree), File (uncompressed file), BufferedWriter (compressed file)
	 * Output: Check Compress function
	 * Return: void (null)
	 */
	public void createCompressedFile(StringBuilder sb, BufferedOutputStream bos) throws IOException
	{
		ByteBuffer b = ByteBuffer.allocate((sb.length() / 8) + 1);
		b.clear();

		byte data = 0;
		int bitIndex = 8;

		for (char bit : sb.toString().toCharArray())
		{
			System.out.println("Char: " + bit);

			if (bit == '1')
			{
				data |= (1 << (bitIndex - 1));
			}
			bitIndex--;

//				String s1 = String.format("%8s", Integer.toBinaryString(data & 0xFF)).replace(' ', '0');
//				System.out.println(s1);

			if (bitIndex == 0)
			{
//				System.out.println("CLEARING!");
				byte d = data;
				bitIndex = 8;
				b.put(d);
				data = 0;
			}
		}

		if (data != 0)
		{
			b.put(data);
		}

		for (byte byteData : b.array())
		{
			String s1 = String.format("%8s", Integer.toBinaryString(byteData & 0xFF)).replace(' ', '0');
			System.out.println(s1);
//				System.out.println(Integer.toHexString(byteData));
		}

		System.out.println(sb);
		bos.write(b.array());
		bos.flush();

	}

	/*
	 * Description: Compress is a recursive function that checks if a HNodes' character matches the character
	 * passed. If there is a match, the BufferedWriter prints the code that corresponds to the character along
	 * with a space into the Compressed file.
	 * Input: HNode (every node will be searched), char (character to search for), BufferedWriter (Compressed file)
	 * Output: Writes into Compressed file
	 * Return: void (null)
	 */
	private String compress(HNode root, char c, BufferedWriter bw) throws IOException{

		if (root == null)
			return "";

		compress(root.left, c, bw);
		if(root.ch == c)
			return root.code;
		compress(root.right, c, bw);
		//end of Compress

		return "";
	}

	/*
	 * Description: Brings HNTree value down to HNode level and calls StatfilePrint
	 * Input: HNTree (root of tree), BufferedWriter (Statistics file)
	 * Output: Check StatFilePrint function
	 * Return: void (null)
	 */
	public void StatFile(HNTree t, BufferedWriter bw) throws IOException {

		HNode temp = t.root;
		StatFilePrint(temp, bw);
		//end of StatFile
	}

	/*
	 * Description: Recursive function that goes through every node, searching for any nodes in the tree
	 * that have character values assigned to them (non-intermediate nodes). Once this node is found, the
	 * BufferedWriter prints the HNode information into the Statistics file.
	 * Input: HNode (every node in tree), BufferedWriter (Statistics file)
	 * Output: Prints all character node information into file
	 * Return: void (null)
	 */
	private void StatFilePrint(HNode temp, BufferedWriter bw) throws IOException {

		if (temp == null)
			return;
		StatFilePrint(temp.left, bw);
		if (temp.ch != (char) -1)
		{
			bw.write("Node: " + temp.ch + " Freq: " + temp.freq + " Code: " + temp.code);
			bw.newLine();
		}
		StatFilePrint(temp.right, bw);
		//end of StatFilePrint
	}

	/*
	 * Description: This function reads from the uncompressed file until EOF. For each line retrieved from
	 * file, the function reads each character of the line and increments an array for the integer representation
	 * of the character EX: aaa -> counts[97] = 3;
	 * Input: File (uncompressed file)
	 * Output: array of counts for each character in file
	 * Return: int[]
	 */
	public int[] getCharFrequencies(File uncompressedPath) throws IOException
	{
		int[] charCounts = new int[256];

		try (BufferedReader inputStream = new BufferedReader(new FileReader(uncompressedPath)))
		{
			String uncompressedLine;

			while ((uncompressedLine = inputStream.readLine()) != null)
			{
				for (int i = 0; i < uncompressedLine.length(); i++)
				{
					char uncompressedChar = uncompressedLine.charAt(i);
					charCounts[uncompressedChar]++;
					m_logger.trace("Character '" + uncompressedChar + "' now has count: "
						+ charCounts[uncompressedChar]);
				}
			}
		} catch (Exception e)
		{
			m_logger.error(e, e);
		}

		return charCounts;
	}

	/*
	 * Description: Creates Huffman Tree of HNTrees (or HNodes that are linked). Using a Min Priority Queue,
	 * a node is created based on the array of counts that was made in CharFreq function. After building tree
	 * the function returns the root node.
	 * Input: int[] (count array)
	 * Output: The root of the Huffman Tree
	 * Return: HNTree
	 */
	public HNTree getHTree(int[] charFrequencies)
	{
		PriorityQueue<HNTree> q = new PriorityQueue<>(Collections.reverseOrder());

		for (int i = 0; i < charFrequencies.length; i++)
		{
			if (charFrequencies[i] > 0)
			{
				char character = (char) i;
				int frequency = charFrequencies[i];

				m_logger.trace("Adding '" + character + "' with frequency of " + frequency + " to priority queue.");
				q.add(new HNTree(frequency, character));
			}
		}

		// Add a NUL character into list to send to represent end of compressed file
		q.add(new HNTree(1, (char) 0));

		while (q.size() > 1)
		{
			HNTree t1 = q.remove();
			HNTree t2 = q.remove();
			q.add(new HNTree(t1, t2));
		}

		return q.remove();
	}

	/*
	 * Description: This function returns an array of Strings for codes that exist for all possible nodes,
	 * 0 for any nodes that DNE.
	 * Input: HNode (root of tree)
	 * Output: Array of binary codes
	 * Return: String[]
	 */
	public String[] getCode(HNode r){
		if(r == null)
			return null;
		String[] c = new String[256];
		setCode(r, c);
		return c;
		//end of getCode
	}

	/*
	 * Description: Recursive function that checks every node in tree and sets codes for each nodes based
	 * on its frequency (Check Huffman Tree properties)
	 * Input: HNode (checks all nodes), String[] (codes array)
	 * Output: HNodes will have codes data field filled
	 * Return: void (null)
	 */
	private void setCode(HNode r, String[] c){
		if(r.left != null){
			r.left.code = r.code + "0";
			setCode(r.left, c);
		}
		if(r.right != null){
			r.right.code = r.code + "1";
			setCode(r.right, c);
		}
		//if leaf node
		if(r.right == null && r.left == null)
			c[(int)r.ch] = r.code;

		//end of setCode
	}

	/*
	 * Description: Lowers level of HNTree to HNode
	 * Input: HNTree (root)
	 * Output: Check InOrder function
	 * Return: void (null)
	 */
	public void InOrderPrint(HNTree n){
		HNTree tmp = n;
		InOrder(tmp.root);
		//end of InOrderPrint
	}

	/*
	 * Description: Recursive function to test printing of character nodes
	 * Input: HNode (checks all nodes)
	 * Output: Prints character node values to standard output
	 * Return: void (null)
	 */
	private void InOrder(HNode n){
		if (n == null)
			return;

		InOrder(n.left);

		//if(n.ch != ' ')
		m_logger.debug("Node: " + n.ch + " Freq: " + n.freq + " Code: " + n.code);

		InOrder(n.right);
		//end of InOrder
	}

	private HNode inOrderSearch(HNode n, char c)
	{
		if (n == null)
		{
			return null;
		}

		if (n.ch == c)
		{
			return n;
		}

		HNode tmp = inOrderSearch(n.left, c);

		if (tmp == null)
		{
			tmp = inOrderSearch(n.right, c);
		}

		return tmp;
	}
}
