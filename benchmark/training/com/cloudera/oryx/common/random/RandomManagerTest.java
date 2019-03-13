/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.random;


import com.cloudera.oryx.common.OryxTest;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;


public final class RandomManagerTest extends OryxTest {
    @Test
    public void testUnseededRandomState() {
        RandomGenerator generator = RandomManager.getUnseededRandom();
        Assert.assertNotEquals(1553355631, generator.nextInt());
    }

    @Test
    public void testRandomState() {
        // Really, a test that the random generator state is reset in tests
        RandomGenerator generator = RandomManager.getRandom();
        Assert.assertEquals(1553355631, generator.nextInt());
        Assert.assertNotEquals(1553355631, generator.nextInt());
    }

    @Test
    public void testRandomStateWithSeed() {
        RandomGenerator generator = RandomManager.getRandom(1234L);
        Assert.assertEquals(35755635, generator.nextInt());
        Assert.assertNotEquals(35755635, generator.nextInt());
    }
}

