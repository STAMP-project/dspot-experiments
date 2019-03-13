package cucumber.runtime.formatter;


import cucumber.api.Result;
import cucumber.runner.TestHelper;
import cucumber.runtime.model.CucumberFeature;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import uk.co.datumedge.hamcrest.json.SameJSONAs;


public class JSONFormatterTest {
    private final List<CucumberFeature> features = new ArrayList<>();

    private final Map<String, Result> stepsToResult = new HashMap<>();

    private final Map<String, String> stepsToLocation = new HashMap<>();

    private final List<AbstractMap.SimpleEntry<String, Result>> hooks = new ArrayList<>();

    private final List<String> hookLocations = new ArrayList<>();

    private final List<Answer<Object>> hookActions = new ArrayList<>();

    private Long stepDuration = 0L;

    @Test
    public void featureWithOutlineTest() {
        String actual = runFeaturesWithFormatter(Arrays.asList("classpath:cucumber/runtime/formatter/JSONPrettyFormatterTest.feature"));
        String expected = new Scanner(getClass().getResourceAsStream("JSONPrettyFormatterTest.json"), "UTF-8").useDelimiter("\\A").next();
        Assert.assertThat(actual, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void featureWithOutlineTestParallel() throws Exception {
        String actual = runFeaturesWithFormatterInParallel(Arrays.asList("classpath:cucumber/runtime/formatter/JSONPrettyFormatterTest.feature"));
        String expected = new Scanner(getClass().getResourceAsStream("JSONPrettyFormatterTest.json"), "UTF-8").useDelimiter("\\A").next();
        Assert.assertThat(actual, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_an_undefined_step() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("undefined"));
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + ((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {},\n") + "            \"result\": {\n") + "              \"status\": \"undefined\"\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_a_passed_step() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_a_failed_step() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("failed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + ((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"failed\",\n") + "              \"error_message\": \"the stack trace\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_outline_with_one_example() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: Fruit party\n" + "\n") + "  Scenario Outline: Monkey eats fruits\n") + "    Given there are <fruits>\n") + "      Examples: Fruit table\n") + "      | fruits  |\n") + "      | bananas |\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"fruit-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Fruit party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"fruit-party;monkey-eats-fruits;fruit-table;2\",\n") + "        \"keyword\": \"Scenario Outline\",\n") + "        \"name\": \"Monkey eats fruits\",\n") + "        \"line\": 7,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_feature_with_background() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((((((("Feature: Banana party\n" + "\n") + "  Background: There are bananas\n") + "    Given there are bananas\n") + "\n") + "  Scenario: Monkey eats bananas\n") + "    Then the monkey eats bananas\n") + "\n") + "  Scenario: Monkey eats more bananas\n") + "    Then the monkey eats more bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToResult.put("the monkey eats bananas", TestHelper.result("passed"));
        stepsToResult.put("the monkey eats more bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepsToLocation.put("the monkey eats bananas", "StepDefs.monkey_eats_bananas()");
        stepsToLocation.put("the monkey eats more bananas", "StepDefs.monkey_eats_more_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"keyword\": \"Background\",\n") + "        \"name\": \"There are bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"background\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      },\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 6,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Then \",\n") + "            \"name\": \"the monkey eats bananas\",\n") + "            \"line\": 7,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.monkey_eats_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      },\n") + "      {\n") + "        \"keyword\": \"Background\",\n") + "        \"name\": \"There are bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"background\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      },\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-more-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats more bananas\",\n") + "        \"line\": 9,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Then \",\n") + "            \"name\": \"the monkey eats more bananas\",\n") + "            \"line\": 10,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.monkey_eats_more_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_feature_and_scenario_with_tags() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((("@Party @Banana\n" + "Feature: Banana party\n") + "  @Monkey\n") + "  Scenario: Monkey eats more bananas\n") + "    Then the monkey eats more bananas\n")));
        features.add(feature);
        stepsToResult.put("the monkey eats more bananas", TestHelper.result("passed"));
        stepsToLocation.put("the monkey eats more bananas", "StepDefs.monkey_eats_more_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"line\": 2,\n") + "    \"elements\": [\n") + "      {\n") + "        \"line\": 4,\n") + "        \"name\": \"Monkey eats more bananas\",\n") + "        \"description\": \"\",\n") + "        \"id\": \"banana-party;monkey-eats-more-bananas\",\n") + "        \"type\": \"scenario\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"result\": {\n") + "              \"duration\": 1000000,\n") + "              \"status\": \"passed\"\n") + "            },\n") + "            \"line\": 5,\n") + "            \"name\": \"the monkey eats more bananas\",\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.monkey_eats_more_bananas()\"\n") + "            },\n") + "            \"keyword\": \"Then \"\n") + "          }\n") + "        ],\n") + "        \"tags\": [\n") + "          {\n") + "            \"name\": \"@Party\"\n") + "          },\n") + "          {\n") + "            \"name\": \"@Banana\"\n") + "          },\n") + "          {\n") + "            \"name\": \"@Monkey\"\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"name\": \"Banana party\",\n") + "    \"description\": \"\",\n") + "    \"id\": \"banana-party\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"tags\": [\n") + "      {\n") + "        \"name\": \"@Party\",\n") + "        \"type\": \"Tag\",\n") + "        \"location\": {\n") + "          \"line\": 1,\n") + "          \"column\": 1\n") + "        }\n") + "      },\n") + "      {\n") + "        \"name\": \"@Banana\",\n") + "        \"type\": \"Tag\",\n") + "        \"location\": {\n") + "          \"line\": 1,\n") + "          \"column\": 8\n") + "        }\n") + "      }\n") + "    ]\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_hooks() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hooks.add(TestHelper.hookEntry("after", TestHelper.result("passed")));
        hookLocations.add("Hooks.before_hook_1()");
        hookLocations.add("Hooks.after_hook_1()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"before\": [\n") + "          {\n") + "            \"match\": {\n") + "              \"location\": \"Hooks.before_hook_1()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ],\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ],\n") + "        \"after\": [\n") + "          {\n") + "            \"match\": {\n") + "              \"location\": \"Hooks.after_hook_1()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_add_step_hooks_to_step() {
        CucumberFeature feature = TestHelper.feature("file:path/test.feature", ("" + (((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n") + "    When monkey arrives\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToResult.put("monkey arrives", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepsToLocation.put("monkey arrives", "StepDefs.monkey_arrives()");
        hooks.add(TestHelper.hookEntry("beforestep", TestHelper.result("passed")));
        hooks.add(TestHelper.hookEntry("afterstep", TestHelper.result("passed")));
        hooks.add(TestHelper.hookEntry("afterstep", TestHelper.result("passed")));
        hookLocations.add("Hooks.beforestep_hooks_1()");
        hookLocations.add("Hooks.afterstep_hooks_1()");
        hookLocations.add("Hooks.afterstep_hooks_2()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"line\": 1,\n") + "    \"elements\": [\n") + "      {\n") + "        \"line\": 3,\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"description\": \"\",\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"type\": \"scenario\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"result\": {\n") + "              \"duration\": 1000000,\n") + "              \"status\": \"passed\"\n") + "            },\n") + "            \"before\": [\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.beforestep_hooks_1()\"\n") + "                }\n") + "              }\n") + "            ],\n") + "            \"line\": 4,\n") + "            \"name\": \"there are bananas\",\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"after\": [\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.afterstep_hooks_1()\"\n") + "                }\n") + "              },\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.afterstep_hooks_2()\"\n") + "                }\n") + "              }\n") + "            ],\n") + "            \"keyword\": \"Given \"\n") + "          },\n") + "          {\n") + "            \"result\": {\n") + "              \"duration\": 1000000,\n") + "              \"status\": \"passed\"\n") + "            },\n") + "            \"before\": [\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.beforestep_hooks_1()\"\n") + "                }\n") + "              }\n") + "            ],\n") + "            \"line\": 5,\n") + "            \"name\": \"monkey arrives\",\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.monkey_arrives()\"\n") + "            },\n") + "            \"after\": [\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.afterstep_hooks_1()\"\n") + "                }\n") + "              },\n") + "              {\n") + "                \"result\": {\n") + "                  \"duration\": 1000000,\n") + "                  \"status\": \"passed\"\n") + "                },\n") + "                \"match\": {\n") + "                  \"location\": \"Hooks.afterstep_hooks_2()\"\n") + "                }\n") + "              }\n") + "            ],\n") + "            \"keyword\": \"When \"\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"name\": \"Banana party\",\n") + "    \"description\": \"\",\n") + "    \"id\": \"banana-party\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_handle_write_from_a_hook() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hookLocations.add("Hooks.before_hook_1()");
        hookActions.add(TestHelper.createWriteHookAction("printed from hook"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"before\": [\n") + "          {\n") + "            \"match\": {\n") + "              \"location\": \"Hooks.before_hook_1()\"\n") + "            },\n") + "            \"output\": [\n") + "              \"printed from hook\"\n") + "            ],\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ],\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_handle_embed_from_a_hook() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        hooks.add(TestHelper.hookEntry("before", TestHelper.result("passed")));
        hookLocations.add("Hooks.before_hook_1()");
        hookActions.add(TestHelper.createEmbedHookAction(new byte[]{ 1, 2, 3 }, "mime-type;base64"));
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + ((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"before\": [\n") + "          {\n") + "            \"match\": {\n") + "              \"location\": \"Hooks.before_hook_1()\"\n") + "            },\n") + "            \"embeddings\": [\n") + "              {\n") + "                \"mime_type\": \"mime-type;base64\",\n") + "                \"data\": \"AQID\"\n") + "              }\n") + "            ],\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ],\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_a_step_with_a_doc_string() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n") + "    \"\"\"\n") + "    doc string content\n") + "    \"\"\"\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"doc_string\": {\n") + "              \"value\": \"doc string content\",\n") + "              \"line\": 5\n") + "            },\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_a_step_with_a_doc_string_and_content_type() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + (((((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n") + "    \"\"\"doc\n") + "    doc string content\n") + "    \"\"\"\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + ((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"doc_string\": {\n") + "              \"content_type\": \"doc\",\n") + "              \"value\": \"doc string content\",\n") + "              \"line\": 5\n") + "            },\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_format_scenario_with_a_step_with_a_data_table() {
        CucumberFeature feature = TestHelper.feature("path/test.feature", ("" + ((((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n") + "      | aa | 11 |\n") + "      | bb | 22 |\n")));
        features.add(feature);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + (((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"rows\": [\n") + "              {\n") + "                \"cells\": [\n") + "                  \"aa\",\n") + "                  \"11\"\n") + "                ]\n") + "              },\n") + "              {\n") + "                \"cells\": [\n") + "                  \"bb\",\n") + "                  \"22\"\n") + "                ]\n") + "              }\n") + "            ],\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }

    @Test
    public void should_handle_several_features() {
        CucumberFeature feature1 = TestHelper.feature("file:path/test1.feature", ("" + ((("Feature: Banana party\n" + "\n") + "  Scenario: Monkey eats bananas\n") + "    Given there are bananas\n")));
        CucumberFeature feature2 = TestHelper.feature("path/test2.feature", ("" + ((("Feature: Orange party\n" + "\n") + "  Scenario: Monkey eats oranges\n") + "    Given there are oranges\n")));
        features.add(feature1);
        features.add(feature2);
        stepsToResult.put("there are bananas", TestHelper.result("passed"));
        stepsToResult.put("there are oranges", TestHelper.result("passed"));
        stepsToLocation.put("there are bananas", "StepDefs.there_are_bananas()");
        stepsToLocation.put("there are oranges", "StepDefs.there_are_oranges()");
        stepDuration = milliSeconds(1);
        String formatterOutput = runFeaturesWithFormatter();
        String expected = "" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("[\n" + "  {\n") + "    \"id\": \"banana-party\",\n") + "    \"uri\": \"file:path/test1.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Banana party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"banana-party;monkey-eats-bananas\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats bananas\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are bananas\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_bananas()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  },\n") + "  {\n") + "    \"id\": \"orange-party\",\n") + "    \"uri\": \"file:path/test2.feature\",\n") + "    \"keyword\": \"Feature\",\n") + "    \"name\": \"Orange party\",\n") + "    \"line\": 1,\n") + "    \"description\": \"\",\n") + "    \"elements\": [\n") + "      {\n") + "        \"id\": \"orange-party;monkey-eats-oranges\",\n") + "        \"keyword\": \"Scenario\",\n") + "        \"name\": \"Monkey eats oranges\",\n") + "        \"line\": 3,\n") + "        \"description\": \"\",\n") + "        \"type\": \"scenario\",\n") + "        \"steps\": [\n") + "          {\n") + "            \"keyword\": \"Given \",\n") + "            \"name\": \"there are oranges\",\n") + "            \"line\": 4,\n") + "            \"match\": {\n") + "              \"location\": \"StepDefs.there_are_oranges()\"\n") + "            },\n") + "            \"result\": {\n") + "              \"status\": \"passed\",\n") + "              \"duration\": 1000000\n") + "            }\n") + "          }\n") + "        ]\n") + "      }\n") + "    ],\n") + "    \"tags\": []\n") + "  }\n") + "]");
        Assert.assertThat(formatterOutput, SameJSONAs.sameJSONAs(expected));
    }
}

