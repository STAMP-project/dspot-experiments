package fj.function;


import fj.F1Functions;
import fj.Function;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class StringsTest {
    @Test
    public void testLines() {
        Assert.assertThat(Function.compose(unlines(), lines()).f("one two three"), Is.is("one two three"));
    }

    @Test
    public void testLinesEmpty() {
        Assert.assertThat(F1Functions.o(unlines(), lines()).f(""), Is.is(""));
    }

    @Test
    public void testLength() {
        Assert.assertThat(length.f("functionaljava"), Is.is(14));
    }

    @Test
    public void testMatches() {
        Assert.assertThat(matches.f("foo").f("foo"), Is.is(true));
    }

    @Test
    public void testContains() {
        Assert.assertThat(contains.f("bar").f("foobar1"), Is.is(true));
    }

    @Test(expected = NullPointerException.class)
    public void testIsEmptyException() {
        Assert.assertThat(isEmpty.f(null), Is.is(true));
    }

    @Test
    public void testIsEmpty() {
        Assert.assertThat(isEmpty.f(""), Is.is(true));
    }

    @Test
    public void testIsNotNullOrEmpty() {
        Assert.assertThat(isNotNullOrEmpty.f("foo"), Is.is(true));
    }

    @Test
    public void testIsNullOrEmpty() {
        Assert.assertThat(isNullOrEmpty.f(null), Is.is(true));
    }

    @Test
    public void testIsNotNullOrBlank() {
        Assert.assertThat(isNotNullOrBlank.f("foo"), Is.is(true));
    }

    @Test
    public void testIsNullOrBlank() {
        Assert.assertThat(isNullOrBlank.f("  "), Is.is(true));
    }
}

