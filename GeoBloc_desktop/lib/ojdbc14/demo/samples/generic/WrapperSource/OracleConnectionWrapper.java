package oracle.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * A simple implementation of a connection wrapper which may be nested to any depth.
 */

public class OracleConnectionWrapper implements oracle.jdbc.OracleConnection
{
  protected oracle.jdbc.OracleConnection connection;

  public OracleConnectionWrapper() {}

  /**
   * Construct an instance which wraps the arguement
   * @param toBeWrapped
   */
  public OracleConnectionWrapper(oracle.jdbc.OracleConnection toBeWrapped)
  {
    super();

    connection = toBeWrapped;

    toBeWrapped.setWrapper((oracle.jdbc.OracleConnection) this);
  }

  /**
   * Unwrap one level.
   * Returns the connection within this wrapper.
   * @return
   */
  public oracle.jdbc.OracleConnection unwrap()
  {
    return connection;
  }


  // perhaps temporary gateway to the above while newdriver is distinct
  // from driver
  public oracle.jdbc.internal.OracleConnection physicalConnectionWithin()
  {
    return connection.physicalConnectionWithin();
  }

  /**
   * Set a connection wrapper as the wrapper of this connection.
   * Recursively sets the wrapper to the lowest level.
   * Thus the physical connection will always know its outermost wrapper
   * The recursion is terminated by the method in oracle.jdbc.driver.OracleConnction
   * which stores its argument.
   * @param wrapper
   * @ See the methods getJavaSqlConnection and getOracleConnection
   * @ in oracle.jdbc.driver.OracleConnection
   */
  public void setWrapper(oracle.jdbc.OracleConnection wrapper)
  {
    connection.setWrapper(wrapper);
  }

  /* override methods in Object to forward them */

  // none at present

  /* implement java.sql.Connection */

  public Statement createStatement() throws SQLException
  {
    return connection.createStatement();
  }

  public PreparedStatement prepareStatement(String sql) throws SQLException
  {
    return connection.prepareStatement(sql);
  }

  public CallableStatement prepareCall(String sql) throws SQLException
  {
    return connection.prepareCall(sql);
  }

  public String nativeSQL(String sql) throws SQLException
  {
    return connection.nativeSQL(sql);
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException
  {
    connection.setAutoCommit(autoCommit);
  }

  public boolean getAutoCommit() throws SQLException
  {
    return connection.getAutoCommit();
  }

  public void commit() throws SQLException
  {
    connection.commit();
  }

  public void rollback() throws SQLException
  {
    connection.rollback();
  }

  public void close() throws SQLException
  {
    connection.close();
  }

  public boolean isClosed() throws SQLException
  {
    return connection.isClosed();
  }

  public DatabaseMetaData getMetaData() throws SQLException
  {
    return connection.getMetaData();
  }

  public void setReadOnly(boolean readOnly) throws SQLException
  {
    connection.setReadOnly(readOnly);
  }

  public boolean isReadOnly() throws SQLException
  {
    return connection.isReadOnly();
  }

  public void setCatalog(String catalog) throws SQLException
  {
    connection.setCatalog(catalog);
  }

  public String getCatalog() throws SQLException
  {
    return connection.getCatalog();
  }

  public void setTransactionIsolation(int level) throws SQLException
  {
    connection.setTransactionIsolation(level);
  }

  public int getTransactionIsolation() throws SQLException
  {
    return connection.getTransactionIsolation();
  }

  public SQLWarning getWarnings() throws SQLException
  {
    return connection.getWarnings();
  }

  public void clearWarnings() throws SQLException
  {
    connection.clearWarnings();
  }

  public Statement createStatement(int resultSetType, 
				   int resultSetConcurrency) throws SQLException
  {
    return connection.createStatement(resultSetType, resultSetConcurrency);
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, 
					    int resultSetConcurrency) throws SQLException
  {
    return connection.prepareStatement(sql, resultSetType, 
				       resultSetConcurrency);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, 
				       int resultSetConcurrency) throws SQLException
  {
    return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
  }

  public java.util.Map getTypeMap() throws SQLException
  {
    return connection.getTypeMap();
  }

  public void setTypeMap(java.util.Map map) throws SQLException
  {
    connection.setTypeMap(map);
  }




  /* implement oracle.jdbc.OracleConnection methods */
  public boolean isProxySession()
  {
    return connection.isProxySession();
  }

  public void openProxySession(int mode, 
			       java.util.Properties prop) throws SQLException
  {
    connection.openProxySession(mode, prop);
  }

  public void archive(int mode, int aseq, String acstext) throws SQLException
  {
    connection.archive(mode, aseq, acstext);
  }

  public boolean getAutoClose() throws SQLException
  {
    return connection.getAutoClose();
  }

  public CallableStatement getCallWithKey(String key) throws SQLException
  {
    return connection.getCallWithKey(key);
  }

  public int getDefaultExecuteBatch()
  {
    return connection.getDefaultExecuteBatch();
  }

  public int getDefaultRowPrefetch()
  {
    return connection.getDefaultRowPrefetch();
  }

  public Object getDescriptor(String sql_name)
  {
    return connection.getDescriptor(sql_name);
  }

  public String[] getEndToEndMetrics() throws SQLException
  {
    return connection.getEndToEndMetrics();
  }

  public short getEndToEndECIDSequenceNumber() throws SQLException
  {
    return connection.getEndToEndECIDSequenceNumber();
  }

  public boolean getIncludeSynonyms()
  {
    return connection.getIncludeSynonyms();
  }

  public boolean getRestrictGetTables()
  {
    return connection.getRestrictGetTables();
  }

  public boolean getImplicitCachingEnabled() throws SQLException
  {
    return connection.getImplicitCachingEnabled();
  }

  public boolean getExplicitCachingEnabled() throws SQLException
  {
    return connection.getExplicitCachingEnabled();
  }


  public Object getJavaObject(String sql_name) throws SQLException
  {
    return connection.getJavaObject(sql_name);
  }

  public boolean getRemarksReporting()
  {
    return connection.getRemarksReporting();
  }

  public String getSQLType(Object obj) throws SQLException
  {
    return connection.getSQLType(obj);
  }

  public int getStmtCacheSize()
  {
    return connection.getStmtCacheSize();
  }

  public int getStatementCacheSize() throws SQLException
  {
    return connection.getStatementCacheSize();
  }

  public PreparedStatement getStatementWithKey(String key) throws SQLException
  {
    return connection.getStatementWithKey(key);
  }

  public short getStructAttrCsId() throws SQLException
  {
    return connection.getStructAttrCsId();
  }

  public String getUserName() throws SQLException
  {
    return connection.getUserName();
  }

  public String getCurrentSchema() throws SQLException
  {
    return connection.getCurrentSchema();
  }

  public boolean getUsingXAFlag()
  {
    return connection.getUsingXAFlag();
  }

  public boolean getXAErrorFlag()
  {
    return connection.getXAErrorFlag();
  }

  public OracleSavepoint oracleSetSavepoint() throws SQLException
  {
    return connection.oracleSetSavepoint();
  }

  public OracleSavepoint oracleSetSavepoint(String name) throws SQLException
  {
    return connection.oracleSetSavepoint(name);
  }

  public void oracleRollback(OracleSavepoint savepoint) throws SQLException
  {
    connection.oracleRollback(savepoint);
  }

  public void oracleReleaseSavepoint(OracleSavepoint savepoint) 
    throws SQLException
  {
    connection.oracleReleaseSavepoint(savepoint);
  }



  public int pingDatabase(int timeOut) throws SQLException
  {
    return connection.pingDatabase(timeOut);
  }

  public void purgeExplicitCache() throws SQLException
  {
    connection.purgeExplicitCache();
  }

  public void purgeImplicitCache() throws SQLException
  {
    connection.purgeImplicitCache();
  }

  public void putDescriptor(String sql_name, Object desc) throws SQLException
  {
    connection.putDescriptor(sql_name, desc);
  }

  public void registerSQLType(String sql_name, 
			      Class java_class) throws SQLException
  {
    connection.registerSQLType(sql_name, java_class);
  }

  public void registerSQLType(String sql_name, 
			      String java_class_name) throws SQLException
  {
    connection.registerSQLType(sql_name, java_class_name);
  }

  public void setAutoClose(boolean autoClose) throws SQLException
  {
    connection.setAutoClose(autoClose);
  }

  public void setDefaultExecuteBatch(int batch) throws SQLException
  {
    connection.setDefaultExecuteBatch(batch);
  }

  public void setDefaultRowPrefetch(int value) throws SQLException
  {
    connection.setDefaultRowPrefetch(value);
  }

  public void setEndToEndMetrics(String[] metrics, 
				 short sequenceNumber) throws SQLException
  {
    connection.setEndToEndMetrics(metrics, sequenceNumber);
  }

  public void setExplicitCachingEnabled(boolean cache) throws SQLException
  {
    connection.setExplicitCachingEnabled(cache);
  }

  public void setImplicitCachingEnabled(boolean cache) throws SQLException
  {
    connection.setImplicitCachingEnabled(cache);
  }

  public void setIncludeSynonyms(boolean synonyms)
  {
    connection.setIncludeSynonyms(synonyms);
  }

  public void setRemarksReporting(boolean reportRemarks)
  {
    connection.setRemarksReporting(reportRemarks);
  }

  public void setRestrictGetTables(boolean restrict)
  {
    connection.setRestrictGetTables(restrict);
  }

  public void setStmtCacheSize(int size) throws SQLException
  {
    connection.setStmtCacheSize(size);
  }

  public void setStatementCacheSize(int size) throws SQLException
  {
    connection.setStatementCacheSize(size);
  }

  public void setStmtCacheSize(int size, 
			       boolean clearMetaData) throws SQLException
  {
    connection.setStmtCacheSize(size, clearMetaData);
  }

  public void setUsingXAFlag(boolean value)
  {
    connection.setUsingXAFlag(value);
  }

  public void setXAErrorFlag(boolean value)
  {
    connection.setXAErrorFlag(value);
  }

  public void shutdown(int mode) throws SQLException
  {
    connection.shutdown(mode);
  }

  public void startup(String startup_str, int mode) throws SQLException
  {
    connection.startup(startup_str, mode);
  }

  public PreparedStatement prepareStatementWithKey(String key) 
    throws SQLException
  {
    return connection.prepareStatementWithKey(key);
  }

  public CallableStatement prepareCallWithKey(String key) throws SQLException
  {
    return connection.prepareCallWithKey(key);
  }


  public void setCreateStatementAsRefCursor(boolean value)
  {
    connection.setCreateStatementAsRefCursor(value);
  }

  public boolean getCreateStatementAsRefCursor()
  {
    return connection.getCreateStatementAsRefCursor();
  }

  public void setSessionTimeZone(String regionName) throws SQLException
  {
    connection.setSessionTimeZone(regionName);
  }

  public String getSessionTimeZone()
  {
    return connection.getSessionTimeZone();
  }

  public Connection _getPC()
  {
    return connection._getPC();
  }

  public boolean isLogicalConnection()
  {
    return connection.isLogicalConnection();
  }

  public void registerTAFCallback(oracle.jdbc.OracleOCIFailover cbk, 
				  Object obj) throws java.sql.SQLException
  {
    connection.registerTAFCallback(cbk, obj);
  }


  public java.util.Properties getProperties()
  {
    return connection.getProperties();
  }

  // Implicit Connection Cache APIs
  public void close(java.util.Properties connAttr) throws SQLException
  {
    connection.close(connAttr);
  }

  public void close(int opt) throws SQLException
  {
    connection.close(opt);
  }

  public void applyConnectionAttributes(java.util.Properties connAttr) 
    throws SQLException
  {
    connection.applyConnectionAttributes(connAttr);
  }

  public java.util.Properties getConnectionAttributes() throws SQLException
  {
    return connection.getConnectionAttributes();
  }

  public java.util.Properties getUnMatchedConnectionAttributes() 
    throws SQLException
  {
    return connection.getUnMatchedConnectionAttributes();
  }

  public void registerConnectionCacheCallback(oracle.jdbc.pool.OracleConnectionCacheCallback occc, 
					      Object userObj, 
					      int cbkFlag) throws SQLException
  {
    connection.registerConnectionCacheCallback(occc, userObj, cbkFlag);
  }

  public void setConnectionReleasePriority(int priority) throws SQLException
  {
    connection.setConnectionReleasePriority(priority);
  }

  public int getConnectionReleasePriority() throws SQLException
  {
    return connection.getConnectionReleasePriority();
  }

  public void setPlsqlWarnings(String setting) throws SQLException
  {
    connection.setPlsqlWarnings(setting);
  }

  public void setHoldability(int holdability) throws SQLException
  {
    connection.setHoldability(holdability);
  }

  public int getHoldability() throws SQLException
  {
    return connection.getHoldability();
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency,                                  int resultSetHoldability) throws SQLException
  {
    return connection.createStatement(resultSetType, resultSetConcurrency,
                                      resultSetHoldability);
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType,
                                            int resultSetConcurrency,
                                            int resultSetHoldability) throws SQLException
  {
    return connection.prepareStatement(sql, resultSetType,
                                       resultSetConcurrency,
                                       resultSetHoldability);
  }

  public CallableStatement prepareCall(String sql, int resultSetType,
                                       int resultSetConcurrency,
                                       int resultSetHoldability) throws SQLException
  {
    return connection.prepareCall(sql, resultSetType, resultSetConcurrency,
                                  resultSetHoldability);
  }


  // JDBC 3.0 methods


  // -----------------------------------------------------------------------
  // |     |     |
  // V     V     V
  // -----------------------------------------------------------------------



  m4_ifJDK14({:
  public synchronized java.sql.Savepoint setSavepoint() throws SQLException
  {
    return connection.setSavepoint();
  }

  public synchronized java.sql.Savepoint setSavepoint(String name) 
    throws SQLException
  {
    return connection.setSavepoint(name);
  }

  public synchronized void rollback(java.sql.Savepoint savepoint) 
    throws SQLException
  {
    connection.rollback(savepoint);
  }

  public synchronized void releaseSavepoint(java.sql.Savepoint savepoint) 
    throws SQLException
  {
    connection.releaseSavepoint(savepoint);
  }

  :});



  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) 
    throws SQLException
  {
    return connection.prepareStatement(sql, autoGeneratedKeys);
  }

  public PreparedStatement prepareStatement(String sql, int columnIndexes[]) 
    throws SQLException
  {
    return connection.prepareStatement(sql, columnIndexes);
  }

  public PreparedStatement prepareStatement(String sql, String columnNames[]) 
    throws SQLException
  {
    return connection.prepareStatement(sql, columnNames);
  }


  // -----------------------------------------------------------------------
  // ^     ^     ^
  // |     |     |
  // -----------------------------------------------------------------------


  m4_initialize("oracle.jdbc.OracleConnectionWrapper");
}


/**
 * @version $Header: OracleConnectionWrapper.java 24-may-2004.12:15:35 rchen Exp $
 * @author  eshirk
 * @since   9.2
 */


