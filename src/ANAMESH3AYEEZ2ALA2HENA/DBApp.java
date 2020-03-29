package ANAMESH3AYEEZ2ALA2HENA;


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
import java.nio.channels.ReadPendingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
//import java.util.Iterator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;








/* TO DOOOO
 1-deleting empty pages after deleting records

2-having a page counter attribute that only increases so that each time I create a page it takes the 
number of the counter and increments the counter and whenever a page is deleted the counter doesnt 
decrement it stays the same.so that no pages with the same name will be created


3-whenever u delete a page file u remove it from the array of page refrences from the table

4-use the polygonCompare method you can use it with two coordinate strings that the user gives u
or u can use it with 2 polygons u already have if it returns 1 first poly is bigger if it returns 0 they are equal
if it returns -1 2nd poly is bigger

5-make sure any record we try to insert is inserted and no system.out.print commands 
are active and all exceptions are handled wth DBAPP exception



*/
public class DBApp {

	public DBApp() {

	}


	public static boolean isIndexed(String tableName,String colName) throws DBAppException {
		boolean isindexed=false;
		
		File oldFile=new File("data/metadata.csv");
		String tbname="";String cname="";String ctype="";String iskey="";String isIndexed="";
		boolean tablefound=false;
		boolean colfound=false;
		boolean wrongIndex=false;
		boolean alreadyIndexed=false;
		String bobo="";
		try {
			
			Scanner x=new Scanner(oldFile);
			x.useDelimiter("[,\n]");
			
			while(x.hasNext()) {
				tbname=x.next();
				cname=x.next();
				ctype=x.next();
				iskey=x.next();
				isIndexed=x.next();
				bobo=tbname+" "+cname+" "+ctype+" "+iskey+" "+isIndexed;
				//System.out.println(bobo);
				
				
				if(tbname.equals(tableName)) {
					tablefound=true;
					if(cname.equals(colName)) {
						colfound=true;
						if(ctype.equals("java.util.Polygon"))
							wrongIndex=true;
						else {
							if(isIndexed.contentEquals("False")) {
								return true;
							}
							else {
								isindexed=true;
							}
								
						}	
					}	
				}
				//if it didn't write the new indexed column then here it writes the normal columns
				
			}
			
			
		}
		catch(Exception e) {
			throw new DBAppException("Indexing error");
		}
		
		if(tablefound==false) {
			throw new DBAppException("Table you are trying to index doesnt exist");
		}else {
			if(colfound==false)
				throw new DBAppException("Column you are trying to index doesnt exist");
			
		}
		
		return isindexed;
		
	}

	public  Table loadTable(String tableName) throws DBAppException{
		Table table=new Table(tableName);

		File myfile=new File("data/metadata.csv");
		boolean tablefound=false; //to check if the table is already there
		BufferedReader reader = null;
		String line = "";
		String cvsSplitBy = ",";
		List<String> coloumn_names =new ArrayList<String>() ;
		List<String> datatype =new ArrayList<String>() ;
		List<Boolean> iskey =new ArrayList<Boolean>() ;
		List<Boolean> isindexed=new ArrayList<Boolean>()  ;
		try{



			//next we need to check if the table is already there
			reader=new BufferedReader(new FileReader("data/metadata.csv"));
			while((line=reader.readLine())!= null){
				String []data=line.split(cvsSplitBy);
			
				if(data[0].equals(tableName)){
					
					//we should throw Table already exists exception
					//System.out.println("Table already exists");
					coloumn_names.add(data[1]);
					datatype.add(data[2]);
					
					if(data[3].equals("False")||data[3].equals("FALSE")) {
						
						iskey.add(false);}
					else {
						iskey.add(true);
					}
					if(data[4].equals("False")||data[4].equals("FALSE")) {
						isindexed.add(false);}
					else {
						isindexed.add(true);}
					
					tablefound=true;

				}
			}

			if(tablefound){
				//table.setName(tableName);
				table.setColoumn_names(coloumn_names);
				table.setDatatype(datatype);
				table.setIsindexed(isindexed);
				table.setIskey(iskey);

			}
			else{
				throw (new DBAppException("Table doesnt exist"));
			}

		}
		catch(FileNotFoundException e){
		throw new DBAppException("");
		}catch(IOException e){
			throw new DBAppException("");
		}

		return table;
	}
	//this method add the table properties to the metadata file 
	//and creates Table object with all required properties
	//then creates a .class file with the table object in it
	//the file is named after the table


	//
	//               done
	//this checks the consisity of data
	/////////////////////////////////////////////////

	public  boolean check(Object data,String datatype) throws DBAppException
	{   
		if(datatype.equals("java.lang.Integer"))
		{
			if(data instanceof Integer)
			{
				//System.out.println("fvfgv1");
				return true;
			}
			else 
			{
				//System.out.println("fvfgv1");
				return false;
			}
		}
		else if(datatype.equals("java.lang.Double")) 
		{
			if(data instanceof Double)
			{
				return true;
			}
			else 
			{   //System.out.println("fvfgv2");
				return false;
			}
		}
		else if(datatype.equals("java.lang.Boolean")) 
		{
			if(data instanceof Boolean)
			{
				return true;
			}
			else 
			{//System.out.println("fvfgv3");
				return false;
			}
		}else if(datatype.equals("java.lang.String")) 
		{
			if(data instanceof String)
			{
				return true;
			}
			else 
			{//System.out.println("fvfgv4");
				return false;
			}
		}else if(datatype.equals("java.lang.Date")) 
		{
			if(data instanceof Date)
			{
				return true;
			}
			else 
			{//System.out.println("fvfgv5");
				return false;
			}
		}
		else
		{
			
				this.makePolygon((String)data);
				return true;
			
		}
	}
	//
	//               done
	//takes a table and hash table and checks that the fields are correct
	/////////////////////////////////////////////////
	public boolean missorins(Table table,Hashtable<String,Object> htblColNameValue) throws DBAppException
	{
		for(int i=0;i<table.getColoumn_names().size()-1;i++)
		{
			Object o=htblColNameValue.get(table.getColoumn_names().get(i));

			if(o==null)
			{
				
				return true;
			}
			String datatype=(table.getDatatype().get(i));

			if(check(o,datatype)==false)
			{   
				
				return true;
			}
		}

		return false;
	}
	public boolean inCons(Table table,Hashtable<String,Object> t) throws DBAppException {
		for(int i=0;i<table.getColoumn_names().size();i++) {
			
			Object o=t.get(table.getColoumn_names().get(i));
			String datatype=(table.getDatatype().get(i));
//			System.out.println(o+" "+datatype);
			if(o==null)
				continue;
			if(check(o,datatype)==false)
				return true;
		}
		return false;
	}
	//gets the pages references of a table
	///
	//////////////////////////////////////////
	public  static Vector deserilizetable(String tblname)
	{    
		Vector result=new Vector();
		try {
			//System.out.println("testo.txt");
			ObjectInputStream o= new ObjectInputStream( new FileInputStream(tblname+".bin"));
			Table foo2 = (Table) o.readObject();
			//System.out.print(foo2.getPagesreferences().get(0));
			o.close();
			
			result.add(foo2.getPagesreferences());
			result.add(foo2.getNumofcreatedpages());
			return result;
		} catch (Exception e) {
			
			List<String> references = new ArrayList<String>();
			int num=0;
			result.add(references);
			result.add(num);
			return result;
		} 
		///////////////////////////////////////////
		//////////////////////////////////////////////main methods
	}
	public  void createTable(String strTableName,
			String strClusteringKeyColumn,
			Hashtable<String,String> htblColNameType )
					throws DBAppException, IOException
					
	{
		File myfile=new File("data/metadata.csv");
		boolean tablefound=false; //to check if the table is already there
		BufferedReader reader = null;
		String line = "";
		String cvsSplitBy = ",";
		try{
			//next we need to check if the table is already there
			reader=new BufferedReader(new FileReader("data/metadata.csv"));
			while((line=reader.readLine())!= null){
				String []data=line.split(cvsSplitBy);
				if(data[0].equals(strTableName)){
					//we should throw Table already exists exception
					//System.out.println("Table already exists");
					tablefound=true;
					break;
				}
			}
			if(!tablefound){
				//if the table is not there then we should add its data :`)
				//we need to traverse through the hashmap to get everything with its type
				//we need to know which one is the primary key of the table
				//we need to know if its indexed or not(by default they are all not indexed
				//will use this to iterate through hash map D:
				BufferedWriter writer=new BufferedWriter(new FileWriter(myfile,true));
				//first we will add the cluster key
				String keyName=strClusteringKeyColumn;
				String keyType=htblColNameType.get(keyName);
				String ki=strTableName+","+keyName+","+keyType+","+"True"+","+"False"+"\n";
				writer.write(ki);
				htblColNameType.remove(strClusteringKeyColumn);
				Iterator i=htblColNameType.entrySet().iterator();
				while(i.hasNext()){	
					Map.Entry mapElement = (Map.Entry)i.next();
					String colName= (String)mapElement.getKey(); //id,name,last name
					String colType=(String)mapElement.getValue();//integer string double we keda
					String key="False";
					if(colName.equals(strClusteringKeyColumn))
						key="True";
					String s=strTableName+","+colName+","+colType+","+key+","+"False"+"\n";
					writer.write(s);
				}
				writer.write(strTableName+",TouchDate,java.util.Date,False,False"+"\n");
				writer.flush();
				writer.close();
			}else{//if table is already there
				throw(new DBAppException("table already exists"));
			}
		}
		catch(FileNotFoundException e){
			throw new DBAppException();
		}catch(IOException e){
			throw new DBAppException();
		}finally{
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new DBAppException();
				}
			}
		}
	}
	public  void init() throws IOException{
		// this does whatever initialization you would like
		// or leave it empty if there is no code you want to
		// execute at application startup
		//creates the metadata file if its not already there
		File myfile=new File("data/metadata.csv");
		if(myfile.createNewFile()){//checking if the metadata file is there or not
			String s="Table Name,Column Name,Column Type,Key,Indexed\n";
			BufferedWriter writer=new BufferedWriter(new FileWriter(myfile,true)); 
			writer.write(s);
			writer.flush();
			writer.close();
		}
	}
	public  void createBTreeIndex(String strTableName,
			String strColName)throws DBAppException{
		if(canBTreeIndex(strTableName, strColName)) {
			
		}else {
			throw new DBAppException("");
		}
		
		
		
		
		
		
		
	}
	public  void createRTreeIndex(String strTableName,
			String strColName)throws DBAppException{
	}
	public  void insertIntoTable(String tablename,
			Hashtable<String,Object> htblColNameValue)throws DBAppException, IOException
	{  
		Table table=this.loadTable(tablename);
		Vector rest=this.deserilizetable(tablename);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int)rest.get(1));
		
		if(this.missorins(table, htblColNameValue)==true)
		{
			throw(new DBAppException("missing or inconsisitant data"));
		}
		else
		{
			//System.out.println("done");
			Vector record=new Vector();
			for(int i=0;i<table.getIskey().size();i++)
			{
				if(table.getIskey().get(i)==true)
				{
					
					if(((String)table.getDatatype().get(i)).equals("java.awt.Polygon"))
					{
						Polygon temp=this.makePolygon((String)htblColNameValue.get(table.getColoumn_names().get(i)));
					    record.add(temp);
					    
					}
					else
					{
					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					//System.out.println(record);
					}
					break;
				}
			}
			for(int i=0;i<table.getIskey().size()-1;i++)
			{
				if(table.getIskey().get(i)==false)
				{
					if(table.getDatatype().get(i)=="java.awt.Polygon")
					{
						Polygon temp=this.makePolygon((String)htblColNameValue.get(table.getColoumn_names().get(i)));
					    record.add(temp);
					}
					else
					{
					record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					
					}
				}
			}
			//TODO
			 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
			    Date date = new Date();  
			    
			record.add(formatter.format(date)+"");
			table.inserttotable(record);
			//		System.out.print(table.getPagesreferences());
		}
	}

	public  void updateTable(String strTableName,
			String strKey,
			Hashtable<String,Object> htblColNameValue )throws DBAppException, IOException
	{
		Table table=this.loadTable(strTableName);
		table.setPagesreferences(this.deserilizetable(strTableName));
		//first we check that the data entered in the hash table is consistent
		if(this.inCons(table, htblColNameValue)) {
			throw(new DBAppException("inconsisitant data"));
		}
		//Secondly, we check mainly if there is missing data or the hash table contains specific columns not the full record
		if(this.missorins(table, htblColNameValue)==true) {
			//first we create a new hash table containing nulls or empty spaces in the missing columns
			Hashtable <String,Object> tempT=new Hashtable();
			for(int i=0;i<table.getColoumn_names().size();i++) {
				if(htblColNameValue.containsKey(table.getColoumn_names().get(i))) {
					tempT.put(table.getColoumn_names().get(i), htblColNameValue.get(table.getColoumn_names().get(i)));
				}
				else {
					tempT.put(table.getColoumn_names().get(i), "");
				}
			}
			//Then, we get the new record format to compare it to the old record currently in the table
			Vector newRecord=new Vector();
			for(int i=0;i<table.getIskey().size()-1;i++)
			{
				if(table.getIskey().get(i)==true)
				{
					newRecord.add(tempT.get(table.getColoumn_names().get(i)));
					break;
				}
			}
			for(int i=0;i<table.getIskey().size()-1;i++)
			{
				if(table.getIskey().get(i)==false)
				{
					newRecord.add(tempT.get(table.getColoumn_names().get(i)));
				}
			}
			//here we compare the new record with the old one
			for(int i=0;i<table.getPagesreferences().size();i++) {
				Page currentPage=table.read_page(table.getPagesreferences().get(i));
				for(int j=0;j<currentPage.RecordsGetter().size();j++) {
					//the old record to compare
					Vector temp=(Vector) currentPage.RecordsGetter().get(j);
					//here we found the targeted record so we remove it form the record
					if(temp.get(0).equals(strKey)) {
						table.removefromtable(temp);
//						System.out.println("hna");
						for(int k=0;k<newRecord.size();k++) {
							//we only change the changed or the entered columns to avoid nulls
							if(newRecord.get(k)=="") {
								continue;
							}
							else {
								//we modify the old record 
								temp.set(k, newRecord.get(k));
							}
							//we add the old record after modifying it
							table.inserttotable(temp);
						}
					}
				}
			}
		}
		else {
			Vector newRecord=new Vector();
			for(int i=0;i<table.getIskey().size()-1;i++)
			{
				if(table.getIskey().get(i)==true)
				{
					newRecord.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					//System.out.println(record);
					break;
				}
			}
			for(int i=0;i<table.getIskey().size()-1;i++)
			{
				if(table.getIskey().get(i)==false)
				{
					newRecord.add(htblColNameValue.get(table.getColoumn_names().get(i)));
					//					System.out.print(newRecord);
					//break;
				}
			}
//			System.out.println(oldRecord);
//			System.out.println(newRecord);
		}
		
	}
	public  void deleteFromTable(String tablename,
			Hashtable<String,Object> htblColNameValue)throws DBAppException, IOException 
			{
			    //gets the table object from metadata
			    Table table=this.loadTable(tablename);
			    table.setPagesreferences(this.deserilizetable(tablename)); 

			        //System.out.println("done");
			        Vector record=new Vector();
			        for(int i=0;i<table.getIskey().size()-1;i++)
			        {
			            if(table.getIskey().get(i)==true)
			            {
			                record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
			                System.err.println(record);
			                break;
			            }
			        }
			        for(int i=0;i<table.getIskey().size()-1;i++)
			        {
			            if(table.getIskey().get(i)==false)
			            {
			                record.add(htblColNameValue.get(table.getColoumn_names().get(i)));
			                
			                //break;
			            }
			        }
			        record.add("touchdate");
			       
			        table.removefromtable(record);
			        



			}		
	public void displayer(String tablename) throws DBAppException
	{
	    Table table=this.loadTable(tablename);
	    table.setPagesreferences(this.deserilizetable(tablename));
	    if (table.getPagesreferences().size()==0)
	       
	    for(int i=0;i<table.getPagesreferences().size();i++)
	    {
	       
	        Page page=table.read_page(table.getPagesreferences().get(i));
	       
	        page.display();
	    }

	}
	
	
	
	public static Polygon makePolygon(String s) throws DBAppException{
		//this method takes the input string from the user
		//and parses the string into polygon and returns the poly
		Polygon p=new Polygon();
		s=s.replace("(","");
		s=s.replace(")","");
		String[] points=s.split(",");
		
		try{
			for(int i=0;i<points.length;i=i+2){
				int x=Integer.parseInt(points[i]);
				int y=Integer.parseInt(points[i+1]);
				p.addPoint(x,y);
			}}
		catch(NumberFormatException e){
			throw new DBAppException("Wrong polygon format");
		}catch(ArrayIndexOutOfBoundsException e){
			throw new DBAppException("Not enough points for polygon");
		}
		
		return p;
	}
	
	//if u want to pass two polygons
	public static int polygonCompare(Polygon p1,Polygon p2){
		// if the first polygon is bigger it returns 1;
		//if the 2nd polygon is bigger it returns -1
		//if they are equal it returns 0
		int n=-2;
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;
		
		Dimension dim2 = p2.getBounds().getSize();
		int p2area = dim2.width * dim2.height;
		if(p1area>p2area){
			n=1;
		}else if(p1area==p2area){
			n=0;
		}else if(p1area<p2area){
			n=-1;
		}
		return n;
	}
	
	//if u want to pass two strings
	public static int polygonCompare(String s1,String s2) throws DBAppException{
		// if the first polygon is bigger it returns 1;
		//if the 2nd polygon is bigger it returns -1
		//if they are equal it returns 0
		Polygon p1=makePolygon(s1);
		Polygon p2=makePolygon(s2);
		int n=-2;
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;
		
		Dimension dim2 = p2.getBounds().getSize();
		int p2area = dim2.width * dim2.height;
		if(p1area>p2area){
			n=1;
		}else if(p1area==p2area){
			n=0;
		}else if(p1area<p2area){
			n=-1;
		}
		return n;
	}

	//---------------------------------------------------------------------------------------------------
	public static boolean canBTreeIndex(String tableName,String colName) throws DBAppException {
		File oldFile=new File("data/metadata.csv");
		String tbname="";String cname="";String ctype="";String iskey="";String isIndexed="";
		boolean tablefound=false;
		boolean colfound=false;
		boolean wrongIndex=false;
		boolean alreadyIndexed=false;
		String bobo="";
		try {
			
			Scanner x=new Scanner(oldFile);
			x.useDelimiter("[,\n]");
			
			while(x.hasNext()) {
				tbname=x.next();
				cname=x.next();
				ctype=x.next();
				iskey=x.next();
				isIndexed=x.next();
				bobo=tbname+" "+cname+" "+ctype+" "+iskey+" "+isIndexed;
				//System.out.println(bobo);
				
				
				if(tbname.equals(tableName)) {
					tablefound=true;
					if(cname.equals(colName)) {
						colfound=true;
						if(ctype.equals("java.util.Polygon"))
							wrongIndex=true;
						else {
							if(isIndexed.contentEquals("False")) {
								return true;
							}
							else {
								alreadyIndexed=true;
							}
								
						}	
					}	
				}
				//if it didn't write the new indexed column then here it writes the normal columns
				
			}
			
			
		}
		catch(Exception e) {
			throw new DBAppException("Indexing error");
		}
		
		if(tablefound==false) {
			throw new DBAppException("Table you are trying to index doesnt exist");
		}else {
			if(colfound==false)
				throw new DBAppException("Column you are trying to index doesnt exist");
			else {
				if(wrongIndex)
					throw new DBAppException("Cannot BTree index a Polygon type");
				else
					if(alreadyIndexed)
						throw new DBAppException("Column "+colName+" is already indexed");
			}
		}
		return false;
	}
	
	
	public static boolean canRTreeIndex(String tableName,String colName) throws DBAppException {
		File oldFile=new File("data/metadata.csv");
		String tbname="";String cname="";String ctype="";String iskey="";String isIndexed="";
		boolean tablefound=false;
		boolean colfound=false;
		boolean wrongIndex=false;
		boolean alreadyIndexed=false;
		String bobo="";
		try {
			
			Scanner x=new Scanner(oldFile);
			x.useDelimiter("[,\n]");
			
			while(x.hasNext()) {
				tbname=x.next();
				cname=x.next();
				ctype=x.next();
				iskey=x.next();
				isIndexed=x.next();
				bobo=tbname+" "+cname+" "+ctype+" "+iskey+" "+isIndexed;
				//System.out.println(bobo);
				
				
				if(tbname.equals(tableName)) {
					tablefound=true;
					if(cname.equals(colName)) {
						colfound=true;
						if(!ctype.equals("java.util.Polygon"))
							wrongIndex=true;
						else {
							if(isIndexed.contentEquals("False")) {
								return true;
							}
							else {
								alreadyIndexed=true;
							}
								
						}	
					}	
				}
				//if it didn't write the new indexed column then here it writes the normal columns
				
			}
			
			
		}
		catch(Exception e) {
			throw new DBAppException("Indexing error");
		}
		
		if(tablefound==false) {
			throw new DBAppException("Table you are trying to index doesnt exist");
		}else {
			if(colfound==false)
				throw new DBAppException("Column you are trying to index doesnt exist");
			else {
				if(wrongIndex)
					throw new DBAppException("Cannot RTree index except Polygon type");
				else
					if(alreadyIndexed)
						throw new DBAppException("Column "+colName+" is already indexed");
			}
		}
		
		return false;
	}
	
	public static void csvIndex(String tableName,String colName)throws DBAppException{
		String tempFile="data/temp.csv";
		File oldFile=new File("data/metadata.csv");
		File newFile=new File(tempFile);
		String tbname="";String cname="";String ctype="";String iskey="";String isIndexed="";
		boolean tablefound=false;
		boolean colfound=false;
		boolean wrongIndex=false;
		boolean alreadyIndexed=false;
		String bobo="";
		try {
			FileWriter fw=new FileWriter(tempFile,true);
			BufferedWriter bw=new BufferedWriter(fw);
			PrintWriter pw=new PrintWriter(bw);
			Scanner x=new Scanner(oldFile);
			x.useDelimiter("[,\n]");
			
			while(x.hasNext()) {
				tbname=x.next();
				cname=x.next();
				ctype=x.next();
				iskey=x.next();
				isIndexed=x.next();
				bobo=tbname+" "+cname+" "+ctype+" "+iskey+" "+isIndexed;
				//System.out.println(bobo);
				
				
				if(tbname.equals(tableName)&&cname.equals(colName)) {
					pw.write(tbname+","+cname+","+ctype+","+iskey+","+"True"+"\n");
							
					
				}else {
					
					pw.write(tbname+","+cname+","+ctype+","+iskey+","+isIndexed+"\n");
				}
				
				
			}
			x.close();
			pw.flush();
			pw.close();
			bw.close();
			fw.close();
		
			System.gc();
		    oldFile.delete();
			
			File dodo=new File("data/metadata.csv");
			
			newFile.renameTo(dodo);
			
		}
		catch(Exception e) {
			throw new DBAppException("Indexing error");
		}
		
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public static Vector binarySelect(int keyIndex,Vector page,Object value,Vector<Vector<?>> records,String operator) throws DBAppException {
		// el method momken teban kebira awi fel awel bas heits its barely the same code repeated ketir
		//nafs el code repeated ma3 e5telaf el operators 3ashan <,>,>=,<=,=
		//fa dont worry its just repetitive badal ma3mel method le kol operator
		
		
		int first=0;
		int last=page.size()-1;
		int mid=(first+last)/2;
		boolean found =false;
		boolean right=false;
		boolean left=false;
		page.size();
		
		
		if(operator.equals("=")) {
		while(first<=last) {
			Vector record=(Vector)page.get(mid);
			Object midValue=record.get(keyIndex);
			if(comparer(record.get(keyIndex), value)<0) {
				first=mid+1;
			}else if(comparer(record.get(keyIndex), value)==0) {
				found=true;
				records.add(record);
				break;
			}else {
				last=mid-1;
			}
			mid=(first+last)/2;
			
		}
		if(found) {
		int moveright=mid+1;
		int moveleft=mid-1;
		Vector record=(Vector)page.get(mid);
		if(moveright<page.size())
			record=(Vector)page.get(moveright);
		while(comparer(record.get(keyIndex), value)==0&&moveright<page.size()) {
			records.add(record);
			moveright=moveright+1;
			if(moveright<page.size())
				record=(Vector)page.get(moveright);
		}
		
		if(moveleft>-1)
			record=(Vector)page.get(moveleft);
		while(comparer(record.get(keyIndex), value)==0&&moveleft>-1) {
			records.add(record);
			moveleft=moveleft-1;
			if(moveleft>-1)
				record=(Vector)page.get(moveleft);
		}
		
		
		
		}
		
		
		}
		
		if(operator.equals(">")) {
			while(first<=last) {
				Vector record=(Vector)page.get(mid);
				Object midValue=record.get(keyIndex);
				if(comparer(record.get(keyIndex), value)<0) {
					first=mid+1;
				}else if(comparer(record.get(keyIndex), value)>0) {
					found=true;
					records.add(record);
					break;
				}else {
					last=mid-1;
				}
				mid=(first+last)/2;
				
			}
			if(found) {
			int moveright=mid+1;
			int moveleft=mid-1;
			Vector record=(Vector)page.get(mid);
			if(moveright<page.size())
				record=(Vector)page.get(moveright);
			while(comparer(record.get(keyIndex), value)>0&&moveright<page.size()) {
				records.add(record);
				moveright=moveright+1;
				if(moveright<page.size())
					record=(Vector)page.get(moveright);
			}
			
			if(moveleft>-1)
				record=(Vector)page.get(moveleft);
			while(comparer(record.get(keyIndex), value)>0&&moveleft>-1) {
				records.add(record);
				moveleft=moveleft-1;
				if(moveleft>-1)
					record=(Vector)page.get(moveleft);
			}
			
			
			
			}
			
			
			}
		
		if(operator.equals(">=")) {
			while(first<=last) {
				Vector record=(Vector)page.get(mid);
				Object midValue=record.get(keyIndex);
				if(comparer(record.get(keyIndex), value)<0) {
					first=mid+1;
				}else if(comparer(record.get(keyIndex), value)>=0) {
					found=true;
					records.add(record);
					break;
				}else {
					last=mid-1;
				}
				mid=(first+last)/2;
				
			}
			if(found) {
			int moveright=mid+1;
			int moveleft=mid-1;
			Vector record=(Vector)page.get(mid);
			if(moveright<page.size())
				record=(Vector)page.get(moveright);
			while(comparer(record.get(keyIndex), value)>=0&&moveright<page.size()) {
				records.add(record);
				moveright=moveright+1;
				if(moveright<page.size())
					record=(Vector)page.get(moveright);
			}
			
			if(moveleft>-1)
				record=(Vector)page.get(moveleft);
			while(comparer(record.get(keyIndex), value)>=0&&moveleft>-1) {
				records.add(record);
				moveleft=moveleft-1;
				if(moveleft>-1)
					record=(Vector)page.get(moveleft);
			}
			
			
			
			}
			
			
			}
		
		//check this bassel
		
		if(operator.equals("<")) {
			while(first<=last) {
				Vector record=(Vector)page.get(mid);
				Object midValue=record.get(keyIndex);
				if(comparer(record.get(keyIndex), value)<0) {
					found=true;
					records.add(record);
					break;
					
				}else if(comparer(record.get(keyIndex), value)>=0) {
					last=mid-1;
				}else {
					last=mid-1;
				}
				mid=(first+last)/2;
				
			}
			if(found) {
			int moveright=mid+1;
			int moveleft=mid-1;
			Vector record=(Vector)page.get(mid);
			if(moveright<page.size())
				record=(Vector)page.get(moveright);
			while(comparer(record.get(keyIndex), value)<0&&moveright<page.size()) {
				records.add(record);
				moveright=moveright+1;
				if(moveright<page.size())
					record=(Vector)page.get(moveright);
			}
			
			if(moveleft>-1)
				record=(Vector)page.get(moveleft);
			while(comparer(record.get(keyIndex), value)<0&&moveleft>-1) {
				records.add(record);
				moveleft=moveleft-1;
				if(moveleft>-1)
					record=(Vector)page.get(moveleft);
			}
			
			
			
			}
			
			
			}
		
		
		if(operator.equals("<=")) {
			while(first<=last) {
				Vector record=(Vector)page.get(mid);
				Object midValue=record.get(keyIndex);
				if(comparer(record.get(keyIndex), value)<=0) {
					found=true;
					records.add(record);
					break;
					
				}else if(comparer(record.get(keyIndex), value)>=0) {
					last=mid-1;
				}else {
					last=mid-1;
				}
				mid=(first+last)/2;
				
			}
			if(found) {
			int moveright=mid+1;
			int moveleft=mid-1;
			Vector record=(Vector)page.get(mid);
			if(moveright<page.size())
				record=(Vector)page.get(moveright);
			while(comparer(record.get(keyIndex), value)<=0&&moveright<page.size()) {
				records.add(record);
				moveright=moveright+1;
				if(moveright<page.size())
					record=(Vector)page.get(moveright);
			}
			
			if(moveleft>-1)
				record=(Vector)page.get(moveleft);
			while(comparer(record.get(keyIndex), value)<=0&&moveleft>-1) {
				records.add(record);
				moveleft=moveleft-1;
				if(moveleft>-1)
					record=(Vector)page.get(moveleft);
			}
			
			
			
			}
			
			
			}
		
		
		
		
		return records;
		
	}
	
	
	 
	 
	 
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  Vector selectHelper(String tableName, String colName, String operator, Object value) throws DBAppException {
		Vector results=new Vector();
		
		// *check if the column is indexed
		// *use indexedSelect
		boolean isKey=false;
		// *the value type is the same as the column type
		// *load the table
		Table table=this.loadTable(tableName);
		Vector rest=this.deserilizetable(tableName);
		table.setPagesreferences((List<String>) rest.get(0));
		table.setNumofcreatedpages((int)rest.get(1));
		// *get the type of the column and parse the value into it
		int colIndex=-1;
		for (int i = 0; i < table.getColoumn_names().size(); i++) {
			if(table.getColoumn_names().get(i).equals(colName)) {
				colIndex=i;
				break;
			}
		}


		isKey=table.getIskey().get(colIndex);
	
		String colType=table.getDatatype().get(colIndex);
		Page p;Vector v;
		
		// *loop on pages
		for(int i=0;i<table.getPagesreferences().size();i++) {
			p=table.read_page(table.getPagesreferences().get(i));
			v=p.RecordsGetter();// *got the records in one page
			
			if(isKey&&!(operator.equals("!="))) {
				// *if its on the key we should binary search inside each page :`)
				
				results=binarySelect(colIndex, v, value, results, operator);
				
				//
				
				
			}else {
				// *just linear search and check the typecast
				for(int j=0;j<v.size();j++) {
					Vector record=(Vector)v.get(j);
					
					if(operator.equals("=")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)==0)
							results.add(record);
					}else if(operator.equals("!=")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)!=0)
							results.add(record);
					}else if(operator.equals("<=")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)==0||comparer(((Vector)v.get(j)).get(colIndex),value)<0)
							results.add(record);
					}else if(operator.equals(">=")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)==0||comparer(((Vector)v.get(j)).get(colIndex),value)>0)
							results.add(record);
					}else if(operator.equals(">")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)>0)
							results.add(record);
					}else if(operator.equals("<")) {
						if(comparer(((Vector)v.get(j)).get(colIndex),value)<0)
							results.add(record);
					}					
					
				}
				
			}
					
					
					
					
			
			
		}
		// java.lang.Integer, java.lang.String,
		//java.lang.Double, java.lang.Boolean, java.util.Date and java.awt.Polygon
		
		
		
		
		
		
		return results;
	}
	
	
	public static int comparer(Object x,Object y) throws DBAppException { 
	
		
		
		try {
			if(x instanceof Integer) {
				return ((Integer)x).compareTo((Integer)y);
				
			}
			if(x instanceof Boolean) {
				return ((Boolean)x).compareTo((Boolean)y);
			}
			if(x instanceof Double) {
				return (( Double)x).compareTo(( Double)y);
			}
			if(x instanceof String) {
				return ((String)x).compareTo((String)y);
			}
			if(x instanceof Polygon) {
				return polygonCompare(((Polygon)x), ((Polygon)y));
			}
			throw new DBAppException("Something went wrong with Comparer");
		}
		catch(Exception e) {
			throw new DBAppException("Something went wrong with Comparer");
		}
		
		
		
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator selectFromTable(SQLTerm[] arrSQLTerms,
			String[] strarrOperators)
			throws DBAppException{
		// *number of queries
		int numberOfResults=arrSQLTerms.length;
		// *check number of operators and number of queries
		if(strarrOperators.length!=numberOfResults-1)
			throw new DBAppException("Wrong number of starr operators");
		// *array of vectors of result of each query
		Vector e=new Vector();
		
		// *result which i will return its iterator
		Vector balabizo=new Vector();
		
		for (int i = 0; i < numberOfResults; i++) {
			// *will go in operate each query and 
			e.add(selectHelper(arrSQLTerms[i]._strTableName, arrSQLTerms[i]._strColumnName,
														arrSQLTerms[i]._strOperator, arrSQLTerms[i]._objValue));
			
		}
		
	
		//operations will the vector of the final results 
		Iterator gogo =operations(e, strarrOperators).iterator();
		// gogo is the iterator of the results which will be returned
		return gogo;
		
		
		
		
	}
	
	@SuppressWarnings("unchecked")
	public static Vector operations(Vector results,String[] strarrOperators) {
		// *recursive method that does the AND/OR/XOR between each query :D
		
		if(results.size()==1) {
			return ((Vector)results.get(0));
		}else {
			//first query results
			Vector first=(Vector)results.remove(0);
			//2nd query results
			Vector second=(Vector)results.remove(0);
			//operation between first and 2nd query
			String opp=strarrOperators[0]; 
			//result of the operation between the 2queries
			Vector f=new Vector();
			//* but i need to remove the operator i used from strarrOpertors so I will go ahead and remove it
			String[] newOperators=new String[strarrOperators.length-1];
			for(int i=0;i<newOperators.length;i++) {
				//* copies all except the first element
				newOperators[i]=strarrOperators[i+1];
			}
			
			// * I have  AND  OR   XOR
			
			// *first case AND
			if(opp.equals("AND")) {
				// *whatever is in the first query must exist in the 2nd query to be in the results
				// * 1 AND 1 = 1
				for(int i=0;i<first.size();i++) {
					if(second.contains((Vector)first.get(i)))
						f.add((Vector)first.get(i));
					
				}
				
			}else if(opp.equals("OR")) {
			// *will add whatever exists but eliminate duplicate records
				f=first;
				for(int i=0;i<first.size();i++){
					if(!(f.contains((Vector)second.get(i))))
						f.add((Vector)second.get(i));
				}
				
			}else if(opp.equals("XOR")) {
				// *will add whatever exists in one query result and doesnt exist in the other
				//                         1 XOR 0 = 1
				
				// *adds what's in first but not in second
				for(int i=0;i<first.size();i++) {
					if(!(first.contains((Vector)second.get(i))))
							f.add((Vector)second.get(i));
				}
				// *adds what's in second but not in first
				for(int i=0;i<second.size();i++) {
					if(!(second.contains((Vector)first.get(i))))
						f.add((Vector)first.get(i));
					
				}
				
				
				
			}
			
			
			
			
			results.add(0, f);
			
			
			
			return operations(results,newOperators);
		}
		
		
		
		
		
		
	}
	
	


}
    