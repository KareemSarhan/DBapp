
import java.awt.Dimension;
import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
//import java.util.Iterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

@SuppressWarnings("all")
/*
 * TO DOOOO 1-deleting empty pages after deleting records
 *
 * 2-having a page counter attribute that only increases so that each time I
 * create a page it takes the number of the counter and increments the counter
 * and whenever a page is deleted the counter doesnt decrement it stays the
 * same.so that no pages with the same name will be created
 *
 *
 * 3-whenever u delete a page file u remove it from the array of page refrences
 * from the table
 *
 * 4-use the polygonCompare method you can use it with two coordinate strings
 * that the user gives u or u can use it with 2 polygons u already have if it
 * returns 1 first poly is bigger if it returns 0 they are equal if it returns
 * -1 2nd poly is bigger
 *
 * 5-make sure any record we try to insert is inserted and no system.out.print
 * commands are active and all exceptions are handled wth DBAPP exception
 *
 *
 *
 */
public class DBApp {

  public DBApp() {
  }

  public Table loadTable(String tableName) throws DBAppException {
    Table table = new Table(tableName);

    File myfile = new File("DB/data/metadata.csv");
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
      reader = new BufferedReader(new FileReader("DB/data/metadata.csv"));
      while ((line = reader.readLine()) != null) {
        String[] data = line.split(cvsSplitBy);

        if (data[0].equals(tableName)) {
          // we should throw Table already exists exception
          // System.out.println("Table already exists");
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
        throw (new DBAppException("Table doesnt exist"));
      }
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

  //
  // done
  // this checks the consisity of data
  /////////////////////////////////////////////////

  public boolean check(Object data, String datatype) throws DBAppException {
    if (datatype.equals("java.lang.Integer")) {
      if (data instanceof Integer) {
        // System.out.println("fvfgv1");
        return true;
      } else {
        // System.out.println("fvfgv1");
        return false;
      }
    } else if (datatype.equals("java.lang.Double")) {
      if (data instanceof Double) {
        return true;
      } else { // System.out.println("fvfgv2");
        return false;
      }
    } else if (datatype.equals("java.lang.Boolean")) {
      if (data instanceof Boolean) {
        return true;
      } else { // System.out.println("fvfgv3");
        return false;
      }
    } else if (datatype.equals("java.lang.String")) {
      if (data instanceof String) {
        return true;
      } else { // System.out.println("fvfgv4");
        return false;
      }
    } else if (datatype.equals("java.lang.Date")) {
      if (data instanceof Date) {
        return true;
      } else { // System.out.println("fvfgv5");
        return false;
      }
    } else {
      this.makePolygon((String) data);
      return true;
    }
  }

  //
  // done
  // takes a table and hash table and checks that the fields are correct
  /////////////////////////////////////////////////
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
  ///
  //////////////////////////////////////////
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
    ///////////////////////////////////////////
    ////////////////////////////////////////////// main methods
  }

  public void createTable(String strTableName, String strClusteringKeyColumn, Hashtable<String, String> htblColNameType)
      throws DBAppException, IOException {
    File myfile = new File("DB/data/metadata.csv");
    boolean tablefound = false; // to check if the table is already there
    BufferedReader reader = null;
    String line = "";
    String cvsSplitBy = ",";
    try {
      // next we need to check if the table is already there
      reader = new BufferedReader(new FileReader("DB/data/metadata.csv"));
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
    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    File myfile = new File("DB/data/metadata.csv");
    if (myfile.createNewFile()) { // checking if the metadata file is there or not
      String s = "Table Name,Column Name,Column Type,Key,Indexed\n";
      BufferedWriter writer = new BufferedWriter(new FileWriter(myfile, true));
      writer.write(s);
      writer.flush();
      writer.close();
    }
  }

  public void createBTreeIndex(String strTableName, String strColName) throws DBAppException {
  }

  public void createRTreeIndex(String strTableName, String strColName) throws DBAppException {
  }

  public void insertIntoTable(String tablename, Hashtable<String, Object> htblColNameValue)
      throws DBAppException, IOException {
    Table table = this.loadTable(tablename);
    Vector rest = this.deserilizetable(tablename);
    table.setPagesreferences((List<String>) rest.get(0));
    table.setNumofcreatedpages((int) rest.get(1));
    if (this.missorins(table, htblColNameValue) == true) {
      throw (new DBAppException("missing or inconsisitant data"));
    } else {
      // System.out.println("done");
      Vector record = new Vector();
      for (int i = 0; i < table.getIskey().size(); i++) {
        if (table.getIskey().get(i) == true) {
          if (((String) table.getDatatype().get(i)).equals("java.awt.Polygon")) {
            Polygon temp = this.makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
            record.add(temp);
          } else {
            record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
            // System.out.println(record);
          }
          break;
        }
      }
      for (int i = 0; i < table.getIskey().size() - 1; i++) {
        if (table.getIskey().get(i) == false) {
          if (table.getDatatype().get(i) == "java.awt.Polygon") {
            Polygon temp = this.makePolygon((String) htblColNameValue.get(table.getColoumn_names().get(i)));
            record.add(temp);
          } else {
            record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
          }
        }
      }
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = new Date();

      record.add(formatter.format(date) + "");
      table.inserttotable(record);
      // System.out.print(table.getPagesreferences());
    }
  }

  // public static void /*Iterator*/ selectFromTable(SQLTerm[] arrSQLTerms,
  // String[] strarrOperators)
  // throws DBAppException{
  //
  // //needs to return an object that inherits iterator i think
  // }
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
      throws DBAppException, IOException {
    Table table = this.loadTable(strTableName);
    table.setPagesreferences(this.deserilizetable(strTableName));
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
      // here we compare the new record with the old one
      for (int i = 0; i < table.getPagesreferences().size(); i++) {
        Page currentPage = table.read_page(table.getPagesreferences().get(i));
        for (int j = 0; j < currentPage.RecordsGetter().size(); j++) {
          // the old record to compare
          Vector temp = (Vector) currentPage.RecordsGetter().get(j);
          // here we found the targeted record so we remove it form the record
          if (temp.get(0).equals(strKey)) {
            table.removefromtable(temp);
            // System.out.println("hna");
            for (int k = 0; k < newRecord.size(); k++) {
              // we only change the changed or the entered columns to avoid nulls
              if (newRecord.get(k) == "") {
                continue;
              } else {
                // we modify the old record
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

  public void deleteFromTable(String tablename, Hashtable<String, Object> htblColNameValue)
      throws DBAppException, IOException {
    // gets the table object from metadata
    Table table = this.loadTable(tablename);
    table.setPagesreferences(this.deserilizetableOLD(tablename));

    Vector record = new Vector();
    for (int i = 0; i < table.getIskey().size() - 1; i++) {
      if (table.getIskey().get(i) == true) {
        record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
        // System.err.println(record);
        break;
      }
    }
    for (int i = 0; i < table.getIskey().size() - 1; i++) {
      if (table.getIskey().get(i) == false) {
        record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
        // break;
      }
    }
    record.add(Gettime());

    table.removefromtable(record);
  }

  public String Gettime() {
    Date date = new Date();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    return formatter.format(date);
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

  public static Polygon makePolygon(String s) throws DBAppException {
    // this method takes the input string from the user
    // and parses the string into polygon and returns the poly
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
    File oldFile = new File("DB/data/metadata.csv");
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
    File oldFile = new File("DB/data/metadata.csv");
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
            if (!ctype.equals("java.util.Polygon"))
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
    String tempFile = "DB/data/temp.csv";
    File oldFile = new File("DB/data/metadata.csv");
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

      System.gc();
      oldFile.delete();

      File dodo = new File("DB/data/metadata.csv");

      newFile.renameTo(dodo);
    } catch (Exception e) {
      throw new DBAppException("Indexing error");
    }
  }
}
