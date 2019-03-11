/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.hamcrest.BaseMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class HamcrestMatchersTest extends TestBase {
    private final class ContainsX extends BaseMatcher<String> {
        public boolean matches(Object o) {
            return ((String) (o)).contains("X");
        }

        public void describeTo(Description d) {
            d.appendText("contains 'X'");
        }
    }

    @Mock
    private IMethods mock;

    @Test
    public void stubs_with_hamcrest_matcher() {
        Mockito.when(mock.simpleMethod(MockitoHamcrest.argThat(new HamcrestMatchersTest.ContainsX()))).thenReturn("X");
        Assert.assertNull(mock.simpleMethod("blah"));
        Assert.assertEquals("X", mock.simpleMethod("blah X blah"));
    }

    @Test
    public void verifies_with_hamcrest_matcher() {
        mock.simpleMethod("blah");
        try {
            Mockito.verify(mock).simpleMethod(MockitoHamcrest.argThat(new HamcrestMatchersTest.ContainsX()));
            Assert.fail();
        } catch (ArgumentsAreDifferent e) {
            Assert.assertThat(e).hasMessageContaining("contains 'X'");
        }
    }

    private class IntMatcher extends BaseMatcher<Integer> {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    @Test
    public void supports_primitive_matchers() {
        Mockito.when(mock.intArgumentReturningInt(MockitoHamcrest.argThat(new HamcrestMatchersTest.IntMatcher()))).thenReturn(5);
        Assert.assertEquals(5, mock.intArgumentReturningInt(10));
    }

    @Test
    public void supports_primitive_matchers_from_core_library() {
        mock.oneArg(true);
        mock.oneArg(((byte) (1)));
        mock.oneArg(2);
        mock.oneArg(3L);
        mock.oneArg('4');
        mock.oneArg(5.0);
        mock.oneArg(6.0F);
        Mockito.verify(mock).oneArg(MockitoHamcrest.booleanThat(CoreMatchers.is(true)));
        Mockito.verify(mock).oneArg(MockitoHamcrest.byteThat(CoreMatchers.is(((byte) (1)))));
        Mockito.verify(mock).oneArg(MockitoHamcrest.intThat(CoreMatchers.is(2)));
        Mockito.verify(mock).oneArg(MockitoHamcrest.longThat(CoreMatchers.is(3L)));
        Mockito.verify(mock).oneArg(MockitoHamcrest.charThat(CoreMatchers.is('4')));
        Mockito.verify(mock).oneArg(MockitoHamcrest.doubleThat(CoreMatchers.is(5.0)));
        Mockito.verify(mock).oneArg(MockitoHamcrest.floatThat(CoreMatchers.is(6.0F)));
    }

    @SuppressWarnings("rawtypes")
    private class NonGenericMatcher extends BaseMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    @Test
    public void supports_non_generic_matchers() {
        Mockito.when(mock.intArgumentReturningInt(nonGenericMatcher())).thenReturn(5);
        Assert.assertEquals(5, mock.intArgumentReturningInt(10));
    }

    @Test
    public void coexists_with_mockito_matcher() {
        Mockito.when(mock.simpleMethod(Mockito.argThat(new ArgumentMatcher<String>() {
            public boolean matches(String argument) {
                return true;
            }
        }))).thenReturn("x");
        Assert.assertEquals("x", mock.simpleMethod("x"));
    }
}

