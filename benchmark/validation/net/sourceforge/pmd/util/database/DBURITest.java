/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sturton
 */
public class DBURITest {
    /**
     * URI with minimum information, relying on defaults in
     * testdefaults.properties
     */
    static final String C_TEST_DEFAULTS = "jdbc:oracle:testdefault://192.168.100.21:1521/ORCL";

    /* Expected values from testdefaults.properties */
    static final String C_DEFAULT_USER = "scott";

    static final String C_DEFAULT_PASSWORD = "tiger";

    static final String C_DEFAULT_LANGUAGES = "java,plsql";

    static final String C_DEFAULT_SCHEMAS = "scott,system";

    static final String C_DEFAULT_SOURCE_CODE_TYPES = "table,view";

    static final String C_DEFAULT_SOURCE_CODE_NAMES = "emp,dept";

    static final String C_DEFAULT_CHARACTERSET = "utf8";

    /**
     * Fully specified URI, overriding defaults in testdefaults.properties
     */
    static final String C_TEST_EXPLICIT = "jdbc:oracle:testdefault:system/oracle@//192.168.100.21:1521/ORCL?characterset=us7ascii&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java&sourcecodenames=PKG_%25%25,PRC_%25%25";

    /* Expected values from testdefaults.properties, with values overridden by
    URI query parameters
     */
    static final String C_EXPLICIT_USER = "system";

    static final String C_EXPLICIT_PASSWORD = "oracle";

    static final String C_EXPLICIT_LANGUAGES = "plsql,java";

    static final String C_EXPLICIT_SCHEMAS = "scott,hr,sh,system";

    static final String C_EXPLICIT_SOURCE_CODE_TYPES = "procedures,functions,triggers,package,types";

    static final String C_EXPLICIT_SOURCE_CODE_NAMES = "PKG_%%,PRC_%%";

    static final String C_EXPLICIT_CHARACTERSET = "us7ascii";

    static final String C_TEST_URI = "test?param1=x%261&param2=&param3=";

    static final String C_ORACLE_OCI_1 = "jdbc:oracle:oci:system/oracle@//192.168.100.21:1521/ORCL";

    static final String C_ORACLE_OCI_2 = "jdbc:oracle:oci:system/oracle@//192.168.100.21:1521/ORCL?characterset=utf8&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java";

    static final String C_ORACLE_OCI_3 = "jdbc:oracle:oci:system/oracle@//myserver.com:1521/customer_db?characterset=utf8&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java&sourcecodenames=PKG_%25%25,PRC_%25%25";

    static final String C_ORACLE_THIN_1 = "jdbc:oracle:thin:system/oracle@//192.168.100.21:1521/ORCL";

    static final String C_ORACLE_THIN_2 = "jdbc:oracle:thin:system/oracle@//192.168.100.21:1521/ORCL?characterset=utf8&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java";

    static final String C_ORACLE_THIN_3 = "jdbc:oracle:thin:system/oracle@//myserver.com:1521/customer_db?characterset=utf8&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java&sourcecodenames=PKG_%25%25,PRC_%25%25";

    static final String C_POSTGRES_1 = "jdbc:postgresql://host/database";

    static final String C_HTTP = "http://localhost:80?characterset=utf8&schemas=scott,hr,sh,system&sourcecodetypes=procedures,functions,triggers,package,types&languages=plsql,java";

    /**
     * Test of dump method, of class DBURI.
     */
    @Test
    public void testDump() throws Exception, URISyntaxException {
        System.out.println("dump");
        String description = "";
        DBURI dburi = new DBURI(DBURITest.C_TEST_URI);
        DBURI.dump(description, dburi.getUri());
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getUri method, of class DBURI.
     */
    @Test
    public void testGetUri() throws Exception, URISyntaxException {
        System.out.println("getUri");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        URI expResult = new URI(DBURITest.C_ORACLE_OCI_1);
        URI result = instance.getUri();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setUri method, of class DBURI.
     */
    @Test
    public void testSetUri() throws Exception, URISyntaxException {
        System.out.println("setUri");
        URI uri = new URI(DBURITest.C_ORACLE_OCI_1);
        DBURI instance = new DBURI(DBURITest.C_TEST_URI);
        instance.setUri(uri);
        Assert.assertEquals(uri, instance.getUri());
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDbType method, of class DBURI.
     */
    @Test
    public void testGetDbType() throws Exception, URISyntaxException {
        System.out.println("getDbType");
        DBURI instance = new DBURI(DBURITest.C_POSTGRES_1);
        DBType expResult = new DBType("postgresql");
        DBType result = instance.getDbType();
        // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDbType method, of class DBURI.
     */
    @Test
    public void testGetDbType2() throws Exception, URISyntaxException {
        System.out.println("getDbType");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        DBType expResult = new DBType("oci");
        DBType result = instance.getDbType();
        // assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setDbType method, of class DBURI.
     */
    @Test
    public void testSetDbType() throws Exception, URISyntaxException {
        System.out.println("setDbType");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        DBType dbType = new DBType("postgresql");
        instance.setDbType(dbType);
        Assert.assertEquals(dbType, instance.getDbType());
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSchemasList method, of class DBURI.
     */
    @Test
    public void testGetSchemasList() throws Exception, URISyntaxException {
        System.out.println("getSchemasList");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        List<String> expResult;
        expResult = Arrays.asList("scott,hr,sh,system".split(","));
        List<String> result = instance.getSchemasList();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setSchemasList method, of class DBURI.
     */
    @Test
    public void testSetSchemasList() throws Exception, URISyntaxException {
        System.out.println("setSchemasList");
        List<String> schemasList = Arrays.asList("scott,hr,sh,system".split(","));
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        instance.setSchemasList(schemasList);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSourceCodeTypesList method, of class DBURI.
     */
    @Test
    public void testGetSourceCodeTypesList() throws Exception, URISyntaxException {
        System.out.println("getSourceCodeTypesList");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        List<String> expResult = Arrays.asList("procedures,functions,triggers,package,types".split(","));
        List<String> result = instance.getSourceCodeTypesList();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setSourceCodeTypesList method, of class DBURI.
     */
    @Test
    public void testSetSourceCodeTypesList() throws Exception, URISyntaxException {
        System.out.println("setSourceCodeTypesList");
        List<String> sourcecodetypesList = Arrays.asList("procedures,functions,triggers,package,types".split(","));
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        instance.setSourceCodeTypesList(sourcecodetypesList);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSourceCodeNamesList method, of class DBURI.
     */
    @Test
    public void testGetSourceCodeNamesList() throws Exception, URISyntaxException {
        System.out.println("getSourceCodeNamesList");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_3);
        List<String> expResult = Arrays.asList("PKG_%%,PRC_%%".split(","));
        List<String> result = instance.getSourceCodeNamesList();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setSourceCodeNamesList method, of class DBURI.
     */
    @Test
    public void testSetSourceCodeNamesList() throws Exception, URISyntaxException {
        System.out.println("setSourceCodeNamesList");
        List<String> sourceCodeNamesList = Arrays.asList("PKG_%%,TRG_%%".split(","));
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        instance.setSourceCodeNamesList(sourceCodeNamesList);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getLanguagesList method, of class DBURI.
     */
    @Test
    public void testGetLanguagesList() throws Exception, URISyntaxException {
        System.out.println("getLanguagesList");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        List<String> expResult = Arrays.asList("plsql,java".split(","));
        List<String> result = instance.getLanguagesList();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setLanguagesList method, of class DBURI.
     */
    @Test
    public void testSetLanguagesList() throws Exception, URISyntaxException {
        System.out.println("setLanguagesList");
        List<String> languagesList = Arrays.asList("plsql,java".split(","));
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        instance.setLanguagesList(languagesList);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDriverClass method, of class DBURI.
     */
    @Test
    public void testGetDriverClass() throws Exception, URISyntaxException {
        System.out.println("getDriverClass");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        String expResult = "oracle.jdbc.OracleDriver";
        String result = instance.getDriverClass();
        System.out.println(("testGetDriverClass: driverClass=" + result));
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getDriverClass method, of class DBURI.
     */
    @Test
    public void testGetThinDriverClass() throws Exception, URISyntaxException {
        System.out.println("getThinDriverClass");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_THIN_1);
        String expResult = "oracle.jdbc.OracleDriver";
        String result = instance.getDriverClass();
        System.out.println(("testGetThinDriverClass: driverClass=" + result));
        System.out.println("testGetThinDriverClass: getDbType().getProperties() follows");
        System.out.println(("testGetThinDriverClass: getDbType().getProperties()=" + (instance.getDbType().getProperties())));
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setDriverClass method, of class DBURI.
     */
    @Test
    public void testSetDriverClass() throws Exception, URISyntaxException {
        System.out.println("setDriverClass");
        String driverClass = "oracle.jdbc.driver.OracleDriver";
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        instance.setDriverClass(driverClass);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getCharacterSet method, of class DBURI.
     */
    @Test
    public void testGetCharacterSet() throws Exception, URISyntaxException {
        System.out.println("getCharacterSet");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        String expResult = "utf8";
        String result = instance.getCharacterSet();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setCharacterSet method, of class DBURI.
     */
    @Test
    public void testSetCharacterSet() throws Exception, URISyntaxException {
        System.out.println("setCharacterSet");
        String characterSet = "utf8";
        DBURI instance = new DBURI(DBURITest.C_POSTGRES_1);
        instance.setCharacterSet(characterSet);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSourceCodeType method, of class DBURI.
     */
    @Test
    public void testGetSourceCodeType() throws Exception, URISyntaxException {
        System.out.println("getSourceCodeType");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        int expResult = 2005;// CLOB

        int result = instance.getSourceCodeType();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setSourceCodeType method, of class DBURI.
     */
    @Test
    public void testSetSourceCodeType() throws Exception, URISyntaxException {
        System.out.println("setSourceCodeType");
        int sourceCodeType = 5;
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_1);
        instance.setSourceCodeType(sourceCodeType);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSubprotocol method, of class DBURI.
     */
    @Test
    public void testGetSubprotocol() throws Exception, URISyntaxException {
        System.out.println("getSubprotocol");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        String expResult = "oracle";
        String result = instance.getSubprotocol();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getSubnamePrefix method, of class DBURI.
     */
    @Test
    public void testGetSubnamePrefix() throws Exception, URISyntaxException {
        System.out.println("getSubnamePrefix");
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        String expResult = "oci";
        String result = instance.getSubnamePrefix();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setSubnamePrefix method, of class DBURI.
     */
    @Test
    public void testSetSubnamePrefix() throws Exception, URISyntaxException {
        System.out.println("setSubnamePrefix");
        String subnamePrefix = "oci8";
        DBURI instance = new DBURI(DBURITest.C_ORACLE_OCI_2);
        instance.setSubnamePrefix(subnamePrefix);
        String result = instance.getSubnamePrefix();
        Assert.assertEquals(subnamePrefix, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getParameters method, of class DBURI.
     */
    @Test
    public void testGetParameters() throws Exception, URISyntaxException {
        System.out.println("getParameters");
        DBURI instance = new DBURI(DBURITest.C_TEST_URI);
        Map<String, String> expResult = new HashMap<>();
        expResult.put("param1", "x&1");
        expResult.put("param2", null);
        expResult.put("param3", null);
        Map<String, String> result = instance.getParameters();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of setParameters method, of class DBURI.
     */
    @Test
    public void testSetParameters() throws Exception, URISyntaxException {
        System.out.println("setParameters");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("param1", "x%FFF");
        parameters.put("param2", "IAmParameter2");
        parameters.put("param3", "IAmParameter3");
        DBURI instance = new DBURI(DBURITest.C_TEST_URI);
        instance.setParameters(parameters);
        // TODO review the generated test code and remove the default call to
        // fail.
        Assert.assertEquals(parameters, instance.getParameters());
    }

    /**
     * Verify that default languages are returned if non are provided in the
     * DBURI.
     */
    @Test
    public void testDefaultLanguagesList() throws Exception, URISyntaxException {
        System.out.println("testDefaultLanguagesList");
        List<String> defaultLanguagesList = Arrays.asList(DBURITest.C_DEFAULT_LANGUAGES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_DEFAULTS);
        List<String> result = instance.getLanguagesList();
        Assert.assertEquals(defaultLanguagesList, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that default CharacterSet are returned if non are provided in the
     * DBURI.
     */
    @Test
    public void testDefaultCharacterSet() throws Exception, URISyntaxException {
        System.out.println("testDefaultCharacterSet");
        DBURI instance = new DBURI(DBURITest.C_TEST_DEFAULTS);
        String result = instance.getCharacterSet();
        Assert.assertEquals(DBURITest.C_DEFAULT_CHARACTERSET, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that default languages are returned if non are provided in the
     * DBURI.
     */
    @Test
    public void testDefaultSchemasList() throws Exception, URISyntaxException {
        System.out.println("testDefaultSchemasList");
        List<String> defaultSchemasList = Arrays.asList(DBURITest.C_DEFAULT_SCHEMAS.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_DEFAULTS);
        List<String> result = instance.getSchemasList();
        Assert.assertEquals(defaultSchemasList, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that default Source Code Types are returned if non are provided in
     * the DBURI.
     */
    @Test
    public void testDefaultSourceCodeTypesList() throws Exception, URISyntaxException {
        System.out.println("testDefaultSourceCodeTypesList");
        List<String> defaultSourceCodeTypesList = Arrays.asList(DBURITest.C_DEFAULT_SOURCE_CODE_TYPES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_DEFAULTS);
        List<String> result = instance.getSourceCodeTypesList();
        Assert.assertEquals(defaultSourceCodeTypesList, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that default languages are returned if non are provided in the
     * DBURI.
     */
    @Test
    public void testDefaultSourceCodeNamesList() throws Exception, URISyntaxException {
        System.out.println("testDefaultSourceCodeNamesList");
        List<String> defaultSourceCodeNamesList = Arrays.asList(DBURITest.C_DEFAULT_SOURCE_CODE_NAMES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_DEFAULTS);
        List<String> result = instance.getSourceCodeNamesList();
        Assert.assertEquals(defaultSourceCodeNamesList, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that languages are returned if provided in the DBURI.
     */
    @Test
    public void testExplicitLanguagesList() throws Exception, URISyntaxException {
        System.out.println("testExplicitLanguagesList");
        List<String> defaultLanguagesList = Arrays.asList(DBURITest.C_EXPLICIT_LANGUAGES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_EXPLICIT);
        List<String> result = instance.getLanguagesList();
        Assert.assertEquals(defaultLanguagesList, result);
        // TODO review the generated test code and remove the call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that CharacterSet are returned if provided in the DBURI.
     */
    @Test
    public void testExplicitCharacterSet() throws Exception, URISyntaxException {
        System.out.println("testExplicitCharacterSet");
        DBURI instance = new DBURI(DBURITest.C_TEST_EXPLICIT);
        String result = instance.getCharacterSet();
        Assert.assertEquals(DBURITest.C_EXPLICIT_CHARACTERSET, result);
        // TODO review the generated test code and remove the call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that languages are returned if provided in the DBURI.
     */
    @Test
    public void testExplicitSchemasList() throws Exception, URISyntaxException {
        System.out.println("testExplicitSchemasList");
        List<String> defaultSchemasList = Arrays.asList(DBURITest.C_EXPLICIT_SCHEMAS.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_EXPLICIT);
        List<String> result = instance.getSchemasList();
        Assert.assertEquals(defaultSchemasList, result);
        // TODO review the generated test code and remove the call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that Source Code Types are returned if provided in the DBURI.
     */
    @Test
    public void testExplicitSourceCodeTypesList() throws Exception, URISyntaxException {
        System.out.println("testExplicitSourceCodeTypesList");
        List<String> defaultSourceCodeTypesList = Arrays.asList(DBURITest.C_EXPLICIT_SOURCE_CODE_TYPES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_EXPLICIT);
        List<String> result = instance.getSourceCodeTypesList();
        Assert.assertEquals(defaultSourceCodeTypesList, result);
        // TODO review the generated test code and remove the call to fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Verify that languages are returned if provided in the DBURI.
     */
    @Test
    public void testExplicitSourceCodeNamesList() throws Exception, URISyntaxException {
        System.out.println("testExplicitSourceCodeNamesList");
        List<String> defaultSourceCodeNamesList = Arrays.asList(DBURITest.C_EXPLICIT_SOURCE_CODE_NAMES.split(","));
        DBURI instance = new DBURI(DBURITest.C_TEST_EXPLICIT);
        List<String> result = instance.getSourceCodeNamesList();
        Assert.assertEquals(defaultSourceCodeNamesList, result);
        // TODO review the generated test code and remove the call to fail.
        // fail("The test case is a prototype.");
    }
}

