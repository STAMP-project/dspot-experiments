/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.util;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.devtools.build.lib.util.GroupedList.GroupedListHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link GroupedList}.
 */
@RunWith(JUnit4.class)
public class GroupedListTest {
    @Test
    public void empty() {
        createSizeN(0);
    }

    @Test
    public void sizeOne() {
        createSizeN(1);
    }

    @Test
    public void sizeTwo() {
        createSizeN(2);
    }

    @Test
    public void sizeN() {
        createSizeN(10);
    }

    @Test
    public void elementsNotEqualDifferentOrder() {
        List<String> list = Lists.newArrayList("a", "b", "c");
        Object compressedList = GroupedListTest.createAndCompress(list);
        ArrayList<String> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        assertThat(GroupedListTest.elementsEqual(compressedList, reversed)).isFalse();
    }

    @Test
    public void elementsNotEqualDifferentSizes() {
        for (int size1 = 0; size1 < 10; size1++) {
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < size1; i++) {
                firstList.add(("test" + i));
            }
            Object array = GroupedListTest.createAndCompress(firstList);
            for (int size2 = 0; size2 < 10; size2++) {
                List<String> secondList = new ArrayList<>();
                for (int i = 0; i < size2; i++) {
                    secondList.add(("test" + i));
                }
                assertWithMessage((((((((GroupedList.create(array)) + ", ") + secondList) + ", ") + size1) + ", ") + size2)).that(GroupedListTest.elementsEqual(array, secondList)).isEqualTo((size1 == size2));
            }
        }
    }

    @Test
    public void listWithOneUniqueElementStoredBare() {
        GroupedList<String> groupedListWithDuplicateInGroup = new GroupedList();
        groupedListWithDuplicateInGroup.append(GroupedListHelper.create("a"));
        GroupedListHelper<String> helper = new GroupedListHelper();
        helper.startGroup();
        helper.add("b");
        helper.add("b");
        helper.endGroup();
        groupedListWithDuplicateInGroup.append(helper);
        GroupedList<String> groupedListWithNoDuplicates = new GroupedList();
        groupedListWithNoDuplicates.append(GroupedListHelper.create("a"));
        groupedListWithNoDuplicates.append(GroupedListHelper.create("b"));
        assertThat(groupedListWithNoDuplicates).isEqualTo(groupedListWithDuplicateInGroup);
    }

    @Test
    public void listWithNoNewElementsStoredEmpty() {
        GroupedList<String> groupedListWithEmptyGroup = new GroupedList();
        GroupedListHelper<String> helper = GroupedListHelper.create("a");
        helper.add("a");
        groupedListWithEmptyGroup.append(helper);
        GroupedList<String> groupedListWithNoDuplicates = new GroupedList();
        groupedListWithNoDuplicates.append(GroupedListHelper.create("a"));
        assertThat(groupedListWithNoDuplicates).isEqualTo(groupedListWithEmptyGroup);
    }

    @Test
    public void group() {
        GroupedList<String> groupedList = new GroupedList();
        assertThat(groupedList.isEmpty()).isTrue();
        GroupedListHelper<String> helper = new GroupedListHelper();
        List<ImmutableList<String>> elements = ImmutableList.of(ImmutableList.of("1"), ImmutableList.of("2a", "2b"), ImmutableList.of("3"), ImmutableList.of("4"), ImmutableList.of("5a", "5b", "5c"), ImmutableList.of("6a", "6b", "6c"));
        List<String> allElts = new ArrayList<>();
        for (List<String> group : elements) {
            if ((group.size()) > 1) {
                helper.startGroup();
            }
            for (String elt : group) {
                helper.add(elt);
            }
            if ((group.size()) > 1) {
                helper.endGroup();
            }
            allElts.addAll(group);
        }
        groupedList.append(helper);
        assertThat(groupedList.numElements()).isEqualTo(allElts.size());
        assertThat(groupedList.isEmpty()).isFalse();
        Object compressed = groupedList.compress();
        assertThat(GroupedList.numElements(compressed)).isEqualTo(groupedList.numElements());
        GroupedListTest.assertElementsEqual(compressed, allElts);
        GroupedListTest.assertElementsEqualInGroups(GroupedList.<String>create(compressed), elements);
        GroupedListTest.assertElementsEqualInGroups(groupedList, elements);
        assertThat(groupedList.getAllElementsAsIterable()).containsExactlyElementsIn(Iterables.concat(groupedList)).inOrder();
    }

    @Test
    public void singletonAndEmptyGroups() {
        GroupedList<String> groupedList = new GroupedList();
        assertThat(groupedList.isEmpty()).isTrue();
        GroupedListHelper<String> helper = new GroupedListHelper();
        // varargs
        @SuppressWarnings("unchecked")
        List<ImmutableList<String>> elements = Lists.newArrayList(ImmutableList.of("1"), ImmutableList.<String>of(), ImmutableList.of("2a", "2b"), ImmutableList.of("3"));
        List<String> allElts = new ArrayList<>();
        for (List<String> group : elements) {
            helper.startGroup();// Start a group even if the group has only one element or is empty.

            for (String elt : group) {
                helper.add(elt);
            }
            helper.endGroup();
            allElts.addAll(group);
        }
        groupedList.append(helper);
        assertThat(groupedList.numElements()).isEqualTo(allElts.size());
        assertThat(groupedList.isEmpty()).isFalse();
        Object compressed = groupedList.compress();
        GroupedListTest.assertElementsEqual(compressed, allElts);
        // Get rid of empty list -- it was not stored in groupedList.
        elements.remove(1);
        GroupedListTest.assertElementsEqualInGroups(GroupedList.<String>create(compressed), elements);
        GroupedListTest.assertElementsEqualInGroups(groupedList, elements);
        assertThat(groupedList.getAllElementsAsIterable()).containsExactlyElementsIn(Iterables.concat(groupedList)).inOrder();
    }

    @Test
    public void sizeWithDuplicatesInAndOutOfGroups() {
        GroupedList<String> groupedList = new GroupedList();
        GroupedListHelper<String> helper = new GroupedListHelper();
        helper.add("1");
        helper.startGroup();
        helper.add("1");
        helper.add("2");
        helper.add("3");
        helper.endGroup();
        helper.add("3");
        groupedList.append(helper);
        assertThat(groupedList.numElements()).isEqualTo(3);
        assertThat(groupedList.listSize()).isEqualTo(2);
    }

    @Test
    public void removeMakesEmpty() {
        GroupedList<String> groupedList = new GroupedList();
        assertThat(groupedList.isEmpty()).isTrue();
        GroupedListHelper<String> helper = new GroupedListHelper();
        // varargs
        @SuppressWarnings("unchecked")
        List<List<String>> elements = Lists.newArrayList(((List<String>) (ImmutableList.of("1"))), ImmutableList.<String>of(), Lists.newArrayList("2a", "2b"), ImmutableList.of("3"), ImmutableList.of("removedGroup1", "removedGroup2"), ImmutableList.of("4"));
        List<String> allElts = new ArrayList<>();
        for (List<String> group : elements) {
            helper.startGroup();// Start a group even if the group has only one element or is empty.

            for (String elt : group) {
                helper.add(elt);
            }
            helper.endGroup();
            allElts.addAll(group);
        }
        groupedList.append(helper);
        Set<String> removed = ImmutableSet.of("2a", "3", "removedGroup1", "removedGroup2");
        groupedList.remove(removed);
        Object compressed = groupedList.compress();
        assertThat(GroupedList.numElements(compressed)).isEqualTo(groupedList.numElements());
        allElts.removeAll(removed);
        GroupedListTest.assertElementsEqual(compressed, allElts);
        elements.get(2).remove("2a");
        elements.remove(ImmutableList.of("3"));
        elements.remove(ImmutableList.of());
        elements.remove(ImmutableList.of("removedGroup1", "removedGroup2"));
        GroupedListTest.assertElementsEqualInGroups(GroupedList.<String>create(compressed), elements);
        GroupedListTest.assertElementsEqualInGroups(groupedList, elements);
    }

    @Test
    public void removeGroupFromSmallList() {
        GroupedList<String> groupedList = new GroupedList();
        assertThat(groupedList.isEmpty()).isTrue();
        GroupedListHelper<String> helper = new GroupedListHelper();
        List<List<String>> elements = new ArrayList<>();
        List<String> group = Lists.newArrayList("1a", "1b", "1c", "1d");
        elements.add(group);
        List<String> allElts = new ArrayList<>();
        helper.startGroup();
        for (String item : elements.get(0)) {
            helper.add(item);
        }
        allElts.addAll(group);
        helper.endGroup();
        groupedList.append(helper);
        Set<String> removed = ImmutableSet.of("1b", "1c");
        groupedList.remove(removed);
        Object compressed = groupedList.compress();
        assertThat(GroupedList.numElements(compressed)).isEqualTo(groupedList.numElements());
        allElts.removeAll(removed);
        GroupedListTest.assertElementsEqual(compressed, allElts);
        elements.get(0).removeAll(removed);
        GroupedListTest.assertElementsEqualInGroups(GroupedList.<String>create(compressed), elements);
        GroupedListTest.assertElementsEqualInGroups(groupedList, elements);
    }

    @Test
    public void removeFromListWithDuplicates() {
        GroupedList<String> groupedList = new GroupedList();
        GroupedListHelper<String> helper = new GroupedListHelper();
        helper.startGroup();
        helper.add("a");
        helper.endGroup();
        groupedList.append(helper);
        groupedList.append(helper);
        groupedList.remove(ImmutableSet.of("a"));
        assertThat(groupedList.isEmpty()).isTrue();
    }
}

