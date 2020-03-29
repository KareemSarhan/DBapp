package ANAMESH3AYEEZ2ALA2HENA;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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

	public Vector removefromtable(Vector record) throws IOException, DBAppException {
		this.update();
		Vector out = new Vector<String>();
		for (int i = 0; i < this.pagesreferences.size(); i++) {
			Page page = this.read_page(this.pagesreferences.get(i));
			System.err.println(page.delete(record));

			if (page.RecordsGetter().isEmpty()) {
				out.add(page.getPath());
				this.pagesreferences.remove(i);
			}
		}
		this.update();

		return out;
	}

	// public static void main(String[] args) throws IOException, DBAppException {
	//// Table t=new Table("final");
	////
	//// Vector r=new Vector();
	//// Polygon p=new Polygon();
	//// p.addPoint(0, 0);
	//// p.addPoint(0, 5);
	//// p.addPoint(5, 0);
	//// p.addPoint(5, 5);
	//// r.add(p);
	//// t.inserttotable(r);
	//
	// try {
	// ObjectInputStream o;
	// o = new ObjectInputStream( new FileInputStream("final.bin"));
	// Table p1 = (Table) o.readObject();
	// // System.out.print(p1.getPath());
	// Vector r=new Vector();
	// Polygon p=new Polygon();
	// p.addPoint(0, 0);
	// p.addPoint(0, 500);
	// p.addPoint(500, 0);
	// p.addPoint(500, 500);
	// r.add(p);
	// r.add(3);
	// p1.inserttotable(r);
	//
	//
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// }
	// Page p=read_page("gay0.bin");
	// p.display();
	// }
}