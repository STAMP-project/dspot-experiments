package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions5 extends IntegrationTest {
    public static class TestCls {
        public static void assertEquals(Object a1, Object a2) {
            if (a1 == null) {
                if (a2 != null) {
                    throw new AssertionError(((a1 + " != ") + a2));
                }
            } else
                if (!(a1.equals(a2))) {
                    throw new AssertionError(((a1 + " != ") + a2));
                }

        }

        public static void assertEquals2(Object a1, Object a2) {
            if (a1 != null) {
                if (!(a1.equals(a2))) {
                    throw new AssertionError(((a1 + " != ") + a2));
                }
            } else {
                if (a2 != null) {
                    throw new AssertionError(((a1 + " != ") + a2));
                }
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("if (a1 == null) {"));
        Assert.assertThat(code, CoreMatchers.containsString("if (a2 != null) {"));
        Assert.assertThat(code, CoreMatchers.containsString("throw new AssertionError(a1 + \" != \" + a2);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("if (a1.equals(a2)) {")));
        Assert.assertThat(code, CoreMatchers.containsString("} else if (!a1.equals(a2)) {"));
    }
}

