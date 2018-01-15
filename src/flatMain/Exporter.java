// FLAT Exporter
// Exports CCS tables to CSV format for quick and easy evidence attachment to Test Runs
// Exporter.java -test_tables -showall -output_file 
// todo - join HEADER table with any TAG_ table search - DONE, ability to customise which fields to be displayed

package flatMain;

import java.sql.*;
import java.io.IOException;
//import java.util.*;
import flatMain.ReadFile;
//import java.io.File;


enum ExporterArgs {
	SEARCH_TABLES(0),
	SEARCH_PARAMS(1),
	OUTPUT_FILENAME(2),
	OUTPUT_PRIORI(3);
	
	private int exporterArgsNo;
	
	ExporterArgs(int exporterArgsNo) {
		this.exporterArgsNo = exporterArgsNo;
	}
	
	public int exporterArgsNo() {
		return exporterArgsNo;
	}
}

public class Exporter {
	public static void main(String[] arg) {
		System.out.println("Connecting to database...");
		
		//TableView rtdTagOnSv = new TableView();
		//rtdTagOnSv.setChosenField("helli");
		
		String workingDir = "exporter";
		workingDir = UnitTest.getWorkingDir(workingDir);
		
		// read in the test inputs file
		String test_inputs = workingDir + arg[ExporterArgs.SEARCH_PARAMS.exporterArgsNo()];
		ReadFile testInputs = new ReadFile(test_inputs);
		
		// read in the test tables file
		String test_tables = workingDir + arg[ExporterArgs.SEARCH_TABLES.exporterArgsNo()];
		ReadFile testTables = new ReadFile(test_tables);
		
		// read in the output prioritisation file
		
		try {
			// CUBIC ITF DETAILS:
			String url = "jdbc:oracle:thin:@//10.7.26.64:1521/";
			String schema = "etscubitf1";
			String username = "CDSREADONLY";
			String password = "CDSREADONLY";	
			

			url = url + schema;
			try (Connection connection = DriverManager.getConnection(url, username, password)) { // connect to DB
				System.out.println("Connected to database!");
				
				Statement stmt = null;
				ResultSet rs = null;
				
				// PUT ACTION IN HERE
				try {
					// MAIN ACTION BLOCK
					
					// init counter for utility purposes
					int i = 0;

					String inputFields[] = testInputs.OpenFileCols();
					String inputValues[] = testInputs.OpenFileValues();
					
					String testTablesList[] = testTables.OpenFile();
					
					String tempStr1 = null;
					String tempStr2 = null;
					String tempStr3 = null;
					
					// Define SQL search parameters
					tempStr1 = inputFields[0];
					tempStr2 = inputFields[0] + " in " + inputValues[0];
					tempStr3 = "a." + inputFields[0] + " in " + inputValues[0];
					for (i = 1; i < inputFields.length; i++) {
						tempStr1 = tempStr1 + ", " + inputFields[i];
						tempStr2 = tempStr2 + " and " + inputFields[i] + " in " + inputValues[i];
						tempStr3 = tempStr3 + " and a." + inputFields[i] + " in " + inputValues[i];
					}

					String inputValuesString = tempStr2; // Search field values
					String inputValuesStringRtd = tempStr3;
					
					int numTables = 0;
					// Iterate for all tables in test_tables.txt
					for (numTables = 0; numTables < testTablesList.length; numTables++) {
						// Write the headers for each table in the CSV file
						String initPrint = "from " + testTablesList[numTables] + " where " + inputFields[0] + " = " + inputValues[0];
						for (i = 1; i < inputFields.length; i++) {
							initPrint = initPrint + " and " + inputFields[i] + " = " + inputValues[i];
						}
						
						UnitTest.csvOut(arg[ExporterArgs.OUTPUT_FILENAME.exporterArgsNo()], initPrint + "\n");
						stmt = connection.createStatement();
						
						// Define query						
						if (testTablesList[numTables].contains("TDM_RTD_TAG")) {
							rs = stmt.executeQuery("SELECT *" + " from tdm_rtd_header a, " + testTablesList[numTables] + " b" + " where " + inputValuesStringRtd
									+ " and a.daykey=b.daykey and (b.batchid=a.batchid and b.filerecordno=a.filerecordno and b.position=a.position and b.smartcardid=a.smartcardid)"
									+ " order by a.smartcardid desc, a.sequencenocard asc");
						} else if (testTablesList[numTables].contains("TDS_RTD_TAG")) {
							rs = stmt.executeQuery("SELECT *" + " from tds_rtd_header a, " + testTablesList[numTables] + " b" + " where " + inputValuesStringRtd
									+ " and a.daykey=b.daykey and (b.batchid=a.batchid and b.filerecordno=a.filerecordno and b.position=a.position and b.smartcardid=a.smartcardid)"
									+ " order by a.smartcardid desc, a.sequencenocard asc");
						} else {
							rs = stmt.executeQuery("SELECT *" + " from " + testTablesList[numTables] + " where " + inputValuesString );	
						}
						
						ResultSetMetaData metadata = rs.getMetaData();
						int columnCount = metadata.getColumnCount();
						// Export all columns
						
						String allCols = "";
						for (i = 1; i <= columnCount; i++) {
							allCols += metadata.getColumnName(i) + ",";
						}
						UnitTest.csvOut(arg[ExporterArgs.OUTPUT_FILENAME.exporterArgsNo()], allCols, "\n");
						
						while (rs.next()) {
					        String row = "";
					        for (i = 1; i <= columnCount; i++) {
					            row += rs.getString(i) + ", ";          
					        }
					        UnitTest.csvOut(arg[ExporterArgs.OUTPUT_FILENAME.exporterArgsNo()],row, "\n");    
					    }
						UnitTest.csvOut(arg[ExporterArgs.OUTPUT_FILENAME.exporterArgsNo()],"\n"); 
					}
					// END MAIN ACTION BLOCK
					
				} catch (SQLException e1) { // Print any errors
					System.out.println("SQLException: " + e1.getMessage());
					System.out.println("SQLState:" + e1.getSQLState());
					System.out.println("VendorError: " + e1.getErrorCode());
				} finally {
					closeRsStmt(rs, stmt);
				}
			} catch (SQLException e) {
				throw new IllegalStateException("Cannot connect to database!", e);
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
	
	public static void closeRsStmt(ResultSet rs, Statement stmt) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqlEx) {
				//ignore this thing
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqlEx) {
				// ignore this thing
			}
			stmt = null;
		}
	}
}
		
		
