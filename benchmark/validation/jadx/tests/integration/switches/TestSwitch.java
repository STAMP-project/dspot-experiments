package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitch extends IntegrationTest {
    public static class TestCls {
        public String escape(String str) {
            int len = str.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '.' :
                    case '/' :
                        sb.append('_');
                        break;
                    case ']' :
                        sb.append('A');
                        break;
                    case '?' :
                        break;
                    default :
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitch.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("case '/':"));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(5)) + "break;")));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(4)) + "default:")));
        Assert.assertEquals(1, TestUtils.count(code, "i++"));
        Assert.assertEquals(4, TestUtils.count(code, "break;"));
    }
}

