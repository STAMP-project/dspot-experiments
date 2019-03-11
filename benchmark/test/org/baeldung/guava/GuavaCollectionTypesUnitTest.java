package org.baeldung.guava;


import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeRangeSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GuavaCollectionTypesUnitTest {
    @Test
    public void whenCreateList_thenCreated() {
        final List<String> names = Lists.newArrayList("John", "Adam", "Jane");
        names.add("Tom");
        Assert.assertEquals(4, names.size());
        names.remove("Adam");
        Assert.assertThat(names, Matchers.contains("John", "Jane", "Tom"));
    }

    @Test
    public void whenReverseList_thenReversed() {
        final List<String> names = Lists.newArrayList("John", "Adam", "Jane");
        final List<String> reversed = Lists.reverse(names);
        Assert.assertThat(reversed, Matchers.contains("Jane", "Adam", "John"));
    }

    @Test
    public void whenCreateCharacterListFromString_thenCreated() {
        final List<Character> chars = Lists.charactersOf("John");
        Assert.assertEquals(4, chars.size());
        Assert.assertThat(chars, Matchers.contains('J', 'o', 'h', 'n'));
    }

    @Test
    public void whenPartitionList_thenPartitioned() {
        final List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom", "Viki", "Tyler");
        final List<List<String>> result = Lists.partition(names, 2);
        Assert.assertEquals(3, result.size());
        Assert.assertThat(result.get(0), Matchers.contains("John", "Jane"));
        Assert.assertThat(result.get(1), Matchers.contains("Adam", "Tom"));
        Assert.assertThat(result.get(2), Matchers.contains("Viki", "Tyler"));
    }

    @Test
    public void whenRemoveDuplicatesFromList_thenRemoved() {
        final List<Character> chars = Lists.newArrayList('h', 'e', 'l', 'l', 'o');
        Assert.assertEquals(5, chars.size());
        final List<Character> result = ImmutableSet.copyOf(chars).asList();
        Assert.assertThat(result, Matchers.contains('h', 'e', 'l', 'o'));
    }

    @Test
    public void whenRemoveNullFromList_thenRemoved() {
        final List<String> names = Lists.newArrayList("John", null, "Adam", null, "Jane");
        Iterables.removeIf(names, Predicates.isNull());
        Assert.assertEquals(3, names.size());
        Assert.assertThat(names, Matchers.contains("John", "Adam", "Jane"));
    }

    @Test
    public void whenCreateImmutableList_thenCreated() {
        final List<String> names = Lists.newArrayList("John", "Adam", "Jane");
        names.add("Tom");
        Assert.assertEquals(4, names.size());
        final ImmutableList<String> immutable = ImmutableList.copyOf(names);
        Assert.assertThat(immutable, Matchers.contains("John", "Adam", "Jane", "Tom"));
    }

    // sets
    @Test
    public void whenCalculateUnionOfSets_thenCorrect() {
        final Set<Character> first = ImmutableSet.of('a', 'b', 'c');
        final Set<Character> second = ImmutableSet.of('b', 'c', 'd');
        final Set<Character> union = Sets.union(first, second);
        Assert.assertThat(union, Matchers.containsInAnyOrder('a', 'b', 'c', 'd'));
    }

    @Test
    public void whenCalculateSetsProduct_thenCorrect() {
        final Set<Character> first = ImmutableSet.of('a', 'b');
        final Set<Character> second = ImmutableSet.of('c', 'd');
        final Set<List<Character>> result = Sets.cartesianProduct(ImmutableList.of(first, second));
        final Function<List<Character>, String> func = new Function<List<Character>, String>() {
            @Override
            public final String apply(final List<Character> input) {
                return Joiner.on(" ").join(input);
            }
        };
        final Iterable<String> joined = Iterables.transform(result, func);
        Assert.assertThat(joined, Matchers.containsInAnyOrder("a c", "a d", "b c", "b d"));
    }

    @Test
    public void whenCalculatingSetIntersection_thenCorrect() {
        final Set<Character> first = ImmutableSet.of('a', 'b', 'c');
        final Set<Character> second = ImmutableSet.of('b', 'c', 'd');
        final Set<Character> intersection = Sets.intersection(first, second);
        Assert.assertThat(intersection, Matchers.containsInAnyOrder('b', 'c'));
    }

    @Test
    public void whenCalculatingSetSymmetricDifference_thenCorrect() {
        final Set<Character> first = ImmutableSet.of('a', 'b', 'c');
        final Set<Character> second = ImmutableSet.of('b', 'c', 'd');
        final Set<Character> intersection = Sets.symmetricDifference(first, second);
        Assert.assertThat(intersection, Matchers.containsInAnyOrder('a', 'd'));
    }

    @Test
    public void whenCalculatingPowerSet_thenCorrect() {
        final Set<Character> chars = ImmutableSet.of('a', 'b');
        final Set<Set<Character>> result = Sets.powerSet(chars);
        final Set<Character> empty = ImmutableSet.<Character>builder().build();
        final Set<Character> a = ImmutableSet.of('a');
        final Set<Character> b = ImmutableSet.of('b');
        final Set<Character> aB = ImmutableSet.of('a', 'b');
        Assert.assertThat(result, Matchers.contains(empty, a, b, aB));
    }

    @Test
    public void whenCreateRangeOfIntegersSet_thenCreated() {
        final int start = 10;
        final int end = 30;
        final ContiguousSet<Integer> set = ContiguousSet.create(Range.closed(start, end), DiscreteDomain.integers());
        Assert.assertEquals(21, set.size());
        Assert.assertEquals(10, set.first().intValue());
        Assert.assertEquals(30, set.last().intValue());
    }

    @Test
    public void whenCreateRangeSet_thenCreated() {
        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closed(1, 10));
        rangeSet.add(Range.closed(12, 15));
        Assert.assertEquals(2, rangeSet.asRanges().size());
        rangeSet.add(Range.closed(10, 12));
        Assert.assertTrue(rangeSet.encloses(Range.closed(1, 15)));
        Assert.assertEquals(1, rangeSet.asRanges().size());
    }

    @Test
    public void whenInsertDuplicatesInMultiSet_thenInserted() {
        final Multiset<String> names = HashMultiset.create();
        names.add("John");
        names.add("Adam", 3);
        names.add("John");
        Assert.assertEquals(2, names.count("John"));
        names.remove("John");
        Assert.assertEquals(1, names.count("John"));
        Assert.assertEquals(3, names.count("Adam"));
        names.remove("Adam", 2);
        Assert.assertEquals(1, names.count("Adam"));
    }

    @Test
    public void whenGetTopUsingMultiSet_thenCorrect() {
        final Multiset<String> names = HashMultiset.create();
        names.add("John");
        names.add("Adam", 5);
        names.add("Jane");
        names.add("Tom", 2);
        final Set<String> sorted = Multisets.copyHighestCountFirst(names).elementSet();
        final List<String> topTwo = Lists.newArrayList(sorted).subList(0, 2);
        Assert.assertEquals(2, topTwo.size());
        Assert.assertEquals("Adam", topTwo.get(0));
        Assert.assertEquals("Tom", topTwo.get(1));
    }

    @Test
    public void whenCreateImmutableMap_thenCreated() {
        final Map<String, Integer> salary = ImmutableMap.<String, Integer>builder().put("John", 1000).put("Jane", 1500).put("Adam", 2000).put("Tom", 2000).build();
        Assert.assertEquals(1000, salary.get("John").intValue());
        Assert.assertEquals(2000, salary.get("Tom").intValue());
    }

    @Test
    public void whenUseSortedMap_thenKeysAreSorted() {
        final ImmutableSortedMap<String, Integer> salary = new ImmutableSortedMap.Builder<String, Integer>(Ordering.natural()).put("John", 1000).put("Jane", 1500).put("Adam", 2000).put("Tom", 2000).build();
        Assert.assertEquals("Adam", salary.firstKey());
        Assert.assertEquals(2000, salary.lastEntry().getValue().intValue());
    }

    @Test
    public void whenCreateBiMap_thenCreated() {
        final BiMap<String, Integer> words = HashBiMap.create();
        words.put("First", 1);
        words.put("Second", 2);
        words.put("Third", 3);
        Assert.assertEquals(2, words.get("Second").intValue());
        Assert.assertEquals("Third", words.inverse().get(3));
    }

    @Test
    public void whenCreateMultimap_thenCreated() {
        final Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruit", "apple");
        multimap.put("fruit", "banana");
        multimap.put("pet", "cat");
        multimap.put("pet", "dog");
        Assert.assertThat(multimap.get("fruit"), Matchers.containsInAnyOrder("apple", "banana"));
        Assert.assertThat(multimap.get("pet"), Matchers.containsInAnyOrder("cat", "dog"));
    }

    @Test
    public void whenGroupListUsingMultimap_thenGrouped() {
        final List<String> names = Lists.newArrayList("John", "Adam", "Tom");
        final Function<String, Integer> function = new Function<String, Integer>() {
            @Override
            public final Integer apply(final String input) {
                return input.length();
            }
        };
        final Multimap<Integer, String> groups = Multimaps.index(names, function);
        Assert.assertThat(groups.get(3), Matchers.containsInAnyOrder("Tom"));
        Assert.assertThat(groups.get(4), Matchers.containsInAnyOrder("John", "Adam"));
    }

    @Test
    public void whenCreateTable_thenCreated() {
        final Table<String, String, Integer> distance = HashBasedTable.create();
        distance.put("London", "Paris", 340);
        distance.put("New York", "Los Angeles", 3940);
        distance.put("London", "New York", 5576);
        Assert.assertEquals(3940, distance.get("New York", "Los Angeles").intValue());
        Assert.assertThat(distance.columnKeySet(), Matchers.containsInAnyOrder("Paris", "New York", "Los Angeles"));
        Assert.assertThat(distance.rowKeySet(), Matchers.containsInAnyOrder("London", "New York"));
    }

    @Test
    public void whenTransposeTable_thenCorrect() {
        final Table<String, String, Integer> distance = HashBasedTable.create();
        distance.put("London", "Paris", 340);
        distance.put("New York", "Los Angeles", 3940);
        distance.put("London", "New York", 5576);
        final Table<String, String, Integer> transposed = Tables.transpose(distance);
        Assert.assertThat(transposed.rowKeySet(), Matchers.containsInAnyOrder("Paris", "New York", "Los Angeles"));
        Assert.assertThat(transposed.columnKeySet(), Matchers.containsInAnyOrder("London", "New York"));
    }

    @Test
    public void whenCreateClassToInstanceMap_thenCreated() {
        final ClassToInstanceMap<Number> numbers = MutableClassToInstanceMap.create();
        numbers.putInstance(Integer.class, 1);
        numbers.putInstance(Double.class, 1.5);
        Assert.assertEquals(1, numbers.get(Integer.class));
        Assert.assertEquals(1.5, numbers.get(Double.class));
    }
}

