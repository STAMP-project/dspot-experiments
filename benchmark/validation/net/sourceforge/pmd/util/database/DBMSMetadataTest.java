/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;


import java.io.Reader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 *
 * @author sturton
 */
@Ignore
public class DBMSMetadataTest {
    static final String C_ORACLE_THIN_1 = "jdbc:oracle:thin:scott/tiger@//192.168.100.21:5521/customer_db?characterset=utf8&schemas=scott,hr,sh,system&objectTypes=procedures,functions,triggers,package,types&languages=plsql,java&name=PKG_%25%7C%7CPRC_%25";

    static final String C_ORACLE_THIN_3 = "jdbc:oracle:thin:scott/oracle@//192.168.100.21:1521/orcl?characterset=utf8&schemas=scott,hr,sh,system&objectTypes=procedures,functions,triggers,package,types&languages=plsql,java&name=PKG_%25%7C%7CPRC_%25";

    static final String C_ORACLE_THIN_4 = "jdbc:oracle:thin:system/oracle@//192.168.100.21:1521/ORCL?characterset=utf8&schemas=scott,hr,sh,system&objectTypes=procedures,functions,triggers,package,types&languages=plsql,java&name=PKG_%25%7C%7CPRC_%25";

    static final String C_ORACLE_THIN_5 = "jdbc:oracle:thin:@//192.168.100.21:1521/ORCL?characterset=utf8&schemas=scott,hr,sh,system&objectTypes=procedures,functions,triggers,package,types&languages=plsql,java&name=PKG_%25%7C%7CPRC_%25&amp;user=system&amp;password=oracle";

    /**
     * URI with minimum information, relying on defaults in
     * testdefaults.properties
     */
    static final String C_TEST_DEFAULTS = "jdbc:oracle:testdefault://192.168.100.21:1521/ORCL";

    private DBURI dbURI;

    private DBURI dbURI4;

    private DBURI dbURI5;

    private DBURI dbURIDefault;

    public DBMSMetadataTest() throws Exception, URISyntaxException {
        dbURI = new DBURI(DBMSMetadataTest.C_ORACLE_THIN_3);
        dbURI4 = new DBURI(DBMSMetadataTest.C_ORACLE_THIN_4);
        dbURI5 = new DBURI(DBMSMetadataTest.C_ORACLE_THIN_5);
        dbURIDefault = new DBURI(DBMSMetadataTest.C_TEST_DEFAULTS);
    }

    /**
     * Verify getConnection method, of class DBMSMetadata.
     */
    @Test
    public void testGetConnection() throws Exception {
        System.out.println("getConnection");
        String driverClass = dbURI.getDriverClass();
        System.out.println(("driverClass==" + driverClass));
        System.out.println(("URL==" + (dbURI.getURL())));
        Class.forName(driverClass);
        Object object = DriverManager.getDriver(dbURI.getURL());
        // Object object = DriverManager.getDriver(C_ORACLE_OCI_3) ;
        Properties properties = new Properties();
        properties.put("user", "system");
        properties.put("password", "oracle");
        Connection expResult = DriverManager.getDriver(dbURI.getURL()).connect(dbURI.getURL(), properties);
        DBMSMetadata instance = new DBMSMetadata(dbURI);
        Connection result = instance.getConnection();
        Assert.assertNotNull(result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify getConnection method, of class DBMSMetadata.
     */
    @Test
    public void testGetConnectionWithConnectionParameters() throws Exception {
        System.out.println("getConnection");
        String driverClass = dbURI5.getDriverClass();
        System.out.println(("driverClass==" + driverClass));
        System.out.println(("URL==" + (dbURI5.getURL())));
        Class.forName(driverClass);
        Object object = DriverManager.getDriver(dbURI5.getURL());
        // Object object = DriverManager.getDriver(C_ORACLE_OCI_3) ;
        Properties properties = new Properties();
        properties.putAll(dbURI5.getParameters());
        Connection expResult = DriverManager.getDriver(dbURI5.getURL()).connect(dbURI5.getURL(), properties);
        DBMSMetadata instance = new DBMSMetadata(dbURI5);
        Connection result = instance.getConnection();
        Assert.assertNotNull(result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSourceCode method, of class DBMSMetadata.
     */
    @Test
    public void testGetSourceCode() throws Exception {
        System.out.println("getSourceCode");
        // String objectType = "PACKAGE";
        // String name = "DBMS_REPCAT_AUTH";
        // String schema = "SYSTEM";
        String objectType = "TABLE";
        String name = "EMP";
        String schema = "SCOTT";
        System.out.println(("dbURI.driverClass==" + (dbURI.getDriverClass())));
        System.out.println(("dbURI.URL==" + (dbURI.getURL())));
        System.out.println(("dbURI.getDBType.getProperties()==" + (dbURI.getDbType().getProperties())));
        System.out.println(("dbURI.getDBType.getSourceCodeReturnType()==" + (dbURI.getDbType().getSourceCodeReturnType())));
        System.out.println(("dbURI.getDBType.getProperties()==" + (dbURI.getDbType().getProperties().getProperty("getSourceCodeStatement"))));
        DBMSMetadata instance = new DBMSMetadata(dbURI);
        Reader expResult = null;
        Reader result = instance.getSourceCode(objectType, name, schema);
        /* StringBuilder stringBuilder = new StringBuilder(1024); char[]
        charArray = new char[1024]; int readChars = 0; while(( readChars =
        result.read(charArray)) > 0 ) {
        System.out.println("Reader.read(CharArray)=="+readChars);
        stringBuilder.append(charArray, 0, readChars); } result.close();

        System.out.println("getSourceCode()==\""+stringBuilder.toString()+
        "\"" );

        assertTrue(stringBuilder.toString().startsWith("\n  CREATE "));
         */
        String resultString = DBMSMetadataTest.getStringFromReader(result);
        System.out.println(("getSourceCode()==\"" + resultString));
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify getSchemas method, of class DBMSMetadata.
     */
    @Test
    public void testGetSchemas() throws Exception {
        System.out.println("getSchemas");
        DBURI testURI = dbURI4;
        String driverClass = testURI.getDriverClass();
        System.out.println(("driverClass==" + driverClass));
        System.out.println(("URL==" + (testURI.getURL())));
        Class.forName(driverClass);
        Object object = DriverManager.getDriver(testURI.getURL());
        // Object object = DriverManager.getDriver(C_ORACLE_OCI_3) ;
        Properties properties = new Properties();
        properties.put("user", "system");
        properties.put("password", "oracle");
        Connection expResult = DriverManager.getDriver(testURI.getURL()).connect(testURI.getURL(), properties);
        DBMSMetadata instance = new DBMSMetadata(testURI);
        Connection result = instance.getConnection();
        Assert.assertNotNull(result);
        ResultSet allSchemas = result.getMetaData().getSchemas();
        DBMSMetadataTest.dumpResultSet(allSchemas, "All Schemas");
        ResultSet allCatalogues = result.getMetaData().getCatalogs();
        DBMSMetadataTest.dumpResultSet(allCatalogues, "All Catalogues");
        String catalog = null;
        String schemasPattern = "PHPDEMO";
        String tablesPattern = null;
        String proceduresPattern = null;
        // Not until Java6 ResultSet matchedSchemas =
        // result.getMetaData().getSchemas(catalog, schemasPattern) ;
        // Not until Java6 dumpResultSet (matchedSchemas, "Matched Schemas") ;
        ResultSet matchedTables = result.getMetaData().getTables(catalog, schemasPattern, tablesPattern, null);
        DBMSMetadataTest.dumpResultSet(matchedTables, "Matched Tables");
        ResultSet matchedProcedures = result.getMetaData().getProcedures(catalog, schemasPattern, proceduresPattern);
        DBMSMetadataTest.dumpResultSet(matchedProcedures, "Matched Procedures");
        System.out.format("testURI=%s,\ngetParameters()=%s\n", DBMSMetadataTest.C_ORACLE_THIN_4, testURI.getParameters());
        System.out.format("testURI=%s,\ngetSchemasList()=%s\n,getSourceCodeTypesList()=%s\n,getSourceCodeNmesList()=%s\n", testURI, testURI.getSchemasList(), testURI.getSourceCodeTypesList(), testURI.getSourceCodeNamesList());
    }

    /**
     * Verify getSchemas method, of class DBMSMetadata.
     */
    @Test
    public void testGetSourceObjectList() throws Exception {
        System.out.println("getConnection");
        DBURI testURI = dbURI4;
        String driverClass = testURI.getDriverClass();
        System.out.println(("driverClass==" + driverClass));
        System.out.println(("URL==" + (testURI.getURL())));
        Class.forName(driverClass);
        Object object = DriverManager.getDriver(testURI.getURL());
        // Object object = DriverManager.getDriver(C_ORACLE_OCI_3) ;
        Properties properties = new Properties();
        properties.put("user", "system");
        properties.put("password", "oracle");
        Connection expResult = DriverManager.getDriver(testURI.getURL()).connect(testURI.getURL(), properties);
        DBMSMetadata instance = new DBMSMetadata(testURI);
        Connection result = instance.getConnection();
        Assert.assertNotNull(result);
        List<SourceObject> sourceObjectList = instance.getSourceObjectList();
        Assert.assertNotNull(sourceObjectList);
        System.out.format("testURI=%s,\ngetParameters()=%s\n", DBMSMetadataTest.C_ORACLE_THIN_4, testURI.getParameters());
        System.out.format("testURI=%s,\ngetSchemasList()=%s\n,getSourceCodeTypesList()=%s\n,getSourceCodeNmesList()=%s\n", testURI, testURI.getSchemasList(), testURI.getSourceCodeTypesList(), testURI.getSourceCodeNamesList());
        System.out.printf("sourceObjectList ...\n");
        for (SourceObject sourceObject : sourceObjectList) {
            System.out.printf("sourceObject=%s\n", sourceObject);
            System.out.printf("sourceCode=[%s]\n", DBMSMetadataTest.getStringFromReader(instance.getSourceCode(sourceObject)));
        }
    }
}

