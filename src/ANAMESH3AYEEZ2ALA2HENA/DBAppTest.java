package ANAMESH3AYEEZ2ALA2HENA;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import BPTree.BPTree;
import RTree.RTree;

@SuppressWarnings("all")

public class DBAppTest {
	public static void main(String[] args) throws Exception {
		String strTableName = "delete";
		DBApp dbApp = new DBApp();
		dbApp.init();
		// createtablemain(dbApp, strTableName);
		// deletemain();
		// dbApp.displayer("delete");
		// dbApp.createRTreeIndex(strTableName, "area");
		Hashtable htblColNameValue;
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer(5));
		// htblColNameValue.put("name", new String("miro" + 3));
		// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)"));
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", 13);
		// htblColNameValue.put("name", new String("miro" + 13));
		// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)"));
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer(3));
		// htblColNameValue.put("name", new String("c"));
		// htblColNameValue.put("area", new String("(-5,0),(0,0),(40,0),(0,36)"));
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer( 1));
		// htblColNameValue.put("name", new String("a" ) );
		// htblColNameValue.put("area", new String("(0,0),(0,0),(-400,0),(0,36)") );
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer( 2 ));
		// htblColNameValue.put("name", new String("b" ) );
		// htblColNameValue.put("area", new String("(12,0),(35,0),(40,0),(0,36)") );
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer(4 ));
		// htblColNameValue.put("name", new String("d" ) );
		// htblColNameValue.put("area", new String("(0,0),(0,0),(-400,0),(0,36)") );
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer(7));
		// htblColNameValue.put("name", new String("miro" + 3));
		// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)"));
		htblColNameValue = new Hashtable();
		// htblColNameValue.put("id", new Integer(1));
		// htblColNameValue.put("name", new String("c"));
		htblColNameValue.put("area", new String("(12,0),(35,0),(40,0),(0,36)"));
		// dbApp.insertIntoTable(strTableName, htblColNameValue);
		// dbApp.createBTreeIndex(strTableName, "id");
		dbApp.deleteFromTable(strTableName, htblColNameValue);
		dbApp.displayer("delete");
	}

	private static void deletemain() throws Exception {
		String strTableName = "delete";

		DBApp dbApp = new DBApp();
		dbApp.init();
		Hashtable htblColNameValue;
		for (int i = 0; i < 400; i++) {
			htblColNameValue = new Hashtable();
			htblColNameValue.put("id", new Integer(i % 25));
			htblColNameValue.put("name", new String("miro" + i % 35));
			htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)"));
			dbApp.insertIntoTable(strTableName, htblColNameValue);
		}
		// dbApp.createBTreeIndex(strTableName, "name");
		// dbApp.displayer("delete");

	}

	private static void createtablemain(DBApp dbApp, String strTableName) throws DBAppException, IOException {
		Hashtable htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("area", "java.awt.Polygon");
		dbApp.createTable(strTableName, "id", htblColNameType);
	}
	// public static<T extends Comparable<T>> void main(String[] args) throws
	// Exception {
	// // System.out.println("balabizo");
	// //String strTableName = "test";
	// //DBApp dbApp = new DBApp();
	// //dbApp.init();
	// /*
	// Hashtable htblColNameType = new Hashtable();
	// htblColNameType.put("id", "java.lang.Integer");
	// htblColNameType.put("name", "java.lang.String");
	// htblColNameType.put("area", "java.awt.Polygon");
	// dbApp.createTable(strTableName, "name", htblColNameType);
	// */

	// // Hashtable htblColNameValue = new Hashtable();
	// // htblColNameValue.put("id", new Integer(3));
	// // htblColNameValue.put("name", new String("c"));
	// // htblColNameValue.put("area", new String("(-5,0),(0,0),(40,0),(0,36)"));
	// // htblColNameValue.put("id", new Integer( 1));
	// // htblColNameValue.put("name", new String("a" ) );
	// // htblColNameValue.put("area", new String("(0,0),(0,0),(-400,0),(0,36)") );
	// // htblColNameValue.put("id", new Integer( 2 ));
	// // htblColNameValue.put("name", new String("b" ) );
	// // htblColNameValue.put("area", new String("(12,0),(35,0),(40,0),(0,36)") );
	// // htblColNameValue.put("id", new Integer(4 ));
	// // htblColNameValue.put("name", new String("d" ) );
	// // htblColNameValue.put("area", new String("(-6,-8),(0,0),(406,0),(-63,36)")
	// );
	// // dbApp.insertIntoTable(strTableName, htblColNameValue);
	// // htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)") );
	// // dbApp.updateTable("test", "c", htblColNameValue);;
	// // System.out.println( dbApp.canBTreeIndex("gay","area")); //hairaga3 true
	// // dbApp.displayer("test");
	// // dbApp.csvIndex("gay","area");//hai5alih indexed
	// // String p1="(0,0),(35,0),(4,0),(0,36)";
	// // String p2="(12,0),(35,0),(40,0),(0,36)";
	// // dbApp.polygonCompare(p1, p2);

	// // System.out.println( dbApp.canBTreeIndex("gay","area")); //hairaga3
	// exception

	// // htblColNameValue.clear( );
	// // htblColNameValue.put("id", new Integer( 453455 ));
	// // htblColNameValue.put("name", new String("Ahmed Noor" ) );
	// // htblColNameValue.put("gpa", new Double( 0.95 ) );
	// // dbApp.insertIntoTable( strTableName , htblColNameValue );

	// //---------------------------------Select Tests---------------------------
	// String strTableName = "gay";
	// DBApp dbApp = new DBApp( );
	// dbApp.init();

	// /*
	// Hashtable htblColNameType = new Hashtable( );
	// htblColNameType.put("id", "java.lang.Integer");
	// htblColNameType.put("name", "java.lang.String");
	// htblColNameType.put("area", "java.awt.Polygon");
	// dbApp.createTable( strTableName, "id", htblColNameType );

	// */
	// // run this loop multiple times to get duplicate records
	// // u can change some elements in the loop to have difrrent records

	// /*
	// Hashtable htblColNameValue ;
	// for(int i=0;i<25;i++) {
	// htblColNameValue = new Hashtable( );
	// htblColNameValue.put("id", new Integer( i ));
	// htblColNameValue.put("name", new String("miro" ) );
	// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)") );
	// dbApp.insertIntoTable( strTableName , htblColNameValue );
	// }
	// */
	// //dbApp.createBTreeIndex(strTableName, "id");

	// SQLTerm[] arrSQLTerms;
	// arrSQLTerms = new SQLTerm[2];
	// int i=0;
	// while( i<2) {
	// arrSQLTerms[i]=new SQLTerm();
	// i++;
	// }
	// arrSQLTerms[0]._strTableName = "gay";
	// arrSQLTerms[0]._strColumnName= "area";
	// arrSQLTerms[0]._strOperator = "!=";//=,<=,>=,>,<
	// arrSQLTerms[0]._objValue = new Integer(1);
	// arrSQLTerms[1]._strTableName = "gay";
	// arrSQLTerms[1]._strColumnName= "id";
	// arrSQLTerms[1]._strOperator = "<";
	// arrSQLTerms[1]._objValue = new Integer(2);
	// String[]strarrOperators = new String[1];
	// strarrOperators[0] = "OR";
	// // lazem te check tab3an hal el column da indexed wala la2
	// //then check law howa polygon 3ashan law ah hate3mel Rtree be nafs kol 7aga

	// // ha create bplus tree be generic type
	// BPTree<T> tree=(BPTree<T>) dbApp.getBPlusTree(strTableName,"id");
	// //ba3d keda ha search el tree
	// //lazem te type cast ma3lesh we lazem Integer mesh int we new Double we keda
	// matensash te type
	// Object v=new Integer(17);

	// //[0] Di el page fel page refrence we [1] di el index

	// Iterator result=dbApp.selectFromTable(arrSQLTerms , strarrOperators);

	// while(result.hasNext()) {
	// System.out.println(result.next());
	// }

	// //-------------------------------------CreateBPlustree
	// Testing-----------------------------------------------

	// /* String strTableName = "gay";
	// DBApp dbApp = new DBApp( );
	// dbApp.init();

	// /*
	// Hashtable htblColNameType = new Hashtable( );
	// htblColNameType.put("id", "java.lang.Integer");
	// htblColNameType.put("name", "java.lang.String");
	// htblColNameType.put("area", "java.awt.Polygon");
	// dbApp.createTable( strTableName, "id", htblColNameType );

	// */
	// // run this loop multiple times to get duplicate records
	// // u can change some elements in the loop to have different records
	// // Hashtable htblColNameValue ;
	// /*
	// for(int i=0;i<25;i++) {
	// htblColNameValue = new Hashtable( );
	// htblColNameValue.put("id", new Integer( i ));
	// htblColNameValue.put("name", new String("miro" ) );
	// htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)") );
	// dbApp.insertIntoTable( strTableName , htblColNameValue );
	// }
	// */

	// // dbApp.createBTreeIndex(strTableName, "name");

	// // BPTree<String> b=(BPTree<String>) dbApp.getBPlusTree(strTableName,
	// "name");

	// // System.out.println(b.searchAll("miro"));

	// //dbApp.createRTreeIndex(strTableName, "area");
	// RTree<T> r=dbApp.getRTree(strTableName, "area");

	// Hashtable htblColNameValue = new Hashtable( );
	// htblColNameValue.put("id", new Integer( i ));
	// htblColNameValue.put("name", new String("miro" ) );
	// htblColNameValue.put("area", new String("(0,3),(0,5),(4,9),(12,0)") );
	// dbApp.insertIntoTable( strTableName , htblColNameValue );
	// dbApp.refreshRTree(strTableName, "area");
	// r=dbApp.getRTree(strTableName, "area");

	// }

}
