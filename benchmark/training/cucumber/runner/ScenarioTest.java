package cucumber.runner;


import gherkin.pickles.PickleLocation;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ScenarioTest {
    @Test
    public void provides_the_uri_of_the_feature_file() {
        Scenario scenario = createScenarioWithFeatureFileUri(uri("path/file.feature"));
        Assert.assertEquals("path/file.feature", scenario.getUri());
    }

    @Test
    public void provides_the_scenario_line() {
        List<PickleLocation> scenarioLocation = Arrays.asList(new PickleLocation(line(3), column(2)));
        Scenario scenario = createScenarioWithScenarioLocations(scenarioLocation);
        Assert.assertEquals(Arrays.asList(3), scenario.getLines());
    }

    @Test
    public void provides_both_the_example_row_line_and_scenario_outline_line_for_scenarios_from_scenario_outlines() {
        List<PickleLocation> scenarioLocation = Arrays.asList(new PickleLocation(line(8), column(4)), new PickleLocation(line(3), column(2)));
        Scenario scenario = createScenarioWithScenarioLocations(scenarioLocation);
        Assert.assertEquals(Arrays.asList(8, 3), scenario.getLines());
    }

    @Test
    public void provides_the_uri_and_scenario_line_as_unique_id() {
        List<PickleLocation> scenarioLocation = Arrays.asList(new PickleLocation(line(3), column(2)));
        Scenario scenario = createScenarioWithFeatureFileUriAndScenarioLocations(uri("path/file.feature"), scenarioLocation);
        Assert.assertEquals("path/file.feature:3", scenario.getId());
    }

    @Test
    public void provides_the_uri_and_example_row_line_as_unique_id_for_scenarios_from_scenario_outlines() {
        List<PickleLocation> scenarioLocation = Arrays.asList(new PickleLocation(line(8), column(4)), new PickleLocation(line(3), column(2)));
        Scenario scenario = createScenarioWithFeatureFileUriAndScenarioLocations(uri("path/file.feature"), scenarioLocation);
        Assert.assertEquals("path/file.feature:8", scenario.getId());
    }
}

