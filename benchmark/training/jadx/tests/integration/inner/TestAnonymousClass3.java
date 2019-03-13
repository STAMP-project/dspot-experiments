package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.TestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass3 extends IntegrationTest {
    public static class TestCls {
        public static class Inner {
            private int f;

            private double d;

            public void test() {
                new Thread() {
                    @Override
                    public void run() {
                        int a = (f)--;
                        p(a);
                        f += 2;
                        f *= 2;
                        a = ++(f);
                        p(a);
                        d /= 3;
                    }

                    public void p(int a) {
                    }
                }.start();
            }
        }
    }

    @Test
    public void test() {
        disableCompilation();
        ClassNode cls = getClassNode(TestAnonymousClass3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(4)) + "public void run() {")));
        Assert.assertThat(code, CoreMatchers.containsString(((TestUtils.indent(3)) + "}.start();")));
        // assertThat(code, not(containsString("synthetic")));
        // assertThat(code, not(containsString("AnonymousClass_")));
        // assertThat(code, containsString("a = f--;"));
    }
}

