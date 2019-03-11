package fj.data;


import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class TreeZipperTest {
    @Test
    public void testDelete() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz = TreeZipper.fromTree(t);
        Assert.assertThat(tz.delete(), Is.is(Option.none()));
    }

    @Test
    public void testDeleteForest() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz = TreeZipper.fromForest(Stream.single(t)).some();
        Assert.assertThat(tz.delete(), Is.is(Option.none()));
    }

    @Test
    public void testHash() {
        final Tree<Integer> t = Tree.node(1, List.single(Tree.leaf(2)));
        final TreeZipper<Integer> tz1 = TreeZipper.fromForest(Stream.single(t)).some();
        final TreeZipper<Integer> tz2 = TreeZipper.fromForest(Stream.single(t)).some();
        Assert.assertThat(tz1.hashCode(), Is.is(tz2.hashCode()));
    }
}

