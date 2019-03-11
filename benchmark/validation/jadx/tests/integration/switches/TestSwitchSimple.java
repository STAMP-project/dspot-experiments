package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchSimple extends IntegrationTest {
    public static class TestCls {
        public void test(int a) {
            String s = null;
            switch (a % 4) {
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
                default :
                    System.out.println("Not Reach");
                    break;
            }
            System.out.println(s);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchSimple.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertEquals(5, TestUtils.count(code, "break;"));
        Assert.assertEquals(1, TestUtils.count(code, "System.out.println(s);"));
        Assert.assertEquals(1, TestUtils.count(code, "System.out.println(\"Not Reach\");"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("switch ((a % 4)) {")));
        Assert.assertThat(code, CoreMatchers.containsString("switch (a % 4) {"));
    }
}

