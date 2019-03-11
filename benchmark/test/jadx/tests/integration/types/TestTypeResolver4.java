package jadx.tests.integration.types;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTypeResolver4 extends IntegrationTest {
    public static class TestCls {
        private static String test(byte[] strArray, int offset) {
            int len = strArray.length;
            int start = offset + (TestTypeResolver4.TestCls.f(strArray, offset));
            int end = start;
            while (((end + 1) < len) && (((strArray[end]) != 0) || ((strArray[(end + 1)]) != 0))) {
                end += 2;
            } 
            byte[] arr = Arrays.copyOfRange(strArray, start, end);
            return new String(arr);
        }

        private static int f(byte[] strArray, int offset) {
            return 0;
        }

        public void check() {
            String test = TestTypeResolver4.TestCls.test(("1234" + ("utfstr\u0000\u0000" + "4567")).getBytes(), 4);
            Assert.assertThat(test, Matchers.is("utfstr"));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTypeResolver4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("(strArray[end] != (byte) 0 || strArray[end + 1] != (byte) 0)"));
    }

    @Test
    public void test2() {
        noDebugInfo();
        getClassNode(TestTypeResolver4.TestCls.class);
    }
}

