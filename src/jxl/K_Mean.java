package jxl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;

public class K_Mean {
	public static BufferedReader nbr=new BufferedReader(new InputStreamReader(System.in));
	public static String filename=ExcelReader.filename;
	public static String dirpath=ExcelReader.dirpath;
	public static int K;//ExcelReader.K;
	public static Connection csql=null;
	static File f = null;
	
	
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}
 
	

	private static void createSQLcluster(int[] assignments) throws Exception {
		Connection csql=ConnToSql.main();	
		Statement stmntsql=csql.createStatement();
		ResultSet rs=stmntsql.getResultSet();
		stmntsql.executeUpdate("USE "+ExcelReader.filename+";");
		String sql = "SELECT * from sheet1";
		rs=stmntsql.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		
		//separating clusters....................
		String dbHeader="";
		for(int i = 2 ; i<= columnCount ; i++){
			if(i<columnCount)
				dbHeader=dbHeader+rsmd.getColumnName(i)+" Double, ";
			else 
				dbHeader=dbHeader+rsmd.getColumnName(i)+" Double";
		}
		String sqlCluster= "Create table "+"For"+K;
		for(int i=0;i<K;++i){
			csql.createStatement().executeUpdate("DROP TABLE IF EXISTS "+"For"+K+ "Cluster"+(i+1));
			sqlCluster = sqlCluster + "Cluster"+(i+1)+" ( "+dbHeader+" );";
			csql.createStatement().execute(sqlCluster);
		//	System.out.println(""+sqlCluster);
			sqlCluster= "Create table "+"For"+K;
		}
		int h=1;
		for(@SuppressWarnings("unused") int clusterNum : assignments) {
		   // System.out.printf(""+clusterNum);
		    h++;
		}
		int[] clusterLabel=new int[h];
		h=0;
		for(int clusterNum : assignments) {
		    clusterLabel[h]=clusterNum+1;
		    h++;
		}
		//cluster files are created .....now insert respective tuples to them
		ResultSet rs2 = csql.createStatement().getResultSet();
		rs2= csql.createStatement().executeQuery("SELECT * from sheet1;");
		String sql4="";
		h=0;
		while(rs2.next()){
			for(int i=2;i<=(columnCount);i++){
				if(i<columnCount)
					sql4= sql4+rs2.getString(i)+", ";
				else if(i==columnCount)
					sql4= sql4+rs2.getString(i)+""; 
			}
			sql4="INSERT INTO "+"For"+K +"Cluster"+clusterLabel[h]+" VALUES (" + sql4+" );";
			try{	
			//System.out.println(""+sql4);
				csql.createStatement().executeUpdate(sql4);
			}
			catch(Exception e){ 
				System.out.println("Error in inserting tuples .. "+e);
			}
			sql4="";
			h++;
		}
	}
	public static void main(int x) throws Exception {
		
		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setSeed(10);
		//important parameter to set: preserver order, number of cluster.
		kmeans.setPreserveInstancesOrder(true);
		K=x;
		kmeans.setNumClusters(K);
		BufferedReader datafile = readDataFile(dirpath+ExcelReader.filename.replace("_xls", "")+".arff"); 
		Instances data = new Instances(datafile);
		kmeans.buildClusterer(data);
		// This array returns the cluster number (starting with 0) for each instance
		// The array has as many elements as the number of instances
		int[] assignments = kmeans.getAssignments();
		createSQLcluster(assignments);
	}
}