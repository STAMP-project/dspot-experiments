package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass12 extends IntegrationTest {
    public static class TestCls {
        public abstract static class BasicAbstract {
            public abstract void doSomething();
        }

        private TestAnonymousClass12.TestCls.BasicAbstract outer;

        private TestAnonymousClass12.TestCls.BasicAbstract inner;

        public void test() {
            outer = new TestAnonymousClass12.TestCls.BasicAbstract() {
                @Override
                public void doSomething() {
                    inner = new TestAnonymousClass12.TestCls.BasicAbstract() {
                        @Override
                        public void doSomething() {
                            inner = null;
                        }
                    };
                }
            };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass12.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("outer = new BasicAbstract() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("inner = new BasicAbstract() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("inner = null;"));
    }
}

