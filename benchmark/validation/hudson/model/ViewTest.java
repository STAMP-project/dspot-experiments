/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Tom Huybrechts
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


import FormValidation.Kind.ERROR;
import FormValidation.Kind.OK;
import IconSet.icons;
import Item.CREATE;
import Jenkins.ADMINISTER;
import Jenkins.ANONYMOUS;
import Jenkins.READ;
import Jenkins.XSTREAM;
import Mode.EXCLUSIVE;
import com.cloudbees.hudson.plugins.folder.Folder;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import hudson.matrix.LabelAxis;
import hudson.matrix.MatrixProject;
import hudson.security.ACL;
import hudson.security.AccessDeniedException2;
import hudson.slaves.DumbSlave;
import hudson.util.HudsonIsLoading;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import jenkins.model.ProjectNamingStrategy;
import org.jenkins.ui.icon.Icon;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Email;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.MockFolder;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Text;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ViewTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule();

    @Issue("JENKINS-7100")
    @Test
    public void xHudsonHeader() throws Exception {
        Assert.assertNotNull(j.createWebClient().goTo("").getWebResponse().getResponseHeaderValue("X-Hudson"));
    }

    @Issue("JENKINS-43848")
    @Test
    public void testNoCacheHeadersAreSet() throws Exception {
        List<NameValuePair> responseHeaders = j.createWebClient().goTo("view/all/itemCategories", "application/json").getWebResponse().getResponseHeaders();
        final Map<String, String> values = new HashMap<>();
        for (NameValuePair p : responseHeaders) {
            values.put(p.getName(), p.getValue());
        }
        String resp = values.get("Cache-Control");
        Assert.assertThat(resp, is("no-cache, no-store, must-revalidate"));
        Assert.assertThat(values.get("Expires"), is("0"));
        Assert.assertThat(values.get("Pragma"), is("no-cache"));
    }

    /**
     * Creating two views with the same name.
     */
    @Email("http://d.hatena.ne.jp/ssogabe/20090101/1230744150")
    @Test
    public void conflictingName() throws Exception {
        Assert.assertNull(j.jenkins.getView("foo"));
        WebClient wc = j.createWebClient();
        HtmlForm form = wc.goTo("newView").getFormByName("createItem");
        form.getInputByName("name").setValueAttribute("foo");
        setChecked(true);
        j.submit(form);
        Assert.assertNotNull(j.jenkins.getView("foo"));
        wc.setThrowExceptionOnFailingStatusCode(false);
        // do it again and verify an error
        Page page = j.submit(form);
        Assert.assertEquals("shouldn't be allowed to create two views of the same name.", HttpURLConnection.HTTP_BAD_REQUEST, page.getWebResponse().getStatusCode());
    }

    @Test
    public void privateView() throws Exception {
        j.createFreeStyleProject("project1");
        User user = User.get("me", true);// create user

        WebClient wc = j.createWebClient();
        HtmlPage userPage = wc.goTo("user/me");
        HtmlAnchor privateViewsLink = userPage.getAnchorByText("My Views");
        Assert.assertNotNull("My Views link not available", privateViewsLink);
        HtmlPage privateViewsPage = ((HtmlPage) (privateViewsLink.click()));
        Text viewLabel = ((Text) (privateViewsPage.getFirstByXPath("//div[@class='tabBar']//div[@class='tab active']/a/text()")));
        Assert.assertTrue("'All' view should be selected", viewLabel.getTextContent().contains(Messages.Hudson_ViewName()));
        View listView = listView("listView");
        HtmlPage newViewPage = wc.goTo("user/me/my-views/newView");
        HtmlForm form = newViewPage.getFormByName("createItem");
        form.getInputByName("name").setValueAttribute("proxy-view");
        setChecked(true);
        HtmlPage proxyViewConfigurePage = j.submit(form);
        View proxyView = user.getProperty(MyViewsProperty.class).getView("proxy-view");
        Assert.assertNotNull(proxyView);
        form = proxyViewConfigurePage.getFormByName("viewConfig");
        form.getSelectByName("proxiedViewName").setSelectedAttribute("listView", true);
        j.submit(form);
        Assert.assertTrue((proxyView instanceof ProxyView));
        Assert.assertEquals(getProxiedViewName(), "listView");
        Assert.assertEquals(getProxiedView(), listView);
    }

    @Test
    public void deleteView() throws Exception {
        WebClient wc = j.createWebClient();
        ListView v = listView("list");
        HtmlPage delete = wc.getPage(v, "delete");
        j.submit(delete.getFormByName("delete"));
        Assert.assertNull(j.jenkins.getView("list"));
        User user = User.get("user", true);
        MyViewsProperty p = user.getProperty(MyViewsProperty.class);
        v = new ListView("list", p);
        p.addView(v);
        delete = wc.getPage(v, "delete");
        j.submit(delete.getFormByName("delete"));
        Assert.assertNull(p.getView("list"));
    }

    @Issue("JENKINS-9367")
    @Test
    public void persistence() throws Exception {
        ListView view = listView("foo");
        ListView v = ((ListView) (XSTREAM.fromXML(XSTREAM.toXML(view))));
        System.out.println(v.getProperties());
        Assert.assertNotNull(v.getProperties());
    }

    @Issue("JENKINS-9367")
    @Test
    public void allImagesCanBeLoaded() throws Exception {
        User.get("user", true);
        // as long as the cloudbees-folder is included as test dependency, its Folder will load icon
        boolean folderPluginActive = (j.jenkins.getPlugin("cloudbees-folder")) != null;
        // link to Folder class is done here to ensure if we remove the dependency, this code will fail and so will be removed
        boolean folderPluginClassesLoaded = (j.jenkins.getDescriptor(Folder.class)) != null;
        // this could be written like this to avoid the hard dependency:
        // boolean folderPluginClassesLoaded = (j.jenkins.getDescriptor("com.cloudbees.hudson.plugins.folder.Folder") != null);
        if ((!folderPluginActive) && folderPluginClassesLoaded) {
            // reset the icon added by Folder because the plugin resources are not reachable
            icons.addIcon(new Icon("icon-folder icon-md", "24x24/folder.gif", "width: 24px; height: 24px;"));
        }
        WebClient webClient = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        j.assertAllImageLoadSuccessfully(webClient.goTo("asynchPeople"));
    }

    @Issue("JENKINS-16608")
    @Test
    public void notAllowedName() throws Exception {
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlForm form = wc.goTo("newView").getFormByName("createItem");
        form.getInputByName("name").setValueAttribute("..");
        setChecked(true);
        HtmlPage page = j.submit(form);
        Assert.assertEquals("\"..\" should not be allowed.", HttpURLConnection.HTTP_BAD_REQUEST, page.getWebResponse().getStatusCode());
    }

    @Issue("JENKINS-17302")
    @Test
    public void doConfigDotXml() throws Exception {
        ListView view = listView("v");
        view.description = "one";
        WebClient wc = j.createWebClient();
        String xml = wc.goToXml("view/v/config.xml").getWebResponse().getContentAsString();
        Assert.assertTrue(xml, xml.contains("<description>one</description>"));
        xml = xml.replace("<description>one</description>", "<description>two</description>");
        WebRequest req = new WebRequest(wc.createCrumbedUrl("view/v/config.xml"), HttpMethod.POST);
        req.setRequestBody(xml);
        req.setEncodingType(null);
        wc.getPage(req);
        Assert.assertEquals("two", view.getDescription());
        xml = asString();
        Assert.assertTrue(xml, xml.contains("<description>two</description>"));
    }

    @Issue("JENKINS-21017")
    @Test
    public void doConfigDotXmlReset() throws Exception {
        ListView view = listView("v");
        view.description = "one";
        WebClient wc = j.createWebClient();
        String xml = wc.goToXml("view/v/config.xml").getWebResponse().getContentAsString();
        Assert.assertThat(xml, containsString("<description>one</description>"));
        xml = xml.replace("<description>one</description>", "");
        WebRequest req = new WebRequest(wc.createCrumbedUrl("view/v/config.xml"), HttpMethod.POST);
        req.setRequestBody(xml);
        req.setEncodingType(null);
        wc.getPage(req);
        Assert.assertEquals(null, view.getDescription());// did not work

        xml = asString();
        Assert.assertThat(xml, not(containsString("<description>")));// did not work

        Assert.assertEquals(j.jenkins, view.getOwner());
    }

    @Test
    public void testGetQueueItems() throws IOException, Exception {
        ListView view1 = listView("view1");
        view1.filterQueue = true;
        ListView view2 = listView("view2");
        view2.filterQueue = true;
        FreeStyleProject inView1 = j.createFreeStyleProject("in-view1");
        inView1.setAssignedLabel(j.jenkins.getLabelAtom("without-any-slave"));
        view1.add(inView1);
        MatrixProject inView2 = j.jenkins.createProject(MatrixProject.class, "in-view2");
        inView2.setAssignedLabel(j.jenkins.getLabelAtom("without-any-slave"));
        view2.add(inView2);
        FreeStyleProject notInView = j.createFreeStyleProject("not-in-view");
        notInView.setAssignedLabel(j.jenkins.getLabelAtom("without-any-slave"));
        FreeStyleProject inBothViews = j.createFreeStyleProject("in-both-views");
        inBothViews.setAssignedLabel(j.jenkins.getLabelAtom("without-any-slave"));
        view1.add(inBothViews);
        view2.add(inBothViews);
        Queue.getInstance().schedule(notInView, 0);
        Queue.getInstance().schedule(inView1, 0);
        Queue.getInstance().schedule(inView2, 0);
        Queue.getInstance().schedule(inBothViews, 0);
        Thread.sleep(1000);
        assertContainsItems(view1, inView1, inBothViews);
        assertNotContainsItems(view1, notInView, inView2);
        assertContainsItems(view2, inView2, inBothViews);
        assertNotContainsItems(view2, notInView, inView1);
    }

    @Test
    public void testGetComputers() throws IOException, Exception {
        ListView view1 = listView("view1");
        ListView view2 = listView("view2");
        ListView view3 = listView("view3");
        view1.filterExecutors = true;
        view2.filterExecutors = true;
        view3.filterExecutors = true;
        Slave slave0 = j.createOnlineSlave(j.jenkins.getLabel("label0"));
        Slave slave1 = j.createOnlineSlave(j.jenkins.getLabel("label1"));
        Slave slave2 = j.createOnlineSlave(j.jenkins.getLabel("label2"));
        Slave slave3 = j.createOnlineSlave(j.jenkins.getLabel("label0"));
        Slave slave4 = j.createOnlineSlave(j.jenkins.getLabel("label4"));
        FreeStyleProject freestyleJob = j.createFreeStyleProject("free");
        view1.add(freestyleJob);
        freestyleJob.setAssignedLabel(j.jenkins.getLabel("label0||label2"));
        MatrixProject matrixJob = j.jenkins.createProject(MatrixProject.class, "matrix");
        view1.add(matrixJob);
        matrixJob.setAxes(new hudson.matrix.AxisList(new LabelAxis("label", Arrays.asList("label1"))));
        FreeStyleProject noLabelJob = j.createFreeStyleProject("not-assigned-label");
        view3.add(noLabelJob);
        noLabelJob.setAssignedLabel(null);
        FreeStyleProject foreignJob = j.createFreeStyleProject("in-other-view");
        view2.add(foreignJob);
        foreignJob.setAssignedLabel(j.jenkins.getLabel("label0||label1"));
        // contains all agents having labels associated with freestyleJob or matrixJob
        assertContainsNodes(view1, slave0, slave1, slave2, slave3);
        assertNotContainsNodes(view1, slave4);
        // contains all agents having labels associated with foreignJob
        assertContainsNodes(view2, slave0, slave1, slave3);
        assertNotContainsNodes(view2, slave2, slave4);
        // contains all slaves as it contains noLabelJob that can run everywhere
        assertContainsNodes(view3, slave0, slave1, slave2, slave3, slave4);
    }

    @Test
    @Issue("JENKINS-21474")
    public void testGetComputersNPE() throws Exception {
        ListView view = listView("aView");
        view.filterExecutors = true;
        DumbSlave dedicatedSlave = j.createOnlineSlave();
        dedicatedSlave.setMode(EXCLUSIVE);
        view.add(j.createFreeStyleProject());
        FreeStyleProject tiedJob = j.createFreeStyleProject();
        tiedJob.setAssignedNode(dedicatedSlave);
        view.add(tiedJob);
        DumbSlave notIncludedSlave = j.createOnlineSlave();
        notIncludedSlave.setMode(EXCLUSIVE);
        assertContainsNodes(view, j.jenkins, dedicatedSlave);
        assertNotContainsNodes(view, notIncludedSlave);
    }

    @Test
    public void testGetItem() throws Exception {
        ListView view = listView("foo");
        FreeStyleProject job1 = j.createFreeStyleProject("free");
        MatrixProject job2 = j.jenkins.createProject(MatrixProject.class, "matrix");
        FreeStyleProject job3 = j.createFreeStyleProject("not-included");
        view.jobNames.add(job2.getDisplayName());
        view.jobNames.add(job1.getDisplayName());
        Assert.assertEquals(("View should return job " + (job1.getDisplayName())), job1, view.getItem("free"));
        Assert.assertNotNull("View should return null.", view.getItem("not-included"));
    }

    @Test
    public void testRename() throws Exception {
        ListView view = listView("foo");
        view.rename("renamed");
        Assert.assertEquals("View should have name foo.", "renamed", view.getDisplayName());
        ListView view2 = listView("foo");
        try {
            view2.rename("renamed");
            Assert.fail("Attempt to rename job with a name used by another view with the same owner should throw exception");
        } catch (Exception Exception) {
        }
        Assert.assertEquals("View should not be renamed if required name has another view with the same owner", "foo", view2.getDisplayName());
    }

    @Test
    public void testGetOwnerItemGroup() throws Exception {
        ListView view = listView("foo");
        Assert.assertEquals("View should have owner jenkins.", j.jenkins.getItemGroup(), view.getOwner().getItemGroup());
    }

    @Test
    public void testGetOwnerPrimaryView() throws Exception {
        ListView view = listView("foo");
        j.jenkins.setPrimaryView(view);
        Assert.assertEquals(("View should have primary view " + (view.getDisplayName())), view, view.getOwner().getPrimaryView());
    }

    @Test
    public void testSave() throws Exception {
        ListView view = listView("foo");
        FreeStyleProject job = j.createFreeStyleProject("free");
        view.jobNames.add("free");
        view.save();
        j.jenkins.doReload();
        // wait until all configuration are reloaded
        if ((j.jenkins.servletContext.getAttribute("app")) instanceof HudsonIsLoading) {
            Thread.sleep(500);
        }
        Assert.assertTrue("View does not contains job free after load.", j.jenkins.getView(view.getDisplayName()).contains(j.jenkins.getItem(job.getName())));
    }

    @Test
    public void testGetProperties() throws Exception {
        View view = listView("foo");
        Thread.sleep(100000);
        HtmlForm f = j.createWebClient().getPage(view, "configure").getFormByName("viewConfig");
        click();
        j.submit(f);
        Assert.assertNotNull("View should contain ViewPropertyImpl property.", view.getProperties().get(ViewTest.PropertyImpl.class));
    }

    public static class PropertyImpl extends ViewProperty {
        public String name;

        @DataBoundConstructor
        public PropertyImpl(String name) {
            this.name = name;
        }

        @TestExtension
        public static class DescriptorImpl extends ViewPropertyDescriptor {
            @Override
            public String getDisplayName() {
                return "Test property";
            }
        }
    }

    @Issue("JENKINS-20509")
    @Test
    public void checkJobName() throws Exception {
        j.createFreeStyleProject("topprj");
        final MockFolder d1 = j.createFolder("d1");
        d1.createProject(FreeStyleProject.class, "subprj");
        final MockFolder d2 = j.createFolder("d2");
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin").grant(READ).everywhere().toEveryone().grant(Job.READ).everywhere().toEveryone().grant(CREATE).onFolders(d1).to("dev"));// not on root or d2

        ACL.impersonate(ANONYMOUS, new jenkins.security.NotReallyRoleSensitiveCallable<Void, Exception>() {
            @Override
            public Void call() throws Exception {
                try {
                    assertCheckJobName(j.jenkins, "whatever", OK);
                    Assert.fail("should not have been allowed");
                } catch (AccessDeniedException2 x) {
                    // OK
                }
                return null;
            }
        });
        ACL.impersonate(User.get("dev").impersonate(), new jenkins.security.NotReallyRoleSensitiveCallable<Void, Exception>() {
            @Override
            public Void call() throws Exception {
                try {
                    assertCheckJobName(j.jenkins, "whatever", OK);
                    Assert.fail("should not have been allowed");
                } catch (AccessDeniedException2 x) {
                    // OK
                }
                try {
                    assertCheckJobName(d2, "whatever", OK);
                    Assert.fail("should not have been allowed");
                } catch (AccessDeniedException2 x) {
                    // OK
                }
                assertCheckJobName(d1, "whatever", OK);
                return null;
            }
        });
        ACL.impersonate(User.get("admin").impersonate(), new jenkins.security.NotReallyRoleSensitiveCallable<Void, Exception>() {
            @Override
            public Void call() throws Exception {
                assertCheckJobName(j.jenkins, "whatever", OK);
                assertCheckJobName(d1, "whatever", OK);
                assertCheckJobName(d2, "whatever", OK);
                assertCheckJobName(j.jenkins, "d1", ERROR);
                assertCheckJobName(j.jenkins, "topprj", ERROR);
                assertCheckJobName(d1, "subprj", ERROR);
                assertCheckJobName(j.jenkins, "", OK);
                assertCheckJobName(j.jenkins, "foo/bie", ERROR);
                assertCheckJobName(d2, "New", OK);
                j.jenkins.setProjectNamingStrategy(new ProjectNamingStrategy.PatternProjectNamingStrategy("[a-z]+", "", true));
                assertCheckJobName(d2, "New", ERROR);
                assertCheckJobName(d2, "new", OK);
                return null;
            }
        });
        JenkinsRule.WebClient wc = j.createWebClient().withBasicCredentials("admin");
        Assert.assertEquals("original ${rootURL}/checkJobName still supported", "<div/>", wc.goTo("checkJobName?value=stuff").getWebResponse().getContentAsString());
        Assert.assertEquals("but now possible on a view in a folder", "<div/>", wc.goTo("job/d1/view/All/checkJobName?value=stuff").getWebResponse().getContentAsString());
    }

    @Issue("JENKINS-41825")
    @Test
    public void brokenGetItems() throws Exception {
        logging.capture(100).record("", Level.INFO);
        j.jenkins.addView(new ViewTest.BrokenView());
        j.createWebClient().goTo("view/broken/");
        boolean found = false;
        LOGS : for (LogRecord record : logging.getRecords()) {
            for (Throwable t = record.getThrown(); t != null; t = t.getCause()) {
                if ((t instanceof IllegalStateException) && (ViewTest.BrokenView.ERR.equals(t.getMessage()))) {
                    found = true;
                    break LOGS;
                }
            }
        }
        Assert.assertTrue(found);
    }

    private static class BrokenView extends ListView {
        static final String ERR = "oops I cannot retrieve items";

        BrokenView() {
            super("broken");
        }

        @Override
        public List<TopLevelItem> getItems() {
            throw new IllegalStateException(ViewTest.BrokenView.ERR);
        }
    }

    @Test
    @Issue("JENKINS-36908")
    @LocalData
    public void testAllViewCreatedIfNoPrimary() throws Exception {
        Assert.assertNotNull(j.getInstance().getView("All"));
    }

    @Test
    @Issue("JENKINS-36908")
    @LocalData
    public void testAllViewNotCreatedIfPrimary() throws Exception {
        Assert.assertNull(j.getInstance().getView("All"));
    }
}

