package net.bytebuddy.test.utility;


import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class CustomHamcrestMatchers {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Test
    public void testContainMatcherSucceeds() throws Exception {
        MatcherAssert.assertThat(Arrays.asList(CustomHamcrestMatchers.FOO, CustomHamcrestMatchers.BAR, CustomHamcrestMatchers.QUX), CustomHamcrestMatchers.containsAllOf(Arrays.asList(CustomHamcrestMatchers.QUX, CustomHamcrestMatchers.FOO, CustomHamcrestMatchers.BAR)));
    }

    @Test(expected = AssertionError.class)
    public void testContainMatcherFails() throws Exception {
        MatcherAssert.assertThat(Arrays.asList(CustomHamcrestMatchers.FOO, CustomHamcrestMatchers.BAR, CustomHamcrestMatchers.QUX), CustomHamcrestMatchers.containsAllOf(Arrays.asList(CustomHamcrestMatchers.QUX, CustomHamcrestMatchers.FOO, CustomHamcrestMatchers.BAZ)));
    }
}

