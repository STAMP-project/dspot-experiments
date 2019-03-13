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
package com.github.pedrovgs.problem62;


import com.github.pedrovgs.linkedlist.ListNode;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class PalindromeListTest {
    private PalindromeList palindromeList;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsInputReversing() {
        palindromeList.checkReversing(null);
    }

    @Test
    public void shouldReturnTrueIfListContainsJustOneNodeReversing() {
        ListNode<Integer> list = new ListNode<Integer>(1);
        boolean result = palindromeList.checkReversing(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldRturnTrueIfListContainsTwoElementsWithTheSameValueReversing() {
        ListNode<Integer> list = createList(new int[]{ 1, 1 });
        boolean result = palindromeList.checkReversing(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueIfListIsPalindromeReversing() {
        ListNode<Integer> list = createList(new int[]{ 1, 2, 1 });
        boolean result = palindromeList.checkReversing(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfListIsNotPalindromeReversing() {
        ListNode<Integer> list = createList(new int[]{ 1, 1, 3 });
        boolean result = palindromeList.checkReversing(list);
        Assert.assertFalse(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullListAsInputIterative() {
        palindromeList.checkIterative(null);
    }

    @Test
    public void shouldReturnTrueIfListContainsJustOneNodeIterative() {
        ListNode<Integer> list = new ListNode<Integer>(1);
        boolean result = palindromeList.checkIterative(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldRturnTrueIfListContainsTwoElementsWithTheSameValueIterative() {
        ListNode<Integer> list = createList(new int[]{ 1, 1 });
        boolean result = palindromeList.checkIterative(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnTrueIfListIsPalindromeIterative() {
        ListNode<Integer> list = createList(new int[]{ 1, 2, 1 });
        boolean result = palindromeList.checkIterative(list);
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseIfListIsNotPalindromeIterative() {
        ListNode<Integer> list = createList(new int[]{ 1, 1, 3 });
        boolean result = palindromeList.checkIterative(list);
        Assert.assertFalse(result);
    }
}

