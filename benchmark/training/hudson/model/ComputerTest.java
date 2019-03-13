/**
 * The MIT License
 *
 * Copyright (c) 2015 Red Hat, Inc.; Christopher Simons
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


import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import hudson.slaves.DumbSlave;
import java.io.File;
import java.net.HttpURLConnection;
import jenkins.model.Jenkins;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.recipes.LocalData;


@Category(SmokeTest.class)
public class ComputerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void discardLogsAfterDeletion() throws Exception {
        DumbSlave delete = j.createOnlineSlave(Jenkins.getInstance().getLabelAtom("delete"));
        DumbSlave keep = j.createOnlineSlave(Jenkins.getInstance().getLabelAtom("keep"));
        File logFile = delete.toComputer().getLogFile();
        Assert.assertTrue(logFile.exists());
        Jenkins.getInstance().removeNode(delete);
        Assert.assertFalse("Slave log should be deleted", logFile.exists());
        Assert.assertFalse("Slave log directory should be deleted", logFile.getParentFile().exists());
        Assert.assertTrue("Slave log should be kept", keep.toComputer().getLogFile().exists());
    }

    /**
     * Verify we can't rename a node over an existing node.
     */
    @Issue("JENKINS-31321")
    @Test
    public void testProhibitRenameOverExistingNode() throws Exception {
        final String NOTE = "Rename node to name of another node should fail.";
        Node nodeA = j.createSlave("nodeA", null, null);
        Node nodeB = j.createSlave("nodeB", null, null);
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlForm form = wc.getPage(nodeB, "configure").getFormByName("config");
        form.getInputByName("_.name").setValueAttribute("nodeA");
        Page page = j.submit(form);
        Assert.assertEquals(NOTE, HttpURLConnection.HTTP_BAD_REQUEST, page.getWebResponse().getStatusCode());
        MatcherAssert.assertThat(NOTE, page.getWebResponse().getContentAsString(), Matchers.containsString("Agent called ?nodeA? already exists"));
    }

    @Test
    public void doNotShowUserDetailsInOfflineCause() throws Exception {
        DumbSlave slave = j.createOnlineSlave();
        final Computer computer = slave.toComputer();
        computer.setTemporarilyOffline(true, new hudson.slaves.OfflineCause.UserCause(User.get("username"), "msg"));
        verifyOfflineCause(computer);
    }

    @Test
    @LocalData
    public void removeUserDetailsFromOfflineCause() throws Exception {
        Computer computer = j.jenkins.getComputer("deserialized");
        verifyOfflineCause(computer);
    }

    @Issue("JENKINS-42969")
    @Test
    public void addAction() throws Exception {
        Computer c = j.createSlave().toComputer();
        class A extends InvisibleAction {}
        Assert.assertEquals(0, c.getActions(A.class).size());
        c.addAction(new A());
        Assert.assertEquals(1, c.getActions(A.class).size());
        c.addAction(new A());
        Assert.assertEquals(2, c.getActions(A.class).size());
    }
}

