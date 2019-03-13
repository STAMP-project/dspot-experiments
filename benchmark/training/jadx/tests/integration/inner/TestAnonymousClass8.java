package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass8 extends IntegrationTest {
    public static class TestCls {
        public final double d = Math.abs(4);

        public Runnable test() {
            return new Runnable() {
                public void run() {
                    System.out.println(d);
                }
            };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass8.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("public Runnable test() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return new Runnable() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("public void run() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("this.d);"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

