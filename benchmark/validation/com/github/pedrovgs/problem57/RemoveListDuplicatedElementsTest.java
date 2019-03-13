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
package com.github.pedrovgs.problem57;


import com.github.pedrovgs.linkedlist.ListNode;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class RemoveListDuplicatedElementsTest {
    private RemoveListDuplicatedElements removeDuplicates;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstancesAsParameter() {
        removeDuplicates.remove(null);
    }

    @Test
    public void shouldNotModifyListIfThereAreNotDuplicatedElements() {
        ListNode<Integer> head = createList(new Integer[]{ 0, 1, 2, 3, 4 });
        removeDuplicates.remove(head);
        assertListContainsElements(new Integer[]{ 0, 1, 2, 3, 4 }, head);
    }

    @Test
    public void shouldRemoveDuplicatedElements() {
        ListNode<Integer> head = createList(new Integer[]{ 0, 1, 0, 2, 2, 3, 4, 4 });
        removeDuplicates.remove(head);
        assertListContainsElements(new Integer[]{ 0, 1, 2, 3, 4 }, head);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullInstancesAsParameter2() {
        removeDuplicates.remove2(null);
    }

    @Test
    public void shouldNotModifyListIfThereAreNotDuplicatedElements2() {
        ListNode<Integer> head = createList(new Integer[]{ 0, 1, 2, 3, 4 });
        removeDuplicates.remove2(head);
        assertListContainsElements(new Integer[]{ 0, 1, 2, 3, 4 }, head);
    }

    @Test
    public void shouldRemoveDuplicatedElements2() {
        ListNode<Integer> head = createList(new Integer[]{ 0, 1, 0, 2, 2, 3, 4, 4 });
        removeDuplicates.remove2(head);
        assertListContainsElements(new Integer[]{ 0, 1, 2, 3, 4 }, head);
    }
}

