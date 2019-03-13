package jadx.tests.integration.others;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldInit2 extends IntegrationTest {
    public static class TestCls {
        public interface BasicAbstract {
            void doSomething();
        }

        private TestFieldInit2.TestCls.BasicAbstract x = new TestFieldInit2.TestCls.BasicAbstract() {
            @Override
            public void doSomething() {
                y = 1;
            }
        };

        private int y = 0;

        public TestCls() {
        }

        public TestCls(int z) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldInit2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("x = new BasicAbstract() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("y = 0;"));
        Assert.assertThat(code, JadxMatchers.containsLines(1, "public TestFieldInit2$TestCls(int z) {", "}"));
    }
}

