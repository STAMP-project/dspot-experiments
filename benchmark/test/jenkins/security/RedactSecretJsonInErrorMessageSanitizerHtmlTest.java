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
package jenkins.security;


import JenkinsRule.WebClient;
import RedactSecretJsonInErrorMessageSanitizer.INSTANCE;
import RedactSecretJsonInErrorMessageSanitizer.REDACT_KEY;
import RedactSecretJsonInErrorMessageSanitizer.REDACT_VALUE;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.RootAction;
import hudson.util.Secret;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.LoggerRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.interceptor.RequirePOST;


@Restricted(NoExternalUse.class)
public class RedactSecretJsonInErrorMessageSanitizerHtmlTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public LoggerRule logging = new LoggerRule();

    @Test
    @Issue("SECURITY-765")
    public void passwordsAreRedacted_andOtherStayTheSame() throws Exception {
        j.jenkins.setCrumbIssuer(null);
        RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestPassword testPassword = j.jenkins.getExtensionList(RootAction.class).get(RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestPassword.class);
        JenkinsRule.WebClient wc = j.createWebClient();
        HtmlPage page = wc.goTo("test");
        String textSimple = "plain-1";
        String pwdSimple = "secret-1";
        setValueAttribute(textSimple);
        setValueAttribute(pwdSimple);
        String textLevelOne = "plain-2";
        String pwdLevelOneA = "secret-2";
        String pwdLevelOneB = "secret-3";
        setValueAttribute(textLevelOne);
        setValueAttribute(pwdLevelOneA);
        setValueAttribute(pwdLevelOneB);
        HtmlForm form = page.getFormByName("config");
        Page formSubmitPage = j.submit(form);
        Assert.assertThat(formSubmitPage.getWebResponse().getStatusCode(), CoreMatchers.equalTo(200));
        JSONObject rawJson = testPassword.lastJsonReceived;
        String rawJsonToString = rawJson.toString();
        Assert.assertThat(rawJsonToString, CoreMatchers.containsString(textSimple));
        Assert.assertThat(rawJsonToString, CoreMatchers.containsString(pwdSimple));
        Assert.assertThat(rawJsonToString, CoreMatchers.containsString(textLevelOne));
        Assert.assertThat(rawJsonToString, CoreMatchers.containsString(pwdLevelOneA));
        Assert.assertThat(rawJson.getString(REDACT_KEY), CoreMatchers.equalTo("pwd-simple"));
        Assert.assertThat(rawJson.getJSONObject("sub-one").getJSONArray(REDACT_KEY), CoreMatchers.allOf(CoreMatchers.hasItem("pwd-level-one-a"), CoreMatchers.hasItem("pwd-level-one-b")));
        JSONObject redactedJson = INSTANCE.sanitize(rawJson);
        String redactedJsonToString = redactedJson.toString();
        Assert.assertThat(redactedJsonToString, CoreMatchers.containsString(textSimple));
        Assert.assertThat(redactedJsonToString, CoreMatchers.not(CoreMatchers.containsString(pwdSimple)));
        Assert.assertThat(redactedJsonToString, CoreMatchers.containsString(textLevelOne));
        Assert.assertThat(redactedJsonToString, CoreMatchers.not(CoreMatchers.containsString(pwdLevelOneA)));
        Assert.assertThat(redactedJsonToString, CoreMatchers.not(CoreMatchers.containsString(pwdLevelOneB)));
        Assert.assertThat(redactedJsonToString, CoreMatchers.containsString(REDACT_VALUE));
    }

    @TestExtension("passwordsAreRedacted_andOtherStayTheSame")
    public static class TestPassword implements RootAction {
        public JSONObject lastJsonReceived;

        public void doSubmitTest(StaplerRequest req, StaplerResponse res) throws Exception {
            lastJsonReceived = req.getSubmittedForm();
            res.setStatus(200);
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "test";
        }
    }

    @Test
    @Issue("SECURITY-765")
    public void checkSanitizationIsApplied_inDescriptor() throws Exception {
        logging.record("", Level.WARNING).capture(100);
        j.jenkins.setCrumbIssuer(null);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.goTo("testDescribable");
        String secret = "s3cr3t";
        setValueAttribute(secret);
        HtmlForm form = page.getFormByName("config");
        Page formSubmitPage = j.submit(form);
        Assert.assertThat(formSubmitPage.getWebResponse().getContentAsString(), CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
        // check the system log also
        Throwable thrown = logging.getRecords().stream().filter(( r) -> r.getMessage().contains("Error while serving")).findAny().get().getThrown();
        // the exception from Descriptor
        Assert.assertThat(thrown.getCause().getMessage(), CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
        // the exception from RequestImpl
        Assert.assertThat(thrown.getCause().getCause().getMessage(), CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
        StringWriter buffer = new StringWriter();
        thrown.printStackTrace(new PrintWriter(buffer));
        String fullStack = buffer.getBuffer().toString();
        Assert.assertThat(fullStack, CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
    }

    @Test
    @Issue("SECURITY-765")
    public void checkSanitizationIsApplied_inStapler() throws Exception {
        logging.record("", Level.WARNING).capture(100);
        j.jenkins.setCrumbIssuer(null);
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.goTo("testStapler");
        String secret = "s3cr3t";
        setValueAttribute(secret);
        HtmlForm form = page.getFormByName("config");
        Page formSubmitPage = j.submit(form);
        Assert.assertThat(formSubmitPage.getWebResponse().getContentAsString(), CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
        // check the system log also
        Throwable thrown = logging.getRecords().stream().filter(( r) -> r.getMessage().contains("Error while serving")).findAny().get().getThrown();
        // the exception from RequestImpl
        Assert.assertThat(thrown.getCause().getMessage(), CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
        StringWriter buffer = new StringWriter();
        thrown.printStackTrace(new PrintWriter(buffer));
        String fullStack = buffer.getBuffer().toString();
        Assert.assertThat(fullStack, CoreMatchers.allOf(CoreMatchers.containsString(REDACT_VALUE), CoreMatchers.not(CoreMatchers.containsString(secret))));
    }

    public static class TestDescribable implements Describable<RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable> {
        @DataBoundConstructor
        public TestDescribable(Secret password) {
            throw new IllegalArgumentException("Try to steal my password");
        }

        public RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable.DescriptorImpl getDescriptor() {
            return Jenkins.getInstance().getDescriptorByType(RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable.DescriptorImpl.class);
        }

        @TestExtension({ "checkSanitizationIsApplied_inStapler", "checkSanitizationIsApplied_inDescriptor" })
        public static final class DescriptorImpl extends Descriptor<RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable> {}
    }

    @TestExtension("checkSanitizationIsApplied_inDescriptor")
    public static class TestDescribablePage implements RootAction {
        public RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable testDescribable;

        @RequirePOST
        public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws Exception {
            Jenkins.getInstance().getDescriptorOrDie(RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable.class).newInstance(req, req.getSubmittedForm());
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "testDescribable";
        }
    }

    @TestExtension("checkSanitizationIsApplied_inStapler")
    public static class TestStaplerPage implements RootAction {
        public RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable testDescribable;

        @RequirePOST
        public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws Exception {
            req.bindJSON(RedactSecretJsonInErrorMessageSanitizerHtmlTest.TestDescribable.class, req.getSubmittedForm());
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "testStapler";
        }
    }
}

