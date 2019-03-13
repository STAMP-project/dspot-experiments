package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.SmaliTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopCondition5 extends SmaliTest {
    public static class TestCls {
        private static int lastIndexOf(int[] array, int target, int start, int end) {
            for (int i = end - 1; i >= start; i--) {
                if ((array[i]) == target) {
                    return i;
                }
            }
            return -1;
        }
    }

    @Test
    public void test0() {
        ClassNode cls = getClassNode(TestLoopCondition5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for ("));
        Assert.assertThat(code, JadxMatchers.containsOne("return -1;"));
        Assert.assertThat(code, JadxMatchers.countString(2, "return "));
    }

    @Test
    public void test1() {
        ClassNode cls = getClassNodeFromSmaliWithPath("loops", "TestLoopCondition5");
        String code = cls.getCode().toString();
        Assert.assertThat(code, Matchers.anyOf(JadxMatchers.containsOne("for ("), JadxMatchers.containsOne("while (true) {")));
        Assert.assertThat(code, JadxMatchers.containsOne("return -1;"));
        Assert.assertThat(code, JadxMatchers.countString(2, "return "));
    }
}

