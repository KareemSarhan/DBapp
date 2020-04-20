package ANAMESH3AYEEZ2ALA2HENA;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import BPTree.BPTree;
import BPTree.Ref;
import RTree.RTRef;
import RTree.RTree;

@SuppressWarnings("all")

// TODO:Page files should be created in the data file

// TODO: Please delete all system.out.print and all system.out.print comments

public class DBApp<T extends Comparable<T>> {

	public DBApp() {
	}

	public Table loadTable(String tableName) throws DBAppException {
		Table table = new Table(tableName);

		File myfile = new File("data/metadata.csv");
		boolean tablefound = false; // to check if the table is already there
		BufferedReader reader = null;
		String line = "";
		String cvsSplitBy = ",";
		List<String> coloumn_names = new ArrayList<String>();
		List<String> datatype = new ArrayList<String>();
		List<Boolean> iskey = new ArrayList<Boolean>();
		List<Boolean> isindexed = new ArrayList<Boolean>();
		try {
			// next we need to check if the table is already there
			reader = new BufferedReader(new FileReader("data/metadata.csv"));
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(cvsSplitBy);

				if (data[0].equals(tableName)) {
					// we should throw Table already exists exception

					coloumn_names.add(data[1]);
					datatype.add(data[2]);
					if (data[3].equals("False"))
						iskey.add(false);
					else
						iskey.add(true);
					if (data[4].equals("False"))
						isindexed.add(false);
					else
						isindexed.add(true);
					tablefound = true;
				}
			}

			if (tablefound) {
				// table.setName(tableName);
				table.setColoumn_names(coloumn_names);
				table.setDatatype(datatype);
				table.setIsindexed(isindexed);
				table.setIskey(iskey);
			} else {
				reader.close();
				throw (new DBAppException("Table doesnt exist"));
			}
			reader.close();

		} catch (FileNotFoundException e) {
			throw new DBAppException("");
		} catch (IOException e) {
			throw new DBAppException("");
		}
		return table;
	}

	// this method add the table properties to the metadata file
	// and creates Table object with all required properties
	// then creates a .class file with the table object in it
	// the file is named after the table

	public boolean check(Object data, String datatype) throws DBAppException {
		if (datatype.equals("java.lang.Integer")) {
			if (data instanceof Integer) {

				return true;
			} else {

				return false;
			}
		} else if (datatype.equals("java.lang.Double")) {
			if (data instanceof Double) {
				return true;
			} else {
				return false;
			}
		} else if (datatype.equals("java.lang.Boolean")) {
			if (data instanceof Boolean) {
				return true;
			} else {
				return false;
			}
		} else if (datatype.equals("java.lang.String")) {
			if (data instanceof String) {
				return true;
			} else {
				return false;
			}
		} else if (datatype.equals("java.lang.Date")) {
			if (data instanceof Date) {
				return true;
			} else {
				return false;
			}
		} else {
			DBApp.makePolygon((String) data);
			return true;
		}
	}

	//
	// done
	// takes a table and hash table and checks that the fields are correct

	public boolean missorins(Table table, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		for (int i = 0; i < table.getColoumn_names().size() - 1; i++) {
			Object o = htblColNameValue.get(table.getColoumn_names().get(i));

			if (o == null) {
				return true;
			}
			String datatype = (table.getDatatype().get(i));

			if (check(o, datatype) == false) {
				return true;
			}
		}

		return false;
	}

	public boolean inCons(Table table, Hashtable<String, Object> t) throws DBAppException {
		for (int i = 0; i < table.getColoumn_names().size(); i++) {
			Object o = t.get(table.getColoumn_names().get(i));
			String datatype = (table.getDatatype().get(i));
			// System.out.println(o+" "+datatype);
			if (o == null)
				continue;
			if (check(o, datatype) == false)
				return true;
		}
		return false;
	}

	// gets the pages references of a table

	public static Vector deserilizetable(String tblname) {
		Vector result = new Vector();
		try {
			// System.out.println("testo.txt");
			ObjectInputStream o = new ObjectInputStream(new FileInputStream(tblname + ".bin"));
			Table foo2 = (Table) o.readObject();
			// System.out.print(foo2.getPagesreferences().get(0));
			o.close();

			result.add(foo2.getPagesreferences());
			result.add(foo2.getNumofcreatedpages());
			return result;
		} catch (Exception e) {
			List<String> references = new ArrayList<String>();
			int num = 0;
			result.add(references);
			result.add(num);
			return result;
		}

	}

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException, IOException {

		File myfile = new File("data/metadata.csv");
		boolean tablefound = false; // to check if the table is already there
		BufferedReader reader = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			// next we need to check if the table is already there
			reader = new BufferedReader(new FileReader("data/metadata.csv"));
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(cvsSplitBy);
				if (data[0].equals(strTableName)) {
					// we should throw Table already exists exception
					// System.out.println("Table already exists");
					tablefound = true;
					break;
				}
			}
			if (!tablefound) {
				// if the table is not there then we should add its data :`)
				// we need to traverse through the hashmap to get everything with its type
				// we need to know which one is the primary key of the table
				// we need to know if its indexed or not(by default they are all not indexed
				// will use this to iterate through hash map D:

				BufferedWriter writer = new BufferedWriter(new FileWriter(myfile, true));

				// first we will add the cluster key

				String keyName = strClusteringKeyColumn;
				String keyType = htblColNameType.get(keyName);
				String ki = strTableName + "," + keyName + "," + keyType + "," + "True" + "," + "False" + "\n";
				writer.write(ki);
				htblColNameType.remove(strClusteringKeyColumn);
				Iterator i = htblColNameType.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry mapElement = (Map.Entry) i.next();
					String colName = (String) mapElement.getKey(); // id,name,last name
					String colType = (String) mapElement.getValue(); // integer string double we keda
					String key = "False";
					if (colName.equals(strClusteringKeyColumn))
						key = "True";
					String s = strTableName + "," + colName + "," + colType + "," + key + "," + "False" + "\n";
					writer.write(s);
				}
				writer.write(strTableName + ",TouchDate,java.util.Date,False,False" + "\n");
				writer.flush();
				writer.close();
			} else { // if table is already there
				throw (new DBAppException("table already exists"));
			}
		} catch (FileNotFoundException e) {
			throw new DBAppException();
		} catch (IOException e) {
			throw new DBAppException();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new DBAppException();
				}
			}
		}
	}

	public void init() throws IOException {
		// this does whatever initialization you would like
		// or leave it empty if there is no code you want to
		// execute at application startup
		// creates the metadata file if its not already there

		File myfile = new File("data/metadata.csv");
		if (myfile.createNewFile()) { // checking if the metadata file is there or not
			String s = "Table Name,Column Name,Column Type,Key,Indexed\n";
			BufferedWriter writer = new BufferedWriter(new FileWriter(myfile, true));
			writer.write(s);
			writer.flush();
			writer.close();
		}
	}

	public RTree<T> getRTree(String strTableName, String strColName) throws Exception {
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

	public Object getBPlusTree(String strTableName, String strColName) throws DBAppException {
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

	public void refreshRTree(String strTableName, String strColName) throws DBAppException {

		// * load table object
		Table table = this.loadTable(strTableName);
		Vector rest = this.deserilizetable(strTableName);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int) rest.get(1));
		// *get the type of the column and parse the value into it
		int colIndex = -1;
		for (int i = 0; i < table.getColoumn_names().size(); i++) {
			if (table.getColoumn_names().get(i).equals(strColName)) {
				colIndex = i;
				break;
			}
		}
		// * get the type of the column to create the tree with the same type
		String colType = table.getDatatype().get(colIndex);
		int treeOrder = 0;
		// java.lang.Integer, java.lang.String,
		// java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon

		// * this will go to the properties file to get the maximum node size :D
		try (InputStream input = new FileInputStream("config/DBApp.properties")) {

			Properties prop = new Properties();

			// load a properties

			prop.load(input);

			// get the property value and print it out

			treeOrder = Integer.parseInt(prop.getProperty("NodeSize"));
		} catch (IOException ex) {
			throw new DBAppException("Error with properties file");
		}

		if (colType.contentEquals("java.awt.Polygon")) {
			// *create a BPlus tree with Integer type
			RTree<Integer> t1 = new RTree<Integer>(treeOrder);

			// *Loop over each page in the table
			for (int i = 0; i < table.getPagesreferences().size(); i++) {

				// *create page object for each page
				Page page = table.read_page(table.getPagesreferences().get(i));

				// *loop over each record in the page and insert it in the tree with its
				// respective place
				Vector<Vector> recordss = page.RecordsGetter();

				for (int j = 0; j < recordss.size(); j++) {

					// *create vector record
					Vector record = recordss.get(j);

					// *insert the respective column key with its position
					Object u = record.get(colIndex);
					Integer h = u instanceof Polygon ? getPolyArea((Polygon) u) : getPolyArea((String) u);
					t1.insert(h, new RTRef(i, j));

				}

			}

			// *now we are done with creating the tree and need to serialize it into a file
			// :D

			try {
				File file = new File("data/" + strTableName + strColName);

				// Saving of object in a file
				FileOutputStream fo = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fo);

				// Method for serialization of object
				out.writeObject(t1);

				out.close();
				fo.close();

			} catch (Exception e) {
				throw new DBAppException("Something went wrong with serializing Rtree");
			}

		}

	}

	public void refreshBTree(String strTableName, String strColName) throws DBAppException {

		Table table = this.loadTable(strTableName);
		Vector rest = this.deserilizetable(strTableName);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int) rest.get(1));
		// *get the type of the column and parse the value into it
		int colIndex = -1;
		for (int i = 0; i < table.getColoumn_names().size(); i++) {
			if (table.getColoumn_names().get(i).equals(strColName)) {
				colIndex = i;
				break;
			}
		}
		// * get the type of the column to create the tree with the same type
		String colType = table.getDatatype().get(colIndex);
		int treeOrder = 0;
		// java.lang.Integer, java.lang.String,
		// java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon

		// * this will go to the properties file to get the maximum node size :D
		try (InputStream input = new FileInputStream("config/DBApp.properties")) {

			Properties prop = new Properties();

			// load a properties

			prop.load(input);

			// get the property value and print it out

			treeOrder = Integer.parseInt(prop.getProperty("NodeSize"));
		} catch (IOException ex) {
			throw new DBAppException("Error with properties file");
		}

		try {

			if (colType.contentEquals("java.lang.Integer")) {
				// *create a BPlus tree with Integer type
				BPTree<Integer> t1 = new BPTree<Integer>(treeOrder);

				// *Loop over each page in the table
				for (int i = 0; i < table.getPagesreferences().size(); i++) {

					// *create page object for each page
					Page page = table.read_page(table.getPagesreferences().get(i));

					// *loop over each record in the page and insert it in the tree with its
					// respective place
					Vector<Vector> recordss = page.RecordsGetter();

					for (int j = 0; j < recordss.size(); j++) {

						// *create vector record
						Vector record = recordss.get(j);

						// *insert the respective column key with its position
						t1.insert((Integer) record.get(colIndex), new Ref(i, j));

					}

				}

				// *now we are done with creating the tree and need to serialize it intoa file
				// :D

				try {
					File file = new File("data/" + strTableName + strColName);

					// Saving of object in a file
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fo);

					// Method for serialization of object
					out.writeObject(t1);

					out.close();
					fo.close();

				} catch (Exception e) {
					throw new DBAppException("Something went wrong with serializing Btree");
				}

				// ---------------------------------------------------------------------------------------
			} else if (colType.contentEquals("java.lang.Double")) {
				// *create a BPlus tree with Integer type
				BPTree<Double> t1 = new BPTree<Double>(treeOrder);

				// *Loop over each page in the table
				for (int i = 0; i < table.getPagesreferences().size(); i++) {

					// *create page object for each page
					Page page = table.read_page(table.getPagesreferences().get(i));

					// *loop over each record in the page and insert it in the tree with its
					// respective place
					Vector<Vector> recordss = page.RecordsGetter();

					for (int j = 0; j < recordss.size(); j++) {

						// *create vector record
						Vector record = recordss.get(j);

						// *insert the respective column key with its position
						t1.insert((Double) record.get(colIndex), new Ref(i, j));

					}

				}

				// *now we are done with creating the tree and need to serialize it intoa file
				// :D

				try {
					File file = new File("data/" + strTableName + strColName);

					// Saving of object in a file
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fo);

					// Method for serialization of object
					out.writeObject(t1);

					out.close();
					fo.close();

				} catch (Exception e) {
					throw new DBAppException("Something went wrong with serializing Btree");
				}

			}
			// ------------------------------------------------------------------------------
			else if (colType.contentEquals("java.lang.String")) {
				// *create a BPlus tree with Integer type
				BPTree<String> t1 = new BPTree<String>(treeOrder);

				// *Loop over each page in the table
				for (int i = 0; i < table.getPagesreferences().size(); i++) {

					// *create page object for each page
					Page page = table.read_page(table.getPagesreferences().get(i));

					// *loop over each record in the page and insert it in the tree with its
					// respective place
					Vector<Vector> recordss = page.RecordsGetter();

					for (int j = 0; j < recordss.size(); j++) {

						// *create vector record
						Vector record = recordss.get(j);

						// *insert the respective column key with its position
						t1.insert((String) record.get(colIndex), new Ref(i, j));

					}

				}

				// *now we are done with creating the tree and need to serialize it into a file
				// :D

				try {
					File file = new File("data/" + strTableName + strColName);

					// Saving of object in a file
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fo);

					// Method for serialization of object
					out.writeObject(t1);

					out.close();
					fo.close();

				} catch (Exception e) {
					throw new DBAppException("Something went wrong with serializing Btree");
				}

			}
			// 0------------------------------------------------------------------------------------------
			else if (colType.contentEquals("java.lang.Boolean")) {
				// *create a BPlus tree with Integer type
				BPTree<Boolean> t1 = new BPTree<Boolean>(treeOrder);

				// *Loop over each page in the table
				for (int i = 0; i < table.getPagesreferences().size(); i++) {

					// *create page object for each page
					Page page = table.read_page(table.getPagesreferences().get(i));

					// *loop over each record in the page and insert it in the tree with its
					// respective place
					Vector<Vector> recordss = page.RecordsGetter();

					for (int j = 0; j < recordss.size(); j++) {

						// *create vector record
						Vector record = recordss.get(j);

						// *insert the respective column key with its position
						t1.insert((Boolean) record.get(colIndex), new Ref(i, j));

					}

				}
				System.out.println(t1.searchAll(true));

				// *now we are done with creating the tree and need to serialize it intoa file
				// :D

				try {
					File file = new File("data/" + strTableName + strColName);

					// Saving of object in a file
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fo);

					// Method for serialization of object
					out.writeObject(t1);

					out.close();
					fo.close();

				} catch (Exception e) {
					throw new DBAppException("Something went wrong with serializing Btree");
				}

			}

		} catch (DBAppException e) {
			throw e;
		} catch (Exception e) {
			throw new DBAppException("Inserting into BPLUS tree went wrong");

		}

	}

	public void createBTreeIndex(String strTableName, String strColName) throws DBAppException {

		if (canBTreeIndex(strTableName, strColName)) {

			// * load table object
			Table table = this.loadTable(strTableName);
			Vector rest = this.deserilizetable(strTableName);
			table.setPagesreferences((List<String>) rest.get(0));
			table.setNumofcreatedpages((int) rest.get(1));
			// *get the type of the column and parse the value into it
			int colIndex = -1;
			for (int i = 0; i < table.getColoumn_names().size(); i++) {
				if (table.getColoumn_names().get(i).equals(strColName)) {
					colIndex = i;
					break;
				}
			}
			// * get the type of the column to create the tree with the same type
			String colType = table.getDatatype().get(colIndex);
			int treeOrder = 0;
			// java.lang.Integer, java.lang.String,
			// java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon

			// * this will go to the properties file to get the maximum node size :D
			try (InputStream input = new FileInputStream("config/DBApp.properties")) {

				Properties prop = new Properties();

				// load a properties

				prop.load(input);

				// get the property value and print it out

				treeOrder = Integer.parseInt(prop.getProperty("NodeSize"));
			} catch (IOException ex) {
				throw new DBAppException("Error with properties file");
			}

			try {

				if (colType.contentEquals("java.lang.Integer")) {
					// *create a BPlus tree with Integer type
					BPTree<Integer> t1 = new BPTree<Integer>(treeOrder);

					// *Loop over each page in the table
					for (int i = 0; i < table.getPagesreferences().size(); i++) {

						// *create page object for each page
						Page page = table.read_page(table.getPagesreferences().get(i));

						// *loop over each record in the page and insert it in the tree with its
						// respective place
						Vector<Vector> recordss = page.RecordsGetter();

						for (int j = 0; j < recordss.size(); j++) {

							// *create vector record
							Vector record = recordss.get(j);

							// *insert the respective column key with its position
							t1.insert((Integer) record.get(colIndex), new Ref(i, j));

						}

					}

					// *now we are done with creating the tree and need to serialize it intoa file
					// :D

					try {
						File file = new File("data/" + strTableName + strColName);

						// Saving of object in a file
						FileOutputStream fo = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fo);

						// Method for serialization of object
						out.writeObject(t1);

						out.close();
						fo.close();

					} catch (Exception e) {
						throw new DBAppException("Something went wrong with serializing Btree");
					}

					// ---------------------------------------------------------------------------------------
				} else if (colType.contentEquals("java.lang.Double")) {
					// *create a BPlus tree with Integer type
					BPTree<Double> t1 = new BPTree<Double>(treeOrder);

					// *Loop over each page in the table
					for (int i = 0; i < table.getPagesreferences().size(); i++) {

						// *create page object for each page
						Page page = table.read_page(table.getPagesreferences().get(i));

						// *loop over each record in the page and insert it in the tree with its
						// respective place
						Vector<Vector> recordss = page.RecordsGetter();

						for (int j = 0; j < recordss.size(); j++) {

							// *create vector record
							Vector record = recordss.get(j);

							// *insert the respective column key with its position
							t1.insert((Double) record.get(colIndex), new Ref(i, j));

						}

					}

					// *now we are done with creating the tree and need to serialize it intoa file
					// :D

					try {
						File file = new File("data/" + strTableName + strColName);

						// Saving of object in a file
						FileOutputStream fo = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fo);

						// Method for serialization of object
						out.writeObject(t1);

						out.close();
						fo.close();

					} catch (Exception e) {
						throw new DBAppException("Something went wrong with serializing Btree");
					}

				}
				// ------------------------------------------------------------------------------
				else if (colType.contentEquals("java.lang.String")) {
					// *create a BPlus tree with Integer type
					BPTree<String> t1 = new BPTree<String>(treeOrder);

					// *Loop over each page in the table
					for (int i = 0; i < table.getPagesreferences().size(); i++) {

						// *create page object for each page
						Page page = table.read_page(table.getPagesreferences().get(i));

						// *loop over each record in the page and insert it in the tree with its
						// respective place
						Vector<Vector> recordss = page.RecordsGetter();

						for (int j = 0; j < recordss.size(); j++) {

							// *create vector record
							Vector record = recordss.get(j);

							// *insert the respective column key with its position
							t1.insert((String) record.get(colIndex), new Ref(i, j));

						}

					}

					// *now we are done with creating the tree and need to serialize it intoa file
					// :D

					try {
						File file = new File("data/" + strTableName + strColName);

						// Saving of object in a file
						FileOutputStream fo = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fo);

						// Method for serialization of object
						out.writeObject(t1);

						out.close();
						fo.close();

					} catch (Exception e) {
						throw new DBAppException("Something went wrong with serializing Btree");
					}

				}
				// 0------------------------------------------------------------------------------------------
				else if (colType.contentEquals("java.lang.Boolean")) {
					// *create a BPlus tree with Integer type
					BPTree<Boolean> t1 = new BPTree<Boolean>(treeOrder);

					// *Loop over each page in the table
					for (int i = 0; i < table.getPagesreferences().size(); i++) {

						// *create page object for each page
						Page page = table.read_page(table.getPagesreferences().get(i));

						// *loop over each record in the page and insert it in the tree with its
						// respective place
						Vector<Vector> recordss = page.RecordsGetter();

						for (int j = 0; j < recordss.size(); j++) {

							// *create vector record
							Vector record = recordss.get(j);

							// *insert the respective column key with its position
							t1.insert((Boolean) record.get(colIndex), new Ref(i, j));

						}

					}

					// *now we are done with creating the tree and need to serialize it intoa file
					// :D

					try {
						File file = new File("data/" + strTableName + strColName);

						// Saving of object in a file
						FileOutputStream fo = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fo);

						// Method for serialization of object
						out.writeObject(t1);

						out.close();
						fo.close();

					} catch (Exception e) {
						throw new DBAppException("Something went wrong with serializing Btree");
					}

				}

				csvIndex(strTableName, strColName);

			} catch (DBAppException e) {
				throw e;
			} catch (Exception e) {
				throw new DBAppException("Inserting into BPLUS tree went wrong");

			}

		}

	}

	// bassel
	public void createRTreeIndex(String strTableName, String strColName) throws DBAppException {

		if (canRTreeIndex(strTableName, strColName)) {

			// * load table object
			Table table = this.loadTable(strTableName);
			Vector rest = this.deserilizetable(strTableName);
			table.setPagesreferences((List<String>) rest.get(0));
			table.setNumofcreatedpages((int) rest.get(1));
			// *get the type of the column and parse the value into it
			int colIndex = -1;
			for (int i = 0; i < table.getColoumn_names().size(); i++) {
				if (table.getColoumn_names().get(i).equals(strColName)) {
					colIndex = i;
					break;
				}
			}
			// * get the type of the column to create the tree with the same type
			String colType = table.getDatatype().get(colIndex);
			int treeOrder = 0;
			// java.lang.Integer, java.lang.String,
			// java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon

			// * this will go to the properties file to get the maximum node size :D
			try (InputStream input = new FileInputStream("config/DBApp.properties")) {

				Properties prop = new Properties();

				// load a properties

				prop.load(input);

				// get the property value and print it out

				treeOrder = Integer.parseInt(prop.getProperty("NodeSize"));
			} catch (IOException ex) {
				throw new DBAppException("Error with properties file");
			}

			if (colType.contentEquals("java.awt.Polygon")) {
				// *create a BPlus tree with Integer type
				RTree<Integer> t1 = new RTree<Integer>(treeOrder);

				// *Loop over each page in the table
				for (int i = 0; i < table.getPagesreferences().size(); i++) {

					// *create page object for each page
					Page page = table.read_page(table.getPagesreferences().get(i));

					// *loop over each record in the page and insert it in the tree with its
					// respective place
					Vector<Vector> recordss = page.RecordsGetter();

					for (int j = 0; j < recordss.size(); j++) {

						// *create vector record
						Vector record = recordss.get(j);

						// *insert the respective column key with its position
						Object u = record.get(colIndex);
						Integer area = u instanceof Polygon ? getPolyArea((Polygon) u) : getPolyArea((String) u);
						t1.insert(area, new RTRef(i, j));

					}

				}

				// *now we are done with creating the tree and need to serialize it into a file
				// :D

				try {
					File file = new File("data/" + strTableName + strColName);

					// Saving of object in a file
					FileOutputStream fo = new FileOutputStream(file);
					ObjectOutputStream out = new ObjectOutputStream(fo);

					// Method for serialization of object
					out.writeObject(t1);

					out.close();
					fo.close();

					csvIndex(strTableName, strColName);

				} catch (Exception e) {
					throw new DBAppException("Something went wrong with serializing Rtree");
				}

			}

		}

	}

	public Integer getPolyArea(Polygon p) {
		int p1area = 0;
		Dimension dim1 = p.getBounds().getSize();
		p1area = dim1.width * dim1.height;
		return p1area;
	}

	public Integer getPolyArea(String s) throws DBAppException {
		int p1area = 0;
		Polygon p1 = makePolygon(s);
		Dimension dim1 = p1.getBounds().getSize();
		p1area = dim1.width * dim1.height;
		return p1area;
	}

	public void insertIntoTable(String tablename, Hashtable<String, Object> htblColNameValue) throws Exception {
		Table table = this.loadTable(tablename);
		Vector rest = this.deserilizetable(tablename);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int) rest.get(1));
		if (this.missorins(table, htblColNameValue) == true) {
			throw (new DBAppException("missing or inconsisitant data"));
		} else {

			Vector record = new Vector();
			for (int i = 0; i < table.getIskey().size(); i++) {
				if ((boolean) table.getIskey().get(i) == true) {

					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					// System.out.println(record);

					break;
				}
			}
			for (int i = 0; i < table.getIskey().size() - 1; i++) {
				if ((boolean) table.getIskey().get(i) == false) {

					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));

				}
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();

			record.add(formatter.format(date) + "");
			table.inserttotablemain(record);
			// System.out.print(record);
		}
		for (int i = 0; i < table.getIsindexed().size(); i++) {
			if ((boolean) table.getIsindexed().get(i) == true) {
				if (table.getDatatype().get(i).equals("java.awt.Polygon")) {
					this.refreshRTree(table.getName(), (String) table.getColoumn_names().get(i));
				} else {
					this.refreshBTree(table.getName(), (String) table.getColoumn_names().get(i));
				}
			}
		}

	}

	public static List<String> deserilizetableOLD(String tblname) {
		try {
			// System.out.println("testo.txt");

			ObjectInputStream o = new ObjectInputStream(new FileInputStream(tblname + ".bin"));
			Table foo2 = (Table) o.readObject();
			// System.out.print(foo2.getPagesreferences().get(0));
			o.close();

			return foo2.getPagesreferences();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			List<String> references = new ArrayList<String>();
			return references;
		}
	}

	public void updateTable(String strTableName, String strKey, Hashtable<String, Object> htblColNameValue)
			throws Exception {
		/*
		 * bos yasta enta lama betgigi te update betecheck 3ala column tab3an we betgib
		 * el values
		 * 
		 * el haitghaiar howa enak lazem teshof el awel el column da indexed wala mesh
		 * indexed ya3ni 3ando tree wala la2 law ah fa enta hatro7 tegib el tree beta3to
		 * we te3mel search biha tab3an hat2oli ezai ha2olak awalan lazem teb2a 3aref
		 * type el column ya3ni Integer wala Double wala eh bezabt now ma3ak el type hat
		 * create tree keda BPTree<Integer> tree;
		 * 
		 * now enta 3awz el tree beta3tak teb2a zayi el tree el fel memory fa hate3mel
		 * gettree method tree=getBPlusTree(strTableName, strColName)
		 * 
		 * BUT fi 7aga el method di betraga3 object fa hate3melha type cast le type el
		 * tree beta3tak tree=getBPlusTree(strTableName, strColName);
		 * 
		 * keda enta ma3ak el tree delwa2ti 3awz tegib el records el maslan 3andohom id
		 * 3
		 * 
		 * Vector<Vector<Integer>>=tree.searchAll(new Integer(3));
		 * 
		 * da hairaga3lak el indexes beta3t kol el records el fel tree el be 3 bel
		 * manzar da record record record [ [page number,index in page], [page
		 * number,index in page], [page number,index in page] ]
		 * 
		 * [ [2,3],[2,5],[3,4] ]
		 * 
		 * ya3ni hatro7 array page refrences we tegeib refrences.get(page number) haidik
		 * el page el fiha awel record ba3d keda hate3mel men el page .get(index in
		 * page) hairaga3lak el record bezabt
		 * 
		 * tab3an lazem ta5od balak men el diffrent types we en el tree beteb2a 3ala
		 * column wa7ed mesh 3al table kolo we momken columns yeb2a liha tree we momken
		 * la2 plz test carefully 3ashan 3aleha daragat ketir
		 * 
		 * ba3d ma teupdate records beta3t column mo3aian plz et2aked hal el column da
		 * isIndexed? 3ashan law ah yeb2a howa 3ando bplus tree lazem tetghayar ma3ah fa
		 * hat call refreshBTree(String strTableName, String strColName)
		 * 
		 * 
		 * 7awel bas plz te test el tree lewa7daha abl ai 7aga we tefham heia
		 * betraga3lak eh we te3melo print we keda heia el donia sahla fel trees el
		 * mohem bas enak testa3mel amaken el records menaha we law ghayart ay column
		 * teshof law indexed ghayar el tree beta3to law la2 yeb2a 5alas dont change
		 * 
		 * we by the ay lazem tezawed el 7eta di fel linear search bardo ya3ni ba3d ma
		 * teupdate kol column zawed ta7tih checks men el table calss en law el column
		 * da indexed hat call refresh Bplustree
		 * 
		 * 
		 * 
		 * if its a polygon dont create b+tree we will implement the R tree in that case
		 * so just put a comment //create r tree
		 * 
		 * 
		 */

		Table table = this.loadTable(strTableName);
		table.setPagesreferences(this.deserilizetableOLD(strTableName));
		// first we check that the data entered in the hash table is consistent
		if (this.inCons(table, htblColNameValue)) {
			throw (new DBAppException("inconsisitant data"));
		}
		// Secondly, we check mainly if there is missing data or the hash table contains
		// specific columns not the full record
		if (this.missorins(table, htblColNameValue) == true) {
			// first we create a new hash table containing nulls or empty spaces in the
			// missing columns
			Hashtable<String, Object> tempT = new Hashtable();
			for (int i = 0; i < table.getColoumn_names().size(); i++) {
				if (htblColNameValue.containsKey(table.getColoumn_names().get(i))) {
					tempT.put(table.getColoumn_names().get(i), htblColNameValue.get(table.getColoumn_names().get(i)));
				} else {
					tempT.put(table.getColoumn_names().get(i), "");
				}
			}
			System.out.println(tempT);
			// Then, we get the new record format to compare it to the old record currently
			// in the table
			Vector newRecord = new Vector();
			for (int i = 0; i < table.getIskey().size() - 1; i++) {
				if (table.getIskey().get(i) == true) {
					newRecord.add(tempT.get(table.getColoumn_names().get(i)));
					break;
				}
			}
			for (int i = 0; i < table.getIskey().size() - 1; i++) {
				if (table.getIskey().get(i) == false) {
					newRecord.add(tempT.get(table.getColoumn_names().get(i)));
				}
			}
			System.out.println(newRecord);

			// here we compare the new record with the old one
			for (int i = 0; i < table.getPagesreferences().size(); i++) {
				Page currentPage = table.read_page(table.getPagesreferences().get(i));
				for (int j = 0; j < currentPage.RecordsGetter().size(); j++) {
					// the old record to compare
					Vector temp = (Vector) currentPage.RecordsGetter().get(j);
					System.out.println("temp at " + j + " :-");
					recordDisplayer(temp);
					if (isPolygon(strKey)) {
						Polygon s = makePolygon(strKey);
						makeString(s);
						System.out.println();
						makeString((Polygon) temp.get(0));
						System.out.println("");
						System.out.println(polygonCompare(s, (Polygon) temp.get(0)));

						if (polygonCompare(s, (Polygon) temp.get(0)) == 0) {
							table.removefromtable(temp);

							System.out.println("j :- " + j);
							// this.displayer(table.getName());
							System.out.println("");
							// System.out.println("hna");
							for (int k = 0; k < newRecord.size(); k++) {
								// we only change the changed or the entered columns to avoid nulls
								if (newRecord.get(k) == "") {
									continue;
								} else {
									if (isPolygon((String) newRecord.get(k))) {
										// we modify the old record
										Polygon newP = makePolygon((String) newRecord.get(k));
										temp.set(k, newP);

										System.out.println("hna");
									} else
										temp.set(k, newRecord.get(k));
								}
								// System.out.println(temp);
								// we add the old record after modifying it

								table.inserttotable(temp);

							}
							// table.inserttotable(temp);
							continue;
						}
						continue;
					}

					if ((temp.get(0).toString()).equals(strKey)) {
						// here we found the targeted record so we remove it form the record
						table.removefromtable(temp);

						for (int k = 0; k < newRecord.size(); k++) {
							// we only change the changed or the entered columns to avoid nulls
							if (newRecord.get(k) == "") {
								continue;
							} else {
								if (isPolygon((String) newRecord.get(k))) {
									// we modify the old record
									Polygon newP = makePolygon((String) newRecord.get(k));
									temp.set(k, newP);
									System.out.println("hna");
								} else
									temp.set(k, newRecord.get(k));
							}
							// we add the old record after modifying it

							table.inserttotable(temp);

						}
					}
				}
			}
		} else {
			Vector newRecord = new Vector();
			for (int i = 0; i < table.getIskey().size() - 1; i++) {
				if (table.getIskey().get(i) == true) {
					newRecord.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					// System.out.println(record);
					break;
				}
			}
			for (int i = 0; i < table.getIskey().size() - 1; i++) {
				if (table.getIskey().get(i) == false) {
					newRecord.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					// System.out.print(newRecord);
					// break;
				}
			}
			// System.out.println(oldRecord);
			// System.out.println(newRecord);
		}

	}

	public static Vector<Vector<Integer>> helpergetcommon(Vector<Vector<Integer>> x, Vector<Vector<Integer>> y) {
		Vector<Vector<Integer>> z = new Vector<Vector<Integer>>();
		for (int i = 0; i < x.size(); i++) {
			if (y.contains(x.get(i))) {
				if (!z.contains(x.get(i)))
					z.add(x.get(i));
			}
		}
		return z;

	}

	public Vector makeRecord(Table table, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		String tablename = table.getName();
		Vector rest = this.deserilizetable(tablename);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int) rest.get(1));
		Vector record = new Vector();
		for (int i = 0; i < table.getIskey().size(); i++) {
			if (table.getIskey().get(i) == true) {
				if (table.getDatatype().get(i).equals("java.awt.Polygon")) {
					if (htblColNameValue.get(table.getColoumn_names().get(i)) != null) {
						PolygonE temp = this
								.makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
						record.add(temp);
					} else {
						PolygonE temp = null;
					}
				} else {
					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
				}
				break;
			}

		}
		for (int i = 0; i < table.getIskey().size(); i++) {
			if (table.getIskey().get(i) == false) {
				if (table.getDatatype().get(i).equals("java.awt.Polygon")) {
					if (htblColNameValue.get(table.getColoumn_names().get(i)) != null) {
						PolygonE temp = this
								.makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
						record.add(temp);
					} else {
						PolygonE temp = null;
					}
				} else {
					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
				}
			}
		}
		return record;

	}

	public static boolean hadindexed(Table table, Vector record) throws DBAppException, Exception {
		boolean hadindexed = false;
		String tablename = table.getName();
		for (int i = 0; i < record.size(); i++) {
			if (table.getIsindexed().get(i) && record.get(i) != null) {
				hadindexed = true;
			}
		}
		return hadindexed;
	}

	public void deleteFromTable(String tablename, Hashtable<String, Object> htblColNameValue) throws Exception {
		// Vector<Object> dellrecord = new Vector<Object>();
		Table table = this.loadTable(tablename);
		helperrefreshtrees(table);

		Vector record = makeRecord(table, htblColNameValue);
		Vector<Vector<Integer>> recordindexes = recordindexes(table, record);
		// remove record using indexes
		int counter = 0;
		if (hadindexed(table, record)) {
			helperrefreshtrees(table);
			while (!recordindexes(table, record).isEmpty() && recordindexes.size() > counter) {
				if (table.removeindfromtable(record, recordindexes(table, record).get(counter))) {
					helperrefreshtrees(table);
					counter = 0;
				} else
					counter++;
			}

			// remove record by looping through the whole table
		} else {
			table.removefromtable(record);
		}
		helperrefreshtrees(table);
	}

	public void helperrefreshtrees(Table table) throws DBAppException {
		for (int i = 0; i < table.getColoumn_names().size(); i++) {
			if (table.getIsindexed().get(i) == true) {
				if (table.getDatatype().get(i) == "java.awt.Polygon") {
					refreshRTree(table.getName(), table.getColoumn_names().get(i));

				} else
					refreshBTree(table.getName(), table.getColoumn_names().get(i));
			}
		}
	}

	public Vector<Vector<Integer>> recordindexes(Table table, Vector record) throws DBAppException, Exception {
		BPTree btree;
		RTree rtree;
		boolean hadindexed = false;
		String tablename = table.getName();
		Vector<Vector<Integer>> recordindexes = new Vector<Vector<Integer>>();
		boolean firsttime = true;
		for (int i = 0; i < record.size(); i++) {
			if (table.getIsindexed().get(i) && record.get(i) != null) {
				hadindexed = true;
				if (getBPlusTree(tablename, table.getColoumn_names().get(i)) instanceof BPTree) {
					btree = (BPTree) getBPlusTree(tablename, table.getColoumn_names().get(i));
					if (firsttime) {
						recordindexes = btree.searchAll((T) record.get(i));
					} else {
						recordindexes = helpergetcommon(recordindexes, btree.searchAll((T) record.get(i)));
					}
				} else if (getRTree(tablename, table.getColoumn_names().get(i)) instanceof RTree) {
					rtree = (RTree) getBPlusTree(tablename, table.getColoumn_names().get(i));
					if (firsttime) {
						recordindexes = rtree.searchAll((T) new Integer(getPolyArea((PolygonE) record.get(i))));
					} else {
						recordindexes = helpergetcommon(recordindexes,
								rtree.searchAll((T) new Integer(getPolyArea((PolygonE) record.get(i)))));
					}
				}
			}
			firsttime = false;
		}
		return recordindexes;
	}
	// public void deleteFromTable(String tablename, Hashtable<String, Object>
	// htblColNameValue) throws Exception {
	// Vector<Vector<Integer>> pos1 = new Vector<Vector<Integer>>();
	// boolean all = false;
	// boolean foundindexed = false;
	// Vector<Vector<Integer>> pos2 = new Vector<Vector<Integer>>();
	// Vector<Vector<Integer>> bigger = new Vector<Vector<Integer>>();
	// Vector<Vector<Integer>> smaller = new Vector<Vector<Integer>>();
	// // Vector<Object> dellrecord = new Vector<Object>();
	// Table table = this.loadTable(tablename);
	// Vector rest = this.deserilizetable(tablename);
	// table.setPagesreferences((List<String>) rest.get(0));
	// table.setNumofcreatedpages((int) rest.get(1));
	// BPTree btree;
	// RTree rtree;
	// Vector search = new Vector<Vector<Integer>>();

	// // System.out.println("done");
	// Vector record = new Vector();
	// for (int i = 0; i < table.getIskey().size(); i++) {
	// if (htblColNameValue.get(table.getColoumn_names().get(i)) != null) {
	// if (table.getIskey().get(i) == true) {
	// if (table.getDatatype().get(i).equals("java.awt.Polygon")) {
	// PolygonE temp = this
	// .makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
	// record.add(temp);
	// } else {
	// record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
	// // System.out.println(record);
	// }
	// break;
	// }
	// }
	// }
	// for (int i = 0; i < table.getIskey().size() - 1; i++) {
	// if (htblColNameValue.get(table.getColoumn_names().get(i)) != null) {
	// if (table.getIskey().get(i) == false) {
	// if (table.getDatatype().get(i).equals("java.awt.Polygon")) {
	// PolygonE temp = this
	// .makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
	// record.add(temp);
	// } else {
	// record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
	// }
	// }
	// }
	// }
	// for (int i = 0; i < record.size(); i++) {
	// if (table.getIsindexed().get(i) && record.get(i) != null) {
	// foundindexed = true;
	// pos2.clear();search
	// pos2.addAll(pos1);
	// pos1.clear();
	// if (getBPlusTree(tablename, table.getColoumn_names().get(i)) instanceof
	// BPTree) {
	// btree = (BPTree) getBPlusTree(tablename, table.getColoumn_names().get(i));
	// search.clear();
	// search.addAll(btree.searchAll((T) record.get(i)));
	// if (search.size() > pos2.size()) {
	// bigger.clear();
	// bigger.addAll(search);
	// smaller.clear();
	// smaller.addAll(pos2);
	// } else {
	// bigger.clear();
	// bigger.addAll(pos2);
	// smaller.clear();
	// smaller.addAll(search);
	// }
	// for (int j = 0; j < bigger.size(); j++) {
	// if (!smaller.contains(bigger.get(j))) {
	// smaller.remove(bigger.get(j));
	// }
	// }
	// pos1.clear();
	// pos1.addAll(smaller);
	// pos2.clear();
	// pos2.addAll(bigger);
	// } else if (getRTree(tablename, table.getColoumn_names().get(i)) instanceof
	// RTree) {
	// rtree = (RTree) getBPlusTree(tablename, table.getColoumn_names().get(i));
	// search.clear();
	// search.addAll(rtree.searchAll(staticgetpolyInteger((PolygonE)
	// record.get(i))));
	// if (search.size() > pos2.size()) {
	// bigger.clear();
	// bigger.addAll(search);
	// smaller.clear();
	// smaller.addAll(pos2);
	// } else {
	// bigger.clear();
	// bigger.addAll(pos2);
	// smaller.clear();
	// smaller.addAll(search);
	// }
	// for (int j = 0; j < bigger.size(); j++) {
	// if (!smaller.contains(bigger.get(j))) {
	// smaller.remove(bigger.get(j));
	// }
	// }
	// pos1.clear();
	// pos1.addAll(smaller);
	// pos2.clear();
	// pos2.addAll(bigger);
	// }
	// } else
	// all = true;

	// }

	// if (!pos2.isEmpty()) {
	// for (Vector<Integer> orgrecord : pos2) {
	// table.removeindfromtable(record, orgrecord);
	// }
	// } else if (!foundindexed) {
	// table.removefromtable(record);

	// }
	// for (int i = 0; i < table.getColoumn_names().size(); i++) {
	// if (table.getIsindexed().get(i) == true) {
	// if (table.getDatatype().get(i) == "java.awt.Polygon") {
	// refreshRTree(table.getName(), table.getColoumn_names().get(i));

	// } else
	// refreshBTree(table.getName(), table.getColoumn_names().get(i));
	// }
	// }
	// }
	public static Object StaticgetTree(String strTableName, String strColName) throws Exception {
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

	public static Integer staticgetpolyInteger(PolygonE object) {
		int area = 0;
		Dimension dim1 = object.getBounds().getSize();
		int p1area = dim1.width * dim1.height;

		return area;
	}

	public String Gettime() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return formatter.format(date);
	}

	public static void makeString(Polygon s) {
		System.out.print("[");

		for (int k = 0; k < s.npoints; k++) {
			System.out.print("(" + s.xpoints[k] + "," + s.ypoints[k] + ") ");
		}
		System.out.print("], ");
	}

	public static void recordDisplayer(Vector record) {
		System.out.print("[");
		for (int j = 0; j < record.size(); j++) {
			// System.out.println(i);
			if (record.get(j) instanceof Polygon) {
				System.out.print("[");
				Polygon currentR = (Polygon) record.get(j);
				for (int k = 0; k < currentR.npoints; k++) {
					System.out.print("(" + currentR.xpoints[k] + "," + currentR.ypoints[k] + ") ");
				}
				System.out.print("], ");
			} else
				System.out.print(record.get(j) + " , ");
		}
		System.out.println("]");
		System.out.println("");
	}

	public void displayer(String tablename) throws DBAppException {
		Table table = this.loadTable(tablename);
		table.setPagesreferences(this.deserilizetableOLD(tablename));
		System.out.println(table.getPagesreferences().size());
		if (table.getPagesreferences().size() == 0) {
			System.out.println("This page is empty");
			return;
		}

		for (int i = 0; i < table.getPagesreferences().size(); i++) {

			Page page = (Page) table.read_page((String) (table.getPagesreferences().get(i)));

			page.display();
		}

	}

	public static boolean isPolygon(String s) throws DBAppException {
		// this method is the same as makePolygon the only difference is that it just
		// checks if the entered string can be polygon or not

		Polygon p = new Polygon();
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
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return true;
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

	// if u want to pass two polygons
	public static int polygonCompare(Polygon p1, Polygon p2) {
		// if the first polygon is bigger it returns 1;
		// if the 2nd polygon is bigger it returns -1
		// if they are equal it returns 0
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

	// if u want to pass two strings
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

	// ---------------------------------------------------------------------------------------------------
	public static boolean canBTreeIndex(String tableName, String colName) throws DBAppException {
		File oldFile = new File("data/metadata.csv");
		String tbname = "";
		String cname = "";
		String ctype = "";
		String iskey = "";
		String isIndexed = "";
		boolean tablefound = false;
		boolean colfound = false;
		boolean wrongIndex = false;
		boolean alreadyIndexed = false;
		String bobo = "";
		try {
			Scanner x = new Scanner(oldFile);
			x.useDelimiter("[,\n]");

			while (x.hasNext()) {
				tbname = x.next();
				cname = x.next();
				ctype = x.next();
				iskey = x.next();
				isIndexed = x.next();
				bobo = tbname + " " + cname + " " + ctype + " " + iskey + " " + isIndexed;
				// System.out.println(bobo);

				if (tbname.equals(tableName)) {
					tablefound = true;
					if (cname.equals(colName)) {
						colfound = true;
						if (ctype.equals("java.util.Polygon"))
							wrongIndex = true;
						else {
							if (isIndexed.contentEquals("False")) {
								return true;
							} else {
								alreadyIndexed = true;
							}
						}
					}
				}
				// if it didn't write the new indexed column then here it writes the normal
				// columns

			}
		} catch (Exception e) {
			throw new DBAppException("Indexing error");
		}

		if (tablefound == false) {
			throw new DBAppException("Table you are trying to index doesnt exist");
		} else {
			if (colfound == false)
				throw new DBAppException("Column you are trying to index doesnt exist");
			else {
				if (wrongIndex)
					throw new DBAppException("Cannot BTree index a Polygon type");
				else if (alreadyIndexed)
					throw new DBAppException("Column " + colName + " is already indexed");
			}
		}
		return false;
	}

	public static boolean canRTreeIndex(String tableName, String colName) throws DBAppException {
		File oldFile = new File("data/metadata.csv");
		String tbname = "";
		String cname = "";
		String ctype = "";
		String iskey = "";
		String isIndexed = "";
		boolean tablefound = false;
		boolean colfound = false;
		boolean wrongIndex = false;
		boolean alreadyIndexed = false;
		String bobo = "";
		try {
			Scanner x = new Scanner(oldFile);
			x.useDelimiter("[,\n]");

			while (x.hasNext()) {
				tbname = x.next();
				cname = x.next();
				ctype = x.next();
				iskey = x.next();
				isIndexed = x.next();
				bobo = tbname + " " + cname + " " + ctype + " " + iskey + " " + isIndexed;
				// System.out.println(bobo);

				if (tbname.equals(tableName)) {
					tablefound = true;
					if (cname.equals(colName)) {
						colfound = true;
						if (!ctype.equals("java.awt.Polygon"))
							wrongIndex = true;
						else {
							if (isIndexed.contentEquals("False")) {
								return true;
							} else {
								alreadyIndexed = true;
							}
						}
					}
				}
				// if it didn't write the new indexed column then here it writes the normal
				// columns

			}
		} catch (Exception e) {
			throw new DBAppException("Indexing error");
		}

		if (tablefound == false) {
			throw new DBAppException("Table you are trying to index doesnt exist");
		} else {
			if (colfound == false)
				throw new DBAppException("Column you are trying to index doesnt exist");
			else {
				if (wrongIndex)
					throw new DBAppException("Cannot RTree index except Polygon type");
				else if (alreadyIndexed)
					throw new DBAppException("Column " + colName + " is already indexed");
			}
		}

		return false;
	}

	public static void csvIndex(String tableName, String colName) throws DBAppException {

		String tempFile = "data/temp.csv";
		File oldFile = new File("data/metadata.csv");
		File newFile = new File(tempFile);
		String tbname = "";
		String cname = "";
		String ctype = "";
		String iskey = "";
		String isIndexed = "";
		boolean tablefound = false;
		boolean colfound = false;
		boolean wrongIndex = false;
		boolean alreadyIndexed = false;
		String bobo = "";
		try {
			FileWriter fw = new FileWriter(tempFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			Scanner x = new Scanner(oldFile);
			x.useDelimiter("[,\n]");

			while (x.hasNext()) {
				tbname = x.next();
				cname = x.next();
				ctype = x.next();
				iskey = x.next();
				isIndexed = x.next();
				bobo = tbname + " " + cname + " " + ctype + " " + iskey + " " + isIndexed;
				// System.out.println(bobo);

				if (tbname.equals(tableName) && cname.equals(colName)) {
					pw.write(tbname + "," + cname + "," + ctype + "," + iskey + "," + "True" + "\n");
				} else {
					pw.write(tbname + "," + cname + "," + ctype + "," + iskey + "," + isIndexed + "\n");
				}
			}
			x.close();
			pw.flush();
			pw.close();
			bw.close();
			fw.close();

			// *now all my new data is in Temp so I will return it to metadata then delete
			// the temp file
			FileWriter ff = new FileWriter(oldFile);
			BufferedWriter bb = new BufferedWriter(ff);
			PrintWriter pp = new PrintWriter(bb);
			Scanner xx = new Scanner(newFile);
			xx.useDelimiter("[,\n]");

			while (xx.hasNext()) {
				tbname = xx.next();
				cname = xx.next();
				ctype = xx.next();
				iskey = xx.next();
				isIndexed = xx.next();
				pp.write(tbname + "," + cname + "," + ctype + "," + iskey + "," + isIndexed + "\n");

			}

			xx.close();
			pp.close();
			bb.close();
			ff.close();
			System.gc();
			newFile.delete();

		} catch (Exception e) {
			throw new DBAppException("Indexing error");
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public static Vector binarySelect(int keyIndex, Vector page, Object value, Vector<Vector<?>> records,
			String operator) throws DBAppException {
		// el method momken teban kebira awi fel awel bas heits its barely the same code
		// repeated ketir
		// nafs el code repeated ma3 e5telaf el operators 3ashan <,>,>=,<=,=
		// fa dont worry its just repetitive badal ma3mel method le kol operator

		int first = 0;
		int last = page.size() - 1;
		int mid = (first + last) / 2;
		boolean found = false;
		boolean right = false;
		boolean left = false;
		page.size();

		if (operator.equals("=")) {
			while (first <= last) {
				Vector record = (Vector) page.get(mid);
				Object midValue = record.get(keyIndex);
				if (comparer(record.get(keyIndex), value) < 0) {
					first = mid + 1;
				} else if (comparer(record.get(keyIndex), value) == 0) {
					found = true;
					records.add(record);
					break;
				} else {
					last = mid - 1;
				}
				mid = (first + last) / 2;

			}
			if (found) {
				int moveright = mid + 1;
				int moveleft = mid - 1;
				Vector record = (Vector) page.get(mid);
				if (moveright < page.size())
					record = (Vector) page.get(moveright);
				while (comparer(record.get(keyIndex), value) == 0 && moveright < page.size()) {
					records.add(record);
					moveright = moveright + 1;
					if (moveright < page.size())
						record = (Vector) page.get(moveright);
				}

				if (moveleft > -1)
					record = (Vector) page.get(moveleft);
				while (comparer(record.get(keyIndex), value) == 0 && moveleft > -1) {
					records.add(record);
					moveleft = moveleft - 1;
					if (moveleft > -1)
						record = (Vector) page.get(moveleft);
				}

			}

		}

		if (operator.equals(">")) {
			while (first <= last) {
				Vector record = (Vector) page.get(mid);
				Object midValue = record.get(keyIndex);
				if (comparer(record.get(keyIndex), value) < 0) {
					first = mid + 1;
				} else if (comparer(record.get(keyIndex), value) > 0) {
					found = true;
					records.add(record);
					break;
				} else {
					last = mid - 1;
				}
				mid = (first + last) / 2;

			}
			if (found) {
				int moveright = mid + 1;
				int moveleft = mid - 1;
				Vector record = (Vector) page.get(mid);
				if (moveright < page.size())
					record = (Vector) page.get(moveright);
				while (comparer(record.get(keyIndex), value) > 0 && moveright < page.size()) {
					records.add(record);
					moveright = moveright + 1;
					if (moveright < page.size())
						record = (Vector) page.get(moveright);
				}

				if (moveleft > -1)
					record = (Vector) page.get(moveleft);
				while (comparer(record.get(keyIndex), value) > 0 && moveleft > -1) {
					records.add(record);
					moveleft = moveleft - 1;
					if (moveleft > -1)
						record = (Vector) page.get(moveleft);
				}

			}

		}

		if (operator.equals(">=")) {
			while (first <= last) {
				Vector record = (Vector) page.get(mid);
				Object midValue = record.get(keyIndex);
				if (comparer(record.get(keyIndex), value) < 0) {
					first = mid + 1;
				} else if (comparer(record.get(keyIndex), value) >= 0) {
					found = true;
					records.add(record);
					break;
				} else {
					last = mid - 1;
				}
				mid = (first + last) / 2;

			}
			if (found) {
				int moveright = mid + 1;
				int moveleft = mid - 1;
				Vector record = (Vector) page.get(mid);
				if (moveright < page.size())
					record = (Vector) page.get(moveright);
				while (comparer(record.get(keyIndex), value) >= 0 && moveright < page.size()) {
					records.add(record);
					moveright = moveright + 1;
					if (moveright < page.size())
						record = (Vector) page.get(moveright);
				}

				if (moveleft > -1)
					record = (Vector) page.get(moveleft);
				while (comparer(record.get(keyIndex), value) >= 0 && moveleft > -1) {
					records.add(record);
					moveleft = moveleft - 1;
					if (moveleft > -1)
						record = (Vector) page.get(moveleft);
				}

			}

		}

		if (operator.equals("<")) {
			while (first <= last) {
				Vector record = (Vector) page.get(mid);
				Object midValue = record.get(keyIndex);
				if (comparer(record.get(keyIndex), value) < 0) {
					found = true;
					records.add(record);
					break;

				} else if (comparer(record.get(keyIndex), value) >= 0) {
					last = mid - 1;
				} else {
					last = mid - 1;
				}
				mid = (first + last) / 2;

			}
			if (found) {
				int moveright = mid + 1;
				int moveleft = mid - 1;
				Vector record = (Vector) page.get(mid);
				if (moveright < page.size())
					record = (Vector) page.get(moveright);
				while (comparer(record.get(keyIndex), value) < 0 && moveright < page.size()) {
					records.add(record);
					moveright = moveright + 1;
					if (moveright < page.size())
						record = (Vector) page.get(moveright);
				}

				if (moveleft > -1)
					record = (Vector) page.get(moveleft);
				while (comparer(record.get(keyIndex), value) < 0 && moveleft > -1) {
					records.add(record);
					moveleft = moveleft - 1;
					if (moveleft > -1)
						record = (Vector) page.get(moveleft);
				}

			}

		}

		if (operator.equals("<=")) {
			while (first <= last) {
				Vector record = (Vector) page.get(mid);
				Object midValue = record.get(keyIndex);
				if (comparer(record.get(keyIndex), value) <= 0) {
					found = true;
					records.add(record);
					break;

				} else if (comparer(record.get(keyIndex), value) >= 0) {
					last = mid - 1;
				} else {
					last = mid - 1;
				}
				mid = (first + last) / 2;

			}
			if (found) {
				int moveright = mid + 1;
				int moveleft = mid - 1;
				Vector record = (Vector) page.get(mid);
				if (moveright < page.size())
					record = (Vector) page.get(moveright);
				while (comparer(record.get(keyIndex), value) <= 0 && moveright < page.size()) {
					records.add(record);
					moveright = moveright + 1;
					if (moveright < page.size())
						record = (Vector) page.get(moveright);
				}

				if (moveleft > -1)
					record = (Vector) page.get(moveleft);
				while (comparer(record.get(keyIndex), value) <= 0 && moveleft > -1) {
					records.add(record);
					moveleft = moveleft - 1;
					if (moveleft > -1)
						record = (Vector) page.get(moveleft);
				}

			}

		}

		return records;

	}

	public Vector newBPSelect(String tableName, String colName, String operator, Object value, List<String> pages)
			throws Exception {

		Vector<Vector<Integer>> y = new Vector<Vector<Integer>>();
		Vector results = new Vector();
		BPTree<T> t;
		Table table = this.loadTable(tableName);
		Vector rest = this.deserilizetable(tableName);
		table.setPagesreferences(pages);
		table.setNumofcreatedpages((int) rest.get(1));
		int colIndex = 0;
		try {

			for (int j = 0; j < table.getColoumn_names().size(); j++) {
				if (table.getColoumn_names().get(j).equals(colName)) {
					colIndex = j;
					break;
				}
			}
			String colType = table.getDatatype().get(colIndex);
			if (colType.equals("java.awt.Polygon")) {
				RTree<T> r = getRTree(tableName, colName);
				switch (operator) {
					case "=":
						y = r.searchAll((T) value);
						break;

					case "<":
						y = r.getLessThan((T) value);
						break;

					case "<=":
						y = r.getLessThanOrEqual((T) value);
						break;

					case ">":
						y = r.getMoreThan((T) value);
						break;

					case ">=":
						y = r.getMoreThanOrEqual((T) value);
						break;

					case "!=":
						y = r.getNotEqual((T) value);
						break;
					default:
						throw new DBAppException("Unvalid operator");

				}
			} else {
				BPTree<T> tree = (BPTree<T>) getBPlusTree(tableName, colName);
				switch (operator) {
					case "=":
						y = tree.searchAll((T) value);
						break;

					case "<":
						y = tree.getLessThan((T) value);
						break;

					case "<=":
						y = tree.getLessThanOrEqual((T) value);
						break;

					case ">":
						y = tree.getMoreThan((T) value);
						break;

					case ">=":
						y = tree.getMoreThanOrEqual((T) value);
						break;

					case "!=":
						y = tree.getNotEqual((T) value);
						break;
					default:
						throw new DBAppException("Unvalid operator");

				}
			}

			for (int i = 0; i < y.size(); i++) {
				int pageNo = y.get(i).get(0);
				int place = y.get(i).get(1);
				Page page = table.read_page(pages.get(pageNo));
				results.add(page.RecordsGetter().get(place));

			}
			return results;
		} catch (DBAppException e) {
			throw e;
		} catch (Exception e) {
			throw e;
			// throw new DBAppException("column is indexed but something went wrong with
			// select");
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector selectHelper(String tableName, String colName, String operator, Object value) throws DBAppException {

		try {
			Vector results = new Vector();

			// *check if the column is indexed
			boolean isIndex = false;
			// *use indexedSelect
			boolean isKey = false;
			// *the value type is the same as the column type
			// *load the table
			Table table = this.loadTable(tableName);
			Vector rest = this.deserilizetable(tableName);
			table.setPagesreferences((List<String>) rest.get(0));
			table.setNumofcreatedpages((int) rest.get(1));
			// *get the type of the column and parse the value into it
			int colIndex = -1;
			for (int i = 0; i < table.getColoumn_names().size(); i++) {
				if (table.getColoumn_names().get(i).equals(colName)) {
					colIndex = i;
					break;
				}
			}

			isIndex = table.getIsindexed().get(colIndex);
			isKey = table.getIskey().get(colIndex);

			String colType = table.getDatatype().get(colIndex);
			Page p;
			Vector v;

			// *loop on pages
			for (int i = 0; i < table.getPagesreferences().size(); i++) {
				p = table.read_page(table.getPagesreferences().get(i));
				v = p.RecordsGetter();// *got the records in one page

				if (isKey && !(operator.equals("!="))) {
					// *if its on the key we should binary search inside each page :`)

					results = binarySelect(colIndex, v, value, results, operator);

					//

				} else {
					// *just linear search and check the typecast
					for (int j = 0; j < v.size(); j++) {
						Vector record = (Vector) v.get(j);

						if (operator.equals("=")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) == 0)
								results.add(record);
						} else if (operator.equals("!=")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) != 0)
								results.add(record);
						} else if (operator.equals("<=")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) == 0
									|| comparer(((Vector) v.get(j)).get(colIndex), value) < 0)
								results.add(record);
						} else if (operator.equals(">=")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) == 0
									|| comparer(((Vector) v.get(j)).get(colIndex), value) > 0)
								results.add(record);
						} else if (operator.equals(">")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) > 0)
								results.add(record);
						} else if (operator.equals("<")) {
							if (comparer(((Vector) v.get(j)).get(colIndex), value) < 0)
								results.add(record);
						}

					}

				}

			}

			return results;
		} catch (Exception e) {
			throw new DBAppException("Something went wrong with select helper");
		}
	}

	public static int comparer(Object x, Object y) throws DBAppException {

		try {
			if (x instanceof Integer) {
				return ((Integer) x).compareTo((Integer) y);

			}
			if (x instanceof Boolean) {
				return ((Boolean) x).compareTo((Boolean) y);
			}
			if (x instanceof Double) {
				return ((Double) x).compareTo((Double) y);
			}
			if (x instanceof String) {
				return ((String) x).compareTo((String) y);
			}
			if (x instanceof Polygon) {
				return polygonCompare(((Polygon) x), ((Polygon) y));
			}
			throw new DBAppException("Something went wrong with Comparer");
		} catch (Exception e) {
			throw new DBAppException("Something went wrong with Comparer");
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws Exception {
		// *number of queries
		int numberOfResults = arrSQLTerms.length;
		// *check number of operators and number of queries
		if (strarrOperators.length != numberOfResults - 1)
			throw new DBAppException("Wrong number of starr operators");
		// *array of vectors of result of each query
		Vector e = new Vector();

		// *result which i will return its iterator
		Vector balabizo = new Vector();

		// * I need to get to see if its indexed or not
		boolean isIndex = false;
		// *load the table

		// *get the type of the column and parse the value into it
		int colIndex = -1;

		for (int i = 0; i < numberOfResults; i++) {
			// * get table info to check if the column is indexed for each query
			String tableName = arrSQLTerms[i]._strTableName;
			String colName = arrSQLTerms[i]._strColumnName;
			Table table = this.loadTable(tableName);
			Vector rest = this.deserilizetable(tableName);
			table.setPagesreferences((List<String>) rest.get(0));
			table.setNumofcreatedpages((int) rest.get(1));
			for (int j = 0; j < table.getColoumn_names().size(); j++) {
				if (table.getColoumn_names().get(j).equals(colName)) {
					colIndex = j;
					break;
				}
			}
			isIndex = table.getIsindexed().get(colIndex);
			if (isIndex) {
				e.add(newBPSelect(tableName, colName, arrSQLTerms[i]._strOperator, arrSQLTerms[i]._objValue,
						table.getPagesreferences()));

			}

			else {
				// *will go in operate each query and
				e.add(selectHelper(arrSQLTerms[i]._strTableName, arrSQLTerms[i]._strColumnName,
						arrSQLTerms[i]._strOperator, arrSQLTerms[i]._objValue));
			}
		}

		// operations will the vector of the final results
		Iterator gogo = operations(e, strarrOperators).iterator();
		// gogo is the iterator of the results which will be returned
		return gogo;

	}

	@SuppressWarnings("unchecked")
	public static Vector operations(Vector results, String[] strarrOperators) {
		// *recursive method that does the AND/OR/XOR between each query :D

		if (results.size() <= 1) {
			if (results.size() == 0)
				return new Vector();

			return ((Vector) results.get(0));
		} else {
			// first query results
			Vector first = (Vector) results.remove(0);
			// 2nd query results
			Vector second = (Vector) results.remove(0);
			// operation between first and 2nd query
			String opp = strarrOperators[0];
			// result of the operation between the 2queries
			Vector f = new Vector();
			// * but i need to remove the operator i used from strarrOpertors so I will go
			// ahead and remove it
			String[] newOperators = new String[strarrOperators.length - 1];
			for (int i = 0; i < newOperators.length; i++) {
				// * copies all except the first element
				newOperators[i] = strarrOperators[i + 1];
			}

			// * I have AND OR XOR

			// *first case AND
			if (opp.equals("AND")) {
				// *whatever is in the first query must exist in the 2nd query to be in the
				// results
				// * 1 AND 1 = 1
				for (int i = 0; i < first.size(); i++) {
					if (second.contains((Vector) first.get(i)))
						f.add((Vector) first.get(i));

				}
				for (int i = 0; i < second.size(); i++) {
					if (first.contains((Vector) second.get(i)))
						f.add((Vector) second.get(i));
				}

			} else if (opp.equals("OR")) {
				// *will add whatever exists but eliminate duplicate records
				f = first;
				for (int i = 0; i < second.size(); i++) {
					if (!(f.contains((Vector) second.get(i))))
						f.add((Vector) second.get(i));
				}

			} else if (opp.equals("XOR")) {
				// *will add whatever exists in one query result and doesnt exist in the other
				// 1 XOR 0 = 1

				// *adds what's in first but not in second
				for (int i = 0; i < second.size(); i++) {
					if (!(first.contains((Vector) second.get(i))))
						f.add((Vector) second.get(i));
				}
				// *adds what's in second but not in first
				for (int i = 0; i < second.size(); i++) {
					if (!(second.contains((Vector) first.get(i))))
						f.add((Vector) first.get(i));

				}

			}

			results.add(0, f);

			return operations(results, newOperators);
		}

	}

	public void isIndexed() {

	}

}
