package org.junit;


import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class AssumptionViolatedExceptionTest {
    @DataPoint
    public static Integer TWO = 2;

    @DataPoint
    public static Matcher<Integer> IS_THREE = CoreMatchers.is(3);

    @DataPoint
    public static Matcher<Integer> NULL = null;

    @Test
    public void assumptionViolatedExceptionWithMatcherDescribesItself() {
        AssumptionViolatedException e = new AssumptionViolatedException(3, CoreMatchers.is(2));
        Assert.assertThat(StringDescription.asString(e), CoreMatchers.is("got: <3>, expected: is <2>"));
    }

    @Test
    public void simpleAssumptionViolatedExceptionDescribesItself() {
        AssumptionViolatedException e = new AssumptionViolatedException("not enough money");
        Assert.assertThat(StringDescription.asString(e), CoreMatchers.is("not enough money"));
    }

    @Test
    public void canInitCauseWithInstanceCreatedWithString() {
        AssumptionViolatedException e = new AssumptionViolatedException("invalid number");
        Throwable cause = new RuntimeException("cause");
        e.initCause(cause);
        Assert.assertThat(e.getCause(), CoreMatchers.is(cause));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithObjectAndMatcher() {
        Throwable testObject = new Exception();
        org.junit.internal.AssumptionViolatedException e = new org.junit.internal.AssumptionViolatedException(testObject, CoreMatchers.containsString("test matcher"));
        Assert.assertThat(e.getCause(), CoreMatchers.is(testObject));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithAssumptionObjectAndMatcher() {
        Throwable testObject = new Exception();
        org.junit.internal.AssumptionViolatedException e = new org.junit.internal.AssumptionViolatedException("sample assumption", testObject, CoreMatchers.containsString("test matcher"));
        Assert.assertThat(e.getCause(), CoreMatchers.is(testObject));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void canSetCauseWithInstanceCreatedWithMainConstructor() {
        Throwable testObject = new Exception();
        org.junit.internal.AssumptionViolatedException e = new org.junit.internal.AssumptionViolatedException("sample assumption", false, testObject, CoreMatchers.containsString("test matcher"));
        Assert.assertThat(e.getCause(), CoreMatchers.is(testObject));
    }

    @Test
    public void canSetCauseWithInstanceCreatedWithExplicitThrowableConstructor() {
        Throwable cause = new Exception();
        AssumptionViolatedException e = new AssumptionViolatedException("invalid number", cause);
        Assert.assertThat(e.getCause(), CoreMatchers.is(cause));
    }
}

