package jadx.tests.integration.switches;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestSwitch3 extends IntegrationTest {
    public static class TestCls {
        private int i;

        void test(int a) {
            switch (a) {
                case 1 :
                    i = 1;
                    return;
                case 2 :
                case 3 :
                    i = 2;
                    return;
                default :
                    i = 4;
                    break;
            }
            i = 5;
        }

        public void check() {
            test(1);
            Assert.assertThat(i, Matchers.is(1));
            test(2);
            Assert.assertThat(i, Matchers.is(2));
            test(3);
            Assert.assertThat(i, Matchers.is(2));
            test(4);
            Assert.assertThat(i, Matchers.is(5));
            test(10);
            Assert.assertThat(i, Matchers.is(5));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestSwitch3.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.countString(0, "break;"));
        Assert.assertThat(code, JadxMatchers.countString(3, "return;"));
    }
}

