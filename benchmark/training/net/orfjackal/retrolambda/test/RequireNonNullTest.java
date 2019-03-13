/**
 * Copyright ? 2013-2016 Esko Luontola and other Retrolambda contributors
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import java.util.Objects;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class RequireNonNullTest {
    @Test
    public void requireNonNull__silent_when_non_null() {
        Objects.requireNonNull(new Object());
    }

    @Test(expected = NullPointerException.class)
    public void requireNonNull__throws_NPE_when_null() {
        Objects.requireNonNull(null);
    }

    @Test
    public void requireNonNull__returns_the_argument() {
        Object expected = new Object();
        Object actual = Objects.requireNonNull(expected);
        MatcherAssert.assertThat(actual, is(sameInstance(expected)));
    }

    @Test
    public void synthetic_null_check__silent_when_non_null() {
        RequireNonNullTest.syntheticNullCheck(new RequireNonNullTest.MaybeNull());
    }

    @Test(expected = NullPointerException.class)
    public void synthetic_null_check__throws_NPE_when_null() {
        RequireNonNullTest.syntheticNullCheck(null);
    }

    private static class MaybeNull {
        final int foo = 0;
    }
}

