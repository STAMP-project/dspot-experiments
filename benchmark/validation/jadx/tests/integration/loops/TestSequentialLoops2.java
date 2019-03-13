package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSequentialLoops2 extends IntegrationTest {
    public static class TestCls {
        private static char[] lowercases = new char[]{ 'a' };

        public static String asciiToLowerCase(String s) {
            char[] c = null;
            int i = s.length();
            while ((i--) > 0) {
                char c1 = s.charAt(i);
                if (c1 <= 127) {
                    char c2 = TestSequentialLoops2.TestCls.lowercases[c1];
                    if (c1 != c2) {
                        c = s.toCharArray();
                        c[i] = c2;
                        break;
                    }
                }
            } 
            while ((i--) > 0) {
                if ((c[i]) <= 127) {
                    c[i] = TestSequentialLoops2.TestCls.lowercases[c[i]];
                }
            } 
            return c == null ? s : new String(c);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSequentialLoops2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "while ("));
        Assert.assertThat(code, CoreMatchers.containsString("break;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return c"));
        Assert.assertThat(code, JadxMatchers.countString(2, "<= 127"));
    }
}

