package org.mockserver.collections;


import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class CircularHashMapTest {
    @Test
    public void shouldNotContainMoreThenMaximumNumberOfEntries() {
        // given
        CircularHashMap<String, String> circularHashMap = new CircularHashMap<String, String>(3);
        // when
        circularHashMap.put("1", "1");
        circularHashMap.put("2", "2");
        circularHashMap.put("3", "3");
        circularHashMap.put("4", "4");
        // then
        Assert.assertEquals(3, circularHashMap.size());
        Assert.assertFalse(circularHashMap.containsKey("1"));
        Assert.assertTrue(circularHashMap.containsKey("2"));
        Assert.assertTrue(circularHashMap.containsKey("3"));
        Assert.assertTrue(circularHashMap.containsKey("4"));
    }

    @Test
    public void shouldFindKeyByObject() {
        // given
        CircularHashMap<String, String> circularHashMap = new CircularHashMap<String, String>(5);
        // when
        circularHashMap.put("0", "a");
        circularHashMap.put("1", "b");
        circularHashMap.put("2", "c");
        circularHashMap.put("3", "d");
        circularHashMap.put("4", "d");
        circularHashMap.put("5", "e");
        // then
        Assert.assertThat(circularHashMap.findKey("b"), Is.is("1"));
        Assert.assertThat(circularHashMap.findKey("c"), Is.is("2"));
        Assert.assertThat(circularHashMap.findKey("x"), CoreMatchers.nullValue());
        Assert.assertThat(circularHashMap.findKey("a"), CoreMatchers.nullValue());
        Assert.assertThat(circularHashMap.findKey("d"), Is.is("3"));
    }
}

