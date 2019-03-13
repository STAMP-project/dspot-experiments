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


import TooltipPosition.BOTTOM_LEFT;
import TooltipPosition.BOTTOM_RIGHT;
import TooltipPosition.CENTER;
import TooltipPosition.CENTER_BOTTOM;
import TooltipPosition.CENTER_LEFT;
import TooltipPosition.CENTER_RIGHT;
import TooltipPosition.CENTER_TOP;
import TooltipPosition.TOP_LEFT;
import TooltipPosition.TOP_RIGHT;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class DeprecatedLithoTooltipTest {
    private static final int HOST_WIDTH = 400;

    private static final int HOST_HEIGHT = 300;

    private static final int ANCHOR_WIDTH = 200;

    private static final int ANCHOR_HEIGHT = 100;

    private static final int MARGIN_LEFT = 20;

    private static final int MARGIN_TOP = 10;

    private ComponentContext mContext;

    private Component mComponent;

    @Mock
    public DeprecatedLithoTooltip mLithoTooltip;

    private ComponentTree mComponentTree;

    private LithoView mLithoView;

    @Test
    public void testBottomLeft() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", BOTTOM_LEFT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, DeprecatedLithoTooltipTest.MARGIN_LEFT, (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + (DeprecatedLithoTooltipTest.ANCHOR_HEIGHT)));
    }

    @Test
    public void testCenterBottom() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", CENTER_BOTTOM);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + ((DeprecatedLithoTooltipTest.ANCHOR_WIDTH) / 2)), (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + (DeprecatedLithoTooltipTest.ANCHOR_HEIGHT)));
    }

    @Test
    public void testBottomRight() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", BOTTOM_RIGHT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + (DeprecatedLithoTooltipTest.ANCHOR_WIDTH)), (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + (DeprecatedLithoTooltipTest.ANCHOR_HEIGHT)));
    }

    @Test
    public void testCenterRight() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", CENTER_RIGHT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + (DeprecatedLithoTooltipTest.ANCHOR_WIDTH)), (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + ((DeprecatedLithoTooltipTest.ANCHOR_HEIGHT) / 2)));
    }

    @Test
    public void testTopRight() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", TOP_RIGHT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + (DeprecatedLithoTooltipTest.ANCHOR_WIDTH)), ((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)));
    }

    @Test
    public void testCenterTop() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", CENTER_TOP);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + ((DeprecatedLithoTooltipTest.ANCHOR_WIDTH) / 2)), ((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)));
    }

    @Test
    public void testTopLeft() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", TOP_LEFT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, DeprecatedLithoTooltipTest.MARGIN_LEFT, ((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)));
    }

    @Test
    public void testCenterLeft() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", CENTER_LEFT);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, DeprecatedLithoTooltipTest.MARGIN_LEFT, (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + ((DeprecatedLithoTooltipTest.ANCHOR_HEIGHT) / 2)));
    }

    @Test
    public void testCenter() {
        LithoTooltipController.showTooltip(mContext, mLithoTooltip, "anchor", CENTER);
        Mockito.verify(mLithoTooltip).showBottomLeft(mLithoView, ((DeprecatedLithoTooltipTest.MARGIN_LEFT) + ((DeprecatedLithoTooltipTest.ANCHOR_WIDTH) / 2)), (((-(DeprecatedLithoTooltipTest.HOST_HEIGHT)) + (DeprecatedLithoTooltipTest.MARGIN_TOP)) + ((DeprecatedLithoTooltipTest.ANCHOR_HEIGHT) / 2)));
    }
}

