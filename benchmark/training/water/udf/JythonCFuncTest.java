package water.udf;


import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import water.TestUtil;


public class JythonCFuncTest extends TestUtil {
    @Test
    public void testPyFunc2InvocationFromResources() throws Exception {
        String[] functionResources = ar("py/test_cfunc2.py", "py/__init__.py");
        CFuncRef cFuncRef = JFuncUtils.loadTestFunc("python", "test1.py", functionResources, "py.test_cfunc2.TestCFunc2");
        testPyFunc2Invocation(cFuncRef, functionResources);
    }

    @Test
    public void testPyFunc2InvocationFromString() throws Exception {
        // Load test python code
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream is = cl.getResourceAsStream("py/test_cfunc2.py")) {
            byte[] ba = IOUtils.toByteArray(is);
            CFuncRef cFuncRef = JFuncUtils.loadRawTestFunc("python", "test2.py", "test.TestCFunc2", ba, "test.py");
            testPyFunc2Invocation(cFuncRef, new String[0]);
        }
    }
}

