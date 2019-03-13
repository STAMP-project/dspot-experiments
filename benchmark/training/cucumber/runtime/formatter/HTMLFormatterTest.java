package cucumber.runtime.formatter;


import cucumber.api.Result;
import cucumber.runner.TestHelper;
import cucumber.runtime.model.CucumberFeature;
import cucumber.util.FixJava;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;


public class HTMLFormatterTest {
    private static final String jsFunctionCallRegexString = "formatter.(\\w*)\\(([^)]*)\\);";

    private static final Pattern jsFunctionCallRegex = Pattern.compile(HTMLFormatterTest.jsFunctionCallRegexString);

    private final List<CucumberFeature> features = new ArrayList<>();

    private final Map<String, Result> stepsToResult = new HashMap<>();

    private final Map<String, String> stepsToLocation = new HashMap<>();

    private final List<AbstractMap.SimpleEntry<String, Result>> hooks = new ArrayList<>();

    private final List<String> hookLocations = new ArrayList<>();

    private final List<Answer<Object>> hookActions = new ArrayList<>();

    private Long stepDuration = null;

    private URL outputDir;

    @Test
    public void writes_index_html() throws Throwable {
        writeReport();
        URL indexHtml = new URL(outputDir, "index.html");
        Document document = Jsoup.parse(new File(indexHtml.getFile()), "UTF-8");
        Element reportElement = document.body().getElementsByClass("cucumber-report").first();
        Assert.assertEquals("", reportElement.text());
    }

    @Test
    public void writes_valid_report_js() throws Throwable {
        writeReport();
        String reportJs = FixJava.readReader(new InputStreamReader(new URL(outputDir, "report.js").openStream(), "UTF-8"));
        assertJsFunctionCallSequence(Arrays.asList(("" + "formatter.uri(\"file:some/path/some.feature\");\n"), ("formatter.feature({\n" + ((("  \"name\": \"\",\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Feature\"\n") + "});\n")), ("formatter.scenario({\n" + ((("  \"name\": \"some cukes\",\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\"\n") + "});\n")), ("formatter.step({\n" + (("  \"name\": \"first step\",\n" + "  \"keyword\": \"Given \"\n") + "});\n")), ("formatter.match({\n" + ("  \"location\": \"path/step_definitions.java:3\"\n" + "});\n")), ("formatter.result({\n" + ("  \"status\": \"passed\"\n" + "});\n")), "formatter.embedding(\"image/png\", \"embedded0.png\");\n", ("formatter.after({\n" + ("  \"status\": \"passed\"\n" + "});\n")), "formatter.embedding(\"text/plain\", \"dodgy stack trace here\");\n", ("formatter.after({\n" + ("  \"status\": \"passed\"\n" + "});\n"))), reportJs);
    }

    @Test
    public void includes_uri() throws Throwable {
        writeReport();
        String reportJs = FixJava.readReader(new InputStreamReader(new URL(outputDir, "report.js").openStream(), "UTF-8"));
        assertContains("formatter.uri(\"file:some/path/some.feature\");", reportJs);
    }

    @Test
    public void included_embedding() throws Throwable {
        writeReport();
        String reportJs = FixJava.readReader(new InputStreamReader(new URL(outputDir, "report.js").openStream(), "UTF-8"));
        assertContains("formatter.embedding(\"image/png\", \"embedded0.png\");", reportJs);
        assertContains("formatter.embedding(\"text/plain\", \"dodgy stack trace here\");", reportJs);
    }

    @Test
    public void should_handle_a_single_scenario() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    Then second step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        stepsToLocation.put("second step", "path/step_definitions.java:7");
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + "formatter.uri(\"file:path/test.feature\");\n"), ("" + (((("formatter.feature({\n" + "  \"description\": \"\",\n") + "  \"name\": \"feature name\",\n") + "  \"keyword\": \"Feature\"\n") + "});\n")), ("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"second step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:7\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});"))), formatterOutput);
    }

    @Test
    public void should_handle_backgound() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: feature name\n" + "  Background: background name\n") + "    Given first step\n") + "  Scenario: scenario 1\n") + "    Then second step\n") + "  Scenario: scenario 2\n") + "    Then third step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        stepsToLocation.put("second step", "path/step_definitions.java:7");
        stepsToLocation.put("third step", "path/step_definitions.java:11");
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.background({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Background\",\n") + "  \"name\": \"background name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario 1\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"second step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:7\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (((("formatter.background({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Background\",\n") + "  \"name\": \"background name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario 2\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"third step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:11\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_scenario_outline() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((((("Feature: feature name\n" + "  Scenario Outline: outline name\n") + "    Given first step\n") + "    Then <arg> step\n") + "    Examples: examples name\n") + "      |  arg   |\n") + "      | second |\n") + "      | third  |\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToResult.put("third step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        stepsToLocation.put("second step", "path/step_definitions.java:7");
        stepsToLocation.put("third step", "path/step_definitions.java:11");
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + "formatter.uri(\"file:path/test.feature\");\n"), ("" + (((("formatter.feature({\n" + "  \"description\": \"\",\n") + "  \"name\": \"feature name\",\n") + "  \"keyword\": \"Feature\"\n") + "});\n")), ("" + (((("formatter.scenarioOutline({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario Outline\",\n") + "  \"name\": \"outline name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"\\u003carg\\u003e step\"\n") + "});\n")), ("" + ((((((((((((((((((((("formatter.examples({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Examples\",\n") + "  \"name\": \"examples name\",\n") + "  \"rows\": [\n") + "    {\n") + "      \"cells\": [\n") + "        \"arg\"\n") + "      ]\n") + "    },\n") + "    {\n") + "      \"cells\": [\n") + "        \"second\"\n") + "      ]\n") + "    },\n") + "    {\n") + "      \"cells\": [\n") + "        \"third\"\n") + "      ]\n") + "    }\n") + "  ]\n") + "});\n")), ("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario Outline\",\n") + "  \"name\": \"outline name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"second step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:7\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario Outline\",\n") + "  \"name\": \"outline name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Then \",\n") + "  \"name\": \"third step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:11\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});"))), formatterOutput);
    }

    @Test
    public void should_handle_before_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + (("formatter.before({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_after_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("passed")));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (("formatter.after({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_after_step_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    When second step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        stepsToLocation.put("second step", "path/step_definitions.java:4");
        hooks.add(TestHelper.hookEntry("afterstep", TestHelper.result("passed")));
        hooks.add(TestHelper.hookEntry("afterstep", TestHelper.result("passed")));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"When \",\n") + "  \"name\": \"second step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:4\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_output_from_before_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hookActions.add(TestHelper.createWriteHookAction("printed from hook"));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + "formatter.write(\"printed from hook\");\n"), ("" + (("formatter.before({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_output_from_after_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("passed")));
        hookActions.add(TestHelper.createWriteHookAction("printed from hook"));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + "formatter.write(\"printed from hook\");\n"), ("" + (("formatter.after({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_output_from_after_step_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n") + "    When second step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToResult.put("second step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        stepsToLocation.put("second step", "path/step_definitions.java:4");
        hooks.add(TestHelper.hookEntry("afterstep", TestHelper.result("passed")));
        hookActions.add(TestHelper.createWriteHookAction("printed from hook"));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + "formatter.write(\"printed from hook\");\n"), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"When \",\n") + "  \"name\": \"second step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:4\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + "formatter.write(\"printed from hook\");\n"), ("" + (("formatter.afterstep({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }

    @Test
    public void should_handle_text_embeddings_from_before_hooks() throws Throwable {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (("Feature: feature name\n" + "  Scenario: scenario name\n") + "    Given first step\n")));
        features.add(feature);
        stepsToResult.put("first step", TestHelper.result("passed"));
        stepsToLocation.put("first step", "path/step_definitions.java:3");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hookActions.add(TestHelper.createEmbedHookAction("embedded from hook".getBytes("US-ASCII"), "text/ascii"));
        stepDuration = 1L;
        String formatterOutput = runFeaturesWithFormatter();
        assertJsFunctionCallSequence(Arrays.asList(("" + (((("formatter.scenario({\n" + "  \"description\": \"\",\n") + "  \"keyword\": \"Scenario\",\n") + "  \"name\": \"scenario name\"\n") + "});\n")), ("" + "formatter.embedding(\"text/ascii\", \"embedded from hook\");\n"), ("" + (("formatter.before({\n" + "  \"status\": \"passed\"\n") + "});\n")), ("" + ((("formatter.step({\n" + "  \"keyword\": \"Given \",\n") + "  \"name\": \"first step\"\n") + "});\n")), ("" + (("formatter.match({\n" + "  \"location\": \"path/step_definitions.java:3\"\n") + "});\n")), ("" + (("formatter.result({\n" + "  \"status\": \"passed\"\n") + "});\n"))), formatterOutput);
    }
}

