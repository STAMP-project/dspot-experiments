/**
 * The MIT License
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., Kohsuke Kawaguchi, Yahoo! Inc.
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
package hudson.tasks;


import JenkinsRule.WebClient;
import Result.SUCCESS;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.EnvVars;
import hudson.model.Build;
import hudson.model.Cause.LegacyCodeCause;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.JDK;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.PasswordParameterDefinition;
import hudson.model.StringParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry;
import hudson.tasks.Maven.MavenInstallation;
import hudson.tasks.Maven.MavenInstallation.DescriptorImpl;
import hudson.tools.ToolProperty;
import java.util.Collections;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import jenkins.mvn.DefaultGlobalSettingsProvider;
import jenkins.mvn.DefaultSettingsProvider;
import jenkins.mvn.FilePathGlobalSettingsProvider;
import jenkins.mvn.FilePathSettingsProvider;
import jenkins.mvn.GlobalMavenConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.ExtractResourceSCM;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.ToolInstallations;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class MavenTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    /**
     * Tests the round-tripping of the configuration.
     */
    @Test
    public void configRoundtrip() throws Exception {
        j.jenkins.getDescriptorByType(DescriptorImpl.class).setInstallations();// reset

        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new Maven("a", null, "b.pom", "c=d", "-e", true));
        JenkinsRule.WebClient webClient = j.createWebClient();
        HtmlPage page = webClient.getPage(p, "configure");
        HtmlForm form = page.getFormByName("config");
        j.submit(form);
        Maven m = p.getBuildersList().get(Maven.class);
        Assert.assertNotNull(m);
        Assert.assertEquals("a", m.targets);
        Assert.assertNull(("found " + (m.mavenName)), m.mavenName);
        Assert.assertEquals("b.pom", m.pom);
        Assert.assertEquals("c=d", m.properties);
        Assert.assertEquals("-e", m.jvmOptions);
        Assert.assertTrue(m.usesPrivateRepository());
    }

    @Test
    public void withNodeProperty() throws Exception {
        MavenInstallation maven = ToolInstallations.configureDefaultMaven();
        String mavenHome = maven.getHome();
        String mavenHomeVar = "${VAR_MAVEN}" + (mavenHome.substring(3));
        String mavenVar = mavenHome.substring(0, 3);
        MavenInstallation varMaven = new MavenInstallation("varMaven", mavenHomeVar, JenkinsRule.NO_PROPERTIES);
        j.jenkins.getDescriptorByType(DescriptorImpl.class).setInstallations(maven, varMaven);
        JDK jdk = j.jenkins.getJDK("default");
        String javaHome = jdk.getHome();
        String javaHomeVar = "${VAR_JAVA}" + (javaHome.substring(3));
        String javaVar = javaHome.substring(0, 3);
        JDK varJDK = new JDK("varJDK", javaHomeVar);
        j.jenkins.getJDKs().add(varJDK);
        j.jenkins.getNodeProperties().replaceBy(Collections.singleton(new hudson.slaves.EnvironmentVariablesNodeProperty(new Entry("VAR_MAVEN", mavenVar), new Entry("VAR_JAVA", javaVar))));
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new Maven("--help", varMaven.getName()));
        project.setJDK(varJDK);
        Build<?, ?> build = project.scheduleBuild2(0).get();
        Assert.assertEquals(SUCCESS, build.getResult());
    }

    @Test
    public void withParameter() throws Exception {
        MavenInstallation maven = ToolInstallations.configureDefaultMaven();
        String mavenHome = maven.getHome();
        String mavenHomeVar = "${VAR_MAVEN}" + (mavenHome.substring(3));
        String mavenVar = mavenHome.substring(0, 3);
        MavenInstallation varMaven = new MavenInstallation("varMaven", mavenHomeVar, JenkinsRule.NO_PROPERTIES);
        j.jenkins.getDescriptorByType(DescriptorImpl.class).setInstallations(maven, varMaven);
        JDK jdk = j.jenkins.getJDK("default");
        String javaHome = jdk.getHome();
        String javaHomeVar = "${VAR_JAVA}" + (javaHome.substring(3));
        String javaVar = javaHome.substring(0, 3);
        JDK varJDK = new JDK("varJDK", javaHomeVar);
        j.jenkins.getJDKs().add(varJDK);
        FreeStyleProject project = j.createFreeStyleProject();
        project.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("VAR_MAVEN", "XXX"), new StringParameterDefinition("VAR_JAVA", "XXX")));
        project.getBuildersList().add(new Maven("--help", varMaven.getName()));
        project.setJDK(varJDK);
        FreeStyleBuild build = project.scheduleBuild2(0, new LegacyCodeCause(), new hudson.model.ParametersAction(new StringParameterValue("VAR_MAVEN", mavenVar), new StringParameterValue("VAR_JAVA", javaVar))).get();
        j.assertBuildStatusSuccess(build);
    }

    /**
     * Simulates the addition of the new Maven via UI and makes sure it works.
     */
    @Test
    public void globalConfigAjax() throws Exception {
        HtmlPage p = j.createWebClient().goTo("configureTools");
        HtmlForm f = p.getFormByName("config");
        HtmlButton b = j.getButtonByCaption(f, "Add Maven");
        b.click();
        j.findPreviousInputElement(b, "name").setValueAttribute("myMaven");
        j.findPreviousInputElement(b, "home").setValueAttribute("/tmp/foo");
        j.submit(f);
        verify();
        // another submission and verify it survives a roundtrip
        p = j.createWebClient().goTo("configure");
        f = p.getFormByName("config");
        j.submit(f);
        verify();
    }

    @Test
    public void sensitiveParameters() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdb = new ParametersDefinitionProperty(new StringParameterDefinition("string", "defaultValue", "string description"), new PasswordParameterDefinition("password", "12345", "password description"), new StringParameterDefinition("string2", "Value2", "string description"));
        project.addProperty(pdb);
        project.setScm(new ExtractResourceSCM(getClass().getResource("maven-empty.zip")));
        project.getBuildersList().add(new Maven("clean package", null));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        @SuppressWarnings("deprecation")
        String buildLog = build.getLog();
        Assert.assertNotNull(buildLog);
        System.out.println(buildLog);
        Assert.assertFalse(buildLog.contains("-Dpassword=12345"));
    }

    @Test
    public void parametersReferencedFromPropertiesShouldRetainBackslashes() throws Exception {
        final String properties = "global.path=$GLOBAL_PATH\nmy.path=$PATH\\\\Dir";
        final StringParameterDefinition parameter = new StringParameterDefinition("PATH", "C:\\Windows");
        final Entry envVar = new Entry("GLOBAL_PATH", "D:\\Jenkins");
        FreeStyleProject project = j.createFreeStyleProject();
        // This test implements legacy behavior, when Build Variables are injected by default
        project.getBuildersList().add(new Maven("--help", null, null, properties, null, false, null, null, true));
        project.addProperty(new ParametersDefinitionProperty(parameter));
        j.jenkins.getNodeProperties().replaceBy(Collections.singleton(new hudson.slaves.EnvironmentVariablesNodeProperty(envVar)));
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        @SuppressWarnings("deprecation")
        String buildLog = build.getLog();
        Assert.assertNotNull(buildLog);
        Assert.assertTrue(("Parameter my.path should preserve backslashes in:\n" + buildLog), buildLog.contains("-Dmy.path=C:\\Windows\\Dir"));
        Assert.assertTrue(("Parameter global.path should preserve backslashes in:\n" + buildLog), buildLog.contains("-Dglobal.path=D:\\Jenkins"));
    }

    @Test
    public void defaultSettingsProvider() throws Exception {
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new Maven("a", null, "a.pom", "c=d", "-e", true));
            Maven m = p.getBuildersList().get(Maven.class);
            Assert.assertNotNull(m);
            Assert.assertEquals(DefaultSettingsProvider.class, m.getSettings().getClass());
            Assert.assertEquals(DefaultGlobalSettingsProvider.class, m.getGlobalSettings().getClass());
        }
        {
            GlobalMavenConfig globalMavenConfig = GlobalMavenConfig.get();
            Assert.assertNotNull("No global Maven Config available", globalMavenConfig);
            globalMavenConfig.setSettingsProvider(new FilePathSettingsProvider("/tmp/settings.xml"));
            globalMavenConfig.setGlobalSettingsProvider(new FilePathGlobalSettingsProvider("/tmp/global-settings.xml"));
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new Maven("b", null, "b.pom", "c=d", "-e", true));
            Maven m = p.getBuildersList().get(Maven.class);
            Assert.assertEquals(FilePathSettingsProvider.class, m.getSettings().getClass());
            Assert.assertEquals("/tmp/settings.xml", getPath());
            Assert.assertEquals("/tmp/global-settings.xml", getPath());
        }
    }

    @Issue("JENKINS-18898")
    @Test
    public void testNullHome() {
        EnvVars env = new EnvVars();
        new MavenInstallation("_", "", Collections.<ToolProperty<?>>emptyList()).buildEnvVars(env);
        Assert.assertTrue(env.isEmpty());
    }

    @Issue("JENKINS-26684")
    @Test
    public void specialCharsInBuildVariablesPassedAsProperties() throws Exception {
        MavenInstallation maven = ToolInstallations.configureMaven3();
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new Maven("--help", maven.getName()));
        p.addProperty(new ParametersDefinitionProperty(new StringParameterDefinition("tilde", "~"), new StringParameterDefinition("exclamation_mark", "!"), new StringParameterDefinition("at_sign", "@"), new StringParameterDefinition("sharp", "#"), new StringParameterDefinition("dollar", "$"), new StringParameterDefinition("percent", "%"), new StringParameterDefinition("circumflex", "^"), new StringParameterDefinition("ampersand", "&"), new StringParameterDefinition("asterix", "*"), new StringParameterDefinition("parentheses", "()"), new StringParameterDefinition("underscore", "_"), new StringParameterDefinition("plus", "+"), new StringParameterDefinition("braces", "{}"), new StringParameterDefinition("brackets", "[]"), new StringParameterDefinition("colon", ":"), new StringParameterDefinition("semicolon", ";"), new StringParameterDefinition("quote", "\""), new StringParameterDefinition("apostrophe", "'"), new StringParameterDefinition("backslash", "\\"), new StringParameterDefinition("pipe", "|"), new StringParameterDefinition("angle_brackets", "<>"), new StringParameterDefinition("comma", ","), new StringParameterDefinition("period", "."), new StringParameterDefinition("slash", "/"), new StringParameterDefinition("question_mark", "?"), new StringParameterDefinition("space", " ")));
        FreeStyleBuild build = j.buildAndAssertSuccess(p);
    }

    @Test
    public void doPassBuildVariablesOptionally() throws Exception {
        MavenInstallation maven = ToolInstallations.configureMaven3();
        FreeStyleProject p = j.createFreeStyleProject();
        p.updateByXml(((Source) (new StreamSource(getClass().getResourceAsStream("MavenTest/doPassBuildVariablesOptionally.xml")))));
        String log = j.buildAndAssertSuccess(p).getLog();
        Assert.assertTrue("Build variables injection should be enabled by default when loading from XML", p.getBuildersList().get(Maven.class).isInjectBuildVariables());
        Assert.assertTrue("Build variables should be injected by default when loading from XML", log.contains("-DNAME=VALUE"));
        p.getBuildersList().clear();
        p.getBuildersList().add(/* do not inject */
        new Maven("--help", maven.getName(), null, null, null, false, null, null, false));
        log = j.buildAndAssertSuccess(p).getLog();
        Assert.assertFalse("Build variables should not be injected", log.contains("-DNAME=VALUE"));
        p.getBuildersList().clear();
        p.getBuildersList().add(/* do inject */
        new Maven("--help", maven.getName(), null, null, null, false, null, null, true));
        log = j.buildAndAssertSuccess(p).getLog();
        Assert.assertTrue("Build variables should be injected", log.contains("-DNAME=VALUE"));
        Assert.assertFalse("Build variables injection should be disabled by default", new Maven("", "").isInjectBuildVariables());
    }

    @Test
    public void doAlwaysPassProperties() throws Exception {
        MavenInstallation maven = ToolInstallations.configureMaven3();
        FreeStyleProject p = j.createFreeStyleProject();
        String properties = "TEST_PROP1=VAL1\nTEST_PROP2=VAL2";
        p.getBuildersList().add(/* do not inject build variables */
        new Maven("--help", maven.getName(), null, properties, null, false, null, null, false));
        String log = j.buildAndAssertSuccess(p).getLog();
        Assert.assertTrue("Properties should always be injected, even when build variables injection is disabled", ((log.contains("-DTEST_PROP1=VAL1")) && (log.contains("-DTEST_PROP2=VAL2"))));
        p.getBuildersList().clear();
        p.getBuildersList().add(/* do inject build variables */
        new Maven("--help", maven.getName(), null, properties, null, false, null, null, true));
        log = j.buildAndAssertSuccess(p).getLog();
        Assert.assertTrue("Properties should always be injected, even when build variables injection is enabled", ((log.contains("-DTEST_PROP1=VAL1")) && (log.contains("-DTEST_PROP2=VAL2"))));
    }

    @Issue("JENKINS-34138")
    @Test
    public void checkMavenInstallationEquals() throws Exception {
        MavenInstallation maven = ToolInstallations.configureMaven3();
        MavenInstallation maven2 = ToolInstallations.configureMaven3();
        Assert.assertEquals(maven.hashCode(), maven2.hashCode());
        Assert.assertTrue(maven.equals(maven2));
    }

    @Issue("JENKINS-34138")
    @Test
    public void checkMavenInstallationNotEquals() throws Exception {
        MavenInstallation maven3 = ToolInstallations.configureMaven3();
        MavenInstallation maven2 = ToolInstallations.configureDefaultMaven();
        Assert.assertNotEquals(maven3.hashCode(), maven2.hashCode());
        Assert.assertFalse(maven3.equals(maven2));
    }
}

