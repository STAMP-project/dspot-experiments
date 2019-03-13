package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public final class AnyOfTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = AnyOf.anyOf(IsEqual.equalTo("irrelevant"), StringStartsWith.startsWith("irr"));
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void evaluatesToTheTheLogicalDisjunctionOfTwoOtherMatchers() {
        Matcher<String> matcher = AnyOf.anyOf(StringStartsWith.startsWith("goo"), StringEndsWith.endsWith("ood"));
        AbstractMatcherTest.assertMatches("didn't pass both sub-matchers", matcher, "good");
        AbstractMatcherTest.assertMatches("didn't pass second sub-matcher", matcher, "mood");
        AbstractMatcherTest.assertMatches("didn't pass first sub-matcher", matcher, "goon");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail both sub-matchers", matcher, "flan");
    }

    @Test
    public void evaluatesToTheTheLogicalDisjunctionOfManyOtherMatchers() {
        Matcher<String> matcher = AnyOf.anyOf(StringStartsWith.startsWith("g"), StringStartsWith.startsWith("go"), StringEndsWith.endsWith("d"), StringStartsWith.startsWith("go"), StringStartsWith.startsWith("goo"));
        AbstractMatcherTest.assertMatches("didn't pass middle sub-matcher", matcher, "vlad");
        AbstractMatcherTest.assertDoesNotMatch("didn't fail all sub-matchers", matcher, "flan");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void supportsMixedTypes() {
        final Matcher<SampleSubClass> matcher = AnyOf.anyOf(IsEqual.equalTo(new SampleBaseClass("bad")), IsEqual.equalTo(new SampleBaseClass("good")), IsEqual.equalTo(new SampleSubClass("ugly")));
        AbstractMatcherTest.assertMatches("didn't pass middle sub-matcher", matcher, new SampleSubClass("good"));
    }

    @Test
    public void hasAReadableDescription() {
        AbstractMatcherTest.assertDescription("(\"good\" or \"bad\" or \"ugly\")", AnyOf.anyOf(IsEqual.equalTo("good"), IsEqual.equalTo("bad"), IsEqual.equalTo("ugly")));
    }

    @Test
    public void varargs() {
        MatcherAssert.assertThat("the text!", new AnyOf(StringStartsWith.startsWith("the"), StringEndsWith.endsWith(".")));
    }
}

