/**
 * Copyright (c) 2008-2010 Yahoo! Inc.
 * All rights reserved.
 * The copyrights to the contents of this file are licensed under the MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
package hudson.security.csrf;


import PresetData.DataSet;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.net.HttpURLConnection;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.recipes.PresetData;


/**
 *
 *
 * @author dty
 */
public class DefaultCrumbIssuerTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    private static final String[] testData = new String[]{ "10.2.3.1", "10.2.3.1,10.20.30.40", "10.2.3.1,10.20.30.41", "10.2.3.3,10.20.30.40,10.20.30.41" };

    private static final String HEADER_NAME = "X-Forwarded-For";

    @Issue("JENKINS-3854")
    @Test
    public void clientIPFromHeader() throws Exception {
        WebClient wc = r.createWebClient();
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[0]);
        HtmlPage p = wc.goTo("configure");
        r.submit(p.getFormByName("config"));
    }

    @Issue("JENKINS-3854")
    @Test
    public void headerChange() throws Exception {
        WebClient wc = r.createWebClient();
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[0]);
        HtmlPage p = wc.goTo("configure");
        wc.removeRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME);
        wc.setThrowExceptionOnFailingStatusCode(false);
        // The crumb should no longer match if we remove the proxy info
        Page page = r.submit(p.getFormByName("config"));
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, page.getWebResponse().getStatusCode());
    }

    @Issue("JENKINS-3854")
    @Test
    public void proxyIPChanged() throws Exception {
        WebClient wc = r.createWebClient();
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[1]);
        HtmlPage p = wc.goTo("configure");
        wc.removeRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME);
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[2]);
        // The crumb should be the same even if the proxy IP changes
        r.submit(p.getFormByName("config"));
    }

    @Issue("JENKINS-3854")
    @Test
    public void proxyIPChain() throws Exception {
        WebClient wc = r.createWebClient();
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[3]);
        HtmlPage p = wc.goTo("configure");
        r.submit(p.getFormByName("config"));
    }

    @Issue("JENKINS-7518")
    @Test
    public void proxyCompatibilityMode() throws Exception {
        CrumbIssuer issuer = new DefaultCrumbIssuer(true);
        Assert.assertNotNull(issuer);
        r.jenkins.setCrumbIssuer(issuer);
        WebClient wc = r.createWebClient();
        wc.addRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME, DefaultCrumbIssuerTest.testData[0]);
        HtmlPage p = wc.goTo("configure");
        wc.removeRequestHeader(DefaultCrumbIssuerTest.HEADER_NAME);
        // The crumb should still match if we remove the proxy info
        r.submit(p.getFormByName("config"));
    }

    @PresetData(DataSet.ANONYMOUS_READONLY)
    @Test
    public void apiXml() throws Exception {
        WebClient wc = r.createWebClient();
        r.assertXPathValue(wc.goToXml("crumbIssuer/api/xml"), "//crumbRequestField", r.jenkins.getCrumbIssuer().getCrumbRequestField());
        String text = wc.goTo("crumbIssuer/api/xml?xpath=concat(//crumbRequestField,'=',//crumb)", "text/plain").getWebResponse().getContentAsString();
        Assert.assertTrue(text, text.matches((("\\Q" + (r.jenkins.getCrumbIssuer().getCrumbRequestField())) + "\\E=[0-9a-f]+")));
        text = wc.goTo("crumbIssuer/api/xml?xpath=concat(//crumbRequestField,\":\",//crumb)", "text/plain").getWebResponse().getContentAsString();
        Assert.assertTrue(text, text.matches((("\\Q" + (r.jenkins.getCrumbIssuer().getCrumbRequestField())) + "\\E:[0-9a-f]+")));
        text = wc.goTo("crumbIssuer/api/xml?xpath=/*/crumbRequestField/text()", "text/plain").getWebResponse().getContentAsString();
        Assert.assertEquals(r.jenkins.getCrumbIssuer().getCrumbRequestField(), text);
        text = wc.goTo("crumbIssuer/api/xml?xpath=/*/crumb/text()", "text/plain").getWebResponse().getContentAsString();
        Assert.assertTrue(text, text.matches("[0-9a-f]+"));
        wc.assertFails("crumbIssuer/api/xml?xpath=concat(\'hack=\"\',//crumb,\'\"\')", HttpURLConnection.HTTP_FORBIDDEN);
        wc.assertFails("crumbIssuer/api/xml?xpath=concat(\"hack=\'\",//crumb,\"\'\")", HttpURLConnection.HTTP_FORBIDDEN);
        wc.assertFails("crumbIssuer/api/xml?xpath=concat('{',//crumb,':1}')", HttpURLConnection.HTTP_FORBIDDEN);// 37.5% chance that crumb ~ /[a-f].+/

        wc.assertFails("crumbIssuer/api/xml?xpath=concat('hack.',//crumb,'=1')", HttpURLConnection.HTTP_FORBIDDEN);// ditto

        r.jenkins.getCrumbIssuer().getDescriptor().setCrumbRequestField("_crumb");
        wc.assertFails("crumbIssuer/api/xml?xpath=concat(//crumbRequestField,'=',//crumb)", HttpURLConnection.HTTP_FORBIDDEN);// perhaps interpretable as JS number

    }

    @PresetData(DataSet.ANONYMOUS_READONLY)
    @Test
    public void apiJson() throws Exception {
        WebClient wc = r.createWebClient();
        String json = wc.goTo("crumbIssuer/api/json", "application/json").getWebResponse().getContentAsString();
        JSONObject jsonObject = JSONObject.fromObject(json);
        Assert.assertEquals(r.jenkins.getCrumbIssuer().getCrumbRequestField(), jsonObject.getString("crumbRequestField"));
        Assert.assertTrue(jsonObject.getString("crumb").matches("[0-9a-f]+"));
        wc.assertFails("crumbIssuer/api/json?jsonp=hack", HttpURLConnection.HTTP_FORBIDDEN);
    }

    @Issue("JENKINS-34254")
    @Test
    public void testRequirePostErrorPageCrumb() throws Exception {
        r.jenkins.setCrumbIssuer(new DefaultCrumbIssuer(false));
        WebClient wc = r.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        Page page = wc.goTo("quietDown");
        Assert.assertEquals("expect HTTP 405 method not allowed", HttpURLConnection.HTTP_BAD_METHOD, page.getWebResponse().getStatusCode());
        HtmlPage retry = ((HtmlPage) (wc.getCurrentWindow().getEnclosedPage()));
        HtmlPage success = r.submit(retry.getFormByName("retry"));
        Assert.assertEquals(HttpURLConnection.HTTP_OK, success.getWebResponse().getStatusCode());
        Assert.assertTrue("quieting down", r.jenkins.isQuietingDown());
    }
}

