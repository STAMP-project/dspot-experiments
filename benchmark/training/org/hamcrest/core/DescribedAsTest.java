package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class DescribedAsTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<Object> matcher = DescribedAs.describedAs("irrelevant", IsAnything.anything());
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void overridesDescriptionOfOtherMatcherWithThatPassedToConstructor() {
        Matcher<?> matcher = DescribedAs.describedAs("my description", IsAnything.anything());
        AbstractMatcherTest.assertDescription("my description", matcher);
    }

    @Test
    public void appendsValuesToDescription() {
        Matcher<?> matcher = DescribedAs.describedAs("value 1 = %0, value 2 = %1", IsAnything.anything(), 33, 97);
        AbstractMatcherTest.assertDescription("value 1 = <33>, value 2 = <97>", matcher);
    }

    @Test
    public void celegatesMatchingToAnotherMatcher() {
        Matcher<String> matcher = DescribedAs.describedAs("irrelevant", IsEqual.equalTo("hi"));
        AbstractMatcherTest.assertMatches(matcher, "hi");
        AbstractMatcherTest.assertDoesNotMatch("matched", matcher, "oi");
    }

    @Test
    public void delegatesMismatchDescriptionToAnotherMatcher() {
        Matcher<Integer> matcher = DescribedAs.describedAs("irrelevant", IsEqual.equalTo(2));
        AbstractMatcherTest.assertMismatchDescription("was <1>", matcher, 1);
    }
}

