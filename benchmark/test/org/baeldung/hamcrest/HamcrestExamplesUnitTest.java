package org.baeldung.hamcrest;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HamcrestExamplesUnitTest {
    // tests
    @Test
    public final void whenVerifyingSingleElementIsPartOfCollection_thenCorrect() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.hasItem("cd"));
        Assert.assertThat(collection, Matchers.not(Matchers.hasItem("zz")));
    }

    @Test
    public final void whenVerifyingMultipleElementsArePartOfCollection_thenCorrect1() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.hasItems("ef", "cd"));
    }

    @Test
    public final void whenVerifyingMultipleElementsArePartOfCollectionInStrictOrder_thenCorrect2() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.contains("ab", "cd", "ef"));
    }

    @Test
    public final void whenVerifyingMultipleElementsArePartOfCollectionInAnyOrder_thenCorrect2() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.containsInAnyOrder("cd", "ab", "ef"));
    }

    @Test
    public final void givenCollectionIsEmpty_whenChecking_thenEmpty() {
        final List<String> collection = Lists.newArrayList();
        Assert.assertThat(collection, Matchers.empty());
    }

    @Test
    public final void givenIterableIsEmpty_whenChecking_thenEmpty() {
        final Iterable<String> collection = Lists.newArrayList();
        Assert.assertThat(collection, Matchers.emptyIterable());
    }

    @Test
    public final void givenCollectionIsNotEmpty_whenChecking_thenNotEmpty() {
        final List<String> collection = Lists.newArrayList("a");
        Assert.assertThat(collection, Matchers.not(Matchers.empty()));
    }

    @Test
    public final void givenMapIsEmpty_whenChecking_thenEmpty() {
        final Map<String, String> collection = Maps.newHashMap();
        Assert.assertThat(collection, Matchers.equalTo(Collections.EMPTY_MAP));
    }

    @Test
    public final void givenArrayIsEmpty_whenChecking_thenEmpty() {
        final String[] array = new String[]{ "ab" };
        Assert.assertThat(array, Matchers.not(Matchers.emptyArray()));
    }

    @Test
    public final void whenCollectionSizeIsChecked_thenCorrect() {
        final List<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.hasSize(3));
    }

    @Test
    public final void whenIterableSizeIsChecked_thenCorrect() {
        final Iterable<String> collection = Lists.newArrayList("ab", "cd", "ef");
        Assert.assertThat(collection, Matchers.<String>iterableWithSize(3));
    }

    @Test
    public final void whenCheckingConditionOverEachItem_thenCorrect() {
        final List<Integer> collection = Lists.newArrayList(15, 20, 25, 30);
        Assert.assertThat(collection, Matchers.everyItem(Matchers.greaterThan(10)));
    }
}

