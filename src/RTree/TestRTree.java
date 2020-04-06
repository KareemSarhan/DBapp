package RTree;


import java.util.Scanner;
import java.util.Vector;

public class TestRTree {
	@SuppressWarnings("unused")
	private static RTRef r;
	private static  int c=0;
	public static void main(String[] args) 
	{	
		RTree<Integer> tree = new RTree<Integer>(2);
	
		
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
