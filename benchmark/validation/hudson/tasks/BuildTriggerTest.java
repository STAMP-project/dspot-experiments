/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Alan Harder
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


import Cause.UpstreamCause;
import Computer.BUILD;
import Item.CONFIGURE;
import Jenkins.ANONYMOUS;
import Jenkins.READ;
import Result.FAILURE;
import Result.SUCCESS;
import Result.UNSTABLE;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Cause;
import hudson.model.DependencyGraph;
import hudson.model.DependencyGraph.Dependency;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.security.AuthorizationMatrixProperty;
import hudson.security.LegacySecurityRealm;
import hudson.security.Permission;
import hudson.security.ProjectMatrixAuthorizationStrategy;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jenkins.security.QueueItemAuthenticatorConfiguration;
import org.acegisecurity.Authentication;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.TestBuilder;
import org.xml.sax.SAXException;


public class BuildTriggerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @ClassRule
    public static BuildWatcher buildWatcher = new BuildWatcher();

    @Test
    public void buildTrigger() throws Exception {
        doTriggerTest(false, SUCCESS, UNSTABLE);
    }

    @Test
    public void triggerEvenWhenUnstable() throws Exception {
        doTriggerTest(true, UNSTABLE, FAILURE);
    }

    @Test
    public void mavenBuildTrigger() throws Exception {
        doMavenTriggerTest(false);
    }

    @Test
    public void mavenTriggerEvenWhenUnstable() throws Exception {
        doMavenTriggerTest(true);
    }

    /**
     *
     *
     * @see ReverseBuildTriggerTest#upstreamProjectSecurity
     */
    @Test
    public void downstreamProjectSecurity() throws Exception {
        j.jenkins.setSecurityRealm(new LegacySecurityRealm());
        ProjectMatrixAuthorizationStrategy auth = new ProjectMatrixAuthorizationStrategy();
        auth.add(READ, "alice");
        auth.add(BUILD, "alice");
        auth.add(BUILD, "anonymous");
        j.jenkins.setAuthorizationStrategy(auth);
        final FreeStyleProject upstream = j.createFreeStyleProject("upstream");
        Authentication alice = User.get("alice").impersonate();
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().add(new org.jvnet.hudson.test.MockQueueItemAuthenticator(Collections.singletonMap("upstream", alice)));
        Map<Permission, Set<String>> perms = new HashMap<Permission, Set<String>>();
        perms.put(Item.READ, Collections.singleton("alice"));
        perms.put(CONFIGURE, Collections.singleton("alice"));
        upstream.addProperty(new AuthorizationMatrixProperty(perms));
        String downstreamName = "d0wnstr3am";// do not clash with English messages!

        FreeStyleProject downstream = j.createFreeStyleProject(downstreamName);
        upstream.getPublishersList().add(new BuildTrigger(downstreamName, Result.SUCCESS));
        j.jenkins.rebuildDependencyGraph();
        /* The long way:
        WebClient wc = createWebClient();
        wc.login("alice");
        HtmlPage page = wc.getHistoryPageFilter(upstream, "configure");
        HtmlForm config = page.getFormByName("config");
        config.getButtonByCaption("Add post-build action").click(); // lib/hudson/project/config-publishers2.jelly
        page.getAnchorByText("Build other projects").click();
        HtmlTextInput childProjects = config.getInputByName("buildTrigger.childProjects");
        childProjects.setValueAttribute(downstreamName);
        submit(config);
         */
        Assert.assertEquals(Collections.singletonList(downstream), upstream.getDownstreamProjects());
        // Downstream projects whose existence we are not aware of will silently not be triggered:
        assertDoCheck(alice, Messages.BuildTrigger_NoSuchProject(downstreamName, "upstream"), upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        FreeStyleBuild b = j.buildAndAssertSuccess(upstream);
        j.assertLogNotContains(downstreamName, b);
        j.waitUntilNoActivity();
        Assert.assertNull(downstream.getLastBuild());
        // If we can see them, but not build them, that is a warning (but this is in cleanUp so the build is still considered a success):
        Map<Permission, Set<String>> grantedPermissions = new HashMap<Permission, Set<String>>();
        grantedPermissions.put(Item.READ, Collections.singleton("alice"));
        AuthorizationMatrixProperty amp = new AuthorizationMatrixProperty(grantedPermissions);
        downstream.addProperty(amp);
        assertDoCheck(alice, Messages.BuildTrigger_you_have_no_permission_to_build_(downstreamName), upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        b = j.buildAndAssertSuccess(upstream);
        j.assertLogContains(downstreamName, b);
        j.waitUntilNoActivity();
        Assert.assertNull(downstream.getLastBuild());
        // If we can build them, then great:
        grantedPermissions.put(Item.BUILD, Collections.singleton("alice"));
        downstream.removeProperty(amp);
        amp = new AuthorizationMatrixProperty(grantedPermissions);
        downstream.addProperty(amp);
        assertDoCheck(alice, null, upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        b = j.buildAndAssertSuccess(upstream);
        j.assertLogContains(downstreamName, b);
        j.waitUntilNoActivity();
        FreeStyleBuild b2 = downstream.getLastBuild();
        Assert.assertNotNull(b2);
        Cause.UpstreamCause cause = b2.getCause(UpstreamCause.class);
        Assert.assertNotNull(cause);
        Assert.assertEquals(b, cause.getUpstreamRun());
        // Now if we have configured some QIA?s but they are not active on this job, we should normally fall back to running as anonymous. Which would normally have no permissions:
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().replace(new org.jvnet.hudson.test.MockQueueItemAuthenticator(Collections.singletonMap("upstream", ANONYMOUS)));
        assertDoCheck(alice, Messages.BuildTrigger_you_have_no_permission_to_build_(downstreamName), upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        b = j.buildAndAssertSuccess(upstream);
        j.assertLogNotContains(downstreamName, b);
        j.waitUntilNoActivity();
        Assert.assertEquals(1, downstream.getLastBuild().number);
        // Unless we explicitly granted them:
        grantedPermissions.put(Item.READ, Collections.singleton("anonymous"));
        grantedPermissions.put(Item.BUILD, Collections.singleton("anonymous"));
        downstream.removeProperty(amp);
        amp = new AuthorizationMatrixProperty(grantedPermissions);
        downstream.addProperty(amp);
        assertDoCheck(alice, null, upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        b = j.buildAndAssertSuccess(upstream);
        j.assertLogContains(downstreamName, b);
        j.waitUntilNoActivity();
        Assert.assertEquals(2, downstream.getLastBuild().number);
        FreeStyleProject simple = j.createFreeStyleProject("simple");
        FreeStyleBuild b3 = j.buildAndAssertSuccess(simple);
        // Finally, in legacy mode we run as SYSTEM:
        grantedPermissions.clear();// similar behavior but different message if DescriptorImpl removed

        downstream.removeProperty(amp);
        amp = new AuthorizationMatrixProperty(grantedPermissions);
        downstream.addProperty(amp);
        QueueItemAuthenticatorConfiguration.get().getAuthenticators().clear();
        assertDoCheck(alice, Messages.BuildTrigger_NoSuchProject(downstreamName, "upstream"), upstream, downstreamName);
        assertDoCheck(alice, null, null, downstreamName);
        b = j.buildAndAssertSuccess(upstream);
        j.assertLogContains(downstreamName, b);
        j.waitUntilNoActivity();
        Assert.assertEquals(3, downstream.getLastBuild().number);
        b3 = j.buildAndAssertSuccess(simple);
    }

    @Test
    @Issue("JENKINS-20989")
    public void downstreamProjectShouldObserveCompletedParent() throws Exception {
        j.jenkins.setNumExecutors(2);
        final FreeStyleProject us = j.createFreeStyleProject();
        us.getPublishersList().add(new BuildTrigger("downstream", true));
        FreeStyleProject ds = createDownstreamProject();
        ds.getBuildersList().add(new BuildTriggerTest.AssertTriggerBuildCompleted(us, j.createWebClient()));
        j.jenkins.rebuildDependencyGraph();
        j.buildAndAssertSuccess(us);
        j.waitUntilNoActivity();
        final FreeStyleBuild dsb = ds.getBuildByNumber(1);
        Assert.assertNotNull(dsb);
        j.waitForCompletion(dsb);
        j.assertBuildStatusSuccess(dsb);
    }

    @Test
    @Issue("JENKINS-20989")
    public void allDownstreamProjectsShouldObserveCompletedParent() throws Exception {
        j.jenkins.setNumExecutors(3);
        final FreeStyleProject us = j.createFreeStyleProject();
        us.getPublishersList().add(new BuildTriggerTest.SlowTrigger("downstream,downstream2"));
        FreeStyleProject ds = createDownstreamProject();
        ds.getBuildersList().add(new BuildTriggerTest.AssertTriggerBuildCompleted(us, j.createWebClient()));
        FreeStyleProject ds2 = j.createFreeStyleProject("downstream2");
        ds2.setQuietPeriod(0);
        ds2.getBuildersList().add(new BuildTriggerTest.AssertTriggerBuildCompleted(us, j.createWebClient()));
        j.jenkins.rebuildDependencyGraph();
        FreeStyleBuild upstream = j.buildAndAssertSuccess(us);
        FreeStyleBuild dsb = assertDownstreamBuild(ds, upstream);
        j.waitForCompletion(dsb);
        j.assertBuildStatusSuccess(dsb);
        dsb = assertDownstreamBuild(ds2, upstream);
        j.waitForCompletion(dsb);
        j.assertBuildStatusSuccess(dsb);
    }

    // Trigger that goes through dependencies very slowly
    private static final class SlowTrigger extends BuildTrigger {
        private static final class Dep extends Dependency {
            private static boolean block = false;

            private Dep(AbstractProject upstream, AbstractProject downstream) {
                super(upstream, downstream);
            }

            @Override
            public boolean shouldTriggerBuild(AbstractBuild build, TaskListener listener, List<Action> actions) {
                if (BuildTriggerTest.SlowTrigger.Dep.block) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        throw new AssertionError(ex);
                    }
                }
                BuildTriggerTest.SlowTrigger.Dep.block = true;
                final boolean should = super.shouldTriggerBuild(build, listener, actions);
                return should;
            }
        }

        public SlowTrigger(String childProjects) {
            super(childProjects, true);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public void buildDependencyGraph(AbstractProject owner, DependencyGraph graph) {
            for (AbstractProject ch : getChildProjects(owner)) {
                graph.addDependency(new BuildTriggerTest.SlowTrigger.Dep(owner, ch));
            }
        }
    }

    // Fail downstream build if upstream is not completed yet
    private static final class AssertTriggerBuildCompleted extends TestBuilder {
        private final FreeStyleProject us;

        private final WebClient wc;

        private AssertTriggerBuildCompleted(FreeStyleProject us, WebClient wc) {
            this.us = us;
            this.wc = wc;
        }

        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
            FreeStyleBuild success = us.getLastSuccessfulBuild();
            FreeStyleBuild last = us.getLastBuild();
            try {
                Assert.assertFalse("Upstream build is not completed after downstream started", last.isBuilding());
                Assert.assertNotNull("Upstream build permalink not correctly updated", success);
                Assert.assertEquals(1, success.getNumber());
            } catch (AssertionError ex) {
                System.err.println(("Upstream build log: " + (last.getLog())));
                throw ex;
            }
            try {
                wc.getPage(us, "lastSuccessfulBuild");
            } catch (SAXException ex) {
                throw new AssertionError(ex);
            }
            return true;
        }
    }
}

