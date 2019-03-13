/**
 * Copyright (c) [2017] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.config.blockchain;


import EtherUtil.Unit.ETHER;
import java.math.BigInteger;
import org.ethereum.config.Constants;
import org.ethereum.config.ConstantsAdapter;
import org.ethereum.core.BlockHeader;
import org.ethereum.util.blockchain.EtherUtil;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("SameParameterValue")
public class ConstantinopleConfigTest {
    private static final byte[] FAKE_HASH = new byte[]{ 11, 12 };

    private static final ConstantinopleConfig constantinopleConfig = new ConstantinopleConfig(new TestBlockchainConfig());

    @Test
    public void testRelatedEip() throws Exception {
        // Byzantium
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip198());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip206());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip211());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip212());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip213());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip214());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip658());
        // Constantinople
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip145());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip1014());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip1052());
        Assert.assertTrue(ConstantinopleConfigTest.constantinopleConfig.eip1283());
        ByzantiumConfig byzantiumConfig = new ByzantiumConfig(new TestBlockchainConfig());
        // Constantinople eips in Byzantium
        Assert.assertFalse(byzantiumConfig.eip145());
        Assert.assertFalse(byzantiumConfig.eip1014());
        Assert.assertFalse(byzantiumConfig.eip1052());
        Assert.assertFalse(byzantiumConfig.eip1283());
    }

    @Test
    public void testDifficultyWithoutExplosion() throws Exception {
        BlockHeader parent = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 0L, 1000000).build();
        BlockHeader current = new BlockHeaderBuilder(parent.getHash(), 1L, (-1)).build();
        BigInteger difficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent);
        Assert.assertEquals(BigInteger.valueOf(1000976), difficulty);
    }

    @Test
    public void testDifficultyAdjustedForParentBlockHavingUncles() throws Exception {
        BlockHeader parent = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 0L, 0).withTimestamp(0L).withUncles(new byte[]{ 1, 2 }).build();
        BlockHeader current = new BlockHeaderBuilder(parent.getHash(), 1L, 0).withTimestamp(9L).build();
        Assert.assertEquals(1, ConstantinopleConfigTest.constantinopleConfig.getCalcDifficultyMultiplier(current, parent).intValue());
    }

    @Test
    public void testDifficultyWithExplosionShouldBeImpactedByBlockTimestamp() throws Exception {
        BlockHeader parent = new BlockHeaderBuilder(new byte[]{ 11, 12 }, 2500000, 8388608).withTimestamp(0).build();
        BlockHeader current = // 10 minutes later, longer time: lowers difficulty
        new BlockHeaderBuilder(parent.getHash(), 2500001, 8388608).withTimestamp((10 * 60)).build();
        BigInteger difficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent);
        Assert.assertEquals(BigInteger.valueOf(8126464), difficulty);
        parent = new BlockHeaderBuilder(new byte[]{ 11, 12 }, 2500000, 8388608).withTimestamp(0).build();
        current = // 5 seconds later, shorter time: higher difficulty
        new BlockHeaderBuilder(parent.getHash(), 2500001, 8388608).withTimestamp(5).build();
        difficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent);
        Assert.assertEquals(BigInteger.valueOf(8396800), difficulty);
    }

    @Test
    public void testDifficultyAboveBlock5MShouldTriggerExplosion() throws Exception {
        int parentDifficulty = 268435456;
        BlockHeader parent = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 6000000, parentDifficulty).build();
        BlockHeader current = new BlockHeaderBuilder(parent.getHash(), 6000001, (-1)).build();
        int actualDifficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent).intValue();
        int differenceWithoutExplosion = actualDifficulty - parentDifficulty;
        Assert.assertEquals(262400, differenceWithoutExplosion);
        parent = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 7000000, parentDifficulty).build();
        current = new BlockHeaderBuilder(parent.getHash(), 7000001, (-1)).build();
        actualDifficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent).intValue();
        differenceWithoutExplosion = actualDifficulty - parentDifficulty;
        Assert.assertEquals(524288, differenceWithoutExplosion);
        parent = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 8000000, parentDifficulty).build();
        current = new BlockHeaderBuilder(parent.getHash(), 8000001, (-1)).build();
        actualDifficulty = ConstantinopleConfigTest.constantinopleConfig.calcDifficulty(current, parent).intValue();
        differenceWithoutExplosion = actualDifficulty - parentDifficulty;
        Assert.assertEquals(268697600, differenceWithoutExplosion);// Explosion!

    }

    @Test
    @SuppressWarnings("PointlessArithmeticExpression")
    public void testCalcDifficultyMultiplier() throws Exception {
        // Note; timestamps are in seconds
        assertCalcDifficultyMultiplier(0L, 1L, 2);
        assertCalcDifficultyMultiplier(0L, 5, 2);// 5 seconds

        assertCalcDifficultyMultiplier(0L, (1 * 10), 1);// 10 seconds

        assertCalcDifficultyMultiplier(0L, (2 * 10), (-0));// 20 seconds

        assertCalcDifficultyMultiplier(0L, (10 * 10), (-9));// 100 seconds

        assertCalcDifficultyMultiplier(0L, (60 * 10), (-64));// 10 mins

        assertCalcDifficultyMultiplier(0L, (60 * 12), (-78));// 12 mins

    }

    @Test
    public void testExplosionChanges() throws Exception {
        BlockHeader beforePauseBlock = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 4000000, 0).build();
        Assert.assertEquals((-2), ConstantinopleConfigTest.constantinopleConfig.getExplosion(beforePauseBlock, null));
        BlockHeader endOfIceAge = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 5000000, 0).build();
        Assert.assertEquals((-2), ConstantinopleConfigTest.constantinopleConfig.getExplosion(endOfIceAge, null));
        BlockHeader startExplodingBlock = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 5200000, 0).build();
        Assert.assertEquals(0, ConstantinopleConfigTest.constantinopleConfig.getExplosion(startExplodingBlock, null));
        startExplodingBlock = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 6000000, 0).build();
        Assert.assertEquals(8, ConstantinopleConfigTest.constantinopleConfig.getExplosion(startExplodingBlock, null));
        startExplodingBlock = new BlockHeaderBuilder(ConstantinopleConfigTest.FAKE_HASH, 8000000, 0).build();
        Assert.assertEquals(28, ConstantinopleConfigTest.constantinopleConfig.getExplosion(startExplodingBlock, null));
    }

    @Test
    public void testBlockReward() throws Exception {
        ConstantinopleConfig constantinopleConfig2 = new ConstantinopleConfig(new TestBlockchainConfig() {
            @Override
            public Constants getConstants() {
                return new ConstantsAdapter(super.getConstants()) {
                    @Override
                    public BigInteger getBLOCK_REWARD() {
                        // Make sure ConstantinopleConfig is not using parent's block reward
                        return BigInteger.TEN;
                    }
                };
            }
        });
        Assert.assertEquals(EtherUtil.convert(2, ETHER), constantinopleConfig2.getConstants().getBLOCK_REWARD());
    }
}

