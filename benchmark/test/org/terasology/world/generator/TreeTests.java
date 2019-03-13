/**
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.world.generator;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.core.world.generator.trees.Trees;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.biomes.BiomeManager;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.blockdata.ExtraBlockDataManager;


/**
 * TODO: more flexibility for estimated extents
 */
public class TreeTests {
    private static final Logger logger = LoggerFactory.getLogger(TreeTests.class);

    private BlockManager blockManager;

    private BiomeManager biomeManager;

    private ExtraBlockDataManager extraDataManager;

    @Test
    public void testBirchDims() {
        Assert.assertEquals(new Vector3i(22, 32, 22), estimateExtent(Trees.birchTree()));
    }

    @Test
    public void testOakDims() {
        Assert.assertEquals(new Vector3i(14, 14, 14), estimateExtent(Trees.oakTree()));
    }

    @Test
    public void testOakVariationDims() {
        Assert.assertEquals(new Vector3i(21, 19, 20), estimateExtent(Trees.oakVariationTree()));
    }

    @Test
    public void testPineDims() {
        Assert.assertEquals(new Vector3i(25, 28, 26), estimateExtent(Trees.pineTree()));
    }

    @Test
    public void testRedTreeDims() {
        Assert.assertEquals(new Vector3i(14, 14, 14), estimateExtent(Trees.redTree()));
    }
}

