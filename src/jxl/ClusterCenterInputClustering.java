package jxl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ClusterCenterInputClustering {
	public static int K=ExcelReader.K;//K is Number of Cluster Centers user has given input
	public static Statement stmntsql = null;
	public static Connection csql=null;
	
	public Double[][] getClusterCenters() throws Exception{
		csql=ConnToSql.main();
		stmntsql = csql.createStatement();
		//System.out.println(filename);
		stmntsql.executeUpdate("USE "+ExcelReader.filename+";");
		String sql = "SELECT * from sheet1";
		ResultSet rs = stmntsql.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int numOfColumns = rsmd.getColumnCount();
		Double[][] clusterCenter;
		clusterCenter = new Double[K+1][numOfColumns+1];
		System.out.println("=================================================================================================================================================================");
		Printer.main("PLEASE ENTER "+K+ " CLUSTER CENTER'S VALUES: ");
		System.out.println("==================================================================================================================================================================\n");
		
		//@SuppressWarnings("resource")
		Scanner clusterRow=new Scanner(System.in);
		for(int i=1 ;i<=K;++i){
			System.out.println("\n----------------------------------------------------------------------------------------------------------------------------------------------------------------");
			Printer.main("PLEASE ENTER CLUSTER # "+i+ " CENTER VALUES");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
			for(int l=2 ;l<=numOfColumns;l++){
				System.out.print(rsmd.getColumnName(l)+ "\t");
			}
			System.out.println();
			for(int j=0 ;j<numOfColumns-1;++j){
				clusterCenter[i][j]=clusterRow.nextDouble();
			}	
		}
		return clusterCenter;
	}
	void clusterCal(Double [][] clusterCenterArray)throws SQLException{
		String quer="SELECT * FROM Sheet1 ;";
		Statement stmnt=csql.createStatement();
		ResultSet rs=stmnt.executeQuery(quer);
		ResultSetMetaData rsmd=rs.getMetaData();
		int columnCount=0;
		int rowCount=0;
		columnCount=rsmd.getColumnCount();
		if (rs.last()) {
		    rowCount = rs.getRow();
		    rs.beforeFirst();// Move to beginning
		}
		//System.out.println(rowCount);
		Double distance=Double.POSITIVE_INFINITY;// distance is initialise to 10000.0 as INFINITY -- //Double distance=10000.0;
		Double distance1=0.0;
		int label =-1;
		int[] clusterLabels=new int[rowCount+1];
		int l=0;
		while(rs.next()){
			//Ecludian Formula Implementation
				for(int j=1;j<=K;j++){
					for(int i=0;i<columnCount-1;i++){
						distance1+=Math.pow((clusterCenterArray[j][i] - Double.parseDouble( rs.getString(i+2))),2);//System.out.print(distance1+"\t");
					}
					//distance1= sqrt((x1-x2)2 + (y1-y2)2 + (z1-z2)2 +.....)
					distance1 = Math.sqrt(distance1);
					double temp=distance1;//System.out.println();
					distance=Math.min(distance,distance1 );
					if(temp==distance) 
						label=j;//System.out.println(distance);
					distance1=0.0;
				}//System.out.println(distance+"  "+label);
				clusterLabels[l]=label;
				l++;		
				distance=Double.POSITIVE_INFINITY;
		}
		stmntsql.executeUpdate("DROP TABLE IF EXISTS Sheet2");
		String sql1="";
		for(int i = 2 ; i<= columnCount ; i++){
			sql1=sql1+rsmd.getColumnName(i)+" Double, ";
		}
		String sql3="CREATE TABLE Sheet2 ("+sql1+"ClusterLabel INTEGER"+");";
   		stmntsql.executeUpdate(sql3);//System.out.println("\nNew Table with ClusterLabel created successfully...");
		String sql5="INSERT INTO Sheet2 VALUES (";
		rs.beforeFirst();
		int x=0;
		while(rs.next()){
			for(int i=2;i<=columnCount;i++){	
					sql5= sql5+rs.getString(i)+","; 
			}
			sql5=sql5+clusterLabels[x++]+");";
			stmntsql.executeUpdate(sql5);
			sql5="INSERT INTO Sheet2 VALUES (";
		}
		//Printer.main("DATA FRAGMENTED ACCORDING TO CLUSTER LABEL SUCCESSFULLY...");
	}
	void closeConnToSql(){
		try{
			stmntsql.close();
			csql.close();
		}
		catch( Exception e ){
			System.err.println( e+ "closeConnToSQL Exception" );
		}
	}

	public static int main( ) throws Exception{
		ClusterCenterInputClustering clus=new ClusterCenterInputClustering();
		clus.clusterCal(clus.getClusterCenters());
		return K;
	}
}
