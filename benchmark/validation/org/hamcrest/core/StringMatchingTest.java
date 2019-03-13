package org.hamcrest.core;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 *
 *
 * @author Steve Freeman 2016 http://www.hamcrest.com
 */
public class StringMatchingTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void startsWithFailsWithNullSubstring() {
        thrown.expect(IllegalArgumentException.class);
        StringStartsWith.startsWith(null);
    }

    @Test
    public void endWithFailsWithNullSubstring() {
        thrown.expect(IllegalArgumentException.class);
        StringEndsWith.endsWith(null);
    }

    @Test
    public void containsFailsWithNullSubstring() {
        thrown.expect(IllegalArgumentException.class);
        StringContains.containsString(null);
    }
}

