package RTree;



import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;





public class RTree<T extends Comparable<T>> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int order;
	private RTreeNode<T> root;
	
	/**
	 * Creates an empty B+ tree
	 * @param order the maximum number of keys in the nodes of the tree
	 */
	public RTree(int order) 
	{
		this.order = order;
		root = new RTreeLeafNode<T>(this.order);
		root.setRoot(true);
	}
	
	/**
	 * Inserts the specified key associated with the given record in the B+ tree
	 * @param key the key to be inserted
	 * @param recordReference the reference of the record associated with the key
	 */
	public void insert(T key, RTRef recordReference)
	{
		PushUp<T> pushUp = root.insert(key, recordReference, null, -1);
		if(pushUp != null)
		{
			RTreeInnerNode<T> newRoot = new RTreeInnerNode<T>(order);
			newRoot.insertLeftAt(0, pushUp.key, root);
			newRoot.setChild(1, pushUp.newNode);
			root.setRoot(false);
			root = newRoot;
			root.setRoot(true);
		}
	}
	
	
	/**
	 * Looks up for the record that is associated with the specified key
	 * @param key the key to find its record
	 * @return the reference of the record associated with this key 
	 */
	public RTRef search(T key)
	{
		return root.search(key);
	}
	
	/**
	 * Delete a key and its associated record from the tree.
	 * @param key the key to be deleted
	 * @return a boolean to indicate whether the key is successfully deleted or it was not in the tree
	 */
	
	public boolean delete(T key)
	{
		boolean done = root.delete(key, null, -1);
		//go down and find the new root in case the old root is deleted
		while(root instanceof RTreeInnerNode && !root.isRoot())
			root = ((RTreeInnerNode<T>) root).getFirstChild();
		return done;
	}
	
	/**
	 * Returns a string representation of the B+ tree.
	 */
	public String toString()
	{	
		
		//	<For Testing>
		// node :  (id)[k1|k2|k3|k4]{P1,P2,P3,}
		String s = "";
		Queue<RTreeNode<T>> cur = new LinkedList<RTreeNode<T>>(), next;
		cur.add(root);
		while(!cur.isEmpty())
		{
			next = new LinkedList<RTreeNode<T>>();
			while(!cur.isEmpty())
			{
				RTreeNode<T> curNode = cur.remove();
				System.out.print(curNode);
				if(curNode instanceof RTreeLeafNode)
					System.out.print("->");
				else
				{
					System.out.print("{");
					RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
					for(int i = 0; i <= parent.numberOfKeys; ++i)
					{
						System.out.print(parent.getChild(i).index+",");
						next.add(parent.getChild(i));
					}
					System.out.print("} ");
				}
				
			}
			System.out.println();
			cur = next;
		}	
		//	</For Testing>
		return s;
	}
	
	@SuppressWarnings({ "unused" })
	public Vector<Vector<Integer>> searchAll(T key) {
		RTree<T> tito=this;
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		
		int page;
		int place;
		while(tito.search(key)!=null) {
			RTRef r;
			Vector<Integer> b2=new Vector<Integer>();
			r=tito.search(key);
			tito.delete(key);
			b2.add(r.getPage());
			b2.add(r.getIndexInPage());
			indexes.add(b2);	
		}
		
		
		
		return indexes;
	}
	
	
	public Vector<Vector<Integer>> getLessThan(T value) {
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
				
				if(keys[i]!=null&&keys[i].compareTo(value)<0) {
					Vector<Vector<Integer>> copy=tito.searchAll((T) keys[i]);
					
					for(int k=0;k<copy.size();k++) {
						indexes.add(copy.get(k));
					}
						
				}
				
			}
			
			leaf=leaf.getNext();
			
		}
		
		
			
		return indexes;
		
	
	}
	public Vector<Vector<Integer>> getLessThanOrEqual(T value) {
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
			
				if(keys[i]!=null&&keys[i].compareTo(value)<=0) {
					Vector<Vector<Integer>> copy=tito.searchAll((T) keys[i]);
					
					for(int k=0;k<copy.size();k++) {
						
						indexes.add(copy.get(k));
					}
						
				}
				
			}
			RTreeLeafNode<T>currr=leaf;
			leaf=leaf.getNext();
			if(leaf.index==currr.index)
				break;
			
		}
		
		
			
		return indexes;
		
	
	}
	
	public Vector<Vector<Integer>> getMoreThan(T value){
		
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
				
				if(keys[i]!=null&&keys[i].compareTo(value)>0) {
					Vector<Vector<Integer>> copy=tito.searchAll((T) keys[i]);
					
					for(int k=0;k<copy.size();k++) {
						indexes.add(copy.get(k));
					}
						
				}
				
			}
			RTreeLeafNode<T>currr=leaf;
			leaf=leaf.getNext();
			
		}
		
		
			
		return indexes;
	}
	
	public Vector<Vector<Integer>> getMoreThanOrEqual(T value){
		
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
				//System.out.print(keys[i]+" ");
				if(keys[i]!=null&&keys[i].compareTo(value)>=0) {
					Vector<Vector<Integer>> copy=tito.searchAll((T) keys[i]);
					
					for(int k=0;k<copy.size();k++) {
						indexes.add(copy.get(k));
					}
						
				}
				
			}
			RTreeLeafNode<T>currr=leaf;
			leaf=leaf.getNext();
			if(leaf!=null)
			if(leaf.index==currr.index)
				break;
		}
		
		
			
		return indexes;
	}
	
	
	public Vector<Vector<Integer>> getNotEqual(T value){
		
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
				
				if(keys[i]!=null&&keys[i].compareTo(value)!=0) {
					Vector<Vector<Integer>> copy=tito.searchAll((T) keys[i]);
					
					for(int k=0;k<copy.size();k++) {
						indexes.add(copy.get(k));
					}
						
				}
				
			}
			if(leaf.getNext()==null)
				break;
			if(leaf==null)
				break;
			if(leaf.index==leaf.getNext().index)
				break;
			
			leaf=leaf.getNext();
			
		}
		
		
			
		return indexes;
	}
	public int[] wezwez(T key) {
		int [] index= {-1,-1};
		
		
		RTree<T> tito=this;
		
		Vector<Vector<Integer>> indexes=new Vector<Vector<Integer>>();
		RTreeNode<T> curNode = tito.root;
		
		
		while(curNode instanceof RTreeInnerNode) {
			RTreeInnerNode<T> parent = (RTreeInnerNode<T>) curNode;
			curNode=parent.getFirstChild();
			
		}
		boolean flag=true;
		// *got first leaf
		
		RTreeLeafNode<T> leaf=(RTreeLeafNode)curNode;
		
		
		while(leaf!=null) {
			Comparable<T>[] keys=leaf.keys;
			
			for(int i=0;i<keys.length;i++) {
				//System.out.print(keys[i]+" ");
				if(keys[i]!=null)
				if(keys[i].compareTo(key)>0) {
					
					RTRef ref=tito.search((T)keys[i]);
					index[0]=ref.getPage();
					index[1]=ref.getIndexInPage();
					return index;
				}
				
			}
			leaf=leaf.getNext();
		}
		return index;
		
	}
	
	
	
}
