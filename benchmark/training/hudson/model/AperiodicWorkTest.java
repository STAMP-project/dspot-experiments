package hudson.model;


import hudson.ExtensionList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class AperiodicWorkTest {
    @Rule
    public JenkinsRule jr = new JenkinsRule();

    @Test
    public void newExtensionsAreScheduled() throws Exception {
        AperiodicWorkTest.TestAperiodicWork tapw = new AperiodicWorkTest.TestAperiodicWork();
        int size = AperiodicWork.all().size();
        ExtensionList.lookup(AperiodicWork.class).add(tapw);
        Assert.assertThat("we have one new AperiodicWork", AperiodicWork.all(), Matchers.hasSize((size + 1)));
        Assert.assertThat("The task was run within 15 seconds", tapw.doneSignal.await(15, TimeUnit.SECONDS), Matchers.is(true));
    }

    private static class TestAperiodicWork extends AperiodicWork {
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
        public AperiodicWork getNewInstance() {
            return this;
        }

        @Override
        protected void doAperiodicRun() {
            doneSignal.countDown();
        }
    }
}

