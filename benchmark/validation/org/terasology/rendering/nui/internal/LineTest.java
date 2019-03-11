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
package org.terasology.rendering.nui.internal;


import Rect2i.EMPTY;
import org.junit.Assert;
import org.junit.Test;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2i;


public class LineTest {
    private Rect2i cropRegion;

    @Test
    public void testRelativeToAbsolute() {
        Rect2i relativeRegion = Rect2i.createFromMinAndMax(5, 10, 20, 15);
        Assert.assertEquals(Line.relativeToAbsolute(relativeRegion, cropRegion), Rect2i.createFromMinAndSize(((cropRegion.minX()) + (relativeRegion.minX())), ((cropRegion.minY()) + (relativeRegion.minY())), relativeRegion.width(), relativeRegion.height()));
    }

    @Test
    public void testLineCoordinatesNoIntersection() {
        // Line is located in the bottom left, outside the region
        int sx = (cropRegion.minX()) - 5;
        int sy = (cropRegion.minY()) - 5;
        int ex = (cropRegion.minX()) - 5;
        int ey = (cropRegion.minY()) - 5;
        Assert.assertNull(Line.getLineCoordinates(sx, sy, ex, ey, EMPTY, cropRegion));
        // Top left
        sx = (cropRegion.minX()) - 5;
        sy = (cropRegion.maxY()) + 5;
        ex = (cropRegion.minX()) - 5;
        ey = (cropRegion.maxY()) + 5;
        Assert.assertNull(Line.getLineCoordinates(sx, sy, ex, ey, EMPTY, cropRegion));
        // Bottom right
        sx = (cropRegion.maxX()) + 5;
        sy = (cropRegion.minY()) - 5;
        ex = (cropRegion.maxX()) + 5;
        ey = (cropRegion.maxY()) - 5;
        Assert.assertNull(Line.getLineCoordinates(sx, sy, ex, ey, EMPTY, cropRegion));
        // Top right
        sx = (cropRegion.maxX()) + 5;
        sy = (cropRegion.maxY()) + 5;
        ex = (cropRegion.maxX()) + 5;
        ey = (cropRegion.maxY()) + 5;
        Assert.assertNull(Line.getLineCoordinates(sx, sy, ex, ey, EMPTY, cropRegion));
    }

    @Test
    public void testLineCoordinatesIntersection() {
        // Test several preset intersecting lines
        Assert.assertEquals(new Line.LineCoordinates(new Vector2i(10, 30), new Vector2i(30, 20)), Line.getLineCoordinates(0, 40, 40, 0, EMPTY, cropRegion));
        Assert.assertEquals(new Line.LineCoordinates(new Vector2i(10, 25), new Vector2i(30, 25)), Line.getLineCoordinates(5, 25, 35, 25, EMPTY, cropRegion));
        Assert.assertEquals(new Line.LineCoordinates(new Vector2i(20, 20), new Vector2i(20, 30)), Line.getLineCoordinates(20, 5, 20, 35, EMPTY, cropRegion));
        Assert.assertEquals(new Line.LineCoordinates(new Vector2i(20, 25), new Vector2i(30, 30)), Line.getLineCoordinates(20, 25, 40, 40, EMPTY, cropRegion));
    }
}

