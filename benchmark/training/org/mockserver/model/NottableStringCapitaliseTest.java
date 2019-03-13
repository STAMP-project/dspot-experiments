package org.mockserver.model;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class NottableStringCapitaliseTest {
    @Test
    public void shouldCapitalisePlainValues() {
        MatcherAssert.assertThat(NottableString.not("value").capitalize(), Is.is(NottableString.not("Value")));
        MatcherAssert.assertThat(NottableString.string("value").capitalize(), Is.is(NottableString.string("Value")));
        MatcherAssert.assertThat(NottableString.not("Value").capitalize(), Is.is(NottableString.not("Value")));
        MatcherAssert.assertThat(NottableString.string("Value").capitalize(), Is.is(NottableString.string("Value")));
    }

    @Test
    public void shouldCapitaliseValuesWithDash() {
        MatcherAssert.assertThat(NottableString.not("value-value").capitalize(), Is.is(NottableString.not("Value-Value")));
        MatcherAssert.assertThat(NottableString.string("value-value").capitalize(), Is.is(NottableString.string("Value-Value")));
        MatcherAssert.assertThat(NottableString.not("Value-Value").capitalize(), Is.is(NottableString.not("Value-Value")));
        MatcherAssert.assertThat(NottableString.string("Value-Value").capitalize(), Is.is(NottableString.string("Value-Value")));
    }

    @Test
    public void shouldCapitaliseValuesWithDashAtStart() {
        MatcherAssert.assertThat(NottableString.not("-value-value").capitalize(), Is.is(NottableString.not("-Value-Value")));
        MatcherAssert.assertThat(NottableString.string("-value-value").capitalize(), Is.is(NottableString.string("-Value-Value")));
        MatcherAssert.assertThat(NottableString.not("-Value-Value").capitalize(), Is.is(NottableString.not("-Value-Value")));
        MatcherAssert.assertThat(NottableString.string("-Value-Value").capitalize(), Is.is(NottableString.string("-Value-Value")));
    }

    @Test
    public void shouldCapitaliseValuesWithDashAtEnd() {
        MatcherAssert.assertThat(NottableString.not("value-value-").capitalize(), Is.is(NottableString.not("Value-Value-")));
        MatcherAssert.assertThat(NottableString.string("value-value-").capitalize(), Is.is(NottableString.string("Value-Value-")));
        MatcherAssert.assertThat(NottableString.not("Value-Value-").capitalize(), Is.is(NottableString.not("Value-Value-")));
        MatcherAssert.assertThat(NottableString.string("Value-Value-").capitalize(), Is.is(NottableString.string("Value-Value-")));
    }

    @Test
    public void shouldCapitaliseValuesWithDashAtStartAndEnd() {
        MatcherAssert.assertThat(NottableString.not("-value-value-").capitalize(), Is.is(NottableString.not("-Value-Value-")));
        MatcherAssert.assertThat(NottableString.string("-value-value-").capitalize(), Is.is(NottableString.string("-Value-Value-")));
        MatcherAssert.assertThat(NottableString.not("-Value-Value-").capitalize(), Is.is(NottableString.not("-Value-Value-")));
        MatcherAssert.assertThat(NottableString.string("-Value-Value-").capitalize(), Is.is(NottableString.string("-Value-Value-")));
    }

    @Test
    public void shouldCapitaliseValuesWithMultipleTouchingDashes() {
        MatcherAssert.assertThat(NottableString.not("value--value").capitalize(), Is.is(NottableString.not("Value--Value")));
        MatcherAssert.assertThat(NottableString.string("value--value").capitalize(), Is.is(NottableString.string("Value--Value")));
        MatcherAssert.assertThat(NottableString.not("Value--Value").capitalize(), Is.is(NottableString.not("Value--Value")));
        MatcherAssert.assertThat(NottableString.string("Value--Value").capitalize(), Is.is(NottableString.string("Value--Value")));
    }

    @Test
    public void shouldLowercaseValues() {
        MatcherAssert.assertThat(NottableString.not("Value").lowercase(), Is.is(NottableString.not("value")));
        MatcherAssert.assertThat(NottableString.string("valuE").lowercase(), Is.is(NottableString.string("value")));
        MatcherAssert.assertThat(NottableString.not("VALUE").lowercase(), Is.is(NottableString.not("value")));
        MatcherAssert.assertThat(NottableString.string("value").lowercase(), Is.is(NottableString.string("value")));
    }
}

