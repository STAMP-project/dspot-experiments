/**
 * import dalvik.system.DexFile;
 */
package tests.api.java.lang.reflect;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.GenericSignatureFormatError;
import junit.framework.TestCase;


// import tests.support.Support_ClassLoader;
public class GenericSignatureFormatErrorTest extends TestCase {
    public void test_Constructor() {
        TestCase.assertNotNull(new GenericSignatureFormatError());
    }

    public void test_readResource() throws Exception {
        File tf = File.createTempFile("classes", ".dex");
        // System.out.println("GenericSignatureFormatErrorTest:"
        // +tf.getAbsolutePath()+", canRead: "+tf.canRead()
        // +", canWrite: "+tf.canWrite());
        InputStream is = this.getClass().getResourceAsStream("dex1.bytes");
        TestCase.assertNotNull(is);
    }
}

