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
package hudson.tasks;


import Result.FAILURE;
import Result.SUCCESS;
import Result.UNSTABLE;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.queue.QueueTaskFuture;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import static BuildStepMonitor.NONE;


/**
 * Verifies that the last successful and stable builds of a job will be kept if requested.
 */
public class LogRotatorTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void successVsFailure() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.setLogRotator(new LogRotator((-1), 2, (-1), (-1)));
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #1

        project.getBuildersList().replaceBy(Collections.singleton(new FailureBuilder()));
        Assert.assertEquals(FAILURE, LogRotatorTest.build(project));// #2

        Assert.assertEquals(FAILURE, LogRotatorTest.build(project));// #3

        Assert.assertEquals(1, LogRotatorTest.numberOf(project.getLastSuccessfulBuild()));
        project.getBuildersList().replaceBy(Collections.<Builder>emptySet());
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #4

        Assert.assertEquals(4, LogRotatorTest.numberOf(project.getLastSuccessfulBuild()));
        Assert.assertEquals(null, project.getBuildByNumber(1));
        Assert.assertEquals(null, project.getBuildByNumber(2));
        Assert.assertEquals(3, LogRotatorTest.numberOf(project.getLastFailedBuild()));
    }

    @Test
    @Issue("JENKINS-2417")
    public void stableVsUnstable() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.setLogRotator(new LogRotator((-1), 2, (-1), (-1)));
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #1

        project.getPublishersList().replaceBy(Collections.singleton(new LogRotatorTest.TestsFail()));
        Assert.assertEquals(UNSTABLE, LogRotatorTest.build(project));// #2

        Assert.assertEquals(UNSTABLE, LogRotatorTest.build(project));// #3

        Assert.assertEquals(1, LogRotatorTest.numberOf(project.getLastStableBuild()));
        project.getPublishersList().replaceBy(Collections.<Publisher>emptySet());
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #4

        Assert.assertEquals(null, project.getBuildByNumber(1));
        Assert.assertEquals(null, project.getBuildByNumber(2));
    }

    @Test
    @Issue("JENKINS-834")
    public void artifactDelete() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.setLogRotator(new LogRotator((-1), 6, (-1), 2));
        project.getPublishersList().replaceBy(Collections.singleton(new ArtifactArchiver("f", "", true, false)));
        Assert.assertEquals("(no artifacts)", FAILURE, LogRotatorTest.build(project));// #1

        Assert.assertFalse(project.getBuildByNumber(1).getHasArtifacts());
        project.getBuildersList().replaceBy(Collections.singleton(new ArtifactArchiverTest.CreateArtifact()));
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #2

        Assert.assertTrue(project.getBuildByNumber(2).getHasArtifacts());
        project.getBuildersList().replaceBy(Arrays.asList(new ArtifactArchiverTest.CreateArtifact(), new FailureBuilder()));
        Assert.assertEquals(FAILURE, LogRotatorTest.build(project));// #3

        Assert.assertTrue(project.getBuildByNumber(2).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertEquals(FAILURE, LogRotatorTest.build(project));// #4

        Assert.assertTrue(project.getBuildByNumber(2).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(4).getHasArtifacts());
        Assert.assertEquals(FAILURE, LogRotatorTest.build(project));// #5

        Assert.assertTrue(project.getBuildByNumber(2).getHasArtifacts());
        Assert.assertFalse("no better than #4", project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(4).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(5).getHasArtifacts());
        project.getBuildersList().replaceBy(Collections.singleton(new ArtifactArchiverTest.CreateArtifact()));
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #6

        Assert.assertFalse("#2 is still lastSuccessful until #6 is complete", project.getBuildByNumber(2).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(4).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(5).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(6).getHasArtifacts());
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #7

        Assert.assertEquals(null, project.getBuildByNumber(1));
        Assert.assertNotNull(project.getBuildByNumber(2));
        Assert.assertFalse("lastSuccessful was #6 for ArtifactArchiver", project.getBuildByNumber(2).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(4).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(5).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(6).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(7).getHasArtifacts());
        Assert.assertEquals(SUCCESS, LogRotatorTest.build(project));// #8

        Assert.assertEquals(null, project.getBuildByNumber(2));
        Assert.assertNotNull(project.getBuildByNumber(3));
        Assert.assertFalse(project.getBuildByNumber(3).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(4).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(5).getHasArtifacts());
        Assert.assertFalse(project.getBuildByNumber(6).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(7).getHasArtifacts());
        Assert.assertTrue(project.getBuildByNumber(8).getHasArtifacts());
    }

    @Test
    @Issue("JENKINS-27836")
    public void artifactsRetainedWhileBuilding() throws Exception {
        j.getInstance().setNumExecutors(3);
        FreeStyleProject p = j.createFreeStyleProject();
        p.setBuildDiscarder(new LogRotator((-1), 3, (-1), 1));
        LogRotatorTest.StallBuilder sync = new LogRotatorTest.StallBuilder();
        p.getBuildersList().replaceBy(Arrays.asList(new ArtifactArchiverTest.CreateArtifact(), sync));
        p.setConcurrentBuild(true);
        QueueTaskFuture<FreeStyleBuild> futureRun1 = p.scheduleBuild2(0);
        FreeStyleBuild run1 = futureRun1.waitForStart();
        sync.waitFor(run1.getNumber(), 1, TimeUnit.SECONDS);
        QueueTaskFuture<FreeStyleBuild> futureRun2 = p.scheduleBuild2(0);
        FreeStyleBuild run2 = futureRun2.waitForStart();
        sync.waitFor(run2.getNumber(), 1, TimeUnit.SECONDS);
        QueueTaskFuture<FreeStyleBuild> futureRun3 = p.scheduleBuild2(0);
        FreeStyleBuild run3 = futureRun3.waitForStart();
        sync.waitFor(run3.getNumber(), 1, TimeUnit.SECONDS);
        Assert.assertThat("we haven't released run1's guard", run1.isBuilding(), Matchers.is(true));
        Assert.assertThat("we haven't released run2's guard", run2.isBuilding(), Matchers.is(true));
        Assert.assertThat("we haven't released run3's guard", run3.isBuilding(), Matchers.is(true));
        Assert.assertThat("we have artifacts in run1", run1.getHasArtifacts(), Matchers.is(true));
        Assert.assertThat("we have artifacts in run2", run2.getHasArtifacts(), Matchers.is(true));
        Assert.assertThat("we have artifacts in run3", run3.getHasArtifacts(), Matchers.is(true));
        sync.release(run1.getNumber());
        futureRun1.get();
        Assert.assertThat("we have released run1's guard", run1.isBuilding(), Matchers.is(false));
        Assert.assertThat("we haven't released run2's guard", run2.isBuilding(), Matchers.is(true));
        Assert.assertThat("we haven't released run3's guard", run3.isBuilding(), Matchers.is(true));
        Assert.assertThat("run1 is last stable build", p.getLastStableBuild(), Matchers.is(run1));
        Assert.assertThat("run1 is last successful build", p.getLastSuccessfulBuild(), Matchers.is(run1));
        Assert.assertThat("we have artifacts in run1", run1.getHasArtifacts(), Matchers.is(true));
        Assert.assertThat("CRITICAL ASSERTION: we have artifacts in run2", run2.getHasArtifacts(), Matchers.is(true));
        Assert.assertThat("we have artifacts in run3", run3.getHasArtifacts(), Matchers.is(true));
        sync.release(run2.getNumber());
        futureRun2.get();
        Assert.assertThat("we have released run2's guard", run2.isBuilding(), Matchers.is(false));
        Assert.assertThat("we haven't released run3's guard", run3.isBuilding(), Matchers.is(true));
        Assert.assertThat("we have no artifacts in run1", run1.getHasArtifacts(), Matchers.is(false));
        Assert.assertThat("run2 is last stable build", p.getLastStableBuild(), Matchers.is(run2));
        Assert.assertThat("run2 is last successful build", p.getLastSuccessfulBuild(), Matchers.is(run2));
        Assert.assertThat("we have artifacts in run2", run2.getHasArtifacts(), Matchers.is(true));
        Assert.assertThat("we have artifacts in run3", run3.getHasArtifacts(), Matchers.is(true));
        sync.release(run3.getNumber());
        futureRun3.get();
        Assert.assertThat("we have released run3's guard", run3.isBuilding(), Matchers.is(false));
        Assert.assertThat("we have no artifacts in run1", run1.getHasArtifacts(), Matchers.is(false));
        Assert.assertThat("we have no artifacts in run2", run2.getHasArtifacts(), Matchers.is(false));
        Assert.assertThat("run3 is last stable build", p.getLastStableBuild(), Matchers.is(run3));
        Assert.assertThat("run3 is last successful build", p.getLastSuccessfulBuild(), Matchers.is(run3));
        Assert.assertThat("we have artifacts in run3", run3.getHasArtifacts(), Matchers.is(true));
    }

    static class TestsFail extends Publisher {
        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
            build.setResult(UNSTABLE);
            return true;
        }

        public BuildStepMonitor getRequiredMonitorService() {
            return NONE;
        }

        public Descriptor<Publisher> getDescriptor() {
            return new Descriptor<Publisher>(LogRotatorTest.TestsFail.class) {};
        }
    }

    public static class StallBuilder extends TestBuilder {
        private int syncBuildNumber;

        private final Object syncLock = new Object();

        private int waitBuildNumber;

        private final Object waitLock = new Object();

        private final ArtifactArchiver archiver = new ArtifactArchiver("f");

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            archiver.perform(build, launcher, listener);
            Logger.getAnonymousLogger().log(Level.INFO, "Building #{0}", build.getNumber());
            synchronized(waitLock) {
                if ((waitBuildNumber) < (build.getNumber())) {
                    waitBuildNumber = build.getNumber();
                    waitLock.notifyAll();
                }
            }
            Logger.getAnonymousLogger().log(Level.INFO, "Waiting #{0}", build.getNumber());
            synchronized(syncLock) {
                while ((build.getNumber()) > (syncBuildNumber)) {
                    try {
                        syncLock.wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace(listener.fatalError("Interrupted: %s", e.getMessage()));
                        return false;
                    }
                } 
            }
            Logger.getAnonymousLogger().log(Level.INFO, "Done #{0}", build.getNumber());
            return true;
        }

        public void release(int upToBuildNumber) {
            synchronized(syncLock) {
                if ((syncBuildNumber) < upToBuildNumber) {
                    Logger.getAnonymousLogger().log(Level.INFO, "Signal #{0}", upToBuildNumber);
                    syncBuildNumber = upToBuildNumber;
                    syncLock.notifyAll();
                }
            }
        }

        public void waitFor(int buildNumber, long timeout, TimeUnit units) throws InterruptedException, TimeoutException {
            long giveUp = (System.nanoTime()) + (units.toNanos(timeout));
            synchronized(waitLock) {
                while ((waitBuildNumber) < buildNumber) {
                    long remaining = giveUp - (System.nanoTime());
                    if (remaining < 0) {
                        throw new TimeoutException();
                    }
                    waitLock.wait((remaining / 1000000L), ((int) (remaining % 1000000L)));
                } 
            }
        }

        public BuildStepMonitor getRequiredMonitorService() {
            return BuildStepMonitor.NONE;
        }
    }
}

