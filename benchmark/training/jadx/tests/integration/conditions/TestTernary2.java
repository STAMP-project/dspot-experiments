package jadx.tests.integration.conditions;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestTernary2 extends IntegrationTest {
    public static class TestCls {
        public void test() {
            Assert.assertTrue(((f(1, 0)) == 0));
        }

        private int f(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTernary2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertEquals(1, TestUtils.count(code, "assertTrue"));
        Assert.assertEquals(1, TestUtils.count(code, "f(1, 0)"));
        // TODO:
        // assertThat(code, containsString("assertTrue(f(1, 0) == 0);"));
    }
}

