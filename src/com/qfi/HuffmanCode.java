package com.qfi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Scanner;
/*
 * Programmer: Vincent Nigro
 * Date of Creation: 12/1/2017
 * Date of Last Edition: 12/3/2017
 * 
 * 
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
 * 
 */
public class HuffmanCode {

	public static void main(String args[]) throws IOException{

		Scanner scan = new Scanner(System.in);
		int result;
		String[] codes = null;

		System.out.println("Welcome to the Huffman Encoding Project!");
		System.out.print("Enter 1 to Compress, 2 to Decompress: ");
		result = scan.nextInt();
		
		//error checking
		while(result != 1 && result != 2){
			System.out.print("Invalid response please enter correct value: ");
			result = scan.nextInt();
		}

		if(result == 1)
		{
			//compression
			File file;
			System.out.print("Please enter file name/location to compress: ");
			file = new File(scan.next());
			System.out.println("Compressing...");
			int[] counts = null;
			try {
				counts = CharFreq(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HNTree T = getHTree(counts);
			codes = getCode(T.root);
			
			HNode r = T.root;
			
			//InOrderPrint(T);
			
			BufferedWriter bw = null;
			FileWriter fw = null;
			
			try{
				System.out.println("Creating Statistics File...");
				fw = new FileWriter("C:\\Users\\Vincent\\Documents\\statistics.txt");
				bw = new BufferedWriter(fw);
				StatFile(T, bw);
			}catch(IOException e){
				e.printStackTrace();
				
			}finally{
				try{
					
					if(bw != null)
						bw.close();
					if(fw != null)
						fw.close();
					
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
			
			try{
				System.out.println("Creating Compressed File...");
				fw = new FileWriter("C:\\Users\\Vincent\\Documents\\Compressed.txt");
				bw = new BufferedWriter(fw);
				HNode temp = T.root;
				CreateCompressedFile(temp, file, bw);
				
			}catch(IOException e){
				e.printStackTrace();
			}finally{
	
				try{
					if(bw != null)
						bw.close();
					if(fw != null)
						fw.close();
				}catch(IOException ex){
					ex.printStackTrace();
				}
			}
			
			System.out.println("Done!");
		}
		else
		{
			System.out.println("Decompressing...");
			File file1 = new File("C:\\Users\\Vincent\\Documents\\statistics.txt");
			File file2 = new File("C:\\Users\\Vincent\\Documents\\Compressed.txt");
			DecompressFile(file1, file2);
			System.out.println("Done!");
		}
		
		//end of main
	}
	
	/*
	 * Description: parseFile takes the statistics file that is found in Documents and searches for the 
	 * character binary code specified by searchStr and once found, prints the character to standard output
	 * Input: File (statistics file), String (binary code)
	 * Output: Prints characters to standard output
	 * Return: void (null)
	 */
	public static void parseFile(File file1, String searchStr) throws IOException{
		BufferedReader inputStream = new BufferedReader(new FileReader(file1));
		String s;
		Scanner scan = new Scanner(inputStream);
		while(inputStream.ready())
		{ 
			while(scan.hasNext()){
				scan.next();
				String l = scan.next();
				for(int i = 0; i < 3; i++)
					scan.next();
				String code = scan.next();
				if(code.equals(searchStr))
					System.out.print(l);
			}
		}
	//end of parseFile	
	}
	
	/*
	 * Description: Scans the compressed file for each binary code until the EOF. As each line is read,
	 * the string is passed to parseFile along with the statistics file.
	 * Input: File (statisics.txt), File (compressed.txt)
	 * Output: Check parseFile function
	 * Return: void (null)
	 * 
	 */
	public static void DecompressFile(File file1, File file2) throws IOException {
		
		String c;
		char l;
		
		Scanner scan = new Scanner(file2);
		
		while(scan.hasNext()){
			c = scan.next();
			parseFile(file1, c);
		}
		//end of DecompressFile
	}
	
	/*
	 * Description: This function creates a BufferedReader that reads from the uncompressed file. The function
	 * will read until EOF and read each character at a time passing each character to the Compress function. 
	 * Input: HNode (root of tree), File (uncompressed file), BufferedWriter (compressed file)
	 * Output: Check Compress function
	 * Return: void (null)
	 */
	public static void CreateCompressedFile(HNode root, File file, BufferedWriter bw) throws IOException {
		
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(file));
			String s;
			char c;
			while((s = inputStream.readLine()) != null){
				for(int i = 0; i < s.length(); i++){
					c = s.charAt(i);
					Compress(root, c, bw);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//end of CreateCompressedFile
	}
	
	/*
	 * Description: Compress is a recursive function that checks if a HNodes' character matches the character
	 * passed. If there is a match, the BufferedWriter prints the code that corresponds to the character along
	 * with a space into the Compressed file.  
	 * Input: HNode (every node will be searched), char (character to search for), BufferedWriter (Compressed file)
	 * Output: Writes into Compressed file 
	 * Return: void (null)
	 */
	private static void Compress(HNode root, char c, BufferedWriter bw) throws IOException{
		
		if (root == null)
			return;
		
			Compress(root.left, c, bw);
		if(root.ch == c)
			bw.write(root.code + " ");
			Compress(root.right, c, bw);
			//end of Compress
	}
	
	/*
	 * Description: Brings HNTree value down to HNode level and calls StatfilePrint
	 * Input: HNTree (root of tree), BufferedWriter (Statistics file)
	 * Output: Check StatFilePrint function
	 * Return: void (null)
	 */
	public static void StatFile(HNTree t, BufferedWriter bw) throws IOException {
		
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
	private static void StatFilePrint(HNode temp, BufferedWriter bw) throws IOException {
		
		if (temp == null)
			return;
			StatFilePrint(temp.left, bw);
		if(temp.ch != ' '){
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
	public static int[] CharFreq(File Fpath) throws IOException{
		int[] counts = new int[256];
		
		try {
			BufferedReader inputStream = new BufferedReader(new FileReader(Fpath));
			String s;
			while((s = inputStream.readLine()) != null){
				for(int i = 0; i < s.length(); i++)
					counts[(int)s.charAt(i)]++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return counts;
		//end of CharFreq
	}
	

	/*
	 * Description: Creates Huffman Tree of HNTrees (or HNodes that are linked). Using a Min Priority Queue,
	 * a node is created based on the array of counts that was made in CharFreq function. After building tree
	 * the function returns the root node. 
	 * Input: int[] (count array)
	 * Output: The root of the Huffman Tree
	 * Return: HNTree
	 */
	public static HNTree getHTree(int[] c){
		PriorityQueue<HNTree> q = new PriorityQueue<HNTree>(Collections.reverseOrder());

		for(int i = 0; i < c.length; i++){
			if(c[i] > 0)
				q.add(new HNTree(c[i], (char)i));
		}
		
		while(q.size() > 1){
			HNTree t1 = q.remove();
			HNTree t2 = q.remove();
			q.add(new HNTree(t1, t2));
		}
		return q.remove();
		//end of getHTree
	}
	
	/*
	 * Description: This function returns an array of Strings for codes that exist for all possible nodes,
	 * 0 for any nodes that DNE.
	 * Input: HNode (root of tree)
	 * Output: Array of binary codes
	 * Return: String[]
	 */
	public static String[] getCode(HNode r){
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
	private static void setCode(HNode r, String[] c){
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
	public static void InOrderPrint(HNTree n){
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
	private static void InOrder(HNode n){
		if (n == null)
			return;
	
			InOrder(n.left);
			
		if(n.ch != ' ')
			System.out.println("Node: " + n.ch + " Freq: " + n.freq + " Code: " + n.code);
		
			InOrder(n.right);
			//end of InOrder
	}
	
	//end of HuffmanCode class
}
