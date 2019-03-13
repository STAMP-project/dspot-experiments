package org.hamcrest.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsIterableContainingTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<?> matcher = IsIterableContaining.hasItem(IsEqual.equalTo("irrelevant"));
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesACollectionThatContainsAnElementForTheGivenMatcher() {
        final Matcher<Iterable<? super String>> itemMatcher = IsIterableContaining.hasItem(IsEqual.equalTo("a"));
        AbstractMatcherTest.assertMatches("list containing 'a'", itemMatcher, Arrays.asList("a", "b", "c"));
    }

    @Test
    public void doesNotMatchCollectionWithoutAnElementForGivenMatcher() {
        final Matcher<Iterable<? super String>> matcher = IsIterableContaining.hasItem(IsIterableContainingTest.mismatchable("a"));
        AbstractMatcherTest.assertMismatchDescription("mismatches were: [mismatched: b, mismatched: c]", matcher, Arrays.asList("b", "c"));
        AbstractMatcherTest.assertMismatchDescription("was empty", matcher, new ArrayList<String>());
    }

    @Test
    public void doesNotMatchNull() {
        AbstractMatcherTest.assertDoesNotMatch("doesn't match null", IsIterableContaining.hasItem(IsEqual.equalTo("a")), null);
    }

    @Test
    public void hasAReadableDescription() {
        AbstractMatcherTest.assertDescription("a collection containing mismatchable: a", IsIterableContaining.hasItem(IsIterableContainingTest.mismatchable("a")));
    }

    @Test
    public void canMatchItemWhenCollectionHoldsSuperclass() {
        // Issue 24
        final Set<Number> s = new HashSet<>();
        s.add(2);
        AbstractMatcherTest.assertMatches(new IsIterableContaining(new IsEqual<Number>(2)), s);
        AbstractMatcherTest.assertMatches(IsIterableContaining.hasItem(2), s);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void matchesMultipleItemsInCollection() {
        final Matcher<Iterable<String>> matcher1 = IsIterableContaining.hasItems(IsEqual.equalTo("a"), IsEqual.equalTo("b"), IsEqual.equalTo("c"));
        AbstractMatcherTest.assertMatches("list containing all items", matcher1, Arrays.asList("a", "b", "c"));
        final Matcher<Iterable<String>> matcher2 = IsIterableContaining.hasItems("a", "b", "c");
        AbstractMatcherTest.assertMatches("list containing all items (without matchers)", matcher2, Arrays.asList("a", "b", "c"));
        final Matcher<Iterable<String>> matcher3 = IsIterableContaining.hasItems(IsEqual.equalTo("a"), IsEqual.equalTo("b"), IsEqual.equalTo("c"));
        AbstractMatcherTest.assertMatches("list containing all items in any order", matcher3, Arrays.asList("c", "b", "a"));
        final Matcher<Iterable<String>> matcher4 = IsIterableContaining.hasItems(IsEqual.equalTo("a"), IsEqual.equalTo("b"), IsEqual.equalTo("c"));
        AbstractMatcherTest.assertMatches("list containing all items plus others", matcher4, Arrays.asList("e", "c", "b", "a", "d"));
        final Matcher<Iterable<String>> matcher5 = IsIterableContaining.hasItems(IsEqual.equalTo("a"), IsEqual.equalTo("b"), IsEqual.equalTo("c"));
        AbstractMatcherTest.assertDoesNotMatch("not match list unless it contains all items", matcher5, Arrays.asList("e", "c", "b", "d"));// 'a' missing

    }

    @Test
    public void reportsMismatchWithAReadableDescriptionForMultipleItems() {
        final Matcher<Iterable<Integer>> matcher = IsIterableContaining.hasItems(3, 4);
        AbstractMatcherTest.assertMismatchDescription("a collection containing <4> mismatches were: [was <1>, was <2>, was <3>]", matcher, Arrays.asList(1, 2, 3));
    }
}

