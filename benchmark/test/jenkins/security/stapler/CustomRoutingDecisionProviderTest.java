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
package jenkins.security.stapler;


import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import hudson.model.UnprotectedRootAction;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.WebMethod;

import static Decision.REJECTED;
import static Decision.UNKNOWN;


@Issue("SECURITY-400")
@For(RoutingDecisionProvider.class)
public class CustomRoutingDecisionProviderTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @TestExtension("customRoutingWhitelistProvider")
    public static class XxxBlacklister extends RoutingDecisionProvider {
        @Override
        public Decision decide(@Nonnull
        String signature) {
            if (signature.contains("xxx")) {
                return REJECTED;
            }
            return UNKNOWN;
        }
    }

    @TestExtension
    public static class OneMethodIsBlacklisted implements UnprotectedRootAction {
        @Override
        @CheckForNull
        public String getUrlName() {
            return "custom";
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        public StaplerAbstractTest.Renderable getLegitGetter() {
            return new StaplerAbstractTest.Renderable();
        }

        public StaplerAbstractTest.Renderable getLegitxxxGetter() {
            return new StaplerAbstractTest.Renderable();
        }
    }

    private static class Renderable {
        public void doIndex() {
            CustomRoutingDecisionProviderTest.replyOk();
        }

        @WebMethod(name = "valid")
        public void valid() {
            CustomRoutingDecisionProviderTest.replyOk();
        }
    }

    @Test
    public void customRoutingWhitelistProvider() throws Exception {
        Page okPage = j.createWebClient().goTo("custom/legitGetter", null);
        Assert.assertThat(okPage.getWebResponse().getStatusCode(), CoreMatchers.is(200));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        Page errorPage = wc.goTo("custom/legitxxxGetter", null);
        Assert.assertThat(errorPage.getWebResponse().getStatusCode(), CoreMatchers.is(404));
    }
}

