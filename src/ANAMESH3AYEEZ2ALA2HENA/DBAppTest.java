package ANAMESH3AYEEZ2ALA2HENA;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

@SuppressWarnings("all")

public class DBAppTest {

	public static void main(String[] args) throws IOException, DBAppException {

		String strTableName = "test";
		DBApp dbApp = new DBApp();
		dbApp.init();
		Hashtable htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("area", "java.awt.Polygon");
		dbApp.createTable(strTableName, "name", htblColNameType);

		Hashtable htblColNameValue = new Hashtable();
		htblColNameValue.put("id", new Integer(3));
		htblColNameValue.put("name", new String("c"));
		htblColNameValue.put("area", new String("(-5,0),(0,0),(40,0),(0,36)"));
		// htblColNameValue.put("id", new Integer( 1));
		// htblColNameValue.put("name", new String("a" ) );
		// htblColNameValue.put("area", new String("(0,0),(0,0),(-400,0),(0,36)") );
		// htblColNameValue.put("id", new Integer( 2 ));
		// htblColNameValue.put("name", new String("b" ) );
		// htblColNameValue.put("area", new String("(12,0),(35,0),(40,0),(0,36)") );
		// htblColNameValue.put("id", new Integer(4 ));
		// htblColNameValue.put("name", new String("d" ) );
		// htblColNameValue.put("area", new String("(-6,-8),(0,0),(406,0),(-63,36)") );
		dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)") );
		// dbApp.updateTable("test", "c", htblColNameValue);;
		// System.out.println( dbApp.canBTreeIndex("gay","area")); //hairaga3 true
		dbApp.displayer("test");
		// dbApp.csvIndex("gay","area");//hai5alih indexed
		// String p1="(0,0),(35,0),(4,0),(0,36)";
		// String p2="(12,0),(35,0),(40,0),(0,36)";
		// dbApp.polygonCompare(p1, p2);

		// System.out.println( dbApp.canBTreeIndex("gay","area")); //hairaga3 exception

		// htblColNameValue.clear( );
		// htblColNameValue.put("id", new Integer( 453455 ));
		// htblColNameValue.put("name", new String("Ahmed Noor" ) );
		// htblColNameValue.put("gpa", new Double( 0.95 ) );
		// dbApp.insertIntoTable( strTableName , htblColNameValue );

	}

}
