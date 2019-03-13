package org.mockserver.collections;


import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author jamesdbloom
 */
public class CircularMultiMapTest {
    @Test
    public void shouldStoreMultipleValuesAgainstSingleKey() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        // when
        circularMultiMap.put("1", "1_1");
        circularMultiMap.put("1", "1_2");
        circularMultiMap.put("1", "1_3");
        circularMultiMap.put("2", "2");
        // then
        Assert.assertEquals(Arrays.asList("1_1", "1_2", "1_3"), circularMultiMap.getAll("1"));
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.getAll("2"));
        Assert.assertEquals(Arrays.asList("1", "1", "1", "2"), circularMultiMap.getKeyListForValues());
    }

    @Test
    public void shouldNotContainMoreThenMaximumNumberOfKeys() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        // when
        circularMultiMap.put("1", "1");
        circularMultiMap.put("2", "2");
        circularMultiMap.put("3", "3");
        circularMultiMap.put("4", "4");
        // then
        Assert.assertEquals(3, circularMultiMap.size());
        Assert.assertFalse(circularMultiMap.containsKey("1"));
        Assert.assertTrue(circularMultiMap.containsKey("2"));
        Assert.assertTrue(circularMultiMap.containsKey("3"));
        Assert.assertTrue(circularMultiMap.containsKey("4"));
        Assert.assertEquals(Sets.newHashSet("2", "3", "4"), circularMultiMap.keySet());
        Assert.assertEquals(Arrays.asList("2", "3", "4"), circularMultiMap.getKeyListForValues());
    }

    @Test
    public void shouldNotAllowAddingMoreThenMaximumNumberOfValuePerKey() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        // when
        circularMultiMap.put("1", "1");
        circularMultiMap.put("1", "2");
        circularMultiMap.put("1", "3");
        circularMultiMap.put("1", "4");
        circularMultiMap.put("1", "5");
        circularMultiMap.put("2", "2");
        circularMultiMap.put("2", "3");
        // then
        // - should have correct keys
        Assert.assertTrue(circularMultiMap.containsKey("1"));
        Assert.assertTrue(circularMultiMap.containsKey("2"));
        Assert.assertEquals(Sets.newHashSet("1", "2"), circularMultiMap.keySet());
        Assert.assertEquals(Arrays.asList("1", "1", "1", "2", "2"), circularMultiMap.getKeyListForValues());
        // - should have correct values
        Assert.assertFalse(circularMultiMap.containsValue("1"));
        Assert.assertTrue(circularMultiMap.containsValue("2"));
        Assert.assertTrue(circularMultiMap.containsValue("3"));
        Assert.assertTrue(circularMultiMap.containsValue("4"));
        Assert.assertTrue(circularMultiMap.containsValue("5"));
        Assert.assertEquals(Arrays.asList("3", "4", "5", "2", "3"), circularMultiMap.values());
        // - should have correct values per key
        Assert.assertEquals("3", circularMultiMap.get("1"));
        Assert.assertEquals("2", circularMultiMap.get("2"));
        Assert.assertEquals(Arrays.asList("3", "4", "5"), circularMultiMap.getAll("1"));
        Assert.assertEquals(Arrays.asList("2", "3"), circularMultiMap.getAll("2"));
    }

    @Test
    public void shouldSupportPuttingAllEntriesInAMap() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        // when
        circularMultiMap.put("1", "1_1");
        circularMultiMap.putAll(new HashMap<String, String>() {
            private static final long serialVersionUID = -580164440676146851L;

            {
                put("1", "1_2");
                put("2", "2");
            }
        });
        circularMultiMap.put("1", "1_3");
        // then
        Assert.assertEquals(Arrays.asList("1_1", "1_2", "1_3"), circularMultiMap.getAll("1"));
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.getAll("2"));
        Assert.assertEquals(Arrays.asList("1", "1", "1", "2"), circularMultiMap.getKeyListForValues());
    }

    @Test
    public void shouldIndicateWhenEmpty() {
        Assert.assertTrue(new CircularMultiMap<String, String>(3, 3).isEmpty());
    }

    @Test
    public void shouldSupportBeingCleared() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        circularMultiMap.put("1", "1_1");
        circularMultiMap.put("1", "1_2");
        circularMultiMap.put("1", "1_3");
        circularMultiMap.put("2", "2");
        // when
        circularMultiMap.clear();
        // then
        Assert.assertTrue(circularMultiMap.isEmpty());
        Assert.assertFalse(circularMultiMap.containsKey("1"));
        Assert.assertFalse(circularMultiMap.containsKey("2"));
        Assert.assertFalse(circularMultiMap.containsValue("1_2"));
        Assert.assertFalse(circularMultiMap.containsValue("2"));
        Assert.assertEquals(Arrays.asList(), circularMultiMap.getKeyListForValues());
    }

    @Test
    public void shouldReturnEntrySet() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        // when
        circularMultiMap.put("1", "1_1");
        circularMultiMap.put("1", "1_2");
        circularMultiMap.put("1", "1_3");
        circularMultiMap.put("2", "2");
        // then
        Assert.assertEquals(Sets.newHashSet(new CircularMultiMap.ImmutableEntry[]{ circularMultiMap.new ImmutableEntry("1", "1_1"), circularMultiMap.new ImmutableEntry("1", "1_2"), circularMultiMap.new ImmutableEntry("1", "1_3"), circularMultiMap.new ImmutableEntry("2", "2") }), circularMultiMap.entrySet());
        Assert.assertEquals(Arrays.asList("1", "1", "1", "2"), circularMultiMap.getKeyListForValues());
    }

    @Test
    public void shouldCorrectlyConstructAndGetEntryValue() {
        // when
        CircularMultiMap.ImmutableEntry immutableEntry = new CircularMultiMap<String, String>(3, 3).new ImmutableEntry("key", "value");
        // then
        Assert.assertEquals(immutableEntry.getKey(), "key");
        Assert.assertEquals(immutableEntry.getValue(), "value");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldNotAllowImmutableEntryToBeModified() {
        new CircularMultiMap<String, String>(3, 3).new ImmutableEntry("key", "value").setValue("new_value");
    }

    @Test
    public void shouldSupportRemovingAllValues() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        circularMultiMap.put("1", "1_1");
        circularMultiMap.put("1", "1_2");
        circularMultiMap.put("1", "1_3");
        circularMultiMap.put("2", "2");
        // when
        Assert.assertEquals(Arrays.asList("1_1", "1_2", "1_3"), circularMultiMap.removeAll("1"));
        Assert.assertNull(circularMultiMap.removeAll("3"));
        // then
        // - should have correct keys
        Assert.assertFalse(circularMultiMap.containsKey("1"));
        Assert.assertTrue(circularMultiMap.containsKey("2"));
        Assert.assertEquals(Sets.newHashSet("2"), circularMultiMap.keySet());
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.getKeyListForValues());
        // - should have correct values
        Assert.assertFalse(circularMultiMap.containsValue("1_1"));
        Assert.assertFalse(circularMultiMap.containsValue("1_2"));
        Assert.assertFalse(circularMultiMap.containsValue("1_3"));
        Assert.assertTrue(circularMultiMap.containsValue("2"));
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.values());
        // - should have correct values per key
        Assert.assertNull(circularMultiMap.get("1"));
        Assert.assertEquals("2", circularMultiMap.get("2"));
        Assert.assertEquals(0, circularMultiMap.getAll("1").size());
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.getAll("2"));
    }

    @Test
    public void shouldSupportRemovingAValue() {
        // given
        CircularMultiMap<String, String> circularMultiMap = new CircularMultiMap<String, String>(3, 3);
        circularMultiMap.put("1", "1_1");
        circularMultiMap.put("1", "1_2");
        circularMultiMap.put("1", "1_3");
        circularMultiMap.put("2", "2");
        // when
        Assert.assertEquals("1_1", circularMultiMap.remove("1"));
        Assert.assertNull(circularMultiMap.remove("3"));
        // then
        // - should have correct keys
        Assert.assertTrue(circularMultiMap.containsKey("1"));
        Assert.assertTrue(circularMultiMap.containsKey("2"));
        Assert.assertEquals(Sets.newHashSet("1", "2"), circularMultiMap.keySet());
        Assert.assertEquals(Arrays.asList("1", "1", "2"), circularMultiMap.getKeyListForValues());
        // - should have correct values
        Assert.assertFalse(circularMultiMap.containsValue("1_1"));
        Assert.assertTrue(circularMultiMap.containsValue("1_2"));
        Assert.assertTrue(circularMultiMap.containsValue("1_3"));
        Assert.assertTrue(circularMultiMap.containsValue("2"));
        Assert.assertEquals(Arrays.asList("1_2", "1_3", "2"), circularMultiMap.values());
        // - should have correct values per key
        Assert.assertEquals("1_2", circularMultiMap.get("1"));
        Assert.assertEquals("2", circularMultiMap.get("2"));
        Assert.assertEquals(Arrays.asList("1_2", "1_3"), circularMultiMap.getAll("1"));
        Assert.assertEquals(Arrays.asList("2"), circularMultiMap.getAll("2"));
    }
}

