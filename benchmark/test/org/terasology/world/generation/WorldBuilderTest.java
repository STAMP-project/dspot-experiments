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
package org.terasology.world.generation;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.context.Context;
import org.terasology.context.internal.ContextImpl;
import org.terasology.math.Region3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.generation.facets.base.BaseFacet3D;
import org.terasology.world.generator.plugin.WorldGeneratorPluginLibrary;


public class WorldBuilderTest {
    private Context context = new ContextImpl();

    @Test
    public void testBorderCalculation() {
        WorldBuilder worldBuilder = new WorldBuilder(context.get(WorldGeneratorPluginLibrary.class));
        worldBuilder.setSeed(12);
        worldBuilder.addProvider(new WorldBuilderTest.Facet1Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet2Provider());
        World world = worldBuilder.build();
        Region3i regionToGenerate = Region3i.createFromCenterExtents(new Vector3i(), 1);
        Region regionData = world.getWorldData(regionToGenerate);
        WorldBuilderTest.Facet1 facet1 = regionData.getFacet(WorldBuilderTest.Facet1.class);
        Assert.assertEquals(regionToGenerate, getWorldRegion());
        WorldBuilderTest.Facet2 facet2 = regionData.getFacet(WorldBuilderTest.Facet2.class);
        Assert.assertEquals(Region3i.createFromMinAndSize(new Vector3i((-3), (-1), (-3)), new Vector3i(7, 3, 7)), getWorldRegion());
    }

    @Test
    public void testCumulativeBorderCalculation() {
        WorldBuilder worldBuilder = new WorldBuilder(context.get(WorldGeneratorPluginLibrary.class));
        worldBuilder.setSeed(12);
        worldBuilder.addProvider(new WorldBuilderTest.Facet1Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet2Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet3Provider());
        World world = worldBuilder.build();
        Region3i regionToGenerate = Region3i.createFromCenterExtents(new Vector3i(), 1);
        Region regionData = world.getWorldData(regionToGenerate);
        WorldBuilderTest.Facet3 facet3 = regionData.getFacet(WorldBuilderTest.Facet3.class);
        Assert.assertEquals(regionToGenerate, getWorldRegion());
        WorldBuilderTest.Facet1 facet1 = regionData.getFacet(WorldBuilderTest.Facet1.class);
        Assert.assertEquals(Region3i.createFromMinAndSize(new Vector3i((-2), (-1), (-2)), new Vector3i(5, 3, 5)), getWorldRegion());
        WorldBuilderTest.Facet2 facet2 = regionData.getFacet(WorldBuilderTest.Facet2.class);
        Assert.assertEquals(Region3i.createFromMinAndSize(new Vector3i((-4), (-1), (-4)), new Vector3i(9, 3, 9)), getWorldRegion());
    }

    @Test
    public void testMultiplePathsBorderCalculation() {
        WorldBuilder worldBuilder = new WorldBuilder(context.get(WorldGeneratorPluginLibrary.class));
        worldBuilder.setSeed(12);
        worldBuilder.addProvider(new WorldBuilderTest.Facet1Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet2Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet4Provider());
        World world = worldBuilder.build();
        Region3i regionToGenerate = Region3i.createFromCenterExtents(new Vector3i(), 1);
        Region regionData = world.getWorldData(regionToGenerate);
        WorldBuilderTest.Facet1 facet1 = regionData.getFacet(WorldBuilderTest.Facet1.class);
        Assert.assertEquals(regionToGenerate, getWorldRegion());
        WorldBuilderTest.Facet4 facet4 = regionData.getFacet(WorldBuilderTest.Facet4.class);
        Assert.assertEquals(regionToGenerate, getWorldRegion());
        WorldBuilderTest.Facet2 facet2 = regionData.getFacet(WorldBuilderTest.Facet2.class);
        Assert.assertEquals(Region3i.createFromMinAndSize(new Vector3i((-4), (-1), (-4)), new Vector3i(9, 3, 9)), getWorldRegion());
    }

    @Test
    public void testUpdating() {
        WorldBuilder worldBuilder = new WorldBuilder(context.get(WorldGeneratorPluginLibrary.class));
        worldBuilder.setSeed(12);
        worldBuilder.addProvider(new WorldBuilderTest.Facet1Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet2Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet3Provider());
        worldBuilder.addProvider(new WorldBuilderTest.Facet4Provider());
        worldBuilder.addProvider(new WorldBuilderTest.FacetUpdater());
        Region3i regionToGenerate = Region3i.createFromCenterExtents(new Vector3i(), 1);
        World world;
        Region regionData;
        // try checking updated facet
        world = worldBuilder.build();
        regionData = world.getWorldData(regionToGenerate);
        Assert.assertTrue(regionData.getFacet(WorldBuilderTest.Facet1.class).updated);
        Assert.assertTrue(regionData.getFacet(WorldBuilderTest.Facet4.class).updated);
        // try checking generated facet
        world = worldBuilder.build();
        regionData = world.getWorldData(regionToGenerate);
        Assert.assertNotNull(regionData.getFacet(WorldBuilderTest.Facet3.class));
        Assert.assertTrue(regionData.getFacet(WorldBuilderTest.Facet4.class).updated);
    }

    public static class Facet1 extends BaseFacet3D {
        public boolean updated;

        public Facet1(Region3i targetRegion, Border3D border) {
            super(targetRegion, border);
        }
    }

    public static class Facet2 extends BaseFacet3D {
        public Facet2(Region3i targetRegion, Border3D border) {
            super(targetRegion, border);
        }
    }

    public static class Facet3 extends BaseFacet3D {
        public Facet3(Region3i targetRegion, Border3D border) {
            super(targetRegion, border);
        }
    }

    public static class Facet4 extends BaseFacet3D {
        public boolean updated;

        public Facet4(Region3i targetRegion, Border3D border) {
            super(targetRegion, border);
        }
    }

    @Produces(WorldBuilderTest.Facet1.class)
    @Requires(@Facet(value = WorldBuilderTest.Facet2.class, border = @FacetBorder(sides = 2)))
    public static class Facet1Provider implements FacetProvider {
        @Override
        public void process(GeneratingRegion region) {
            WorldBuilderTest.Facet1 facet = new WorldBuilderTest.Facet1(region.getRegion(), region.getBorderForFacet(WorldBuilderTest.Facet1.class));
            region.setRegionFacet(WorldBuilderTest.Facet1.class, facet);
        }
    }

    @Produces(WorldBuilderTest.Facet2.class)
    public static class Facet2Provider implements FacetProvider {
        @Override
        public void process(GeneratingRegion region) {
            WorldBuilderTest.Facet2 facet = new WorldBuilderTest.Facet2(region.getRegion(), region.getBorderForFacet(WorldBuilderTest.Facet2.class));
            region.setRegionFacet(WorldBuilderTest.Facet2.class, facet);
        }
    }

    @Produces(WorldBuilderTest.Facet3.class)
    @Requires(@Facet(value = WorldBuilderTest.Facet1.class, border = @FacetBorder(sides = 1)))
    public static class Facet3Provider implements FacetProvider {
        @Override
        public void process(GeneratingRegion region) {
            WorldBuilderTest.Facet3 facet = new WorldBuilderTest.Facet3(region.getRegion(), region.getBorderForFacet(WorldBuilderTest.Facet3.class));
            region.setRegionFacet(WorldBuilderTest.Facet3.class, facet);
        }
    }

    @Produces(WorldBuilderTest.Facet4.class)
    @Requires(@Facet(value = WorldBuilderTest.Facet2.class, border = @FacetBorder(sides = 3)))
    public static class Facet4Provider implements FacetProvider {
        @Override
        public void process(GeneratingRegion region) {
            WorldBuilderTest.Facet4 facet = new WorldBuilderTest.Facet4(region.getRegion(), region.getBorderForFacet(WorldBuilderTest.Facet4.class));
            region.setRegionFacet(WorldBuilderTest.Facet4.class, facet);
        }
    }

    @Requires(@Facet(WorldBuilderTest.Facet2.class))
    @Produces(WorldBuilderTest.Facet3.class)
    @Updates({ @Facet(WorldBuilderTest.Facet1.class), @Facet(WorldBuilderTest.Facet4.class) })
    public static class FacetUpdater implements FacetProvider {
        @Override
        public void process(GeneratingRegion region) {
            WorldBuilderTest.Facet3 facet = new WorldBuilderTest.Facet3(region.getRegion(), region.getBorderForFacet(WorldBuilderTest.Facet3.class));
            WorldBuilderTest.Facet1 facet1 = region.getRegionFacet(WorldBuilderTest.Facet1.class);
            WorldBuilderTest.Facet4 facet4 = region.getRegionFacet(WorldBuilderTest.Facet4.class);
            facet1.updated = true;
            facet4.updated = true;
            region.setRegionFacet(WorldBuilderTest.Facet3.class, facet);
        }
    }
}

