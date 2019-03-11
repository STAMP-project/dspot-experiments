/**
 * The MIT License
 *
 * Copyright 2014 Jesse Glick.
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


import WorkspaceCleanupThread.recurrencePeriodHours;
import hudson.FilePath;
import hudson.remoting.VirtualChannel;
import hudson.scm.NullSCM;
import hudson.slaves.DumbSlave;
import hudson.slaves.WorkspaceList;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import jenkins.MasterToSlaveFileCallable;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.WithoutJenkins;

import static WorkspaceCleanupThread.disabled;
import static WorkspaceCleanupThread.retainForDays;


public class WorkspaceCleanupThreadTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Rule
    public LoggerRule logs = new LoggerRule().record(WorkspaceCleanupThread.class, Level.ALL);

    @Test
    public void cleanUpSlaves() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws1 = createOldWorkspaceOn(r.createOnlineSlave(), p);
        p.setAssignedNode(r.jenkins);
        FreeStyleBuild b = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        Assert.assertEquals(r.jenkins, b.getBuiltOn());
        FilePath ws2 = b.getWorkspace();
        FilePath ws3 = createOldWorkspaceOn(r.createOnlineSlave(), p);
        performCleanup();
        Assert.assertFalse(ws1.exists());// Old one - deleted

        Assert.assertTrue(ws2.exists());// Not old enough - kept

        Assert.assertTrue(ws3.exists());// Latest - kept

    }

    @Issue("JENKINS-21023")
    @Test
    public void modernMasterWorkspaceLocation() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws1 = createOldWorkspaceOn(r.jenkins, p);
        DumbSlave s = r.createOnlineSlave();
        FilePath ws2 = createOldWorkspaceOn(s, p);
        Assert.assertEquals(s, p.getLastBuiltOn());
        performCleanup();
        Assert.assertFalse(ws1.exists());
        Assert.assertTrue(ws2.exists());
    }

    @Issue("JENKINS-21023")
    @Test
    public void jobInFolder() throws Exception {
        MockFolder d = r.createFolder("d");
        FreeStyleProject p1 = d.createProject(FreeStyleProject.class, "p");
        FilePath ws1 = createOldWorkspaceOn(r.jenkins, p1);
        DumbSlave s1 = r.createOnlineSlave();
        FilePath ws2 = createOldWorkspaceOn(s1, p1);
        DumbSlave s2 = r.createOnlineSlave();
        FilePath ws3 = createOldWorkspaceOn(s2, p1);
        Assert.assertEquals(s2, p1.getLastBuiltOn());
        FreeStyleProject p2 = d.createProject(FreeStyleProject.class, "p2");
        FilePath ws4 = createOldWorkspaceOn(s1, p2);
        Assert.assertEquals(s1, p2.getLastBuiltOn());
        ws2.getParent().act(new WorkspaceCleanupThreadTest.Touch(0));// ${s1.rootPath}/workspace/d/

        performCleanup();
        Assert.assertFalse(ws1.exists());
        Assert.assertFalse(ws2.exists());
        Assert.assertTrue(ws3.exists());
        Assert.assertTrue(ws4.exists());
    }

    @Test
    public void doNothingIfDisabled() throws Exception {
        disabled = true;
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws = createOldWorkspaceOn(r.jenkins, p);
        createOldWorkspaceOn(r.createOnlineSlave(), p);
        performCleanup();
        Assert.assertTrue(ws.exists());
        disabled = false;
        performCleanup();
        Assert.assertFalse(ws.exists());
    }

    @Test
    public void removeOnlyWhatIsOldEnough() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws = createOldWorkspaceOn(r.jenkins, p);
        createOldWorkspaceOn(r.createOnlineSlave(), p);
        long twoDaysOld = (System.currentTimeMillis()) - (TimeUnit.DAYS.toMillis(2));
        ws.act(new WorkspaceCleanupThreadTest.Touch(twoDaysOld));
        retainForDays = 3;
        performCleanup();
        Assert.assertTrue(ws.exists());
        retainForDays = 1;
        performCleanup();
        Assert.assertFalse(ws.exists());
    }

    @Test
    @WithoutJenkins
    public void recurrencePeriodIsInHours() {
        Assert.assertEquals(TimeUnit.HOURS.toMillis(recurrencePeriodHours), new WorkspaceCleanupThread().getRecurrencePeriod());
    }

    @Test
    public void vetoByScm() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws = createOldWorkspaceOn(r.jenkins, p);
        createOldWorkspaceOn(r.createOnlineSlave(), p);
        p.setScm(new WorkspaceCleanupThreadTest.VetoSCM(false));
        performCleanup();
        Assert.assertTrue(ws.exists());
        p.setScm(new WorkspaceCleanupThreadTest.VetoSCM(true));
        performCleanup();
        Assert.assertFalse(ws.exists());
    }

    @Issue("JENKINS-27152")
    @Test
    public void deleteTemporaryDirectory() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FilePath ws = createOldWorkspaceOn(r.jenkins, p);
        FilePath tmp = WorkspaceList.tempDir(ws);
        tmp.child("stuff").write("content", null);
        createOldWorkspaceOn(r.createOnlineSlave(), p);
        performCleanup();
        Assert.assertFalse(ws.exists());
        Assert.assertFalse("temporary directory should be cleaned up as well", tmp.exists());
    }

    private static final class VetoSCM extends NullSCM {
        private final boolean answer;

        public VetoSCM(boolean answer) {
            this.answer = answer;
        }

        @Override
        public boolean processWorkspaceBeforeDeletion(Job<?, ?> project, FilePath workspace, Node node) throws IOException, InterruptedException {
            return answer;
        }
    }

    private static final class Touch extends MasterToSlaveFileCallable<Void> {
        private static final long serialVersionUID = 1L;

        private final long time;

        public Touch(long time) {
            this.time = time;
        }

        @Override
        public Void invoke(File f, VirtualChannel channel) throws IOException, InterruptedException {
            Assume.assumeTrue(("failed to reset lastModified on " + f), f.setLastModified(time));
            return null;
        }
    }
}

