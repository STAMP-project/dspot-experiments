package jadx.tests.integration.usethis;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Objects;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInlineThis2 extends IntegrationTest {
    public static class TestCls {
        public int field;

        private void test() {
            TestInlineThis2.TestCls thisVar = this;
            if (Objects.isNull(thisVar)) {
                System.out.println("null");
            }
            thisVar.method();
            thisVar.field = 123;
        }

        private void method() {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInlineThis2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("thisVar")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("thisVar.method()")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("thisVar.field")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("= this")));
        Assert.assertThat(code, JadxMatchers.containsOne("if (Objects.isNull(this)) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.field = 123;"));
        Assert.assertThat(code, JadxMatchers.containsOne("method();"));
    }
}

