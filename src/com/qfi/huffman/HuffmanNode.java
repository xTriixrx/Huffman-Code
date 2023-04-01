package com.qfi.huffman;

/**
 * The HuffmanNode class is a Huffman node. The HuffmanNode class contains a character for which
 * the node is representing, the frequency of that character shows up in some ASCII text file, a String based code which
 * is a prefix representation of that character in binary. This string will be used to generate the associated bits with the
 * string based prefix once the compression begins. A joining constructor is provided in order to group the different
 * frequency nodes together during the building of the entire frequency tree.
 *
 * @author Vincent.Nigro
 * @version 1.0.0
 */
public class HuffmanNode implements Comparable<HuffmanNode>
{
	private char m_ch;
	private int m_freq;
	private String m_code = "";
	private HuffmanNode m_left = null;
	private HuffmanNode m_right = null;
	private HuffmanNode m_parent = null;

	/**
	 * HNode setting constructor.
	 *
	 * @param freq - The number of times this character is in the decompressed file.
	 * @param letter - The character associated with this newly instantiated HNode.
	 */
	public HuffmanNode(int freq, char letter)
	{
		m_freq = freq;
		m_ch = letter;
	}

	/**
	 * HuffmanNode join constructor.
	 *
	 * @param h1 - A HuffmanNode object
	 * @param h2 - A HuffmanNode object
	 */
	public HuffmanNode(HuffmanNode h1, HuffmanNode h2)
	{
		m_left = h1;
		m_right = h2;
		m_parent = this;
		m_ch = (char) -1;
		m_freq = h1.getFrequency() + h2.getFrequency();
	}

	/**
	 * The compareTo method is an overridden method for the Comparable interface.
	 *
	 * @param t - A HuffmanNode to compare to the current node based on its frequency.
	 * @return int
	 */
	public int compareTo(HuffmanNode t)
	{
		return Integer.compare(t.getFrequency(), m_freq);
	}

	/**
	 * The equals method is an overridden method for the Comparable interface.
	 *
	 * @param obj - An object to check if it is equal to the current instance.
	 * @return boolean
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof HuffmanNode)
		{
			return obj.hashCode() == this.hashCode();
		}

		return false;
	}

	/**
	 * The hashCode method is an overridden method for the Comparable interface;
	 *
	 * @return int
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

	/**
	 * Accessor for the frequency property.
	 *
	 * @return int
	 */
	public int getFrequency()
	{
		return m_freq;
	}

	/**
	 * Accessor for the character property.
	 *
	 * @return char
	 */
	public char getCharacter()
	{
		return m_ch;
	}

	/**
	 * Accessor for the code property.
	 *
	 * @return String
	 */
	public String getCode()
	{
		return m_code;
	}

	/**
	 * Accessor for the left HuffmanNode property.
	 *
	 * @return HuffmanNode
	 */
	public HuffmanNode getLeft()
	{
		return m_left;
	}

	/**
	 * Accessor for the right HuffmanNode property.
	 *
	 * @return HuffmanNode
	 */
	public HuffmanNode getRight()
	{
		return m_right;
	}

	/**
	 * Accessor for the parent HuffmanNode property.
	 *
	 * @return HuffmanNode
	 */
	public HuffmanNode getParent()
	{
		return m_parent;
	}

	/**
	 * Mutator for the code property.
	 *
	 * @param code - The string based binary code to be set for this HuffmanNode object.
	 */
	public void setCode(String code)
	{
		m_code = code;
	}
}