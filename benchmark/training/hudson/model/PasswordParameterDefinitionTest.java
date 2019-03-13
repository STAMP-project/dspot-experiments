/**
 * The MIT License
 *
 * Copyright 2013 Jesse Glick.
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


import Item.BUILD;
import Jenkins.ADMINISTER;
import Jenkins.READ;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import hudson.Launcher;
import java.io.IOException;
import jenkins.security.apitoken.ApiTokenTestHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.TestBuilder;


public class PasswordParameterDefinitionTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void defaultValueKeptSecret() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new PasswordParameterDefinition("p", "s3cr3t", "")));
        j.configRoundtrip(p);
        Assert.assertEquals("s3cr3t", getDefaultValue());
    }

    @Issue("JENKINS-36476")
    @Test
    public void defaultValueAlwaysAvailable() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        j.jenkins.setSecurityRealm(j.createDummySecurityRealm());
        j.jenkins.setAuthorizationStrategy(new MockAuthorizationStrategy().grant(ADMINISTER).everywhere().to("admin").grant(READ, Item.READ, BUILD).everywhere().to("dev"));
        FreeStyleProject p = j.createFreeStyleProject();
        p.addProperty(new ParametersDefinitionProperty(new PasswordParameterDefinition("secret", "s3cr3t", "")));
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().println((("I heard about a " + (build.getEnvironment(listener).get("secret"))) + "!"));
                return true;
            }
        });
        User admin = User.getById("admin", true);
        User dev = User.getById("dev", true);
        JenkinsRule.WebClient wc = // ParametersDefinitionProperty/index.jelly sends a 405 but really it is OK
        j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        // Control case: admin can use default value.
        j.submit(wc.withBasicApiToken(admin).getPage(p, "build?delay=0sec").getFormByName("parameters"));
        j.waitUntilNoActivity();
        FreeStyleBuild b1 = p.getLastBuild();
        Assert.assertEquals(1, b1.getNumber());
        j.assertLogContains("I heard about a s3cr3t!", j.assertBuildStatusSuccess(b1));
        // Another control case: anyone can enter a different value.
        HtmlForm form = wc.withBasicApiToken(dev).getPage(p, "build?delay=0sec").getFormByName("parameters");
        HtmlPasswordInput input = form.getInputByName("value");
        input.setText("rumor");
        j.submit(form);
        j.waitUntilNoActivity();
        FreeStyleBuild b2 = p.getLastBuild();
        Assert.assertEquals(2, b2.getNumber());
        j.assertLogContains("I heard about a rumor!", j.assertBuildStatusSuccess(b2));
        // Test case: anyone can use default value.
        j.submit(wc.withBasicApiToken(dev).getPage(p, "build?delay=0sec").getFormByName("parameters"));
        j.waitUntilNoActivity();
        FreeStyleBuild b3 = p.getLastBuild();
        Assert.assertEquals(3, b3.getNumber());
        j.assertLogContains("I heard about a s3cr3t!", j.assertBuildStatusSuccess(b3));
        // Another control case: blank values.
        form = wc.withBasicApiToken(dev).getPage(p, "build?delay=0sec").getFormByName("parameters");
        input = form.getInputByName("value");
        input.setText("");
        j.submit(form);
        j.waitUntilNoActivity();
        FreeStyleBuild b4 = p.getLastBuild();
        Assert.assertEquals(4, b4.getNumber());
        j.assertLogContains("I heard about a !", j.assertBuildStatusSuccess(b4));
    }
}

