package io.dropwizard.jersey.params;


import org.junit.jupiter.api.Test;


public class BooleanParamTest {
    @Test
    public void trueReturnsTrue() {
        final BooleanParam param = new BooleanParam("true");
        assertThat(param.get()).isTrue();
    }

    @Test
    public void uppercaseTrueReturnsTrue() {
        final BooleanParam param = new BooleanParam("TRUE");
        assertThat(param.get()).isTrue();
    }

    @Test
    public void falseReturnsFalse() {
        final BooleanParam param = new BooleanParam("false");
        assertThat(param.get()).isFalse();
    }

    @Test
    public void uppercaseFalseReturnsFalse() {
        final BooleanParam param = new BooleanParam("FALSE");
        assertThat(param.get()).isFalse();
    }

    @Test
    public void nullThrowsAnException() {
        booleanParamNegativeTest(null);
    }

    @Test
    public void nonBooleanValuesThrowAnException() {
        booleanParamNegativeTest("foo");
    }
}

