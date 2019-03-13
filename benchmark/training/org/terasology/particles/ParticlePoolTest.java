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
package org.terasology.particles;


import org.junit.Assert;
import org.junit.Test;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;


/**
 * Unit test for {@link ParticlePool}.
 */
public class ParticlePoolTest {
    private static Random random = new FastRandom(9083);

    @Test(expected = IllegalArgumentException.class)
    public void constructorTest() {
        final int[] poolSizes = new int[]{ 1, 27, 133 };
        for (int size : poolSizes) {
            ParticlePool pool = new ParticlePool(size);
            Assert.assertEquals(size, pool.size());
            Assert.assertEquals(0, pool.livingParticles());
            Assert.assertEquals(pool.size(), pool.deadParticles());
            Assert.assertEquals(size, pool.energy.length);
            Assert.assertEquals((size * 3), pool.position.length);
            Assert.assertEquals((size * 3), pool.previousPosition.length);
            Assert.assertEquals((size * 3), pool.velocity.length);
            Assert.assertEquals((size * 3), pool.scale.length);
            Assert.assertEquals((size * 4), pool.color.length);
        }
        // Should throw exception after creating the pool
        ParticlePool pool = new ParticlePool(0);
    }

    @Test
    public void reviveParticleTest() {
        final int poolSize = 8;
        ParticlePool pool = new ParticlePool(poolSize);
        for (int i = 1; i <= poolSize; i++) {
            pool.reviveParticle();
            Assert.assertEquals(i, pool.livingParticles());
            Assert.assertEquals((poolSize - i), pool.deadParticles());
            Assert.assertEquals(poolSize, pool.size());
        }
    }

    @Test
    public void moveDeceasedTest() {
        // initialize
        int poolSize = 14;
        int livingParticles = 7;
        int deadParticles = poolSize - livingParticles;
        ParticlePool testPool = new ParticlePool(poolSize);
        ParticlePoolTest.fillWithRandom(testPool, livingParticles);
        ParticlePool comparisonPool = ParticlePoolTest.createCopy(testPool);
        // kill particle 3
        testPool.moveDeceasedParticle(3);
        livingParticles--;
        deadParticles++;
        Assert.assertEquals(poolSize, testPool.size());
        Assert.assertEquals(livingParticles, testPool.livingParticles());
        Assert.assertEquals(deadParticles, testPool.deadParticles());
        ParticlePoolTest.assertEqualParticles(comparisonPool, 6, testPool, 3, 1.0E-6F);
        // kill particle 0
        testPool.moveDeceasedParticle(0);
        livingParticles--;
        deadParticles++;
        Assert.assertEquals(poolSize, testPool.size());
        Assert.assertEquals(livingParticles, testPool.livingParticles());
        Assert.assertEquals(deadParticles, testPool.deadParticles());
        ParticlePoolTest.assertEqualParticles(comparisonPool, 5, testPool, 0, 1.0E-6F);
        // test it with a pool of length one (degenerate case)
        poolSize = 1;
        testPool = new ParticlePool(poolSize);
        ParticlePoolTest.fillWithRandom(testPool, 1);
        livingParticles = 1;
        deadParticles = 0;
        testPool.moveDeceasedParticle(0);
        livingParticles--;
        deadParticles++;
        Assert.assertEquals(poolSize, testPool.size());
        Assert.assertEquals(livingParticles, testPool.livingParticles());
        Assert.assertEquals(deadParticles, testPool.deadParticles());
        // there are no living particles to compare here
    }
}

