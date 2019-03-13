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
package jenkins.security.apitoken;


import hudson.model.User;
import jenkins.security.ApiTokenProperty;
import jenkins.security.Messages;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;


public class ApiTokenPropertyConfigurationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("JENKINS-32776")
    public void newUserTokenConfiguration() throws Exception {
        ApiTokenPropertyConfiguration config = ApiTokenPropertyConfiguration.get();
        config.setTokenGenerationOnCreationEnabled(true);
        {
            User userWith = User.getById("userWith", true);
            ApiTokenProperty withToken = userWith.getProperty(ApiTokenProperty.class);
            Assert.assertTrue(withToken.hasLegacyToken());
            Assert.assertEquals(1, withToken.getTokenList().size());
            String tokenValue = withToken.getApiToken();
            Assert.assertNotEquals(Messages.ApiTokenProperty_NoLegacyToken(), tokenValue);
        }
        config.setTokenGenerationOnCreationEnabled(false);
        {
            User userWithout = User.getById("userWithout", true);
            ApiTokenProperty withoutToken = userWithout.getProperty(ApiTokenProperty.class);
            Assert.assertFalse(withoutToken.hasLegacyToken());
            Assert.assertEquals(0, withoutToken.getTokenList().size());
            String tokenValue = withoutToken.getApiToken();
            Assert.assertEquals(Messages.ApiTokenProperty_NoLegacyToken(), tokenValue);
        }
    }
}

