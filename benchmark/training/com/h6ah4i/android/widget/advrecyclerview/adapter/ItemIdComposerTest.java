/**
 * Copyright (C) 2016 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.h6ah4i.android.widget.advrecyclerview.adapter;


import ItemIdComposer.BIT_MASK_CHILD_ID;
import ItemIdComposer.BIT_MASK_GROUP_ID;
import ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG;
import ItemIdComposer.BIT_MASK_SEGMENT;
import ItemIdComposer.BIT_OFFSET_CHILD_ID;
import ItemIdComposer.BIT_OFFSET_GROUP_ID;
import ItemIdComposer.BIT_OFFSET_RESERVED_SIGN_FLAG;
import ItemIdComposer.BIT_OFFSET_SEGMENT;
import ItemIdComposer.BIT_WIDTH_CHILD_ID;
import ItemIdComposer.BIT_WIDTH_GROUP_ID;
import ItemIdComposer.BIT_WIDTH_RESERVED_SIGN_FLAG;
import ItemIdComposer.BIT_WIDTH_SEGMENT;
import ItemIdComposer.MAX_CHILD_ID;
import ItemIdComposer.MAX_GROUP_ID;
import ItemIdComposer.MAX_SEGMENT;
import ItemIdComposer.MAX_WRAPPED_ID;
import ItemIdComposer.MIN_CHILD_ID;
import ItemIdComposer.MIN_GROUP_ID;
import ItemIdComposer.MIN_SEGMENT;
import ItemIdComposer.MIN_WRAPPED_ID;
import RecyclerView.NO_ID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ItemIdComposer.BIT_MASK_CHILD_ID;
import static ItemIdComposer.BIT_MASK_GROUP_ID;
import static ItemIdComposer.BIT_MASK_RESERVED_SIGN_FLAG;
import static ItemIdComposer.BIT_MASK_SEGMENT;
import static ItemIdComposer.BIT_OFFSET_CHILD_ID;
import static ItemIdComposer.BIT_OFFSET_GROUP_ID;
import static ItemIdComposer.BIT_OFFSET_RESERVED_SIGN_FLAG;
import static ItemIdComposer.BIT_OFFSET_SEGMENT;
import static ItemIdComposer.BIT_WIDTH_CHILD_ID;
import static ItemIdComposer.BIT_WIDTH_GROUP_ID;
import static ItemIdComposer.BIT_WIDTH_RESERVED_SIGN_FLAG;
import static ItemIdComposer.BIT_WIDTH_SEGMENT;
import static ItemIdComposer.MAX_CHILD_ID;
import static ItemIdComposer.MAX_GROUP_ID;
import static ItemIdComposer.MAX_SEGMENT;
import static ItemIdComposer.MIN_CHILD_ID;
import static ItemIdComposer.MIN_GROUP_ID;
import static ItemIdComposer.MIN_SEGMENT;


public class ItemIdComposerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void totalBitWidth() {
        Assert.assertThat(((((BIT_WIDTH_RESERVED_SIGN_FLAG) + (BIT_WIDTH_SEGMENT)) + (BIT_WIDTH_GROUP_ID)) + (BIT_WIDTH_CHILD_ID)), CoreMatchers.is(64));
    }

    @Test
    public void bitOffsets() {
        Assert.assertThat(BIT_OFFSET_CHILD_ID, CoreMatchers.is(0));
        Assert.assertThat(BIT_OFFSET_GROUP_ID, CoreMatchers.is(((BIT_WIDTH_CHILD_ID) + (BIT_OFFSET_CHILD_ID))));
        Assert.assertThat(BIT_OFFSET_SEGMENT, CoreMatchers.is(((BIT_WIDTH_GROUP_ID) + (BIT_OFFSET_GROUP_ID))));
        Assert.assertThat(BIT_OFFSET_RESERVED_SIGN_FLAG, CoreMatchers.is(((BIT_WIDTH_SEGMENT) + (BIT_OFFSET_SEGMENT))));
        Assert.assertThat(((BIT_OFFSET_RESERVED_SIGN_FLAG) + (BIT_WIDTH_RESERVED_SIGN_FLAG)), CoreMatchers.is(64));
    }

    @Test
    public void bitMasks() {
        Assert.assertThat(BIT_MASK_RESERVED_SIGN_FLAG, CoreMatchers.is(ItemIdComposerTest.genBitMask(BIT_WIDTH_RESERVED_SIGN_FLAG, BIT_OFFSET_RESERVED_SIGN_FLAG)));
        Assert.assertThat(BIT_MASK_SEGMENT, CoreMatchers.is(ItemIdComposerTest.genBitMask(BIT_WIDTH_SEGMENT, BIT_OFFSET_SEGMENT)));
        Assert.assertThat(BIT_MASK_GROUP_ID, CoreMatchers.is(ItemIdComposerTest.genBitMask(BIT_WIDTH_GROUP_ID, BIT_OFFSET_GROUP_ID)));
        Assert.assertThat(BIT_MASK_CHILD_ID, CoreMatchers.is(ItemIdComposerTest.genBitMask(BIT_WIDTH_CHILD_ID, BIT_OFFSET_CHILD_ID)));
    }

    @Test
    public void bitWidthSegment() {
        Assert.assertThat(BIT_WIDTH_SEGMENT, CoreMatchers.is(ItemViewTypeComposer.BIT_WIDTH_SEGMENT));
    }

    @Test
    public void minMaxSegment() {
        Assert.assertThat(MIN_SEGMENT, CoreMatchers.is(0));
        Assert.assertThat(MAX_SEGMENT, CoreMatchers.is(ItemIdComposerTest.unsignedIntMax(BIT_WIDTH_SEGMENT)));
    }

    @Test
    public void minMaxGroupId() {
        Assert.assertThat(MIN_GROUP_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMin(BIT_WIDTH_GROUP_ID)));
        Assert.assertThat(MAX_GROUP_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMax(BIT_WIDTH_GROUP_ID)));
    }

    @Test
    public void minMaxChildId() {
        Assert.assertThat(MIN_CHILD_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMin(BIT_WIDTH_CHILD_ID)));
        Assert.assertThat(MAX_CHILD_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMax(BIT_WIDTH_CHILD_ID)));
    }

    @Test
    public void minMaxWrappedId() {
        Assert.assertThat(MIN_WRAPPED_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMin(((BIT_WIDTH_GROUP_ID) + (BIT_WIDTH_CHILD_ID)))));
        Assert.assertThat(MAX_WRAPPED_ID, CoreMatchers.is(ItemIdComposerTest.signedLongMax(((BIT_WIDTH_GROUP_ID) + (BIT_WIDTH_CHILD_ID)))));
    }

    @Test
    public void composeExpandableChildId() throws Exception {
        // zero
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(0L, 0L), CoreMatchers.is(0L));
        // one
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(1L, 0L), CoreMatchers.is((1L << (BIT_OFFSET_GROUP_ID))));
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(0L, 1L), CoreMatchers.is((1L << (BIT_OFFSET_CHILD_ID))));
        // minus one
        Assert.assertThat(ItemIdComposer.composeExpandableChildId((-1L), 0L), CoreMatchers.is(BIT_MASK_GROUP_ID));
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(0L, (-1L)), CoreMatchers.is(BIT_MASK_CHILD_ID));
        // min
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(MIN_GROUP_ID, 0L), CoreMatchers.is(ItemIdComposerTest.genBitMask(1, (((BIT_WIDTH_GROUP_ID) - 1) + (BIT_OFFSET_GROUP_ID)))));
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(0L, MIN_CHILD_ID), CoreMatchers.is(ItemIdComposerTest.genBitMask(1, (((BIT_WIDTH_CHILD_ID) - 1) + (BIT_OFFSET_CHILD_ID)))));
        // max
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(MAX_GROUP_ID, 0L), CoreMatchers.is(ItemIdComposerTest.genBitMask(((BIT_WIDTH_GROUP_ID) - 1), BIT_OFFSET_GROUP_ID)));
        Assert.assertThat(ItemIdComposer.composeExpandableChildId(0L, MAX_CHILD_ID), CoreMatchers.is(ItemIdComposerTest.genBitMask(((BIT_WIDTH_CHILD_ID) - 1), BIT_OFFSET_CHILD_ID)));
    }

    @Test
    public void composeExpandableChildId_GroupIdMaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(((MAX_GROUP_ID) + 1), 0);
    }

    @Test
    public void composeExpandableChildId_GroupIdMinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(((MIN_GROUP_ID) - 1), 0);
    }

    @Test
    public void composeExpandableChildId_ChildIdMaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(0, ((MAX_CHILD_ID) + 1));
    }

    @Test
    public void composeExpandableChildId_ChildIdMinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        ItemIdComposer.composeExpandableChildId(0, ((MIN_CHILD_ID) - 1));
    }

    @Test
    public void composeExpandableGroupId() throws Exception {
        // zero
        // noinspection PointlessBitwiseExpression
        Assert.assertThat(ItemIdComposer.composeExpandableGroupId(0L), CoreMatchers.is(((0L << (BIT_OFFSET_GROUP_ID)) | (BIT_MASK_CHILD_ID))));
        // one
        Assert.assertThat(ItemIdComposer.composeExpandableGroupId(1L), CoreMatchers.is(((1L << (BIT_OFFSET_GROUP_ID)) | (BIT_MASK_CHILD_ID))));
        // minus one
        Assert.assertThat(ItemIdComposer.composeExpandableGroupId((-1L)), CoreMatchers.is(((BIT_MASK_GROUP_ID) | (BIT_MASK_CHILD_ID))));
        // min
        Assert.assertThat(ItemIdComposer.composeExpandableGroupId(MIN_GROUP_ID), CoreMatchers.is(((ItemIdComposerTest.genBitMask(1, (((BIT_WIDTH_GROUP_ID) - 1) + (BIT_OFFSET_GROUP_ID)))) | (BIT_MASK_CHILD_ID))));
        // max
        Assert.assertThat(ItemIdComposer.composeExpandableGroupId(MAX_GROUP_ID), CoreMatchers.is(((ItemIdComposerTest.genBitMask(((BIT_WIDTH_GROUP_ID) - 1), BIT_OFFSET_GROUP_ID)) | (BIT_MASK_CHILD_ID))));
    }

    @Test
    public void isExpandableGroup() throws Exception {
        // zero
        Assert.assertThat(ItemIdComposer.isExpandableGroup(0L), CoreMatchers.is(false));
        // NO_ID
        Assert.assertThat(ItemIdComposer.isExpandableGroup(NO_ID), CoreMatchers.is(false));
        // not group - 1
        Assert.assertThat(ItemIdComposer.isExpandableGroup(((BIT_MASK_CHILD_ID) >> 1)), CoreMatchers.is(false));
        // not group - 2
        Assert.assertThat(ItemIdComposer.isExpandableGroup(((BIT_MASK_CHILD_ID) & (~1))), CoreMatchers.is(false));
        // is group - 1
        Assert.assertThat(ItemIdComposer.isExpandableGroup(BIT_MASK_CHILD_ID), CoreMatchers.is(true));
        // is group - 2
        Assert.assertThat(ItemIdComposer.isExpandableGroup(((BIT_MASK_GROUP_ID) | (BIT_MASK_CHILD_ID))), CoreMatchers.is(true));
    }

    @Test
    public void extractSegmentPart() throws Exception {
        // zero
        Assert.assertThat(ItemIdComposer.extractSegmentPart(0L), CoreMatchers.is(0));
        // one
        Assert.assertThat(ItemIdComposer.extractSegmentPart((1L << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(1));
        // min
        Assert.assertThat(ItemIdComposer.extractSegmentPart((((long) (MIN_SEGMENT)) << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(MIN_SEGMENT));
        // max
        Assert.assertThat(ItemIdComposer.extractSegmentPart((((long) (MAX_SEGMENT)) << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(MAX_SEGMENT));
        // etc - 1
        Assert.assertThat(ItemIdComposer.extractSegmentPart((((BIT_MASK_RESERVED_SIGN_FLAG) | (BIT_MASK_GROUP_ID)) | (BIT_MASK_CHILD_ID))), CoreMatchers.is(0));
        // etc - 2
        Assert.assertThat(ItemIdComposer.extractSegmentPart(BIT_MASK_SEGMENT), CoreMatchers.is(MAX_SEGMENT));
    }

    @Test
    public void extractExpandableGroupIdPart() throws Exception {
        // invalid - 1
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(0L), CoreMatchers.is(NO_ID));
        // invalid - 2
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(1), CoreMatchers.is(NO_ID));
        // invalid - 3
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(NO_ID), CoreMatchers.is(NO_ID));
        // zero
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(0L)), CoreMatchers.is(0L));
        // one
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(1L)), CoreMatchers.is(1L));
        // minus one
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId((-1L))), CoreMatchers.is((-1L)));
        // min
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(MIN_GROUP_ID)), CoreMatchers.is(MIN_GROUP_ID));
        // max
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(ItemIdComposer.composeExpandableGroupId(MAX_GROUP_ID)), CoreMatchers.is(MAX_GROUP_ID));
        // etc - 1
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(((BIT_MASK_RESERVED_SIGN_FLAG) | (BIT_MASK_CHILD_ID))), CoreMatchers.is(0L));
        // etc - 2
        Assert.assertThat(ItemIdComposer.extractExpandableGroupIdPart(((BIT_MASK_SEGMENT) | (BIT_MASK_CHILD_ID))), CoreMatchers.is(0L));
    }

    @Test
    public void extractExpandableChildIdPart() throws Exception {
        // invalid - 1
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableGroupId(0L)), CoreMatchers.is(NO_ID));
        // invalid - 2
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(NO_ID), CoreMatchers.is(NO_ID));
        // zero
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, 0L)), CoreMatchers.is(0L));
        // one
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, 1L)), CoreMatchers.is(1L));
        // minus one
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, (-1L))), CoreMatchers.is((-1L)));
        // min
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, MIN_CHILD_ID)), CoreMatchers.is(MIN_CHILD_ID));
        // max
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(ItemIdComposer.composeExpandableChildId(0L, MAX_CHILD_ID)), CoreMatchers.is(MAX_CHILD_ID));
        // etc - 1
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(BIT_MASK_RESERVED_SIGN_FLAG), CoreMatchers.is(0L));
        // etc - 2
        Assert.assertThat(ItemIdComposer.extractExpandableChildIdPart(BIT_MASK_SEGMENT), CoreMatchers.is(0L));
    }

    @Test
    public void extractWrappedIdPart() throws Exception {
        // invalid - 1
        Assert.assertThat(ItemIdComposer.extractWrappedIdPart(NO_ID), CoreMatchers.is(NO_ID));
        // zero
        Assert.assertThat(ItemIdComposer.extractWrappedIdPart(0L), CoreMatchers.is(0L));
        // full bits
        Assert.assertThat(ItemIdComposer.extractWrappedIdPart(((BIT_MASK_GROUP_ID) | (BIT_MASK_CHILD_ID))), CoreMatchers.is((-1L)));
        // etc - 1
        Assert.assertThat(ItemIdComposer.extractWrappedIdPart(BIT_MASK_RESERVED_SIGN_FLAG), CoreMatchers.is(0L));
        // etc - 2
        Assert.assertThat(ItemIdComposer.extractWrappedIdPart(BIT_MASK_SEGMENT), CoreMatchers.is(0L));
    }

    @Test
    public void composeSegment() throws Exception {
        // zero
        Assert.assertThat(ItemIdComposer.composeSegment(0, 0L), CoreMatchers.is(0L));
        // one
        Assert.assertThat(ItemIdComposer.composeSegment(1, 0L), CoreMatchers.is((1L << (BIT_OFFSET_SEGMENT))));
        // min
        Assert.assertThat(ItemIdComposer.composeSegment(MIN_SEGMENT, 0L), CoreMatchers.is((((long) (MIN_SEGMENT)) << (BIT_OFFSET_SEGMENT))));
        // max
        Assert.assertThat(ItemIdComposer.composeSegment(MAX_SEGMENT, 0L), CoreMatchers.is((((long) (MAX_SEGMENT)) << (BIT_OFFSET_SEGMENT))));
    }

    @Test
    public void composeSegment_MinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        // noinspection Range
        ItemIdComposer.composeSegment(((MIN_SEGMENT) - 1), 0L);
    }

    @Test
    public void composeSegment_MaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        // noinspection Range
        ItemIdComposer.composeSegment(((MAX_SEGMENT) + 1), 0L);
    }
}

