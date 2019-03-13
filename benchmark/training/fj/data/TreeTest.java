package fj.data;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by MarkPerry on 29/08/2015.
 */
public class TreeTest {
    @Test
    public void emptyLength() {
        Tree<Integer> t = Tree.leaf(1);
        Assert.assertThat(t.length(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shallowStreamLength() {
        Tree<Integer> t2 = Tree.node(3, Stream.stream(Tree.leaf(4), Tree.leaf(5)));
        Assert.assertThat(t2.length(), CoreMatchers.equalTo(3));
    }

    @Test
    public void deepStreamLength() {
        Tree<Integer> t3 = Tree.node(4, Stream.stream(Tree.leaf(5), Tree.node(6, Stream.stream(Tree.leaf(7), Tree.leaf(8)))));
        Assert.assertThat(t3.length(), CoreMatchers.equalTo(5));
    }

    @Test
    public void singleIsLeft() {
        Tree<Integer> t = Tree.leaf(1);
        Assert.assertThat(t.isLeaf(), CoreMatchers.equalTo(true));
    }

    @Test
    public void shallowStreamIsLeaf() {
        Tree<Integer> t2 = Tree.node(3, Stream.stream(Tree.leaf(4), Tree.leaf(5)));
        Assert.assertThat(t2.isLeaf(), CoreMatchers.equalTo(false));
    }

    @Test
    public void deepStreamIsLeaf() {
        Tree<Integer> t3 = Tree.node(4, Stream.stream(Tree.leaf(5), Tree.node(6, Stream.stream(Tree.leaf(7), Tree.leaf(8)))));
        Assert.assertThat(t3.isLeaf(), CoreMatchers.equalTo(false));
    }
}

