package ANAMESH3AYEEZ2ALA2HENA;

import java.awt.Polygon;
import java.beans.Transient;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Vector removefromtable(Vector record) throws Exception {
		this.update();
		Vector out = new Vector<String>();
		for (int i = 0; i < this.pagesreferences.size(); i++) {
			Page page = this.read_page(this.pagesreferences.get(i));
			System.err.println(page.delete(record, this.getIsindexed(), i, this));

			if (page.RecordsGetter().isEmpty()) {
				out.add(page.getPath());
				this.pagesreferences.remove(i);
				i--;
			}
		}
		this.update();

		return out;
	}

	public void removeindfromtable(Vector<Object> dellrecord, Vector<Integer> record)
			throws IOException, DBAppException {
		Page page = this.read_page(this.pagesreferences.get(record.get(0)));
		page.deleteind(dellrecord, record.get(1));
		if (page.RecordsGetter().isEmpty()) {
			this.pagesreferences.remove(record.get(0));
		}
		this.update();
	}

}
