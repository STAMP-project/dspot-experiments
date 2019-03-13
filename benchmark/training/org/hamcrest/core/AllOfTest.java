package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public final class AllOfTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = AllOf.allOf(IsEqual.equalTo("irrelevant"), StringStartsWith.startsWith("irr"));
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void evaluatesToTheTheLogicalConjunctionOfTwoOtherMatchers() {
        Matcher<String> matcher = AllOf.allOf(StringStartsWith.startsWith("goo"), StringEndsWith.endsWith("ood"));
        AbstractMatcherTest.assertMatches("didn't pass both sub-matchers", matcher, "good");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail first sub-matcher", matcher, "mood");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail second sub-matcher", matcher, "goon");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail both sub-matchers", matcher, "fred");
    }

    @Test
    public void evaluatesToTheTheLogicalConjunctionOfManyOtherMatchers() {
        Matcher<String> matcher = AllOf.allOf(StringStartsWith.startsWith("g"), StringStartsWith.startsWith("go"), StringEndsWith.endsWith("d"), StringStartsWith.startsWith("go"), StringStartsWith.startsWith("goo"));
        AbstractMatcherTest.assertMatches("didn't pass all sub-matchers", matcher, "good");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail middle sub-matcher", matcher, "goon");
    }

    @Test
    public void supportsMixedTypes() {
        final Matcher<SampleSubClass> matcher = AllOf.allOf(IsEqual.equalTo(new SampleBaseClass("bad")), Is.is(IsNull.notNullValue()), IsEqual.equalTo(new SampleBaseClass("good")), IsEqual.equalTo(new SampleSubClass("ugly")));
        AbstractMatcherTest.assertDoesNotMatch("didn't fail last sub-matcher", matcher, new SampleSubClass("good"));
    }

    @Test
    public void hasAReadableDescription() {
        AbstractMatcherTest.assertDescription("(\"good\" and \"bad\" and \"ugly\")", AllOf.allOf(IsEqual.equalTo("good"), IsEqual.equalTo("bad"), IsEqual.equalTo("ugly")));
    }

    @Test
    public void hasAMismatchDescriptionDescribingTheFirstFailingMatch() {
        AbstractMatcherTest.assertMismatchDescription("\"good\" was \"bad\"", AllOf.allOf(IsEqual.equalTo("bad"), IsEqual.equalTo("good")), "bad");
    }

    @Test
    public void varargs() {
        MatcherAssert.assertThat("the text!", new AllOf(StringStartsWith.startsWith("the"), StringContains.containsString("text"), StringEndsWith.endsWith("!")));
    }
}

