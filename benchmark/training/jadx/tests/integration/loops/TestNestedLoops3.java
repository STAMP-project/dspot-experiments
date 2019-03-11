package jadx.tests.integration.loops;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestNestedLoops3 extends IntegrationTest {
    public static class TestCls {
        int c = 0;

        public int test(int b) {
            int i;
            loop0 : while (true) {
                f1();
                i = 0;
                while (true) {
                    f2();
                    if (i != 0) {
                        break loop0;
                    }
                    i += 3;
                    if (b >= 16) {
                        break loop0;
                    }
                    try {
                        exc();
                        break;
                    } catch (Exception e) {
                        // 
                    }
                } 
            } 
            return i;
        }

        private void exc() throws Exception {
            if ((c) > 200) {
                throw new Exception();
            }
        }

        private void f1() {
            c += 1;
        }

        private void f2() {
            c += 100;
        }

        public void check() {
            test(1);
            Assert.assertEquals(302, c);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestNestedLoops3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("} catch (Exception e) {"));
    }
}

