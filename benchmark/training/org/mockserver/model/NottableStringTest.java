package org.mockserver.model;


import junit.framework.TestCase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Test;


public class NottableStringTest {
    @Test
    public void shouldReturnValuesSetInConstructors() {
        // when
        NottableString nottableString = NottableString.not("value");
        // then
        MatcherAssert.assertThat(nottableString.isNot(), Is.is(true));
        MatcherAssert.assertThat(nottableString.getValue(), Is.is("value"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorsWithDefaultNotSetting() {
        // when
        NottableString nottableString = NottableString.string("value");
        // then
        MatcherAssert.assertThat(nottableString.isNot(), Is.is(false));
        MatcherAssert.assertThat(nottableString.getValue(), Is.is("value"));
    }

    @Test
    public void shouldReturnValuesSetInConstructorsWithNullNotParameter() {
        // when
        NottableString nottableString = NottableString.string("value", null);
        // then
        MatcherAssert.assertThat(nottableString.isNot(), Is.is(false));
        MatcherAssert.assertThat(nottableString.getValue(), Is.is("value"));
    }

    @Test
    public void shouldEqual() {
        MatcherAssert.assertThat(NottableString.string("value"), Is.is(NottableString.string("value")));
        MatcherAssert.assertThat(NottableString.not("value"), Is.is(NottableString.not("value")));
        MatcherAssert.assertThat(NottableString.string("value"), Is.is(((Object) ("value"))));
    }

    @Test
    public void shouldEqualIgnoreCase() {
        TestCase.assertTrue(NottableString.string("value").equalsIgnoreCase(NottableString.string("VALUE")));
        TestCase.assertTrue(NottableString.not("value").equalsIgnoreCase(NottableString.not("vaLUe")));
        TestCase.assertTrue(NottableString.string("value").equalsIgnoreCase("VaLue"));
    }

    @Test
    public void shouldEqualWhenNull() {
        MatcherAssert.assertThat(NottableString.string(null), Is.is(NottableString.string(null)));
        MatcherAssert.assertThat(NottableString.string("value"), IsNot.not(NottableString.string(null)));
        MatcherAssert.assertThat(NottableString.string(null), IsNot.not(NottableString.string("value")));
    }

    @Test
    public void shouldEqualForDoubleNegative() {
        MatcherAssert.assertThat(NottableString.not("value"), IsNot.not(NottableString.string("value")));
        MatcherAssert.assertThat(NottableString.not("value"), IsNot.not(((Object) ("value"))));
        MatcherAssert.assertThat(NottableString.string("value"), IsNot.not(NottableString.string("other_value")));
        MatcherAssert.assertThat(NottableString.string("value"), IsNot.not(((Object) ("other_value"))));
        MatcherAssert.assertThat(NottableString.string("value"), IsNot.not(NottableString.not("value")));
    }

    @Test
    public void shouldEqualForDoubleNegativeIgnoreCase() {
        TestCase.assertFalse(NottableString.not("value").equalsIgnoreCase(NottableString.string("VAlue")));
        TestCase.assertFalse(NottableString.not("value").equalsIgnoreCase("vaLUe"));
        TestCase.assertFalse(NottableString.string("value").equalsIgnoreCase(NottableString.string("other_value")));
        TestCase.assertFalse(NottableString.string("value").equalsIgnoreCase("OTHER_value"));
        TestCase.assertFalse(NottableString.string("value").equalsIgnoreCase(NottableString.not("VALUE")));
    }

    @Test
    public void shouldEqualForNotValueNull() {
        TestCase.assertTrue(NottableString.not("value").equals(NottableString.string("value", true)));
        TestCase.assertTrue(NottableString.string("value").equals(NottableString.string("value", false)));
        NottableString initiallyTrueValue = NottableString.string("value");
        TestCase.assertTrue(initiallyTrueValue.equals(NottableString.string("value", null)));
        TestCase.assertFalse(NottableString.string("value", null).isNot());
    }

    @Test
    public void shouldConvertToString() {
        MatcherAssert.assertThat(NottableString.not("value").toString(), Is.is("!value"));
        MatcherAssert.assertThat(("" + (NottableString.not("value"))), Is.is("!value"));
        MatcherAssert.assertThat(String.valueOf(NottableString.not("value")), Is.is("!value"));
        MatcherAssert.assertThat(NottableString.string("value").toString(), Is.is("value"));
        MatcherAssert.assertThat(("" + (NottableString.string("value"))), Is.is("value"));
        MatcherAssert.assertThat(String.valueOf(NottableString.string("value")), Is.is("value"));
    }
}

