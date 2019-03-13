/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitousage.IMethods;
import org.mockitoutil.TestBase;


public class CustomMatchersTest extends TestBase {
    private final class ContainsFoo implements ArgumentMatcher<String> {
        public boolean matches(String arg) {
            return arg.contains("foo");
        }
    }

    private final class IsAnyBoolean implements ArgumentMatcher<Boolean> {
        public boolean matches(Boolean arg) {
            return true;
        }
    }

    private final class IsSorZ implements ArgumentMatcher<Character> {
        public boolean matches(Character character) {
            return (character.equals('s')) || (character.equals('z'));
        }
    }

    private final class IsZeroOrOne<T extends Number> implements ArgumentMatcher<T> {
        public boolean matches(T number) {
            return ((number.intValue()) == 0) || ((number.intValue()) == 1);
        }
    }

    private IMethods mock;

    @Test
    public void shouldUseCustomBooleanMatcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.booleanThat(new CustomMatchersTest.IsAnyBoolean()))).thenReturn("foo");
        Assert.assertEquals("foo", mock.oneArg(true));
        Assert.assertEquals("foo", mock.oneArg(false));
        Assert.assertEquals(null, mock.oneArg("x"));
    }

    @Test
    public void shouldUseCustomCharMatcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.charThat(new CustomMatchersTest.IsSorZ()))).thenReturn("foo");
        Assert.assertEquals("foo", mock.oneArg('s'));
        Assert.assertEquals("foo", mock.oneArg('z'));
        Assert.assertEquals(null, mock.oneArg('x'));
    }

    class Article {
        private int pageNumber;

        private String headline;

        public Article(int pageNumber, String headline) {
            super();
            this.pageNumber = pageNumber;
            this.headline = headline;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public String getHeadline() {
            return headline;
        }
    }

    @Test
    public void shouldUseCustomPrimitiveNumberMatchers() {
        Mockito.when(mock.oneArg(ArgumentMatchers.byteThat(new CustomMatchersTest.IsZeroOrOne<Byte>()))).thenReturn("byte");
        Mockito.when(mock.oneArg(ArgumentMatchers.shortThat(new CustomMatchersTest.IsZeroOrOne<Short>()))).thenReturn("short");
        Mockito.when(mock.oneArg(ArgumentMatchers.intThat(new CustomMatchersTest.IsZeroOrOne<Integer>()))).thenReturn("int");
        Mockito.when(mock.oneArg(ArgumentMatchers.longThat(new CustomMatchersTest.IsZeroOrOne<Long>()))).thenReturn("long");
        Mockito.when(mock.oneArg(ArgumentMatchers.floatThat(new CustomMatchersTest.IsZeroOrOne<Float>()))).thenReturn("float");
        Mockito.when(mock.oneArg(ArgumentMatchers.doubleThat(new CustomMatchersTest.IsZeroOrOne<Double>()))).thenReturn("double");
        Assert.assertEquals("byte", mock.oneArg(((byte) (0))));
        Assert.assertEquals("short", mock.oneArg(((short) (1))));
        Assert.assertEquals("int", mock.oneArg(0));
        Assert.assertEquals("long", mock.oneArg(1L));
        Assert.assertEquals("float", mock.oneArg(0.0F));
        Assert.assertEquals("double", mock.oneArg(1.0));
        Assert.assertEquals(null, mock.oneArg(2));
        Assert.assertEquals(null, mock.oneArg("foo"));
    }

    @Test
    public void shouldUseCustomObjectMatcher() {
        Mockito.when(mock.oneArg(ArgumentMatchers.argThat(new CustomMatchersTest.ContainsFoo()))).thenReturn("foo");
        Assert.assertEquals("foo", mock.oneArg("foo"));
        Assert.assertEquals(null, mock.oneArg("bar"));
    }

    @Test
    public void shouldCustomMatcherPrintDescriptionBasedOnName() {
        mock.simpleMethod("foo");
        try {
            Mockito.verify(mock).simpleMethod(containsTest());
            Assert.fail();
        } catch (AssertionError e) {
            assertThat(e).hasMessageContaining("<String that contains xxx>");
        }
    }

    private final class StringThatContainsXxx implements ArgumentMatcher<String> {
        public boolean matches(String arg) {
            return arg.contains("xxx");
        }
    }

    @Test
    public void shouldAnonymousCustomMatcherPrintDefaultDescription() {
        mock.simpleMethod("foo");
        try {
            Mockito.verify(mock).simpleMethod(((String) (ArgumentMatchers.argThat(new ArgumentMatcher<Object>() {
                public boolean matches(Object argument) {
                    return false;
                }
            }))));
            Assert.fail();
        } catch (AssertionError e) {
            assertThat(e).hasMessageContaining("<custom argument matcher>").hasMessageContaining("foo");
        }
    }
}

