package com.github.pedrovgs.problem18;


import com.github.pedrovgs.binarytree.BinaryNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class IsBSTTest {
    private IsBST isBST;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullBinaryTreesRecursive() {
        isBST.checkRecursive(null);
    }

    @Test
    public void shouldReturnTrueIfTheTreeContainsJustOneNodeRecursive() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        Assert.assertTrue(isBST.checkRecursive(root));
    }

    @Test
    public void shouldReturnFalseIfLeftNodesAreGraterThanRightNodesRecursive() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        root.setLeft(n2);
        root.setRight(n1);
        Assert.assertFalse(isBST.checkRecursive(root));
    }

    @Test
    public void shouldReturnTrueIfBinaryTreeIsBSTRecursive() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        n1.setLeft(root);
        n1.setRight(n2);
        Assert.assertTrue(isBST.checkRecursive(n1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullBinaryTreesIterative() {
        isBST.checkIterative(null);
    }

    @Test
    public void shouldReturnTrueIfTheTreeContainsJustOneNodeIterative() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        Assert.assertTrue(isBST.checkIterative(root));
    }

    @Test
    public void shouldReturnFalseIfLeftNodesAreGraterThanRightNodesIterative() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        root.setLeft(n2);
        root.setRight(n1);
        Assert.assertFalse(isBST.checkIterative(root));
    }

    @Test
    public void shouldReturnTrueIfBinaryTreeIsBSTIterative() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        n1.setLeft(root);
        n1.setRight(n2);
        Assert.assertTrue(isBST.checkIterative(n1));
    }
}

