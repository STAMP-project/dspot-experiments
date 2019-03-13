package org.robolectric.res;


import ResType.BOOLEAN;
import ResType.COLOR;
import ResType.DIMEN;
import ResType.FRACTION;
import ResType.INTEGER;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ResType}
 */
@RunWith(JUnit4.class)
public class ResTypeTest {
    @Test
    public void testInferFromValue() {
        assertThat(ResType.inferFromValue("#802C76AD")).isEqualTo(COLOR);
        assertThat(ResType.inferFromValue("true")).isEqualTo(BOOLEAN);
        assertThat(ResType.inferFromValue("false")).isEqualTo(BOOLEAN);
        assertThat(ResType.inferFromValue("10dp")).isEqualTo(DIMEN);
        assertThat(ResType.inferFromValue("10sp")).isEqualTo(DIMEN);
        assertThat(ResType.inferFromValue("10pt")).isEqualTo(DIMEN);
        assertThat(ResType.inferFromValue("10px")).isEqualTo(DIMEN);
        assertThat(ResType.inferFromValue("10in")).isEqualTo(DIMEN);
        assertThat(ResType.inferFromValue("10")).isEqualTo(INTEGER);
        assertThat(ResType.inferFromValue("10.9")).isEqualTo(FRACTION);
    }
}

