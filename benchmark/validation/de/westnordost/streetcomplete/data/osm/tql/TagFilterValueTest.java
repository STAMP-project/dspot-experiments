package de.westnordost.streetcomplete.data.osm.tql;


import de.westnordost.osmapi.map.data.OsmNode;
import org.junit.Assert;
import org.junit.Test;


public class TagFilterValueTest {
    @Test
    public void matchesEqual() {
        TagFilterValue eq = new TagFilterValue("highway", "=", "residential");
        Assert.assertTrue(eq.matches(elementWithTag("highway", "residential")));
        Assert.assertFalse(eq.matches(elementWithTag("highway", "residental")));
        Assert.assertFalse(eq.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesNEqual() {
        TagFilterValue neq = new TagFilterValue("highway", "!=", "residential");
        Assert.assertFalse(neq.matches(elementWithTag("highway", "residential")));
        Assert.assertTrue(neq.matches(elementWithTag("highway", "residental")));
        Assert.assertTrue(neq.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesLikeDot() {
        TagFilterValue like = new TagFilterValue("highway", "~", ".esidential");
        Assert.assertTrue(like.matches(elementWithTag("highway", "residential")));
        Assert.assertTrue(like.matches(elementWithTag("highway", "wesidential")));
        Assert.assertFalse(like.matches(elementWithTag("highway", "rresidential")));
        Assert.assertFalse(like.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesLikeOr() {
        TagFilterValue like = new TagFilterValue("highway", "~", "residential|unclassified");
        Assert.assertTrue(like.matches(elementWithTag("highway", "residential")));
        Assert.assertTrue(like.matches(elementWithTag("highway", "unclassified")));
        Assert.assertFalse(like.matches(elementWithTag("highway", "blub")));
        Assert.assertFalse(like.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesNotLikeDot() {
        TagFilterValue notlike = new TagFilterValue("highway", "!~", ".*");
        Assert.assertFalse(notlike.matches(elementWithTag("highway", "anything")));
        Assert.assertTrue(notlike.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesNotLikeSomething() {
        TagFilterValue notlike = new TagFilterValue("noname", "!~", "yes");
        Assert.assertFalse(notlike.matches(elementWithTag("noname", "yes")));
        Assert.assertTrue(notlike.matches(elementWithTag("noname", "no")));
        Assert.assertTrue(notlike.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void matchesNoValue() {
        TagFilterValue key = new TagFilterValue("name", null, null);
        Assert.assertTrue(key.matches(elementWithTag("name", "yes")));
        Assert.assertTrue(key.matches(elementWithTag("name", "no")));
        Assert.assertFalse(key.matches(new OsmNode(0, 0, 0.0, 0.0, null)));
    }

    @Test
    public void toStringWorks() {
        TagFilterValue key = new TagFilterValue("A", "=", "B");
        Assert.assertEquals("\"A\"=\"B\"", key.toString());
        Assert.assertEquals("[\"A\"=\"B\"]", key.toOverpassQLString());
    }

    @Test
    public void toRegexString() {
        TagFilterValue key = new TagFilterValue("A", "~", "B");
        Assert.assertEquals("\"A\"~\"B\"", key.toString());
        Assert.assertEquals("[\"A\"~\"^(B)$\"]", key.toOverpassQLString());
    }

    @Test
    public void toRegexDotToString() {
        TagFilterValue key = new TagFilterValue("A", "!~", ".*");
        Assert.assertEquals("\"A\"!~\".*\"", key.toString());
        Assert.assertEquals("[\"A\"!~\".\"]", key.toOverpassQLString());
    }
}

