package de.westnordost.streetcomplete.data.osm.changes;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class StringMapEntryModifyTest {
    @Test
    public void modify() {
        StringMapEntryModify c = new StringMapEntryModify("a", "b", "c");
        Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        Assert.assertEquals("MODIFY \"a\"=\"b\" -> \"a\"=\"c\"", c.toString());
        Assert.assertFalse(c.conflictsWith(m));
        c.applyTo(m);
        Assert.assertTrue(c.conflictsWith(m));
    }

    @Test
    public void reverse() {
        StringMapEntryChange modify = new StringMapEntryModify("a", "b", "c");
        StringMapEntryChange reverseModify = modify.reversed();
        Map<String, String> m = new HashMap<>();
        m.put("a", "b");
        modify.applyTo(m);
        reverseModify.applyTo(m);
        Assert.assertEquals(1, m.size());
        Assert.assertEquals("b", m.get("a"));
    }
}

