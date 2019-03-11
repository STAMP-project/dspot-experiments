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
package hudson.model;


import Computer.BUILD;
import Item.DISCOVER;
import Jenkins.ADMINISTER;
import Jenkins.READ;
import JenkinsRule.WebClient;
import Queue.BuildableItem;
import Queue.Item;
import Queue.WaitingItem;
import Run.QUEUE_ID_UNKNOWN;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlFileInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlFormUtil;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import hudson.Functions;
import hudson.Launcher;
import hudson.XmlFile;
import hudson.matrix.Axis;
import hudson.matrix.LabelAxis;
import hudson.matrix.MatrixBuild;
import hudson.matrix.MatrixProject;
import hudson.matrix.MatrixRun;
import hudson.matrix.TextAxis;
import hudson.model.Cause.RemoteCause;
import hudson.model.Cause.UserIdCause;
import hudson.model.Queue.BlockedItem;
import hudson.model.Queue.Executable;
import hudson.model.labels.LabelExpression;
import hudson.model.listeners.SaveableListener;
import hudson.model.queue.CauseOfBlockage;
import hudson.model.queue.QueueTaskDispatcher;
import hudson.model.queue.QueueTaskFuture;
import hudson.model.queue.ScheduleResult;
import hudson.model.queue.SubTask;
import hudson.security.ACL;
import hudson.security.AuthorizationMatrixProperty;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.Permission;
import hudson.security.ProjectMatrixAuthorizationStrategy;
import hudson.security.SparseACL;
import hudson.slaves.DumbSlave;
import hudson.slaves.DummyCloudImpl;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.NodeProvisionerRule;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import hudson.triggers.SCMTrigger.SCMTriggerCause;
import hudson.triggers.TimerTrigger.TimerTriggerCause;
import hudson.util.OneShotEvent;
import hudson.util.XStream2;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jenkins.model.Jenkins;
import jenkins.security.QueueItemAuthenticatorConfiguration;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.acls.sid.PrincipalSid;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.SequenceLock;
import org.jvnet.hudson.test.SleepBuilder;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class QueueTest {
    @Rule
    public JenkinsRule r = new NodeProvisionerRule((-1), 0, 10);

    @Rule
    public LoggerRule logging = new LoggerRule().record(Queue.class, Level.FINE);

    /**
     * Checks the persistence of queue.
     */
    @Test
    public void persistence() throws Exception {
        Queue q = r.jenkins.getQueue();
        // prevent execution to push stuff into the queue
        r.jenkins.setNumExecutors(0);
        FreeStyleProject testProject = r.createFreeStyleProject("test");
        testProject.scheduleBuild(new UserIdCause());
        q.save();
        System.out.println(FileUtils.readFileToString(new File(r.jenkins.getRootDir(), "queue.xml")));
        Assert.assertEquals(1, q.getItems().length);
        q.clear();
        Assert.assertEquals(0, q.getItems().length);
        // load the contents back
        q.load();
        Assert.assertEquals(1, q.getItems().length);
        // did it bind back to the same object?
        Assert.assertSame(q.getItems()[0].task, testProject);
    }

    /**
     * Make sure the queue can be reconstructed from a List queue.xml.
     * Prior to the Queue.State class, the Queue items were just persisted as a List.
     */
    @LocalData
    @Test
    public void recover_from_legacy_list() throws Exception {
        Queue q = r.jenkins.getQueue();
        // loaded the legacy queue.xml from test LocalData located in
        // resources/hudson/model/QueueTest/recover_from_legacy_list.zip
        Assert.assertEquals(1, q.getItems().length);
        // The current counter should be the id from the item brought back
        // from the persisted queue.xml.
        Assert.assertEquals(3, WaitingItem.getCurrentCounterValue());
    }

    /**
     * Can {@link Queue} successfully recover removal?
     */
    @Test
    public void persistence2() throws Exception {
        Queue q = r.jenkins.getQueue();
        resetQueueState();
        Assert.assertEquals(0, WaitingItem.getCurrentCounterValue());
        // prevent execution to push stuff into the queue
        r.jenkins.setNumExecutors(0);
        FreeStyleProject testProject = r.createFreeStyleProject("test");
        testProject.scheduleBuild(new UserIdCause());
        q.save();
        System.out.println(FileUtils.readFileToString(new File(r.jenkins.getRootDir(), "queue.xml")));
        Assert.assertEquals(1, q.getItems().length);
        q.clear();
        Assert.assertEquals(0, q.getItems().length);
        // delete the project before loading the queue back
        testProject.delete();
        q.load();
        Assert.assertEquals(0, q.getItems().length);
        // The counter state should be maintained.
        Assert.assertEquals(1, WaitingItem.getCurrentCounterValue());
    }

    @Test
    public void queue_id_to_run_mapping() throws Exception {
        FreeStyleProject testProject = r.createFreeStyleProject("test");
        FreeStyleBuild build = r.assertBuildStatusSuccess(testProject.scheduleBuild2(0));
        Assert.assertNotEquals(QUEUE_ID_UNKNOWN, build.getQueueId());
    }

    /**
     * {@link hudson.model.Queue.BlockedItem} is not static. Make sure its persistence doesn't end up re-persisting the whole Queue instance.
     */
    @Test
    public void persistenceBlockedItem() throws Exception {
        Queue q = r.jenkins.getQueue();
        final SequenceLock seq = new SequenceLock();
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                seq.phase(0);// first, we let one build going

                seq.phase(2);
                return true;
            }
        });
        Future<FreeStyleBuild> b1 = p.scheduleBuild2(0);
        seq.phase(1);// and make sure we have one build under way

        // get another going
        Future<FreeStyleBuild> b2 = p.scheduleBuild2(0);
        q.scheduleMaintenance().get();
        Queue[] items = q.getItems();
        Assert.assertEquals(1, items.length);
        Assert.assertTrue(("Got " + (items[0])), ((items[0]) instanceof BlockedItem));
        q.save();
    }

    public static final class FileItemPersistenceTestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("text/html");
            resp.getWriter().println(("<html><body><form action='/' method=post name=main enctype='multipart/form-data'>" + ("<input type=file name=test><input type=submit>" + "</form></body></html>")));
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            try {
                ServletFileUpload f = new ServletFileUpload(new DiskFileItemFactory());
                List<?> v = f.parseRequest(req);
                Assert.assertEquals(1, v.size());
                XStream2 xs = new XStream2();
                System.out.println(xs.toXML(v.get(0)));
            } catch (FileUploadException e) {
                throw new ServletException(e);
            }
        }
    }

    @Test
    public void fileItemPersistence() throws Exception {
        // TODO: write a synchronous connector?
        byte[] testData = new byte[1024];
        for (int i = 0; i < (testData.length); i++)
            testData[i] = ((byte) (i));

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        server.addConnector(connector);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new QueueTest.FileItemPersistenceTestServlet()), "/");
        server.setHandler(handler);
        server.start();
        try {
            JenkinsRule.WebClient wc = r.createWebClient();
            @SuppressWarnings("deprecation")
            HtmlPage p = ((HtmlPage) (wc.getPage((("http://localhost:" + (connector.getLocalPort())) + '/'))));
            HtmlForm f = p.getFormByName("main");
            HtmlFileInput input = ((HtmlFileInput) (f.getInputByName("test")));
            input.setData(testData);
            HtmlFormUtil.submit(f);
        } finally {
            server.stop();
        }
    }

    @Issue("JENKINS-33467")
    @Test
    public void foldableCauseAction() throws Exception {
        final OneShotEvent buildStarted = new OneShotEvent();
        final OneShotEvent buildShouldComplete = new OneShotEvent();
        r.setQuietPeriod(0);
        FreeStyleProject project = r.createFreeStyleProject();
        // Make build sleep a while so it blocks new builds
        project.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                buildStarted.signal();
                buildShouldComplete.block();
                return true;
            }
        });
        // Start one build to block others
        Assert.assertTrue(project.scheduleBuild(new UserIdCause()));
        buildStarted.block();// wait for the build to really start

        // Schedule a new build, and trigger it many ways while it sits in queue
        Future<FreeStyleBuild> fb = project.scheduleBuild2(0, new UserIdCause());
        Assert.assertNotNull(fb);
        Assert.assertTrue(project.scheduleBuild(new SCMTriggerCause("")));
        Assert.assertTrue(project.scheduleBuild(new UserIdCause()));
        Assert.assertTrue(project.scheduleBuild(new TimerTriggerCause()));
        Assert.assertTrue(project.scheduleBuild(new RemoteCause("1.2.3.4", "test")));
        Assert.assertTrue(project.scheduleBuild(new RemoteCause("4.3.2.1", "test")));
        Assert.assertTrue(project.scheduleBuild(new SCMTriggerCause("")));
        Assert.assertTrue(project.scheduleBuild(new RemoteCause("1.2.3.4", "test")));
        Assert.assertTrue(project.scheduleBuild(new RemoteCause("1.2.3.4", "foo")));
        Assert.assertTrue(project.scheduleBuild(new SCMTriggerCause("")));
        Assert.assertTrue(project.scheduleBuild(new TimerTriggerCause()));
        // Wait for 2nd build to finish
        buildShouldComplete.signal();
        FreeStyleBuild build = fb.get();
        // Make sure proper folding happened.
        CauseAction ca = build.getAction(CauseAction.class);
        Assert.assertNotNull(ca);
        StringBuilder causes = new StringBuilder();
        for (Cause c : ca.getCauses())
            causes.append(((c.getShortDescription()) + "\n"));

        Assert.assertEquals("Build causes should have all items, even duplicates", ("Started by user SYSTEM\nStarted by user SYSTEM\n" + ((((("Started by an SCM change\nStarted by an SCM change\nStarted by an SCM change\n" + "Started by timer\nStarted by timer\n") + "Started by remote host 1.2.3.4 with note: test\n") + "Started by remote host 1.2.3.4 with note: test\n") + "Started by remote host 4.3.2.1 with note: test\n") + "Started by remote host 1.2.3.4 with note: foo\n")), causes.toString());
        // View for build should group duplicates
        JenkinsRule.WebClient wc = r.createWebClient();
        String nl = System.getProperty("line.separator");
        String buildPage = wc.getPage(build, "").asText().replace(nl, " ");
        Assert.assertTrue(("Build page should combine duplicates and show counts: " + buildPage), buildPage.contains(("Started by user SYSTEM (2 times) " + (((("Started by an SCM change (3 times) " + "Started by timer (2 times) ") + "Started by remote host 1.2.3.4 with note: test (2 times) ") + "Started by remote host 4.3.2.1 with note: test ") + "Started by remote host 1.2.3.4 with note: foo"))));
        System.out.println(new XmlFile(new File(build.getRootDir(), "build.xml")).asString());
    }

    @Issue("JENKINS-8790")
    @Test
    public void flyweightTasks() throws Exception {
        MatrixProject m = r.jenkins.createProject(MatrixProject.class, "p");
        m.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("FOO", "value")));
        if (Functions.isWindows()) {
            m.getBuildersList().add(new BatchFile("ping -n 3 127.0.0.1 >nul"));
        } else {
            m.getBuildersList().add(new Shell("sleep 3"));
        }
        m.setAxes(new hudson.matrix.AxisList(new TextAxis("DoesntMatter", "aaa", "bbb")));
        List<Future<MatrixBuild>> futures = new ArrayList<Future<MatrixBuild>>();
        for (int i = 0; i < 3; i++) {
            futures.add(m.scheduleBuild2(0, new UserIdCause(), new ParametersAction(new StringParameterValue("FOO", ("value" + i)))));
        }
        for (Future<MatrixBuild> f : futures) {
            r.assertBuildStatusSuccess(f);
        }
    }

    @Issue("JENKINS-7291")
    @Test
    public void flyweightTasksWithoutMasterExecutors() throws Exception {
        DummyCloudImpl cloud = new DummyCloudImpl(r, 0);
        cloud.label = r.jenkins.getLabel("remote");
        r.jenkins.clouds.add(cloud);
        r.jenkins.setNumExecutors(0);
        r.jenkins.setNodes(Collections.<Node>emptyList());
        MatrixProject m = r.jenkins.createProject(MatrixProject.class, "p");
        m.setAxes(new hudson.matrix.AxisList(new LabelAxis("label", Arrays.asList("remote"))));
        MatrixBuild build;
        try {
            build = m.scheduleBuild2(0).get(60, TimeUnit.SECONDS);
        } catch (TimeoutException x) {
            throw ((AssertionError) (new AssertionError(r.jenkins.getQueue().getItems().toString()).initCause(x)));
        }
        r.assertBuildStatusSuccess(build);
        Assert.assertEquals("", build.getBuiltOnStr());
        List<MatrixRun> runs = build.getRuns();
        Assert.assertEquals(1, runs.size());
        Assert.assertEquals("slave0", runs.get(0).getBuiltOnStr());
    }

    @Issue("JENKINS-10944")
    @Test
    public void flyweightTasksBlockedByShutdown() throws Exception {
        r.jenkins.doQuietDown(true, 0);
        AtomicInteger cnt = new AtomicInteger();
        QueueTest.TestFlyweightTask task = new QueueTest.TestFlyweightTask(cnt, null);
        Assert.assertTrue(Queue.isBlockedByShutdown(task));
        r.jenkins.getQueue().schedule2(task, 0);
        r.jenkins.getQueue().maintain();
        r.jenkins.doCancelQuietDown();
        Assert.assertFalse(Queue.isBlockedByShutdown(task));
        r.waitUntilNoActivity();
        Assert.assertEquals(1, cnt.get());
        assert (task.exec) instanceof OneOffExecutor : task.exec;
    }

    @Issue("JENKINS-24519")
    @Test
    public void flyweightTasksBlockedBySlave() throws Exception {
        Label label = Label.get("myslave");
        AtomicInteger cnt = new AtomicInteger();
        QueueTest.TestFlyweightTask task = new QueueTest.TestFlyweightTask(cnt, label);
        r.jenkins.getQueue().schedule2(task, 0);
        r.jenkins.getQueue().maintain();
        r.createSlave(label);
        r.waitUntilNoActivity();
        Assert.assertEquals(1, cnt.get());
        assert (task.exec) instanceof OneOffExecutor : task.exec;
    }

    @Issue("JENKINS-41127")
    @Test
    public void flyweightTasksUnwantedConcurrency() throws Exception {
        Label label = r.jenkins.getSelfLabel();
        AtomicInteger cnt = new AtomicInteger();
        QueueTest.TestFlyweightTask task1 = new QueueTest.TestFlyweightTask(cnt, label);
        QueueTest.TestFlyweightTask task2 = new QueueTest.TestFlyweightTask(cnt, label);
        Assert.assertFalse(isConcurrentBuild());
        Assert.assertFalse(isConcurrentBuild());
        // We need to call Queue#maintain without any interleaving Queue modification to reproduce the issue.
        Queue.withLock(() -> {
            r.jenkins.getQueue().schedule2(task1, 0);
            r.jenkins.getQueue().maintain();
            Queue.Item item1 = r.jenkins.getQueue().getItem(task1);
            assertThat(r.jenkins.getQueue().getPendingItems(), contains(item1));
            r.jenkins.getQueue().schedule2(task2, 0);
            r.jenkins.getQueue().maintain();
            Queue.Item item2 = r.jenkins.getQueue().getItem(task2);
            // Before the fix, item1 would no longer be present in the pending items (but would
            // still be assigned to a live executor), and item2 would not be blocked, which would
            // allow the tasks to execute concurrently.
            assertThat(r.jenkins.getQueue().getPendingItems(), contains(item1));
            assertTrue(item2.isBlocked());
        });
    }

    @Issue("JENKINS-27256")
    @Test
    public void inQueueTaskLookupByAPI() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        Label label = Label.get("unknown-slave");
        // Give the project an "unknown-slave" label, forcing it to
        // stay in the queue after we schedule it, allowing us to query it.
        p.setAssignedLabel(label);
        p.scheduleBuild2(0);
        JenkinsRule.WebClient webclient = r.createWebClient();
        XmlPage queueItems = webclient.goToXml("queue/api/xml");
        String queueTaskId = queueItems.getXmlDocument().getElementsByTagName("id").item(0).getTextContent();
        Assert.assertNotNull(queueTaskId);
        XmlPage queueItem = webclient.goToXml((("queue/item/" + queueTaskId) + "/api/xml"));
        Assert.assertNotNull(queueItem);
        String tagName = queueItem.getDocumentElement().getTagName();
        Assert.assertTrue(((tagName.equals("blockedItem")) || (tagName.equals("buildableItem"))));
    }

    @Issue("JENKINS-28926")
    @Test
    public void upstreamDownstreamCycle() throws Exception {
        FreeStyleProject trigger = r.createFreeStyleProject();
        FreeStyleProject chain1 = r.createFreeStyleProject();
        FreeStyleProject chain2a = r.createFreeStyleProject();
        FreeStyleProject chain2b = r.createFreeStyleProject();
        FreeStyleProject chain3 = r.createFreeStyleProject();
        trigger.getPublishersList().add(new hudson.tasks.BuildTrigger(String.format("%s, %s, %s, %s", chain1.getName(), chain2a.getName(), chain2b.getName(), chain3.getName()), true));
        trigger.setQuietPeriod(0);
        chain1.setQuietPeriod(1);
        chain2a.setQuietPeriod(1);
        chain2b.setQuietPeriod(1);
        chain3.setQuietPeriod(1);
        chain1.getPublishersList().add(new hudson.tasks.BuildTrigger(String.format("%s, %s", chain2a.getName(), chain2b.getName()), true));
        chain2a.getPublishersList().add(new hudson.tasks.BuildTrigger(chain3.getName(), true));
        chain2b.getPublishersList().add(new hudson.tasks.BuildTrigger(chain3.getName(), true));
        chain1.setBlockBuildWhenDownstreamBuilding(true);
        chain2a.setBlockBuildWhenDownstreamBuilding(true);
        chain2b.setBlockBuildWhenDownstreamBuilding(true);
        chain3.setBlockBuildWhenUpstreamBuilding(true);
        r.jenkins.rebuildDependencyGraph();
        r.buildAndAssertSuccess(trigger);
        // the trigger should build immediately and schedule the cycle
        r.waitUntilNoActivity();
        final Queue queue = r.getInstance().getQueue();
        Assert.assertThat("The cycle should have been defanged and chain1 executed", queue.getItem(chain1), Matchers.nullValue());
        Assert.assertThat("The cycle should have been defanged and chain2a executed", queue.getItem(chain2a), Matchers.nullValue());
        Assert.assertThat("The cycle should have been defanged and chain2b executed", queue.getItem(chain2b), Matchers.nullValue());
        Assert.assertThat("The cycle should have been defanged and chain3 executed", queue.getItem(chain3), Matchers.nullValue());
    }

    public static class TestFlyweightTask extends QueueTest.TestTask implements Queue.FlyweightTask {
        Executor exec;

        private final Label assignedLabel;

        public TestFlyweightTask(AtomicInteger cnt, Label assignedLabel) {
            super(cnt);
            this.assignedLabel = assignedLabel;
        }

        @Override
        protected void doRun() {
            exec = Executor.currentExecutor();
        }

        @Override
        public Label getAssignedLabel() {
            return assignedLabel;
        }
    }

    @Test
    public void taskEquality() throws Exception {
        AtomicInteger cnt = new AtomicInteger();
        QueueTest.TestTask originalTask = new QueueTest.TestTask(cnt, true);
        ScheduleResult result = r.jenkins.getQueue().schedule2(originalTask, 0);
        Assert.assertTrue(result.isCreated());
        hudson.model.Queue.WaitingItem item = result.getCreateItem();
        Assert.assertFalse(r.jenkins.getQueue().schedule2(new QueueTest.TestTask(cnt), 0).isCreated());
        originalTask.isBlocked = false;
        item.getFuture().get();
        r.waitUntilNoActivity();
        Assert.assertEquals(1, cnt.get());
    }

    static class TestTask implements Queue.Task {
        private final AtomicInteger cnt;

        boolean isBlocked;

        TestTask(AtomicInteger cnt) {
            this(cnt, false);
        }

        TestTask(AtomicInteger cnt, boolean isBlocked) {
            this.cnt = cnt;
            this.isBlocked = isBlocked;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof QueueTest.TestTask) && ((cnt) == (((QueueTest.TestTask) (o)).cnt));
        }

        @Override
        public int hashCode() {
            return cnt.hashCode();
        }

        @Override
        public CauseOfBlockage getCauseOfBlockage() {
            return isBlocked ? CauseOfBlockage.fromMessage(Messages._Queue_Unknown()) : null;
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public String getFullDisplayName() {
            return "Test";
        }

        @Override
        public void checkAbortPermission() {
        }

        @Override
        public boolean hasAbortPermission() {
            return true;
        }

        @Override
        public String getUrl() {
            return "test/";
        }

        @Override
        public String getDisplayName() {
            return "Test";
        }

        @Override
        public ResourceList getResourceList() {
            return new ResourceList();
        }

        protected void doRun() {
        }

        @Override
        public Executable createExecutable() throws IOException {
            return new Executable() {
                @Override
                public SubTask getParent() {
                    return QueueTest.TestTask.this;
                }

                @Override
                public long getEstimatedDuration() {
                    return -1;
                }

                @Override
                public void run() {
                    doRun();
                    cnt.incrementAndGet();
                }
            };
        }
    }

    @Test
    public void waitForStart() throws Exception {
        final OneShotEvent ev = new OneShotEvent();
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                ev.block();
                return true;
            }
        });
        QueueTaskFuture<FreeStyleBuild> v = p.scheduleBuild2(0);
        FreeStyleBuild b = v.waitForStart();
        Assert.assertEquals(1, b.getNumber());
        Assert.assertTrue(b.isBuilding());
        Assert.assertSame(p, b.getProject());
        ev.signal();// let the build complete

        FreeStyleBuild b2 = r.assertBuildStatusSuccess(v);
        Assert.assertSame(b, b2);
    }

    /**
     * Make sure that the running build actually carries an credential.
     */
    @Test
    public void accessControl() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().add(new org.jvnet.hudson.test.MockQueueItemAuthenticator(Collections.singletonMap(p.getFullName(), QueueTest.alice)));
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                Assert.assertEquals(QueueTest.alice, Jenkins.getAuthentication());
                return true;
            }
        });
        r.assertBuildStatusSuccess(p.scheduleBuild2(0));
    }

    private static Authentication alice = new org.acegisecurity.providers.UsernamePasswordAuthenticationToken("alice", "alice", new GrantedAuthority[0]);

    /**
     * Make sure that the slave assignment honors the permissions.
     *
     * We do this test by letting a build run twice to determine its natural home,
     * and then introduce a security restriction to prohibit that.
     */
    @Test
    public void permissionSensitiveSlaveAllocations() throws Exception {
        r.jenkins.setNumExecutors(0);// restrict builds to those agents

        DumbSlave s1 = r.createSlave();
        DumbSlave s2 = r.createSlave();
        FreeStyleProject p = r.createFreeStyleProject();
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().add(new org.jvnet.hudson.test.MockQueueItemAuthenticator(Collections.singletonMap(p.getFullName(), QueueTest.alice)));
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                Assert.assertEquals(QueueTest.alice, Jenkins.getAuthentication());
                return true;
            }
        });
        final FreeStyleBuild b1 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        final FreeStyleBuild b2 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
        // scheduling algorithm would prefer running the same job on the same node
        // kutzi: 'prefer' != 'enforce', therefore disabled this assertion: assertSame(b1.getBuiltOn(),b2.getBuiltOn());
        r.jenkins.setAuthorizationStrategy(new QueueTest.AliceCannotBuild(b1.getBuiltOnStr()));
        // now that we prohibit alice to do a build on the same node, the build should run elsewhere
        for (int i = 0; i < 3; i++) {
            FreeStyleBuild b3 = r.assertBuildStatusSuccess(p.scheduleBuild2(0));
            Assert.assertNotSame(b3.getBuiltOnStr(), b1.getBuiltOnStr());
        }
    }

    private static class AliceCannotBuild extends GlobalMatrixAuthorizationStrategy {
        private final String blocked;

        AliceCannotBuild(String blocked) {
            add(ADMINISTER, "anonymous");
            this.blocked = blocked;
        }

        @Override
        public ACL getACL(Node node) {
            if (node.getNodeName().equals(blocked)) {
                // ACL that allow anyone to do anything except Alice can't build.
                SparseACL acl = new SparseACL(null);
                acl.add(new PrincipalSid(QueueTest.alice), BUILD, false);
                acl.add(new PrincipalSid("anonymous"), ADMINISTER, true);
                return acl;
            }
            return super.getACL(node);
        }
    }

    @Test
    public void pendingsConsistenceAfterErrorDuringMaintain() throws IOException, InterruptedException, ExecutionException {
        FreeStyleProject project1 = r.createFreeStyleProject();
        FreeStyleProject project2 = r.createFreeStyleProject();
        TopLevelItemDescriptor descriptor = new TopLevelItemDescriptor(FreeStyleProject.class) {
            @Override
            public FreeStyleProject newInstance(ItemGroup parent, String name) {
                return ((FreeStyleProject) (new FreeStyleProject(parent, name) {
                    @Override
                    public Label getAssignedLabel() {
                        throw new IllegalArgumentException("Test exception");// cause dead of executor

                    }

                    @Override
                    public void save() {
                        // do not need save
                    }
                }));
            }
        };
        FreeStyleProject projectError = ((FreeStyleProject) (r.jenkins.createProject(descriptor, "throw-error")));
        project1.setAssignedLabel(r.jenkins.getSelfLabel());
        project2.setAssignedLabel(r.jenkins.getSelfLabel());
        project1.getBuildersList().add(new Shell("sleep 2"));
        project1.scheduleBuild2(0);
        QueueTaskFuture<FreeStyleBuild> v = project2.scheduleBuild2(0);
        projectError.scheduleBuild2(0);
        Executor e = r.jenkins.toComputer().getExecutors().get(0);
        Thread.sleep(2000);
        while ((project2.getLastBuild()) == null) {
            if (!(e.isAlive())) {
                break;// executor is dead due to exception

            }
            if (e.isIdle()) {
                Assert.assertTrue((("Node went to idle before project had" + (project2.getDisplayName())) + " been started"), v.isDone());
            }
            Thread.sleep(1000);
        } 
        if ((project2.getLastBuild()) != null)
            return;

        Queue.getInstance().cancel(projectError);// cancel job which cause dead of executor

        while (!(e.isIdle())) {
            // executor should take project2 from queue
            Thread.sleep(1000);
        } 
        // project2 should not be in pendings
        List<Queue.BuildableItem> items = Queue.getInstance().getPendingItems();
        for (Queue.BuildableItem item : items) {
            Assert.assertFalse((("Project " + (project2.getDisplayName())) + " stuck in pendings"), item.task.getName().equals(project2.getName()));
        }
    }

    @Test
    public void cancelInQueue() throws Exception {
        // parepare an offline slave.
        DumbSlave slave = r.createOnlineSlave();
        Assert.assertFalse(slave.toComputer().isOffline());
        slave.toComputer().disconnect(null).get();
        Assert.assertTrue(slave.toComputer().isOffline());
        FreeStyleProject p = r.createFreeStyleProject();
        p.setAssignedNode(slave);
        QueueTaskFuture<FreeStyleBuild> f = p.scheduleBuild2(0);
        try {
            f.get(3, TimeUnit.SECONDS);
            Assert.fail("Should time out (as the slave is offline).");
        } catch (TimeoutException e) {
        }
        Queue.Item item = Queue.getInstance().getItem(p);
        Assert.assertNotNull(item);
        Queue.getInstance().doCancelItem(item.getId());
        Assert.assertNull(Queue.getInstance().getItem(p));
        try {
            f.get(10, TimeUnit.SECONDS);
            Assert.fail("Should not get (as it is cancelled).");
        } catch (CancellationException e) {
        }
    }

    @Test
    public void waitForStartAndCancelBeforeStart() throws Exception {
        final OneShotEvent ev = new OneShotEvent();
        FreeStyleProject p = r.createFreeStyleProject();
        QueueTaskFuture<FreeStyleBuild> f = p.scheduleBuild2(10);
        final Queue.Item item = Queue.getInstance().getItem(p);
        Assert.assertNotNull(item);
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Queue.getInstance().doCancelItem(item.getId());
                } catch (IOException | ServletException e) {
                    printStackTrace();
                }
            }
        }, 2, TimeUnit.SECONDS);
        try {
            f.waitForStart();
            Assert.fail("Expected an CancellationException to be thrown");
        } catch (CancellationException e) {
        }
    }

    /* When a flyweight task is restricted to run on a specific node, the node will be provisioned
    and the flyweight task will be executed.
     */
    @Issue("JENKINS-30084")
    @Test
    public void shouldRunFlyweightTaskOnProvisionedNodeWhenNodeRestricted() throws Exception {
        MatrixProject matrixProject = r.jenkins.createProject(MatrixProject.class, "p");
        matrixProject.setAxes(new hudson.matrix.AxisList(new Axis("axis", "a", "b")));
        Label label = LabelExpression.get("aws-linux-dummy");
        DummyCloudImpl dummyCloud = new DummyCloudImpl(r, 0);
        dummyCloud.label = label;
        r.jenkins.clouds.add(dummyCloud);
        matrixProject.setAssignedLabel(label);
        r.assertBuildStatusSuccess(matrixProject.scheduleBuild2(0));
        Assert.assertEquals("aws-linux-dummy", matrixProject.getBuilds().getLastBuild().getBuiltOn().getLabelString());
    }

    // let's make sure that the downstream project is not started before the upstream --> we want to simulate
    // the case: buildable-->blocked-->buildable
    public static class BlockDownstreamProjectExecution extends NodeProperty<Slave> {
        @Override
        public CauseOfBlockage canTake(Queue.BuildableItem item) {
            if (item.task.getName().equals("downstream")) {
                return new CauseOfBlockage() {
                    @Override
                    public String getShortDescription() {
                        return "slave not provisioned";
                    }
                };
            }
            return null;
        }

        @TestExtension("shouldBeAbleToBlockFlyWeightTaskOnLastMinute")
        public static class DescriptorImpl extends NodePropertyDescriptor {}
    }

    @Issue({ "SECURITY-186", "SECURITY-618" })
    @Test
    public void queueApiOutputShouldBeFilteredByUserPermission() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        ProjectMatrixAuthorizationStrategy str = new ProjectMatrixAuthorizationStrategy();
        str.add(READ, "bob");
        str.add(READ, "alice");
        str.add(READ, "james");
        r.jenkins.setAuthorizationStrategy(str);
        FreeStyleProject project = r.createFreeStyleProject("project");
        Map<Permission, Set<String>> permissions = new HashMap<Permission, Set<String>>();
        permissions.put(Item.READ, Collections.singleton("bob"));
        permissions.put(DISCOVER, Collections.singleton("james"));
        AuthorizationMatrixProperty prop1 = new AuthorizationMatrixProperty(permissions);
        project.addProperty(prop1);
        project.getBuildersList().add(new SleepBuilder(10));
        project.scheduleBuild2(0);
        User alice = User.getById("alice", true);
        User bob = User.getById("bob", true);
        User james = User.getById("james", true);
        JenkinsRule.WebClient webClient = r.createWebClient();
        webClient.withBasicApiToken(bob);
        XmlPage p = webClient.goToXml("queue/api/xml");
        // bob has permission on the project and will be able to see it in the queue together with information such as the URL and the name.
        for (DomNode element : getChildNodes()) {
            if (element.getNodeName().equals("task")) {
                for (DomNode child : getChildNodes()) {
                    if (child.getNodeName().equals("name")) {
                        Assert.assertEquals(child.asText(), "project");
                    } else
                        if (child.getNodeName().equals("url")) {
                            Assert.assertNotNull(child.asText());
                        }

                }
            }
        }
        webClient = r.createWebClient();
        webClient.withBasicApiToken(alice);
        XmlPage p2 = webClient.goToXml("queue/api/xml");
        // alice does not have permission on the project and will not see it in the queue.
        Assert.assertTrue(p2.getByXPath("/queue/node()").isEmpty());
        webClient = r.createWebClient();
        webClient.withBasicApiToken(james);
        XmlPage p3 = webClient.goToXml("queue/api/xml");
        // james has DISCOVER permission on the project and will only be able to see the task name.
        List projects = p3.getByXPath("/queue/discoverableItem/task/name/text()");
        Assert.assertEquals(1, projects.size());
        Assert.assertEquals("project", projects.get(0).toString());
        // Also check individual item exports.
        String url = (project.getQueueItem().getUrl()) + "api/xml";
        r.createWebClient().withBasicApiToken(bob).goToXml(url);// OK, 200

        r.createWebClient().withBasicApiToken(james).assertFails(url, HttpURLConnection.HTTP_FORBIDDEN);// only DISCOVER ? AccessDeniedException

        r.createWebClient().withBasicApiToken(alice).assertFails(url, HttpURLConnection.HTTP_NOT_FOUND);// not even DISCOVER

    }

    // we force the project not to be executed so that it stays in the queue
    @TestExtension("queueApiOutputShouldBeFilteredByUserPermission")
    public static class MyQueueTaskDispatcher extends QueueTaskDispatcher {
        @Override
        public CauseOfBlockage canTake(Node node, Queue.BuildableItem item) {
            return new CauseOfBlockage() {
                @Override
                public String getShortDescription() {
                    return "blocked by canTake";
                }
            };
        }
    }

    @Test
    public void testGetCauseOfBlockageForNonConcurrentFreestyle() throws Exception {
        Queue queue = r.getInstance().getQueue();
        FreeStyleProject t1 = r.createFreeStyleProject("project");
        t1.getBuildersList().add(new SleepBuilder(TimeUnit.SECONDS.toMillis(30)));
        t1.setConcurrentBuild(false);
        t1.scheduleBuild2(0).waitForStart();
        t1.scheduleBuild2(0);
        queue.maintain();
        Assert.assertEquals(1, r.jenkins.getQueue().getBlockedItems().size());
        CauseOfBlockage actual = r.jenkins.getQueue().getBlockedItems().get(0).getCauseOfBlockage();
        CauseOfBlockage expected = new jenkins.model.BlockedBecauseOfBuildInProgress(t1.getFirstBuild());
        Assert.assertEquals(expected.getShortDescription(), actual.getShortDescription());
    }

    @Test
    @LocalData
    public void load_queue_xml() {
        Queue q = r.getInstance().getQueue();
        Queue[] items = q.getItems();
        Assert.assertEquals(Arrays.asList(items).toString(), 11, items.length);
        Assert.assertEquals("Loading the queue should not generate saves", 0, QueueTest.QueueSaveSniffer.count);
    }

    @TestExtension("load_queue_xml")
    public static final class QueueSaveSniffer extends SaveableListener {
        private static int count = 0;

        @Override
        public void onChange(Saveable o, XmlFile file) {
            if (o instanceof Queue) {
                (QueueTest.QueueSaveSniffer.count)++;
            }
        }
    }
}

