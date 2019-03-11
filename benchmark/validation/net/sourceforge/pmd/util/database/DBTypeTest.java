/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.database;


import java.io.File;
import java.util.Properties;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sturton
 */
public class DBTypeTest {
    private File absoluteFile;

    private Properties testProperties;

    private Properties includeProperties;

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    public void testGetPropertiesFromFile() throws Exception {
        System.out.println("getPropertiesFromFile");
        DBType instance = new DBType(absoluteFile.getAbsolutePath());
        Properties expResult = testProperties;
        Properties result = instance.getProperties();
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    public void testGetProperties() throws Exception {
        System.out.println("testGetProperties");
        DBType instance = new DBType("test");
        Properties expResult = testProperties;
        System.out.println(("testGetProperties: expected results " + (testProperties)));
        Properties result = instance.getProperties();
        System.out.println(("testGetProperties: actual results " + result));
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getProperties method, of class DBType.
     */
    @Test
    public void testGetIncludeProperties() throws Exception {
        System.out.println("testGetIncludeProperties");
        DBType instance = new DBType("include");
        Properties expResult = includeProperties;
        System.out.println(("testGetIncludeProperties: expected results " + (includeProperties)));
        Properties result = instance.getProperties();
        System.out.println(("testGetIncludeProperties: actual results " + result));
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }

    /**
     * Test of getResourceBundleAsProperties method, of class DBType.
     */
    @Test
    public void testAsProperties() {
        System.out.println("asProperties");
        ResourceBundle bundle = ResourceBundle.getBundle(((DBType.class.getPackage().getName()) + ".test"));
        Properties expResult = testProperties;
        Properties result = DBType.getResourceBundleAsProperties(bundle);
        Assert.assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to
        // fail.
        // fail("The test case is a prototype.");
    }
}

