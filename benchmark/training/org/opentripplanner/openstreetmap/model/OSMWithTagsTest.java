package org.opentripplanner.openstreetmap.model;


import org.junit.Assert;
import org.junit.Test;


public class OSMWithTagsTest {
    @Test
    public void testHasTag() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertFalse(o.hasTag("foo"));
        Assert.assertFalse(o.hasTag("FOO"));
        o.addTag("foo", "bar");
        Assert.assertTrue(o.hasTag("foo"));
        Assert.assertTrue(o.hasTag("FOO"));
    }

    @Test
    public void testGetTag() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertNull(o.getTag("foo"));
        Assert.assertNull(o.getTag("FOO"));
        o.addTag("foo", "bar");
        Assert.assertEquals("bar", o.getTag("foo"));
        Assert.assertEquals("bar", o.getTag("FOO"));
    }

    @Test
    public void testIsFalse() {
        Assert.assertTrue(OSMWithTags.isFalse("no"));
        Assert.assertTrue(OSMWithTags.isFalse("0"));
        Assert.assertTrue(OSMWithTags.isFalse("false"));
        Assert.assertFalse(OSMWithTags.isFalse("yes"));
        Assert.assertFalse(OSMWithTags.isFalse("1"));
        Assert.assertFalse(OSMWithTags.isFalse("true"));
        Assert.assertFalse(OSMWithTags.isFalse("foo"));
        Assert.assertFalse(OSMWithTags.isFalse("bar"));
        Assert.assertFalse(OSMWithTags.isFalse("baz"));
    }

    @Test
    public void testIsTrue() {
        Assert.assertTrue(OSMWithTags.isTrue("yes"));
        Assert.assertTrue(OSMWithTags.isTrue("1"));
        Assert.assertTrue(OSMWithTags.isTrue("true"));
        Assert.assertFalse(OSMWithTags.isTrue("no"));
        Assert.assertFalse(OSMWithTags.isTrue("0"));
        Assert.assertFalse(OSMWithTags.isTrue("false"));
        Assert.assertFalse(OSMWithTags.isTrue("foo"));
        Assert.assertFalse(OSMWithTags.isTrue("bar"));
        Assert.assertFalse(OSMWithTags.isTrue("baz"));
    }

    @Test
    public void testIsTagFalseOrTrue() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertFalse(o.isTagFalse("foo"));
        Assert.assertFalse(o.isTagFalse("FOO"));
        Assert.assertFalse(o.isTagTrue("foo"));
        Assert.assertFalse(o.isTagTrue("FOO"));
        o.addTag("foo", "true");
        Assert.assertFalse(o.isTagFalse("foo"));
        Assert.assertFalse(o.isTagFalse("FOO"));
        Assert.assertTrue(o.isTagTrue("foo"));
        Assert.assertTrue(o.isTagTrue("FOO"));
        o.addTag("foo", "no");
        Assert.assertTrue(o.isTagFalse("foo"));
        Assert.assertTrue(o.isTagFalse("FOO"));
        Assert.assertFalse(o.isTagTrue("foo"));
        Assert.assertFalse(o.isTagTrue("FOO"));
    }

    @Test
    public void testDoesAllowTagAccess() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertFalse(o.doesTagAllowAccess("foo"));
        o.addTag("foo", "bar");
        Assert.assertFalse(o.doesTagAllowAccess("foo"));
        o.addTag("foo", "designated");
        Assert.assertTrue(o.doesTagAllowAccess("foo"));
        o.addTag("foo", "official");
        Assert.assertTrue(o.doesTagAllowAccess("foo"));
    }

    @Test
    public void testIsGeneralAccessDenied() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertFalse(o.isGeneralAccessDenied());
        o.addTag("access", "something");
        Assert.assertFalse(o.isGeneralAccessDenied());
        o.addTag("access", "license");
        Assert.assertTrue(o.isGeneralAccessDenied());
        o.addTag("access", "no");
        Assert.assertTrue(o.isGeneralAccessDenied());
    }

    @Test
    public void testIsThroughTrafficExplicitlyDisallowed() {
        OSMWithTags o = new OSMWithTags();
        Assert.assertFalse(o.isThroughTrafficExplicitlyDisallowed());
        o.addTag("access", "something");
        Assert.assertFalse(o.isThroughTrafficExplicitlyDisallowed());
        o.addTag("access", "destination");
        Assert.assertTrue(o.isThroughTrafficExplicitlyDisallowed());
        o.addTag("access", "forestry");
        Assert.assertTrue(o.isThroughTrafficExplicitlyDisallowed());
        o.addTag("access", "private");
        Assert.assertTrue(o.isThroughTrafficExplicitlyDisallowed());
    }
}

