package com.qfi.huffman;

public class HNode
{
	char ch;
	int freq;
	String code = "";
	HNode left = null;
	HNode right = null;
	HNode parent = null;

	public HNode()
	{
		this.freq = 0;
		this.ch = ' ';
	}

	public HNode(char letter, int freq)
	{
		this.ch = letter;
		this.freq = freq;
	}
}
