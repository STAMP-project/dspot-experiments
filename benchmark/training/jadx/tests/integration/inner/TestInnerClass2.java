package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import java.util.Timer;
import java.util.TimerTask;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClass2 extends IntegrationTest {
    public static class TestCls {
        private static class TerminateTask extends TimerTask {
            @Override
            public void run() {
                System.err.println("Test timed out");
            }
        }

        public void test() {
            new Timer().schedule(new TestInnerClass2.TestCls.TerminateTask(), 1000);
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInnerClass2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("new Timer().schedule(new TerminateTask(), 1000);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("synthetic")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("this")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("null")));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("AnonymousClass")));
    }
}

