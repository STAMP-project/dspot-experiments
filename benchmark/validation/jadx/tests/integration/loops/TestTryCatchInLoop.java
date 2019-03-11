package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchInLoop extends IntegrationTest {
    public static class TestCls {
        int c = 0;

        public int test() {
            while (true) {
                try {
                    exc();
                    break;
                } catch (Exception e) {
                    // 
                }
            } 
            if ((c) == 5) {
                System.out.println(c);
            }
            return 0;
        }

        private void exc() throws Exception {
            (c)++;
            if ((c) < 3) {
                throw new Exception();
            }
        }

        public void check() {
            test();
            Assert.assertEquals(3, c);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchInLoop.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("break;"));
    }
}

