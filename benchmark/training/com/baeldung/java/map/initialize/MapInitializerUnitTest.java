package com.baeldung.java.map.initialize;


import MapInitializer.articleMapOne;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MapInitializerUnitTest {
    @Test
    public void givenStaticMap_whenUpdated_thenCorrect() {
        articleMapOne.put("NewArticle1", "Convert array to List");
        Assert.assertEquals(articleMapOne.get("NewArticle1"), "Convert array to List");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenSingleTonMap_whenEntriesAdded_throwsException() {
        Map<String, String> map = MapInitializer.createSingletonMap();
        map.put("username2", "password2");
    }
}

