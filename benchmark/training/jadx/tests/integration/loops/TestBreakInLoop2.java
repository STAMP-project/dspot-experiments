package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestBreakInLoop2 extends IntegrationTest {
    public static class TestCls {
        public void test(List<Integer> data) throws Exception {
            for (; ;) {
                try {
                    funcB(data);
                    break;
                } catch (Exception ex) {
                    if (funcC()) {
                        throw ex;
                    }
                    data.clear();
                }
                Thread.sleep(100);
            }
        }

        private boolean funcB(List<Integer> data) {
            return false;
        }

        private boolean funcC() {
            return true;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestBreakInLoop2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("while (true) {"));
        Assert.assertThat(code, Matchers.anyOf(JadxMatchers.containsOne("break;"), JadxMatchers.containsOne("return;")));
        Assert.assertThat(code, JadxMatchers.containsOne("throw ex;"));
        Assert.assertThat(code, JadxMatchers.containsOne("data.clear();"));
        Assert.assertThat(code, JadxMatchers.containsOne("Thread.sleep(100);"));
    }
}

