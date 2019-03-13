package io.dropwizard.configuration;


import org.junit.jupiter.api.Test;


public class EnvironmentVariableLookupTest {
    @Test
    public void defaultConstructorEnablesStrict() {
        assumeThat(System.getenv("nope")).isNull();
        assertThatExceptionOfType(UndefinedEnvironmentVariableException.class).isThrownBy(() -> new EnvironmentVariableLookup().lookup("nope"));
    }

    @Test
    public void lookupReplacesWithEnvironmentVariables() {
        EnvironmentVariableLookup lookup = new EnvironmentVariableLookup(false);
        // Let's hope this doesn't break on Windows
        assertThat(lookup.lookup("TEST")).isEqualTo(System.getenv("TEST"));
        assertThat(lookup.lookup("nope")).isNull();
    }

    @Test
    public void lookupThrowsExceptionInStrictMode() {
        assumeThat(System.getenv("nope")).isNull();
        assertThatExceptionOfType(UndefinedEnvironmentVariableException.class).isThrownBy(() -> new EnvironmentVariableLookup(true).lookup("nope"));
    }
}

