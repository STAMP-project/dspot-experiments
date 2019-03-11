/**
 * Copyright 2017 MovingBlocks
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
package org.terasology.world.Zones;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.world.generation.Region;
import org.terasology.world.zones.LayeredZoneRegionFunction;
import org.terasology.world.zones.MinMaxLayerThickness;
import org.terasology.world.zones.Zone;


public class LayeredZoneRegionFunctionTest {
    private Zone parent = new Zone("Parent", () -> true);

    private Region region;

    @Test
    public void testCreation() {
        int minWidth = 100;
        int maxWidth = 200;
        int ordering = 1000;
        LayeredZoneRegionFunction function = new LayeredZoneRegionFunction(new MinMaxLayerThickness(minWidth, maxWidth), ordering);
        Assert.assertEquals(ordering, function.getOrdering());
    }

    @Test
    public void testSurface() {
        Assert.assertTrue(parent.getChildZone("Ground").containsBlock(0, 100, 0, region));
        Assert.assertTrue(parent.getChildZone("Ground").containsBlock(0, 1, 0, region));
        Assert.assertFalse(parent.getChildZone("Ground").containsBlock(0, 101, 0, region));
        Assert.assertFalse(parent.getChildZone("Ground").containsBlock(0, 0, 0, region));
        Assert.assertTrue(parent.getChildZone("Above ground").containsBlock(0, 101, 0, region));
        Assert.assertTrue(parent.getChildZone("Above ground").containsBlock(0, 200, 0, region));
        Assert.assertFalse(parent.getChildZone("Above ground").containsBlock(0, 100, 0, region));
        Assert.assertFalse(parent.getChildZone("Above ground").containsBlock(0, 201, 0, region));
    }

    @Test
    public void testUnderground() {
        Assert.assertTrue(parent.getChildZone("Shallow underground").containsBlock(0, 0, 0, region));
        Assert.assertTrue(parent.getChildZone("Shallow underground").containsBlock(0, (-99), 0, region));
        Assert.assertFalse(parent.getChildZone("Shallow underground").containsBlock(0, 1, 0, region));
        Assert.assertFalse(parent.getChildZone("Shallow underground").containsBlock(0, (-100), 0, region));
    }

    @Test
    public void testSky() {
        Assert.assertTrue(parent.getChildZone("Low sky").containsBlock(0, 201, 0, region));
        Assert.assertTrue(parent.getChildZone("Low sky").containsBlock(0, 300, 0, region));
        Assert.assertFalse(parent.getChildZone("Low sky").containsBlock(0, 200, 0, region));
        Assert.assertFalse(parent.getChildZone("Low sky").containsBlock(0, 301, 0, region));
    }

    @Test
    public void testExtremes() {
        // Test values at the extremes (beyond the top and bottom of the declared layers
        // The last layer in each direction should extend outwards
        Assert.assertTrue(parent.getChildZone("Medium sky").containsBlock(0, 10000, 0, region));
        Assert.assertTrue(parent.getChildZone("Medium underground").containsBlock(0, (-10000), 0, region));
        Assert.assertFalse(parent.getChildZone("Medium sky").containsBlock(0, (-10000), 0, region));
        Assert.assertFalse(parent.getChildZone("Medium underground").containsBlock(0, 10000, 0, region));
    }
}

