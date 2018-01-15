package flatMain;

import java.sql.*;
import java.io.IOException;
import java.util.*;
import flatMain.ReadFile;
import java.io.File;

// Currently only works for DD_JOURNEY_SEGMENT

enum InputArgs {
	SEARCH_TABLES(0),
	SEARCH_PARAMS(1),
	EXPECTED_RESULTS(2),
	OUTPUT_FILENAME(3),
	SUPRESS_PRINTING(4);
	
	private int inputArgsNo;
	
	InputArgs(int inputArgsNo) {
		this.inputArgsNo = inputArgsNo;
	}
	
	public int inputArgsNo() {
		return inputArgsNo;
	}
}

public class Runner {
	public static void main(String[] arg) {
		System.out.println("Connecting to database...");
		
		
		String workingDir = "common";
		workingDir = UnitTest.getWorkingDir(workingDir);
		
		// read in the test inputs file
		String test_inputs = workingDir + arg[InputArgs.SEARCH_PARAMS.inputArgsNo()];
		ReadFile testInputs = new ReadFile(test_inputs);
		
		// read in the test expected values file
		String expected_result = workingDir + arg[InputArgs.EXPECTED_RESULTS.inputArgsNo()];
		ReadFile expectedResults = new ReadFile(expected_result);
		
		// read in the test tables file
		String test_tables = workingDir + arg[InputArgs.SEARCH_TABLES.inputArgsNo()];
		ReadFile testTables = new ReadFile(test_tables);
		
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
					String expectedValueFields[] = expectedResults.OpenFileCols();
					String expectedValueValues[] = expectedResults.OpenFileValues();
					
					String testTablesList[] = testTables.OpenFile();
					
					String tempStr1 = null;
					String tempStr2 = null;
					
					// Define SQL search parameters
					tempStr1 = inputFields[0];
					tempStr2 = inputFields[0] + " in " + inputValues[0];
					for (i = 1; i < inputFields.length; i++) {
						tempStr1 = tempStr1 + ", " + inputFields[i];
						tempStr2 = tempStr2 + " and " + inputFields[i] + " in " + inputValues[i];
					}
					//String inputFieldsString = tempStr1; // Desired search field headers, not used
					String inputValuesString = tempStr2; // Search field values
					
					// Define SQL expected results parameters
					tempStr1 = expectedValueFields[0];
					for (i = 1; i < expectedValueFields.length; i++) {
						tempStr1 = tempStr1 + ", " + expectedValueFields[i];
					}
					String expectedValuesString = tempStr1; // Desired expected value fields
					
					stmt = connection.createStatement();
					// Define query
					rs = stmt.executeQuery("SELECT " + expectedValuesString + " from " + testTablesList[0] + " where " + inputValuesString );
					
					// Print header
					String[] resultString = new String[expectedValueFields.length];					
					
					// Print all rows
					int j = 0;					
					rs.next(); // note that program will quit here if ResultSet is empty
					while (!rs.isAfterLast()) {
						for (j = 1; j < expectedValueFields.length + 1; j++) {
							//System.out.print(rs.getString(j));
							resultString[j-1] = rs.getString(j);
						}
						rs.next();
					}
					for (j = 0; j < expectedValueFields.length; j++) {
						expectedValueValues[j] = expectedValueValues[j].replaceAll("'", "");
					}
					
					// Only print results if args say so
					if (!arg[InputArgs.SUPRESS_PRINTING.inputArgsNo()].matches("Y") ) {
						System.out.println("Test Results: Actual (Expected)"); // Results
						for(i = 0; i < expectedValueFields.length; i++) {
							expectedValueFields[i] = expectedValueFields[i].toUpperCase();
							System.out.print(expectedValueFields[i] + ": " + resultString[i].toUpperCase() + " (" + expectedValueValues[i].toUpperCase() + ")\n");
							
						}
					}
					
					String outputFile = arg[InputArgs.OUTPUT_FILENAME.inputArgsNo()]; // name of output .csv file
					UnitTest.csvOut(outputFile, UnitTest.strArray2String(expectedValueFields), UnitTest.strArray2String(resultString), UnitTest.strArray2String(expectedValueValues));
					
					System.out.println("Evaluation: "); // Compare Actual + Expected	
					int numMismatch = 0;
					for (j = 0; j < expectedValueFields.length; j++) {
						// look for any mismatches then print if true
						if (!expectedValueValues[j].trim().equals(resultString[j].trim())) {
							System.out.println("Actual " + expectedValueFields[j].toUpperCase() + " = " + resultString[j]
									+ ", expected = " + expectedValueValues[j].replaceAll("'", ""));
							numMismatch += 1;
						}
					}
					if (numMismatch == 0) {
						System.out.println("Test successful! No mismatches.");
					} else {
						System.out.println("Test failed!");
						System.out.println(numMismatch + " (of " + expectedValueFields.length + ") possible mismatches detected.");
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
		
		
