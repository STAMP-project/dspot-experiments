/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class InternalNodeResolvedPaddingTest {
    private InternalNode mInternalNode;

    @Test
    public void testPaddingLeftWithUndefinedStartEnd() {
        mInternalNode.paddingPx(LEFT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingLeft()).isEqualTo(10);
    }

    @Test
    public void testPaddingLeftWithDefinedStart() {
        mInternalNode.paddingPx(START, 5);
        mInternalNode.paddingPx(LEFT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingLeft()).isEqualTo(5);
    }

    @Test
    public void testPaddingLeftWithDefinedEnd() {
        mInternalNode.paddingPx(END, 5);
        mInternalNode.paddingPx(LEFT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingLeft()).isEqualTo(10);
    }

    @Test
    public void testPaddingLeftWithDefinedStartInRtl() {
        mInternalNode.paddingPx(START, 5);
        mInternalNode.paddingPx(LEFT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, RTL);
        assertThat(mInternalNode.getPaddingLeft()).isEqualTo(10);
    }

    @Test
    public void testPaddingLeftWithDefinedEndInRtl() {
        mInternalNode.paddingPx(END, 5);
        mInternalNode.paddingPx(LEFT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, RTL);
        assertThat(mInternalNode.getPaddingLeft()).isEqualTo(5);
    }

    @Test
    public void testPaddingRightWithUndefinedStartEnd() {
        mInternalNode.paddingPx(RIGHT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingRight()).isEqualTo(10);
    }

    @Test
    public void testPaddingRightWithDefinedStart() {
        mInternalNode.paddingPx(START, 5);
        mInternalNode.paddingPx(RIGHT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingRight()).isEqualTo(10);
    }

    @Test
    public void testPaddingRightWithDefinedEnd() {
        mInternalNode.paddingPx(END, 5);
        mInternalNode.paddingPx(RIGHT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, LTR);
        assertThat(mInternalNode.getPaddingRight()).isEqualTo(5);
    }

    @Test
    public void testPaddingRightWithDefinedStartInRtl() {
        mInternalNode.paddingPx(START, 5);
        mInternalNode.paddingPx(RIGHT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, RTL);
        assertThat(mInternalNode.getPaddingRight()).isEqualTo(5);
    }

    @Test
    public void testPaddingRightWithDefinedEndInRtl() {
        mInternalNode.paddingPx(END, 5);
        mInternalNode.paddingPx(RIGHT, 10);
        InternalNodeResolvedPaddingTest.setDirection(mInternalNode, RTL);
        assertThat(mInternalNode.getPaddingRight()).isEqualTo(10);
    }
}

