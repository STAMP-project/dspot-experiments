package fj.data;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class ZipperTest {
    @Test
    public void testZipper() {
        Zipper<Integer> z = Zipper.zipper(Stream.nil(), 0, Stream.range(1, 9));
        Assert.assertThat(z.map(( i) -> i + 13).toStream(), Is.is(Stream.range(13, 22)));
    }

    @Test
    public void testNext() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
        z = z.next().some();
        Assert.assertThat(z.lefts(), Is.is(Stream.arrayStream(new Integer[]{ 2, 1 })));
        Assert.assertThat(z.focus(), Is.is(3));
        Assert.assertThat(z.rights(), Is.is(Stream.nil()));
    }

    @Test
    public void testNextNone() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.nil());
        Assert.assertThat(z.next().isNone(), Is.is(true));
    }

    @Test
    public void testCycleNext() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
        Assert.assertThat(z.cycleNext(), Is.is(z.next().some()));
    }

    @Test
    public void testCycleNextLast() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.nil());
        z = z.cycleNext();
        Assert.assertThat(z.lefts(), Is.is(Stream.nil()));
        Assert.assertThat(z.focus(), Is.is(1));
        Assert.assertThat(z.rights(), Is.is(Stream.single(2)));
    }

    @Test
    public void testPrevious() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
        z = z.previous().some();
        Assert.assertThat(z.lefts(), Is.is(Stream.nil()));
        Assert.assertThat(z.focus(), Is.is(1));
        Assert.assertThat(z.rights(), Is.is(Stream.arrayStream(new Integer[]{ 2, 3 })));
    }

    @Test
    public void testPreviousNone() {
        Zipper<Integer> z = Zipper.zipper(Stream.nil(), 2, Stream.single(3));
        Assert.assertThat(z.previous().isNone(), Is.is(true));
    }

    @Test
    public void testCyclePrevious() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
        Assert.assertThat(z.cyclePrevious(), Is.is(z.previous().some()));
    }

    @Test
    public void testCyclePreviousFirst() {
        Zipper<Integer> z = Zipper.zipper(Stream.nil(), 1, Stream.single(2));
        z = z.cyclePrevious();
        Assert.assertThat(z.lefts(), Is.is(Stream.single(1)));
        Assert.assertThat(z.focus(), Is.is(2));
        Assert.assertThat(z.rights(), Is.is(Stream.nil()));
    }

    @Test
    public void testInsertLeft() {
        Zipper<Integer> z = Zipper.single(2);
        z = z.insertLeft(1);
        Assert.assertThat(z.lefts(), Is.is(Stream.nil()));
        Assert.assertThat(z.focus(), Is.is(1));
        Assert.assertThat(z.rights(), Is.is(Stream.single(2)));
    }

    @Test
    public void testInsertRight() {
        Zipper<Integer> z = Zipper.single(2);
        z = z.insertRight(3);
        Assert.assertThat(z.lefts(), Is.is(Stream.single(2)));
        Assert.assertThat(z.focus(), Is.is(3));
        Assert.assertThat(z.rights(), Is.is(Stream.nil()));
    }

    @Test
    public void testDeleteOthers() {
        Zipper<Integer> z = Zipper.zipper(Stream.single(1), 2, Stream.single(3));
        z = z.deleteOthers();
        Assert.assertThat(z.lefts(), Is.is(Stream.nil()));
        Assert.assertThat(z.focus(), Is.is(2));
        Assert.assertThat(z.rights(), Is.is(Stream.nil()));
    }

    @Test
    public void testFind() {
        Zipper<Integer> z = Zipper.zipper(Stream.nil(), 0, Stream.range(1));
        z = z.find(( i) -> i == 4).some();
        Assert.assertThat(z.lefts(), Is.is(Stream.arrayStream(new Integer[]{ 3, 2, 1, 0 })));
        Assert.assertThat(z.focus(), Is.is(4));
        Assert.assertThat(z.rights().take(3), Is.is(Stream.range(5, 8)));
    }
}

