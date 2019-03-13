/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.tools.rumen;


import org.junit.Assert;
import org.junit.Test;


public class TestRandomSeedGenerator {
    @Test
    public void testSeedGeneration() {
        long masterSeed1 = 42;
        long masterSeed2 = 43;
        Assert.assertTrue("Deterministic seeding", ((RandomSeedGenerator.getSeed("stream1", masterSeed1)) == (RandomSeedGenerator.getSeed("stream1", masterSeed1))));
        Assert.assertTrue("Deterministic seeding", ((RandomSeedGenerator.getSeed("stream2", masterSeed2)) == (RandomSeedGenerator.getSeed("stream2", masterSeed2))));
        Assert.assertTrue("Different streams", ((RandomSeedGenerator.getSeed("stream1", masterSeed1)) != (RandomSeedGenerator.getSeed("stream2", masterSeed1))));
        Assert.assertTrue("Different master seeds", ((RandomSeedGenerator.getSeed("stream1", masterSeed1)) != (RandomSeedGenerator.getSeed("stream1", masterSeed2))));
    }
}

