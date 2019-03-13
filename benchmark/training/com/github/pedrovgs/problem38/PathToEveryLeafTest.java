package com.github.pedrovgs.problem38;


import com.github.pedrovgs.binarytree.BinaryNode;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class PathToEveryLeafTest {
    private PathToEveryLeaf pathToEveryLeaf;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTrees() {
        pathToEveryLeaf.calculate(null);
    }

    @Test
    public void shouldReturnOneElementWithJustOneNodeIfTheTreeContainsJustOneElement() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(1);
        List<List<BinaryNode>> paths = pathToEveryLeaf.calculate(root);
        Assert.assertEquals(1, paths.size());
        Assert.assertEquals(1, paths.get(0).get(0).getData());
    }

    @Test
    public void shouldReturnTwoListsIfTheTreeContainsTwoLeafs() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        BinaryNode<Integer> n3 = new BinaryNode<Integer>(3);
        root.setLeft(n2);
        root.setRight(n3);
        List<List<BinaryNode>> paths = pathToEveryLeaf.calculate(root);
        Assert.assertEquals(2, paths.size());
        assertPathTo(paths.get(0), root, n2);
        assertPathTo(paths.get(1), root, n3);
    }

    @Test
    public void shouldCalculatePathsToEveryLeaf() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        BinaryNode<Integer> n3 = new BinaryNode<Integer>(3);
        BinaryNode<Integer> n4 = new BinaryNode<Integer>(4);
        BinaryNode<Integer> n5 = new BinaryNode<Integer>(5);
        root.setLeft(n2);
        root.setRight(n3);
        n3.setLeft(n4);
        n3.setRight(n5);
        List<List<BinaryNode>> paths = pathToEveryLeaf.calculate(root);
        Assert.assertEquals(3, paths.size());
        assertPathTo(paths.get(0), root, n2);
        assertPathTo(paths.get(1), root, n3, n4);
        assertPathTo(paths.get(2), root, n3, n5);
    }
}

