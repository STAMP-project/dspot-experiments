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


import ZoomableLayout.PositionalWidget;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.math.geom.Vector2i;
import org.terasology.rendering.nui.Canvas;


public class ZoomableLayoutTest {
    private static final int CANVAS_WIDTH = 100;

    private static final int CANVAS_HEIGHT = 50;

    private static final float WORLD_WIDTH = 100;

    private static final float WORLD_HEIGHT = 100;

    private ZoomableLayout zoomableLayout;

    private Canvas canvas;

    private PositionalWidget item1;

    private PositionalWidget item2;

    private PositionalWidget item3;

    private Vector2f pos1;

    private Vector2f pos2;

    private Vector2f pos3;

    private Vector2f size1;

    private Vector2f size2;

    private Vector2f size3;

    @Test
    public void testScaling() throws Exception {
        zoomableLayout.onDraw(canvas);
        // world size scaled to fit ratio of screen size - world size now 200 x 100
        Assert.assertEquals(zoomableLayout.getWindowSize(), new Vector2f(((ZoomableLayoutTest.WORLD_WIDTH) * 2), ZoomableLayoutTest.WORLD_HEIGHT));
        Assert.assertEquals(zoomableLayout.getScreenSize(), new Vector2i(ZoomableLayoutTest.CANVAS_WIDTH, ZoomableLayoutTest.CANVAS_HEIGHT));
        Assert.assertEquals(zoomableLayout.getPixelSize(), new Vector2f(((ZoomableLayoutTest.CANVAS_WIDTH) / ((ZoomableLayoutTest.WORLD_WIDTH) * 2)), ((ZoomableLayoutTest.CANVAS_HEIGHT) / (ZoomableLayoutTest.WORLD_HEIGHT))));
        // coordinates on widgets scaled down by half
        Mockito.verify(canvas).drawWidget(item1, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos1.x) / 2)), TeraMath.ceilToInt(((pos1.y) / 2))), new Vector2i(TeraMath.ceilToInt((((pos1.x) + (size1.x)) / 2)), TeraMath.ceilToInt((((pos1.y) + (size1.y)) / 2)))));
        Mockito.verify(canvas).drawWidget(item2, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos2.x) / 2)), TeraMath.ceilToInt(((pos2.y) / 2))), new Vector2i(TeraMath.ceilToInt((((pos2.x) + (size2.x)) / 2)), TeraMath.ceilToInt((((pos2.y) + (size2.y)) / 2)))));
        Mockito.verify(canvas).drawWidget(item3, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos3.x) / 2)), TeraMath.ceilToInt(((pos3.y) / 2))), new Vector2i(TeraMath.ceilToInt((((pos3.x) + (size3.x)) / 2)), TeraMath.ceilToInt((((pos3.y) + (size3.y)) / 2)))));
    }

    @Test
    public void testZoomOut() throws Exception {
        zoomableLayout.onDraw(canvas);
        // zoom out 2x from top left corner
        zoomableLayout.zoom(2, 2, new Vector2i(0, 0));
        zoomableLayout.onDraw(canvas);
        // world size doubled
        Assert.assertEquals(zoomableLayout.getWindowSize(), new Vector2f((((ZoomableLayoutTest.WORLD_WIDTH) * 2) * 2), ((ZoomableLayoutTest.WORLD_HEIGHT) * 2)));
        Assert.assertEquals(zoomableLayout.getScreenSize(), new Vector2i(ZoomableLayoutTest.CANVAS_WIDTH, ZoomableLayoutTest.CANVAS_HEIGHT));
        Assert.assertEquals(zoomableLayout.getPixelSize(), new Vector2f(((ZoomableLayoutTest.CANVAS_WIDTH) / (((ZoomableLayoutTest.WORLD_WIDTH) * 2) * 2)), ((ZoomableLayoutTest.CANVAS_HEIGHT) / ((ZoomableLayoutTest.WORLD_HEIGHT) * 2))));
        Mockito.verify(canvas).drawWidget(item1, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos1.x) / 4)), TeraMath.ceilToInt(((pos1.y) / 4))), new Vector2i(TeraMath.ceilToInt((((pos1.x) + (size1.x)) / 4)), TeraMath.ceilToInt((((pos1.y) + (size1.y)) / 4)))));
        Mockito.verify(canvas).drawWidget(item2, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos2.x) / 4)), TeraMath.ceilToInt(((pos2.y) / 4))), new Vector2i(TeraMath.ceilToInt((((pos2.x) + (size2.x)) / 4)), TeraMath.ceilToInt((((pos2.y) + (size2.y)) / 4)))));
        Mockito.verify(canvas).drawWidget(item3, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos3.x) / 4)), TeraMath.ceilToInt(((pos3.y) / 4))), new Vector2i(TeraMath.ceilToInt((((pos3.x) + (size3.x)) / 4)), TeraMath.ceilToInt((((pos3.y) + (size3.y)) / 4)))));
    }

    @Test
    public void testZoomInAndDrag() throws Exception {
        zoomableLayout.onDraw(canvas);
        // zoom in 2x towards left top corner
        zoomableLayout.zoom(0.5F, 0.5F, new Vector2i(0, 0));
        zoomableLayout.onDraw(canvas);
        // world size halved
        Assert.assertEquals(zoomableLayout.getWindowSize(), new Vector2f(ZoomableLayoutTest.WORLD_WIDTH, ((ZoomableLayoutTest.WORLD_HEIGHT) / 2)));
        Assert.assertEquals(zoomableLayout.getScreenSize(), new Vector2i(ZoomableLayoutTest.CANVAS_WIDTH, ZoomableLayoutTest.CANVAS_HEIGHT));
        Assert.assertEquals(zoomableLayout.getPixelSize(), new Vector2f(((ZoomableLayoutTest.CANVAS_WIDTH) / (ZoomableLayoutTest.WORLD_WIDTH)), ((ZoomableLayoutTest.CANVAS_HEIGHT) / ((ZoomableLayoutTest.WORLD_HEIGHT) / 2))));
        Mockito.verify(canvas).drawWidget(item1, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(pos1.x), TeraMath.ceilToInt(pos1.y)), new Vector2i(TeraMath.ceilToInt(((pos1.x) + (size1.x))), TeraMath.ceilToInt(((pos1.y) + (size1.y))))));
        Mockito.verify(canvas).drawWidget(item2, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(pos2.x), TeraMath.ceilToInt(pos2.y)), new Vector2i(TeraMath.ceilToInt(((pos2.x) + (size2.x))), TeraMath.ceilToInt(((pos2.y) + (size2.y))))));
        Mockito.verify(canvas).drawWidget(item3, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(pos3.x), TeraMath.ceilToInt(pos3.y)), new Vector2i(TeraMath.ceilToInt(((pos3.x) + (size3.x))), TeraMath.ceilToInt(((pos3.y) + (size3.y))))));
        // simulate drag to item2
        zoomableLayout.setWindowPosition(pos2);
        zoomableLayout.onDraw(canvas);
        // item1 out of canvas
        Mockito.verify(canvas).drawWidget(item1, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos1.x) - (pos2.x))), TeraMath.ceilToInt(((pos1.y) - (pos2.y)))), new Vector2i(TeraMath.ceilToInt((((pos1.x) + (size1.x)) - (pos2.x))), TeraMath.ceilToInt((((pos1.y) + (size1.y)) - (pos2.y))))));
        Mockito.verify(canvas).drawWidget(item2, Rect2i.createFromMinAndMax(Vector2i.zero(), new Vector2i(TeraMath.ceilToInt(size2.x), TeraMath.ceilToInt(size2.y))));
        Mockito.verify(canvas).drawWidget(item3, Rect2i.createFromMinAndMax(new Vector2i(TeraMath.ceilToInt(((pos3.x) - (pos2.x))), TeraMath.ceilToInt(((pos3.y) - (pos2.y)))), new Vector2i(TeraMath.ceilToInt((((pos3.x) + (size3.x)) - (pos2.x))), TeraMath.ceilToInt((((pos3.y) + (size3.y)) - (pos2.y))))));
    }
}

