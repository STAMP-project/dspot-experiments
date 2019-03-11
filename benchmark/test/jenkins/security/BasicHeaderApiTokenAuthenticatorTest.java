/**
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
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
package jenkins.security;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import hudson.ExtensionComponent;
import hudson.model.User;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;
import jenkins.ExtensionFilter;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RestartableJenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class BasicHeaderApiTokenAuthenticatorTest {
    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    @Issue("SECURITY-896")
    public void legacyToken_regularCase() {
        AtomicReference<String> token = new AtomicReference<>();
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                enableLegacyTokenGenerationOnUserCreation();
                configureSecurity();
                {
                    JenkinsRule.WebClient wc = rr.j.createWebClient();
                    // default SecurityListener will save the user when adding the LastGrantedAuthoritiesProperty
                    // and so the user is persisted
                    wc.login("user1");
                    HtmlPage page = wc.goTo("user/user1/configure");
                    String tokenValue = getText();
                    token.set(tokenValue);
                }
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                User user = User.getById("user1", false);
                Assert.assertNotNull(user);
                JenkinsRule.WebClient wc = rr.j.createWebClient();
                wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
                {
                    // for invalid token, no effect
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user1", "invalid-token"));
                    Assert.assertThat(wc.getPage(request).getWebResponse().getStatusCode(), Matchers.equalTo(401));
                }
                {
                    // for invalid user, no effect
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user-not-valid", token.get()));
                    Assert.assertThat(wc.getPage(request).getWebResponse().getStatusCode(), Matchers.equalTo(401));
                }
                Assert.assertNull(User.getById("user-not-valid", false));
                {
                    // valid user with valid token, ok
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user1", token.get()));
                    XmlPage xmlPage = wc.getPage(request);
                    Assert.assertThat(xmlPage, hasXPath("//name", Matchers.is("user1")));
                }
            }
        });
    }

    /* The user is not saved after login without the default SecurityListener#fireAuthenticated */
    @Test
    @Issue("SECURITY-896")
    public void legacyToken_withoutLastGrantedAuthorities() {
        AtomicReference<String> token = new AtomicReference<>();
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                enableLegacyTokenGenerationOnUserCreation();
                configureSecurity();
                {
                    JenkinsRule.WebClient wc = rr.j.createWebClient();
                    wc.login("user1");
                    HtmlPage page = wc.goTo("user/user1/configure");
                    String tokenValue = getText();
                    token.set(tokenValue);
                }
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                User user = User.getById("user1", false);
                Assert.assertNull(user);
                JenkinsRule.WebClient wc = rr.j.createWebClient();
                wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
                {
                    // for invalid token, no effect
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user1", "invalid-token"));
                    Assert.assertThat(wc.getPage(request).getWebResponse().getStatusCode(), Matchers.equalTo(401));
                }
                {
                    // for invalid user, no effect
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user-not-valid", token.get()));
                    Assert.assertThat(wc.getPage(request).getWebResponse().getStatusCode(), Matchers.equalTo(401));
                }
                Assert.assertNull(User.getById("user1", false));
                Assert.assertNull(User.getById("user-not-valid", false));
                {
                    // valid user with valid token, ok
                    WebRequest request = new WebRequest(new URL(((rr.j.jenkins.getRootUrl()) + "whoAmI/api/xml")));
                    request.setAdditionalHeader("Authorization", base64("user1", token.get()));
                    XmlPage xmlPage = wc.getPage(request);
                    Assert.assertThat(xmlPage, hasXPath("//name", Matchers.is("user1")));
                }
            }
        });
        rr.addStep(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                User user = User.getById("user1", false);
                Assert.assertNull(user);
            }
        });
    }

    @TestExtension("legacyToken_withoutLastGrantedAuthorities")
    public static class RemoveDefaultSecurityListener extends ExtensionFilter {
        @Override
        public <T> boolean allows(Class<T> type, ExtensionComponent<T> component) {
            return !(SecurityListener.class.isAssignableFrom(type));
        }
    }
}

