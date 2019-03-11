package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import jadx.tests.api.utils.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass4 extends IntegrationTest {
    public static class TestCls {
        public static class Inner {
            private int f;

            private double d;

            public void test() {
                new Thread() {
                    {
                        f = 1;
                    }

                    @Override
                    public void run() {
                        d = 7.5;
                    }
                }.start();
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass4.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne(((TestUtils.indent(3)) + "new Thread() {")));
        Assert.assertThat(code, JadxMatchers.containsOne(((TestUtils.indent(4)) + "{")));
        Assert.assertThat(code, JadxMatchers.containsOne("f = 1;"));
        Assert.assertThat(code, JadxMatchers.countString(2, ((TestUtils.indent(4)) + "}")));
        Assert.assertThat(code, JadxMatchers.containsOne(((TestUtils.indent(4)) + "public void run() {")));
        Assert.assertThat(code, JadxMatchers.containsOne("d = 7.5"));
        Assert.assertThat(code, JadxMatchers.containsOne(((TestUtils.indent(3)) + "}.start();")));
    }
}

