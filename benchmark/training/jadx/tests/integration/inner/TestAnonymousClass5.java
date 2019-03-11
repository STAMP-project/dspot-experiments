package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass5 extends IntegrationTest {
    public static class TestCls {
        private final Map<String, TestAnonymousClass5.TestCls> map = new HashMap<>();

        private int a;

        public Iterable<TestAnonymousClass5.TestCls> test(String name) {
            final TestAnonymousClass5.TestCls cls = map.get(name);
            if (cls == null) {
                return null;
            }
            final int listSize = cls.size();
            final Iterator<TestAnonymousClass5.TestCls> iterator = new Iterator<TestAnonymousClass5.TestCls>() {
                int counter = 0;

                @Override
                public TestAnonymousClass5.TestCls next() {
                    (cls.a)++;
                    (counter)++;
                    return cls;
                }

                @Override
                public boolean hasNext() {
                    return (counter) < listSize;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
            return new Iterable<TestAnonymousClass5.TestCls>() {
                @Override
                public Iterator<TestAnonymousClass5.TestCls> iterator() {
                    return iterator;
                }
            };
        }

        private int size() {
            return 7;
        }

        public void check() {
            TestAnonymousClass5.TestCls v = new TestAnonymousClass5.TestCls();
            v.a = 3;
            map.put("a", v);
            Iterable<TestAnonymousClass5.TestCls> it = test("a");
            TestAnonymousClass5.TestCls next = it.iterator().next();
            Assert.assertThat(next, Matchers.sameInstance(v));
            Assert.assertThat(next.a, Matchers.is(4));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("map.get(name);"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("access$008")));
        // TODO
        // assertThat(code, not(containsString("synthetic")));
    }
}

