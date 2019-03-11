/**
 * same methods as org.junit.Assert.*
 */
/**
 * ditto, with different names
 */
package com.baeldung.testing.jgotesting;


import org.hamcrest.Matchers;
import org.jgotesting.Checker;
import org.jgotesting.rule.JGoTestRule;
import org.junit.Rule;
import org.junit.Test;


public class JGoTestingUnitTest {
    @Rule
    public final JGoTestRule test = new JGoTestRule();

    @Test
    public void whenComparingIntegers_thenEqual() {
        int anInt = 10;
        assertEquals(anInt, 10);
        checkEquals(anInt, 10);
    }

    @Test
    public void whenComparingStrings_thenMultipleAssertions() {
        String aString = "This is a string";
        String anotherString = "This Is A String";
        test.check(aString, Matchers.equalToIgnoringCase(anotherString)).check(((aString.length()) == 16)).check(aString.startsWith("This"));
    }

    @Test
    public void givenChecker_whenComparingStrings_thenEqual() throws Exception {
        Checker<String> aChecker = ( s) -> s.matches("\\d+");
        String aString = "1235";
        test.check(aString, aChecker);
    }
}

