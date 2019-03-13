package hudson.model;


import hudson.ExtensionList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class PeriodicWorkTest {
    @Rule
    public JenkinsRule jr = new JenkinsRule();

    @Test
    public void newExtensionsAreScheduled() throws Exception {
        PeriodicWorkTest.TestPeriodicWork tpw = new PeriodicWorkTest.TestPeriodicWork();
        int size = PeriodicWork.all().size();
        ExtensionList.lookup(PeriodicWork.class).add(tpw);
        Assert.assertThat("we have one new PeriodicWork", PeriodicWork.all(), Matchers.hasSize((size + 1)));
        Assert.assertThat("The task was run within 15 seconds", tpw.doneSignal.await(15, TimeUnit.SECONDS), Matchers.is(true));
    }

    private static class TestPeriodicWork extends PeriodicWork {
        CountDownLatch doneSignal = new CountDownLatch(1);

        @Override
        public long getRecurrencePeriod() {
            // should make this only ever run once initially for testing.
            return Long.MAX_VALUE;
        }

        @Override
        public long getInitialDelay() {
            // Don't delay just run it!
            return 0L;
        }

        @Override
        protected void doRun() throws Exception {
            doneSignal.countDown();
        }
    }
}

