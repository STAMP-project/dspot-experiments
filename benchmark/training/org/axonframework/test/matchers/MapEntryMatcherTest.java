package org.axonframework.test.matchers;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.hamcrest.StringDescription;
import org.junit.Assert;
import org.junit.Test;


public class MapEntryMatcherTest {
    private static final Map<String, Object> EXPECTED = new HashMap<>();

    static {
        MapEntryMatcherTest.EXPECTED.put("a", new MapEntryMatcherTest.ValueItem("a"));
        MapEntryMatcherTest.EXPECTED.put("b", new MapEntryMatcherTest.ValueItem("b"));
        MapEntryMatcherTest.EXPECTED.put("c", new MapEntryMatcherTest.ValueItem("c"));
    }

    private final MapEntryMatcher matcher = new MapEntryMatcher(MapEntryMatcherTest.EXPECTED);

    @Test
    public void nullSafe() {
        Assert.assertFalse(matcher.matches(null));
    }

    @Test
    public void testExpectedEntriesNotPresent() {
        Assert.assertFalse(matcher.matches(Collections.singletonMap("a", new MapEntryMatcherTest.ValueItem("a"))));
        Assert.assertThat(matcher.getMissingEntries(), EqualsMatcher.equalTo(newHashMap("b", new MapEntryMatcherTest.ValueItem("b"), "c", new MapEntryMatcherTest.ValueItem("c"))));
    }

    @Test
    public void testTooManyEntries() {
        Assert.assertFalse(matcher.matches(newHashMap("a", new MapEntryMatcherTest.ValueItem("a"), "b", new MapEntryMatcherTest.ValueItem("b"), "c", new MapEntryMatcherTest.ValueItem("c"), "d", new MapEntryMatcherTest.ValueItem("d"), "e", new MapEntryMatcherTest.ValueItem("e"))));
        Assert.assertThat(matcher.getAdditionalEntries(), EqualsMatcher.equalTo(newHashMap("d", new MapEntryMatcherTest.ValueItem("d"), "e", new MapEntryMatcherTest.ValueItem("e"))));
    }

    @Test
    public void testIncorrectValue() {
        Assert.assertFalse(matcher.matches(newHashMap("a", new MapEntryMatcherTest.ValueItem("a"), "b", new MapEntryMatcherTest.ValueItem("b"), "c", new MapEntryMatcherTest.ValueItem("CCCC"))));
        Assert.assertThat(matcher.getAdditionalEntries(), EqualsMatcher.equalTo(newHashMap("c", new MapEntryMatcherTest.ValueItem("CCCC"))));
        Assert.assertFalse(matcher.matches(newHashMap("a", new MapEntryMatcherTest.ValueItem("a"), "b", new MapEntryMatcherTest.ValueItem("b"), "c", null)));
        Assert.assertThat(matcher.getAdditionalEntries(), EqualsMatcher.equalTo(newHashMap("c", null)));
    }

    @Test
    public void testIncorrectKey() {
        Assert.assertFalse(matcher.matches(newHashMap("a", new MapEntryMatcherTest.ValueItem("a"), "b", new MapEntryMatcherTest.ValueItem("b"), "CCCC", new MapEntryMatcherTest.ValueItem("c"))));
        Assert.assertThat(matcher.getAdditionalEntries(), EqualsMatcher.equalTo(newHashMap("CCCC", new MapEntryMatcherTest.ValueItem("c"))));
        Assert.assertFalse(matcher.matches(newHashMap("a", new MapEntryMatcherTest.ValueItem("a"), "b", new MapEntryMatcherTest.ValueItem("b"), null, new MapEntryMatcherTest.ValueItem("c"))));
        Assert.assertThat(matcher.getAdditionalEntries(), EqualsMatcher.equalTo(newHashMap(null, new MapEntryMatcherTest.ValueItem("c"))));
    }

    @Test
    public void testAnyOrder() {
        TreeMap<String, Object> sortedMap = new TreeMap();
        sortedMap.putAll(MapEntryMatcherTest.EXPECTED);
        Assert.assertTrue(matcher.matches(sortedMap));
        Assert.assertTrue(matcher.matches(sortedMap.descendingMap()));
    }

    @Test
    public void matchEmptyMap() {
        Assert.assertTrue(new MapEntryMatcher(Collections.emptyMap()).matches(Collections.emptyMap()));
    }

    @Test
    public void testNull() {
        Assert.assertFalse(new MapEntryMatcher(Collections.emptyMap()).matches(null));
    }

    @Test
    public void testNonMapType() {
        Assert.assertFalse(new MapEntryMatcher(Collections.emptyMap()).matches(new Object()));
        Assert.assertFalse(new MapEntryMatcher(Collections.emptyMap()).matches(Collections.emptySet()));
    }

    @Test
    public void testToString() {
        StringDescription description = new StringDescription();
        matcher.describeTo(description);
        Assert.assertEquals(description.toString(), ("map containing " + (String.format("[<%s=%s>,<%s=%s>,<%s=%s>]", MapEntryMatcherTest.EXPECTED.entrySet().stream().flatMap(( entry) -> Stream.of(entry.getKey(), entry.getValue())).toArray()))));
    }

    private static class ValueItem {
        private String name;

        public ValueItem(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            MapEntryMatcherTest.ValueItem valueItem = ((MapEntryMatcherTest.ValueItem) (o));
            return name.equals(valueItem.name);
        }

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("ValueItem{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}

