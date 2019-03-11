package org.hswebframework.web;


import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


public class MapsTests {
    @Test
    public void testCreateMap() {
        Assert.assertEquals(Maps.buildMap().put("1", 1).get().get("1"), 1);
        Assert.assertEquals(Maps.buildMap(new HashMap()).put("1", 1).get().get("1"), 1);
        Assert.assertEquals(Maps.buildMap(HashMap::new).put("1", 1).get().get("1"), 1);
    }
}

