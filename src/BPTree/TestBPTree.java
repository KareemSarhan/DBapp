package BPTree;


import java.util.Scanner;
import java.util.Vector;

public class TestBPTree {
	@SuppressWarnings("unused")
	private static Ref r;
	private static  int c=0;
	public static void main(String[] args) 
	{	
		BPTree<Integer> tree = new BPTree<Integer>(2);
	
		tree.insert(3, new Ref(1,2));
		tree.insert(3, new Ref(1,4));
		tree.insert(3, new Ref(6,6));
		tree.insert(3, new Ref(64,4));
		tree.insert(3, new Ref(23,9));
		tree.insert(3, new Ref(0,9));
		tree.insert(3, new Ref(33,15));
	/*	Vector<int[]> v=tree.searchAll(3);
		
		for (int i = 0; i <v.size(); i++) {
			int[] c=new int[2];
			c=v.get(i);
			System.out.println(c[0]+" "+c[1]);
		}
		*/
		/*while(true) 
		{
			int x = sc.nextInt();
			if(x == -1)
				break;
			r=new Ref(0,c++);
			tree.insert(x, r);
			System.out.println(tree.toString());
		}
		while(true) 
		{
			int x = sc.nextInt();
			if(x == -1)
				break;
			tree.delete(x);
			System.out.println(tree.toString());
		}
		while(true) {
			int x=sc.nextInt();
			if(x==-1)
				break;
			System.out.println("Page: "+tree.search(x).getPage()+" Index: "+tree.search(x).getIndexInPage());
		}
		
		*/
	
		
		
		
		
	}	
}
