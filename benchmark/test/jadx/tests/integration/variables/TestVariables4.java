package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVariables4 extends IntegrationTest {
    public static class TestCls {
        private static boolean runTest(String clsName) {
            try {
                boolean pass = false;
                String msg = null;
                Throwable exc = null;
                Class<?> cls = Class.forName(clsName);
                if ((cls.getSuperclass()) == (TestVariables4.TestCls.AbstractTest.class)) {
                    Method mth = cls.getMethod("testRun");
                    try {
                        TestVariables4.TestCls.AbstractTest test = ((TestVariables4.TestCls.AbstractTest) (cls.getConstructor().newInstance()));
                        pass = ((Boolean) (mth.invoke(test)));
                    } catch (InvocationTargetException e) {
                        pass = false;
                        exc = e.getCause();
                    } catch (Throwable e) {
                        pass = false;
                        exc = e;
                    }
                } else {
                    msg = "not extends AbstractTest";
                }
                System.err.println(((((">> " + (pass ? "PASS" : "FAIL")) + "\t") + clsName) + (msg == null ? "" : "\t - " + msg)));
                if (exc != null) {
                    exc.printStackTrace();
                }
                return pass;
            } catch (ClassNotFoundException e) {
                System.err.println((("Class '" + clsName) + "' not found"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private static class AbstractTest {}
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestVariables4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("} catch (InvocationTargetException e) {"));
        Assert.assertThat(code, CoreMatchers.containsString("pass = false;"));
        Assert.assertThat(code, CoreMatchers.containsString("exc = e.getCause();"));
        Assert.assertThat(code, CoreMatchers.containsString("System.err.println(\"Class \'\" + clsName + \"\' not found\");"));
        Assert.assertThat(code, CoreMatchers.containsString("return pass;"));
    }

    @Test
    public void test2() {
        noDebugInfo();
        getClassNode(TestVariables4.TestCls.class);
    }
}

