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
package com.github.pedrovgs.problem65;


import com.github.pedrovgs.binarytree.BinaryNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class IsTreeBalancedTest {
    private IsTreeBalanced isTreeBalanced;

    @Test
    public void shouldReturnTrueIfTreeIsNull() {
        boolean isBalanced = isTreeBalanced.check(null);
        Assert.assertTrue(isBalanced);
    }

    @Test
    public void shouldReturnFalseIfTreeIsNotBalanced() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        BinaryNode<Integer> n3 = new BinaryNode<Integer>(3);
        BinaryNode<Integer> n4 = new BinaryNode<Integer>(4);
        root.setLeft(n1);
        root.setRight(n2);
        n1.setLeft(n3);
        n3.setLeft(n4);
        boolean isBalanced = isTreeBalanced.check(root);
        Assert.assertFalse(isBalanced);
    }

    @Test
    public void shouldReturnTrueIfTreeIsBalanced() {
        BinaryNode<Integer> root = new BinaryNode<Integer>(0);
        BinaryNode<Integer> n1 = new BinaryNode<Integer>(1);
        BinaryNode<Integer> n2 = new BinaryNode<Integer>(2);
        BinaryNode<Integer> n3 = new BinaryNode<Integer>(3);
        BinaryNode<Integer> n4 = new BinaryNode<Integer>(4);
        root.setLeft(n1);
        root.setRight(n2);
        n1.setLeft(n3);
        n2.setRight(n4);
        boolean isBalanced = isTreeBalanced.check(root);
        Assert.assertTrue(isBalanced);
    }
}

