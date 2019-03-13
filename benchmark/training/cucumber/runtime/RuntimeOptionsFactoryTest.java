package cucumber.runtime;


import SnippetType.CAMELCASE;
import cucumber.api.CucumberOptions;
import cucumber.api.Plugin;
import cucumber.api.SnippetType;
import cucumber.runner.TimeService;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.formatter.Plugins;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


public class RuntimeOptionsFactoryTest {
    @Test
    public void create_strict() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.Strict.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertTrue(runtimeOptions.isStrict());
    }

    @Test
    public void create_non_strict() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.NotStrict.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertFalse(runtimeOptions.isStrict());
    }

    @Test
    public void create_without_options() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.WithoutOptions.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertFalse(runtimeOptions.isStrict());
        Assert.assertThat(runtimeOptions.getFeaturePaths(), contains(RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), runtimeOptions);
        Assert.assertThat(plugins.getPlugins(), hasSize(2));
        assertPluginExists(plugins.getPlugins(), "cucumber.runtime.formatter.ProgressFormatter");
        assertPluginExists(plugins.getPlugins(), "cucumber.runtime.formatter.DefaultSummaryPrinter");
    }

    @Test
    public void create_without_options_with_base_class_without_options() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.WithoutOptionsWithBaseClassWithoutOptions.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), runtimeOptions);
        Assert.assertThat(runtimeOptions.getFeaturePaths(), contains(RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
        Assert.assertThat(plugins.getPlugins(), hasSize(2));
        assertPluginExists(plugins.getPlugins(), "cucumber.runtime.formatter.ProgressFormatter");
        assertPluginExists(plugins.getPlugins(), "cucumber.runtime.formatter.DefaultSummaryPrinter");
    }

    @Test
    public void create_with_no_name() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.NoName.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertTrue(runtimeOptions.getTagFilters().isEmpty());
        Assert.assertTrue(runtimeOptions.getNameFilters().isEmpty());
        Assert.assertTrue(runtimeOptions.getLineFilters().isEmpty());
    }

    @Test
    public void create_with_multiple_names() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.MultipleNames.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        List<Pattern> filters = runtimeOptions.getNameFilters();
        Assert.assertEquals(2, filters.size());
        Iterator<Pattern> iterator = filters.iterator();
        Assert.assertEquals("name1", getRegexpPattern(iterator.next()));
        Assert.assertEquals("name2", getRegexpPattern(iterator.next()));
    }

    @Test
    public void create_with_snippets() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.Snippets.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertEquals(CAMELCASE, runtimeOptions.getSnippetType());
    }

    @Test
    public void create_default_summary_printer_when_no_summary_printer_plugin_is_defined() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.ClassWithNoSummaryPrinterPlugin.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), runtimeOptions);
        assertPluginExists(plugins.getPlugins(), "cucumber.runtime.formatter.DefaultSummaryPrinter");
    }

    @Test
    public void inherit_plugin_from_baseclass() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.SubClassWithFormatter.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        List<Plugin> plugins = getPlugins();
        assertPluginExists(plugins, "cucumber.runtime.formatter.JSONFormatter");
        assertPluginExists(plugins, "cucumber.runtime.formatter.PrettyFormatter");
    }

    @Test
    public void override_monochrome_flag_from_baseclass() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.SubClassWithMonoChromeTrue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertTrue(runtimeOptions.isMonochrome());
    }

    @Test
    public void create_with_junit_options() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.ClassWithJunitOption.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertEquals(Arrays.asList("option1", "option2=value"), runtimeOptions.getJunitOptions());
    }

    @Test
    public void create_with_glue() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.ClassWithGlue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:app/features/user/registration"), RuntimeOptionsFactoryTest.uri("classpath:app/features/hooks")));
    }

    @Test
    public void create_with_extra_glue() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.ClassWithExtraGlue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:app/features/hooks"), RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
    }

    @Test
    public void create_with_extra_glue_in_subclass_of_extra_glue() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.SubClassWithExtraGlueOfExtraGlue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:app/features/user/hooks"), RuntimeOptionsFactoryTest.uri("classpath:app/features/hooks"), RuntimeOptionsFactoryTest.uri("classpath:cucumber/runtime")));
    }

    @Test
    public void create_with_extra_glue_in_subclass_of_glue() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.SubClassWithExtraGlueOfGlue.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        Assert.assertThat(runtimeOptions.getGlue(), contains(RuntimeOptionsFactoryTest.uri("classpath:app/features/user/hooks"), RuntimeOptionsFactoryTest.uri("classpath:app/features/user/registration"), RuntimeOptionsFactoryTest.uri("classpath:app/features/hooks")));
    }

    @Test(expected = CucumberException.class)
    public void cannot_create_with_glue_and_extra_glue() {
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(RuntimeOptionsFactoryTest.ClassWithGlueAndExtraGlue.class);
        runtimeOptionsFactory.create();
    }

    // empty
    @CucumberOptions(snippets = SnippetType.CAMELCASE)
    private static class Snippets {}

    // empty
    @CucumberOptions(strict = true)
    private static class Strict {}

    // empty
    @CucumberOptions
    private static class NotStrict {}

    // empty
    @CucumberOptions(name = { "name1", "name2" })
    private static class MultipleNames {}

    // empty
    @CucumberOptions
    private static class NoName {}

    // empty
    private static class WithoutOptions {}

    // empty
    private static class WithoutOptionsWithBaseClassWithoutOptions extends RuntimeOptionsFactoryTest.WithoutOptions {}

    // empty
    @CucumberOptions(plugin = "pretty")
    private static class SubClassWithFormatter extends RuntimeOptionsFactoryTest.BaseClassWithFormatter {}

    // empty
    @CucumberOptions(plugin = "json:test-json-report.json")
    private static class BaseClassWithFormatter {}

    // empty
    @CucumberOptions(monochrome = true)
    private static class SubClassWithMonoChromeTrue extends RuntimeOptionsFactoryTest.BaseClassWithMonoChromeFalse {}

    // empty
    @CucumberOptions(monochrome = false)
    private static class BaseClassWithMonoChromeFalse {}

    // empty
    @CucumberOptions(plugin = "cucumber.runtime.formatter.AnyStepDefinitionReporter")
    private static class ClassWithNoFormatterPlugin {}

    // empty
    @CucumberOptions(plugin = "pretty")
    private static class ClassWithNoSummaryPrinterPlugin {}

    // empty
    @CucumberOptions(junit = { "option1", "option2=value" })
    private static class ClassWithJunitOption {}

    // empty
    @CucumberOptions(glue = { "app.features.user.registration", "app.features.hooks" })
    private static class ClassWithGlue {}

    // empty
    @CucumberOptions(extraGlue = { "app.features.hooks" })
    private static class ClassWithExtraGlue {}

    // empty
    @CucumberOptions(extraGlue = { "app.features.user.hooks" })
    private static class SubClassWithExtraGlueOfExtraGlue extends RuntimeOptionsFactoryTest.ClassWithExtraGlue {}

    // empty
    @CucumberOptions(extraGlue = { "app.features.user.hooks" })
    private static class SubClassWithExtraGlueOfGlue extends RuntimeOptionsFactoryTest.ClassWithGlue {}

    // empty
    @CucumberOptions(extraGlue = { "app.features.hooks" }, glue = { "app.features.user.registration" })
    private static class ClassWithGlueAndExtraGlue {}
}

