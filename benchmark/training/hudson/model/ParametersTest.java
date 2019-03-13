package hudson.model;


import Queue.Item;
import com.gargoylesoftware.htmlunit.html.DomNodeUtil;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlFormUtil;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import hudson.markup.MarkupFormatter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.jvnet.hudson.test.CaptureEnvironmentBuilder;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;


/**
 *
 *
 * @author huybrechts
 */
public class ParametersTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void parameterTypes() throws Exception {
        FreeStyleProject otherProject = j.createFreeStyleProject();
        otherProject.scheduleBuild2(0).get();
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(new StringParameterDefinition("string", "defaultValue", "string description"), new BooleanParameterDefinition("boolean", true, "boolean description"), new ChoiceParameterDefinition("choice", "Choice 1\nChoice 2", "choice description"), new RunParameterDefinition("run", otherProject.getName(), "run description", null));
        project.addProperty(pdp);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.goTo((("job/" + (project.getName())) + "/build?delay=0sec"));
        HtmlForm form = page.getFormByName("parameters");
        HtmlElement element = DomNodeUtil.selectSingleNode(form, "//tr[td/div/input/@value='string']");
        Assert.assertNotNull(element);
        Assert.assertEquals("string description", getTextContent());
        HtmlTextInput stringParameterInput = DomNodeUtil.selectSingleNode(element, ".//input[@name='value']");
        Assert.assertEquals("defaultValue", stringParameterInput.getAttribute("value"));
        Assert.assertEquals("string", getTextContent());
        stringParameterInput.setAttribute("value", "newValue");
        element = DomNodeUtil.selectSingleNode(form, "//tr[td/div/input/@value='boolean']");
        Assert.assertNotNull(element);
        Assert.assertEquals("boolean description", getTextContent());
        Object o = DomNodeUtil.selectSingleNode(element, ".//input[@name='value']");
        System.out.println(o);
        HtmlCheckBoxInput booleanParameterInput = ((HtmlCheckBoxInput) (o));
        Assert.assertEquals(true, booleanParameterInput.isChecked());
        Assert.assertEquals("boolean", getTextContent());
        element = DomNodeUtil.selectSingleNode(form, ".//tr[td/div/input/@value='choice']");
        Assert.assertNotNull(element);
        Assert.assertEquals("choice description", getTextContent());
        Assert.assertEquals("choice", getTextContent());
        element = DomNodeUtil.selectSingleNode(form, ".//tr[td/div/input/@value='run']");
        Assert.assertNotNull(element);
        Assert.assertEquals("run description", getTextContent());
        Assert.assertEquals("run", getTextContent());
        j.submit(form);
        Queue.Item q = j.jenkins.getQueue().getItem(project);
        if (q != null)
            q.getFuture().get();
        else
            Thread.sleep(1000);

        Assert.assertEquals("newValue", builder.getEnvVars().get("STRING"));
        Assert.assertEquals("true", builder.getEnvVars().get("BOOLEAN"));
        Assert.assertEquals("Choice 1", builder.getEnvVars().get("CHOICE"));
        Assert.assertEquals(((j.jenkins.getRootUrl()) + (otherProject.getLastBuild().getUrl())), builder.getEnvVars().get("RUN"));
    }

    @Test
    public void choiceWithLTGT() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(new ChoiceParameterDefinition("choice", "Choice 1\nChoice <2>", "choice description"));
        project.addProperty(pdp);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.goTo((("job/" + (project.getName())) + "/build?delay=0sec"));
        HtmlForm form = page.getFormByName("parameters");
        HtmlElement element = DomNodeUtil.selectSingleNode(form, ".//tr[td/div/input/@value='choice']");
        Assert.assertNotNull(element);
        Assert.assertEquals("choice description", getTextContent());
        Assert.assertEquals("choice", getTextContent());
        HtmlOption opt = DomNodeUtil.selectSingleNode(element, "td/div/select/option[@value='Choice <2>']");
        Assert.assertNotNull(opt);
        Assert.assertEquals("Choice <2>", opt.asText());
        opt.setSelected(true);
        j.submit(form);
        Queue.Item q = j.jenkins.getQueue().getItem(project);
        if (q != null)
            q.getFuture().get();
        else
            Thread.sleep(1000);

        Assert.assertNotNull(builder.getEnvVars());
        Assert.assertEquals("Choice <2>", builder.getEnvVars().get("CHOICE"));
    }

    @Test
    public void sensitiveParameters() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdb = new ParametersDefinitionProperty(new PasswordParameterDefinition("password", "12345", "password description"));
        project.addProperty(pdb);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        Set<String> sensitiveVars = build.getSensitiveBuildVariables();
        Assert.assertNotNull(sensitiveVars);
        Assert.assertTrue(sensitiveVars.contains("password"));
    }

    @Test
    public void nonSensitiveParameters() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdb = new ParametersDefinitionProperty(new StringParameterDefinition("string", "defaultValue", "string description"));
        project.addProperty(pdb);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        Set<String> sensitiveVars = build.getSensitiveBuildVariables();
        Assert.assertNotNull(sensitiveVars);
        Assert.assertFalse(sensitiveVars.contains("string"));
    }

    @Test
    public void mixedSensitivity() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdb = new ParametersDefinitionProperty(new StringParameterDefinition("string", "defaultValue", "string description"), new PasswordParameterDefinition("password", "12345", "password description"), new StringParameterDefinition("string2", "Value2", "string description"));
        project.addProperty(pdb);
        CaptureEnvironmentBuilder builder = new CaptureEnvironmentBuilder();
        project.getBuildersList().add(builder);
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        Set<String> sensitiveVars = build.getSensitiveBuildVariables();
        Assert.assertNotNull(sensitiveVars);
        Assert.assertFalse(sensitiveVars.contains("string"));
        Assert.assertTrue(sensitiveVars.contains("password"));
        Assert.assertFalse(sensitiveVars.contains("string2"));
    }

    @Test
    @Issue("JENKINS-3539")
    public void fileParameterNotSet() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        ParametersDefinitionProperty pdp = new ParametersDefinitionProperty(new FileParameterDefinition("filename", "description"));
        project.addProperty(pdp);
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.goTo((("job/" + (project.getName())) + "/build?delay=0sec"));
        HtmlForm form = page.getFormByName("parameters");
        j.submit(form);
        Queue.Item q = j.jenkins.getQueue().getItem(project);
        if (q != null)
            q.getFuture().get();
        else
            Thread.sleep(1000);

        Assert.assertFalse("file must not exist", project.getSomeWorkspace().child("filename").exists());
    }

    @Test
    @Issue("JENKINS-11543")
    public void unicodeParametersArePresetCorrectly() throws Exception {
        final FreeStyleProject p = j.createFreeStyleProject();
        ParametersDefinitionProperty pdb = new ParametersDefinitionProperty(new StringParameterDefinition("sname:a???", "svalue:a???", "sdesc:a???"), new FileParameterDefinition("fname:a???", "fdesc:a???"));
        p.addProperty(pdb);
        WebClient wc = // Ignore 405
        j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.getPage(p, "build");
        // java.lang.IllegalArgumentException: No such parameter definition: <gibberish>.
        wc.setThrowExceptionOnFailingStatusCode(true);
        final HtmlForm form = page.getFormByName("parameters");
        HtmlFormUtil.submit(form, HtmlFormUtil.getButtonByCaption(form, "Build"));
    }

    @Issue("SECURITY-353")
    @Test
    public void xss() throws Exception {
        j.jenkins.setMarkupFormatter(new ParametersTest.MyMarkupFormatter());
        FreeStyleProject p = j.createFreeStyleProject("p");
        StringParameterDefinition param = new StringParameterDefinition("<param name>", "<param default>", "<param description>");
        Assert.assertEquals("<b>[</b>param description<b>]</b>", param.getFormattedDescription());
        p.addProperty(new ParametersDefinitionProperty(param));
        WebClient wc = j.createWebClient().withThrowExceptionOnFailingStatusCode(false);
        HtmlPage page = wc.getPage(p, "build?delay=0sec");
        collector.checkThat(page.getWebResponse().getStatusCode(), is(HttpStatus.SC_METHOD_NOT_ALLOWED));// 405 to dissuade scripts from thinking this triggered the build

        String text = page.getWebResponse().getContentAsString();
        collector.checkThat("build page should escape param name", text, containsString("&lt;param name&gt;"));
        collector.checkThat("build page should not leave param name unescaped", text, not(containsString("<param name>")));
        collector.checkThat("build page should escape param default", text, containsString("&lt;param default&gt;"));
        collector.checkThat("build page should not leave param default unescaped", text, not(containsString("<param default>")));
        collector.checkThat("build page should mark up param description", text, containsString("<b>[</b>param description<b>]</b>"));
        collector.checkThat("build page should not leave param description unescaped", text, not(containsString("<param description>")));
        HtmlForm form = page.getFormByName("parameters");
        HtmlTextInput value = form.getInputByValue("<param default>");
        value.setText("<param value>");
        j.submit(form);
        j.waitUntilNoActivity();
        FreeStyleBuild b = p.getBuildByNumber(1);
        page = j.createWebClient().getPage(b, "parameters/");
        text = page.getWebResponse().getContentAsString();
        collector.checkThat("parameters page should escape param name", text, containsString("&lt;param name&gt;"));
        collector.checkThat("parameters page should not leave param name unescaped", text, not(containsString("<param name>")));
        collector.checkThat("parameters page should escape param value", text, containsString("&lt;param value&gt;"));
        collector.checkThat("parameters page should not leave param value unescaped", text, not(containsString("<param value>")));
        collector.checkThat("parameters page should mark up param description", text, containsString("<b>[</b>param description<b>]</b>"));
        collector.checkThat("parameters page should not leave param description unescaped", text, not(containsString("<param description>")));
    }

    static class MyMarkupFormatter extends MarkupFormatter {
        @Override
        public void translate(String markup, Writer output) throws IOException {
            Matcher m = Pattern.compile("[<>]").matcher(markup);
            StringBuffer buf = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(buf, (m.group().equals("<") ? "<b>[</b>" : "<b>]</b>"));
            } 
            m.appendTail(buf);
            output.write(buf.toString());
        }
    }
}

