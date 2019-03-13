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
package hudson.util;


import ACL.SYSTEM;
import CLICommandInvoker.Result;
import Items.XSTREAM2;
import OldDataMonitor.VersionRange;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebRequest;
import hudson.cli.CLICommandInvoker;
import hudson.diagnosis.OldDataMonitor;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.Saveable;
import hudson.model.User;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import jenkins.model.Jenkins;
import jenkins.security.apitoken.ApiTokenTestHelper;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.TestExtension;
import org.jvnet.hudson.test.recipes.LocalData;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


public class RobustReflectionConverterTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Issue("JENKINS-21024")
    @LocalData
    @Test
    public void randomExceptionsReported() throws Exception {
        FreeStyleProject p = r.jenkins.getItemByFullName("j", FreeStyleProject.class);
        Assert.assertNotNull(p);
        Assert.assertEquals(Collections.emptyMap(), p.getTriggers());
        OldDataMonitor odm = ((OldDataMonitor) (r.jenkins.getAdministrativeMonitor("OldData")));
        Map<Saveable, OldDataMonitor.VersionRange> data = odm.getData();
        Assert.assertEquals(Collections.singleton(p), data.keySet());
        String text = data.values().iterator().next().extra;
        Assert.assertTrue(text, text.contains("Could not call hudson.triggers.TimerTrigger.readResolve"));
    }

    // Testing describable object to demonstrate what is expected with RobustReflectionConverter#addCriticalField
    // This should be configured with a specific keyword,
    // and should reject configurations with other keywords.
    // GUI related implementations (@DataBoundConstructor and newInstance) aren't used actually
    // (no jelly files are provides and they don't work actually),
    // but written to clarify a use case.
    public static class AcceptOnlySpecificKeyword extends AbstractDescribableImpl<RobustReflectionConverterTest.AcceptOnlySpecificKeyword> {
        public static final String ACCEPT_KEYWORD = "accept";

        private final String keyword;

        @DataBoundConstructor
        public AcceptOnlySpecificKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getKeyword() {
            return keyword;
        }

        public boolean isAcceptable() {
            return RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD.equals(keyword);
        }

        private Object readResolve() throws Exception {
            if (!(SYSTEM.equals(Jenkins.getAuthentication()))) {
                // called via REST / CLI with authentication
                if (!(isAcceptable())) {
                    // Reject invalid configuration via REST / CLI.
                    throw new Exception(String.format("Bad keyword: %s", getKeyword()));
                }
            }
            return this;
        }

        @TestExtension
        public static class DescriptorImpl extends Descriptor<RobustReflectionConverterTest.AcceptOnlySpecificKeyword> {
            @Override
            public String getDisplayName() {
                return "AcceptOnlySpecificKeyword";
            }

            @Override
            public RobustReflectionConverterTest.AcceptOnlySpecificKeyword newInstance(StaplerRequest req, JSONObject formData) throws FormException {
                RobustReflectionConverterTest.AcceptOnlySpecificKeyword instance = super.newInstance(req, formData);
                if (!(instance.isAcceptable())) {
                    throw new FormException(String.format("Bad keyword: %s", instance.getKeyword()), "keyword");
                }
                return instance;
            }
        }
    }

    public static class KeywordProperty extends JobProperty<Job<?, ?>> {
        private final RobustReflectionConverterTest.AcceptOnlySpecificKeyword nonCriticalField;

        private final RobustReflectionConverterTest.AcceptOnlySpecificKeyword criticalField;

        public KeywordProperty(RobustReflectionConverterTest.AcceptOnlySpecificKeyword nonCriticalField, RobustReflectionConverterTest.AcceptOnlySpecificKeyword criticalField) {
            this.nonCriticalField = nonCriticalField;
            this.criticalField = criticalField;
        }

        public RobustReflectionConverterTest.AcceptOnlySpecificKeyword getNonCriticalField() {
            return nonCriticalField;
        }

        public RobustReflectionConverterTest.AcceptOnlySpecificKeyword getCriticalField() {
            return criticalField;
        }

        @TestExtension
        public static class DescriptorImpl extends JobPropertyDescriptor {
            @Override
            public String getDisplayName() {
                return "KeywordProperty";
            }

            @Override
            public JobProperty<?> newInstance(StaplerRequest req, JSONObject formData) throws FormException {
                // unfortunately, default newInstance bypasses newInstances for members.
                formData = formData.getJSONObject("keywordProperty");
                @SuppressWarnings("unchecked")
                Descriptor<RobustReflectionConverterTest.AcceptOnlySpecificKeyword> d = Jenkins.getInstance().getDescriptor(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.class);
                return new RobustReflectionConverterTest.KeywordProperty(d.newInstance(req, formData.getJSONObject("nonCriticalField")), d.newInstance(req, formData.getJSONObject("criticalField")));
            }
        }
    }

    private static final String CONFIGURATION_TEMPLATE = "<?xml version='1.1' encoding='UTF-8'?>" + ((((((((((("<project>" + "<properties>") + "<hudson.util.RobustReflectionConverterTest_-KeywordProperty>") + "<nonCriticalField>") + "<keyword>%s</keyword>") + "</nonCriticalField>") + "<criticalField>") + "<keyword>%s</keyword>") + "</criticalField>") + "</hudson.util.RobustReflectionConverterTest_-KeywordProperty>") + "</properties>") + "</project>");

    @Test
    public void testRestInterfaceFailure() throws Exception {
        ApiTokenTestHelper.enableLegacyBehavior();
        XSTREAM2.addCriticalField(RobustReflectionConverterTest.KeywordProperty.class, "criticalField");
        User test = User.getById("test", true);
        // without addCriticalField. This is accepted.
        {
            FreeStyleProject p = r.createFreeStyleProject();
            p.addProperty(new RobustReflectionConverterTest.KeywordProperty(new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD), new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD)));
            p.save();
            // Configure a bad keyword via REST.
            r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
            WebClient wc = r.createWebClient();
            wc.withBasicApiToken(test);
            WebRequest req = new WebRequest(new URL(((wc.getContextPath()) + (String.format("%s/config.xml", p.getUrl())))), HttpMethod.POST);
            req.setEncodingType(null);
            req.setRequestBody(String.format(RobustReflectionConverterTest.CONFIGURATION_TEMPLATE, "badvalue", RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD));
            wc.getPage(req);
            // AcceptOnlySpecificKeyword with bad value is not instantiated for rejected with readResolve,
            Assert.assertNull(p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getNonCriticalField());
            Assert.assertEquals(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD, p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
            // but save to the disk.
            r.jenkins.reload();
            p = r.jenkins.getItemByFullName(p.getFullName(), FreeStyleProject.class);
            Assert.assertEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getNonCriticalField().getKeyword());
            Assert.assertEquals(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD, p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
        }
        // with addCriticalField. This is not accepted.
        {
            FreeStyleProject p = r.createFreeStyleProject();
            p.addProperty(new RobustReflectionConverterTest.KeywordProperty(new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD), new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD)));
            p.save();
            // Configure a bad keyword via REST.
            r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
            WebClient wc = r.createWebClient().withThrowExceptionOnFailingStatusCode(false);
            wc.withBasicApiToken(test);
            WebRequest req = new WebRequest(new URL(((wc.getContextPath()) + (String.format("%s/config.xml", p.getUrl())))), HttpMethod.POST);
            req.setEncodingType(null);
            req.setRequestBody(String.format(RobustReflectionConverterTest.CONFIGURATION_TEMPLATE, RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD, "badvalue"));
            Page page = wc.getPage(req);
            Assert.assertEquals("Submitting unacceptable configuration via REST should fail.", HttpURLConnection.HTTP_INTERNAL_ERROR, page.getWebResponse().getStatusCode());
            // Configuration should not be updated for a failure of the critical field,
            Assert.assertNotEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
            r.jenkins.reload();
            // rejected configuration is not saved
            p = r.jenkins.getItemByFullName(p.getFullName(), FreeStyleProject.class);
            Assert.assertNotEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
        }
    }

    @Test
    public void testCliFailure() throws Exception {
        XSTREAM2.addCriticalField(RobustReflectionConverterTest.KeywordProperty.class, "criticalField");
        // without addCriticalField. This is accepted.
        {
            FreeStyleProject p = r.createFreeStyleProject();
            p.addProperty(new RobustReflectionConverterTest.KeywordProperty(new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD), new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD)));
            p.save();
            // Configure a bad keyword via CLI.
            r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
            CLICommandInvoker.Result ret = asUser("test").withStdin(new ByteArrayInputStream(String.format(RobustReflectionConverterTest.CONFIGURATION_TEMPLATE, "badvalue", RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD).getBytes())).withArgs(p.getFullName()).invoke();
            Assert.assertEquals(0, ret.returnCode());
            // AcceptOnlySpecificKeyword with bad value is not instantiated for rejected with readResolve,
            Assert.assertNull(p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getNonCriticalField());
            Assert.assertEquals(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD, p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
            // but save to the disk.
            r.jenkins.reload();
            p = r.jenkins.getItemByFullName(p.getFullName(), FreeStyleProject.class);
            Assert.assertEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getNonCriticalField().getKeyword());
        }
        // with addCriticalField. This is not accepted.
        {
            FreeStyleProject p = r.createFreeStyleProject();
            p.addProperty(new RobustReflectionConverterTest.KeywordProperty(new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD), new RobustReflectionConverterTest.AcceptOnlySpecificKeyword(RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD)));
            p.save();
            // Configure a bad keyword via CLI.
            r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
            CLICommandInvoker.Result ret = asUser("test").withStdin(new ByteArrayInputStream(String.format(RobustReflectionConverterTest.CONFIGURATION_TEMPLATE, RobustReflectionConverterTest.AcceptOnlySpecificKeyword.ACCEPT_KEYWORD, "badvalue").getBytes())).withArgs(p.getFullName()).invoke();
            Assert.assertNotEquals(0, ret.returnCode());
            // Configuration should not be updated for a failure of the critical field,
            Assert.assertNotEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
            r.jenkins.reload();
            // rejected configuration is not saved
            p = r.jenkins.getItemByFullName(p.getFullName(), FreeStyleProject.class);
            Assert.assertNotEquals("badvalue", p.getProperty(RobustReflectionConverterTest.KeywordProperty.class).getCriticalField().getKeyword());
        }
    }
}

