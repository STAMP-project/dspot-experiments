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


import Computer.CONFIGURE;
import HttpMethod.POST;
import Jenkins.ADMINISTER;
import Jenkins.ANONYMOUS;
import JenkinsRule.WebClient;
import Node.Mode.EXCLUSIVE;
import Permission.READ;
import Result.FAILURE;
import TagCloud.Entry;
import TaskListener.NULL;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.maven.MavenModuleSet;
import hudson.model.Node.Mode;
import hudson.model.queue.CauseOfBlockage;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.slaves.ComputerListener;
import hudson.slaves.NodeProperty;
import hudson.slaves.OfflineCause;
import hudson.slaves.OfflineCause.ByCLI;
import hudson.slaves.OfflineCause.UserCause;
import hudson.util.TagCloud;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Callable;
import jenkins.model.Jenkins;
import jenkins.security.QueueItemAuthenticatorConfiguration;
import org.acegisecurity.context.SecurityContextHolder;
import org.hamcrest.core.StringEndsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RunLoadCounter;
import org.jvnet.hudson.test.TestExtension;


/**
 *
 *
 * @author Lucie Votypkova
 */
public class NodeTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    public static boolean addDynamicLabel = false;

    public static boolean notTake = false;

    @Test
    public void testSetTemporaryOfflineCause() throws Exception {
        Node node = j.createOnlineSlave();
        FreeStyleProject project = j.createFreeStyleProject();
        project.setAssignedLabel(j.jenkins.getLabel(node.getDisplayName()));
        OfflineCause cause = new ByCLI("message");
        node.setTemporaryOfflineCause(cause);
        for (ComputerListener l : ComputerListener.all()) {
            l.onOnline(node.toComputer(), NULL);
        }
        Assert.assertEquals("Node should have offline cause which was set.", cause, node.toComputer().getOfflineCause());
        OfflineCause cause2 = new ByCLI("another message");
        node.setTemporaryOfflineCause(cause2);
        Assert.assertEquals("Node should have original offline cause after setting another.", cause, node.toComputer().getOfflineCause());
    }

    @Test
    public void testOfflineCause() throws Exception {
        Node node = j.createOnlineSlave();
        Computer computer = node.toComputer();
        OfflineCause.UserCause cause;
        final User someone = User.get("someone@somewhere.com");
        ACL.impersonate(someone.impersonate());
        computer.doToggleOffline("original message");
        cause = ((UserCause) (computer.getOfflineCause()));
        Assert.assertTrue(cause.toString(), cause.toString().matches("^.*?Disconnected by someone@somewhere.com : original message"));
        Assert.assertEquals(someone, cause.getUser());
        final User root = User.get("root@localhost");
        ACL.impersonate(root.impersonate());
        computer.doChangeOfflineCause("new message");
        cause = ((UserCause) (computer.getOfflineCause()));
        Assert.assertTrue(cause.toString(), cause.toString().matches("^.*?Disconnected by root@localhost : new message"));
        Assert.assertEquals(root, cause.getUser());
        computer.doToggleOffline(null);
        Assert.assertNull(computer.getOfflineCause());
    }

    @Test
    public void testOfflineCauseAsAnonymous() throws Exception {
        Node node = j.createOnlineSlave();
        final Computer computer = node.toComputer();
        OfflineCause.UserCause cause;
        try (ACLContext ctxt = ACL.as(ANONYMOUS)) {
            computer.doToggleOffline("original message");
        }
        cause = ((UserCause) (computer.getOfflineCause()));
        Assert.assertThat(cause.toString(), StringEndsWith.endsWith("Disconnected by anonymous : original message"));
        Assert.assertEquals(User.getUnknown(), cause.getUser());
        final User root = User.get("root@localhost");
        try (ACLContext ctxt = ACL.as(root.impersonate())) {
            computer.doChangeOfflineCause("new message");
        }
        cause = ((UserCause) (computer.getOfflineCause()));
        Assert.assertThat(cause.toString(), StringEndsWith.endsWith("Disconnected by root@localhost : new message"));
        Assert.assertEquals(root, cause.getUser());
        computer.doToggleOffline(null);
        Assert.assertNull(computer.getOfflineCause());
    }

    @Test
    public void testGetLabelCloud() throws Exception {
        Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        FreeStyleProject project = j.createFreeStyleProject();
        final Label label = j.jenkins.getLabel("label1");
        project.setAssignedLabel(label);
        label.reset();// Make sure cached value is not used

        TagCloud<LabelAtom> cloud = node.getLabelCloud();
        for (int i = 0; i < (cloud.size()); i++) {
            TagCloud.Entry e = cloud.get(i);
            if (e.item.equals(label)) {
                Assert.assertEquals("Label label1 should have one tied project.", 1, e.weight, 0);
            } else {
                Assert.assertEquals((("Label " + (e.item)) + " should not have any tied project."), 0, e.weight, 0);
            }
        }
    }

    @Test
    public void testGetAssignedLabels() throws Exception {
        Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        LabelAtom notContained = j.jenkins.getLabelAtom("notContained");
        NodeTest.addDynamicLabel = true;
        Assert.assertTrue("Node should have label1.", node.getAssignedLabels().contains(j.jenkins.getLabelAtom("label1")));
        Assert.assertTrue("Node should have label2.", node.getAssignedLabels().contains(j.jenkins.getLabelAtom("label2")));
        Assert.assertTrue("Node should have dynamically added dynamicLabel.", node.getAssignedLabels().contains(j.jenkins.getLabelAtom("dynamicLabel")));
        Assert.assertFalse("Node should not have label notContained.", node.getAssignedLabels().contains(notContained));
        Assert.assertTrue("Node should have self label.", node.getAssignedLabels().contains(node.getSelfLabel()));
    }

    @Test
    public void testCanTake() throws Exception {
        Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        FreeStyleProject project = j.createFreeStyleProject();
        project.setAssignedLabel(j.jenkins.getLabel("label1"));
        FreeStyleProject project2 = j.createFreeStyleProject();
        FreeStyleProject project3 = j.createFreeStyleProject();
        project3.setAssignedLabel(j.jenkins.getLabel("notContained"));
        Queue.BuildableItem item = new Queue.BuildableItem(new hudson.model.Queue.WaitingItem(new GregorianCalendar(), project, new ArrayList<Action>()));
        Queue.BuildableItem item2 = new Queue.BuildableItem(new hudson.model.Queue.WaitingItem(new GregorianCalendar(), project2, new ArrayList<Action>()));
        Queue.BuildableItem item3 = new Queue.BuildableItem(new hudson.model.Queue.WaitingItem(new GregorianCalendar(), project3, new ArrayList<Action>()));
        Assert.assertNull("Node should take project which is assigned to its label.", node.canTake(item));
        Assert.assertNull("Node should take project which is assigned to its label.", node.canTake(item2));
        Assert.assertNotNull("Node should not take project which is not assigned to its label.", node.canTake(item3));
        String message = Messages._Node_LabelMissing(node.getNodeName(), j.jenkins.getLabel("notContained")).toString();
        Assert.assertEquals("Cause of blockage should be missing label.", message, node.canTake(item3).getShortDescription());
        ((Slave) (node)).setMode(EXCLUSIVE);
        Assert.assertNotNull("Node should not take project which has null label because it is in exclusive mode.", node.canTake(item2));
        message = Messages._Node_BecauseNodeIsReserved(node.getNodeName()).toString();
        Assert.assertEquals("Cause of blockage should be reserved label.", message, node.canTake(item2).getShortDescription());
        node.getNodeProperties().add(new NodeTest.NodePropertyImpl());
        NodeTest.notTake = true;
        Assert.assertNotNull("Node should not take project because node property not alow it.", node.canTake(item));
        Assert.assertTrue("Cause of blockage should be bussy label.", ((node.canTake(item)) instanceof CauseOfBlockage.BecauseLabelIsBusy));
        User user = User.get("John");
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        realm.createAccount("John", "");
        NodeTest.notTake = false;
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().add(new org.jvnet.hudson.test.MockQueueItemAuthenticator(Collections.singletonMap(project.getFullName(), user.impersonate())));
        Assert.assertNotNull("Node should not take project because user does not have build permission.", node.canTake(item));
        message = Messages._Node_LackingBuildPermission(item.authenticate().getName(), node.getNodeName()).toString();
        Assert.assertEquals("Cause of blockage should be bussy label.", message, node.canTake(item).getShortDescription());
    }

    @Test
    public void testCreatePath() throws Exception {
        Node node = j.createOnlineSlave();
        Node node2 = j.createSlave();
        String absolutePath = ((Slave) (node)).remoteFS;
        FilePath path = node.createPath(absolutePath);
        Assert.assertNotNull("Path should be created.", path);
        Assert.assertNotNull("Channel should be set.", path.getChannel());
        Assert.assertEquals("Channel should be equals to channel of node.", node.getChannel(), path.getChannel());
        path = node2.createPath(absolutePath);
        Assert.assertNull("Path should be null if slave have channel null.", path);
    }

    @Test
    public void testHasPermission() throws Exception {
        Node node = j.createOnlineSlave();
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        j.jenkins.setCrumbIssuer(null);
        HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false);
        j.jenkins.setSecurityRealm(realm);
        User user = realm.createAccount("John Smith", "abcdef");
        SecurityContextHolder.getContext().setAuthentication(user.impersonate());
        Assert.assertFalse("Current user should not have permission read.", node.hasPermission(READ));
        auth.add(CONFIGURE, user.getId());
        Assert.assertTrue("Current user should have permission CONFIGURE.", user.hasPermission(Permission.CONFIGURE));
        auth.add(ADMINISTER, user.getId());
        Assert.assertTrue("Current user should have permission read, because he has permission administer.", user.hasPermission(READ));
        SecurityContextHolder.getContext().setAuthentication(ANONYMOUS);
        user = User.get("anonymous");
        Assert.assertFalse("Current user should not have permission read, because does not have global permission read and authentication is anonymous.", user.hasPermission(READ));
    }

    @Test
    public void testGetChannel() throws Exception {
        Slave slave = j.createOnlineSlave();
        Node nodeOffline = j.createSlave();
        Node node = new hudson.slaves.DumbSlave("slave2", "description", slave.getRemoteFS(), "1", Mode.NORMAL, "", slave.getLauncher(), slave.getRetentionStrategy(), slave.getNodeProperties());
        Assert.assertNull("Channel of node should be null because node has not assigned computer.", node.getChannel());
        Assert.assertNull("Channel of node should be null because assigned computer is offline.", nodeOffline.getChannel());
        Assert.assertNotNull("Channel of node should not be null.", slave.getChannel());
    }

    @Test
    public void testToComputer() throws Exception {
        Slave slave = j.createOnlineSlave();
        Node node = new hudson.slaves.DumbSlave("slave2", "description", slave.getRemoteFS(), "1", Mode.NORMAL, "", slave.getLauncher(), slave.getRetentionStrategy(), slave.getNodeProperties());
        Assert.assertNull("Slave which is not added into Jenkins list nodes should not have assigned computer.", node.toComputer());
        Assert.assertNotNull("Slave which is added into Jenkins list nodes should have assigned computer.", slave.toComputer());
    }

    /**
     * Verify that the Label#getTiedJobCount does not perform a lazy loading operation.
     */
    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWithJobs() throws Exception {
        final Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        MavenModuleSet mavenProject = j.jenkins.createProject(MavenModuleSet.class, "p");
        mavenProject.setAssignedLabel(j.jenkins.getLabel("label1"));
        RunLoadCounter.prepare(mavenProject);
        j.assertBuildStatus(FAILURE, mavenProject.scheduleBuild2(0).get());
        Integer labelCount = RunLoadCounter.assertMaxLoads(mavenProject, 0, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final Label label = j.jenkins.getLabel("label1");
                label.reset();// Make sure cached value is not used

                return label.getTiedJobCount();
            }
        });
        Assert.assertEquals("Should have only one job tied to label.", 1, labelCount.intValue());
    }

    @Issue("JENKINS-27188")
    @Test
    public void envPropertiesImmutable() throws Exception {
        Slave slave = j.createSlave();
        String propertyKey = "JENKINS-27188";
        EnvVars envVars = slave.getComputer().getEnvironment();
        envVars.put(propertyKey, "huuhaa");
        Assert.assertTrue(envVars.containsKey(propertyKey));
        Assert.assertFalse(slave.getComputer().getEnvironment().containsKey(propertyKey));
        Assert.assertNotSame(slave.getComputer().getEnvironment(), slave.getComputer().getEnvironment());
    }

    /**
     * Create two projects which have the same label and verify that both are accounted for when getting a count
     * of the jobs tied to the current label.
     */
    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelMultipleSlaves() throws Exception {
        final Node node1 = j.createOnlineSlave();
        node1.setLabelString("label1");
        final Node node2 = j.createOnlineSlave();
        node1.setLabelString("label1");
        MavenModuleSet project = j.jenkins.createProject(MavenModuleSet.class, "p1");
        final Label label = j.jenkins.getLabel("label1");
        project.setAssignedLabel(label);
        j.assertBuildStatus(FAILURE, project.scheduleBuild2(0).get());
        MavenModuleSet project2 = j.jenkins.createProject(MavenModuleSet.class, "p2");
        project2.setAssignedLabel(label);
        j.assertBuildStatus(FAILURE, project2.scheduleBuild2(0).get());
        label.reset();// Make sure cached value is not used

        Assert.assertEquals("Two jobs should be tied to this label.", 2, label.getTiedJobCount());
    }

    /**
     * Verify that when a label is removed from a job that the tied job count does not include the removed job.
     */
    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWhenLabelRemoveFromProject() throws Exception {
        final Node node = j.createOnlineSlave();
        node.setLabelString("label1");
        MavenModuleSet project = j.jenkins.createProject(MavenModuleSet.class, "p");
        final Label label = j.jenkins.getLabel("label1");
        project.setAssignedLabel(label);
        j.assertBuildStatus(FAILURE, project.scheduleBuild2(0).get());
        project.setAssignedLabel(null);
        label.reset();// Make sure cached value is not used

        Assert.assertEquals("Label1 should have no tied jobs after the job label was removed.", 0, label.getTiedJobCount());
    }

    /**
     * Create a project with the OR label expression.
     */
    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWithLabelOrExpression() throws Exception {
        Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        FreeStyleProject project = j.createFreeStyleProject();
        project.setAssignedLabel(new LabelExpression.Or(j.jenkins.getLabel("label1"), j.jenkins.getLabel("label2")));
        TagCloud<LabelAtom> cloud = node.getLabelCloud();
        assertThatCloudLabelContains(cloud, "label1", 0);
        assertThatCloudLabelContains(cloud, "label2", 0);
    }

    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWithLabelAndExpression() throws Exception {
        Node node = j.createOnlineSlave();
        node.setLabelString("label1 label2");
        FreeStyleProject project = j.createFreeStyleProject();
        project.setAssignedLabel(new LabelExpression.And(j.jenkins.getLabel("label1"), j.jenkins.getLabel("label2")));
        TagCloud<LabelAtom> cloud = node.getLabelCloud();
        assertThatCloudLabelContains(cloud, "label1", 0);
        assertThatCloudLabelContains(cloud, "label2", 0);
    }

    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWithBothAndOrExpression() throws Exception {
        Node n1 = j.createOnlineSlave();
        Node n2 = j.createOnlineSlave();
        Node n3 = j.createOnlineSlave();
        Node n4 = j.createOnlineSlave();
        n1.setLabelString("label1 label2 label3");
        n2.setLabelString("label1");
        n3.setLabelString("label1 label2");
        n4.setLabelString("label1 label");
        FreeStyleProject p = j.createFreeStyleProject();
        p.setAssignedLabel(LabelExpression.parseExpression("label1 && (label2 || label3)"));
        // Node 1 should not be tied to any labels
        TagCloud<LabelAtom> n1LabelCloud = n1.getLabelCloud();
        assertThatCloudLabelContains(n1LabelCloud, "label1", 0);
        assertThatCloudLabelContains(n1LabelCloud, "label2", 0);
        assertThatCloudLabelContains(n1LabelCloud, "label3", 0);
        // Node 2 should not be tied to any labels
        TagCloud<LabelAtom> n2LabelCloud = n1.getLabelCloud();
        assertThatCloudLabelContains(n2LabelCloud, "label1", 0);
        // Node 3 should not be tied to any labels
        TagCloud<LabelAtom> n3LabelCloud = n1.getLabelCloud();
        assertThatCloudLabelContains(n3LabelCloud, "label1", 0);
        assertThatCloudLabelContains(n3LabelCloud, "label2", 0);
        // Node 4 should not be tied to any labels
        TagCloud<LabelAtom> n4LabelCloud = n1.getLabelCloud();
        assertThatCloudLabelContains(n4LabelCloud, "label1", 0);
    }

    @Issue("JENKINS-26391")
    @Test
    public void testGetAssignedLabelWithSpaceOnly() throws Exception {
        Node n = j.createOnlineSlave();
        n.setLabelString("label1 label2");
        FreeStyleProject p = j.createFreeStyleProject();
        p.setAssignedLabel(j.jenkins.getLabel("label1 label2"));
        TagCloud<LabelAtom> cloud = n.getLabelCloud();
        assertThatCloudLabelDoesNotContain(cloud, "label1 label2", 0);
    }

    @Issue("SECURITY-281")
    @Test
    public void masterComputerConfigDotXml() throws Exception {
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.assertFails("computer/(master)/config.xml", HttpURLConnection.HTTP_BAD_REQUEST);
        WebRequest settings = new WebRequest(wc.createCrumbedUrl("computer/(master)/config.xml"));
        settings.setHttpMethod(POST);
        settings.setRequestBody("<hudson/>");
        wc.setThrowExceptionOnFailingStatusCode(false);
        Page page = wc.getPage(settings);
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, page.getWebResponse().getStatusCode());
    }

    @TestExtension
    public static class LabelFinderImpl extends LabelFinder {
        @Override
        public Collection<LabelAtom> findLabels(Node node) {
            List<LabelAtom> atoms = new ArrayList<LabelAtom>();
            if (NodeTest.addDynamicLabel) {
                atoms.add(Jenkins.getInstance().getLabelAtom("dynamicLabel"));
            }
            return atoms;
        }
    }

    @TestExtension
    public static class NodePropertyImpl extends NodeProperty {
        @Override
        public CauseOfBlockage canTake(Queue.BuildableItem item) {
            if (NodeTest.notTake)
                return new CauseOfBlockage.BecauseLabelIsBusy(item.getAssignedLabel());

            return null;
        }
    }
}

