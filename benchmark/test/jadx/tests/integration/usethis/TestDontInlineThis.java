package jadx.tests.integration.usethis;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class TestDontInlineThis extends IntegrationTest {
    public static class TestCls {
        public int field = new Random().nextInt();

        private TestDontInlineThis.TestCls test() {
            TestDontInlineThis.TestCls res;
            if ((field) == 7) {
                res = this;
            } else {
                res = new TestDontInlineThis.TestCls();
            }
            res.method();
            return res;
        }

        private void method() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestDontInlineThis.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("TestDontInlineThis$TestCls res"));
        Assert.assertThat(code, JadxMatchers.containsOne("res = this;"));
        Assert.assertThat(code, JadxMatchers.containsOne("res = new TestDontInlineThis$TestCls();"));
    }
}

