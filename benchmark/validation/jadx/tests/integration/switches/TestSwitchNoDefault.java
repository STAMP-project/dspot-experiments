package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchNoDefault extends IntegrationTest {
    public static class TestCls {
        public void test(int a) {
            String s = null;
            switch (a) {
                case 1 :
                    s = "1";
                    break;
                case 2 :
                    s = "2";
                    break;
                case 3 :
                    s = "3";
                    break;
                case 4 :
                    s = "4";
                    break;
            }
            System.out.println(s);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchNoDefault.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertEquals(4, TestUtils.count(code, "break;"));
        Assert.assertEquals(1, TestUtils.count(code, "System.out.println(s);"));
    }
}

