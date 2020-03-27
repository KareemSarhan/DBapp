
import java.awt.Dimension;
import java.awt.Polygon;
import java.io.*;
import java.util.*;
import java.util.Date;

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

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.out.print("Failer");

		}

		try (InputStream input = new FileInputStream("config/DBapp.properties")) {

			Properties prop = new Properties();

			// load a properties

			prop.load(input);

			// get the property value and print it out
			this.maxnumrecords = 4;
			// Integer.parseInt(prop.getProperty("MaximumRowsCountinPage")) ;

		} catch (IOException ex) {
			throw new DBAppException("Error with properties file");
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

	public void display() {
		for (int i = 0; i < this.records.size(); i++) {

			System.out.println(records.get(i).toString());
		}
	}

	public boolean delete(Vector record) throws IOException, DBAppException {
		// System.err.println(this.records.indexOf(record));
		boolean removed = false;
		for (int i = 0; i < this.records.size(); i++) {
			// if ()
			// System.err.println(SubCompare((Vector) this.records.get(i), record));
			if (SubCompare((Vector) this.records.get(i), record)) {
				this.records.remove((Vector) this.records.get(i));
				i--;
				removed = true;

			}
		}
		if (this.records.indexOf(record) == -1) {
		} else {
			this.records.remove(record);
			removed = true;
			this.update();
		}

		// this.display();
		return removed;
	}

	private boolean SubCompare(Vector record, Vector Deleterecord) {
		// System.out.println(record.toString());
		// System.out.println(Deleterecord.toString());
		if (!record.get(0).equals(Deleterecord.get(0)))
			return false;

		for (int i = 1; i < Deleterecord.size(); i++) {
			if (Deleterecord.get(i) != null && !Deleterecord.get(i).equals(record.get(i)))
				return false;
		}
		return true;

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

		} else if (x instanceof String) {
			if (((String) x).compareTo((String) y) > 0) {
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
			int n = polygonCompare((Polygon) x, (Polygon) y);
			if (n <= 0) {
				return true;
			}
			return false;
		}
	}

	public static int polygonCompare(Polygon p1, Polygon p2) {
		// if the first polygon is bigger it returns 1;
		// if the 2nd polygon is bigger it returns -1
		// if they are equal it returns 0
		int n = -2;
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;

		Dimension dim2 = p2.getBounds().getSize();
		int p2area = dim2.width * dim2.height;
		// System.out.println(p1area+" "+p2area);
		if (p1area > p2area) {
			n = 1;
		} else if (p1area == p2area) {
			n = 0;
		} else if (p1area < p2area) {
			n = -1;
		}

		return n;
	}

	// public static void main(String[]args) throws DBAppException, IOException
	//// {
	////
	//// try {
	//// ObjectInputStream o;
	//// o = new ObjectInputStream( new FileInputStream("final0.bin"));
	//// Page p = (Page) o.readObject();
	//// //System.out.print(p.ID);
	//// p.display();
	////// Vector rec=new Vector();
	////// rec.add(1);
	//////
	////// p.insertpage(rec);
	//////
	////// p.display();
	////
	////
	//// } catch (Exception e) {
	////
	//// //System.out.print(e.p);
	//// e.printStackTrace();
	//// }
	////
	// }
}
