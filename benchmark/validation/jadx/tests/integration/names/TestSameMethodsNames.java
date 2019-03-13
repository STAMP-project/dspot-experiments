package jadx.tests.integration.names;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSameMethodsNames extends IntegrationTest {
    public static class TestCls<V> {
        public static void test() {
            new TestSameMethodsNames.TestCls.Bug().Bug();
        }

        public static class Bug {
            public Bug() {
                System.out.println("constructor");
            }

            void Bug() {
                System.out.println("Bug");
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSameMethodsNames.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("new Bug().Bug();"));
    }
}

