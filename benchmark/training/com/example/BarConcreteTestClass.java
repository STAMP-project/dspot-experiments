package com.example;


import com.example.cache.Cache;
import java.util.Optional;
import org.junit.Test;


public class BarConcreteTestClass extends AbstractIntegrationTest {
    private Cache cache;

    @Test
    public void testInsertValue() {
        cache.put("bar", "BAR");
        Optional<String> foundObject = cache.get("bar", String.class);
        assertTrue("When inserting an object into the cache, it can be retrieved", foundObject.isPresent());
        assertEquals("When accessing the value of a retrieved object, the value must be the same", "BAR", foundObject.get());
    }
}

