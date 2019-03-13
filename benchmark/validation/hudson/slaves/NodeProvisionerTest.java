/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.slaves;


import hudson.BulkChange;
import hudson.Launcher;
import hudson.tasks.Builder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SleepBuilder;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class NodeProvisionerTest {
    @Rule
    public JenkinsRule r = /* run x1000 the regular speed to speed up the test */
    new NodeProvisionerRule(10, 100, 10);

    /**
     * Latch synchronization primitive that waits for N thread to pass the checkpoint.
     * <p>
     * This is used to make sure we get a set of builds that run long enough.
     */
    static class Latch {
        /**
         * Initial value
         */
        public final CountDownLatch counter;

        private final int init;

        Latch(int n) {
            this.init = n;
            this.counter = new CountDownLatch(n);
        }

        void block() throws InterruptedException {
            this.counter.countDown();
            this.counter.await(60, TimeUnit.SECONDS);
        }

        /**
         * Creates a builder that blocks until the latch opens.
         */
        public Builder createBuilder() {
            return new Builder() {
                public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                    block();
                    return true;
                }
            };
        }
    }

    /**
     * Scenario: schedule a build and see if one slave is provisioned.
     */
    // TODO fragile
    @Test
    public void autoProvision() throws Exception {
        try (BulkChange bc = new BulkChange(r.jenkins)) {
            DummyCloudImpl cloud = initHudson(10);
            FreeStyleProject p = createJob(new SleepBuilder(10));
            Future<FreeStyleBuild> f = p.scheduleBuild2(0);
            f.get(30, TimeUnit.SECONDS);// if it's taking too long, abort.

            // since there's only one job, we expect there to be just one slave
            Assert.assertEquals(1, cloud.numProvisioned);
        }
    }

    /**
     * Scenario: we got a lot of jobs all of the sudden, and we need to fire up a few nodes.
     */
    // TODO fragile
    @Test
    public void loadSpike() throws Exception {
        try (BulkChange bc = new BulkChange(r.jenkins)) {
            DummyCloudImpl cloud = initHudson(0);
            verifySuccessfulCompletion(buildAll(create5SlowJobs(new NodeProvisionerTest.Latch(5))));
            // the time it takes to complete a job is eternally long compared to the time it takes to launch
            // a new slave, so in this scenario we end up allocating 5 slaves for 5 jobs.
            Assert.assertEquals(5, cloud.numProvisioned);
        }
    }

    /**
     * Scenario: make sure we take advantage of statically configured slaves.
     */
    // TODO fragile
    @Test
    public void baselineSlaveUsage() throws Exception {
        try (BulkChange bc = new BulkChange(r.jenkins)) {
            DummyCloudImpl cloud = initHudson(0);
            // add slaves statically upfront
            r.createSlave().toComputer().connect(false).get();
            r.createSlave().toComputer().connect(false).get();
            verifySuccessfulCompletion(buildAll(create5SlowJobs(new NodeProvisionerTest.Latch(5))));
            // we should have used two static slaves, thus only 3 slaves should have been provisioned
            Assert.assertEquals(3, cloud.numProvisioned);
        }
    }

    /**
     * Scenario: loads on one label shouldn't translate to load on another label.
     */
    // TODO fragile
    @Test
    public void labels() throws Exception {
        try (BulkChange bc = new BulkChange(r.jenkins)) {
            DummyCloudImpl cloud = initHudson(0);
            Label blue = r.jenkins.getLabel("blue");
            Label red = r.jenkins.getLabel("red");
            cloud.label = red;
            // red jobs
            List<FreeStyleProject> redJobs = create5SlowJobs(new NodeProvisionerTest.Latch(5));
            for (FreeStyleProject p : redJobs)
                p.setAssignedLabel(red);

            // blue jobs
            List<FreeStyleProject> blueJobs = create5SlowJobs(new NodeProvisionerTest.Latch(5));
            for (FreeStyleProject p : blueJobs)
                p.setAssignedLabel(blue);

            // build all
            List<Future<FreeStyleBuild>> blueBuilds = buildAll(blueJobs);
            verifySuccessfulCompletion(buildAll(redJobs));
            // cloud should only give us 5 nodes for 5 red jobs
            Assert.assertEquals(5, cloud.numProvisioned);
            // and all blue jobs should be still stuck in the queue
            for (Future<FreeStyleBuild> bb : blueBuilds)
                Assert.assertFalse(bb.isDone());

        }
    }
}

