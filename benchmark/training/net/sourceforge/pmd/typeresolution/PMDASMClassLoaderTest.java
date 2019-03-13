/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;


import java.util.Map;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;
import org.junit.Assert;
import org.junit.Test;


public class PMDASMClassLoaderTest {
    private PMDASMClassLoader cl;

    @Test
    public void testLoadClassWithImportOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand";
        Class<?> clazz = cl.loadClass(className);
        Assert.assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        Assert.assertNotNull(imports);
        Assert.assertEquals("java.util.List", imports.get("List"));
        Assert.assertEquals("java.util.ArrayList", imports.get("ArrayList"));
        Assert.assertEquals("java.lang.Object", imports.get("Object"));
        Assert.assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand", imports.get("ClassWithImportOnDemand"));
    }

    @Test
    public void testClassWithImportInnerOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand";
        Class<?> clazz = cl.loadClass(className);
        Assert.assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        Assert.assertNotNull(imports);
        Assert.assertEquals("java.util.Iterator", imports.get("Iterator"));
        Assert.assertEquals("java.util.Map", imports.get("Map"));
        Assert.assertEquals("java.util.Set", imports.get("Set"));
        Assert.assertEquals("java.util.Map$Entry", imports.get("Entry"));
        Assert.assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        Assert.assertEquals("java.lang.Object", imports.get("Object"));
        Assert.assertEquals("java.util.StringTokenizer", imports.get("StringTokenizer"));
        Assert.assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand", imports.get("ClassWithImportInnerOnDemand"));
    }

    /**
     * Unit test for bug 3546093.
     *
     * @throws Exception
     * 		any error
     */
    @Test
    public void testCachingOfNotFoundClasses() throws Exception {
        PMDASMClassLoaderTest.MockedClassLoader mockedClassloader = new PMDASMClassLoaderTest.MockedClassLoader();
        PMDASMClassLoader cl = PMDASMClassLoader.getInstance(mockedClassloader);
        String notExistingClassname = "that.clazz.doesnot.Exist";
        try {
            cl.loadClass(notExistingClassname);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // expected
        }
        try {
            cl.loadClass(notExistingClassname);
            Assert.fail();
        } catch (ClassNotFoundException e) {
            // expected
        }
        Assert.assertEquals(1, mockedClassloader.findClassCalls);
    }

    private static class MockedClassLoader extends ClassLoader {
        int findClassCalls = 0;

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            (findClassCalls)++;
            return super.findClass(name);
        }
    }
}

