package io.cucumber.core.model;


import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public class FeatureWithLinesTest {
    @Test
    public void should_create_FileWithFilters_with_no_lines() {
        FeatureWithLines featureWithLines = FeatureWithLines.parse("foo.feature");
        Assert.assertEquals(URI.create("file:foo.feature"), featureWithLines.uri());
        Assert.assertThat(featureWithLines.lines(), emptyCollectionOf(Integer.class));
    }

    @Test
    public void should_create_FileWithFilters_with_1_line() {
        FeatureWithLines featureWithLines = FeatureWithLines.parse("foo.feature:999");
        Assert.assertEquals(URI.create("file:foo.feature"), featureWithLines.uri());
        Assert.assertThat(featureWithLines.lines(), contains(999));
    }

    @Test
    public void should_create_FileWithFilters_with_2_lines() {
        FeatureWithLines featureWithLines = FeatureWithLines.parse("foo.feature:999:2000");
        Assert.assertEquals(URI.create("file:foo.feature"), featureWithLines.uri());
        Assert.assertThat(featureWithLines.lines(), contains(999, 2000));
    }
}

