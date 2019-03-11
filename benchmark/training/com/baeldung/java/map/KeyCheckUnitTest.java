package com.baeldung.java.map;


import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class KeyCheckUnitTest {
    @Test
    public void whenKeyIsPresent_thenContainsKeyReturnsTrue() {
        Map<String, String> map = Collections.singletonMap("key", "value");
        Assert.assertTrue(map.containsKey("key"));
        Assert.assertFalse(map.containsKey("missing"));
    }

    @Test
    public void whenKeyHasNullValue_thenGetStillWorks() {
        Map<String, String> map = Collections.singletonMap("nothing", null);
        Assert.assertTrue(map.containsKey("nothing"));
        Assert.assertNull(map.get("nothing"));
    }
}

