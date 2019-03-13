package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopCondition extends IntegrationTest {
    public static class TestCls {
        public String f;

        private void setEnabled(boolean r1z) {
        }

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

        private void testMoreComplexIfInLoop(ArrayList<String> list) throws Exception {
            for (int i = 0; (i != 16) && (i < 255); i++) {
                list.set(i, "ABC");
                if (i == 128) {
                    return;
                }
                list.set(i, "DEF");
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopCondition.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("i < this.f.length()"));
        Assert.assertThat(code, JadxMatchers.containsOne("list.set(i, \"ABC\")"));
        Assert.assertThat(code, JadxMatchers.containsOne("list.set(i, \"DEF\")"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (j == 2) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("setEnabled(true);"));
        Assert.assertThat(code, JadxMatchers.containsOne("setEnabled(false);"));
    }
}

