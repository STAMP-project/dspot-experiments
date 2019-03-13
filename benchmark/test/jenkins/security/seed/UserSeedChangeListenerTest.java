/**
 * The MIT License
 *
 * Copyright (c) 2018 CloudBees, Inc.
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
package jenkins.security.seed;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import hudson.model.User;
import java.net.URL;
import javax.annotation.Nonnull;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;


public class UserSeedChangeListenerTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    {
        j.timeout = 0;
    }

    @Test
    public void onProgrammaticUserSeedChange_listenerTriggered() throws Exception {
        UserSeedChangeListenerTest.TestUserSeedChangeListener testListener = j.jenkins.getExtensionList(UserSeedChangeListener.class).get(UserSeedChangeListenerTest.TestUserSeedChangeListener.class);
        String userId = "alice";
        User alice = User.getById(userId, true);
        Assert.assertNull(testListener.lastUserIdReceived);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        Assert.assertNull(testListener.lastUserIdReceived);
        userSeed.renewSeed();
        Assert.assertThat(testListener.lastUserIdReceived, CoreMatchers.is(userId));
        Assert.assertThat(testListener.userWasNull, CoreMatchers.is(false));
    }

    @Test
    public void onWebCallUserSeedChange_listenerTriggered() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        UserSeedChangeListenerTest.TestUserSeedChangeListener testListener = j.jenkins.getExtensionList(UserSeedChangeListener.class).get(UserSeedChangeListenerTest.TestUserSeedChangeListener.class);
        String userId = "alice";
        User alice = User.getById(userId, true);
        UserSeedProperty userSeed = alice.getProperty(UserSeedProperty.class);
        JenkinsRule.WebClient wc = j.createWebClient();
        WebRequest webRequest = new WebRequest(new URL((((((j.getURL()) + (alice.getUrl())) + "/") + (userSeed.getDescriptor().getDescriptorUrl())) + "/renewSessionSeed")), HttpMethod.POST);
        Assert.assertNull(testListener.lastUserIdReceived);
        wc.getPage(webRequest);
        Assert.assertThat(testListener.lastUserIdReceived, CoreMatchers.is(userId));
        Assert.assertThat(testListener.userWasNull, CoreMatchers.is(false));
    }

    @TestExtension
    public static class TestUserSeedChangeListener extends UserSeedChangeListener {
        String lastUserIdReceived;

        boolean userWasNull;

        @Override
        public void onUserSeedRenewed(@Nonnull
        User user) {
            if (user == null) {
                userWasNull = true;
            }
            lastUserIdReceived = user.getId();
        }
    }
}

