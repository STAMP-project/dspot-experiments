/**
 * Copyright 2018-present Facebook, Inc.
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


import DebugOverlayDrawable.COLOR_RED_SEMITRANSPARENT;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static DebugOverlayDrawable.BOX_WIDTH_PX;


@RunWith(ComponentsTestRunner.class)
public class DebugOverlayDrawableTest {
    @Test
    public void testEqualList() {
        List<Boolean> testList = new ArrayList<>();
        testList.add(false);
        testList.add(false);
        testList.add(false);
        DebugOverlayDrawable testD = new DebugOverlayDrawable(testList);
        List<Boolean> equalList = new ArrayList<>(testList);
        DebugOverlayDrawable equalD = new DebugOverlayDrawable(equalList);
        Assert.assertEquals(testD.hashCode(), equalD.hashCode());
        Assert.assertTrue(testD.isEquivalentTo(equalD));
    }

    @Test
    public void testNotEqualList() {
        List<Boolean> testList = new ArrayList<>();
        testList.add(true);
        testList.add(false);
        testList.add(true);
        DebugOverlayDrawable testD = new DebugOverlayDrawable(testList);
        List<Boolean> notEqualList = new ArrayList<>();
        notEqualList.add(true);
        notEqualList.add(true);
        notEqualList.add(false);
        DebugOverlayDrawable notEqualD = new DebugOverlayDrawable(notEqualList);
        Assert.assertNotEquals(testD.hashCode(), notEqualD.hashCode());
        Assert.assertFalse(testD.isEquivalentTo(notEqualD));
    }

    @Test
    public void testEmptyList() {
        List<Boolean> testList = new ArrayList<>();
        testList.add(true);
        testList.add(true);
        testList.add(true);
        DebugOverlayDrawable testD = new DebugOverlayDrawable(testList);
        DebugOverlayDrawable emptyD = new DebugOverlayDrawable(new ArrayList());
        Assert.assertNotEquals(testD.hashCode(), emptyD.hashCode());
        Assert.assertFalse(testD.isEquivalentTo(emptyD));
    }

    @Test
    public void testBigList() {
        List<Boolean> bigList = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            bigList.add(true);
        }
        DebugOverlayDrawable testD = new DebugOverlayDrawable(bigList);
        DebugOverlayDrawable bigD = new DebugOverlayDrawable(new ArrayList(bigList));
        Assert.assertEquals(testD.hashCode(), bigD.hashCode());
        Assert.assertTrue(testD.isEquivalentTo(bigD));
    }

    @Test
    public void testSameParams() {
        List<Boolean> testList = new ArrayList<>();
        testList.add(true);
        testList.add(false);
        testList.add(true);
        testList.add(true);
        DebugOverlayDrawable testD = new DebugOverlayDrawable(testList);
        DebugOverlayDrawable sameD = new DebugOverlayDrawable(testList);
        Assert.assertEquals(testD.text, sameD.text);
        Assert.assertEquals(testD.overlayColor, sameD.overlayColor);
    }

    @Test
    public void testDrawingBounds() {
        List<Boolean> single = new ArrayList<>();
        single.add(true);
        DebugOverlayDrawable debugOverlayDrawable = new DebugOverlayDrawable(single);
        int left = 10;
        int top = 15;
        int right = 30;
        int bottom = 35;
        debugOverlayDrawable.setBounds(left, top, right, bottom);
        int size = 40;
        DebugOverlayDrawableTest.TestCanvas c = new DebugOverlayDrawableTest.TestCanvas(size, size);
        debugOverlayDrawable.draw(c);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(0, c.getColor(i, (top - 2)));
            Assert.assertEquals(0, c.getColor(i, (bottom + 2)));
            Assert.assertEquals(0, c.getColor((left - 2), i));
            Assert.assertEquals(0, c.getColor((right + 2), i));
        }
        for (int i = left + (BOX_WIDTH_PX); i < right; i++) {
            Assert.assertEquals((((("Wrong color for " + i) + "-") + top) + " pixel"), COLOR_RED_SEMITRANSPARENT, c.getColor(i, top));
        }
    }

    static class TestCanvas extends Canvas {
        private final int[][] canvasColor;

        TestCanvas(int width, int height) {
            canvasColor = new int[width][height];
        }

        @Override
        public void drawRect(RectF rect, Paint paint) {
            drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
        }

        @Override
        public void drawRect(Rect rect, Paint paint) {
            drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
        }

        @Override
        public void drawRect(float left, float top, float right, float bottom, Paint paint) {
            int color = paint.getColor();
            for (int x = ((int) (left)); x < right; x++) {
                for (int y = ((int) (top)); y < bottom; y++) {
                    setColor(color, x, y);
                }
            }
        }

        private void setColor(int color, int x, int y) {
            canvasColor[x][y] = color;
        }

        int getColor(int x, int y) {
            return canvasColor[x][y];
        }
    }
}

