package com.baeldung.arrays;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ArraysUnitTest {
    private String[] intro;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void whenCopyOfRange_thenAbridgedArray() {
        String[] abridgement = Arrays.copyOfRange(intro, 0, 3);
        Assert.assertArrayEquals(new String[]{ "once", "upon", "a" }, abridgement);
        Assert.assertFalse(Arrays.equals(intro, abridgement));
    }

    @Test
    public void whenCopyOf_thenNullElement() {
        String[] revised = Arrays.copyOf(intro, 3);
        String[] expanded = Arrays.copyOf(intro, 5);
        Assert.assertArrayEquals(Arrays.copyOfRange(intro, 0, 3), revised);
        Assert.assertNull(expanded[4]);
    }

    @Test
    public void whenFill_thenAllMatch() {
        String[] stutter = new String[3];
        Arrays.fill(stutter, "once");
        Assert.assertTrue(Stream.of(stutter).allMatch(( el) -> "once".equals(el)));
    }

    @Test
    public void whenEqualsContent_thenMatch() {
        Assert.assertTrue(Arrays.equals(new String[]{ "once", "upon", "a", "time" }, intro));
        Assert.assertFalse(Arrays.equals(new String[]{ "once", "upon", "a", null }, intro));
    }

    @Test
    public void whenNestedArrays_thenDeepEqualsPass() {
        String[] end = new String[]{ "the", "end" };
        Object[] story = new Object[]{ intro, new String[]{ "chapter one", "chapter two" }, end };
        Object[] copy = new Object[]{ intro, new String[]{ "chapter one", "chapter two" }, end };
        Assert.assertTrue(Arrays.deepEquals(story, copy));
        Assert.assertFalse(Arrays.equals(story, copy));
    }

    @Test
    public void whenSort_thenArraySorted() {
        String[] sorted = Arrays.copyOf(intro, 4);
        Arrays.sort(sorted);
        Assert.assertArrayEquals(new String[]{ "a", "once", "time", "upon" }, sorted);
    }

    @Test
    public void whenBinarySearch_thenFindElements() {
        String[] sorted = Arrays.copyOf(intro, 4);
        Arrays.sort(sorted);
        int exact = Arrays.binarySearch(sorted, "time");
        int caseInsensitive = Arrays.binarySearch(sorted, "TiMe", String::compareToIgnoreCase);
        Assert.assertEquals("time", sorted[exact]);
        Assert.assertEquals(2, exact);
        Assert.assertEquals(exact, caseInsensitive);
    }

    @Test
    public void whenNullElement_thenArraysHashCodeNotEqual() {
        int beforeChange = Arrays.hashCode(intro);
        int before = intro.hashCode();
        intro[3] = null;
        int after = intro.hashCode();
        int afterChange = Arrays.hashCode(intro);
        Assert.assertNotEquals(beforeChange, afterChange);
        Assert.assertEquals(before, after);
    }

    @Test
    public void whenNestedArrayNullElement_thenEqualsFailDeepHashPass() {
        Object[] looping = new Object[]{ intro, intro };
        int deepHashBefore = Arrays.deepHashCode(looping);
        int hashBefore = Arrays.hashCode(looping);
        intro[3] = null;
        int hashAfter = Arrays.hashCode(looping);
        int deepHashAfter = Arrays.deepHashCode(looping);
        Assert.assertEquals(hashAfter, hashBefore);
        Assert.assertNotEquals(deepHashAfter, deepHashBefore);
    }

    @Test
    public void whenStreamBadIndex_thenException() {
        Assert.assertEquals(Arrays.stream(intro).count(), 4);
        exception.expect(ArrayIndexOutOfBoundsException.class);
        Arrays.stream(intro, 2, 1).count();
    }

    @Test
    public void whenSetAllToUpper_thenAppliedToAllElements() {
        String[] longAgo = new String[4];
        Arrays.setAll(longAgo, ( i) -> intro[i].toUpperCase());
        Assert.assertArrayEquals(longAgo, new String[]{ "ONCE", "UPON", "A", "TIME" });
    }

    @Test
    public void whenToString_thenFormattedArrayString() {
        Assert.assertEquals("[once, upon, a, time]", Arrays.toString(intro));
    }

    @Test
    public void whenNestedArrayDeepString_thenFormattedArraysString() {
        String[] end = new String[]{ "the", "end" };
        Object[] story = new Object[]{ intro, new String[]{ "chapter one", "chapter two" }, end };
        Assert.assertEquals("[[once, upon, a, time], [chapter one, chapter two], [the, end]]", Arrays.deepToString(story));
    }

    @Test
    public void whenAsList_thenImmutableArray() {
        List<String> rets = Arrays.asList(intro);
        Assert.assertTrue(rets.contains("upon"));
        Assert.assertTrue(rets.contains("time"));
        Assert.assertEquals(rets.size(), 4);
        exception.expect(UnsupportedOperationException.class);
        rets.add("the");
    }

    @Test
    public void givenIntArray_whenPrefixAdd_thenAllAccumulated() {
        int[] arri = new int[]{ 1, 2, 3, 4 };
        Arrays.parallelPrefix(arri, ( left, right) -> left + right);
        MatcherAssert.assertThat(arri, Matchers.is(new int[]{ 1, 3, 6, 10 }));
    }

    @Test
    public void givenStringArray_whenPrefixConcat_thenAllMerged() {
        String[] arrs = new String[]{ "1", "2", "3" };
        Arrays.parallelPrefix(arrs, ( left, right) -> left + right);
        MatcherAssert.assertThat(arrs, Matchers.is(new String[]{ "1", "12", "123" }));
    }

    @Test
    public void whenPrefixAddWithRange_thenRangeAdded() {
        int[] arri = new int[]{ 1, 2, 3, 4, 5 };
        Arrays.parallelPrefix(arri, 1, 4, ( left, right) -> left + right);
        MatcherAssert.assertThat(arri, Matchers.is(new int[]{ 1, 2, 5, 9, 5 }));
    }

    @Test
    public void whenPrefixNonAssociative_thenError() {
        boolean consistent = true;
        Random r = new Random();
        for (int k = 0; k < 100000; k++) {
            int[] arrA = r.ints(100, 1, 5).toArray();
            int[] arrB = Arrays.copyOf(arrA, arrA.length);
            Arrays.parallelPrefix(arrA, this::nonassociativeFunc);
            for (int i = 1; i < (arrB.length); i++) {
                arrB[i] = nonassociativeFunc(arrB[(i - 1)], arrB[i]);
            }
            consistent = Arrays.equals(arrA, arrB);
            if (!consistent)
                break;

        }
        Assert.assertFalse(consistent);
    }
}

