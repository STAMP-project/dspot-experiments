package io.cucumber.core.model;


import java.net.URI;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class FeatureIdentifierTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void can_parse_feature_path_with_feature() {
        URI uri = FeatureIdentifier.parse(FeaturePath.parse("classpath:/path/to/file.feature"));
        Assert.assertEquals("classpath", uri.getScheme());
        Assert.assertEquals("/path/to/file.feature", uri.getSchemeSpecificPart());
    }

    @Test
    public void reject_feature_with_lines() {
        expectedException.expectMessage("featureIdentifier does not reference a single feature file");
        FeatureIdentifier.parse(URI.create("classpath:/path/to/file.feature:10:40"));
    }

    @Test
    public void reject_directory_form() {
        expectedException.expectMessage("featureIdentifier does not reference a single feature file");
        FeatureIdentifier.parse(URI.create("classpath:/path/to"));
    }
}

