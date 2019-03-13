package jadx.tests.integration.variables;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestVariables5 extends IntegrationTest {
    public static class TestCls {
        public String f = "str//ing";

        private boolean enabled;

        private void testIfInLoop() {
            int j = 0;
            for (int i = 0; i < (f.length()); i++) {
                char ch = f.charAt(i);
                if (ch == '/') {
                    j++;
                    if (j == 2) {
                        setEnabled(true);
                        return;
                    }
                }
            }
            setEnabled(false);
        }

        private void setEnabled(boolean b) {
            this.enabled = b;
        }

        public void check() {
            setEnabled(false);
            testIfInLoop();
            Assert.assertTrue(enabled);
        }
    }

    @Test
    public void test() {
        noDebugInfo();
        ClassNode cls = getClassNode(TestVariables5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("int i2++;")));
        Assert.assertThat(code, JadxMatchers.containsOne("int i = 0;"));
        Assert.assertThat(code, JadxMatchers.containsOne("i++;"));
    }
}

