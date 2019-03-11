package jadx.tests.integration.usethis;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInlineThis extends IntegrationTest {
    public static class TestCls {
        public int field;

        private void test() {
            TestInlineThis.TestCls something = this;
            something.method();
            something.field = 123;
        }

        private void method() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInlineThis.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("something")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("something.method()")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("something.field")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("= this")));
        Assert.assertThat(code, JadxMatchers.containsOne("this.field = 123;"));
        Assert.assertThat(code, JadxMatchers.containsOne("method();"));
    }
}

