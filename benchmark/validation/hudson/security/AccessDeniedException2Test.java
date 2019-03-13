/**
 * The MIT License
 *
 * Copyright 2017 CloudBees, Inc.
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
package hudson.security;


import JenkinsRule.DummySecurityRealm;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import java.net.HttpURLConnection;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;


public class AccessDeniedException2Test {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Issue("JENKINS-39402")
    @Test
    public void youAreInGroupHeaders() throws Exception {
        JenkinsRule.DummySecurityRealm realm = r.createDummySecurityRealm();
        String[] groups = new String[1000];
        for (int i = 0; i < (groups.length); i++) {
            groups[i] = "group" + i;
        }
        realm.addGroups("user", groups);
        r.jenkins.setSecurityRealm(realm);
        r.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy());
        try {
            r.createWebClient().login("user");
            Assert.fail("should not have been allowed to access anything");
        } catch (FailingHttpStatusCodeException x) {
            Assert.assertEquals(HttpURLConnection.HTTP_FORBIDDEN, x.getStatusCode());
            Assert.assertNotNull(x.getResponse().getResponseHeaderValue("X-You-Are-In-Group-Disabled"));
        }
    }
}

