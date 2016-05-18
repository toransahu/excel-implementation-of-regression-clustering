import java.io.BufferedReader;
import java.io.InputStreamReader;

import jxl.*;

public class Main {
	public static double[][][] error;
	private static int result;
	private static void concludeMinimum() {
		for(int k=0;k<2;++k){
			if(k==0)
			{
				System.out.println("\n================================================================================================================================================================");
				Printer.main("Linear Regression");
				System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
			}
			else
			{
				System.out.println("\n================================================================================================================================================================");
				Printer.main("Least Median Square Regression");
				System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
			}
		for(int j=0;j<2;++j){
			double temp= Double.POSITIVE_INFINITY ;
			for(int m = 0;m<ExcelReader.K;++m){
					int cmp=Double.compare(error[m][k][j], temp);
					if(cmp < 0){
						temp=error[m][k][j];
						result=m+1;
					}
			}
			if(j==0){
				Printer.main("MAE is minimum for K = "+(result));
				System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------");
			}
			else if(j==1){
				Printer.main("RMSE is minimum for K = "+(result));
				System.out.println("\n================================================================================================================================================================");
			}
		}
		}
	}
	public static void main(String[] args) throws Exception{
		ExcelReader.main();
		error = new double[ExcelReader.K][2][2];
		int n=0;
		System.out.println("\n================================================================================================================================================================");
		Printer.main("Make Choice of Cluster Center input");
		System.out.println("================================================================================================================================================================");
		Printer.main("1.Using K Means");
		Printer.main("2.Manual");
		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		n =Integer.parseInt(br.readLine());
		switch(n){
		case 1: 
				for(int m =1;m<=ExcelReader.K;++m){
					K_Mean.main(m);
					ExcelWriterPOI.main(m);
					SqlToArff.main(m);
					error[m-1][0][0]=SqlToArff.error[0][0];
					error[m-1][0][1]=SqlToArff.error[0][1];
					error[m-1][1][0]=SqlToArff.error[1][0];
					error[m-1][1][1]=SqlToArff.error[1][1];
				}
				//ConcludeMinimum() function should be called only for multiple values of K
				concludeMinimum();
		break;
		case 2: ClusterCenterInputClustering.main();
				SortCluster.main();
				ExcelWriterPOI.main(ExcelReader.K);
				SqlToArff.main(ExcelReader.K);
		break;
		default: 	System.out.println("\n================================================================================================================================================================");
					Printer.error("Make Proper Choice");
					System.out.println("\n================================================================================================================================================================");
		}
	
	}

}
