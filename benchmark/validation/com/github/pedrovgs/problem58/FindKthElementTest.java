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
package com.github.pedrovgs.problem58;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class FindKthElementTest {
    private FindKthElement findElement;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListNodes() {
        findElement.find(null, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativePositions() {
        findElement.find(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), (-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotAcceptPositionsGreaterThanListSize() {
        findElement.find(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), 2);
    }

    @Test
    public void shouldReturnLastNodeIfPositionIsZero() {
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find(new com.github.pedrovgs.linkedlist.ListNode<Integer>(1), 0);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnFirstElementIfPositionIsEqualsToListSizeMinusOne() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find(list, 2);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnKthToLastElement() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find(list, 1);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(2);
        Assert.assertEquals(expectedNode, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListNodes2() {
        findElement.find2(null, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativePositions2() {
        findElement.find2(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), (-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotAcceptPositionsGreaterThanListSize2() {
        findElement.find2(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), 2);
    }

    @Test
    public void shouldReturnLastNodeIfPositionIsZero2() {
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find2(new com.github.pedrovgs.linkedlist.ListNode<Integer>(1), 0);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnFirstElementIfPositionIsEqualsToListSizeMinusOne2() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find2(list, 2);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnKthToLastElement2() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find2(list, 1);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(2);
        Assert.assertEquals(expectedNode, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListNodes3() {
        findElement.find3(null, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativePositions3() {
        findElement.find3(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), (-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldNotAcceptPositionsGreaterThanListSize3() {
        findElement.find3(new com.github.pedrovgs.linkedlist.ListNode<Integer>(3), 2);
    }

    @Test
    public void shouldReturnLastNodeIfPositionIsZero3() {
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find3(new com.github.pedrovgs.linkedlist.ListNode<Integer>(1), 0);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnFirstElementIfPositionIsEqualsToListSizeMinusOne3() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find3(list, 2);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(1);
        Assert.assertEquals(expectedNode, result);
    }

    @Test
    public void shouldReturnKthToLastElement3() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> list = createList(new int[]{ 1, 2, 3 });
        com.github.pedrovgs.linkedlist.ListNode result = findElement.find3(list, 1);
        com.github.pedrovgs.linkedlist.ListNode<Integer> expectedNode = new com.github.pedrovgs.linkedlist.ListNode<Integer>(2);
        Assert.assertEquals(expectedNode, result);
    }
}

