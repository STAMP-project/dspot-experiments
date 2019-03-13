/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pedrovgs.problem19;


import com.github.pedrovgs.binarytree.BinaryNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class BinaryTreeDepthTest {
    private BinaryTreeDepth binaryTreeDepth;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullBinaryNodes() {
        binaryTreeDepth.get(null);
    }

    @Test
    public void shouldReturn1IfTreeContainsJustOneElement() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        int depth = binaryTreeDepth.get(root);
        Assert.assertEquals(1, depth);
    }

    @Test
    public void shouldCalculateBinaryTreeDepthEvenIfTreeContainsJustOneBigLeftBranch() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        root.setLeft(n1);
        n1.setLeft(n2);
        int depth = binaryTreeDepth.get(root);
        Assert.assertEquals(3, depth);
    }

    @Test
    public void shouldCalculateBinaryTreeDepthEvenIfTreeContainsJustOneBigRightBranch() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        root.setRight(n1);
        n1.setRight(n2);
        int depth = binaryTreeDepth.get(root);
        Assert.assertEquals(3, depth);
    }

    @Test
    public void shouldCalculateBinaryTreeDepthWithANormalBinaryTree() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        BinaryNode<Integer> n3 = new BinaryNode<Integer>(3);
        BinaryNode<Integer> n4 = new BinaryNode<Integer>(4);
        BinaryNode<Integer> n5 = new BinaryNode<Integer>(5);
        root.setLeft(n1);
        root.setRight(n2);
        n1.setLeft(n3);
        n1.setRight(n4);
        n4.setLeft(n5);
        int depth = binaryTreeDepth.get(root);
        Assert.assertEquals(4, depth);
    }
}

