package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsInstanceOfTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<?> matcher = IsInstanceOf.instanceOf(Number.class);
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void evaluatesToTrueIfArgumentIsInstanceOfASpecificClass() {
        final Matcher<Object> matcher = IsInstanceOf.instanceOf(Number.class);
        AbstractMatcherTest.assertMatches(matcher, 1);
        AbstractMatcherTest.assertMatches(matcher, 1.1);
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
    }

    @Test
    public void hasAReadableDescription() {
        AbstractMatcherTest.assertDescription("an instance of java.lang.Number", IsInstanceOf.instanceOf(Number.class));
    }

    @Test
    public void describesActualClassInMismatchMessage() {
        AbstractMatcherTest.assertMismatchDescription("\"some text\" is a java.lang.String", IsInstanceOf.instanceOf(Number.class), "some text");
    }

    @Test
    public void matchesPrimitiveTypes() {
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(boolean.class), true);
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(byte.class), ((byte) (1)));
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(char.class), 'x');
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(double.class), 5.0);
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(float.class), 5.0F);
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(int.class), 2);
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(long.class), 4L);
        AbstractMatcherTest.assertMatches(IsInstanceOf.any(short.class), ((short) (1)));
    }

    @Test
    public void instanceOfRequiresACastToReturnTheCorrectTypeForUseInJMock() {
        @SuppressWarnings("unused")
        Integer anInteger = ((Integer) (IsInstanceOfTest.with(IsInstanceOf.instanceOf(Integer.class))));
    }

    @Test
    public void anyWillReturnTheCorrectTypeForUseInJMock() {
        @SuppressWarnings("unused")
        Integer anInteger = IsInstanceOfTest.with(IsInstanceOf.any(Integer.class));
    }
}

