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
package com.github.pedrovgs.problem24;


import com.github.pedrovgs.binarytree.BinaryNode;
import com.github.pedrovgs.problem15.BinaryTreeInOrder;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class SortedArrayToBSTTest {
    private SortedArrayToBST sortedArrayToBST;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArrays() {
        sortedArrayToBST.transform(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptAnEmptyArray() {
        Integer[] emptyArray = new Integer[0];
        sortedArrayToBST.transform(emptyArray);
    }

    @Test
    public void shouldReturnJustOneNodeIfTheArrayContainsJustOneElement() {
        Integer[] array = new Integer[]{ 1 };
        BinaryNode<Integer> result = sortedArrayToBST.transform(array);
        Assert.assertEquals(new Integer(1), result.getData());
    }

    /**
     * If you get an in order traversal of a BST you get a sorted collection of elements. We are
     * going to use this property to assert the result.
     */
    @Test
    public void shouldReturnOneBinarySearchTree() {
        Integer[] array = new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        BinaryNode<Integer> result = sortedArrayToBST.transform(array);
        BinaryTreeInOrder inOrder = new BinaryTreeInOrder();
        List<BinaryNode<Integer>> resultList = inOrder.getIterative(result);
        Integer[] resultArray = new Integer[resultList.size()];
        for (int i = 0; i < (resultList.size()); i++) {
            resultArray[i] = resultList.get(i).getData();
        }
        Assert.assertArrayEquals(array, resultArray);
    }
}

