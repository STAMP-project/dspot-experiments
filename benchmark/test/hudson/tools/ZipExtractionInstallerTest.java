/**
 * The MIT License
 *
 * Copyright 2018 CloudBees, Inc.
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
package hudson.tools;


import Jenkins.ADMINISTER;
import Jenkins.READ;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptEngine;
import com.gargoylesoftware.htmlunit.javascript.host.xml.XMLHttpRequest;
import hudson.model.JDK;
import hudson.model.User;
import hudson.util.FormValidation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sourceforge.htmlunit.corejs.javascript.Function;
import net.sourceforge.htmlunit.corejs.javascript.Scriptable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;


public class ZipExtractionInstallerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    @Issue("SECURITY-794")
    public void onlyAdminCanReachTheDoCheck() throws Exception {
        final String ADMIN = "admin";
        final String USER = "user";
        j.jenkins.setCrumbIssuer(null);
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to(ADMIN).grant(READ).everywhere().to(USER));
        User.getById(ADMIN, true);
        User.getById(USER, true);
        WebRequest request = new WebRequest(new URL(((j.getURL()) + "descriptorByName/hudson.tools.ZipExtractionInstaller/checkUrl")), HttpMethod.POST);
        request.setRequestBody(URLEncoder.encode("value=https://www.google.com", StandardCharsets.UTF_8.name()));
        JenkinsRule.WebClient adminWc = j.createWebClient();
        adminWc.login(ADMIN);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, adminWc.getPage(request).getWebResponse().getStatusCode());
        JenkinsRule.WebClient userWc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        userWc.login(USER);
        Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, userWc.getPage(request).getWebResponse().getStatusCode());
    }

    @Test
    @Issue("SECURITY-794")
    public void roundtrip() throws Exception {
        final String VALID_URL = "https://www.google.com";
        final String INVALID_URL = "only-crappy-letters";
        ZipExtractionInstaller installer = new ZipExtractionInstaller("", VALID_URL, "");
        j.jenkins.getJDKs().add(new JDK("test", tmp.getRoot().getAbsolutePath(), Arrays.asList(new InstallSourceProperty(Arrays.<ToolInstaller>asList(installer)))));
        JenkinsRule.WebClient wc = j.createWebClient();
        ZipExtractionInstallerTest.SpyingJavaScriptEngine jsEngine = new ZipExtractionInstallerTest.SpyingJavaScriptEngine(wc, "ZipExtractionInstaller/checkUrl", HttpMethod.POST);
        wc.setJavaScriptEngine(jsEngine);
        HtmlPage page = wc.goTo("configureTools");
        XMLHttpRequest lastRequest = jsEngine.getLastRequest();
        String body = URLDecoder.decode(ZipExtractionInstallerTest.getPrivateWebRequestField(lastRequest).getRequestBody(), "UTF-8");
        Assert.assertThat(body, Matchers.containsString(VALID_URL));
        Assert.assertEquals(FormValidation.ok().renderHtml(), lastRequest.getResponseText());
        HtmlTextInput urlInput = page.getDocumentElement().getOneHtmlElementByAttribute("input", "value", VALID_URL);
        urlInput.setAttribute("value", INVALID_URL);
        j.submit(page.getFormByName("config"));
        JDK jdk = j.jenkins.getJDK("test");
        InstallSourceProperty isp = jdk.getProperties().get(InstallSourceProperty.class);
        Assert.assertEquals(1, isp.installers.size());
        Assert.assertEquals(INVALID_URL, isp.installers.get(ZipExtractionInstaller.class).getUrl());
        wc.goTo("configureTools");
        lastRequest = jsEngine.getLastRequest();
        body = URLDecoder.decode(ZipExtractionInstallerTest.getPrivateWebRequestField(lastRequest).getRequestBody(), "UTF-8");
        Assert.assertThat(body, Matchers.containsString(INVALID_URL));
        Assert.assertThat(lastRequest.getResponseText(), Matchers.containsString(Messages.ZipExtractionInstaller_malformed_url()));
    }

    private class SpyingJavaScriptEngine extends JavaScriptEngine {
        private List<XMLHttpRequest> storedRequests = new ArrayList<>();

        private String urlToMatch;

        private HttpMethod method;

        SpyingJavaScriptEngine(JenkinsRule.WebClient wc, @Nullable
        String urlToMatch, @Nullable
        HttpMethod method) {
            super(wc);
            this.urlToMatch = urlToMatch;
            this.method = method;
        }

        @Override
        public Object callFunction(HtmlPage page, Function function, Scriptable scope, Scriptable thisObject, Object[] args) {
            if (thisObject instanceof XMLHttpRequest) {
                try {
                    WebRequest request = ZipExtractionInstallerTest.getPrivateWebRequestField(((XMLHttpRequest) (thisObject)));
                    boolean correctUrl = ((urlToMatch) == null) || (request.getUrl().toString().contains(urlToMatch));
                    boolean correctMethod = ((method) == null) || (request.getHttpMethod().equals(method));
                    if (correctUrl && correctMethod) {
                        if ((getReadyState()) == 4) {
                            storedRequests.add(((XMLHttpRequest) (thisObject)));
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return super.callFunction(page, function, scope, thisObject, args);
        }

        @Nonnull
        public XMLHttpRequest getLastRequest() {
            if (storedRequests.isEmpty()) {
                Assert.fail("There is no available requests for the proposed url/method");
            }
            return storedRequests.get(((storedRequests.size()) - 1));
        }
    }
}

