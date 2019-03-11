package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestLoopCondition2 extends IntegrationTest {
    public static class TestCls {
        public int test(boolean a) {
            int i = 0;
            while (a && (i < 10)) {
                i++;
            } 
            return i;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestLoopCondition2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("int i = 0;"));
        Assert.assertThat(code, JadxMatchers.containsOne("while (a && i < 10) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("i++;"));
        Assert.assertThat(code, JadxMatchers.containsOne("return i;"));
    }
}

