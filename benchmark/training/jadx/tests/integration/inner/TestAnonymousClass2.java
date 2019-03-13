package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass2 extends IntegrationTest {
    public static class TestCls {
        /* public Runnable test3() {
        final int i = f + 2;
        return new Runnable() {
        @Override
        public void run() {
        f = i;
        }
        };
        }
         */
        public static class Inner {
            private int f;

            public Runnable test() {
                return new Runnable() {
                    @Override
                    public void run() {
                        f = 1;
                    }
                };
            }

            public Runnable test2() {
                return new Runnable() {
                    @Override
                    public void run() {
                        Object obj = TestAnonymousClass2.TestCls.Inner.this;
                    }
                };
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("AnonymousClass_")));
        Assert.assertThat(code, CoreMatchers.containsString("f = 1;"));
        // assertThat(code, containsString("f = i;"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("Inner obj = ;")));
        Assert.assertThat(code, CoreMatchers.containsString("Inner.this;"));
    }
}

