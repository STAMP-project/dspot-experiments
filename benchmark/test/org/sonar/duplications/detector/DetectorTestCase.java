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
package org.sonar.duplications.detector;


import Block.Builder;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.duplications.block.Block;
import org.sonar.duplications.block.ByteArray;
import org.sonar.duplications.index.CloneGroup;
import org.sonar.duplications.index.CloneIndex;
import org.sonar.duplications.index.ClonePart;
import org.sonar.duplications.junit.TestNamePrinter;


public abstract class DetectorTestCase {
    @Rule
    public TestNamePrinter testNamePrinter = new TestNamePrinter();

    protected static int LINES_PER_BLOCK = 5;

    /**
     * Given:
     * <pre>
     * y:   2 3 4 5
     * z:     3 4
     * x: 1 2 3 4 5 6
     * </pre>
     * Expected:
     * <pre>
     * x-y (2 3 4 5)
     * x-y-z (3 4)
     * </pre>
     */
    @Test
    public void exampleFromPaper() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("y", "2 3 4 5"), DetectorTestCase.newBlocks("z", "3 4"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("x", "1 2 3 4 5 6");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertEquals(2, result.size());
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(4, DetectorTestCase.newClonePart("x", 1, 4), DetectorTestCase.newClonePart("y", 0, 4)));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("x", 2, 2), DetectorTestCase.newClonePart("y", 1, 2), DetectorTestCase.newClonePart("z", 0, 2)));
    }

    /**
     * Given:
     * <pre>
     * a:   2 3 4 5
     * b:     3 4
     * c: 1 2 3 4 5 6
     * </pre>
     * Expected:
     * <pre>
     * c-a (2 3 4 5)
     * c-a-b (3 4)
     * </pre>
     */
    @Test
    public void exampleFromPaperWithModifiedResourceIds() {
        CloneIndex cloneIndex = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("a", "2 3 4 5"), DetectorTestCase.newBlocks("b", "3 4"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("c", "1 2 3 4 5 6");
        List<CloneGroup> clones = detect(cloneIndex, fileBlocks);
        DetectorTestCase.print(clones);
        Assert.assertThat(clones.size(), CoreMatchers.is(2));
        Assert.assertThat(clones, CloneGroupMatcher.hasCloneGroup(4, DetectorTestCase.newClonePart("c", 1, 4), DetectorTestCase.newClonePart("a", 0, 4)));
        Assert.assertThat(clones, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("c", 2, 2), DetectorTestCase.newClonePart("a", 1, 2), DetectorTestCase.newClonePart("b", 0, 2)));
    }

    /**
     * Given:
     * <pre>
     * b:     3 4 5 6
     * c:         5 6 7
     * a: 1 2 3 4 5 6 7 8 9
     * </pre>
     * Expected:
     * <pre>
     * a-b (3 4 5 6)
     * a-b-c (5 6)
     * a-c (5 6 7)
     * </pre>
     */
    @Test
    public void example1() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("b", "3 4 5 6"), DetectorTestCase.newBlocks("c", "5 6 7"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 3 4 5 6 7 8 9");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(3));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(4, DetectorTestCase.newClonePart("a", 2, 4), DetectorTestCase.newClonePart("b", 0, 4)));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(3, DetectorTestCase.newClonePart("a", 4, 3), DetectorTestCase.newClonePart("c", 0, 3)));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("a", 4, 2), DetectorTestCase.newClonePart("b", 2, 2), DetectorTestCase.newClonePart("c", 0, 2)));
    }

    /**
     * Given:
     * <pre>
     * b: 1 2 3 4 1 2 3 4 1 2 3 4
     * c: 1 2 3 4
     * a: 1 2 3 5
     * </pre>
     * Expected:
     * <pre>
     * a-b-b-b-c (1 2 3)
     * </pre>
     */
    @Test
    public void example2() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("b", "1 2 3 4 1 2 3 4 1 2 3 4"), DetectorTestCase.newBlocks("c", "1 2 3 4"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 3 5");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(3, DetectorTestCase.newClonePart("a", 0, 3), DetectorTestCase.newClonePart("b", 0, 3), DetectorTestCase.newClonePart("b", 4, 3), DetectorTestCase.newClonePart("b", 8, 3), DetectorTestCase.newClonePart("c", 0, 3)));
    }

    /**
     * Test for problem, which was described in original paper - same clone would be reported twice.
     * Given:
     * <pre>
     * a: 1 2 3 1 2 4
     * </pre>
     * Expected only one clone:
     * <pre>
     * a-a (1 2)
     * </pre>
     */
    @Test
    public void clonesInFileItself() {
        CloneIndex index = DetectorTestCase.createIndex();
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 3 1 2 4");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("a", 0, 2), DetectorTestCase.newClonePart("a", 3, 2)));
    }

    /**
     * Given:
     * <pre>
     * b: 1 2 1 2
     * a: 1 2 1
     * </pre>
     * Expected:
     * <pre>
     * a-b-b (1 2)
     * a-b (1 2 1)
     * </pre>
     * "a-a-b-b (1)" should not be reported, because fully covered by "a-b (1 2 1)"
     */
    @Test
    public void covered() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("b", "1 2 1 2"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 1");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(2));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(3, DetectorTestCase.newClonePart("a", 0, 3), DetectorTestCase.newClonePart("b", 0, 3)));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("a", 0, 2), DetectorTestCase.newClonePart("b", 0, 2), DetectorTestCase.newClonePart("b", 2, 2)));
    }

    /**
     * Given:
     * <pre>
     * b: 1 2 1 2 1 2 1
     * a: 1 2 1 2 1 2
     * </pre>
     * Expected:
     * <pre>
     * a-b-b (1 2 1 2 1) - note that there is overlapping among parts for "b"
     * a-b (1 2 1 2 1 2)
     * </pre>
     */
    @Test
    public void problemWithNestedCloneGroups() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("b", "1 2 1 2 1 2 1"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 1 2 1 2");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(2));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(6, DetectorTestCase.newClonePart("a", 0, 6), DetectorTestCase.newClonePart("b", 0, 6)));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(5, DetectorTestCase.newClonePart("a", 0, 5), DetectorTestCase.newClonePart("b", 0, 5), DetectorTestCase.newClonePart("b", 2, 5)));
    }

    /**
     * Given:
     * <pre>
     * a: 1 2 3
     * b: 1 2 4
     * a: 1 2 5
     * </pre>
     * Expected:
     * <pre>
     * a-b (1 2) - instead of "a-a-b", which will be the case if file from index not ignored
     * </pre>
     */
    @Test
    public void fileAlreadyInIndex() {
        CloneIndex index = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("a", "1 2 3"), DetectorTestCase.newBlocks("b", "1 2 4"));
        // Note about blocks with hashes "3", "4" and "5": those blocks here in order to not face another problem - with EOF (see separate test)
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 5");
        List<CloneGroup> result = detect(index, fileBlocks);
        DetectorTestCase.print(result);
        Assert.assertThat(result.size(), CoreMatchers.is(1));
        Assert.assertThat(result, CloneGroupMatcher.hasCloneGroup(2, DetectorTestCase.newClonePart("a", 0, 2), DetectorTestCase.newClonePart("b", 0, 2)));
    }

    /**
     * Given: file with repeated hashes
     * Expected: only one query of index for each unique hash
     */
    @Test
    public void only_one_query_of_index_for_each_unique_hash() {
        CloneIndex index = Mockito.spy(DetectorTestCase.createIndex());
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 1 2");
        detect(index, fileBlocks);
        Mockito.verify(index).getBySequenceHash(new ByteArray("01"));
        Mockito.verify(index).getBySequenceHash(new ByteArray("02"));
        Mockito.verifyNoMoreInteractions(index);
    }

    /**
     * Given: empty list of blocks for file
     * Expected: {@link Collections#EMPTY_LIST}
     */
    @Test
    public void shouldReturnEmptyListWhenNoBlocksForFile() {
        List<CloneGroup> result = detect(null, new Block[0]);
        Assert.assertThat(result, CoreMatchers.sameInstance(Collections.EMPTY_LIST));
    }

    /**
     * Given:
     * <pre>
     * b: 1 2 3 4
     * a: 1 2 3
     * </pre>
     * Expected clone which ends at the end of file "a":
     * <pre>
     * a-b (1 2 3)
     * </pre>
     */
    @Test
    public void problemWithEndOfFile() {
        CloneIndex cloneIndex = DetectorTestCase.createIndex(DetectorTestCase.newBlocks("b", "1 2 3 4"));
        Block[] fileBlocks = DetectorTestCase.newBlocks("a", "1 2 3");
        List<CloneGroup> clones = detect(cloneIndex, fileBlocks);
        DetectorTestCase.print(clones);
        Assert.assertThat(clones.size(), CoreMatchers.is(1));
        Assert.assertThat(clones, CloneGroupMatcher.hasCloneGroup(3, DetectorTestCase.newClonePart("a", 0, 3), DetectorTestCase.newClonePart("b", 0, 3)));
    }

    /**
     * Given file with two lines, containing following statements:
     * <pre>
     * 0: A,B,A,B
     * 1: A,B,A
     * </pre>
     * with block size 5 each block will span both lines, and hashes will be:
     * <pre>
     * A,B,A,B,A=1
     * B,A,B,A,B=2
     * A,B,A,B,A=1
     * </pre>
     * Expected: one clone with two parts, which contain exactly the same lines
     */
    @Test
    public void same_lines_but_different_indexes() {
        CloneIndex cloneIndex = DetectorTestCase.createIndex();
        Block.Builder block = Block.builder().setResourceId("a").setLines(0, 1);
        Block[] fileBlocks = new Block[]{ block.setBlockHash(new ByteArray("1".getBytes())).setIndexInFile(0).build(), block.setBlockHash(new ByteArray("2".getBytes())).setIndexInFile(1).build(), block.setBlockHash(new ByteArray("1".getBytes())).setIndexInFile(2).build() };
        List<CloneGroup> clones = detect(cloneIndex, fileBlocks);
        DetectorTestCase.print(clones);
        Assert.assertThat(clones.size(), CoreMatchers.is(1));
        Iterator<CloneGroup> clonesIterator = clones.iterator();
        CloneGroup clone = clonesIterator.next();
        Assert.assertThat(clone.getCloneUnitLength(), CoreMatchers.is(1));
        Assert.assertThat(clone.getCloneParts().size(), CoreMatchers.is(2));
        Assert.assertThat(clone.getOriginPart(), CoreMatchers.is(new ClonePart("a", 0, 0, 1)));
        Assert.assertThat(clone.getCloneParts(), CoreMatchers.hasItem(new ClonePart("a", 0, 0, 1)));
        Assert.assertThat(clone.getCloneParts(), CoreMatchers.hasItem(new ClonePart("a", 2, 0, 1)));
    }
}

