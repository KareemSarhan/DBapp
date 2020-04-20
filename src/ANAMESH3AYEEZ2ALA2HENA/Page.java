package ANAMESH3AYEEZ2ALA2HENA;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.*;
import java.util.*;

import BPTree.BPTree;
import RTree.RTree;

@SuppressWarnings("all")

public class Page implements Serializable {
	private String ID;
	private String path;
	private Vector records;
	private int maxnumrecords;

	public Page(String id) throws DBAppException {
		this.ID = id;
		this.records = new Vector();
		this.maxnumrecords = 5;
		PrintWriter writer;
		this.path = this.ID + ".bin";
		try {
			writer = new PrintWriter(this.path, "UTF-8");

			InputStream input = new FileInputStream("config/DBApp.properties");

			Properties prop = new Properties();

			// load a properties

			prop.load(input);

			// get the property value and print it out
			this.maxnumrecords = Integer.parseInt(prop.getProperty("MaximumRowsCountinPage"));

			writer.flush();
			writer.close();
		} catch (Exception ex) {
			this.maxnumrecords = 5;

		}

	}

	public int getMaxnumrecords() {
		return maxnumrecords;
	}

	public String getPath() {
		return path;
	}

	private void update() throws IOException, DBAppException {
		try {
			FileOutputStream file = new FileOutputStream(this.path);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this);
			out.close();
			file.close();
		} catch (FileNotFoundException e) {
			throw new DBAppException("ERROR has occured");
		}
	}

	// the records will be vector of vectors(clo
	// we have to insert according to the clustering key
	// the the order of data will b handled internally(key then the res of the
	// fields
	public Vector insertpage(Vector record) throws IOException, DBAppException {

		if (this.records.size() == 0) {// page is empty

			this.records.insertElementAt(record, 0);

			this.update();
			return null;
		} else { // page is not empty
			Object tempg = null;
			tempg = record.get(0);
			Vector last = (Vector) this.records.get(this.records.size() - 1);
			// Vector first=(Vector) this.records.get(0);

			if (this.maxnumrecords == this.records.size()) {
				if (compare(last.get(0), tempg)) {

					// System.out.println("greater than the last record in the page");

					return record;
				} else {
					// System.out.println("over flow");
					for (int i = 0; i < this.maxnumrecords; i++) {
						Vector temp = (Vector) this.records.elementAt(i);
						//
						if (compare(tempg, temp.get(0))) {
							// record.remove(record.size()-1);
							this.records.add(i, record);
							Vector result = (Vector) this.records.get(maxnumrecords);
							this.records.remove(this.maxnumrecords);
							this.update();
							return result;
						}
					}
				}
			} else// page is not full
			{

				for (int i = 0; i < this.records.size(); i++) {
					Vector temp = (Vector) this.records.elementAt(i);

					if (compare(tempg, temp.get(0))) {
						this.records.add(i, record);
						this.update();
						return null;
					}
				}

				this.records.add(record);
			}
		}

		try {
			this.update();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.update();
		return null;

	}

	public void quickadd(int loc, Vector record) throws IOException, DBAppException {
		this.records.add(loc, record);
		this.update();
	}

	public void removelastrecord() throws IOException, DBAppException {
		this.records.removeElementAt(this.records.size() - 1);
		this.update();
	}

	public void display() {
		for (int i = 0; i < this.records.size(); i++) {
			Vector temp = (Vector) this.records.get(i);
			System.out.print("[");
			for (int j = 0; j < temp.size(); j++) {
				// System.out.println(i);
				if (temp.get(j) instanceof Polygon) {
					System.out.print("[");
					Polygon currentR = (Polygon) temp.get(j);
					for (int k = 0; k < currentR.npoints; k++) {
						System.out.print("(" + currentR.xpoints[k] + "," + currentR.ypoints[k] + ") ");
					}
					System.out.print("], ");
				} else
					System.out.print(temp.get(j) + " , ");
			}
			System.out.print("]");
			System.out.println("");
		}
	}

	public boolean deleteind(Vector<Object> dellrecord, Integer integer) throws IOException, DBAppException {
		Vector pagerecord = (Vector) RecordsGetter().get(integer);
		boolean flag= false;
		if (FinalSubCompare(dellrecord, pagerecord)) {
			flag = this.records.remove(pagerecord);
		}
		this.update();
		return flag;
	}

	private boolean FinalSubCompare(Vector<Object> dellrecord, Vector pagerecord) throws DBAppException {
		for (int i = 0; i < dellrecord.size(); i++) {

			if (dellrecord.get(i) != null) {
				if (dellrecord.get(i) instanceof PolygonE) {
					if (!makePolygon((String)pagerecord.get(i)).equals((PolygonE) dellrecord.get(i)))
						return false;
				} else {
					if (!pagerecord.get(i).equals(dellrecord.get(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean delete(Vector record) throws Exception {
		// System.err.println(this.records.indexOf(record));
		boolean removed = false;
		for (int i = 0; i < this.records.size(); i++) {
			if (FinalSubCompare(record,(Vector)this.records.get(i))) {
				removed = this.records.remove((Vector) this.records.get(i));
				i--;
			}
		}
		this.update();
		return removed;
	}

	private static <T extends Comparable<T>> boolean SubCompare(Vector record, Vector Deleterecord,
			List<Boolean> indexed, int pagenum, int recnum, Table table) throws Exception {
		Vector<Vector<Integer>> tempVtree;
		BPTree<T> BPtree;
		RTree<T> Rtree;
		boolean returned = false;

		Vector<Integer> tempVector = new Vector<Integer>();
		for (int i = 0; i < Deleterecord.size() - 1; i++) {
			if (Deleterecord.get(i) != null && indexed.get(i) == false) {
				if (record.get(i) instanceof PolygonE && Deleterecord.get(i) instanceof PolygonE) {
					if (!((PolygonE) record.get(i)).equals(((PolygonE) Deleterecord.get(i))))
						return returned;
				} else {
					if (!record.get(i).equals(Deleterecord.get(i))) {
						return returned;
					}

				}
			} else if (Deleterecord.get(i) != null && indexed.get(i) == true) {
				if (Deleterecord.get(i) instanceof PolygonE) {
					Rtree = (RTree<T>) DBApp.StaticgetTree(table.getName(), table.getColoumn_names().get(i));
					tempVtree = Rtree.searchAll((T) Deleterecord.get(i));
					tempVector.add(pagenum);
					tempVector.add(recnum);
					if (tempVtree.indexOf(tempVector) == -1) {
						return returned;
					}
				} else {
					BPtree = (BPTree<T>) DBApp.StaticgetTree(table.getName(), table.getColoumn_names().get(i));
					tempVtree = BPtree.searchAll((T) Deleterecord.get(i));
					tempVector.add(pagenum);
					tempVector.add(recnum);
					if (tempVtree.indexOf(tempVector) == -1) {
						return returned;
					}

					// if (!record.get(i).equals(Deleterecord.get(i))) {
					// return false;
					// }
				}

			}
		}
		returned = true;
		return returned;

	}

	public Vector RecordsGetter() {
		return this.records;
	}

	public static boolean compare(Object x, Object y) {
		if (x instanceof Integer) {

			if ((int) x <= (int) y) {

				return true;
			}
			return false;
		} else if (x instanceof Double) {
			if ((Double) x <= (Double) y) {
				return true;
			}
			return false;

		} else if (x instanceof Date) {
			Date temp1 = (Date) x;
			Date temp2 = (Date) y;
			if (temp1.compareTo(temp2) > 0) {
				return true;
			}
			return false;
		} else {
			try {
				int n = polygonCompare((String) x, (String) y);
				if (n <= 0) {
					// System.out.println(1);
					return true;
				}
				// System.out.println(1.1);
				return false;
			} catch (Exception e) {
				if (((String) x).compareTo((String) y) <= 0) {
					// System.out.println(2);
					return true;
				}
				// System.out.println(2.1);
				return false;
			}

		}
	}

	public static PolygonE makePolygon(String s) throws DBAppException {
		// this method takes the input string from the user
		// and parses the string into polygon and returns the poly
		PolygonE p = new PolygonE();
		s = s.replace("(", "");
		s = s.replace(")", "");
		String[] points = s.split(",");

		try {
			for (int i = 0; i < points.length; i = i + 2) {
				int x = Integer.parseInt(points[i]);
				int y = Integer.parseInt(points[i + 1]);
				p.addPoint(x, y);
			}
		} catch (NumberFormatException e) {
			throw new DBAppException("Wrong polygon format");
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new DBAppException("Not enough points for polygon");
		}

		return p;
	}

	public static int polygonCompare(String s1, String s2) throws DBAppException {
		// if the first polygon is bigger it returns 1;
		// if the 2nd polygon is bigger it returns -1
		// if they are equal it returns 0
		Polygon p1 = makePolygon(s1);
		Polygon p2 = makePolygon(s2);
		int n = -2;
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;

		Dimension dim2 = p2.getBounds().getSize();
		int p2area = dim2.width * dim2.height;
		if (p1area > p2area) {
			n = 1;
		} else if (p1area == p2area) {
			n = 0;
		} else if (p1area < p2area) {
			n = -1;
		}
		return n;
	}
}

// public static void main(String[]args) throws DBAppException, IOException
// {
//// Page p=new Page("final");
//// Vector rec=new Vector();
//// rec.add("(10,20),(30,30),(40,40),(50,60)");
//// p.insertpage(rec);
//// System.out.print(p.records);
// ////
// try {
// ObjectInputStream o;
// o = new ObjectInputStream( new FileInputStream("final.bin"));
// Page p = (Page) o.readObject();
// System.out.println(p.maxnumrecords);
// //p.display();
// Vector rec=new Vector();
// rec.add("c");
//
// p.insertpage(rec);
//
// p.display();
//
//
// } catch (Exception e) {
//
// //System.out.print(e.p);
// e.printStackTrace();
// }
// ////
// }
// }
//// if ((record.get(0) instanceof PolygonE) && (Deleterecord.get(0) instanceof
//// PolygonE)) {
//// if (!((PolygonE) record.get(0)).equals((PolygonE) Deleterecord.get(0))) {
//// flag = flag & false;
//// // they are not equal if they got here
//// } else if (!record.get(0).equals(Deleterecord.get(0))) {
//// flag = flag & false;
//// }
//// }
//// for (int i = 1; i < Deleterecord.size(); i++) {
//// // if delete record has a value and this value is equal to the value of the
//// // record
//// // then this record will be deleted
//// if (record.get(i) instanceof PolygonE && Deleterecord.get(i) instanceof
//// PolygonE
//// && Deleterecord.get(i) != null
//// && !((PolygonE) record.get(i)).equals((PolygonE) Deleterecord.get(i)))
//// flag = flag & false;
//// }
//// for (int i = 1; i < Deleterecord.size(); i++) {
//// // if delete record has a value and this value is equal to the value of the
//// // record
//// // then this record will be deleted
//// if (Deleterecord.get(i) != null &&
//// !Deleterecord.get(i).equals(record.get(i)))
//// flag = flag & false;
//// }
//// return flag;