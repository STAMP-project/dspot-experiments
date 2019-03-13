package fj.data;


import Enumerator.intEnumerator;
import Equal.charEqual;
import fj.P2;
import java.util.ConcurrentModificationException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Zheka Kozlov on 27.05.2015.
 */
public class StreamTest {
    @Test
    public void infiniteStream() {
        Stream.Stream<Integer> s = Stream.Stream.forever(intEnumerator, 0).bind(Stream.Stream::single);
        Assert.assertThat(List.range(0, 5), CoreMatchers.is(s.take(5).toList()));
    }

    @Test
    public void testToString() {
        Stream.Stream<Integer> range = Stream.Stream.range(1);
        Assert.assertThat(range.toString(), CoreMatchers.is(CoreMatchers.equalTo("Cons(1, ?)")));
    }

    /**
     * This test demonstrates the known problem of creating streams from mutable structures.
     *
     * Some of the ways streams created in this way can fail is:
     * - weak stream references getting garbage collected
     * - underlying mutable data structure changes
     * - iterator gets updated (e.g. iterator used to create 2 different streams).
     */
    @Test(expected = ConcurrentModificationException.class)
    public void iterableStreamWithStructureUpdate() {
        java.util.List<Integer> list = List.list(1, 2, 3).toJavaList();
        Stream.Stream<Integer> s1 = Stream.Stream.iterableStream(list);
        int x = s1.head();
        list.remove(1);
        Stream.Stream<Integer> s2 = s1.tail()._1();
        x = s2.head();
    }

    @Test(expected = Error.class)
    public void testCycleNil() {
        cycle(Stream.Stream.nil());
    }

    @Test
    public void testCycle() {
        Stream.Stream<Character> s = stream(new Character[]{ 'a', 'b' });
        Assert.assertThat(cycle(s).take(5), CoreMatchers.is(stream(new Character[]{ 'a', 'b', 'a', 'b', 'a' })));
    }

    @Test
    public void testIterate() {
        Assert.assertThat(iterate(( a) -> (2 * a) + 1, 1).take(5), CoreMatchers.is(stream(new Integer[]{ 1, 3, 7, 15, 31 })));
    }

    @Test
    public void testArrayStreamEmpty() {
        Assert.assertThat(arrayStream(new Integer[]{  }), CoreMatchers.is(Stream.Stream.nil()));
    }

    @Test(expected = Error.class)
    public void testNilHead() {
        Stream.Stream.nil().head();
    }

    @Test(expected = Error.class)
    public void testNilTail() {
        Stream.Stream.nil().tail();
    }

    @Test
    public void testArray() {
        Character[] a = new Character[]{ 'a', 'b', 'c' };
        Stream.Stream<Character> s = stream(a);
        Assert.assertThat(s.array(Character[].class), CoreMatchers.is(a));
    }

    @Test
    public void testZipIndex() {
        Character[] a = new Character[]{ 'a', 'b', 'c' };
        P2<Stream.Stream<Character>, Stream.Stream<Integer>> p = unzip(stream(a).zipIndex().drop(1));
        Assert.assertThat(p._1(), CoreMatchers.is(stream(new Character[]{ 'b', 'c' })));
        Assert.assertThat(p._2(), CoreMatchers.is(stream(new Integer[]{ 1, 2 })));
    }

    @Test
    public void testMinus() {
        Character[] a1 = new Character[]{ 'a', 'b', 'c', 'd', 'e' };
        Stream.Stream<Character> s1 = stream(a1);
        Character[] a2 = new Character[]{ 'b', 'e' };
        Stream.Stream<Character> s2 = stream(a2);
        Assert.assertThat(s1.minus(charEqual, s2), CoreMatchers.is(stream(new Character[]{ 'a', 'c', 'd' })));
    }
}

