package jadx.tests.integration.invoke;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestOverloadedMethodInvoke extends IntegrationTest {
    public static class TestCls {
        int c;

        public void method(Throwable th, int a) {
            (c)++;
            if (th != null) {
                c += 100;
            }
            c += a;
        }

        public void method(Exception e, int a) {
            c += 1000;
            if (e != null) {
                c += 10000;
            }
            c += a;
        }

        public void test(Throwable th, Exception e) {
            method(e, 10);
            method(th, 100);
            method(((Throwable) (e)), 1000);
            method(((Exception) (th)), 10000);
        }

        public void check() {
            test(null, new Exception());
            Assert.assertEquals(23212, c);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestOverloadedMethodInvoke.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("public void test(Throwable th, Exception e) {"));
        Assert.assertThat(code, JadxMatchers.containsOne("method(e, 10);"));
        Assert.assertThat(code, JadxMatchers.containsOne("method(th, 100);"));
        Assert.assertThat(code, JadxMatchers.containsOne("method((Throwable) e, 1000);"));
        Assert.assertThat(code, JadxMatchers.containsOne("method((Exception) th, 10000);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("(Exception) e")));
    }
}

