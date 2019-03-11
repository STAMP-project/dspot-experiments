package org.baeldung.guava;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class GuavaMapInitializeUnitTest {
    @Test
    public void givenKeyValuesShoudInitializeMap() {
        Map<String, String> articles = ImmutableMap.of("Title", "My New Article", "Title2", "Second Article");
        Assert.assertThat(articles.get("Title"), IsEqual.equalTo("My New Article"));
        Assert.assertThat(articles.get("Title2"), IsEqual.equalTo("Second Article"));
    }

    @Test
    public void givenKeyValuesShouldCreateMutableMap() {
        Map<String, String> articles = Maps.newHashMap(ImmutableMap.of("Title", "My New Article", "Title2", "Second Article"));
        Assert.assertThat(articles.get("Title"), IsEqual.equalTo("My New Article"));
        Assert.assertThat(articles.get("Title2"), IsEqual.equalTo("Second Article"));
    }
}

