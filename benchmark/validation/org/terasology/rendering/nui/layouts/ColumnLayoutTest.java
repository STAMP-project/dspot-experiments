/**
 * Copyright 2014 MovingBlocks
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


public class ColumnLayoutTest {
    private static final int CANVAS_HEIGHT = 200;

    private static final int CANVAS_WIDTH = 200;

    private ColumnLayout columnLayout;

    private Canvas canvas;

    private UIWidget itemAt1x1;

    private UIWidget itemAt2x1;

    private UIWidget itemAt3x1;

    private UIWidget itemAt1x2;

    private UIWidget itemAt2x2;

    private UIWidget itemAt3x2;

    @Test
    public void testThreeColumnsProportionallySized() throws Exception {
        columnLayout.setAutoSizeColumns(false);
        columnLayout.setFillVerticalSpace(false);
        columnLayout.setColumnWidths(0.5F, 0.2F, 0.3F);
        Vector2i result = columnLayout.getPreferredContentSize(canvas, canvas.size());
        // This is the size of the first column divided by its ratio.
        // In general, the minimum column size / ratio guarantees the ration
        // and insures that every column has at least as much as its preferred size
        Assert.assertEquals(100, result.x);
        Assert.assertEquals(20, result.y);
        columnLayout.onDraw(canvas);
        // Gets half of entire area
        Mockito.verify(canvas).drawWidget(itemAt1x1, Rect2i.createFromMinAndSize(0, (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), ((ColumnLayoutTest.CANVAS_WIDTH) / 2), 10));
        // Gets one-fifth of entire area
        Mockito.verify(canvas).drawWidget(itemAt2x1, Rect2i.createFromMinAndSize(((ColumnLayoutTest.CANVAS_WIDTH) / 2), (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), (((ColumnLayoutTest.CANVAS_WIDTH) * 2) / 10), 10));
        // Gets three-tens of entire area
        Mockito.verify(canvas).drawWidget(itemAt3x1, Rect2i.createFromMinAndSize((((ColumnLayoutTest.CANVAS_WIDTH) / 2) + (((ColumnLayoutTest.CANVAS_WIDTH) * 2) / 10)), (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), (((ColumnLayoutTest.CANVAS_WIDTH) * 3) / 10), 10));
        // Gets half of entire area
        Mockito.verify(canvas).drawWidget(itemAt1x2, Rect2i.createFromMinAndSize(0, ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), ((ColumnLayoutTest.CANVAS_WIDTH) / 2), 10));
        // Gets one-fifth of entire area
        Mockito.verify(canvas).drawWidget(itemAt2x2, Rect2i.createFromMinAndSize(((ColumnLayoutTest.CANVAS_WIDTH) / 2), ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), (((ColumnLayoutTest.CANVAS_WIDTH) * 2) / 10), 10));
        // Gets three-tens of entire area
        Mockito.verify(canvas).drawWidget(itemAt3x2, Rect2i.createFromMinAndSize((((ColumnLayoutTest.CANVAS_WIDTH) / 2) + (((ColumnLayoutTest.CANVAS_WIDTH) * 2) / 10)), ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), (((ColumnLayoutTest.CANVAS_WIDTH) * 3) / 10), 10));
    }

    @Test
    public void testThreeColumnsAutosizedMinimallySized() throws Exception {
        columnLayout.setAutoSizeColumns(true);
        columnLayout.setFillVerticalSpace(false);
        Vector2i result = columnLayout.getPreferredContentSize(canvas, canvas.size());
        Assert.assertEquals(75, result.x);
        Assert.assertEquals(20, result.y);
        columnLayout.onDraw(canvas);
        Mockito.verify(canvas).drawWidget(itemAt1x1, Rect2i.createFromMinAndSize((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2), (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), 50, 10));
        Mockito.verify(canvas).drawWidget(itemAt2x1, Rect2i.createFromMinAndSize(((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2) + 50), (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), 5, 10));
        Mockito.verify(canvas).drawWidget(itemAt3x1, Rect2i.createFromMinAndSize((((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2) + 50) + 5), (((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2), 20, 10));
        Mockito.verify(canvas).drawWidget(itemAt1x2, Rect2i.createFromMinAndSize((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2), ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), 50, 10));
        Mockito.verify(canvas).drawWidget(itemAt2x2, Rect2i.createFromMinAndSize(((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2) + 50), ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), 5, 10));
        Mockito.verify(canvas).drawWidget(itemAt3x2, Rect2i.createFromMinAndSize((((((ColumnLayoutTest.CANVAS_WIDTH) - 75) / 2) + 50) + 5), ((((ColumnLayoutTest.CANVAS_HEIGHT) - 20) / 2) + 10), 20, 10));
    }
}

