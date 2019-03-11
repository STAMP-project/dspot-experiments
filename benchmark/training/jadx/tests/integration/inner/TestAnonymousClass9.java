package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass9 extends IntegrationTest {
    public static class TestCls {
        public Callable<String> c = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "str";
            }
        };

        public Runnable test() {
            return new FutureTask<String>(this.c) {
                public void run() {
                    System.out.println(6);
                }
            };
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass9.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("c = new Callable<String>() {"));
        Assert.assertThat(code, JadxMatchers.containsOne("return new FutureTask<String>(this.c) {"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

