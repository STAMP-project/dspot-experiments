package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBreakInComplexIf2 extends IntegrationTest {
    public static class TestCls {
        private int test(List<String> list) {
            int length = 0;
            for (String str : list) {
                if ((str.isEmpty()) || ((str.length()) > 4)) {
                    break;
                }
                if (str.equals("skip")) {
                    continue;
                }
                if (str.equals("a")) {
                    break;
                }
                length++;
            }
            return length;
        }

        public void check() {
            Assert.assertThat(test(Arrays.asList("x", "y", "skip", "z", "a")), Matchers.is(3));
            Assert.assertThat(test(Arrays.asList("x", "skip", "")), Matchers.is(1));
            Assert.assertThat(test(Arrays.asList("skip", "y", "12345")), Matchers.is(1));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestBreakInComplexIf2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "break;"));
    }

    @Test
    public void testNoDebug() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestBreakInComplexIf2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(2, "break;"));
    }
}

