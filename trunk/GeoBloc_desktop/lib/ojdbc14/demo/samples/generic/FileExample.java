/* 
 * This sample demonstrates how to use DIRECTORY object.
 * 
 * 1. It creates a DIRECTORY object specifies an alias for a directory
 * on the server's file system where external binary file are
 * located. By doing so, you can use directory names when referring
 * to BFILEs rather than hard-coding the operating system pathname
 * thereby allowing greater file management flexibility.
 *
 * 2. It opens, reads, and dumps two binary files (file1.bin, file2.bin)
 * stored at DIRECTORY.
 *
 * 3. usage: java FileExample <test_dir>
 *        test_dir is a location where file1.bin and file2.bin are pre-stored 
 *        This file is to insert into a table with the contents
 *        of file1.bin and file2.bin. file1.bin and file2.bin are of binary 
 *        files 
 *
 * 4. It drops, creates, and populates table test_dir_table into the
 * database.
 *
 * 5. Please use jdk1.2 or later version and classes12.zip  
 */

import java.sql.*;
import java.io.*;
import java.util.*;

//including this import makes the code easier to read
import oracle.jdbc.*;
import oracle.jdbc.pool.OracleDataSource;

// needed for new BFILE class
import oracle.sql.*;

public class FileExample
{
  public static void main (String args [])
       throws Exception
  {
    if ( args.length != 1 )
    {
        System.out.println("usage: java FileExample <test_dir>");
        System.exit(0);
    }

    // The sample creates a DIRECTORY and you have to be connected as
    // "system" to be able to run the test.
    // If you can't connect as "system" have your system manager
    // create the directory for you, grant you the rights to it, and
    // remove the portion of this program that drops and creates the directory.

    String url = "jdbc:oracle:oci8:@";
    try {
      String url1 = System.getProperty("JDBC_URL");
      if (url1 != null)
        url = url1;
    } catch (Exception e) {
      // If there is any security exception, ignore it
      // and use the default
    }

    // Create an OracleDataSource instance 
    OracleDataSource ods = new OracleDataSource();
    ods.setUser("system");
    ods.setPassword("manager");
    ods.setURL(url);

    // Connect to the database
    Connection conn = ods.getConnection();

    // It's faster when auto commit is off
    conn.setAutoCommit (false);

    // Create a Statement
    Statement stmt = conn.createStatement ();

    try
    {
      stmt.execute ("drop directory TEST_DIR");
    }
    catch (SQLException e)
    {
      // An error is raised if the directory does not exist.  Just ignore it.
    }

    stmt.execute ("create directory TEST_DIR as '" + args[0] + "'");

    try
    {
      stmt.execute ("drop table test_dir_table");
    }
    catch (SQLException e)
    {
      // An error is raised if the table does not exist.  Just ignore it.
    }

    // Create and populate a table with files
    // The files file1.bin and file2.bin must exist in the directory 
    // TEST_DIR created above as symbolic name for args[0].
    stmt.execute ("create table test_dir_table (x varchar2 (30), b bfile)");
    stmt.execute ("insert into test_dir_table values ('one', bfilename ('TEST_DIR', 'file1.bin'))");
    stmt.execute ("insert into test_dir_table values ('two', bfilename ('TEST_DIR', 'file2.bin'))");

    // Select the file from the table
    ResultSet rset = stmt.executeQuery ("select * from test_dir_table");
    while (rset.next ())
    {
      String x = rset.getString (1);
      BFILE bfile = ((OracleResultSet)rset).getBFILE (2);
      System.out.println (x + " " + bfile);

      // Dump the file contents
      dumpBfile (conn, bfile);
    }

    // Close all resources
    rset.close();
    stmt.close();
    conn.close();
  }

  // Utility function to dump the contents of a Bfile
  static void dumpBfile (Connection conn, BFILE bfile)
    throws Exception
  {
    System.out.println ("Dumping file " + bfile.getName());
    System.out.println ("File exists: " + bfile.fileExists());
    System.out.println ("File open: " + bfile.isFileOpen());

    System.out.println ("Opening File: ");

    bfile.openFile();

    System.out.println ("File open: " + bfile.isFileOpen());

    long length = bfile.length();
    System.out.println ("File length: " + length);

    int chunk = 10;

    InputStream instream = bfile.getBinaryStream();

    // Create temporary buffer for read
    byte[] buffer = new byte[chunk];

    // Fetch data  
    while ((length = instream.read(buffer)) != -1)
    {
      System.out.print("Read " + length + " bytes: ");

      for (int i=0; i<length; i++)
        System.out.print(buffer[i]+" ");
      System.out.println();
    }

    // Close input stream
    instream.close();
 
    // close file handler
    bfile.closeFile();
  }
}
