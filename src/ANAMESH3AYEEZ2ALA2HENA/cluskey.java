package ANAMESH3AYEEZ2ALA2HENA;
import java.awt.Dimension;
import java.awt.Polygon;
import java.util.Date;

public class cluskey {
int numkey;
String strkey;
double Doubkey;
Date datekey;
Polygon polkey;
//List valid;
	public void set(Object x) 
	{
		if(x instanceof Integer)
		{
			this.numkey=(int)x;
			this.strkey=null;
			this.datekey=null;
			this.Doubkey=0.0;
			this.polkey=null;
			//System.out.println("uhjhb");
		}
		else if(x instanceof Double)
		{
			this.numkey=0;
			this.strkey=null;
			this.datekey=null;
			this.Doubkey=(Double) x;
			this.polkey=null;
			
		}
		else if(x instanceof String)
		{
			//this.numkey=0;
			this.strkey=(String) x;
			this.datekey=null;
			this.Doubkey=0.0;
			this.polkey=null;
			
		}
		else if(x instanceof Date)
		{
			this.numkey=0;
			this.strkey=null;
			this.datekey=(Date)x;
			this.Doubkey=0.0;
			this.polkey=null;
		}
		else 
		{
			this.numkey=0;
			this.strkey=null;
			this.datekey=null;
			this.Doubkey=0.0;
			this.polkey=(Polygon)x;
		}
	}
	public boolean compare(cluskey x) 
	{
		if(this.numkey!=0)
		{
			if(this.numkey<=x.getNumkey())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(this.Doubkey!=0.0)
		{
			if(this.Doubkey<=x.getDoubkey())
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
		else if(this.strkey!=null)
		{
			if(this.strkey.compareTo(x.getStrkey())<=0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else if(this.datekey!=null)
		{
			
			if(this.datekey.compareTo(x.getDatekey())<=0)
			{
				return true;
			}
			else
			{
				return false;
			}
			
		}
		else
		{ 
			System.out.println(this.polkey);
			System.out.println(x.polkey);
			
			int n=this.polygonCompare(this.polkey,x.polkey);
			if(n<=0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
	}
	public int getNumkey() {
		return numkey;
	}
	public void setNumkey(int numkey) {
		this.numkey = numkey;
	}
	public String getStrkey() {
		return strkey;
	}
	public void setStrkey(String strkey) {
		this.strkey = strkey;
	}
	public double getDoubkey() {
		return Doubkey;
	}
	public void setDoubkey(double doubkey) {
		Doubkey = doubkey;
	}
	public Date getDatekey() {
		return datekey;
	}
	public void setDatekey(Date datekey) {
		this.datekey = datekey;
	}
	public static Polygon makePolygon(String s) throws DBAppException{
		//this method takes the input string from the user
		//and parses the string into polygon and returns the poly
		Polygon p=new Polygon();
		s=s.replace("(", "");
		s=s.replace(")", "");
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
	public static int polygonCompare(Polygon p1,Polygon p2){
		// if the first polygon is bigger it returns 1;
		//if the 2nd polygon is bigger it returns -1
		//if they are equal it returns 0
		int n=-2;
		Dimension dim1 = p1.getBounds().getSize();
		int p1area = dim1.width * dim1.height;
		
		Dimension dim2 = p2.getBounds().getSize();
		int p2area = dim2.width * dim2.height;
		//System.out.println(p1area+" "+p2area);
		if(p1area>p2area){
			n=1;
		}else if(p1area==p2area){
			n=0;
		}else if(p1area<p2area){
			n=-1;
		}
		//System.out.println(n);
		return n;
	}
	public static void main(String[]args) throws DBAppException
	{
		cluskey x=new cluskey();
		//Polygon a=makePolygon("(1000000,20),(30,30),(40,40),(900,60)");
		x.set(1);
		//Polygon s=makePolygon("(10,800),(30,30),(40,40),(900,60)");
		cluskey y=new cluskey();
		y.set(0);
		System.out.print(x.compare(y));
	}
}
