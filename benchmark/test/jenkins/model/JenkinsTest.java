/**
 * The MIT License
 *
 * Copyright (c) 2004-2011, Yahoo!, Inc.
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
package jenkins.model;


import FormValidation.Kind.ERROR;
import FormValidation.Kind.OK;
import FormValidation.Kind.WARNING;
import Jenkins.ADMINISTER;
import Jenkins.ANONYMOUS;
import Jenkins.READ;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.Computer;
import hudson.model.Failure;
import hudson.model.FreeStyleProject;
import hudson.model.InvisibleAction;
import hudson.model.RestartListener;
import hudson.model.RootAction;
import hudson.model.TaskListener;
import hudson.model.UnprotectedRootAction;
import hudson.model.User;
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.slaves.ComputerListener;
import hudson.slaves.DumbSlave;
import hudson.slaves.OfflineCause;
import hudson.util.FormValidation;
import hudson.util.HttpResponses;
import hudson.util.VersionNumber;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import jenkins.AgentProtocol;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.WithPlugin;
import org.kohsuke.stapler.HttpResponse;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Jenkins.VERSION;


/**
 * Tests of the {@link Jenkins} class instance logic.
 *
 * @see Jenkins
 * @see JenkinsRule
 */
@Category(SmokeTest.class)
public class JenkinsTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Issue("SECURITY-406")
    @Test
    public void testUserCreationFromUrlForAdmins() throws Exception {
        WebClient wc = j.createWebClient();
        Assert.assertNull("User not supposed to exist", User.getById("nonexistent", false));
        wc.assertFails("user/nonexistent", 404);
        Assert.assertNull("User not supposed to exist", User.getById("nonexistent", false));
        try {
            User.ALLOW_USER_CREATION_VIA_URL = true;
            // expected to work
            wc.goTo("user/nonexistent2");
            Assert.assertNotNull("User supposed to exist", User.getById("nonexistent2", false));
        } finally {
            User.ALLOW_USER_CREATION_VIA_URL = false;
        }
    }

    @Test
    public void testIsDisplayNameUniqueTrue() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName("currentProjectDisplayName");
        FreeStyleProject p = j.createFreeStyleProject(jobName);
        p.setDisplayName("displayName");
        Jenkins jenkins = Jenkins.getInstance();
        Assert.assertTrue(jenkins.isDisplayNameUnique("displayName1", curJobName));
        Assert.assertTrue(jenkins.isDisplayNameUnique(jobName, curJobName));
    }

    @Test
    public void testIsDisplayNameUniqueFalse() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        final String displayName = "displayName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName("currentProjectDisplayName");
        FreeStyleProject p = j.createFreeStyleProject(jobName);
        p.setDisplayName(displayName);
        Jenkins jenkins = Jenkins.getInstance();
        Assert.assertFalse(jenkins.isDisplayNameUnique(displayName, curJobName));
    }

    @Test
    public void testIsDisplayNameUniqueSameAsCurrentJob() throws Exception {
        final String curJobName = "curJobName";
        final String displayName = "currentProjectDisplayName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName(displayName);
        Jenkins jenkins = Jenkins.getInstance();
        // should be true as we don't test against the current job
        Assert.assertTrue(jenkins.isDisplayNameUnique(displayName, curJobName));
    }

    @Test
    public void testIsNameUniqueTrue() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        j.createFreeStyleProject(curJobName);
        j.createFreeStyleProject(jobName);
        Jenkins jenkins = Jenkins.getInstance();
        Assert.assertTrue(jenkins.isNameUnique("jobName1", curJobName));
    }

    @Test
    public void testIsNameUniqueFalse() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        j.createFreeStyleProject(curJobName);
        j.createFreeStyleProject(jobName);
        Jenkins jenkins = Jenkins.getInstance();
        Assert.assertFalse(jenkins.isNameUnique(jobName, curJobName));
    }

    @Test
    public void testIsNameUniqueSameAsCurrentJob() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        j.createFreeStyleProject(curJobName);
        j.createFreeStyleProject(jobName);
        Jenkins jenkins = Jenkins.getInstance();
        // true because we don't test against the current job
        Assert.assertTrue(jenkins.isNameUnique(curJobName, curJobName));
    }

    @Test
    public void testDoCheckDisplayNameUnique() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName("currentProjectDisplayName");
        FreeStyleProject p = j.createFreeStyleProject(jobName);
        p.setDisplayName("displayName");
        Jenkins jenkins = Jenkins.getInstance();
        FormValidation v = jenkins.doCheckDisplayName("1displayName", curJobName);
        Assert.assertEquals(FormValidation.ok(), v);
    }

    @Test
    public void testDoCheckDisplayNameSameAsDisplayName() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        final String displayName = "displayName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName("currentProjectDisplayName");
        FreeStyleProject p = j.createFreeStyleProject(jobName);
        p.setDisplayName(displayName);
        Jenkins jenkins = Jenkins.getInstance();
        FormValidation v = jenkins.doCheckDisplayName(displayName, curJobName);
        Assert.assertEquals(WARNING, v.kind);
    }

    @Test
    public void testDoCheckDisplayNameSameAsJobName() throws Exception {
        final String curJobName = "curJobName";
        final String jobName = "jobName";
        final String displayName = "displayName";
        FreeStyleProject curProject = j.createFreeStyleProject(curJobName);
        curProject.setDisplayName("currentProjectDisplayName");
        FreeStyleProject p = j.createFreeStyleProject(jobName);
        p.setDisplayName(displayName);
        Jenkins jenkins = Jenkins.getInstance();
        FormValidation v = jenkins.doCheckDisplayName(jobName, curJobName);
        Assert.assertEquals(WARNING, v.kind);
    }

    @Test
    public void testDoCheckViewName_GoodName() throws Exception {
        String[] viewNames = new String[]{ "", "Jenkins" };
        Jenkins jenkins = Jenkins.getInstance();
        for (String viewName : viewNames) {
            FormValidation v = jenkins.doCheckViewName(viewName);
            Assert.assertEquals(OK, v.kind);
        }
    }

    @Test
    public void testDoCheckViewName_NotGoodName() throws Exception {
        String[] viewNames = new String[]{ "Jenkins?", "Jenkins*", "Jenkin/s", "Jenkin\\s", "jenkins%", "Jenkins!", "Jenkins[]", "Jenkin<>s", "^Jenkins", ".." };
        Jenkins jenkins = Jenkins.getInstance();
        for (String viewName : viewNames) {
            FormValidation v = jenkins.doCheckViewName(viewName);
            Assert.assertEquals(ERROR, v.kind);
        }
    }

    /**
     * Makes sure access to "/foobar" for UnprotectedRootAction gets through.
     */
    @Test
    @Issue("JENKINS-14113")
    public void testUnprotectedRootAction() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new FullControlOnceLoggedInAuthorizationStrategy());
        WebClient wc = j.createWebClient();
        wc.goTo("foobar");
        wc.goTo("foobar/");
        wc.goTo("foobar/zot");
        // and make sure this fails
        wc.assertFails("foobar-zot/", HttpURLConnection.HTTP_INTERNAL_ERROR);
        Assert.assertEquals(3, j.jenkins.getExtensionList(RootAction.class).get(JenkinsTest.RootActionImpl.class).count);
    }

    @Test
    public void testDoScript() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("alice").grant(READ).everywhere().to("bob").grantWithoutImplication(ADMINISTER, READ).everywhere().to("charlie"));
        WebClient wc = j.createWebClient();
        wc.withBasicApiToken(User.getById("alice", true));
        wc.goTo("script");
        wc.assertFails("script?script=System.setProperty('hack','me')", HttpURLConnection.HTTP_BAD_METHOD);
        Assert.assertNull(System.getProperty("hack"));
        WebRequest req = new WebRequest(new URL(((wc.getContextPath()) + "script?script=System.setProperty('hack','me')")), HttpMethod.POST);
        wc.getPage(req);
        Assert.assertEquals("me", System.getProperty("hack"));
        wc.assertFails("scriptText?script=System.setProperty('hack','me')", HttpURLConnection.HTTP_BAD_METHOD);
        req = new WebRequest(new URL(((wc.getContextPath()) + "scriptText?script=System.setProperty('huck','you')")), HttpMethod.POST);
        wc.getPage(req);
        Assert.assertEquals("you", System.getProperty("huck"));
        wc.withBasicApiToken(User.getById("bob", true));
        wc.assertFails("script", HttpURLConnection.HTTP_FORBIDDEN);
        wc.withBasicApiToken(User.getById("charlie", true));
        wc.assertFails("script", HttpURLConnection.HTTP_FORBIDDEN);
    }

    @Test
    public void testDoEval() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("alice").grant(READ).everywhere().to("bob").grantWithoutImplication(ADMINISTER, READ).everywhere().to("charlie"));
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false).withBasicApiToken(User.getById("alice", true));
        wc.assertFails("eval", HttpURLConnection.HTTP_BAD_METHOD);
        Assert.assertEquals("3", eval(wc).getWebResponse().getContentAsString());
        wc.withBasicApiToken(User.getById("bob", true));
        Page page = eval(wc);
        Assert.assertEquals("bob has only READ", HttpURLConnection.HTTP_FORBIDDEN, page.getWebResponse().getStatusCode());
        wc.withBasicApiToken(User.getById("charlie", true));
        page = eval(wc);
        Assert.assertEquals("charlie has ADMINISTER but not RUN_SCRIPTS", HttpURLConnection.HTTP_FORBIDDEN, page.getWebResponse().getStatusCode());
    }

    @TestExtension("testUnprotectedRootAction")
    public static class RootActionImpl implements UnprotectedRootAction {
        private int count;

        public String getIconFileName() {
            return null;
        }

        public String getDisplayName() {
            return null;
        }

        public String getUrlName() {
            return "foobar";
        }

        public HttpResponse doDynamic() {
            Assert.assertTrue(Jenkins.getInstance().getAuthentication().getName().equals("anonymous"));
            (count)++;
            return HttpResponses.html("OK");
        }
    }

    @TestExtension("testUnprotectedRootAction")
    public static class ProtectedRootActionImpl extends InvisibleAction implements RootAction {
        @Override
        public String getUrlName() {
            return "foobar-zot";
        }

        public HttpResponse doDynamic() {
            throw new AssertionError();
        }
    }

    @Test
    @Issue("JENKINS-20866")
    public void testErrorPageShouldBeAnonymousAccessible() throws Exception {
        HudsonPrivateSecurityRealm s = new HudsonPrivateSecurityRealm(false, false, null);
        User alice = s.createAccount("alice", "alice");
        j.jenkins.setSecurityRealm(s);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        j.jenkins.setAuthorizationStrategy(auth);
        // no anonymous read access
        Assert.assertTrue((!(Jenkins.get().hasPermission(ANONYMOUS, READ))));
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage p = wc.goTo("error/reportError");
        Assert.assertEquals(p.asText(), HttpURLConnection.HTTP_BAD_REQUEST, p.getWebResponse().getStatusCode());// not 403 forbidden

        Assert.assertTrue(p.getWebResponse().getContentAsString().contains("My car is black"));
    }

    @TestExtension("testErrorPageShouldBeAnonymousAccessible")
    public static class ReportError implements UnprotectedRootAction {
        public String getIconFileName() {
            return null;
        }

        public String getDisplayName() {
            return null;
        }

        public String getUrlName() {
            return "error";
        }

        public HttpResponse doReportError() {
            return new Failure("My car is black");
        }
    }

    @Test
    @Issue("JENKINS-23551")
    public void testComputerListenerNotifiedOnRestart() {
        // Simulate restart calling listeners
        for (RestartListener listener : RestartListener.all())
            listener.onRestart();

        ArgumentCaptor<OfflineCause> captor = ArgumentCaptor.forClass(OfflineCause.class);
        Mockito.verify(JenkinsTest.listenerMock).onOffline(Mockito.eq(j.jenkins.toComputer()), captor.capture());
        Assert.assertTrue(captor.getValue().toString().contains("restart"));
    }

    @TestExtension("testComputerListenerNotifiedOnRestart")
    public static final ComputerListener listenerMock = Mockito.mock(ComputerListener.class);

    @Test
    public void runScriptOnOfflineComputer() throws Exception {
        DumbSlave slave = j.createSlave(true);
        j.disconnectSlave(slave);
        URL url = new URL(j.getURL(), (("computer/" + (slave.getNodeName())) + "/scriptText?script=println(42)"));
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        WebRequest req = new WebRequest(url, HttpMethod.POST);
        Page page = wc.getPage(wc.addCrumb(req));
        WebResponse rsp = page.getWebResponse();
        MatcherAssert.assertThat(rsp.getContentAsString(), Matchers.containsString("Node is offline"));
        MatcherAssert.assertThat(rsp.getStatusCode(), Matchers.equalTo(404));
    }

    @Test
    @Issue("JENKINS-38487")
    public void startupShouldNotFailOnFailingOnlineListener() {
        // We do nothing, FailingOnOnlineListener & JenkinsRule should cause the
        // boot failure if the issue is not fixed.
    }

    @TestExtension("startupShouldNotFailOnFailingOnlineListener")
    public static final class FailingOnOnlineListener extends ComputerListener {
        @Override
        public void onOnline(Computer c, TaskListener listener) throws IOException, InterruptedException {
            throw new IOException("Something happened (the listener always throws this exception)");
        }
    }

    @Test
    @Issue("JENKINS-39465")
    public void agentProtocols_singleEnable_roundtrip() throws Exception {
        final Set<String> defaultProtocols = Collections.unmodifiableSet(j.jenkins.getAgentProtocols());
        final Set<String> newProtocols = new HashSet<>(defaultProtocols);
        newProtocols.add(JenkinsTest.MockOptInProtocol1.NAME);
        j.jenkins.setAgentProtocols(newProtocols);
        j.jenkins.save();
        final Set<String> agentProtocolsBeforeReload = j.jenkins.getAgentProtocols();
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol1.NAME, "before the roundtrip");
        j.jenkins.reload();
        final Set<String> reloadedProtocols = j.jenkins.getAgentProtocols();
        Assert.assertFalse("The protocol list must have been really reloaded", (agentProtocolsBeforeReload == reloadedProtocols));
        MatcherAssert.assertThat("We should have additional enabled protocol", reloadedProtocols.size(), Matchers.equalTo(((defaultProtocols.size()) + 1)));
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol1.NAME, "after the roundtrip");
    }

    @Test
    @Issue("JENKINS-39465")
    public void agentProtocols_multipleDisable_roundtrip() throws Exception {
        final Set<String> defaultProtocols = Collections.unmodifiableSet(j.jenkins.getAgentProtocols());
        assertProtocolEnabled(JenkinsTest.MockOptOutProtocol1.NAME, "after startup");
        final Set<String> newProtocols = new HashSet<>(defaultProtocols);
        newProtocols.remove(JenkinsTest.MockOptOutProtocol1.NAME);
        j.jenkins.setAgentProtocols(newProtocols);
        j.jenkins.save();
        assertProtocolDisabled(JenkinsTest.MockOptOutProtocol1.NAME, "before the roundtrip");
        final Set<String> agentProtocolsBeforeReload = j.jenkins.getAgentProtocols();
        j.jenkins.reload();
        Assert.assertFalse("The protocol list must have been really refreshed", (agentProtocolsBeforeReload == (j.jenkins.getAgentProtocols())));
        MatcherAssert.assertThat("We should have disabled one protocol", j.jenkins.getAgentProtocols().size(), Matchers.equalTo(((defaultProtocols.size()) - 1)));
        assertProtocolDisabled(JenkinsTest.MockOptOutProtocol1.NAME, "after the roundtrip");
    }

    @Test
    @Issue("JENKINS-39465")
    public void agentProtocols_multipleEnable_roundtrip() throws Exception {
        final Set<String> defaultProtocols = Collections.unmodifiableSet(j.jenkins.getAgentProtocols());
        final Set<String> newProtocols = new HashSet<>(defaultProtocols);
        newProtocols.add(JenkinsTest.MockOptInProtocol1.NAME);
        newProtocols.add(JenkinsTest.MockOptInProtocol2.NAME);
        j.jenkins.setAgentProtocols(newProtocols);
        j.jenkins.save();
        final Set<String> agentProtocolsBeforeReload = j.jenkins.getAgentProtocols();
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol1.NAME, "before the roundtrip");
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol2.NAME, "before the roundtrip");
        j.jenkins.reload();
        final Set<String> reloadedProtocols = j.jenkins.getAgentProtocols();
        Assert.assertFalse("The protocol list must have been really reloaded", (agentProtocolsBeforeReload == reloadedProtocols));
        MatcherAssert.assertThat("There should be two additional enabled protocols", reloadedProtocols.size(), Matchers.equalTo(((defaultProtocols.size()) + 2)));
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol1.NAME, "after the roundtrip");
        assertProtocolEnabled(JenkinsTest.MockOptInProtocol2.NAME, "after the roundtrip");
    }

    @Test
    @Issue("JENKINS-39465")
    public void agentProtocols_singleDisable_roundtrip() throws Exception {
        final Set<String> defaultProtocols = Collections.unmodifiableSet(j.jenkins.getAgentProtocols());
        final String protocolToDisable1 = JenkinsTest.MockOptOutProtocol1.NAME;
        final String protocolToDisable2 = JenkinsTest.MockOptOutProtocol2.NAME;
        final Set<String> newProtocols = new HashSet<>(defaultProtocols);
        newProtocols.remove(protocolToDisable1);
        newProtocols.remove(protocolToDisable2);
        j.jenkins.setAgentProtocols(newProtocols);
        j.jenkins.save();
        assertProtocolDisabled(protocolToDisable1, "before the roundtrip");
        assertProtocolDisabled(protocolToDisable2, "before the roundtrip");
        final Set<String> agentProtocolsBeforeReload = j.jenkins.getAgentProtocols();
        j.jenkins.reload();
        Assert.assertFalse("The protocol list must have been really reloaded", (agentProtocolsBeforeReload == (j.jenkins.getAgentProtocols())));
        MatcherAssert.assertThat("We should have disabled two protocols", j.jenkins.getAgentProtocols().size(), Matchers.equalTo(((defaultProtocols.size()) - 2)));
        assertProtocolDisabled(protocolToDisable1, "after the roundtrip");
        assertProtocolDisabled(protocolToDisable2, "after the roundtrip");
    }

    @TestExtension
    public static class MockOptInProtocol1 extends JenkinsTest.MockOptInProtocol {
        static final String NAME = "MOCK-OPTIN-1";

        @Override
        public String getName() {
            return JenkinsTest.MockOptInProtocol1.NAME;
        }
    }

    @TestExtension
    public static class MockOptInProtocol2 extends JenkinsTest.MockOptInProtocol {
        static final String NAME = "MOCK-OPTIN-2";

        @Override
        public String getName() {
            return JenkinsTest.MockOptInProtocol2.NAME;
        }
    }

    private abstract static class MockOptInProtocol extends AgentProtocol {
        @Override
        public boolean isOptIn() {
            return true;
        }

        @Override
        public void handle(Socket socket) throws IOException, InterruptedException {
            throw new IOException("This is a mock agent protocol. It cannot be used for connection");
        }
    }

    @TestExtension
    public static class MockOptOutProtocol1 extends JenkinsTest.MockOptOutProtocol {
        static final String NAME = "MOCK-OPTOUT-1";

        @Override
        public String getName() {
            return JenkinsTest.MockOptOutProtocol1.NAME;
        }
    }

    @TestExtension
    public static class MockOptOutProtocol2 extends JenkinsTest.MockOptOutProtocol {
        static final String NAME = "MOCK-OPTOUT-2";

        @Override
        public String getName() {
            return JenkinsTest.MockOptOutProtocol2.NAME;
        }
    }

    private abstract static class MockOptOutProtocol extends AgentProtocol {
        @Override
        public boolean isOptIn() {
            return false;
        }

        @Override
        public void handle(Socket socket) throws IOException, InterruptedException {
            throw new IOException("This is a mock agent protocol. It cannot be used for connection");
        }
    }

    @Issue("JENKINS-42577")
    @Test
    public void versionIsSavedInSave() throws Exception {
        VERSION = "1.0";
        j.jenkins.save();
        VersionNumber storedVersion = Jenkins.getStoredVersion();
        Assert.assertNotNull(storedVersion);
        Assert.assertEquals(storedVersion.toString(), "1.0");
        VERSION = null;
        j.jenkins.save();
        VersionNumber nullVersion = Jenkins.getStoredVersion();
        Assert.assertNull(nullVersion);
    }

    // Sources: https://github.com/Vlatombe/jenkins-47406
    @Issue("JENKINS-47406")
    @Test
    @WithPlugin("jenkins-47406.hpi")
    public void jobCreatedByInitializerIsRetained() {
        Assert.assertNotNull("JENKINS-47406 should exist", j.jenkins.getItem("JENKINS-47406"));
    }
}

