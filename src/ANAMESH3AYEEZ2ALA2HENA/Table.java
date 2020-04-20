package ANAMESH3AYEEZ2ALA2HENA;

import java.awt.Dimension;
import java.awt.Polygon;
import java.beans.Transient;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import BPTree.BPTree;
import RTree.RTree;

@SuppressWarnings("all")

public class Table implements Serializable {
	private transient String name;
	private transient String path;
	private List<String> pagesreferences;
	private transient List<String> coloumn_names;
	private transient List<String> datatype;
	private transient List<Boolean> iskey;
	private transient List<Boolean> isindexed;
	private int numofcreatedpages;

	public Table(String name) {
		this.name = name;
		this.path = this.name + ".bin";
		this.pagesreferences = new ArrayList<String>();
		this.numofcreatedpages = 0;

	}

	public String getPath() {
		return path;

	}

	public int getNumofcreatedpages() {
		return numofcreatedpages;
	}

	public void setNumofcreatedpages(int numofcreatedpages) {
		this.numofcreatedpages = numofcreatedpages;
	}

	public String getName() {

		return name;
	}

	public void setPagesreferences(List<String> pagesreferences) {
		this.pagesreferences = pagesreferences;
	}

	public List<String> getPagesreferences() {
		return pagesreferences;
	}

	public List<String> getColoumn_names() {
		return coloumn_names;
	}

	public void setColoumn_names(List<String> coloumn_names) {
		this.coloumn_names = coloumn_names;
	}

	public List<String> getDatatype() {
		return datatype;
	}

	public void setDatatype(List<String> datatype) {
		this.datatype = datatype;
	}

	public List<Boolean> getIskey() {
		return iskey;
	}

	public void setIskey(List<Boolean> iskey) {
		this.iskey = iskey;
	}

	public List<Boolean> getIsindexed() {
		return isindexed;
	}

	public void setIsindexed(List<Boolean> isindexed) {
		this.isindexed = isindexed;
	}

	public void update() throws IOException, DBAppException {
		try {
			PrintWriter writer;
			writer = new PrintWriter(this.path, "UTF-8");

			// System.out.print(true);
			FileOutputStream file = new FileOutputStream(this.path);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this);
			out.close();
			file.close();
		} catch (FileNotFoundException e) {
			throw new DBAppException();
		}

	}

	public void inserttotablemain(Vector record) throws Exception {
		int[] ref = // null;
				this.get_ref_indx(record);
		System.out.print(ref);
		// System.out.print(ref[0]+":"+ref[1]);
		// new int[]{-1,0};
		if (ref == null) {
			this.inserttotable(record);
		} else {
			if (ref[0] == -1) {
				// case 1 there are no pages
				if (this.pagesreferences.size() == 0) {
					Page p = new Page(this.name + this.numofcreatedpages);
					this.pagesreferences.add(p.getPath());
					p.insertpage(record);
					this.numofcreatedpages += 1;
				} else {
					// the last record in the table
					Page p = read_page(this.pagesreferences.get(this.pagesreferences.size() - 1));
					// p.insertpage(record);
					// System.out.print(p.RecordsGetter().size());
					if (p.getMaxnumrecords() == p.RecordsGetter().size()) {
						// System.out.print("b");
						Page p1 = new Page(this.name + this.numofcreatedpages);
						p1.insertpage((Vector) p.RecordsGetter().lastElement());

						this.numofcreatedpages += 1;
						this.pagesreferences.add(p1.getPath());

					}
				}
			} else {
				System.out.println(this.pagesreferences);
				Page p = read_page(this.pagesreferences.get(ref[0]));
				p.quickadd(ref[1], record);

				// System.out.println(p.RecordsGetter());
				if (p.RecordsGetter().size() > p.getMaxnumrecords()) {
					Vector temp = (Vector) p.RecordsGetter().lastElement();
					for (int i = ref[0] + 1; i < this.pagesreferences.size(); i++) {
						Page page = this.read_page(this.pagesreferences.get(i));

						temp = page.insertpage(temp);

						if (temp == null) {
							break;
						}
					}
					if (temp != null) {
						Page p1 = new Page(this.name + this.numofcreatedpages);
						this.pagesreferences.add(p1.getPath());
						p1.insertpage(temp);
						this.numofcreatedpages += 1;
					}
				}
			}
		}
		this.update();
	}

	public void inserttotable(Vector record) throws IOException, DBAppException {
		Vector temp = record;
		// there are no pages
		if (this.pagesreferences.size() == 0) {
			Page p = new Page(this.name + this.numofcreatedpages);
			this.pagesreferences.add(p.getPath());
			p.insertpage(record);
			this.numofcreatedpages += 1;
		} else// there are pages
		{

			for (int i = 0; i < this.pagesreferences.size(); i++) {
				Page page = this.read_page(this.pagesreferences.get(i));

				temp = page.insertpage(temp);

				if (temp == null) {
					break;
				}
			}
			if (temp != null) {
				Page p = new Page(this.name + this.numofcreatedpages);
				this.pagesreferences.add(p.getPath());
				p.insertpage(temp);
				this.numofcreatedpages += 1;
			}
		}

		this.update();
	}

	public <T extends Comparable<T>> int[] get_ref_indx(Vector record) throws Exception {

		for (int i = 0; i < this.iskey.size(); i++) {
			if (this.iskey.get(i) == true) {
				if (this.datatype.get(i).equals("java.awt.Polygon") && this.isindexed.get(i) == true) {
					System.out.println(this.name + "  " + this.coloumn_names.get(i));
					RTree<T> temp = this.getRTree(this.name, this.coloumn_names.get(i));
					int[] res = temp.wezwez((T) getPolyArea((String) record.get(0)));
					System.out.println("1:" + res[0] + res[1]);
					return res;
				} else if (this.datatype.get(i).equals("java.awt.Polygon") == false && this.isindexed.get(i) == true) {
					// wher we should return a bptree ?????
					BPTree<T> temp = (BPTree<T>) this.getBPlusTree(this.name, this.coloumn_names.get(i));
					int[] res = temp.wezwez((T) record.get(0));
					System.out.println("2:" + res[0] + res[1]);
					return res;
				} else {
					// System.out.println("3");
					return null;
				}
			}
		}
		return null;
	}

	public <T extends Comparable<T>> RTree<T> getRTree(String strTableName, String strColName) throws Exception {
		RTree<T> o = new RTree<T>(3);
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream("data/" + strTableName + strColName);
			ObjectInputStream in = new ObjectInputStream(file);

			// Method for deserialization of object
			o = (RTree<T>) in.readObject();

			in.close();
			file.close();

		}

		catch (Exception ex) {
			throw ex;
		}
		return o;

	}

	public <T extends Comparable<T>> Object getBPlusTree(String strTableName, String strColName) throws DBAppException {
		Object o = 3;
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream("data/" + strTableName + strColName);
			ObjectInputStream in = new ObjectInputStream(file);

			// Method for deserialization of object
			o = (Object) in.readObject();

			in.close();
			file.close();

		}

		catch (Exception ex) {

		}

		return o;
	}

	public Integer getPolyArea(String s) throws DBAppException {
		int area = 0;
		Polygon p1 = makePolygon(s);
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;
		return area;
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

	static Page read_page(String ref) {

		try {
			ObjectInputStream o;
			o = new ObjectInputStream(new FileInputStream(ref));
			Page foo2 = (Page) o.readObject();
			return foo2;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;
	}

	public Vector removefromtable(Vector record) throws Exception {
		this.update();
		Vector out = new Vector<String>();
		for (int i = 0; i < this.pagesreferences.size(); i++) {
			Page page = this.read_page(this.pagesreferences.get(i));
			page.delete(record);
			if (page.RecordsGetter().isEmpty()) {
				out.add(page.getPath());
				this.pagesreferences.remove(i);
				i--;
			}
		}
		this.update();

		return out;
	}

	public boolean removeindfromtable(Vector<Object> dellrecord, Vector<Integer> recordlocation)
			throws IOException, DBAppException {
				boolean flag = false;
		Page page = this.read_page(this.pagesreferences.get(recordlocation.get(0)));
		flag =  page.deleteind(dellrecord, recordlocation.get(1));
		if (page.RecordsGetter().isEmpty()) {
			this.pagesreferences.remove(recordlocation.get(0));
		}
		this.update();
		return flag;
	}

	public static void main(String[] args) throws Exception {
		Table t = new Table("weza");
		System.out.print(t.read_page("finaloooo0.bin").RecordsGetter());
	}
}
