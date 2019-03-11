/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
 * Copyright (c) 2015 Christopher Simons
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
package hudson.model;


import Item.EXTENDED_READ;
import ProjectNamingStrategy.DEFAULT_NAMING_STRATEGY;
import Result.ABORTED;
import Result.FAILURE;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.Functions;
import hudson.model.queue.QueueTaskFuture;
import hudson.tasks.ArtifactArchiver;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.util.TextFile;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import jenkins.model.ProjectNamingStrategy;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.FailureBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.RunLoadCounter;
import org.jvnet.hudson.test.SleepBuilder;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class JobTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @SuppressWarnings("unchecked")
    @Test
    public void jobPropertySummaryIsShownInMainPage() throws Exception {
        AbstractProject project = j.createFreeStyleProject();
        project.addProperty(new JobTest.JobPropertyImpl("NeedleInPage"));
        HtmlPage page = j.createWebClient().getPage(project);
        WebAssert.assertTextPresent(page, "NeedleInPage");
    }

    @Test
    public void buildNumberSynchronization() throws Exception {
        AbstractProject project = j.createFreeStyleProject();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch stopLatch = new CountDownLatch(2);
        JobTest.BuildNumberSyncTester test1 = new JobTest.BuildNumberSyncTester(project, startLatch, stopLatch, true);
        JobTest.BuildNumberSyncTester test2 = new JobTest.BuildNumberSyncTester(project, startLatch, stopLatch, false);
        new Thread(test1).start();
        new Thread(test2).start();
        startLatch.countDown();
        stopLatch.await();
        Assert.assertTrue(test1.message, test2.passed);
        Assert.assertTrue(test2.message, test2.passed);
    }

    public static class BuildNumberSyncTester implements Runnable {
        private final AbstractProject p;

        private final CountDownLatch start;

        private final CountDownLatch stop;

        private final boolean assign;

        String message;

        boolean passed;

        BuildNumberSyncTester(AbstractProject p, CountDownLatch l1, CountDownLatch l2, boolean b) {
            this.p = p;
            this.start = l1;
            this.stop = l2;
            this.assign = b;
            this.message = null;
            this.passed = false;
        }

        public void run() {
            try {
                start.await();
                for (int i = 0; i < 100; i++) {
                    int buildNumber = -1;
                    int savedBuildNumber = -1;
                    TextFile f;
                    synchronized(p) {
                        if (assign) {
                            buildNumber = p.assignBuildNumber();
                            f = p.getNextBuildNumberFile();
                            if (f == null) {
                                this.message = "Could not get build number file";
                                this.passed = false;
                                return;
                            }
                            savedBuildNumber = Integer.parseInt(f.readTrim());
                            if (buildNumber != (savedBuildNumber - 1)) {
                                this.message = ((("Build numbers don't match (" + buildNumber) + ", ") + (savedBuildNumber - 1)) + ")";
                                this.passed = false;
                                return;
                            }
                        } else {
                            buildNumber = (p.getNextBuildNumber()) + 100;
                            p.updateNextBuildNumber(buildNumber);
                            f = p.getNextBuildNumberFile();
                            if (f == null) {
                                this.message = "Could not get build number file";
                                this.passed = false;
                                return;
                            }
                            savedBuildNumber = Integer.parseInt(f.readTrim());
                            if (buildNumber != savedBuildNumber) {
                                this.message = ((("Build numbers don't match (" + buildNumber) + ", ") + savedBuildNumber) + ")";
                                this.passed = false;
                                return;
                            }
                        }
                    }
                }
                this.passed = true;
            } catch (InterruptedException e) {
            } catch (IOException e) {
                Assert.fail("Failed to assign build number");
            } finally {
                stop.countDown();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static class JobPropertyImpl extends JobProperty<Job<?, ?>> {
        public static JobTest.JobPropertyImpl.DescriptorImpl DESCRIPTOR = new JobTest.JobPropertyImpl.DescriptorImpl();

        private final String testString;

        public JobPropertyImpl(String testString) {
            this.testString = testString;
        }

        public String getTestString() {
            return testString;
        }

        @Override
        public JobPropertyDescriptor getDescriptor() {
            return JobTest.JobPropertyImpl.DESCRIPTOR;
        }

        private static final class DescriptorImpl extends JobPropertyDescriptor {
            public String getDisplayName() {
                return "";
            }
        }
    }

    @LocalData
    @Test
    public void readPermission() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.assertFails("job/testJob/", HttpURLConnection.HTTP_NOT_FOUND);
        wc.assertFails("jobCaseInsensitive/testJob/", HttpURLConnection.HTTP_NOT_FOUND);
        wc.withBasicCredentials("joe");// Has Item.READ permission

        // Verify we can access both URLs:
        wc.goTo("job/testJob/");
        wc.goTo("jobCaseInsensitive/TESTJOB/");
    }

    @LocalData
    @Test
    public void configDotXmlPermission() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        j.jenkins.setCrumbIssuer(null);
        JenkinsRule.WebClient wc = j.createWebClient();
        boolean saveEnabled = EXTENDED_READ.getEnabled();
        EXTENDED_READ.setEnabled(true);
        try {
            wc.assertFails("job/testJob/config.xml", HttpURLConnection.HTTP_FORBIDDEN);
            wc.setThrowExceptionOnFailingStatusCode(false);
            // Has CONFIGURE and EXTENDED_READ permission
            wc.withBasicApiToken(User.getById("alice", true));
            JobTest.tryConfigDotXml(wc, HttpURLConnection.HTTP_INTERNAL_ERROR, "Both perms; should get 500");
            // Has only CONFIGURE permission (this should imply EXTENDED_READ)
            wc.withBasicApiToken(User.getById("bob", true));
            JobTest.tryConfigDotXml(wc, HttpURLConnection.HTTP_INTERNAL_ERROR, "Config perm should imply EXTENDED_READ");
            // Has only EXTENDED_READ permission
            wc.withBasicApiToken(User.getById("charlie", true));
            JobTest.tryConfigDotXml(wc, HttpURLConnection.HTTP_FORBIDDEN, "No permission, should get 403");
        } finally {
            EXTENDED_READ.setEnabled(saveEnabled);
        }
    }

    @LocalData
    @Issue("JENKINS-6371")
    @Test
    public void getArtifactsUpTo() throws Exception {
        // There was a bug where intermediate directories were counted,
        // so too few artifacts were returned.
        Run r = j.jenkins.getItemByFullName("testJob", Job.class).getLastCompletedBuild();
        Assert.assertEquals(3, r.getArtifacts().size());
        Assert.assertEquals(3, r.getArtifactsUpTo(3).size());
        Assert.assertEquals(2, r.getArtifactsUpTo(2).size());
        Assert.assertEquals(1, r.getArtifactsUpTo(1).size());
    }

    @Issue("JENKINS-10182")
    @Test
    public void emptyDescriptionReturnsEmptyPage() throws Exception {
        // A NPE was thrown if a job had a null (empty) description.
        JenkinsRule.WebClient wc = j.createWebClient();
        FreeStyleProject project = j.createFreeStyleProject("project");
        project.setDescription("description");
        Assert.assertEquals("description", getContent());
        project.setDescription(null);
        Assert.assertEquals("", getContent());
    }

    @Test
    public void projectNamingStrategy() throws Exception {
        j.jenkins.setProjectNamingStrategy(new ProjectNamingStrategy.PatternProjectNamingStrategy("DUMMY.*", false));
        final FreeStyleProject p = j.createFreeStyleProject("DUMMY_project");
        Assert.assertNotNull("no project created", p);
        try {
            j.createFreeStyleProject("project");
            Assert.fail("should not get here, the project name is not allowed, therefore the creation must fail!");
        } catch (Failure e) {
            // OK, expected
        } finally {
            // set it back to the default naming strategy, otherwise all other tests would fail to create jobs!
            j.jenkins.setProjectNamingStrategy(DEFAULT_NAMING_STRATEGY);
        }
        j.createFreeStyleProject("project");
    }

    @Issue("JENKINS-16023")
    @Test
    public void getLastFailedBuild() throws Exception {
        final FreeStyleProject p = j.createFreeStyleProject();
        RunLoadCounter.prepare(p);
        p.getBuildersList().add(new FailureBuilder());
        j.assertBuildStatus(FAILURE, p.scheduleBuild2(0).get());
        j.assertBuildStatus(FAILURE, p.scheduleBuild2(0).get());
        j.assertBuildStatus(FAILURE, p.scheduleBuild2(0).get());
        p.getBuildersList().remove(FailureBuilder.class);
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        Assert.assertEquals(6, p.getLastSuccessfulBuild().getNumber());
        Assert.assertEquals(3, RunLoadCounter.assertMaxLoads(p, 1, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return p.getLastFailedBuild().getNumber();
            }
        }).intValue());
    }

    @Issue("JENKINS-19764")
    @Test
    public void testRenameWithCustomBuildsDirWithSubdir() throws Exception {
        j.jenkins.setRawBuildsDir("${JENKINS_HOME}/builds/${ITEM_FULL_NAME}/builds");
        final FreeStyleProject p = j.createFreeStyleProject();
        p.scheduleBuild2(0).get();
        p.renameTo("different-name");
    }

    @Issue("JENKINS-44657")
    @Test
    public void testRenameWithCustomBuildsDirWithBuildsIntact() throws Exception {
        j.jenkins.setRawBuildsDir("${JENKINS_HOME}/builds/${ITEM_FULL_NAME}/builds");
        final FreeStyleProject p = j.createFreeStyleProject();
        final File oldBuildsDir = p.getBuildDir();
        j.buildAndAssertSuccess(p);
        String oldDirContent = dirContent(oldBuildsDir);
        p.renameTo("different-name");
        final File newBuildDir = p.getBuildDir();
        Assert.assertNotNull(newBuildDir);
        Assert.assertNotEquals(oldBuildsDir.getAbsolutePath(), newBuildDir.getAbsolutePath());
        String newDirContent = dirContent(newBuildDir);
        Assert.assertEquals(oldDirContent, newDirContent);
    }

    @Issue("JENKINS-44657")
    @Test
    public void testRenameWithCustomBuildsDirWithBuildsIntactInFolder() throws Exception {
        j.jenkins.setRawBuildsDir("${JENKINS_HOME}/builds/${ITEM_FULL_NAME}/builds");
        final MockFolder f = j.createFolder("F");
        final FreeStyleProject p1 = f.createProject(FreeStyleProject.class, "P1");
        j.buildAndAssertSuccess(p1);
        File oldP1BuildsDir = p1.getBuildDir();
        final String oldP1DirContent = dirContent(oldP1BuildsDir);
        f.renameTo("different-name");
        File newP1BuildDir = p1.getBuildDir();
        Assert.assertNotNull(newP1BuildDir);
        Assert.assertNotEquals(oldP1BuildsDir.getAbsolutePath(), newP1BuildDir.getAbsolutePath());
        String newP1DirContent = dirContent(newP1BuildDir);
        Assert.assertEquals(oldP1DirContent, newP1DirContent);
        final FreeStyleProject p2 = f.createProject(FreeStyleProject.class, "P2");
        if (Functions.isWindows()) {
            p2.getBuildersList().add(new BatchFile("echo hello > hello.txt"));
        } else {
            p2.getBuildersList().add(new Shell("echo hello > hello.txt"));
        }
        p2.getPublishersList().add(new ArtifactArchiver("*.txt"));
        j.buildAndAssertSuccess(p2);
        File oldP2BuildsDir = p2.getBuildDir();
        final String oldP2DirContent = dirContent(oldP2BuildsDir);
        FreeStyleBuild b2 = p2.getBuilds().getLastBuild();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        b2.getLogText().writeRawLogTo(0, out);
        final String oldB2Log = new String(out.toByteArray());
        Assert.assertTrue(b2.getArtifactManager().root().child("hello.txt").exists());
        f.renameTo("something-else");
        // P1 check again
        newP1BuildDir = p1.getBuildDir();
        Assert.assertNotNull(newP1BuildDir);
        Assert.assertNotEquals(oldP1BuildsDir.getAbsolutePath(), newP1BuildDir.getAbsolutePath());
        newP1DirContent = dirContent(newP1BuildDir);
        Assert.assertEquals(oldP1DirContent, newP1DirContent);
        // P2 check
        b2 = p2.getBuilds().getLastBuild();
        Assert.assertNotNull(b2);
        out = new ByteArrayOutputStream();
        b2.getLogText().writeRawLogTo(0, out);
        final String newB2Log = new String(out.toByteArray());
        Assert.assertEquals(oldB2Log, newB2Log);
        Assert.assertTrue(b2.getArtifactManager().root().child("hello.txt").exists());
        File newP2BuildDir = p2.getBuildDir();
        Assert.assertNotNull(newP2BuildDir);
        Assert.assertNotEquals(oldP2BuildsDir.getAbsolutePath(), newP2BuildDir.getAbsolutePath());
        String newP2DirContent = dirContent(newP2BuildDir);
        Assert.assertEquals(oldP2DirContent, newP2DirContent);
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameTrimsLeadingSpace() throws Exception {
        tryRename("myJob1", " foo", "foo");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameTrimsTrailingSpace() throws Exception {
        tryRename("myJob2", "foo ", "foo");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testAllowTrimmingByUser() throws Exception {
        Assume.assumeFalse("Unix-only test.", Functions.isWindows());
        tryRename("myJob3 ", "myJob3", "myJob3");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameWithLeadingSpaceTrimsLeadingSpace() throws Exception {
        Assume.assumeFalse("Unix-only test.", Functions.isWindows());
        tryRename(" myJob4", " foo", "foo");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameWithLeadingSpaceTrimsTrailingSpace() throws Exception {
        Assume.assumeFalse("Unix-only test.", Functions.isWindows());
        tryRename(" myJob5", "foo ", "foo");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameWithTrailingSpaceTrimsTrailingSpace() throws Exception {
        Assume.assumeFalse("Unix-only test.", Functions.isWindows());
        tryRename("myJob6 ", "foo ", "foo");
    }

    @Issue("JENKINS-30502")
    @Test
    public void testRenameWithTrailingSpaceTrimsLeadingSpace() throws Exception {
        Assume.assumeFalse("Unix-only test.", Functions.isWindows());
        tryRename("myJob7 ", " foo", "foo");
    }

    @Issue("JENKINS-35160")
    @Test
    public void interruptOnDelete() throws Exception {
        j.jenkins.setNumExecutors(2);
        Queue.getInstance().maintain();
        final FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("dummy", "0")));
        p.setConcurrentBuild(true);
        p.getBuildersList().add(new SleepBuilder(30000));// we want the uninterrupted job to run for long time

        FreeStyleBuild build1 = p.scheduleBuild2(0).getStartCondition().get();
        FreeStyleBuild build2 = p.scheduleBuild2(0).getStartCondition().get();
        QueueTaskFuture<FreeStyleBuild> build3 = p.scheduleBuild2(0);
        long start = System.nanoTime();
        p.delete();
        long end = System.nanoTime();
        Assert.assertThat((end - start), Matchers.lessThan(TimeUnit.SECONDS.toNanos(1)));
        Assert.assertThat(build1.getResult(), Matchers.is(ABORTED));
        Assert.assertThat(build2.getResult(), Matchers.is(ABORTED));
        Assert.assertThat(build3.isCancelled(), Matchers.is(true));
    }
}

