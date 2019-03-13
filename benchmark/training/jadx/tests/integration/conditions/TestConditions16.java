package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestConditions16 extends IntegrationTest {
    public static class TestCls {
        private static boolean test(int a, int b) {
            return ((a < 0) || (((b % 2) != 0) && (a > 28))) || (b < 0);
        }

        public void check() {
            Assert.assertTrue(TestConditions16.TestCls.test((-1), 1));
            Assert.assertTrue(TestConditions16.TestCls.test(1, (-1)));
            Assert.assertTrue(TestConditions16.TestCls.test(29, 3));
            Assert.assertFalse(TestConditions16.TestCls.test(2, 2));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestConditions16.TestCls.class);
        String code = cls.getCode().toString();
        // assertThat(code, containsOne("return a < 0 || (b % 2 != 0 && a > 28) || b < 0;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return a < 0 || ((b % 2 != 0 && a > 28) || b < 0);"));
    }
}

