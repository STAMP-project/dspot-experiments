package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchContinue extends IntegrationTest {
    public static class TestCls {
        public String test(int a) {
            String s = "";
            while (a > 0) {
                switch (a % 4) {
                    case 1 :
                        s += "1";
                        break;
                    case 3 :
                    case 4 :
                        s += "4";
                        break;
                    case 5 :
                        a -= 2;
                        continue;
                }
                s += "-";
                a--;
            } 
            return s;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchContinue.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("switch (a % 4) {"));
        Assert.assertEquals(4, TestUtils.count(code, "case "));
        Assert.assertEquals(2, TestUtils.count(code, "break;"));
        Assert.assertThat(code, JadxMatchers.containsOne("a -= 2;"));
        Assert.assertThat(code, JadxMatchers.containsOne("continue;"));
    }
}

