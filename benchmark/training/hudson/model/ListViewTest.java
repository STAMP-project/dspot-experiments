/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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


import hudson.matrix.MatrixProject;
import hudson.matrix.TextAxis;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AuthorizationStrategy;
import hudson.security.Permission;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.acegisecurity.Authentication;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.recipes.LocalData;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.mockito.Mockito;


public class ListViewTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TestName testName = new TestName();

    @Issue("JENKINS-15309")
    @LocalData
    @Test
    public void nullJobNames() throws Exception {
        Assert.assertTrue(j.jenkins.getView("v").getItems().isEmpty());
    }

    @Test
    public void testJobLinksAreValid() throws Exception {
        /* jenkins
        + -- folder1
             |-- job1
             +-- folder2
                 +-- job2
         */
        MockFolder folder1 = j.jenkins.createProject(MockFolder.class, "folder1");
        FreeStyleProject job1 = folder1.createProject(FreeStyleProject.class, "job1");
        MockFolder folder2 = folder1.createProject(MockFolder.class, "folder2");
        FreeStyleProject job2 = folder2.createProject(FreeStyleProject.class, "job2");
        ListView lv = new ListView("myview");
        lv.setRecurse(true);
        lv.setIncludeRegex(".*");
        j.jenkins.addView(lv);
        WebClient webClient = j.createWebClient();
        checkLinkFromViewExistsAndIsValid(folder1, j.jenkins, lv, webClient);
        checkLinkFromViewExistsAndIsValid(job1, j.jenkins, lv, webClient);
        checkLinkFromViewExistsAndIsValid(folder2, j.jenkins, lv, webClient);
        checkLinkFromViewExistsAndIsValid(job2, j.jenkins, lv, webClient);
        ListView lv2 = new ListView("myview", folder1);
        lv2.setRecurse(true);
        lv2.setIncludeRegex(".*");
        folder1.addView(lv2);
        checkLinkFromItemExistsAndIsValid(job1, folder1, folder1, webClient);
        checkLinkFromItemExistsAndIsValid(folder2, folder1, folder1, webClient);
        checkLinkFromViewExistsAndIsValid(job2, folder1, lv2, webClient);
    }

    @Issue("JENKINS-20415")
    @Test
    public void nonTopLevelItemGroup() throws Exception {
        MatrixProject mp = j.jenkins.createProject(MatrixProject.class, "mp");
        mp.setAxes(new hudson.matrix.AxisList(new TextAxis("axis", "one", "two")));
        Assert.assertEquals(2, mp.getItems().size());
        ListView v = new ListView("v");
        j.jenkins.addView(v);
        v.setIncludeRegex(".*");
        v.setRecurse(true);
        // Note: did not manage to reproduce CCE until I changed expand to use ?for (TopLevelItem item : items)? rather than ?for (Item item : items)?; perhaps a compiler-specific issue?
        Assert.assertEquals(Collections.singletonList(mp), v.getItems());
    }

    @Issue("JENKINS-18680")
    @Test
    public void renamesMovesAndDeletes() throws Exception {
        MockFolder top = j.createFolder("top");
        MockFolder sub = top.createProject(MockFolder.class, "sub");
        FreeStyleProject p1 = top.createProject(FreeStyleProject.class, "p1");
        FreeStyleProject p2 = sub.createProject(FreeStyleProject.class, "p2");
        FreeStyleProject p3 = top.createProject(FreeStyleProject.class, "p3");
        ListView v = new ListView("v");
        v.setRecurse(true);
        top.addView(v);
        v.add(p1);
        v.add(p2);
        v.add(p3);
        Assert.assertEquals(new HashSet<TopLevelItem>(Arrays.asList(p1, p2, p3)), new HashSet<TopLevelItem>(v.getItems()));
        sub.renameTo("lower");
        MockFolder stuff = top.createProject(MockFolder.class, "stuff");
        Items.move(p1, stuff);
        p3.delete();
        top.createProject(FreeStyleProject.class, "p3");
        Assert.assertEquals(new HashSet<TopLevelItem>(Arrays.asList(p1, p2)), new HashSet<TopLevelItem>(v.getItems()));
        top.renameTo("upper");
        Assert.assertEquals(new HashSet<TopLevelItem>(Arrays.asList(p1, p2)), new HashSet<TopLevelItem>(v.getItems()));
    }

    @Issue("JENKINS-23893")
    @Test
    public void renameJobContainedInTopLevelView() throws Exception {
        ListView view = new ListView("view", j.jenkins);
        j.jenkins.addView(view);
        FreeStyleProject job = j.createFreeStyleProject("old_name");
        view.add(job);
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
        job.renameTo("new_name");
        Assert.assertFalse(("old job name is still contained: " + (view.jobNames)), view.jobNames.contains("old_name"));
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
    }

    @Test
    public void renameContainedJob() throws Exception {
        MockFolder folder = j.createFolder("folder");
        ListView view = new ListView("view", folder);
        folder.addView(view);
        FreeStyleProject job = folder.createProject(FreeStyleProject.class, "old_name");
        view.add(job);
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
        job.renameTo("new_name");
        Assert.assertFalse("old job name is still contained", view.jobNames.contains("old_name"));
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
    }

    @Issue("JENKINS-23893")
    @Test
    public void deleteJobContainedInTopLevelView() throws Exception {
        ListView view = new ListView("view", j.jenkins);
        j.jenkins.addView(view);
        FreeStyleProject job = j.createFreeStyleProject("project");
        view.add(job);
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
        job.delete();
        Assert.assertFalse(view.contains(job));
        Assert.assertFalse(view.jobNamesContains(job));
    }

    @Test
    public void deleteContainedJob() throws Exception {
        MockFolder folder = j.createFolder("folder");
        ListView view = new ListView("view", folder);
        folder.addView(view);
        FreeStyleProject job = folder.createProject(FreeStyleProject.class, "project");
        view.add(job);
        Assert.assertTrue(view.contains(job));
        Assert.assertTrue(view.jobNamesContains(job));
        job.delete();
        Assert.assertFalse(view.contains(job));
        Assert.assertFalse(view.jobNamesContains(job));
    }

    @Issue("JENKINS-22769")
    @Test
    public void renameJobInViewYouCannotSee() throws Exception {
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new ListViewTest.AllButViewsAuthorizationStrategy());
        final FreeStyleProject p = j.createFreeStyleProject("p1");
        ListView v = new ListView("v", j.jenkins);
        v.add(p);
        j.jenkins.addView(v);
        try (ACLContext acl = ACL.as(User.get("alice"))) {
            p.renameTo("p2");
        }
        Assert.assertEquals(Collections.singletonList(p), v.getItems());
    }

    @Issue("JENKINS-41128")
    @Test
    public void addJobUsingAPI() throws Exception {
        ListView v = new ListView("view", j.jenkins);
        j.jenkins.addView(v);
        StaplerRequest req = Mockito.mock(StaplerRequest.class);
        StaplerResponse rsp = Mockito.mock(StaplerResponse.class);
        String configXml = IOUtils.toString(getClass().getResourceAsStream(String.format("%s/%s/config.xml", getClass().getSimpleName(), testName.getMethodName())), "UTF-8");
        Mockito.when(req.getMethod()).thenReturn("POST");
        Mockito.when(req.getParameter("name")).thenReturn("job1");
        Mockito.when(req.getInputStream()).thenReturn(new ListViewTest.Stream(IOUtils.toInputStream(configXml)));
        Mockito.when(req.getContentType()).thenReturn("application/xml");
        v.doCreateItem(req, rsp);
        List<TopLevelItem> items = v.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("job1", items.get(0).getName());
    }

    @Issue("JENKINS-23411")
    @Test
    public void doRemoveJobFromViewNullItem() throws Exception {
        MockFolder folder = j.createFolder("folder");
        ListView view = new ListView("view", folder);
        folder.addView(view);
        FreeStyleProject job = folder.createProject(FreeStyleProject.class, "job1");
        view.add(job);
        List<TopLevelItem> items = view.getItems();
        Assert.assertEquals(1, items.size());
        Assert.assertEquals("job1", items.get(0).getName());
        // remove a contained job
        view.doRemoveJobFromView("job1");
        List<TopLevelItem> itemsNow = view.getItems();
        Assert.assertEquals(0, itemsNow.size());
        // remove a not contained job
        try {
            view.doRemoveJobFromView("job2");
            Assert.fail("Remove job2");
        } catch (Failure e) {
            Assert.assertEquals(e.getMessage(), "Query parameter 'name' does not correspond to a known and readable item");
        }
    }

    private static class AllButViewsAuthorizationStrategy extends AuthorizationStrategy {
        @Override
        public ACL getRootACL() {
            return UNSECURED.getRootACL();
        }

        @Override
        public Collection<String> getGroups() {
            return Collections.emptyList();
        }

        @Override
        public ACL getACL(View item) {
            return new ACL() {
                @Override
                public boolean hasPermission(Authentication a, Permission permission) {
                    return a.equals(SYSTEM);
                }
            };
        }
    }

    private static class Stream extends ServletInputStream {
        private final InputStream inner;

        public Stream(final InputStream inner) {
            this.inner = inner;
        }

        @Override
        public int read() throws IOException {
            return inner.read();
        }

        @Override
        public boolean isFinished() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isReady() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }
}

