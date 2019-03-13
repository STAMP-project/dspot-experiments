package hudson.console;


import Jenkins.SESSION_HASH;
import JenkinsRule.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeUtil;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.FilePath;
import hudson.Launcher;
import hudson.MarkupText;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.PollingResult;
import hudson.scm.PollingResult.Change;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.triggers.SCMTrigger;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.SequenceLock;
import org.jvnet.hudson.test.SingleFileSCM;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.TestExtension;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class ConsoleAnnotatorTest {
    @Rule
    public JenkinsRule r = new JenkinsRule();

    /**
     * Let the build complete, and see if stateless {@link ConsoleAnnotator} annotations happen as expected.
     */
    @Issue("JENKINS-6031")
    @Test
    public void completedStatelessLogAnnotation() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().println("---");
                listener.getLogger().println("ooo");
                listener.getLogger().println("ooo");
                return true;
            }
        });
        FreeStyleBuild b = r.buildAndAssertSuccess(p);
        // make sure we see the annotation
        HtmlPage rsp = r.createWebClient().getPage(b, "console");
        Assert.assertEquals(1, DomNodeUtil.selectNodes(rsp, "//B[@class='demo']").size());
        // make sure raw console output doesn't include the garbage
        TextPage raw = ((TextPage) (r.createWebClient().goTo(((b.getUrl()) + "consoleText"), "text/plain")));
        System.out.println(raw.getContent());
        String nl = System.getProperty("line.separator");
        Assert.assertTrue(raw.getContent().contains(((((((nl + "---") + nl) + "ooo") + nl) + "ooo") + nl)));
        // there should be two 'ooo's
        String xml = rsp.asXml();
        Assert.assertEquals(xml, 3, xml.split("ooo").length);
    }

    /**
     * Only annotates the first occurrence of "ooo".
     */
    @TestExtension("completedStatelessLogAnnotation")
    public static final class DemoAnnotatorFactory extends ConsoleAnnotatorFactory<FreeStyleBuild> {
        @Override
        public ConsoleAnnotator<FreeStyleBuild> newInstance(FreeStyleBuild context) {
            return new ConsoleAnnotatorTest.DemoAnnotator();
        }
    }

    public static class DemoAnnotator extends ConsoleAnnotator<FreeStyleBuild> {
        private static final String ANNOTATE_TEXT = "ooo" + (System.getProperty("line.separator"));

        @Override
        public ConsoleAnnotator<FreeStyleBuild> annotate(FreeStyleBuild build, MarkupText text) {
            if (text.getText().equals(ConsoleAnnotatorTest.DemoAnnotator.ANNOTATE_TEXT)) {
                text.addMarkup(0, 3, "<b class=demo>", "</b>");
                return null;
            }
            return this;
        }
    }

    @Issue("JENKINS-6034")
    @Test
    public void consoleAnnotationFilterOut() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().print("abc\n");
                listener.getLogger().print(((HyperlinkNote.encodeTo("http://infradna.com/", "def")) + "\n"));
                return true;
            }
        });
        FreeStyleBuild b = r.buildAndAssertSuccess(p);
        // make sure we see the annotation
        HtmlPage rsp = r.createWebClient().getPage(b, "console");
        Assert.assertEquals(1, DomNodeUtil.selectNodes(rsp, "//A[@href='http://infradna.com/']").size());
        // make sure raw console output doesn't include the garbage
        TextPage raw = ((TextPage) (r.createWebClient().goTo(((b.getUrl()) + "consoleText"), "text/plain")));
        Assert.assertThat(raw.getContent(), Matchers.containsString("\nabc\ndef\n"));
    }

    class ProgressiveLogClient {
        WebClient wc;

        Run run;

        String consoleAnnotator;

        String start;

        private Page p;

        ProgressiveLogClient(JenkinsRule.WebClient wc, Run r) {
            this.wc = wc;
            this.run = r;
        }

        String next() throws IOException {
            WebRequest req = new WebRequest(new URL(((((r.getURL()) + (run.getUrl())) + "/logText/progressiveHtml") + ((start) != null ? "?start=" + (start) : ""))));
            req.setEncodingType(null);
            Map headers = new HashMap();
            if ((consoleAnnotator) != null)
                headers.put("X-ConsoleAnnotator", consoleAnnotator);

            req.setAdditionalHeaders(headers);
            p = wc.getPage(req);
            consoleAnnotator = p.getWebResponse().getResponseHeaderValue("X-ConsoleAnnotator");
            start = p.getWebResponse().getResponseHeaderValue("X-Text-Size");
            return p.getWebResponse().getContentAsString();
        }
    }

    /**
     * Tests the progressive output by making sure that the state of {@link ConsoleAnnotator}s are
     * maintained across different progressiveLog calls.
     */
    @Test
    public void progressiveOutput() throws Exception {
        final SequenceLock lock = new SequenceLock();
        JenkinsRule.WebClient wc = r.createWebClient();
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                lock.phase(0);
                // make sure the build is now properly started
                lock.phase(2);
                listener.getLogger().println("line1");
                lock.phase(4);
                listener.getLogger().println("line2");
                lock.phase(6);
                return true;
            }
        });
        Future<FreeStyleBuild> f = p.scheduleBuild2(0);
        lock.phase(1);
        FreeStyleBuild b = p.getBuildByNumber(1);
        ConsoleAnnotatorTest.ProgressiveLogClient plc = new ConsoleAnnotatorTest.ProgressiveLogClient(wc, b);
        // the page should contain some output indicating the build has started why and etc.
        plc.next();
        lock.phase(3);
        Assert.assertEquals("<b tag=1>line1</b>\r\n", plc.next());
        // the new invocation should start from where the previous call left off
        lock.phase(5);
        Assert.assertEquals("<b tag=2>line2</b>\r\n", plc.next());
        lock.done();
        // should complete successfully
        r.assertBuildStatusSuccess(f);
    }

    @TestExtension("progressiveOutput")
    public static final ConsoleAnnotatorFactory STATEFUL_ANNOTATOR = new ConsoleAnnotatorFactory() {
        public ConsoleAnnotator newInstance(Object context) {
            return new ConsoleAnnotatorTest.StatefulAnnotator();
        }
    };

    public static class StatefulAnnotator extends ConsoleAnnotator<Object> {
        int n = 1;

        public ConsoleAnnotator annotate(Object build, MarkupText text) {
            if (text.getText().startsWith("line"))
                text.addMarkup(0, 5, (("<b tag=" + ((n)++)) + ">"), "</b>");

            return this;
        }
    }

    /**
     * Place {@link ConsoleNote}s and make sure it works.
     */
    @Test
    public void consoleAnnotation() throws Exception {
        final SequenceLock lock = new SequenceLock();
        JenkinsRule.WebClient wc = r.createWebClient();
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                lock.phase(0);
                // make sure the build is now properly started
                lock.phase(2);
                listener.getLogger().print("abc");
                listener.annotate(new ConsoleAnnotatorTest.DollarMark());
                listener.getLogger().println("def");
                lock.phase(4);
                listener.getLogger().print("123");
                listener.annotate(new ConsoleAnnotatorTest.DollarMark());
                listener.getLogger().print("456");
                listener.annotate(new ConsoleAnnotatorTest.DollarMark());
                listener.getLogger().println("789");
                lock.phase(6);
                return true;
            }
        });
        Future<FreeStyleBuild> f = p.scheduleBuild2(0);
        // discard the initial header portion
        lock.phase(1);
        FreeStyleBuild b = p.getBuildByNumber(1);
        ConsoleAnnotatorTest.ProgressiveLogClient plc = new ConsoleAnnotatorTest.ProgressiveLogClient(wc, b);
        plc.next();
        lock.phase(3);
        Assert.assertEquals("abc$$$def\r\n", plc.next());
        lock.phase(5);
        Assert.assertEquals("123$$$456$$$789\r\n", plc.next());
        lock.done();
        // should complete successfully
        r.assertBuildStatusSuccess(f);
    }

    /**
     * Places a triple dollar mark at the specified position.
     */
    public static final class DollarMark extends ConsoleNote<Object> {
        public ConsoleAnnotator annotate(Object context, MarkupText text, int charPos) {
            text.addMarkup(charPos, "$$$");
            return null;
        }

        @TestExtension
        public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {}
    }

    /**
     * script.js defined in the annotator needs to be incorporated into the console page.
     */
    @Test
    public void scriptInclusion() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        FreeStyleBuild b = r.buildAndAssertSuccess(p);
        HtmlPage html = r.createWebClient().getPage(b, "console");
        // verify that there's an element inserted by the script
        Assert.assertNotNull(html.getElementById("inserted-by-test1"));
        Assert.assertNotNull(html.getElementById("inserted-by-test2"));
        for (DomElement e : html.getElementsByTagName("script")) {
            String src = e.getAttribute("src");
            if (!(src.isEmpty())) {
                Assert.assertThat(src, Matchers.containsString(SESSION_HASH));
            }
        }
    }

    public static final class JustToIncludeScript extends ConsoleNote<Object> {
        public ConsoleAnnotator annotate(Object build, MarkupText text, int charPos) {
            return null;
        }

        @TestExtension("scriptInclusion")
        public static final class DescriptorImpl extends ConsoleAnnotationDescriptor {}
    }

    @TestExtension("scriptInclusion")
    public static final class JustToIncludeScriptAnnotator extends ConsoleAnnotatorFactory {
        public ConsoleAnnotator newInstance(Object context) {
            return null;
        }
    }

    /**
     * Makes sure '<', '&', are escaped.
     */
    @Issue("JENKINS-5952")
    @Test
    public void escape() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                listener.getLogger().println("<b>&amp;</b>");
                return true;
            }
        });
        FreeStyleBuild b = r.buildAndAssertSuccess(p);
        HtmlPage html = r.createWebClient().getPage(b, "console");
        String text = html.asText();
        System.out.println(text);
        Assert.assertTrue(text.contains("<b>&amp;</b>"));
        Assert.assertTrue(JenkinsRule.getLog(b).contains("<b>&amp;</b>"));
    }

    /**
     * Makes sure that annotations in the polling output is handled correctly.
     */
    @Test
    public void pollingOutput() throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.setScm(new ConsoleAnnotatorTest.PollingSCM());
        SCMTrigger t = new SCMTrigger("@daily");
        t.start(p, true);
        p.addTrigger(t);
        r.buildAndAssertSuccess(p);
        // poll now
        t.new Runner().run();
        HtmlPage log = r.createWebClient().getPage(p, "scmPollLog");
        String text = log.asText();
        Assert.assertTrue(text, text.contains("$$$hello from polling"));
    }

    public static class PollingSCM extends SingleFileSCM {
        public PollingSCM() throws UnsupportedEncodingException {
            super("abc", "def");
        }

        @Override
        protected PollingResult compareRemoteRevisionWith(AbstractProject project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
            listener.annotate(new ConsoleAnnotatorTest.DollarMark());
            listener.getLogger().println("hello from polling");
            return new PollingResult(Change.NONE);
        }

        @TestExtension
        public static final class DescriptorImpl extends SCMDescriptor<ConsoleAnnotatorTest.PollingSCM> {
            public DescriptorImpl() {
                super(ConsoleAnnotatorTest.PollingSCM.class, RepositoryBrowser.class);
            }
        }
    }
}

