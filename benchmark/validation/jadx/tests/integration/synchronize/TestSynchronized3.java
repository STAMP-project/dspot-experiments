package jadx.tests.integration.synchronize;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSynchronized3 extends IntegrationTest {
    public static class TestCls {
        private int x;

        public void f() {
        }

        public void test() {
            while (true) {
                synchronized(this) {
                    if ((x) == 0) {
                        throw new IllegalStateException("bad luck");
                    }
                    (x)++;
                    if ((x) == 10) {
                        break;
                    }
                }
                (this.x)++;
                f();
            } 
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSynchronized3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsLines(3, "}", "this.x++;", "f();"));
    }
}

