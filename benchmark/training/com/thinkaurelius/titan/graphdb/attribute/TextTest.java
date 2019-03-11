package com.thinkaurelius.titan.graphdb.attribute;


import Cmp.EQUAL;
import Cmp.NOT_EQUAL;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TextTest {
    @Test
    public void testContains() {
        String text = "This world is full of 1funny surprises! A Full Yes";
        // Contains
        Assert.assertTrue(CONTAINS.test(text, "world"));
        Assert.assertTrue(CONTAINS.test(text, "wOrLD"));
        Assert.assertFalse(CONTAINS.test(text, "worl"));
        Assert.assertTrue(CONTAINS.test(text, "this"));
        Assert.assertTrue(CONTAINS.test(text, "yes"));
        Assert.assertFalse(CONTAINS.test(text, "funny"));
        Assert.assertFalse(CONTAINS.test(text, "a"));
        Assert.assertFalse(CONTAINS.test(text, "A"));
        Assert.assertTrue(CONTAINS.test(text, "surprises"));
        Assert.assertTrue(CONTAINS.test(text, "FULL"));
        Assert.assertTrue(CONTAINS.test(text, "full surprises"));
        Assert.assertTrue(CONTAINS.test(text, "full,surprises,world"));
        Assert.assertFalse(CONTAINS.test(text, "full bunny"));
        Assert.assertTrue(CONTAINS.test(text, "a world"));
        // Prefix
        Assert.assertTrue(CONTAINS_PREFIX.test(text, "worl"));
        Assert.assertTrue(CONTAINS_PREFIX.test(text, "wORl"));
        Assert.assertTrue(CONTAINS_PREFIX.test(text, "ye"));
        Assert.assertTrue(CONTAINS_PREFIX.test(text, "Y"));
        Assert.assertFalse(CONTAINS_PREFIX.test(text, "fo"));
        Assert.assertFalse(CONTAINS_PREFIX.test(text, "of 1f"));
        Assert.assertFalse(CONTAINS_PREFIX.test(text, "ses"));
        // Regex
        Assert.assertTrue(CONTAINS_REGEX.test(text, "fu[l]+"));
        Assert.assertTrue(CONTAINS_REGEX.test(text, "wor[ld]{1,2}"));
        Assert.assertTrue(CONTAINS_REGEX.test(text, "\\dfu\\w*"));
        Assert.assertFalse(CONTAINS_REGEX.test(text, "fo"));
        Assert.assertFalse(CONTAINS_REGEX.test(text, "wor[l]+"));
        Assert.assertFalse(CONTAINS_REGEX.test(text, "wor[ld]{3,5}"));
        String name = "fully funny";
        // Cmp
        Assert.assertTrue(EQUAL.test(name.toString(), name));
        Assert.assertFalse(NOT_EQUAL.test(name, name));
        Assert.assertFalse(EQUAL.test("fullly funny", name));
        Assert.assertTrue(NOT_EQUAL.test("fullly funny", name));
        // Prefix
        Assert.assertTrue(PREFIX.test(name, "fully"));
        Assert.assertTrue(PREFIX.test(name, "ful"));
        Assert.assertTrue(PREFIX.test(name, "fully fu"));
        Assert.assertFalse(PREFIX.test(name, "fun"));
        // REGEX
        Assert.assertTrue(REGEX.test(name, "(fu[ln]*y) (fu[ln]*y)"));
        Assert.assertFalse(REGEX.test(name, "(fu[l]*y) (fu[l]*y)"));
        Assert.assertTrue(REGEX.test(name, "(fu[l]*y) .*"));
    }
}

