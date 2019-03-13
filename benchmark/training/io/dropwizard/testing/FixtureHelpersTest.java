package io.dropwizard.testing;


import org.junit.jupiter.api.Test;


public class FixtureHelpersTest {
    @Test
    public void readsTheFileAsAString() {
        assertThat(FixtureHelpers.fixture("fixtures/fixture.txt")).isEqualTo("YAY FOR ME");
    }

    @Test
    public void throwsIllegalStateExceptionWhenFileDoesNotExist() {
        assertThatIllegalArgumentException().isThrownBy(() -> fixture("this-does-not-exist.foo"));
    }
}

