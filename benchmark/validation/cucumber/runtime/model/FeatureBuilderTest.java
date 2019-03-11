package cucumber.runtime.model;


import cucumber.runtime.io.Resource;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FeatureBuilderTest {
    @Test
    public void ignores_duplicate_features() throws IOException {
        FeatureBuilder builder = new FeatureBuilder();
        URI featurePath = URI.create("foo.feature");
        Resource resource1 = createResourceMock(featurePath);
        Resource resource2 = createResourceMock(featurePath);
        builder.parse(resource1);
        builder.parse(resource2);
        List<CucumberFeature> features = builder.build();
        Assert.assertEquals(1, features.size());
    }

    @Test
    public void works_when_path_and_uri_are_the_same() throws IOException {
        URI featurePath = URI.create("path/foo.feature");
        Resource resource = createResourceMock(featurePath);
        FeatureBuilder builder = new FeatureBuilder();
        builder.parse(resource);
        List<CucumberFeature> features = builder.build();
        Assert.assertEquals(1, features.size());
        Assert.assertEquals(featurePath, features.get(0).getUri());
    }
}

