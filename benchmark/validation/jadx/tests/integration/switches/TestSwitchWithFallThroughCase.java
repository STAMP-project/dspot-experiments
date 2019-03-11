package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitchWithFallThroughCase extends IntegrationTest {
    @SuppressWarnings("fallthrough")
    public static class TestCls {
        public String test(int a, boolean b, boolean c) {
            String str = "";
            switch (a % 4) {
                case 1 :
                    str += ">";
                    if ((a == 5) && b) {
                        if (c) {
                            str += "1";
                        } else {
                            str += "!c";
                        }
                        break;
                    }
                case 2 :
                    if (b) {
                        str += "2";
                    }
                    break;
                case 3 :
                    break;
                default :
                    str += "default";
                    break;
            }
            str += ";";
            return str;
        }

        public void check() {
            Assert.assertEquals(">1;", test(5, true, true));
            Assert.assertEquals(">2;", test(1, true, true));
            Assert.assertEquals(";", test(3, true, true));
            Assert.assertEquals("default;", test(0, true, true));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitchWithFallThroughCase.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("switch (a % 4) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (a == 5 && b) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (b) {"));
    }
}

