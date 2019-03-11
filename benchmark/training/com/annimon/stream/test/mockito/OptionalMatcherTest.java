package com.annimon.stream.test.mockito;


import com.annimon.stream.Optional;
import com.annimon.stream.test.hamcrest.CommonMatcher;
import com.annimon.stream.test.mockito.OptionalMatcher.EmptyOptionalMatcher;
import com.annimon.stream.test.mockito.OptionalMatcher.PresentOptionalMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class OptionalMatcherTest {
    private static final Optional<Object> PRESENT_OBJECT_OPTIONAL = Optional.of(new Object());

    private static final Optional<Object> EMPTY_OBJECT_OPTIONAL = Optional.empty();

    private static final Optional<String> PRESENT_STRING_OPTIONAL = Optional.of("ANY_STRING");

    private static final Optional<String> EMPTY_STRING_OPTIONAL = Optional.empty();

    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(OptionalMatcher.class, CommonMatcher.hasOnlyPrivateConstructors());
    }

    @Test
    public void testPresentOptionalMatcherMatching() {
        PresentOptionalMatcher<Object> matcher = new PresentOptionalMatcher<Object>();
        Assert.assertTrue(matcher.matches(OptionalMatcherTest.PRESENT_OBJECT_OPTIONAL));
        Assert.assertFalse(matcher.matches(OptionalMatcherTest.EMPTY_OBJECT_OPTIONAL));
        Assert.assertFalse(matcher.matches(null));
    }

    @Test
    public void testPresentOptionalMatcherToString() {
        PresentOptionalMatcher<Object> matcher = new PresentOptionalMatcher<Object>();
        Assert.assertEquals(matcher.toString(), "anyPresentOptional()");
    }

    @Test
    public void testAnyPresentObjectOptionalWhenStubbing() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        Mockito.when(foo.barObject(OptionalMatcher.anyPresentOptional())).thenReturn(true);
        Assert.assertFalse(foo.barObject(OptionalMatcherTest.EMPTY_OBJECT_OPTIONAL));
        Assert.assertTrue(foo.barObject(OptionalMatcherTest.PRESENT_OBJECT_OPTIONAL));
    }

    @Test
    public void testAnyPresentObjectOptionalWhenVerifying() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        foo.barObject(OptionalMatcherTest.PRESENT_OBJECT_OPTIONAL);
        Mockito.verify(foo, Mockito.times(1)).barObject(OptionalMatcher.anyPresentOptional());
        Mockito.verify(foo, Mockito.never()).barObject(OptionalMatcher.anyEmptyOptional());
    }

    @Test
    public void testAnyPresentStringOptionalWhenStubbing() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        Mockito.when(foo.barString(OptionalMatcher.anyPresentOptional(String.class))).thenReturn(true);
        Assert.assertFalse(foo.barString(OptionalMatcherTest.EMPTY_STRING_OPTIONAL));
        Assert.assertTrue(foo.barString(OptionalMatcherTest.PRESENT_STRING_OPTIONAL));
    }

    @Test
    public void testAnyPresentStringOptionalWhenVerifying() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        foo.barString(OptionalMatcherTest.PRESENT_STRING_OPTIONAL);
        Mockito.verify(foo, Mockito.times(1)).barString(OptionalMatcher.anyPresentOptional(String.class));
        Mockito.verify(foo, Mockito.never()).barString(OptionalMatcher.anyEmptyOptional(String.class));
    }

    @Test
    public void testEmptyOptionalMatcherMatching() {
        EmptyOptionalMatcher<Object> matcher = new EmptyOptionalMatcher<Object>();
        Assert.assertFalse(matcher.matches(OptionalMatcherTest.PRESENT_OBJECT_OPTIONAL));
        Assert.assertTrue(matcher.matches(OptionalMatcherTest.EMPTY_OBJECT_OPTIONAL));
        Assert.assertFalse(matcher.matches(null));
    }

    @Test
    public void testEmptyOptionalMatcherToString() {
        EmptyOptionalMatcher<Object> matcher = new EmptyOptionalMatcher<Object>();
        Assert.assertEquals(matcher.toString(), "anyEmptyOptional()");
    }

    @Test
    public void testAnyEmptyObjectOptionalWhenStubbing() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        Mockito.when(foo.barObject(OptionalMatcher.anyEmptyOptional())).thenReturn(true);
        Assert.assertTrue(foo.barObject(OptionalMatcherTest.EMPTY_OBJECT_OPTIONAL));
        Assert.assertFalse(foo.barObject(OptionalMatcherTest.PRESENT_OBJECT_OPTIONAL));
    }

    @Test
    public void testAnyEmptyObjectOptionalWhenVerifying() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        foo.barObject(OptionalMatcherTest.EMPTY_OBJECT_OPTIONAL);
        Mockito.verify(foo, Mockito.never()).barObject(OptionalMatcher.anyPresentOptional());
        Mockito.verify(foo, Mockito.times(1)).barObject(OptionalMatcher.anyEmptyOptional());
    }

    @Test
    public void testAnyEmptyStringOptionalWhenStubbing() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        Mockito.when(foo.barString(OptionalMatcher.anyEmptyOptional(String.class))).thenReturn(true);
        Assert.assertTrue(foo.barString(OptionalMatcherTest.EMPTY_STRING_OPTIONAL));
        Assert.assertFalse(foo.barString(OptionalMatcherTest.PRESENT_STRING_OPTIONAL));
    }

    @Test
    public void testAnyEmptyStringOptionalWhenVerifying() {
        OptionalMatcherTest.Foo foo = Mockito.mock(OptionalMatcherTest.Foo.class);
        foo.barString(OptionalMatcherTest.EMPTY_STRING_OPTIONAL);
        Mockito.verify(foo, Mockito.never()).barString(OptionalMatcher.anyPresentOptional(String.class));
        Mockito.verify(foo, Mockito.times(1)).barString(OptionalMatcher.anyEmptyOptional(String.class));
    }

    private interface Foo {
        boolean barObject(Optional<Object> argument);

        boolean barString(Optional<String> argument);
    }
}

