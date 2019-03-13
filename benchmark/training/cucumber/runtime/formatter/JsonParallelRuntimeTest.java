package cucumber.runtime.formatter;


import org.junit.Assert;
import org.junit.Test;
import uk.co.datumedge.hamcrest.json.SameJSONAs;


// TODO: Merge with the existing test
public class JsonParallelRuntimeTest {
    @Test
    public void testSingleFeature() {
        StringBuilder parallel = new StringBuilder();
        builder().withArgs("--threads", "3", "src/test/resources/cucumber/runtime/formatter/JSONPrettyFormatterTest.feature").withAdditionalPlugins(new JSONFormatter(parallel)).build().run();
        StringBuilder serial = new StringBuilder();
        builder().withArgs("--threads", "1", "src/test/resources/cucumber/runtime/formatter/JSONPrettyFormatterTest.feature").withAdditionalPlugins(new JSONFormatter(serial)).build().run();
        Assert.assertThat(parallel.toString(), SameJSONAs.sameJSONAs(serial.toString()).allowingAnyArrayOrdering());
    }

    @Test
    public void testMultipleFeatures() {
        StringBuilder parallel = new StringBuilder();
        builder().withArgs("--threads", "3", "src/test/resources/cucumber/runtime/formatter/JSONPrettyFormatterTest.feature", "src/test/resources/cucumber/runtime/formatter/FormatterInParallel.feature").withAdditionalPlugins(new JSONFormatter(parallel)).build().run();
        StringBuilder serial = new StringBuilder();
        builder().withArgs("--threads", "1", "src/test/resources/cucumber/runtime/formatter/JSONPrettyFormatterTest.feature", "src/test/resources/cucumber/runtime/formatter/FormatterInParallel.feature").withAdditionalPlugins(new JSONFormatter(serial)).build().run();
        Assert.assertThat(parallel.toString(), SameJSONAs.sameJSONAs(serial.toString()).allowingAnyArrayOrdering());
    }
}

