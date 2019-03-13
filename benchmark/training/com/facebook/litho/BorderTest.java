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


import Border.Corner;
import Border.Corner.BOTTOM_LEFT;
import Border.Corner.BOTTOM_RIGHT;
import Border.Corner.TOP_LEFT;
import Border.Corner.TOP_RIGHT;
import PathDashPathEffect.Style.MORPH;
import PathDashPathEffect.Style.ROTATE;
import YogaEdge.ALL;
import YogaEdge.BOTTOM;
import YogaEdge.END;
import YogaEdge.HORIZONTAL;
import YogaEdge.LEFT;
import YogaEdge.RIGHT;
import YogaEdge.START;
import YogaEdge.TOP;
import YogaEdge.VERTICAL;
import android.graphics.ComposePathEffect;
import android.graphics.DashPathEffect;
import android.graphics.DiscretePathEffect;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static Border.EDGE_BOTTOM;
import static Border.EDGE_LEFT;
import static Border.EDGE_RIGHT;
import static Border.EDGE_TOP;


@RunWith(ComponentsTestRunner.class)
public class BorderTest {
    @Test
    public void testIndividualColorSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(LEFT, -65536).color(TOP, -256).color(RIGHT, -1).color(BOTTOM, -65281).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_TOP]).isEqualTo(-256);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(-1);
        assertThat(border.mEdgeColors[EDGE_BOTTOM]).isEqualTo(-65281);
    }

    @Test
    public void testAllColorSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(ALL, -65536).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_TOP]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_BOTTOM]).isEqualTo(-65536);
    }

    @Test
    public void testHorizontalColorSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(ALL, -65536).color(HORIZONTAL, -16711936).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-16711936);
        assertThat(border.mEdgeColors[EDGE_TOP]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(-16711936);
        assertThat(border.mEdgeColors[EDGE_BOTTOM]).isEqualTo(-65536);
    }

    @Test
    public void testVerticalColorSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(ALL, -65536).color(VERTICAL, -16711936).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_TOP]).isEqualTo(-16711936);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_BOTTOM]).isEqualTo(-16711936);
    }

    @Test
    public void testStartEndResolving() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(START, -65536).color(END, 65535).widthPx(START, 100).widthPx(END, 200).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(65535);
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(100);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(200);
    }

    @Test
    public void testIndividualWidthSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).widthPx(LEFT, 1).widthPx(TOP, 2).widthPx(RIGHT, 3).widthPx(BOTTOM, 4).build();
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(1);
        assertThat(border.mEdgeWidths[EDGE_TOP]).isEqualTo(2);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(3);
        assertThat(border.mEdgeWidths[EDGE_BOTTOM]).isEqualTo(4);
    }

    @Test
    public void testAllWidthSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).widthPx(ALL, 5).build();
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_TOP]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_BOTTOM]).isEqualTo(5);
    }

    @Test
    public void testHorizontalWidthSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).widthPx(ALL, 1).widthPx(HORIZONTAL, 5).build();
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_TOP]).isEqualTo(1);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_BOTTOM]).isEqualTo(1);
    }

    @Test
    public void testVerticalWidthSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).widthPx(ALL, 1).widthPx(VERTICAL, 5).build();
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(1);
        assertThat(border.mEdgeWidths[EDGE_TOP]).isEqualTo(5);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(1);
        assertThat(border.mEdgeWidths[EDGE_BOTTOM]).isEqualTo(5);
    }

    @Test
    public void testAllColorWidthSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).color(LEFT, -65536).color(TOP, -256).color(RIGHT, -1).color(BOTTOM, -65281).widthPx(LEFT, 1).widthPx(TOP, 2).widthPx(RIGHT, 3).widthPx(BOTTOM, 4).build();
        assertThat(border.mEdgeColors[EDGE_LEFT]).isEqualTo(-65536);
        assertThat(border.mEdgeColors[EDGE_TOP]).isEqualTo(-256);
        assertThat(border.mEdgeColors[EDGE_RIGHT]).isEqualTo(-1);
        assertThat(border.mEdgeColors[EDGE_BOTTOM]).isEqualTo(-65281);
        assertThat(border.mEdgeWidths[EDGE_LEFT]).isEqualTo(1);
        assertThat(border.mEdgeWidths[EDGE_TOP]).isEqualTo(2);
        assertThat(border.mEdgeWidths[EDGE_RIGHT]).isEqualTo(3);
        assertThat(border.mEdgeWidths[EDGE_BOTTOM]).isEqualTo(4);
    }

    @Test
    public void testBothRadiiSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).radiusPx(1337).build();
        assertThat(border.mRadius[Corner.BOTTOM_LEFT]).isEqualTo(1337);
        assertThat(border.mRadius[Corner.BOTTOM_RIGHT]).isEqualTo(1337);
        assertThat(border.mRadius[Corner.TOP_LEFT]).isEqualTo(1337);
        assertThat(border.mRadius[Corner.TOP_RIGHT]).isEqualTo(1337);
    }

    @Test
    public void testIndividualRadiiSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).radiusPx(TOP_LEFT, 1).radiusPx(TOP_RIGHT, 2).radiusPx(BOTTOM_RIGHT, 3).radiusPx(BOTTOM_LEFT, 4).build();
        assertThat(border.mRadius[Corner.TOP_LEFT]).isEqualTo(1);
        assertThat(border.mRadius[Corner.TOP_RIGHT]).isEqualTo(2);
        assertThat(border.mRadius[Corner.BOTTOM_RIGHT]).isEqualTo(3);
        assertThat(border.mRadius[Corner.BOTTOM_LEFT]).isEqualTo(4);
    }

    @Test
    public void testEffectSetting() {
        final ComponentContext c = new ComponentContext(application);
        Border border = Border.create(c).dashEffect(new float[]{ 1.0F, 1.0F }, 0.0F).build();
        assertThat(border.mPathEffect).isInstanceOf(DashPathEffect.class);
        border = Border.create(c).discreteEffect(1.0F, 0.0F).build();
        assertThat(border.mPathEffect).isInstanceOf(DiscretePathEffect.class);
        border = Border.create(c).pathDashEffect(new Path(), 0.0F, 0.0F, ROTATE).build();
        assertThat(border.mPathEffect).isInstanceOf(PathDashPathEffect.class);
        border = Border.create(c).discreteEffect(1.0F, 1.0F).dashEffect(new float[]{ 1.0F, 2.0F }, 1.0F).build();
        assertThat(border.mPathEffect).isInstanceOf(ComposePathEffect.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyEffectsThrows() {
        final ComponentContext c = new ComponentContext(application);
        Border.create(c).pathDashEffect(new Path(), 1.0F, 1.0F, MORPH).dashEffect(new float[]{ 1.0F, 2.0F }, 1.0F).discreteEffect(1.0F, 2.0F).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentWidthWithEffectThrows() {
        final ComponentContext c = new ComponentContext(application);
        Border.create(c).widthPx(ALL, 10).widthPx(LEFT, 5).discreteEffect(1.0F, 1.0F).build();
    }
}

