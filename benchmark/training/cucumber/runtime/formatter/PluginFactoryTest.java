package cucumber.runtime.formatter;


import Result.Type;
import cucumber.api.PickleStepTestStep;
import cucumber.api.Result;
import cucumber.api.TestCase;
import cucumber.api.event.TestStepFinished;
import cucumber.runner.EventBus;
import cucumber.runner.TimeServiceEventBus;
import cucumber.runner.TimeServiceStub;
import cucumber.runtime.CucumberException;
import cucumber.runtime.Utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginFactoryTest {
    private PluginFactory fc = new PluginFactory();

    @Test
    public void instantiates_junit_plugin_with_file_arg() throws IOException {
        Object plugin = fc.create(("junit:" + (File.createTempFile("cucumber", "xml"))));
        Assert.assertEquals(JUnitFormatter.class, plugin.getClass());
    }

    @Test
    public void instantiates_html_plugin_with_dir_arg() throws IOException {
        Object plugin = fc.create(("html:" + (TempDir.createTempDirectory().getAbsolutePath())));
        Assert.assertEquals(HTMLFormatter.class, plugin.getClass());
    }

    @Test
    public void fails_to_instantiate_html_plugin_without_dir_arg() throws IOException {
        try {
            fc.create("html");
            Assert.fail();
        } catch (CucumberException e) {
            Assert.assertEquals("You must supply an output argument to html. Like so: html:output", e.getMessage());
        }
    }

    @Test
    public void instantiates_pretty_plugin_with_file_arg() throws IOException {
        Object plugin = fc.create(("pretty:" + (Utils.toURL(TempDir.createTempFile().getAbsolutePath()))));
        Assert.assertEquals(PrettyFormatter.class, plugin.getClass());
    }

    @Test
    public void instantiates_pretty_plugin_without_file_arg() {
        Object plugin = fc.create("pretty");
        Assert.assertEquals(PrettyFormatter.class, plugin.getClass());
    }

    @Test
    public void instantiates_usage_plugin_without_file_arg() {
        Object plugin = fc.create("usage");
        Assert.assertEquals(UsageFormatter.class, plugin.getClass());
    }

    @Test
    public void instantiates_usage_plugin_with_file_arg() throws IOException {
        Object plugin = fc.create(("usage:" + (TempDir.createTempFile().getAbsolutePath())));
        Assert.assertEquals(UsageFormatter.class, plugin.getClass());
    }

    @Test
    public void plugin_does_not_buffer_its_output() throws IOException {
        PrintStream previousSystemOut = System.out;
        OutputStream mockSystemOut = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(mockSystemOut));
            // Need to create a new plugin factory here since we need it to pick up the new value of System.out
            fc = new PluginFactory();
            ProgressFormatter plugin = ((ProgressFormatter) (fc.create("progress")));
            EventBus bus = new TimeServiceEventBus(new TimeServiceStub(0));
            plugin.setEventPublisher(bus);
            Result result = new Result(Type.PASSED, 0L, null);
            TestStepFinished event = new TestStepFinished(bus.getTime(), Mockito.mock(TestCase.class), Mockito.mock(PickleStepTestStep.class), result);
            bus.send(event);
            Assert.assertThat(mockSystemOut.toString(), CoreMatchers.is(CoreMatchers.not("")));
        } finally {
            System.setOut(previousSystemOut);
        }
    }

    @Test
    public void instantiates_single_custom_appendable_plugin_with_stdout() {
        PluginFactoryTest.WantsAppendable plugin = ((PluginFactoryTest.WantsAppendable) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable")));
        Assert.assertThat(plugin.out, CoreMatchers.is(CoreMatchers.instanceOf(PrintStream.class)));
        try {
            fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable");
            Assert.fail();
        } catch (CucumberException expected) {
            Assert.assertEquals(("Only one formatter can use STDOUT, now both cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable " + ("and cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable use it. " + "If you use more than one formatter you must specify output path with PLUGIN:PATH_OR_URL")), expected.getMessage());
        }
    }

    @Test
    public void instantiates_custom_appendable_plugin_with_stdout_and_file() throws IOException {
        PluginFactoryTest.WantsAppendable plugin = ((PluginFactoryTest.WantsAppendable) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable")));
        Assert.assertThat(plugin.out, CoreMatchers.is(CoreMatchers.instanceOf(PrintStream.class)));
        PluginFactoryTest.WantsAppendable plugin2 = ((PluginFactoryTest.WantsAppendable) (fc.create(("cucumber.runtime.formatter.PluginFactoryTest$WantsAppendable:" + (TempDir.createTempFile().getAbsolutePath())))));
        Assert.assertEquals(UTF8OutputStreamWriter.class, plugin2.out.getClass());
    }

    @Test
    public void instantiates_custom_url_plugin() throws IOException {
        PluginFactoryTest.WantsUrl plugin = ((PluginFactoryTest.WantsUrl) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsUrl:halp")));
        Assert.assertEquals(new URL("file:halp/"), plugin.out);
    }

    @Test
    public void instantiates_custom_url_plugin_with_http() throws IOException {
        PluginFactoryTest.WantsUrl plugin = ((PluginFactoryTest.WantsUrl) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsUrl:http://halp/")));
        Assert.assertEquals(new URL("http://halp/"), plugin.out);
    }

    @Test
    public void instantiates_custom_uri_plugin_with_ws() throws IOException, URISyntaxException {
        PluginFactoryTest.WantsUri plugin = ((PluginFactoryTest.WantsUri) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsUri:ws://halp/")));
        Assert.assertEquals(new URI("ws://halp/"), plugin.out);
    }

    @Test
    public void instantiates_custom_file_plugin() throws IOException {
        PluginFactoryTest.WantsFile plugin = ((PluginFactoryTest.WantsFile) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsFile:halp.txt")));
        Assert.assertEquals(new File("halp.txt"), plugin.out);
    }

    @Test
    public void instantiates_custom_string_arg_plugin() throws IOException {
        PluginFactoryTest.WantsString plugin = ((PluginFactoryTest.WantsString) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsString:hello")));
        Assert.assertEquals("hello", plugin.arg);
    }

    @Test
    public void instantiates_plugin_using_empty_constructor_when_unspecified() throws IOException {
        PluginFactoryTest.WantsStringOrDefault plugin = ((PluginFactoryTest.WantsStringOrDefault) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsStringOrDefault")));
        Assert.assertEquals("defaultValue", plugin.arg);
    }

    @Test
    public void instantiates_plugin_using_arg_constructor_when_specified() throws IOException {
        PluginFactoryTest.WantsStringOrDefault plugin = ((PluginFactoryTest.WantsStringOrDefault) (fc.create("cucumber.runtime.formatter.PluginFactoryTest$WantsStringOrDefault:hello")));
        Assert.assertEquals("hello", plugin.arg);
    }

    @Test
    public void instantiates_timeline_plugin_with_dir_arg() throws IOException {
        Object plugin = fc.create(("timeline:" + (TempDir.createTempDirectory().getAbsolutePath())));
        Assert.assertEquals(TimelineFormatter.class, plugin.getClass());
    }

    public static class WantsAppendable extends StubFormatter {
        public final Appendable out;

        public WantsAppendable(Appendable out) {
            this.out = out;
        }

        public WantsAppendable() {
            this.out = null;
        }
    }

    public static class WantsUrl extends StubFormatter {
        public final URL out;

        public WantsUrl(URL out) {
            this.out = out;
        }
    }

    public static class WantsUri extends StubFormatter {
        public final URI out;

        public WantsUri(URI out) {
            this.out = out;
        }
    }

    public static class WantsFile extends StubFormatter {
        public final File out;

        public WantsFile(File out) {
            this.out = out;
        }
    }

    public static class WantsString extends StubFormatter {
        public final String arg;

        public WantsString(String arg) {
            this.arg = arg;
        }
    }

    public static class WantsStringOrDefault extends StubFormatter {
        public final String arg;

        public WantsStringOrDefault(String arg) {
            this.arg = arg;
        }

        public WantsStringOrDefault() {
            this("defaultValue");
        }
    }
}

