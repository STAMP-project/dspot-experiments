/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Yahoo! Inc., CloudBees, Inc.
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


import Ant.DescriptorImpl;
import AuthorizationStrategy.UNSECURED;
import BuildStep.PUBLISHERS;
import Mode.NORMAL;
import SecurityRealm.NO_AUTHENTICATION;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeUtil;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.tasks.Ant.AntInstallation;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import jenkins.model.Jenkins;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.jvnet.hudson.test.Email;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.SmokeTest;
import org.jvnet.hudson.test.recipes.LocalData;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
@Category(SmokeTest.class)
public class HudsonTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Tests the basic UI sanity and HtmlUnit set up.
     */
    @Test
    public void globalConfigRoundtrip() throws Exception {
        j.jenkins.setQuietPeriod(10);
        j.jenkins.setScmCheckoutRetryCount(9);
        j.jenkins.setNumExecutors(8);
        j.configRoundtrip();
        Assert.assertEquals(10, j.jenkins.getQuietPeriod());
        Assert.assertEquals(9, j.jenkins.getScmCheckoutRetryCount());
        Assert.assertEquals(8, j.jenkins.getNumExecutors());
    }

    /**
     * Performs a very basic round-trip of a non-empty system configuration screen.
     * This makes sure that the structured form submission is working (to some limited extent.)
     */
    @Test
    @LocalData
    @Email("http://www.nabble.com/Hudson.configure-calling-deprecated-Descriptor.configure-td19051815.html")
    public void simpleConfigSubmit() throws Exception {
        // just load the page and resubmit
        HtmlPage configPage = j.createWebClient().goTo("configure");
        HtmlForm form = configPage.getFormByName("config");
        j.submit(form);
        // Load tools page and resubmit too
        HtmlPage toolsConfigPage = j.createWebClient().goTo("configureTools");
        HtmlForm toolsForm = toolsConfigPage.getFormByName("config");
        j.submit(toolsForm);
        // make sure all the pieces are intact
        Assert.assertEquals(2, j.jenkins.getNumExecutors());
        Assert.assertSame(NORMAL, j.jenkins.getMode());
        Assert.assertSame(NO_AUTHENTICATION, j.jenkins.getSecurityRealm());
        Assert.assertSame(UNSECURED, j.jenkins.getAuthorizationStrategy());
        Assert.assertEquals(5, j.jenkins.getQuietPeriod());
        List<JDK> jdks = j.jenkins.getJDKs();
        Assert.assertEquals(3, jdks.size());// Hudson adds one more

        assertJDK(jdks.get(0), "jdk1", "/tmp");
        assertJDK(jdks.get(1), "jdk2", "/tmp");
        AntInstallation[] ants = j.jenkins.getDescriptorByType(DescriptorImpl.class).getInstallations();
        Assert.assertEquals(2, ants.length);
        assertAnt(ants[0], "ant1", "/tmp");
        assertAnt(ants[1], "ant2", "/tmp");
    }

    /**
     * Makes sure that the search index includes job names.
     *
     * @see SearchTest#testFailure
    This test makes sure that a failure will result in an exception
     */
    @Test
    public void searchIndex() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        Page jobPage = j.search(p.getName());
        URL url = jobPage.getUrl();
        System.out.println(url);
        Assert.assertTrue(url.getPath().endsWith((("/job/" + (p.getName())) + "/")));
    }

    /**
     * Top page should only have one item in the breadcrumb.
     */
    @Test
    public void breadcrumb() throws Exception {
        HtmlPage root = j.createWebClient().goTo("");
        DomElement navbar = root.getElementById("breadcrumbs");
        Assert.assertEquals(1, DomNodeUtil.selectNodes(navbar, "LI/A").size());
    }

    /**
     * Configure link from "/computer/(master)/" should work.
     */
    @Test
    @Email("http://www.nabble.com/Master-slave-refactor-td21361880.html")
    public void computerConfigureLink() throws Exception {
        HtmlPage page = j.createWebClient().goTo("computer/(master)/configure");
        j.submit(page.getFormByName("config"));
    }

    /**
     * Configure link from "/computer/(master)/" should work.
     */
    @Test
    @Email("http://www.nabble.com/Master-slave-refactor-td21361880.html")
    public void deleteHudsonComputer() throws Exception {
        WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo("computer/(master)/");
        for (HtmlAnchor a : page.getAnchors()) {
            Assert.assertFalse(a.getHrefAttribute(), a.getHrefAttribute().endsWith("delete"));
        }
        wc.setThrowExceptionOnFailingStatusCode(false);
        // try to delete it by hitting the final URL directly
        WebRequest req = new WebRequest(new URL(((wc.getContextPath()) + "computer/(master)/doDelete")), HttpMethod.POST);
        page = wc.getPage(wc.addCrumb(req));
        Assert.assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, page.getWebResponse().getStatusCode());
        // the master computer object should be still here
        page = wc.goTo("computer/(master)/");
        Assert.assertEquals(HttpURLConnection.HTTP_OK, page.getWebResponse().getStatusCode());
    }

    /**
     * Legacy descriptors should be visible in the /descriptor/xyz URL.
     */
    @Test
    @Email("http://www.nabble.com/1.286-version-and-description-The-requested-resource-%28%29-is-not--available.-td22233801.html")
    public void legacyDescriptorLookup() throws Exception {
        Descriptor dummy = new Descriptor(HudsonTest.class) {};
        PUBLISHERS.addRecorder(dummy);
        Assert.assertSame(dummy, j.jenkins.getDescriptor(HudsonTest.class.getName()));
        PUBLISHERS.remove(dummy);
        Assert.assertNull(j.jenkins.getDescriptor(HudsonTest.class.getName()));
    }

    /**
     * Verify null/invalid primaryView setting doesn't result in infinite loop.
     */
    @Test
    @Issue("JENKINS-6938")
    public void invalidPrimaryView() throws Exception {
        Field pv = Jenkins.class.getDeclaredField("primaryView");
        pv.setAccessible(true);
        String value = null;
        pv.set(j.jenkins, value);
        Assert.assertNull("null primaryView", j.jenkins.getView(value));
        value = "some bogus name";
        pv.set(j.jenkins, value);
        Assert.assertNull("invalid primaryView", j.jenkins.getView(value));
    }
}

