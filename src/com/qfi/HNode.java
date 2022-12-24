package com.qfi;

public class HNode{
	
	int freq;
	char ch;
	HNode left, right, parent;
	String code = "";
	
	public HNode()
	{
		this.freq = 0;
		this.ch = ' ';
	}
	
	public HNode(char letter, int freq){
		this.ch = letter;
		this.freq = freq;
	
	}
	
	
	

}
