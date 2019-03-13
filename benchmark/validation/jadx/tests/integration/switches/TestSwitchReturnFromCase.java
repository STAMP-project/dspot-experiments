package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchReturnFromCase extends IntegrationTest {
    public static class TestCls {
        public void test(int a) {
            String s = null;
            if (a > 1000) {
                return;
            }
            switch (a % 4) {
                case 1 :
                    s = "1";
                    break;
                case 2 :
                    s = "2";
                    break;
                case 3 :
                case 4 :
                    s = "4";
                    break;
                case 5 :
                    return;
            }
            s = "5";
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchReturnFromCase.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("switch (a % 4) {"));
        Assert.assertEquals(5, TestUtils.count(code, "case "));
        Assert.assertEquals(3, TestUtils.count(code, "break;"));
        Assert.assertThat(code, JadxMatchers.containsOne("s = \"1\";"));
        Assert.assertThat(code, JadxMatchers.containsOne("s = \"2\";"));
        Assert.assertThat(code, JadxMatchers.containsOne("s = \"4\";"));
        Assert.assertThat(code, JadxMatchers.containsOne("s = \"5\";"));
    }
}

