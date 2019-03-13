package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsEqualTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<?> matcher = IsEqual.equalTo("irrelevant");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void comparesObjectsUsingEqualsMethod() {
        final Matcher<String> matcher1 = IsEqual.equalTo("hi");
        AbstractMatcherTest.assertMatches(matcher1, "hi");
        AbstractMatcherTest.assertDoesNotMatch(matcher1, "bye");
        AbstractMatcherTest.assertDoesNotMatch(matcher1, null);
        final Matcher<Integer> matcher2 = IsEqual.equalTo(1);
        AbstractMatcherTest.assertMatches(matcher2, 1);
        AbstractMatcherTest.assertDoesNotMatch(matcher2, 2);
        AbstractMatcherTest.assertDoesNotMatch(matcher2, null);
    }

    @Test
    public void canCompareNullValues() {
        final Matcher<Object> matcher = IsEqual.equalTo(null);
        AbstractMatcherTest.assertMatches(matcher, null);
        AbstractMatcherTest.assertDoesNotMatch(matcher, 2);
        AbstractMatcherTest.assertDoesNotMatch(matcher, "hi");
        AbstractMatcherTest.assertDoesNotMatch(matcher, new String[]{ "a", "b" });
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Test
    public void honoursIsEqualImplementationEvenWithNullValues() {
        Object alwaysEqual = new Object() {
            @Override
            public boolean equals(Object obj) {
                return true;
            }
        };
        Object neverEqual = new Object() {
            @Override
            public boolean equals(Object obj) {
                return false;
            }
        };
        Matcher<Object> matcher = IsEqual.equalTo(null);
        AbstractMatcherTest.assertMatches(matcher, alwaysEqual);
        AbstractMatcherTest.assertDoesNotMatch(matcher, neverEqual);
    }

    @Test
    public void comparesTheElementsOfAnObjectArray() {
        String[] s1 = new String[]{ "a", "b" };
        String[] s2 = new String[]{ "a", "b" };
        String[] s3 = new String[]{ "c", "d" };
        String[] s4 = new String[]{ "a", "b", "c", "d" };
        final Matcher<String[]> matcher = IsEqual.equalTo(s1);
        AbstractMatcherTest.assertMatches(matcher, s1);
        AbstractMatcherTest.assertMatches(matcher, s2);
        AbstractMatcherTest.assertDoesNotMatch(matcher, s3);
        AbstractMatcherTest.assertDoesNotMatch(matcher, s4);
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
    }

    @Test
    public void comparesTheElementsOfArraysWithNulls() {
        String[] s1 = new String[]{ "a", null, "b" };
        String[] s2 = new String[]{ "a", null, "b" };
        String[] s3 = new String[]{ "c", "d" };
        String[] s4 = new String[]{ "a", "b", "c", "d" };
        final Matcher<String[]> matcher = IsEqual.equalTo(s1);
        AbstractMatcherTest.assertMatches(matcher, s1);
        AbstractMatcherTest.assertMatches(matcher, s2);
        AbstractMatcherTest.assertDoesNotMatch(matcher, s3);
        AbstractMatcherTest.assertDoesNotMatch(matcher, s4);
    }

    @Test
    public void comparesTheElementsOfAnArrayOfPrimitiveTypes() {
        int[] i1 = new int[]{ 1, 2 };
        int[] i2 = new int[]{ 1, 2 };
        int[] i3 = new int[]{ 3, 4 };
        int[] i4 = new int[]{ 1, 2, 3, 4 };
        final Matcher<int[]> matcher = IsEqual.equalTo(i1);
        AbstractMatcherTest.assertMatches(matcher, i1);
        AbstractMatcherTest.assertMatches(matcher, i2);
        AbstractMatcherTest.assertDoesNotMatch(matcher, i3);
        AbstractMatcherTest.assertDoesNotMatch(matcher, i4);
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
    }

    @Test
    public void recursivelyTestsElementsOfArrays() {
        int[][] i1 = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 } };
        int[][] i2 = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 } };
        int[][] i3 = new int[][]{ new int[]{ 5, 6 }, new int[]{ 7, 8 } };
        int[][] i4 = new int[][]{ new int[]{ 1, 2, 3, 4 }, new int[]{ 3, 4 } };
        final Matcher<int[][]> matcher = IsEqual.equalTo(i1);
        AbstractMatcherTest.assertMatches(matcher, i1);
        AbstractMatcherTest.assertMatches(matcher, i2);
        AbstractMatcherTest.assertDoesNotMatch(matcher, i3);
        AbstractMatcherTest.assertDoesNotMatch(matcher, i4);
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
    }

    @Test
    public void hasUntypedVariant() {
        Object original = 10;
        AbstractMatcherTest.assertMatches(IsEqual.equalToObject(10), original);
        AbstractMatcherTest.assertDoesNotMatch(IsEqual.equalToObject(0), original);
        AbstractMatcherTest.assertDoesNotMatch(IsEqual.equalToObject("10"), original);
        AbstractMatcherTest.assertDoesNotMatch(IsEqual.equalToObject(10), "10");
    }

    @Test
    public void includesTheResultOfCallingToStringOnItsArgumentInTheDescription() {
        final String argumentDescription = "ARGUMENT DESCRIPTION";
        Object argument = new Object() {
            @Override
            public String toString() {
                return argumentDescription;
            }
        };
        AbstractMatcherTest.assertDescription((("<" + argumentDescription) + ">"), IsEqual.equalTo(argument));
    }

    @Test
    public void returnsAnObviousDescriptionIfCreatedWithANestedMatcherByMistake() {
        Matcher<? super String> innerMatcher = IsEqual.equalTo("NestedMatcher");
        AbstractMatcherTest.assertDescription((("<" + (innerMatcher.toString())) + ">"), IsEqual.equalTo(innerMatcher));
    }

    @Test
    public void returnsGoodDescriptionIfCreatedWithNullReference() {
        AbstractMatcherTest.assertDescription("null", IsEqual.equalTo(null));
    }
}

