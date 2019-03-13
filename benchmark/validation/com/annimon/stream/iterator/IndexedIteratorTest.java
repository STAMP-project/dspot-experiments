package com.annimon.stream.iterator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class IndexedIteratorTest {
    @Test
    public void testDefault() {
        IndexedIterator<String> iterator = new IndexedIterator<String>(Arrays.asList("a", "b", "c").iterator());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(0));
        String value;
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("a"));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("b"));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(2));
        Assert.assertThat(iterator.getIndex(), Matchers.is(2));
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("c"));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(3));
    }

    @Test
    public void testWithStartAndStep() {
        IndexedIterator<String> iterator = new IndexedIterator<String>(100, (-5), Arrays.asList("a", "b", "c").iterator());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(100));
        String value;
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("a"));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(95));
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("b"));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(90));
        Assert.assertThat(iterator.getIndex(), Matchers.is(90));
        value = iterator.next();
        Assert.assertThat(value, Matchers.is("c"));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(85));
    }

    @Test
    public void testRemove() {
        List<String> list = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        IndexedIterator<String> iterator = new IndexedIterator<String>(list.iterator());
        iterator.next();
        iterator.next();
        iterator.remove();
        Assert.assertThat(list, Matchers.is(Arrays.asList("a", "c")));
    }
}

