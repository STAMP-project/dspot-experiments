package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass7 extends IntegrationTest {
    public static class TestCls {
        public static Runnable test(final double d) {
            return new Runnable() {
                public void run() {
                    System.out.println(d);
                }
            };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass7.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("public static Runnable test(final double d) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return new Runnable() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("public void run() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println(d);"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

