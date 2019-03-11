package jadx.tests.integration.generics;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;


public class TestGenerics6 extends IntegrationTest {
    public static class TestCls {
        public void test1(Collection<? extends TestGenerics6.TestCls.A> as) {
            for (TestGenerics6.TestCls.A a : as) {
                a.f();
            }
        }

        public void test2(Collection<? extends TestGenerics6.TestCls.A> is) {
            for (TestGenerics6.TestCls.I i : is) {
                i.f();
            }
        }

        private interface I {
            void f();
        }

        private class A implements TestGenerics6.TestCls.I {
            public void f() {
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestGenerics6.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("for (A a : as) {"));
        // TODO: fix iterable arg type (unexpected cast to A in bytecode)
        // assertThat(code, containsOne("for (I i : is) {"));
    }
}

