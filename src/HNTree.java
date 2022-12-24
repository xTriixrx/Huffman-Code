
public class HNTree implements Comparable<HNTree> {
	
	HNode root;
	
	public HNTree(HNTree t1, HNTree t2){
		root = new HNode();
		root.left = t1.root;
		root.right = t2.root;
		t1.root.parent = root;
		t2.root.parent = root;
		root.ch = ' ';
		root.freq = t1.root.freq + t2.root.freq;
	}
	
	public HNTree(int freq, char c){
		root = new HNode(c, freq);
	}
	
	public int compareTo(HNTree t){
		if(root.freq < t.root.freq)
			return 1;
		else if(root.freq == t.root.freq)
			return 0;
		else
			return -1;
	}

}
