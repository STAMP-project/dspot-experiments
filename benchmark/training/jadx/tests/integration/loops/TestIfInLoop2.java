package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestIfInLoop2 extends IntegrationTest {
    public static class TestCls {
        public static void test(String str) {
            int len = str.length();
            int at = 0;
            while (at < len) {
                char c = str.charAt(at);
                int endAt = at + 1;
                if (c == 'A') {
                    while (endAt < len) {
                        c = str.charAt(endAt);
                        if (c == 'B') {
                            break;
                        }
                        endAt++;
                    } 
                }
                at = endAt;
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestIfInLoop2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("for (int at = 0; at < len; at = endAt) {")));
    }
}

