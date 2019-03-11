package water.udf;


import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.TestUtil;


/**
 * Test DkvClassLoader.
 */
public class DkvClassLoaderTest extends TestUtil {
    @Test
    public void testClassLoadFromKey() throws Exception {
        String testJar = "water/udf/cfunc_test.jar";
        Key k = JFuncUtils.loadTestJar("testKeyName.jar", testJar);
        ClassLoader cl = new DkvClassLoader(k, Thread.currentThread().getContextClassLoader());
        int classCnt = 0;
        int resourceCnt = 0;
        try (JarInputStream jis = new JarInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream(testJar))) {
            JarEntry entry = null;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.isDirectory())
                    continue;

                String entryName = entry.getName();
                if (entryName.endsWith(".class")) {
                    String klazzName = entryName.replace('/', '.').substring(0, ((entryName.length()) - (".class".length())));
                    assertLoadableClass(cl, klazzName);
                    classCnt++;
                }
                byte[] exptectedContent = DkvClassLoader.readJarEntry(jis, entry);
                assertLoadableResource(cl, entryName, exptectedContent);
                resourceCnt++;
            } 
            // Just make sure, that we tested at least one class file and one resource
            Assert.assertTrue((("The file " + testJar) + " needs to contain at least one classfile"), (classCnt > 0));
            Assert.assertTrue((("The file " + testJar) + " needs to contain at least one resource"), (resourceCnt > 0));
        } finally {
            DKV.remove(k);
        }
    }
}

