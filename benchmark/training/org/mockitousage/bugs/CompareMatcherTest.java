/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockitousage.IMethods;


public class CompareMatcherTest {
    private static final Object NOT_A_COMPARABLE = new Object();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    public IMethods mock;

    /**
     * Should not throw an {@link NullPointerException}
     *
     * @see <a href="https://github.com/mockito/mockito/issues/457">Bug 457</a>
     */
    @Test
    public void compareNullArgument() {
        final IMethods mock = Mockito.mock(IMethods.class);
        Mockito.when(mock.forInteger(AdditionalMatchers.leq(5))).thenReturn("");
        assertThat(mock.forInteger(null)).isNull();// a default value must be returned

    }

    /**
     * Should not throw an {@link ClassCastException}
     */
    @Test
    public void compareToNonCompareable() {
        Mockito.when(mock.forObject(AdditionalMatchers.leq(5))).thenReturn("");
        assertThat(mock.forObject(CompareMatcherTest.NOT_A_COMPARABLE)).isNull();// a default value must be returned

    }

    /**
     * Should not throw an {@link ClassCastException}
     */
    @Test
    public void compareToNull() {
        Mockito.when(mock.forInteger(AdditionalMatchers.leq(((Integer) (null))))).thenReturn("");
        assertThat(mock.forInteger(null)).isNull();// a default value must be returned

    }

    /**
     * Should not throw an {@link ClassCastException}
     */
    @Test
    public void compareToStringVsInt() {
        Mockito.when(mock.forObject(ArgumentMatchers.startsWith("Hello"))).thenReturn("");
        assertThat(mock.forObject(123)).isNull();// a default value must be returned

    }

    @Test
    public void compareToIntVsString() throws Exception {
        Mockito.when(mock.forObject(AdditionalMatchers.leq(5))).thenReturn("");
        mock.forObject("abc");
    }

    @Test
    public void matchesOverloadsMustBeIgnored() {
        class TestMatcher implements ArgumentMatcher<Integer> {
            @Override
            public boolean matches(Integer arg) {
                return false;
            }

            @SuppressWarnings("unused")
            public boolean matches(Date arg) {
                throw new UnsupportedOperationException();
            }

            @SuppressWarnings("unused")
            public boolean matches(Integer arg, Void v) {
                throw new UnsupportedOperationException();
            }
        }
        Mockito.when(mock.forObject(ArgumentMatchers.argThat(new TestMatcher()))).thenReturn("x");
        assertThat(mock.forObject(123)).isNull();
    }

    @Test
    public void matchesWithSubTypeExtendingGenericClass() {
        abstract class GenericMatcher<T> implements ArgumentMatcher<T> {}
        class TestMatcher extends GenericMatcher<Integer> {
            @Override
            public boolean matches(Integer argument) {
                return false;
            }
        }
        Mockito.when(mock.forObject(ArgumentMatchers.argThat(new TestMatcher()))).thenReturn("x");
        assertThat(mock.forObject(123)).isNull();
    }

    @Test
    public void matchesWithSubTypeGenericMethod() {
        class GenericMatcher<T> implements ArgumentMatcher<T> {
            @Override
            public boolean matches(T argument) {
                return false;
            }
        }
        Mockito.when(mock.forObject(ArgumentMatchers.argThat(new GenericMatcher<Integer>()))).thenReturn("x");
        assertThat(mock.forObject(123)).isNull();
    }
}

