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
package com.hazelcast.journal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.function.Function;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;


/**
 * Base class for implementing data-structure specific event journal test where the journal is expiring.
 *
 * @param <EJ_TYPE>
 * 		the type of the event journal event
 */
public abstract class AbstractEventJournalExpiringTest<EJ_TYPE> extends HazelcastTestSupport {
    private static final Random RANDOM = new Random();

    protected HazelcastInstance[] instances;

    private int partitionId;

    private TruePredicate<EJ_TYPE> TRUE_PREDICATE = new TruePredicate<EJ_TYPE>();

    private Function<EJ_TYPE, EJ_TYPE> IDENTITY_FUNCTION = new IdentityFunction<EJ_TYPE>();

    @Test
    public void skipsEventsWhenExpired() throws Throwable {
        init();
        final EventJournalTestContext<String, Integer, EJ_TYPE> context = createContext();
        String key = randomPartitionKey();
        final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
        readFromJournal(context, exception, 0);
        for (int i = 0; i < 100000; i++) {
            context.dataAdapter.put(key, i);
            if ((exception.get()) != null) {
                throw exception.get();
            }
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1));
        }
    }
}

