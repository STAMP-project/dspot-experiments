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
package com.github.pedrovgs.problem61;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class SumListsTest {
    private SumLists sumLists;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsFirstParameterReverse() {
        sumLists.sumReverse(null, new com.github.pedrovgs.linkedlist.ListNode<Integer>(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsSecondParameterReverse() {
        sumLists.sumReverse(new com.github.pedrovgs.linkedlist.ListNode<Integer>(0), null);
    }

    @Test
    public void shouldSumNumbersWithJustOneDigitReverse() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 3 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 8 });
        int result = sumLists.sumReverse(n1, n2);
        Assert.assertEquals(11, result);
    }

    @Test
    public void shouldSumNumbersWithMoreThanOneDigitReverse() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 5, 5, 1 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 4, 1, 3 });
        int result = sumLists.sumReverse(n1, n2);
        Assert.assertEquals(469, result);
    }

    @Test
    public void shouldSumNumbersWithDifferentSizesReverse() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 5, 5, 1 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 5 });
        int result = sumLists.sumReverse(n1, n2);
        Assert.assertEquals(160, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsFirstParameter() {
        sumLists.sum(null, new com.github.pedrovgs.linkedlist.ListNode<Integer>(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsSecondParameter() {
        sumLists.sum(new com.github.pedrovgs.linkedlist.ListNode<Integer>(0), null);
    }

    @Test
    public void shouldSumNumbersWithJustOneDigit() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 3 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 8 });
        int result = sumLists.sum(n1, n2);
        Assert.assertEquals(11, result);
    }

    @Test
    public void shouldSumNumbersWithMoreThanOneDigit() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 1, 5, 5 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 3, 1, 4 });
        int result = sumLists.sum(n1, n2);
        Assert.assertEquals(469, result);
    }

    @Test
    public void shouldSumNumbersWithDifferentSizes() {
        com.github.pedrovgs.linkedlist.ListNode<Integer> n1 = createList(new int[]{ 1, 5, 5 });
        com.github.pedrovgs.linkedlist.ListNode<Integer> n2 = createList(new int[]{ 5 });
        int result = sumLists.sum(n1, n2);
        Assert.assertEquals(160, result);
    }
}

