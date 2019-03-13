package org.web3j.utils;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class CollectionTest {
    @Test
    public void testTail() {
        Assert.assertThat(Collection.tail(Collection.EMPTY_STRING_ARRAY), Is.is(Collection.EMPTY_STRING_ARRAY));
        Assert.assertThat(Collection.tail(Collection.create("a", "b", "c")), Is.is(Collection.create("b", "c")));
        Assert.assertThat(Collection.tail(Collection.create("a")), Is.is(Collection.EMPTY_STRING_ARRAY));
    }

    @Test
    public void testCreate() {
        Assert.assertThat(Collection.create("a"), Is.is(new String[]{ "a" }));
        Assert.assertThat(Collection.create(""), Is.is(new String[]{ "" }));
        Assert.assertThat(Collection.create("a", "b"), Is.is(new String[]{ "a", "b" }));
    }

    @Test
    public void testJoin() {
        Assert.assertThat(Collection.join(Arrays.asList("a  ", "b ", " c "), ","), Is.is("a,b,c"));
        Assert.assertThat(Collection.join(Arrays.asList("a", "b", "c", "d"), ","), Is.is("a,b,c,d"));
        Assert.assertThat(Collection.join(Arrays.asList("a  ", "b ", " c "), ", "), Is.is("a, b, c"));
        Assert.assertThat(Collection.join(Arrays.asList("a", "b", "c", "d"), ", "), Is.is("a, b, c, d"));
    }

    @Test
    public void testJoinWithFunction() {
        final List<CollectionTest.FakeSpec> specs1 = Arrays.asList(new CollectionTest.FakeSpec("a"), new CollectionTest.FakeSpec("b"), new CollectionTest.FakeSpec("c"));
        Assert.assertThat(Collection.join(specs1, ",", CollectionTest.FakeSpec::getName), Is.is("a,b,c"));
        final List<CollectionTest.FakeSpec> specs2 = Arrays.asList(new CollectionTest.FakeSpec("a"), new CollectionTest.FakeSpec("b"), new CollectionTest.FakeSpec("c"));
        Assert.assertThat(Collection.join(specs2, ", ", CollectionTest.FakeSpec::getName), Is.is("a, b, c"));
        final List<CollectionTest.FakeSpec> specs3 = Arrays.asList(new CollectionTest.FakeSpec(" a"), new CollectionTest.FakeSpec("b  "), new CollectionTest.FakeSpec(" c "));
        Assert.assertThat(Collection.join(specs3, ",", CollectionTest.FakeSpec::getName), Is.is("a,b,c"));
        final List<CollectionTest.FakeSpec> specs4 = Arrays.asList(new CollectionTest.FakeSpec(" a"), new CollectionTest.FakeSpec("b  "), new CollectionTest.FakeSpec(" c "));
        Assert.assertThat(Collection.join(specs4, ", ", CollectionTest.FakeSpec::getName), Is.is("a, b, c"));
    }

    /**
     * Fake object to test {@link Collection#join(List, String, Function)}.
     */
    private final class FakeSpec {
        private final String name;

        private FakeSpec(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

