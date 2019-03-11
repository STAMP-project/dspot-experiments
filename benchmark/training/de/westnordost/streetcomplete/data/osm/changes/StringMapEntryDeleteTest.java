package de.westnordost.streetcomplete.data.osm.changes;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class StringMapEntryDeleteTest {
    @Test
    public void delete() {
        StringMapEntryDelete c = new StringMapEntryDelete("a", "b");
        Map<String, String> m = new HashMap<>();
        m.put("a", "c");
        Assert.assertEquals("DELETE \"a\"=\"b\"", c.toString());
        Assert.assertTrue(c.conflictsWith(m));
        m.put("a", "b");
        Assert.assertFalse(c.conflictsWith(m));
        c.applyTo(m);
        Assert.assertFalse(m.containsKey("a"));
        Assert.assertTrue(c.conflictsWith(m));
    }

    @Test
    public void reverse() {
        Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        StringMapEntryChange delete = new StringMapEntryDelete("a", "b");
        StringMapEntryChange reverseDelete = delete.reversed();
        delete.applyTo(m);
        reverseDelete.applyTo(m);
        Assert.assertEquals(1, m.size());
        Assert.assertEquals("b", m.get("a"));
    }
}

