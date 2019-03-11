package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopConditionInvoke extends IntegrationTest {
    public static class TestCls {
        private static final char STOP_CHAR = 0;

        private int pos;

        private boolean test(char lastChar) {
            int startPos = pos;
            char ch;
            while ((ch = next()) != (TestLoopConditionInvoke.TestCls.STOP_CHAR)) {
                if (ch == lastChar) {
                    return true;
                }
            } 
            pos = startPos;
            return false;
        }

        private char next() {
            return 0;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopConditionInvoke.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("do {"));
        Assert.assertThat(code, JadxMatchers.containsOne("if (ch == 0) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.pos = startPos;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return false;"));
        Assert.assertThat(code, JadxMatchers.containsOne("} while (ch != lastChar);"));
        Assert.assertThat(code, JadxMatchers.containsOne("return true;"));
    }
}

