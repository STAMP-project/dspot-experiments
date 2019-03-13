/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.duplications.detector.original;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BlocksGroupTest {
    @Test
    public void shouldReturnSize() {
        BlocksGroup group = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1), BlocksGroupTest.newBlock("b", 2));
        Assert.assertThat(group.size(), CoreMatchers.is(2));
    }

    @Test
    public void shouldCreateEmptyGroup() {
        Assert.assertThat(BlocksGroup.empty().size(), CoreMatchers.is(0));
    }

    @Test
    public void testSubsumedBy() {
        BlocksGroup group1 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1), BlocksGroupTest.newBlock("b", 2));
        BlocksGroup group2 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 4));
        // block "c" from group2 does not have corresponding block in group1
        Assert.assertThat(group2.subsumedBy(group1, 1), CoreMatchers.is(false));
    }

    @Test
    public void testSubsumedBy2() {
        BlocksGroup group1 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1), BlocksGroupTest.newBlock("b", 2));
        BlocksGroup group2 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 3));
        BlocksGroup group3 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 4));
        BlocksGroup group4 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 5));
        Assert.assertThat(group2.subsumedBy(group1, 1), CoreMatchers.is(true));// correction of index - 1

        Assert.assertThat(group3.subsumedBy(group1, 2), CoreMatchers.is(true));// correction of index - 2

        Assert.assertThat(group3.subsumedBy(group2, 1), CoreMatchers.is(true));// correction of index - 1

        Assert.assertThat(group4.subsumedBy(group1, 3), CoreMatchers.is(true));// correction of index - 3

        Assert.assertThat(group4.subsumedBy(group2, 2), CoreMatchers.is(true));// correction of index - 2

        Assert.assertThat(group4.subsumedBy(group3, 1), CoreMatchers.is(true));// correction of index - 1

    }

    @Test
    public void testIntersect() {
        BlocksGroup group1 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1), BlocksGroupTest.newBlock("b", 2));
        BlocksGroup group2 = BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 3));
        BlocksGroup intersection = group1.intersect(group2);
        Assert.assertThat(intersection.size(), CoreMatchers.is(2));
    }

    /**
     * Results for this test taken from results of work of naive implementation.
     */
    @Test
    public void testSubsumedBy3() {
        // ['a'[2|2-7]:3, 'b'[0|0-5]:3] subsumedBy ['a'[1|1-6]:2] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 0)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1)), 1), CoreMatchers.is(false));
        // ['a'[3|3-8]:4, 'b'[1|1-6]:4] subsumedBy ['a'[1|1-6]:2] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1)), 1), CoreMatchers.is(false));
        // ['a'[4|4-9]:5, 'b'[2|2-7]:5] subsumedBy ['a'[1|1-6]:2] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1)), 1), CoreMatchers.is(false));
        // ['a'[5|5-10]:6, 'b'[3|3-8]:6] subsumedBy ['a'[1|1-6]:2] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 1)), 1), CoreMatchers.is(false));
        // ['a'[3|3-8]:4, 'b'[1|1-6]:4] subsumedBy ['a'[2|2-7]:3, 'b'[0|0-5]:3] true
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 0)), 1), CoreMatchers.is(true));
        // ['a'[4|4-9]:5, 'b'[2|2-7]:5, 'c'[0|0-5]:5] subsumedBy ['a'[3|3-8]:4, 'b'[1|1-6]:4] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2), BlocksGroupTest.newBlock("c", 0)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)), 1), CoreMatchers.is(false));
        // ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6] subsumedBy ['a'[3|3-8]:4, 'b'[1|1-6]:4] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)), 1), CoreMatchers.is(false));
        // ['a'[6|6-11]:7, 'c'[2|2-7]:7] subsumedBy ['a'[3|3-8]:4, 'b'[1|1-6]:4] false
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 6), BlocksGroupTest.newBlock("c", 2)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)), 1), CoreMatchers.is(false));
        // ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6] subsumedBy ['a'[4|4-9]:5, 'b'[2|2-7]:5, 'c'[0|0-5]:5] true
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2), BlocksGroupTest.newBlock("c", 0)), 1), CoreMatchers.is(true));
        // ['a'[6|6-11]:7, 'c'[2|2-7]:7] subsumedBy ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6] true
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 6), BlocksGroupTest.newBlock("c", 2)).subsumedBy(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1)), 1), CoreMatchers.is(true));
    }

    /**
     * Results for this test taken from results of work of naive implementation.
     */
    @Test
    public void testIntersect2() {
        // ['a'[2|2-7]:3, 'b'[0|0-5]:3]
        // intersect ['a'[3|3-8]:4, 'b'[1|1-6]:4]
        // as ['a'[3|3-8]:4, 'b'[1|1-6]:4]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 2), BlocksGroupTest.newBlock("b", 0)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1))).size(), CoreMatchers.is(2));
        // ['a'[3|3-8]:4, 'b'[1|1-6]:4]
        // intersect ['a'[4|4-9]:5, 'b'[2|2-7]:5, 'c'[0|0-5]:5]
        // as ['a'[4|4-9]:5, 'b'[2|2-7]:5]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 3), BlocksGroupTest.newBlock("b", 1)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2), BlocksGroupTest.newBlock("c", 0))).size(), CoreMatchers.is(2));
        // ['a'[4|4-9]:5, 'b'[2|2-7]:5]
        // intersect ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6]
        // as ['a'[5|5-10]:6, 'b'[3|3-8]:6]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1))).size(), CoreMatchers.is(2));
        // ['a'[5|5-10]:6, 'b'[3|3-8]:6]
        // intersect ['a'[6|6-11]:7, 'c'[2|2-7]:7]
        // as ['a'[6|6-11]:7]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 6), BlocksGroupTest.newBlock("c", 2))).size(), CoreMatchers.is(1));
        // ['a'[4|4-9]:5, 'b'[2|2-7]:5, 'c'[0|0-5]:5]
        // intersect ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6]
        // as ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 4), BlocksGroupTest.newBlock("b", 2), BlocksGroupTest.newBlock("c", 0)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1))).size(), CoreMatchers.is(3));
        // ['a'[5|5-10]:6, 'b'[3|3-8]:6, 'c'[1|1-6]:6]
        // intersect ['a'[6|6-11]:7, 'c'[2|2-7]:7]
        // as ['a'[6|6-11]:7, 'c'[2|2-7]:7]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 5), BlocksGroupTest.newBlock("b", 3), BlocksGroupTest.newBlock("c", 1)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 6), BlocksGroupTest.newBlock("c", 2))).size(), CoreMatchers.is(2));
        // ['a'[6|6-11]:7, 'c'[2|2-7]:7]
        // intersect ['a'[7|7-12]:8]
        // as ['a'[7|7-12]:8]
        Assert.assertThat(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 6), BlocksGroupTest.newBlock("c", 7)).intersect(BlocksGroupTest.newBlocksGroup(BlocksGroupTest.newBlock("a", 7))).size(), CoreMatchers.is(1));
    }
}

