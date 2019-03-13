package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTernary extends IntegrationTest {
    public static class TestCls {
        public boolean test1(int a) {
            return a != 2;
        }

        public void test2(int a) {
            Assert.assertTrue((a == 3));
        }

        public int test3(int a) {
            return a > 0 ? 1 : (a + 2) * 3;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTernary.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("else")));
        Assert.assertThat(code, CoreMatchers.containsString("return a != 2;"));
        Assert.assertThat(code, CoreMatchers.containsString("assertTrue(a == 3)"));
        Assert.assertThat(code, CoreMatchers.containsString("return a > 0 ? 1 : (a + 2) * 3;"));
    }
}

