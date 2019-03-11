package de.westnordost.streetcomplete.data.osm.changes;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class StringMapEntryAddTest {
    @Test
    public void add() {
        StringMapEntryAdd c = new StringMapEntryAdd("a", "b");
        Map<String, String> m = new HashMap<>();
        Assert.assertEquals("ADD \"a\"=\"b\"", c.toString());
        Assert.assertFalse(c.conflictsWith(m));
        c.applyTo(m);
        Assert.assertEquals("b", m.get("a"));
        Assert.assertTrue(c.conflictsWith(m));
    }

    @Test
    public void reverse() {
        Map<String, String> m = new HashMap<>();
        StringMapEntryChange add = new StringMapEntryAdd("a", "b");
        StringMapEntryChange reverseAdd = add.reversed();
        add.applyTo(m);
        reverseAdd.applyTo(m);
        Assert.assertTrue(m.isEmpty());
    }
}

