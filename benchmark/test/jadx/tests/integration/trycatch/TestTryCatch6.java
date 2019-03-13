package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatch6 extends IntegrationTest {
    public static class TestCls {
        private static boolean test(Object obj) {
            boolean res = false;
            while (true) {
                try {
                    res = TestTryCatch6.TestCls.exc(obj);
                    return res;
                } catch (IOException e) {
                    res = true;
                } catch (Throwable e) {
                    if (obj == null) {
                        obj = new Object();
                    }
                }
            } 
        }

        private static boolean exc(Object obj) throws IOException {
            if (obj == null) {
                throw new IOException();
            }
            return true;
        }

        public void check() {
            Assert.assertTrue(TestTryCatch6.TestCls.test(new Object()));
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestTryCatch6.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("try {"));
    }
}

