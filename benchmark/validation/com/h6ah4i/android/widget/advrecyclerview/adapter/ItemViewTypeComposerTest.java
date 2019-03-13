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


import ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG;
import ItemViewTypeComposer.BIT_MASK_SEGMENT;
import ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE;
import ItemViewTypeComposer.BIT_OFFSET_EXPANDABLE_FLAG;
import ItemViewTypeComposer.BIT_OFFSET_SEGMENT;
import ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE;
import ItemViewTypeComposer.BIT_WIDTH_EXPANDABLE_FLAG;
import ItemViewTypeComposer.BIT_WIDTH_SEGMENT;
import ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE;
import ItemViewTypeComposer.MAX_SEGMENT;
import ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE;
import ItemViewTypeComposer.MIN_SEGMENT;
import ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static ItemViewTypeComposer.BIT_MASK_EXPANDABLE_FLAG;
import static ItemViewTypeComposer.BIT_MASK_SEGMENT;
import static ItemViewTypeComposer.BIT_MASK_WRAPPED_VIEW_TYPE;
import static ItemViewTypeComposer.BIT_OFFSET_EXPANDABLE_FLAG;
import static ItemViewTypeComposer.BIT_OFFSET_SEGMENT;
import static ItemViewTypeComposer.BIT_OFFSET_WRAPPED_VIEW_TYPE;
import static ItemViewTypeComposer.BIT_WIDTH_EXPANDABLE_FLAG;
import static ItemViewTypeComposer.BIT_WIDTH_SEGMENT;
import static ItemViewTypeComposer.BIT_WIDTH_WRAPPED_VIEW_TYPE;
import static ItemViewTypeComposer.MAX_SEGMENT;
import static ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE;
import static ItemViewTypeComposer.MIN_SEGMENT;
import static ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE;


public class ItemViewTypeComposerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void totalBitWidth() {
        Assert.assertThat((((BIT_WIDTH_EXPANDABLE_FLAG) + (BIT_WIDTH_SEGMENT)) + (BIT_WIDTH_WRAPPED_VIEW_TYPE)), CoreMatchers.is(32));
    }

    @Test
    public void bitOffsets() {
        Assert.assertThat(BIT_OFFSET_WRAPPED_VIEW_TYPE, CoreMatchers.is(0));
        Assert.assertThat(BIT_OFFSET_SEGMENT, CoreMatchers.is(((BIT_WIDTH_WRAPPED_VIEW_TYPE) + (BIT_OFFSET_WRAPPED_VIEW_TYPE))));
        Assert.assertThat(BIT_OFFSET_EXPANDABLE_FLAG, CoreMatchers.is(((BIT_WIDTH_SEGMENT) + (BIT_OFFSET_SEGMENT))));
        Assert.assertThat(((BIT_OFFSET_EXPANDABLE_FLAG) + (BIT_WIDTH_EXPANDABLE_FLAG)), CoreMatchers.is(32));
    }

    @Test
    public void bitMasks() {
        Assert.assertThat(BIT_MASK_EXPANDABLE_FLAG, CoreMatchers.is(ItemViewTypeComposerTest.genBitMask(BIT_WIDTH_EXPANDABLE_FLAG, BIT_OFFSET_EXPANDABLE_FLAG)));
        Assert.assertThat(BIT_MASK_SEGMENT, CoreMatchers.is(ItemViewTypeComposerTest.genBitMask(BIT_WIDTH_SEGMENT, BIT_OFFSET_SEGMENT)));
        Assert.assertThat(BIT_MASK_WRAPPED_VIEW_TYPE, CoreMatchers.is(ItemViewTypeComposerTest.genBitMask(BIT_WIDTH_WRAPPED_VIEW_TYPE, BIT_OFFSET_WRAPPED_VIEW_TYPE)));
    }

    @Test
    public void bitWidthSegment() {
        Assert.assertThat(BIT_WIDTH_SEGMENT, CoreMatchers.is(ItemIdComposer.BIT_WIDTH_SEGMENT));
    }

    @Test
    public void minMaxSegment() {
        Assert.assertThat(MIN_SEGMENT, CoreMatchers.is(0));
        Assert.assertThat(MAX_SEGMENT, CoreMatchers.is(ItemViewTypeComposerTest.unsignedIntMax(BIT_WIDTH_SEGMENT)));
    }

    @Test
    public void minMaxWrappedViewType() {
        Assert.assertThat(MIN_WRAPPED_VIEW_TYPE, CoreMatchers.is(ItemViewTypeComposerTest.signedIntMin(BIT_WIDTH_WRAPPED_VIEW_TYPE)));
        Assert.assertThat(MAX_WRAPPED_VIEW_TYPE, CoreMatchers.is(ItemViewTypeComposerTest.signedIntMax(BIT_WIDTH_WRAPPED_VIEW_TYPE)));
    }

    @Test
    public void extractSegmentPart() throws Exception {
        // zero
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart(0), CoreMatchers.is(0));
        // one
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart((1 << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(1));
        // min
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart(((MIN_SEGMENT) << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(MIN_SEGMENT));
        // max
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart(((MAX_SEGMENT) << (BIT_OFFSET_SEGMENT))), CoreMatchers.is(MAX_SEGMENT));
        // etc - 1
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart(((BIT_MASK_EXPANDABLE_FLAG) | (BIT_MASK_WRAPPED_VIEW_TYPE))), CoreMatchers.is(0));
        // etc - 2
        Assert.assertThat(ItemViewTypeComposer.extractSegmentPart(BIT_MASK_SEGMENT), CoreMatchers.is(MAX_SEGMENT));
    }

    @Test
    public void extractWrappedViewTypePart() throws Exception {
        // zero
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(0), CoreMatchers.is(0));
        // one
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart((1 << (BIT_OFFSET_WRAPPED_VIEW_TYPE))), CoreMatchers.is(1));
        // full bits
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(BIT_MASK_WRAPPED_VIEW_TYPE), CoreMatchers.is((-1)));
        // min
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(((MIN_WRAPPED_VIEW_TYPE) << (BIT_OFFSET_WRAPPED_VIEW_TYPE))), CoreMatchers.is(MIN_WRAPPED_VIEW_TYPE));
        // max
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(((MAX_WRAPPED_VIEW_TYPE) << (BIT_OFFSET_WRAPPED_VIEW_TYPE))), CoreMatchers.is(MAX_WRAPPED_VIEW_TYPE));
        // etc - 1
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(BIT_MASK_EXPANDABLE_FLAG), CoreMatchers.is(0));
        // etc - 2
        Assert.assertThat(ItemViewTypeComposer.extractWrappedViewTypePart(BIT_MASK_SEGMENT), CoreMatchers.is(0));
    }

    @Test
    public void isExpandableGroup() throws Exception {
        // zero
        Assert.assertThat(ItemViewTypeComposer.isExpandableGroup(0), CoreMatchers.is(false));
        // not group - 1
        Assert.assertThat(ItemViewTypeComposer.isExpandableGroup(BIT_MASK_SEGMENT), CoreMatchers.is(false));
        // not group - 2
        Assert.assertThat(ItemViewTypeComposer.isExpandableGroup(BIT_MASK_WRAPPED_VIEW_TYPE), CoreMatchers.is(false));
        // is group - 1
        Assert.assertThat(ItemViewTypeComposer.isExpandableGroup(BIT_MASK_EXPANDABLE_FLAG), CoreMatchers.is(true));
        // is group - 2
        Assert.assertThat(ItemViewTypeComposer.isExpandableGroup((((BIT_MASK_EXPANDABLE_FLAG) | (BIT_MASK_SEGMENT)) | (BIT_MASK_WRAPPED_VIEW_TYPE))), CoreMatchers.is(true));
    }

    @Test
    public void composeSegment() throws Exception {
        // zero
        Assert.assertThat(ItemViewTypeComposer.composeSegment(0, 0), CoreMatchers.is(0));
        // one
        Assert.assertThat(ItemViewTypeComposer.composeSegment(1, 0), CoreMatchers.is((1 << (BIT_OFFSET_SEGMENT))));
        // min
        Assert.assertThat(ItemViewTypeComposer.composeSegment(MIN_SEGMENT, 0), CoreMatchers.is(((MIN_SEGMENT) << (BIT_OFFSET_SEGMENT))));
        // max
        Assert.assertThat(ItemViewTypeComposer.composeSegment(MAX_SEGMENT, 0), CoreMatchers.is(((MAX_SEGMENT) << (BIT_OFFSET_SEGMENT))));
    }

    @Test
    public void composeSegment_MinOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        // noinspection Range
        ItemViewTypeComposer.composeSegment(((MIN_SEGMENT) - 1), 0);
    }

    @Test
    public void composeSegment_MaxOutOfRange() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        // noinspection Range
        ItemViewTypeComposer.composeSegment(((MAX_SEGMENT) + 1), 0);
    }
}

