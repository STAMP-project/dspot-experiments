package org.junit.tests.experimental.theories.internal;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class ParameterizedAssertionErrorTest {
    @DataPoint
    public static final String METHOD_NAME = "methodName";

    @DataPoint
    public static final NullPointerException NULL_POINTER_EXCEPTION = new NullPointerException();

    @DataPoint
    public static Object[] NO_OBJECTS = new Object[0];

    @DataPoint
    public static ParameterizedAssertionError A = new ParameterizedAssertionError(ParameterizedAssertionErrorTest.NULL_POINTER_EXCEPTION, ParameterizedAssertionErrorTest.METHOD_NAME);

    @DataPoint
    public static ParameterizedAssertionError B = new ParameterizedAssertionError(ParameterizedAssertionErrorTest.NULL_POINTER_EXCEPTION, ParameterizedAssertionErrorTest.METHOD_NAME);

    @DataPoint
    public static ParameterizedAssertionError B2 = new ParameterizedAssertionError(ParameterizedAssertionErrorTest.NULL_POINTER_EXCEPTION, "methodName2");

    @Test
    public void canJoinWhenToStringFails() {
        Assert.assertThat(ParameterizedAssertionError.join(" ", new Object() {
            @Override
            public String toString() {
                throw new UnsupportedOperationException();
            }
        }), CoreMatchers.is("[toString failed]"));
    }
}

