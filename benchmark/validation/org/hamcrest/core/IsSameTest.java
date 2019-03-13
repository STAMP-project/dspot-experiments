package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsSameTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = IsSame.sameInstance("irrelevant");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void evaluatesToTrueIfArgumentIsReferenceToASpecifiedObject() {
        Object o1 = new Object();
        Matcher<Object> matcher = IsSame.sameInstance(o1);
        AbstractMatcherTest.assertMatches(matcher, o1);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
    }

    @Test
    public void alternativeFactoryMethodAlsoMatchesOnlyIfArgumentIsReferenceToASpecifiedObject() {
        Object o1 = new Object();
        Matcher<Object> matcher = IsSame.theInstance(o1);
        AbstractMatcherTest.assertMatches(matcher, o1);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
    }

    @Test
    public void returnsReadableDescriptionFromToString() {
        AbstractMatcherTest.assertDescription("sameInstance(\"ARG\")", IsSame.sameInstance("ARG"));
    }

    @Test
    public void returnsReadableDescriptionFromToStringWhenInitialisedWithNull() {
        AbstractMatcherTest.assertDescription("sameInstance(null)", IsSame.sameInstance(null));
    }
}

