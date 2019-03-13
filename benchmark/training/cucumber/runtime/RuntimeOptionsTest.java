package cucumber.runtime;


import RuntimeOptions.VERSION;
import RuntimeOptions.usageText;
import SnippetType.CAMELCASE;
import SnippetType.UNDERSCORE;
import cucumber.api.event.EventListener;
import cucumber.api.event.EventPublisher;
import cucumber.api.formatter.ColorAware;
import cucumber.api.formatter.StrictAware;
import cucumber.runner.TimeService;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.formatter.Plugins;
import cucumber.runtime.io.ResourceLoader;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.StringStartsWith;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class RuntimeOptionsTest {
    private final Properties properties = new Properties();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private ResourceLoader resourceLoader;

    @Test
    public void has_version_from_properties_file() {
        Assert.assertTrue(VERSION.matches("\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?"));
    }

    @Test
    public void has_usage() {
        RuntimeOptions.loadUsageTextIfNeeded();
        Assert.assertThat(usageText, StringStartsWith.startsWith("Usage"));
    }

    @Test
    public void assigns_feature_paths() {
        RuntimeOptions options = new RuntimeOptions("--glue somewhere somewhere_else");
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:somewhere_else")));
    }

    @Test
    public void strips_line_filters_from_feature_paths_and_put_them_among_line_filters() {
        RuntimeOptions options = new RuntimeOptions("--glue somewhere somewhere_else.feature:3");
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:somewhere_else.feature")));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:somewhere_else.feature"), Collections.singleton(3)));
    }

    @Test
    public void select_multiple_lines_in_a_features() {
        RuntimeOptions options = new RuntimeOptions("--glue somewhere somewhere_else.feature:3:5");
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:somewhere_else.feature")));
        Set<Integer> lines = new HashSet<>(Arrays.asList(3, 5));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:somewhere_else.feature"), lines));
    }

    @Test
    public void combines_line_filters_from_repeated_features() {
        RuntimeOptions options = new RuntimeOptions("--glue somewhere somewhere_else.feature:3 somewhere_else.feature:5");
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:somewhere_else.feature")));
        Set<Integer> lines = new HashSet<>(Arrays.asList(3, 5));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:somewhere_else.feature"), lines));
    }

    @Test
    public void assigns_filters_from_tags() {
        RuntimeOptions options = new RuntimeOptions("--tags @keep_this somewhere_else");
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:somewhere_else")));
        Assert.assertThat(options.getTagFilters(), contains("@keep_this"));
    }

    @Test
    public void strips_options() {
        RuntimeOptions options = new RuntimeOptions("  --glue  somewhere   somewhere_else");
        Assert.assertThat(options.getFeaturePaths(), CoreMatchers.is(Collections.singletonList(RuntimeOptionsTest.uri("file:somewhere_else"))));
    }

    @Test
    public void assigns_glue() {
        RuntimeOptions options = new RuntimeOptions("--glue somewhere");
        Assert.assertThat(options.getGlue(), contains(RuntimeOptionsTest.uri("classpath:somewhere")));
    }

    @Test
    public void creates_html_formatter() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--plugin", "html:some/dir", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins().get(0).getClass().getName(), CoreMatchers.is("cucumber.runtime.formatter.HTMLFormatter"));
    }

    @Test
    public void creates_progress_formatter_as_default() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins().get(0).getClass().getName(), CoreMatchers.is("cucumber.runtime.formatter.ProgressFormatter"));
    }

    @Test
    public void creates_progress_formatter_when_no_formatter_plugin_is_specified() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--plugin", "cucumber.runtime.formatter.AnyStepDefinitionReporter", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.ProgressFormatter")));
    }

    @Test
    public void creates_default_summary_printer_when_no_summary_printer_plugin_is_specified() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--plugin", "pretty", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.DefaultSummaryPrinter")));
    }

    @Test
    public void creates_null_summary_printer() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--plugin", "null_summary", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.NullSummaryPrinter")));
        Assert.assertThat(plugins.getPlugins(), CoreMatchers.not(IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.DefaultSummaryPrinter"))));
    }

    @Test
    public void assigns_strict() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--strict", "--glue", "somewhere"));
        Assert.assertTrue(options.isStrict());
    }

    @Test
    public void assigns_strict_short() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("-s", "--glue", "somewhere"));
        Assert.assertTrue(options.isStrict());
    }

    @Test
    public void default_strict() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--glue", "somewhere"));
        Assert.assertThat(options.isStrict(), CoreMatchers.is(false));
    }

    @Test
    public void assigns_wip() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--wip", "--glue", "somewhere"));
        Assert.assertThat(options.isWip(), CoreMatchers.is(true));
    }

    @Test
    public void assigns_wip_short() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("-w", "--glue", "somewhere"));
        Assert.assertThat(options.isWip(), CoreMatchers.is(true));
    }

    @Test
    public void default_wip() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--glue", "somewhere"));
        Assert.assertThat(options.isWip(), CoreMatchers.is(false));
    }

    @Test
    public void name_without_spaces_is_preserved() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--name", "someName"));
        Pattern actualPattern = options.getNameFilters().iterator().next();
        Assert.assertThat(actualPattern.pattern(), CoreMatchers.is("someName"));
    }

    @Test
    public void name_with_spaces_is_preserved() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--name", "some Name"));
        Pattern actualPattern = options.getNameFilters().iterator().next();
        Assert.assertThat(actualPattern.pattern(), CoreMatchers.is("some Name"));
    }

    @Test
    public void ensure_name_with_spaces_works_with_cucumber_options() {
        properties.setProperty("cucumber.options", "--name 'some Name'");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Collections.<String>emptyList());
        Pattern actualPattern = options.getNameFilters().iterator().next();
        Assert.assertThat(actualPattern.pattern(), CoreMatchers.is("some Name"));
    }

    @Test
    public void ensure_name_with_spaces_works_with_args() {
        RuntimeOptions options = new RuntimeOptions("--name 'some Name'");
        Pattern actualPattern = options.getNameFilters().iterator().next();
        Assert.assertThat(actualPattern.pattern(), CoreMatchers.is("some Name"));
    }

    @Test
    public void assigns_single_junit_option() {
        RuntimeOptions options = new RuntimeOptions("--junit,option");
        Assert.assertThat(options.getJunitOptions(), contains("option"));
    }

    @Test
    public void assigns_multiple_junit_options() {
        RuntimeOptions options = new RuntimeOptions("--junit,option1,option2=value");
        Assert.assertThat(options.getJunitOptions(), contains("option1", "option2=value"));
    }

    @Test
    public void clobbers_junit_options_from_cli_if_junit_options_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--junit,option_from_property");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--junit,option_to_be_clobbered"));
        Assert.assertThat(options.getJunitOptions(), contains("option_from_property"));
    }

    @Test
    public void overrides_options_with_system_properties_without_clobbering_non_overridden_ones() {
        properties.setProperty("cucumber.options", "--glue lookatme this_clobbers_feature_paths");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--strict", "--glue", "somewhere", "somewhere_else"));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:this_clobbers_feature_paths")));
        Assert.assertThat(options.getGlue(), contains(RuntimeOptionsTest.uri("classpath:lookatme")));
        Assert.assertTrue(options.isStrict());
    }

    @Test
    public void ensure_cli_glue_is_preserved_when_cucumber_options_property_defined() {
        properties.setProperty("cucumber.options", "--tags @foo");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--glue", "somewhere"));
        Assert.assertThat(options.getGlue(), contains(RuntimeOptionsTest.uri("classpath:somewhere")));
    }

    @Test
    public void clobbers_filters_from_cli_if_filters_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--tags @clobber_with_this");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--tags", "@should_be_clobbered"));
        Assert.assertThat(options.getTagFilters(), contains("@clobber_with_this"));
    }

    @Test
    public void clobbers_tag_and_name_filters_from_cli_if_line_filters_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "path/file.feature:3");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--tags", "@should_be_clobbered", "--name", "should_be_clobbered"));
        Assert.assertThat(options.getTagFilters(), emptyCollectionOf(String.class));
    }

    @Test
    public void clobbers_tag_and_name_filters_from_cli_if_rerun_file_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "@src/test/resources/cucumber/runtime/runtime-options-rerun.txt");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--tags", "@should_be_clobbered", "--name", "should_be_clobbered"));
        Assert.assertThat(options.getTagFilters(), emptyCollectionOf(String.class));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:this/should/be/rerun.feature"), Collections.singleton(12)));
    }

    @Test
    public void preserves_filters_from_cli_if_filters_not_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--strict");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--tags", "@keep_this"));
        Assert.assertThat(options.getTagFilters(), contains("@keep_this"));
    }

    @Test
    public void clobbers_features_from_cli_if_features_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "new newer");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("old", "older"));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:new"), RuntimeOptionsTest.uri("file:newer")));
    }

    @Test
    public void strips_lines_from_features_from_cli_if_filters_are_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--tags @Tag");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("path/file.feature:3"));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:path/file.feature")));
    }

    @Test
    public void preserves_features_from_cli_if_features_not_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--plugin pretty");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("old", "older"));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:old"), RuntimeOptionsTest.uri("file:older")));
    }

    @Test
    public void clobbers_line_filters_from_cli_if_features_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "new newer");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--tags", "@keep_this", "path/file1.feature:1"));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:new"), RuntimeOptionsTest.uri("file:newer")));
        Assert.assertThat(options.getTagFilters(), contains("@keep_this"));
    }

    @Test
    public void clobbers_formatter_plugins_from_cli_if_formatters_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--plugin pretty");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--plugin", "html:some/dir", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.PrettyFormatter")));
        Assert.assertThat(plugins.getPlugins(), CoreMatchers.not(IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.HTMLFormatter"))));
    }

    @Test
    public void adds_to_formatter_plugins_with_add_plugin_option() {
        properties.setProperty("cucumber.options", "--add-plugin pretty");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--plugin", "html:some/dir", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.HTMLFormatter")));
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.PrettyFormatter")));
    }

    @Test
    public void clobbers_summary_plugins_from_cli_if_summary_printer_specified_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--plugin default_summary");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--plugin", "null_summary", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.DefaultSummaryPrinter")));
        Assert.assertThat(plugins.getPlugins(), CoreMatchers.not(IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.NullSummaryPrinter"))));
    }

    @Test
    public void adds_to_summary_plugins_with_add_plugin_option() {
        properties.setProperty("cucumber.options", "--add-plugin default_summary");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--plugin", "null_summary", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.NullSummaryPrinter")));
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.DefaultSummaryPrinter")));
    }

    @Test
    public void does_not_clobber_plugins_of_different_type_when_specifying_plugins_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--plugin default_summary");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--plugin", "pretty", "--glue", "somewhere"));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.PrettyFormatter")));
        Assert.assertThat(plugins.getPlugins(), IsCollectionContaining.hasItem(RuntimeOptionsTest.plugin("cucumber.runtime.formatter.DefaultSummaryPrinter")));
    }

    @Test
    public void allows_removal_of_strict_in_cucumber_options_property() {
        properties.setProperty("cucumber.options", "--no-strict");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Arrays.asList("--strict"));
        Assert.assertThat(options.isStrict(), CoreMatchers.is(false));
    }

    @Test
    public void fail_on_unsupported_options() {
        expectedException.expectMessage("Unknown option: -concreteUnsupportedOption");
        new RuntimeOptions(Arrays.asList("-concreteUnsupportedOption", "somewhere", "somewhere_else"));
    }

    @Test
    public void threads_default_1() {
        RuntimeOptions options = new RuntimeOptions(Collections.<String>emptyList());
        Assert.assertThat(options.getThreads(), CoreMatchers.is(1));
    }

    @Test
    public void ensure_threads_param_is_used() {
        RuntimeOptions options = new RuntimeOptions(Arrays.asList("--threads", "10"));
        Assert.assertThat(options.getThreads(), CoreMatchers.is(10));
    }

    @Test
    public void ensure_less_than_1_thread_is_not_allowed() {
        expectedException.expect(CucumberException.class);
        expectedException.expectMessage("--threads must be > 0");
        new RuntimeOptions(Arrays.asList("--threads", "0"));
    }

    @Test
    public void set_monochrome_on_color_aware_formatters() {
        RuntimeOptions options = new RuntimeOptions(new Env(), Arrays.asList("--monochrome", "--plugin", RuntimeOptionsTest.AwareFormatter.class.getName()));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        RuntimeOptionsTest.AwareFormatter formatter = ((RuntimeOptionsTest.AwareFormatter) (plugins.getPlugins().get(0)));
        Assert.assertThat(formatter.isMonochrome(), CoreMatchers.is(true));
    }

    @Test
    public void set_strict_on_strict_aware_formatters() {
        RuntimeOptions options = new RuntimeOptions(new Env(), Arrays.asList("--strict", "--plugin", RuntimeOptionsTest.AwareFormatter.class.getName()));
        Plugins plugins = new Plugins(getClass().getClassLoader(), new PluginFactory(), new cucumber.runner.TimeServiceEventBus(TimeService.SYSTEM), options);
        RuntimeOptionsTest.AwareFormatter formatter = ((RuntimeOptionsTest.AwareFormatter) (plugins.getPlugins().get(0)));
        Assert.assertThat(formatter.isStrict(), CoreMatchers.is(true));
    }

    @Test
    public void ensure_default_snippet_type_is_underscore() {
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Collections.<String>emptyList());
        Assert.assertThat(options.getSnippetType(), CoreMatchers.is(UNDERSCORE));
    }

    @Test
    public void set_snippet_type() {
        properties.setProperty("cucumber.options", "--snippets camelcase");
        RuntimeOptions options = new RuntimeOptions(new Env(properties), Collections.<String>emptyList());
        Assert.assertThat(options.getSnippetType(), CoreMatchers.is(CAMELCASE));
    }

    @Test
    public void loads_no_features_when_rerun_file_contains_carriage_return() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "\r";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), emptyCollectionOf(URI.class));
    }

    @Test
    public void loads_features_specified_in_rerun_file() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "file:path/bar.feature:2\n";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:path/bar.feature")));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:path/bar.feature"), Collections.singleton(2)));
    }

    @Test
    public void loads_no_features_when_rerun_file_is_empty() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), emptyCollectionOf(URI.class));
    }

    @Test
    public void loads_no_features_when_rerun_file_contains_new_line() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "\n";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), emptyCollectionOf(URI.class));
    }

    @Test
    public void loads_no_features_when_rerun_file_contains_new_line_and_carriage_return() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "\r\n";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), emptyCollectionOf(URI.class));
    }

    @Test
    public void last_new_line_is_optinal() throws Exception {
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = "file:path/bar.feature" + ":2";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:path/bar.feature")));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:path/bar.feature"), Collections.singleton(2)));
    }

    @Test
    public void loads_features_specified_in_rerun_file_from_classpath_when_not_in_file_system() throws Exception {
        String featurePath = "classpath:path/bar.feature";
        URI featureUri = RuntimeOptionsTest.uri(featurePath);
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = featurePath + ":2";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), contains(featureUri));
        Assert.assertThat(options.getLineFilters(), hasEntry(featureUri, Collections.singleton(2)));
    }

    @Test
    public void understands_whitespace_in_rerun_filepath() throws Exception {
        String featurePath1 = "My Documents/tests/bar.feature";
        String rerunPath = "file:rerun.txt";
        String rerunFile = featurePath1 + ":2\n";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri("file:My%20Documents/tests/bar.feature")));
        Assert.assertThat(options.getLineFilters(), hasEntry(RuntimeOptionsTest.uri("file:My%20Documents/tests/bar.feature"), Collections.singleton(2)));
    }

    @Test
    public void understands_rerun_files_separated_by_with_whitespace() throws Exception {
        String featurePath1 = "file:/home/users/mp/My%20Documents/tests/bar.feature";
        String featurePath2 = "file:/home/users/mp/My%20Documents/tests/foo.feature";
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = ((featurePath1 + ":2 ") + featurePath2) + ":4";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), CoreMatchers.is(Arrays.asList(RuntimeOptionsTest.uri(featurePath1), RuntimeOptionsTest.uri(featurePath2))));
    }

    @Test
    public void understands_rerun_files_without_separation_in_rerun_filepath() throws Exception {
        String featurePath1 = "file:/home/users/mp/My%20Documents/tests/bar.feature";
        String featurePath2 = "file:/home/users/mp/My%20Documents/tests/foo.feature";
        String rerunPath = "file:path/rerun.txt";
        String rerunFile = ((featurePath1 + ":2") + featurePath2) + ":4";
        RuntimeOptionsTest.mockFileResource(resourceLoader, rerunPath, rerunFile);
        RuntimeOptions options = new RuntimeOptions(resourceLoader, new Env(properties), Collections.singletonList(("@" + rerunPath)));
        Assert.assertThat(options.getFeaturePaths(), contains(RuntimeOptionsTest.uri(featurePath1), RuntimeOptionsTest.uri(featurePath2)));
    }

    public static final class AwareFormatter implements EventListener , ColorAware , StrictAware {
        private boolean strict;

        private boolean monochrome;

        private boolean isStrict() {
            return strict;
        }

        @Override
        public void setStrict(boolean strict) {
            this.strict = strict;
        }

        boolean isMonochrome() {
            return monochrome;
        }

        @Override
        public void setMonochrome(boolean monochrome) {
            this.monochrome = monochrome;
        }

        @Override
        public void setEventPublisher(EventPublisher publisher) {
        }
    }
}

