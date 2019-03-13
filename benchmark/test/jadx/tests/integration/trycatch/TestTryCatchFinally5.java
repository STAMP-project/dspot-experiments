package jadx.tests.integration.trycatch;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class TestTryCatchFinally5 extends IntegrationTest {
    public static class TestCls {
        private <E> List<E> test(TestTryCatchFinally5.TestCls.A a, TestTryCatchFinally5.TestCls.B<E> b) {
            TestTryCatchFinally5.TestCls.C c = p(a);
            if (c == null) {
                return null;
            }
            TestTryCatchFinally5.TestCls.D d = b.f(c);
            try {
                if (!(d.first())) {
                    return null;
                }
                List<E> list = new ArrayList<>();
                do {
                    list.add(b.load(d));
                } while (d.toNext() );
                return list;
            } finally {
                d.close();
            }
        }

        private TestTryCatchFinally5.TestCls.C p(TestTryCatchFinally5.TestCls.A a) {
            return ((TestTryCatchFinally5.TestCls.C) (a));
        }

        private interface A {}

        private interface B<T> {
            TestTryCatchFinally5.TestCls.D f(TestTryCatchFinally5.TestCls.C c);

            T load(TestTryCatchFinally5.TestCls.D d);
        }

        private interface C {}

        private interface D {
            boolean first();

            boolean toNext();

            void close();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestTryCatchFinally5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("} finally {"));
        // TODO: remove duplicates on multiple paths
        // assertThat(code, containsOne("d.close();"));
    }
}

