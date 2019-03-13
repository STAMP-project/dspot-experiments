package org.baeldung.java.collections;


import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


public class ArrayListUnitTest {
    private List<String> stringsToSearch;

    @Test
    public void givenNewArrayList_whenCheckCapacity_thenDefaultValue() {
        List<String> list = new ArrayList<>();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void givenCollection_whenProvideItToArrayListCtor_thenArrayListIsPopulatedWithItsElements() {
        Collection<Integer> numbers = IntStream.range(0, 10).boxed().collect(Collectors.toSet());
        List<Integer> list = new ArrayList<>(numbers);
        Assert.assertEquals(10, list.size());
        Assert.assertTrue(numbers.containsAll(list));
    }

    @Test
    public void givenElement_whenAddToArrayList_thenIsAdded() {
        List<Long> list = new ArrayList<>();
        list.add(1L);
        list.add(2L);
        list.add(1, 3L);
        Assert.assertThat(Arrays.asList(1L, 3L, 2L), CoreMatchers.equalTo(list));
    }

    @Test
    public void givenCollection_whenAddToArrayList_thenIsAdded() {
        List<Long> list = new ArrayList<>(Arrays.asList(1L, 2L, 3L));
        LongStream.range(4, 10).boxed().collect(Collectors.collectingAndThen(Collectors.toCollection(ArrayList::new), ( ys) -> list.addAll(0, ys)));
        Assert.assertThat(Arrays.asList(4L, 5L, 6L, 7L, 8L, 9L, 1L, 2L, 3L), CoreMatchers.equalTo(list));
    }

    @Test
    public void givenExistingElement_whenCallIndexOf_thenReturnCorrectIndex() {
        Assert.assertEquals(10, stringsToSearch.indexOf("a"));
        Assert.assertEquals(26, stringsToSearch.lastIndexOf("a"));
    }

    @Test
    public void givenCondition_whenIterateArrayList_thenFindAllElementsSatisfyingCondition() {
        Iterator<String> it = stringsToSearch.iterator();
        Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));
        List<String> result = new ArrayList<>();
        while (it.hasNext()) {
            String s = it.next();
            if (matchingStrings.contains(s)) {
                result.add(s);
            }
        } 
        Assert.assertEquals(6, result.size());
    }

    @Test
    public void givenPredicate_whenIterateArrayList_thenFindAllElementsSatisfyingPredicate() {
        Set<String> matchingStrings = new HashSet<>(Arrays.asList("a", "c", "9"));
        List<String> result = stringsToSearch.stream().filter(matchingStrings::contains).collect(Collectors.toCollection(ArrayList::new));
        Assert.assertEquals(6, result.size());
    }

    @Test
    public void givenSortedArray_whenUseBinarySearch_thenFindElement() {
        List<String> copy = new ArrayList<>(stringsToSearch);
        Collections.sort(copy);
        int index = Collections.binarySearch(copy, "f");
        Assert.assertThat(index, IsNot.not(CoreMatchers.equalTo((-1))));
    }

    @Test
    public void givenIndex_whenRemove_thenCorrectElementRemoved() {
        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(list);
        list.remove(0);
        Assert.assertThat(list.get(0), CoreMatchers.equalTo(8));
        list.remove(Integer.valueOf(0));
        Assert.assertFalse(list.contains(0));
    }

    @Test
    public void givenListIterator_whenReverseTraversal_thenRetrieveElementsInOppositeOrder() {
        List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toCollection(ArrayList::new));
        ListIterator<Integer> it = list.listIterator(list.size());
        List<Integer> result = new ArrayList<>(list.size());
        while (it.hasPrevious()) {
            result.add(it.previous());
        } 
        Collections.reverse(list);
        Assert.assertThat(result, CoreMatchers.equalTo(list));
    }

    @Test
    public void givenCondition_whenIterateArrayList_thenRemoveAllElementsSatisfyingCondition() {
        Set<String> matchingStrings = Sets.newHashSet("a", "b", "c", "d", "e", "f");
        Iterator<String> it = stringsToSearch.iterator();
        while (it.hasNext()) {
            if (matchingStrings.contains(it.next())) {
                it.remove();
            }
        } 
        Assert.assertEquals(20, stringsToSearch.size());
    }
}

