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
package com.hazelcast.concurrent.idgen;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.collection.LongHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class IdGeneratorStressTest extends HazelcastTestSupport {
    private static final int THREAD_COUNT = 32;

    private static final int NUMBER_OF_IDS_PER_THREAD = 40000;

    private static final int TOTAL_ID_GENERATED = (IdGeneratorStressTest.THREAD_COUNT) * (IdGeneratorStressTest.NUMBER_OF_IDS_PER_THREAD);

    @Parameterized.Parameter(0)
    public int clusterSize;

    String name;

    HazelcastInstance[] instances;

    @Test
    public void testMultipleThreads() throws InterruptedException, ExecutionException {
        pickIdGenerator().init(13013);
        List<Future> futureList = new ArrayList<Future>(IdGeneratorStressTest.THREAD_COUNT);
        for (int i = 0; i < (IdGeneratorStressTest.THREAD_COUNT); i++) {
            IdGenerator idGenerator = pickIdGenerator();
            IdGeneratorStressTest.IdGeneratorCallable callable = new IdGeneratorStressTest.IdGeneratorCallable(idGenerator);
            Future<long[]> future = HazelcastTestSupport.spawn(callable);
            futureList.add(future);
        }
        LongHashSet totalGeneratedIds = new LongHashSet(IdGeneratorStressTest.TOTAL_ID_GENERATED, (-1));
        for (Future<long[]> future : futureList) {
            long[] generatedIds = future.get();
            for (long generatedId : generatedIds) {
                Assert.assertTrue(("ID: " + generatedId), totalGeneratedIds.add(generatedId));
            }
        }
        Assert.assertEquals(IdGeneratorStressTest.TOTAL_ID_GENERATED, totalGeneratedIds.size());
    }

    private static class IdGeneratorCallable implements Callable<long[]> {
        IdGenerator idGenerator;

        public IdGeneratorCallable(IdGenerator idGenerator) {
            this.idGenerator = idGenerator;
        }

        @Override
        public long[] call() throws Exception {
            long[] generatedIds = new long[IdGeneratorStressTest.NUMBER_OF_IDS_PER_THREAD];
            for (int j = 0; j < (IdGeneratorStressTest.NUMBER_OF_IDS_PER_THREAD); j++) {
                generatedIds[j] = idGenerator.newId();
            }
            return generatedIds;
        }
    }
}

