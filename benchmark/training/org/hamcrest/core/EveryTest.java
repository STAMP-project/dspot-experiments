package org.hamcrest.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class EveryTest {
    private final Matcher<Iterable<? extends String>> matcher = Every.everyItem(StringContains.containsString("a"));

    @Test
    public void copesWithNullsAndUnknownTypes() {
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesOnlyWhenEveryItemMatches() {
        AbstractMatcherTest.assertMatches(matcher, Arrays.asList("AaA", "BaB", "CaC"));
        AbstractMatcherTest.assertDoesNotMatch(matcher, Arrays.asList("AaA", "BXB", "CaC"));
    }

    @Test
    public void matchesEmptyLists() {
        AbstractMatcherTest.assertMatches("didn't match empty list", matcher, new ArrayList<String>());
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("every item is a string containing \"a\"", matcher);
    }

    @Test
    public void describesAMismatch() {
        AbstractMatcherTest.assertMismatchDescription("an item was \"BXB\"", matcher, Collections.singletonList("BXB"));
    }
}

