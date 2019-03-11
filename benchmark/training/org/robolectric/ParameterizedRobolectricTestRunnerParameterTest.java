package org.robolectric;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameter;
import org.robolectric.annotation.Config;


/**
 * Tests for the {@link Parameter} annotation
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public final class ParameterizedRobolectricTestRunnerParameterTest {
    @Parameter(0)
    public boolean booleanValue;

    @Parameter(1)
    public int intValue;

    @Parameter(2)
    public String stringValue;

    @Parameter(3)
    public String expected;

    @Test
    public void parameters_shouldHaveValues() {
        assertThat(((("" + (booleanValue)) + (intValue)) + (stringValue))).isEqualTo(expected);
    }
}

