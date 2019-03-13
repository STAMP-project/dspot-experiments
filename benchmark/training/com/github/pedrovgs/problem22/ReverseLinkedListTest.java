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
package com.github.pedrovgs.problem22;


import com.github.pedrovgs.linkedlist.ListNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class ReverseLinkedListTest {
    private ReverseLinkedList reverseLinkedList;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullElementsIterative() {
        reverseLinkedList.reverseIterative(null);
    }

    @Test
    public void shouldAcceptLinkedListWithJustOneElementIterative() {
        ListNode<Integer> head = new ListNode<Integer>(1);
        Assert.assertEquals(head, reverseLinkedList.reverseIterative(head));
    }

    @Test
    public void shouldReverseLinkedListIterative() {
        ListNode<Integer> head = new ListNode<Integer>(1);
        ListNode<Integer> n2 = new ListNode<Integer>(2);
        ListNode<Integer> n3 = new ListNode<Integer>(3);
        ListNode<Integer> n4 = new ListNode<Integer>(4);
        head.setNext(n2);
        n2.setNext(n3);
        n3.setNext(n4);
        ListNode reversedList = reverseLinkedList.reverseIterative(head);
        Assert.assertEquals(n4, reversedList);
        Assert.assertEquals(n3, reversedList.getNext());
        Assert.assertEquals(n2, reversedList.getNext().getNext());
        Assert.assertEquals(head, reversedList.getNext().getNext().getNext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullElementsRecursive() {
        reverseLinkedList.reverseRecursive(null);
    }

    @Test
    public void shouldAcceptLinkedListWithJustOneElementRecursive() {
        ListNode<Integer> head = new ListNode<Integer>(1);
        Assert.assertEquals(head, reverseLinkedList.reverseRecursive(head));
    }

    @Test
    public void shouldReverseLinkedListRecursive() {
        ListNode<Integer> head = new ListNode<Integer>(1);
        ListNode<Integer> n2 = new ListNode<Integer>(2);
        ListNode<Integer> n3 = new ListNode<Integer>(3);
        ListNode<Integer> n4 = new ListNode<Integer>(4);
        head.setNext(n2);
        n2.setNext(n3);
        n3.setNext(n4);
        ListNode reversedList = reverseLinkedList.reverseRecursive(head);
        Assert.assertEquals(n4, reversedList);
        Assert.assertEquals(n3, reversedList.getNext());
        Assert.assertEquals(n2, reversedList.getNext().getNext());
        Assert.assertEquals(head, reversedList.getNext().getNext().getNext());
    }
}

