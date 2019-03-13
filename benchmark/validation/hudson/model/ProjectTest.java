/**
 * The MIT License
 *
 * Copyright 2013 Red Hat, Inc.
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


import AbstractProject.WorkspaceOfflineReason.all_suitable_nodes_are_offline;
import Computer.threadPoolForRemoting;
import EnvironmentVariablesNodeProperty.Entry;
import Fingerprint.RangeSet;
import Jenkins.READ;
import JenkinsRule.WebClient;
import Job.BUILD;
import Job.CONFIGURE;
import Job.DELETE;
import Job.WIPEOUT;
import NodeProvisioner.PlannedNode;
import TaskListener.NULL;
import antlr.ANTLRException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.event.Event;
import hudson.Descriptor;
import hudson.model.AbstractProject.BecauseOfDownstreamBuildInProgress;
import hudson.model.AbstractProject.BecauseOfUpstreamBuildInProgress;
import hudson.model.Cause.LegacyCodeCause;
import hudson.model.Queue.Executable;
import hudson.model.Queue.Task;
import hudson.model.labels.LabelAtom;
import hudson.model.queue.QueueTaskFuture;
import hudson.model.queue.SubTask;
import hudson.model.queue.SubTaskContributor;
import hudson.scm.NullSCM;
import hudson.scm.PollingResult;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AccessDeniedException2;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.slaves.Cloud;
import hudson.slaves.DumbSlave;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProvisioner;
import hudson.tasks.Computer;
import hudson.tasks.Label;
import hudson.triggers.SCMTrigger;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import jenkins.model.BlockedBecauseOfBuildInProgress;
import jenkins.model.Jenkins;
import jenkins.model.WorkspaceWriter;
import jenkins.scm.DefaultSCMCheckoutStrategyImpl;
import jenkins.scm.SCMCheckoutStrategy;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.reactor.ReactorException;
import org.jvnet.hudson.test.FakeChangeLogSCM;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.TestExtension;

import static Result.SUCCESS;


/**
 *
 *
 * @author Lucie Votypkova
 */
public class ProjectTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    public static boolean createAction = false;

    public static boolean getFilePath = false;

    public static boolean createSubTask = false;

    @Test
    public void testSave() throws IOException, InterruptedException, ReactorException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.disabled = true;
        p.nextBuildNumber = 5;
        p.description = "description";
        p.save();
        j.jenkins.reload();
        Assert.assertEquals("All persistent data should be saved.", "description", p.description);
        Assert.assertEquals("All persistent data should be saved.", 5, p.nextBuildNumber);
        Assert.assertEquals("All persistent data should be saved", true, p.disabled);
    }

    @Test
    public void testOnCreateFromScratch() throws IOException, Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        j.buildAndAssertSuccess(p);
        p.removeRun(p.getLastBuild());
        ProjectTest.createAction = true;
        p.onCreatedFromScratch();
        Assert.assertNotNull("Project should have last build.", p.getLastBuild());
        Assert.assertNotNull("Project should have transient action TransientAction.", p.getAction(ProjectTest.TransientAction.class));
        ProjectTest.createAction = false;
    }

    @Test
    public void testOnLoad() throws IOException, Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        j.buildAndAssertSuccess(p);
        p.removeRun(p.getLastBuild());
        ProjectTest.createAction = true;
        p.onLoad(j.jenkins, "project");
        Assert.assertTrue("Project should have a build.", ((p.getLastBuild()) != null));
        Assert.assertTrue("Project should have a scm.", ((p.getScm()) != null));
        Assert.assertTrue("Project should have Transient Action TransientAction.", ((p.getAction(ProjectTest.TransientAction.class)) != null));
        ProjectTest.createAction = false;
    }

    @Test
    public void testGetEnvironment() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Slave slave = j.createOnlineSlave();
        EnvironmentVariablesNodeProperty.Entry entry = new EnvironmentVariablesNodeProperty.Entry("jdk", "some_java");
        slave.getNodeProperties().add(new EnvironmentVariablesNodeProperty(entry));
        EnvVars var = p.getEnvironment(slave, NULL);
        Assert.assertEquals("Environment should have set jdk.", "some_java", var.get("jdk"));
    }

    @Test
    public void testPerformDelete() throws IOException, Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.performDelete();
        Assert.assertFalse("Project should be deleted from disk.", p.getConfigFile().exists());
        Assert.assertTrue("Project should be disabled when deleting start.", p.isDisabled());
    }

    @Test
    public void testGetAssignedLabel() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.setAssignedLabel(j.jenkins.getSelfLabel());
        Slave slave = j.createOnlineSlave();
        Assert.assertEquals("Project should have Jenkins's self label.", j.jenkins.getSelfLabel(), p.getAssignedLabel());
        p.setAssignedLabel(null);
        Assert.assertNull("Project should not have any label.", p.getAssignedLabel());
        p.setAssignedLabel(slave.getSelfLabel());
        Assert.assertEquals("Project should have self label of slave", slave.getSelfLabel(), p.getAssignedLabel());
    }

    @Test
    public void testGetAssignedLabelString() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Slave slave = j.createOnlineSlave();
        Assert.assertNull("Project should not have any label.", p.getAssignedLabelString());
        p.setAssignedLabel(j.jenkins.getSelfLabel());
        Assert.assertNull("Project should return null, because assigned label is Jenkins.", p.getAssignedLabelString());
        p.setAssignedLabel(slave.getSelfLabel());
        Assert.assertEquals("Project should return name of slave.", slave.getSelfLabel().name, p.getAssignedLabelString());
    }

    @Test
    public void testGetSomeWorkspace() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Assert.assertNull("Project which has never run should not have any workspace.", p.getSomeWorkspace());
        ProjectTest.getFilePath = true;
        Assert.assertNotNull("Project should have any workspace because WorkspaceBrowser find some.", p.getSomeWorkspace());
        ProjectTest.getFilePath = false;
        String cmd = "echo ahoj > some.log";
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile(cmd) : new Shell(cmd)));
        j.buildAndAssertSuccess(p);
        Assert.assertNotNull("Project should has any workspace.", p.getSomeWorkspace());
    }

    @Test
    public void testGetSomeBuildWithWorkspace() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        String cmd = "echo ahoj > some.log";
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile(cmd) : new Shell(cmd)));
        Assert.assertNull("Project which has never run should not have any build with workspace.", p.getSomeBuildWithWorkspace());
        j.buildAndAssertSuccess(p);
        Assert.assertEquals("Last build should have workspace.", p.getLastBuild(), p.getSomeBuildWithWorkspace());
        p.getLastBuild().delete();
        Assert.assertNull("Project should not have build with some workspace.", p.getSomeBuildWithWorkspace());
    }

    @Issue("JENKINS-10450")
    @Test
    public void workspaceBrowsing() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        String cmd = "echo ahoj > some.log";
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile(cmd) : new Shell(cmd)));
        j.buildAndAssertSuccess(p);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("job/project/ws/some.log", "text/plain");
        wc.assertFails("job/project/ws/other.log", 404);
        p.doDoWipeOutWorkspace();
        wc.assertFails("job/project/ws/some.log", 404);
    }

    @Test
    public void testGetQuietPeriod() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Assert.assertEquals("Quiet period should be default.", j.jenkins.getQuietPeriod(), p.getQuietPeriod());
        j.jenkins.setQuietPeriod(0);
        Assert.assertEquals("Quiet period is not set so it should be the same as global quiet period.", 0, p.getQuietPeriod());
        p.setQuietPeriod(10);
        Assert.assertEquals("Quiet period was set.", p.getQuietPeriod(), 10);
    }

    @Test
    public void testGetScmCheckoutStrategy() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.setScmCheckoutStrategy(null);
        Assert.assertTrue("Project should return default checkout strategy if scm checkout strategy is not set.", ((p.getScmCheckoutStrategy()) instanceof DefaultSCMCheckoutStrategyImpl));
        SCMCheckoutStrategy strategy = new ProjectTest.SCMCheckoutStrategyImpl();
        p.setScmCheckoutStrategy(strategy);
        Assert.assertEquals("Project should return its scm checkout strategy if this strategy is not null", strategy, p.getScmCheckoutStrategy());
    }

    @Test
    public void testGetScmCheckoutRetryCount() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Assert.assertEquals("Scm retry count should be default.", j.jenkins.getScmCheckoutRetryCount(), p.getScmCheckoutRetryCount());
        j.jenkins.setScmCheckoutRetryCount(6);
        Assert.assertEquals("Scm retry count should be the same as global scm retry count.", 6, p.getScmCheckoutRetryCount());
        HtmlForm form = j.createWebClient().goTo(((p.getUrl()) + "/configure")).getFormByName("config");
        click();
        // required due to the new default behavior of click
        form.getInputByName("hasCustomScmCheckoutRetryCount").click(new Event(), true);
        form.getInputByName("scmCheckoutRetryCount").setValueAttribute("7");
        j.submit(form);
        Assert.assertEquals("Scm retry count was set.", 7, p.getScmCheckoutRetryCount());
    }

    @Test
    public void isBuildable() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Assert.assertTrue("Project should be buildable.", p.isBuildable());
        p.disable();
        Assert.assertFalse("Project should not be buildable if it is disabled.", p.isBuildable());
        p.enable();
        AbstractProject p2 = ((AbstractProject) (j.jenkins.copy(j.jenkins.getItem("project"), "project2")));
        Assert.assertFalse("Project should not be buildable until is saved.", p2.isBuildable());
        p2.save();
        Assert.assertTrue("Project should be buildable after save.", p2.isBuildable());
    }

    @Test
    public void testMakeDisabled() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.makeDisabled(false);
        Assert.assertFalse("Project should be enabled.", p.isDisabled());
        p.makeDisabled(true);
        Assert.assertTrue("Project should be disabled.", p.isDisabled());
        p.makeDisabled(false);
        p.setAssignedLabel(j.jenkins.getLabel("nonExist"));
        p.scheduleBuild2(0);
        p.makeDisabled(true);
        Assert.assertNull("Project should be canceled.", Queue.getInstance().getItem(p));
    }

    @Test
    public void testAddProperty() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        JobProperty prop = new ProjectTest.JobPropertyImp();
        ProjectTest.createAction = true;
        p.addProperty(prop);
        Assert.assertNotNull("Project does not contain added property.", p.getProperty(prop.getClass()));
        Assert.assertNotNull("Project did not update transient actions.", p.getAction(ProjectTest.TransientAction.class));
    }

    @Test
    public void testScheduleBuild2() throws IOException, InterruptedException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.setAssignedLabel(j.jenkins.getLabel("nonExist"));
        p.scheduleBuild(0, new LegacyCodeCause(), new Action[0]);
        Assert.assertNotNull("Project should be in queue.", Queue.getInstance().getItem(p));
        p.setAssignedLabel(null);
        int count = 0;
        while ((count < 5) && ((p.getLastBuild()) == null)) {
            Thread.sleep(1000);// give some time to start build

            count++;
        } 
        Assert.assertNotNull("Build should be done or in progress.", p.getLastBuild());
    }

    @Test
    public void testSchedulePolling() throws ANTLRException, IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Assert.assertFalse("Project should not schedule polling because no scm trigger is set.", p.schedulePolling());
        SCMTrigger trigger = new SCMTrigger("0 0 * * *");
        p.addTrigger(trigger);
        trigger.start(p, true);
        Assert.assertTrue("Project should schedule polling.", p.schedulePolling());
        p.disable();
        Assert.assertFalse("Project should not schedule polling because project is disabled.", p.schedulePolling());
    }

    @Test
    public void testSaveAfterSet() throws Exception, ReactorException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.setScm(new NullSCM());
        p.setScmCheckoutStrategy(new ProjectTest.SCMCheckoutStrategyImpl());
        p.setQuietPeriod(15);
        p.setBlockBuildWhenDownstreamBuilding(true);
        p.setBlockBuildWhenUpstreamBuilding(true);
        j.jenkins.getJDKs().add(new JDK("jdk", "path"));
        j.jenkins.save();
        p.setJDK(j.jenkins.getJDK("jdk"));
        p.setCustomWorkspace("/some/path");
        j.jenkins.reload();
        Assert.assertNotNull("Project did not save scm.", p.getScm());
        Assert.assertTrue("Project did not save scm checkout strategy.", ((p.getScmCheckoutStrategy()) instanceof ProjectTest.SCMCheckoutStrategyImpl));
        Assert.assertEquals("Project did not save quiet period.", 15, p.getQuietPeriod());
        Assert.assertTrue("Project did not save block if downstream is building.", p.blockBuildWhenDownstreamBuilding());
        Assert.assertTrue("Project did not save block if upstream is building.", p.blockBuildWhenUpstreamBuilding());
        Assert.assertNotNull("Project did not save jdk", p.getJDK());
        Assert.assertEquals("Project did not save custom workspace.", "/some/path", p.getCustomWorkspace());
    }

    @Test
    public void testGetActions() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        ProjectTest.createAction = true;
        p.updateTransientActions();
        Assert.assertNotNull("Action should contain transient actions too.", p.getAction(ProjectTest.TransientAction.class));
        ProjectTest.createAction = false;
    }

    // for debugging
    // static {
    // Logger.getLogger("").getHandlers()[0].setFormatter(new MilliSecLogFormatter());
    // }
    @Test
    public void testGetCauseOfBlockage() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.getBuildersList().add((Functions.isWindows() ? new BatchFile("ping -n 10 127.0.0.1 >nul") : new Shell("sleep 10")));
        QueueTaskFuture<FreeStyleBuild> b1 = waitForStart(p);
        assertInstanceOf(("Build can not start because previous build has not finished: " + (p.getCauseOfBlockage())), p.getCauseOfBlockage(), BlockedBecauseOfBuildInProgress.class);
        p.getLastBuild().getExecutor().interrupt();
        b1.get();// wait for it to finish

        FreeStyleProject downstream = j.createFreeStyleProject("project-downstream");
        downstream.getBuildersList().add((Functions.isWindows() ? new BatchFile("ping -n 10 127.0.0.1 >nul") : new Shell("sleep 10")));
        p.getPublishersList().add(new BuildTrigger(Collections.singleton(downstream), SUCCESS));
        Jenkins.getInstance().rebuildDependencyGraph();
        p.setBlockBuildWhenDownstreamBuilding(true);
        QueueTaskFuture<FreeStyleBuild> b2 = waitForStart(downstream);
        assertInstanceOf("Build can not start because build of downstream project has not finished.", p.getCauseOfBlockage(), BecauseOfDownstreamBuildInProgress.class);
        downstream.getLastBuild().getExecutor().interrupt();
        b2.get();
        downstream.setBlockBuildWhenUpstreamBuilding(true);
        waitForStart(p);
        assertInstanceOf("Build can not start because build of upstream project has not finished.", downstream.getCauseOfBlockage(), BecauseOfUpstreamBuildInProgress.class);
    }

    private static final Logger LOGGER = Logger.getLogger(ProjectTest.class.getName());

    @Test
    public void testGetSubTasks() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        p.addProperty(new ProjectTest.JobPropertyImp());
        ProjectTest.createSubTask = true;
        List<SubTask> subtasks = p.getSubTasks();
        boolean containsSubTaskImpl = false;
        boolean containsSubTaskImpl2 = false;
        for (SubTask sub : subtasks) {
            if (sub instanceof ProjectTest.SubTaskImpl)
                containsSubTaskImpl = true;

            if (sub instanceof ProjectTest.SubTaskImpl2)
                containsSubTaskImpl2 = true;

        }
        ProjectTest.createSubTask = false;
        Assert.assertTrue("Project should return subtasks provided by SubTaskContributor.", containsSubTaskImpl2);
        Assert.assertTrue("Project should return subtasks provided by JobProperty.", containsSubTaskImpl);
    }

    @Test
    public void testCreateExecutable() throws IOException {
        FreeStyleProject p = j.createFreeStyleProject("project");
        Build build = p.createExecutable();
        Assert.assertNotNull("Project should create executable.", build);
        Assert.assertEquals("CreatedExecutable should be the last build.", build, p.getLastBuild());
        Assert.assertEquals("Next build number should be increased.", 2, p.nextBuildNumber);
        p.disable();
        build = p.createExecutable();
        Assert.assertNull("Disabled project should not create executable.", build);
        Assert.assertEquals("Next build number should not be increased.", 2, p.nextBuildNumber);
    }

    @Test
    public void testCheckout() throws IOException, Exception {
        SCM scm = new NullSCM();
        FreeStyleProject p = j.createFreeStyleProject("project");
        Slave slave = j.createOnlineSlave();
        AbstractBuild build = p.createExecutable();
        FilePath ws = slave.getWorkspaceFor(p);
        Assert.assertNotNull(ws);
        FilePath path = slave.toComputer().getWorkspaceList().allocate(ws, build).path;
        build.setWorkspace(path);
        BuildListener listener = new StreamBuildListener(BuildListener.NULL.getLogger(), Charset.defaultCharset());
        Assert.assertTrue("Project with null smc should perform checkout without problems.", p.checkout(build, new hudson.Launcher.RemoteLauncher(listener, slave.getChannel(), true), listener, new File(build.getRootDir(), "changelog.xml")));
        p.setScm(scm);
        Assert.assertTrue("Project should perform checkout without problems.", p.checkout(build, new hudson.Launcher.RemoteLauncher(listener, slave.getChannel(), true), listener, new File(build.getRootDir(), "changelog.xml")));
    }

    @Test
    public void testHasParticipant() throws Exception {
        User user = User.get("John Smith", true, Collections.emptyMap());
        FreeStyleProject project = j.createFreeStyleProject("project");
        FreeStyleProject project2 = j.createFreeStyleProject("project2");
        FakeChangeLogSCM scm = new FakeChangeLogSCM();
        project2.setScm(scm);
        j.buildAndAssertSuccess(project2);
        Assert.assertFalse("Project should not have any participant.", project2.hasParticipant(user));
        scm.addChange().withAuthor(user.getId());
        project.setScm(scm);
        j.buildAndAssertSuccess(project);
        Assert.assertTrue("Project should have participant.", project.hasParticipant(user));
    }

    @Test
    public void testGetRelationship() throws Exception {
        final FreeStyleProject upstream = j.createFreeStyleProject("upstream");
        FreeStyleProject downstream = j.createFreeStyleProject("downstream");
        j.buildAndAssertSuccess(upstream);
        j.buildAndAssertSuccess(upstream);
        j.buildAndAssertSuccess(downstream);
        Assert.assertTrue("Project upstream should not have any relationship with downstream", upstream.getRelationship(downstream).isEmpty());
        upstream.getPublishersList().add(new Fingerprinter("change.log", true));
        upstream.getBuildersList().add(new WorkspaceWriter("change.log", "hello"));
        upstream.getPublishersList().add(new ArtifactArchiver("change.log"));
        downstream.getPublishersList().add(new Fingerprinter("change.log", false));
        downstream.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                for (Run<?, ?>.Artifact a : upstream.getLastBuild().getArtifacts()) {
                    Util.copyFile(a.getFile(), new File(build.getWorkspace().child(a.getFileName()).getRemote()));
                }
                return true;
            }
        });
        j.buildAndAssertSuccess(upstream);
        j.buildAndAssertSuccess(downstream);
        j.buildAndAssertSuccess(upstream);
        j.buildAndAssertSuccess(downstream);
        upstream.getBuildersList().add(new WorkspaceWriter("change.log", "helloWorld"));
        j.buildAndAssertSuccess(upstream);
        j.buildAndAssertSuccess(downstream);
        Map<Integer, Fingerprint.RangeSet> relationship = upstream.getRelationship(downstream);
        Assert.assertFalse("Project upstream should have relationship with downstream", relationship.isEmpty());
        Assert.assertTrue("Relationship should contain upstream #3", relationship.keySet().contains(3));
        Assert.assertFalse("Relationship should not contain upstream #4 because previous fingerprinted file was not changed since #3", relationship.keySet().contains(4));
        Assert.assertEquals("downstream #2 should be the first build which depends on upstream #3", 2, relationship.get(3).min());
        Assert.assertEquals("downstream #3 should be the last build which depends on upstream #3", 3, ((relationship.get(3).max()) - 1));
        Assert.assertEquals("downstream #4 should depend only on upstream #5", 4, relationship.get(5).min());
        Assert.assertEquals("downstream #4 should depend only on upstream #5", 4, ((relationship.get(5).max()) - 1));
    }

    @Test
    public void testDoCancelQueue() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        try (ACLContext as = ACL.as(user)) {
            project.doCancelQueue(null, null);
            Assert.fail("User should not have permission to build project");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
    }

    @Test
    public void testDoDoDelete() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        User user = User.getById("john", true);
        try (ACLContext as = ACL.as(user)) {
            project.doDoDelete(null, null);
            Assert.fail("User should not have permission to build project");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
        auth.add(READ, user.getId());
        auth.add(Job.READ, user.getId());
        auth.add(DELETE, user.getId());
        // use Basic to speedup the test, normally it's pure UI testing
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.withBasicCredentials(user.getId());
        HtmlPage p = wc.goTo(((project.getUrl()) + "delete"));
        List<HtmlForm> forms = p.getForms();
        for (HtmlForm form : forms) {
            if ("doDelete".equals(form.getAttribute("action"))) {
                j.submit(form);
            }
        }
        Assert.assertNull("Project should be deleted form memory.", j.jenkins.getItem(project.getDisplayName()));
        Assert.assertFalse("Project should be deleted form disk.", project.getRootDir().exists());
    }

    @Test
    public void testDoDoWipeOutWorkspace() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        try (ACLContext as = ACL.as(user)) {
            project.doDoWipeOutWorkspace();
            Assert.fail("User should not have permission to build project");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
        auth.add(Job.READ, user.getId());
        auth.add(BUILD, user.getId());
        auth.add(WIPEOUT, user.getId());
        auth.add(READ, user.getId());
        Slave slave = j.createOnlineSlave();
        project.setAssignedLabel(slave.getSelfLabel());
        String cmd = "echo hello > change.log";
        project.getBuildersList().add((Functions.isWindows() ? new BatchFile(cmd) : new Shell(cmd)));
        j.buildAndAssertSuccess(project);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.withBasicCredentials(user.getId(), "password");
        WebRequest request = new WebRequest(new URL((((wc.getContextPath()) + (project.getUrl())) + "doWipeOutWorkspace")), HttpMethod.POST);
        HtmlPage p = wc.getPage(request);
        Assert.assertEquals(p.getWebResponse().getStatusCode(), 200);
        Thread.sleep(500);
        Assert.assertFalse("Workspace should not exist.", project.getSomeWorkspace().exists());
    }

    @Test
    public void testDoDisable() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        try (ACLContext as = ACL.as(user)) {
            project.doDisable();
            Assert.fail("User should not have permission to build project");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
        auth.add(Job.READ, user.getId());
        auth.add(CONFIGURE, user.getId());
        auth.add(READ, user.getId());
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.withBasicCredentials(user.getId(), "password");
        HtmlPage p = wc.goTo(project.getUrl());
        List<HtmlForm> forms = p.getForms();
        for (HtmlForm form : forms) {
            if ("disable".equals(form.getAttribute("action"))) {
                j.submit(form);
            }
        }
        Assert.assertTrue("Project should be disabled.", project.isDisabled());
    }

    @Test
    public void testDoEnable() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("project");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "password");
        try (ACLContext as = ACL.as(user)) {
            project.disable();
        }
        try (ACLContext as = ACL.as(user)) {
            project.doEnable();
            Assert.fail("User should not have permission to build project");
        } catch (Exception e) {
            if (!(e.getClass().isAssignableFrom(AccessDeniedException2.class))) {
                Assert.fail("AccessDeniedException should be thrown.");
            }
        }
        auth.add(Job.READ, user.getId());
        auth.add(CONFIGURE, user.getId());
        auth.add(READ, user.getId());
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.withBasicCredentials(user.getId(), "password");
        HtmlPage p = wc.goTo(project.getUrl());
        List<HtmlForm> forms = p.getForms();
        for (HtmlForm form : forms) {
            if ("enable".equals(form.getAttribute("action"))) {
                j.submit(form);
            }
        }
        Assert.assertFalse("Project should be enabled.", project.isDisabled());
    }

    /**
     * Job is un-restricted (no nabel), this is submitted to queue, which spawns an on demand slave
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testJobSubmittedShouldSpawnCloud() throws Exception {
        /**
         * Setup a project with an SCM. Jenkins should have no executors in itself.
         */
        FreeStyleProject proj = j.createFreeStyleProject("JENKINS-21394-spawn");
        ProjectTest.RequiresWorkspaceSCM requiresWorkspaceScm = new ProjectTest.RequiresWorkspaceSCM(true);
        proj.setScm(requiresWorkspaceScm);
        j.jenkins.setNumExecutors(0);
        /* We have a cloud */
        ProjectTest.DummyCloudImpl2 c2 = new ProjectTest.DummyCloudImpl2(j, 0);
        c2.label = new LabelAtom("test-cloud-label");
        j.jenkins.clouds.add(c2);
        SCMTrigger t = new SCMTrigger("@daily", true);
        t.start(proj, true);
        proj.addTrigger(t);
        t.new Runner().run();
        Thread.sleep(1000);
        // Assert that the job IS submitted to Queue.
        Assert.assertEquals(1, j.jenkins.getQueue().getItems().length);
    }

    /**
     * Job is restricted, but label can not be provided by any cloud, only normal agents. Then job will not submit, because no slave is available.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUnrestrictedJobNoLabelByCloudNoQueue() throws Exception {
        Assert.assertTrue(j.jenkins.clouds.isEmpty());
        // Create slave. (Online)
        Slave s1 = j.createOnlineSlave();
        // Create a project, and bind the job to the created slave
        FreeStyleProject proj = j.createFreeStyleProject("JENKINS-21394-noqueue");
        proj.setAssignedLabel(s1.getSelfLabel());
        // Add an SCM to the project. We require a workspace for the poll
        ProjectTest.RequiresWorkspaceSCM requiresWorkspaceScm = new ProjectTest.RequiresWorkspaceSCM(true);
        proj.setScm(requiresWorkspaceScm);
        j.buildAndAssertSuccess(proj);
        // Now create another slave. And restrict the job to that slave. The slave is offline, leaving the job with no assignable nodes.
        // We tell our mock SCM to return that it has got changes. But since there are no agents, we get the desired result.
        Slave s2 = j.createSlave();
        proj.setAssignedLabel(s2.getSelfLabel());
        requiresWorkspaceScm.hasChange = true;
        // Poll (We now should have NO online agents, this should now return NO_CHANGES.
        PollingResult pr = proj.poll(j.createTaskListener());
        Assert.assertFalse(pr.hasChanges());
        SCMTrigger t = new SCMTrigger("@daily", true);
        t.start(proj, true);
        proj.addTrigger(t);
        t.new Runner().run();
        /**
         * Assert that the log contains the correct message.
         */
        HtmlPage log = j.createWebClient().getPage(proj, "scmPollLog");
        String logastext = log.asText();
        Assert.assertTrue(logastext.contains((("(" + (all_suitable_nodes_are_offline.name())) + ")")));
    }

    /**
     * Job is restricted. Label is on slave that can be started in cloud. Job is submitted to queue, which spawns an on demand slave.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRestrictedLabelOnSlaveYesQueue() throws Exception {
        FreeStyleProject proj = j.createFreeStyleProject("JENKINS-21394-yesqueue");
        ProjectTest.RequiresWorkspaceSCM requiresWorkspaceScm = new ProjectTest.RequiresWorkspaceSCM(true);
        proj.setScm(requiresWorkspaceScm);
        j.jenkins.setNumExecutors(0);
        /* We have a cloud */
        ProjectTest.DummyCloudImpl2 c2 = new ProjectTest.DummyCloudImpl2(j, 0);
        c2.label = new LabelAtom("test-cloud-label");
        j.jenkins.clouds.add(c2);
        proj.setAssignedLabel(c2.label);
        SCMTrigger t = new SCMTrigger("@daily", true);
        t.start(proj, true);
        proj.addTrigger(t);
        t.new Runner().run();
        Thread.sleep(1000);
        // The job should be in queue
        Assert.assertEquals(1, j.jenkins.getQueue().getItems().length);
    }

    @Issue("JENKINS-22750")
    @Test
    public void testMasterJobPutInQueue() throws Exception {
        FreeStyleProject proj = j.createFreeStyleProject("JENKINS-21394-yes-master-queue");
        ProjectTest.RequiresWorkspaceSCM requiresWorkspaceScm = new ProjectTest.RequiresWorkspaceSCM(true);
        proj.setAssignedLabel(null);
        proj.setScm(requiresWorkspaceScm);
        j.jenkins.setNumExecutors(1);
        proj.setScm(requiresWorkspaceScm);
        // First build is not important
        j.buildAndAssertSuccess(proj);
        SCMTrigger t = new SCMTrigger("@daily", true);
        t.start(proj, true);
        proj.addTrigger(t);
        t.new Runner().run();
        Assert.assertFalse(j.jenkins.getQueue().isEmpty());
    }

    public static class TransientAction extends InvisibleAction {}

    @TestExtension
    public static class TransientActionFactoryImpl extends TransientProjectActionFactory {
        @Override
        public Collection<? extends Action> createFor(AbstractProject target) {
            List<Action> actions = new ArrayList<Action>();
            if (ProjectTest.createAction)
                actions.add(new ProjectTest.TransientAction());

            return actions;
        }
    }

    @TestExtension
    public static class RequiresWorkspaceSCM extends NullSCM {
        public boolean hasChange = false;

        public RequiresWorkspaceSCM() {
        }

        public RequiresWorkspaceSCM(boolean hasChange) {
            this.hasChange = hasChange;
        }

        @Override
        public boolean pollChanges(AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener) throws IOException, InterruptedException {
            return hasChange;
        }

        @Override
        public boolean requiresWorkspaceForPolling() {
            return true;
        }

        @Override
        public SCMDescriptor<?> getDescriptor() {
            return new SCMDescriptor<SCM>(null) {};
        }

        @Override
        protected PollingResult compareRemoteRevisionWith(AbstractProject project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
            if (!(hasChange)) {
                return PollingResult.NO_CHANGES;
            }
            return PollingResult.SIGNIFICANT;
        }
    }

    @TestExtension
    public static class AlwaysChangedSCM extends NullSCM {
        @Override
        public boolean pollChanges(AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener) throws IOException, InterruptedException {
            return true;
        }

        @Override
        public boolean requiresWorkspaceForPolling() {
            return false;
        }

        @Override
        protected PollingResult compareRemoteRevisionWith(AbstractProject project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
            return PollingResult.SIGNIFICANT;
        }
    }

    @TestExtension
    public static class WorkspaceBrowserImpl extends WorkspaceBrowser {
        @Override
        public hudson.FilePath getWorkspace(Job job) {
            if (ProjectTest.getFilePath)
                return new FilePath(new File("some_file_path"));

            return null;
        }
    }

    public static class SCMCheckoutStrategyImpl extends DefaultSCMCheckoutStrategyImpl implements Serializable {
        public SCMCheckoutStrategyImpl() {
        }
    }

    public static class JobPropertyImp extends JobProperty {
        @Override
        public Collection getSubTasks() {
            ArrayList<SubTask> list = new ArrayList<SubTask>();
            list.add(new ProjectTest.SubTaskImpl());
            return list;
        }
    }

    @TestExtension
    public static class SubTaskContributorImpl extends SubTaskContributor {
        @Override
        public Collection<? extends SubTask> forProject(AbstractProject<?, ?> p) {
            ArrayList<SubTask> list = new ArrayList<SubTask>();
            if (ProjectTest.createSubTask) {
                list.add(new ProjectTest.SubTaskImpl2());
            }
            return list;
        }
    }

    public static class SubTaskImpl2 extends ProjectTest.SubTaskImpl {}

    public static class SubTaskImpl implements SubTask {
        public String projectName;

        @Override
        public Executable createExecutable() throws IOException {
            return null;
        }

        @Override
        public Task getOwnerTask() {
            return ((Task) (Jenkins.getInstance().getItem(projectName)));
        }

        @Override
        public String getDisplayName() {
            return "some task";
        }
    }

    public class ActionImpl extends InvisibleAction {}

    @TestExtension
    public static class DummyCloudImpl2 extends Cloud {
        private final transient JenkinsRule caller;

        /**
         * Configurable delay between the {@link Cloud#provision(Label,int)} and the actual launch of a slave,
         * to emulate a real cloud that takes some time for provisioning a new system.
         *
         * <p>
         * Number of milliseconds.
         */
        private final int delay;

        // stats counter to perform assertions later
        public int numProvisioned;

        /**
         * Only reacts to provisioning for this label.
         */
        public Label label;

        public DummyCloudImpl2() {
            super("test");
            this.delay = 0;
            this.caller = null;
        }

        public DummyCloudImpl2(JenkinsRule caller, int delay) {
            super("test");
            this.caller = caller;
            this.delay = delay;
        }

        @Override
        public Collection<NodeProvisioner.PlannedNode> provision(Label label, int excessWorkload) {
            List<NodeProvisioner.PlannedNode> r = new ArrayList<NodeProvisioner.PlannedNode>();
            // Always provision...even if there is no workload.
            while (excessWorkload >= 0) {
                System.out.println("Provisioning");
                (numProvisioned)++;
                Future<Node> f = threadPoolForRemoting.submit(new ProjectTest.DummyCloudImpl2.Launcher(delay));
                r.add(new NodeProvisioner.PlannedNode((((name) + " #") + (numProvisioned)), f, 1));
                excessWorkload -= 1;
            } 
            return r;
        }

        @Override
        public boolean canProvision(Label label) {
            // This cloud can ALWAYS provision
            return true;
            /* return label==this.label; */
        }

        private final class Launcher implements Callable<Node> {
            private final long time;

            /**
             * This is so that we can find out the status of Callable from the debugger.
             */
            private volatile Computer computer;

            private Launcher(long time) {
                this.time = time;
            }

            @Override
            public hudson.Node call() throws Exception {
                // simulate the delay in provisioning a new slave,
                // since it's normally some async operation.
                Thread.sleep(time);
                System.out.println("launching slave");
                DumbSlave slave = caller.createSlave(label);
                computer = slave.toComputer();
                computer.connect(false).get();
                synchronized(ProjectTest.DummyCloudImpl2.this) {
                    System.out.println((((computer.getName()) + " launch") + (computer.isOnline() ? "ed successfully" : " failed")));
                    System.out.println(computer.getLog());
                }
                return slave;
            }
        }

        @Override
        public Descriptor<Cloud> getDescriptor() {
            throw new UnsupportedOperationException();
        }
    }
}

