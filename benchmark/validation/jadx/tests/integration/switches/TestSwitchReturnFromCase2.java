package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchReturnFromCase2 extends IntegrationTest {
    public static class TestCls {
        public boolean test(int a) {
            switch (a % 4) {
                case 2 :
                case 3 :
                    if (a == 2) {
                        return true;
                    }
                    return true;
            }
            return false;
        }

        public void check() {
            Assert.assertTrue(test(2));
            Assert.assertTrue(test(3));
            Assert.assertTrue(test(15));
            Assert.assertFalse(test(1));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchReturnFromCase2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("switch (a % 4) {"));
    }
}

