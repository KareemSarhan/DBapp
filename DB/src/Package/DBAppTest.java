package DB.src.Package;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

@SuppressWarnings("all")

public class DBAppTest {

	public static void main(String[] args) throws IOException, DBAppException {

		String strTableName = "Table";
		DBApp dbApp = new DBApp();
		dbApp.init();
		Hashtable htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("area", "java.awt.Polygon");
		// dbApp.createTable(strTableName, "area", htblColNameType);
		Hashtable htblColNameValue = new Hashtable();
		htblColNameValue.put("id", new Integer(2343432));
		htblColNameValue.put("name", new String("miro"));
		htblColNameValue.put("area", new String("(0,0),(0,0),(0,0),(0,0)"));
		dbApp.insertIntoTable(strTableName, htblColNameValue);
		dbApp.displayer(strTableName);

		dbApp.deleteFromTable(strTableName, htblColNameValue);


		dbApp.displayer(strTableName);

		// dbApp.deleteFromTable(strTableName, htblColNameValue);
		// System.out.println( dbApp.canBTreeIndex("Table","area")); //hairaga3 true
		// dbApp.displayer(strTableName);
		// dbApp.displayer(strTableName);

		// dbApp.displayer(strTableName);

		// dbApp.csvIndex("Table","area");//hai5alih indexed
		// System.out.println( dbApp.canBTreeIndex("Table","area")); //hairaga3
		// exception
		// htblColNameValue.clear( );
		// htblColNameValue.put("id", new Integer( 453455 ));
		// htblColNameValue.put("name", new String("Ahmed Noor" ) );
		// htblColNameValue.put("gpa", new Double( 0.95 ) );
		// dbApp.insertIntoTable( strTableName , htblColNameValue );
		//

	}

}
