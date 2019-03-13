/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.crdt;


import java.util.List;
import java.util.Random;
import org.junit.Test;


public abstract class AbstractCRDTPropertyTest<C extends CRDT<C>, H, S> {
    private final Random random = new Random();

    private int numCounters;

    @Test
    public void strongEventualConsistency() {
        final int rounds = 1000;
        for (int i = 0; i < rounds; i++) {
            final int operationCount = 50 + (random.nextInt(50));
            final List<C> crdts = setupCRDTs(numCounters);
            final H state = getStateHolder();
            final Operation<C, H>[] operations = generateOperations(operationCount);
            for (Operation<C, H> operation : operations) {
                operation.perform(crdts, state);
            }
            mergeAll(crdts);
            assertState(crdts, state, numCounters, operations);
        }
    }
}

