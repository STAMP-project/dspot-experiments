/**
 * The MIT License
 *
 * Copyright (c) 2017, CloudBees, Inc.
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


import com.gargoylesoftware.htmlunit.WebResponse;
import hudson.security.captcha.CaptchaSupport;
import java.io.IOException;
import java.io.OutputStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class SecurityRealmTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-43852")
    public void testCacheHeaderInResponse() throws Exception {
        SecurityRealm securityRealm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(securityRealm);
        WebResponse response = j.createWebClient().goTo("securityRealm/captcha", "").getWebResponse();
        Assert.assertEquals(response.getContentAsString(), "");
        securityRealm.setCaptchaSupport(new SecurityRealmTest.DummyCaptcha());
        response = j.createWebClient().goTo("securityRealm/captcha", "image/png").getWebResponse();
        Assert.assertThat(response.getResponseHeaderValue("Cache-Control"), CoreMatchers.is("no-cache, no-store, must-revalidate"));
        Assert.assertThat(response.getResponseHeaderValue("Pragma"), CoreMatchers.is("no-cache"));
        Assert.assertThat(response.getResponseHeaderValue("Expires"), CoreMatchers.is("0"));
    }

    private class DummyCaptcha extends CaptchaSupport {
        @Override
        public boolean validateCaptcha(String id, String text) {
            return false;
        }

        @Override
        public void generateImage(String id, OutputStream ios) throws IOException {
        }
    }
}

