/**
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.nui.layouts;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.UIWidget;


public class RowLayoutTest {
    private static final int CANVAS_HEIGHT = 200;

    private static final int CANVAS_WIDTH = 200;

    private RowLayout rowLayout;

    private Canvas canvas;

    private UIWidget itemAt1x1;

    private UIWidget itemAt1x2;

    private UIWidget itemAt1x3;

    @Test
    public void testAllRelativeWidths() throws Exception {
        // Set relative width for all 3 widgets
        rowLayout.setColumnRatios(0.4F, 0.5F, 0.1F);
        rowLayout.setHorizontalSpacing(0);
        Vector2i result = rowLayout.getPreferredContentSize(canvas, canvas.size());
        // Preferred width should be width of canvas
        Assert.assertEquals(RowLayoutTest.CANVAS_WIDTH, result.x);
        // Preferred height should be the height of the tallest widget
        Assert.assertEquals(15, result.y);
        rowLayout.onDraw(canvas);
        // Width split according to the relative widths of the widgets
        // Gets 4/10 of the entire area
        final int width1 = ((RowLayoutTest.CANVAS_WIDTH) * 4) / 10;
        // Gets 1/2 of the entire area
        final int width2 = (RowLayoutTest.CANVAS_WIDTH) / 2;
        // Gets 1/10 of the entire area
        final int width3 = (RowLayoutTest.CANVAS_WIDTH) / 10;
        Mockito.verify(canvas).drawWidget(itemAt1x1, Rect2i.createFromMinAndSize(0, 0, width1, RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x2, Rect2i.createFromMinAndSize(width1, 0, width2, RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x3, Rect2i.createFromMinAndSize((width1 + width2), 0, width3, RowLayoutTest.CANVAS_HEIGHT));
    }

    @Test
    public void testNoRelativeWidths() throws Exception {
        rowLayout.setHorizontalSpacing(0);
        Vector2i result = rowLayout.getPreferredContentSize(canvas, canvas.size());
        // Preferred width should be width of canvas
        Assert.assertEquals(RowLayoutTest.CANVAS_WIDTH, result.x);
        // Preferred height should be the height of the tallest widget
        Assert.assertEquals(15, result.y);
        rowLayout.onDraw(canvas);
        // Width split equally among 3 widgets as they have no relative widths
        Mockito.verify(canvas).drawWidget(itemAt1x1, Rect2i.createFromMinAndSize(0, 0, ((RowLayoutTest.CANVAS_WIDTH) / 3), RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x2, Rect2i.createFromMinAndSize(((RowLayoutTest.CANVAS_WIDTH) / 3), 0, ((RowLayoutTest.CANVAS_WIDTH) / 3), RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x3, Rect2i.createFromMinAndSize((((RowLayoutTest.CANVAS_WIDTH) / 3) + ((RowLayoutTest.CANVAS_WIDTH) / 3)), 0, ((RowLayoutTest.CANVAS_WIDTH) / 3), RowLayoutTest.CANVAS_HEIGHT));
    }

    @Test
    public void testSomeRelativeWidths() throws Exception {
        // Sets relative width for first widget only
        rowLayout.setColumnRatios(0.5F);
        rowLayout.setHorizontalSpacing(0);
        Vector2i result = rowLayout.getPreferredContentSize(canvas, canvas.size());
        // Preferred width should be width of canvas
        Assert.assertEquals(RowLayoutTest.CANVAS_WIDTH, result.x);
        // Preferred height should be the height of the tallest widget
        Assert.assertEquals(15, result.y);
        rowLayout.onDraw(canvas);
        // Width first determined for widget with relative width, then split equally among remaining widgets
        final int width1 = (RowLayoutTest.CANVAS_WIDTH) / 2;
        final int width2 = ((RowLayoutTest.CANVAS_WIDTH) - width1) / 2;
        final int width3 = ((RowLayoutTest.CANVAS_WIDTH) - width1) / 2;
        Mockito.verify(canvas).drawWidget(itemAt1x1, Rect2i.createFromMinAndSize(0, 0, width1, RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x2, Rect2i.createFromMinAndSize(width1, 0, width2, RowLayoutTest.CANVAS_HEIGHT));
        Mockito.verify(canvas).drawWidget(itemAt1x3, Rect2i.createFromMinAndSize((width1 + width2), 0, width3, RowLayoutTest.CANVAS_HEIGHT));
    }
}

