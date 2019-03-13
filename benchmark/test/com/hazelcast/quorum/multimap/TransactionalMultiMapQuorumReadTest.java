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
package com.hazelcast.quorum.multimap;


import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.quorum.AbstractQuorumTest;
import com.hazelcast.quorum.QuorumType;
import com.hazelcast.test.HazelcastSerialParametersRunnerFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastSerialParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class TransactionalMultiMapQuorumReadTest extends AbstractQuorumTest {
    @Parameterized.Parameter(0)
    public static TransactionOptions options;

    @Parameterized.Parameter(1)
    public static QuorumType quorumType;

    @Test
    public void txSize_successful_whenQuorumSize_met() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.size();
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void txSize_failing_whenQuorumSize_notMet() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.size();
        transactionContext.commitTransaction();
    }

    @Test
    public void txGet_successful_whenQuorumSize_met() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.get("123");
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void txGet_failing_whenQuorumSize_notMet() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.get("123");
        transactionContext.commitTransaction();
    }

    @Test
    public void txValuesCount_successful_whenQuorumSize_met() {
        TransactionContext transactionContext = newTransactionContext(0);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.valueCount("123");
        transactionContext.commitTransaction();
    }

    @Test(expected = TransactionException.class)
    public void txValuesCount_failing_whenQuorumSize_notMet() {
        TransactionContext transactionContext = newTransactionContext(3);
        transactionContext.beginTransaction();
        TransactionalMultiMap<Object, Object> map = transactionContext.getMultiMap(((AbstractQuorumTest.MULTI_MAP_NAME) + (TransactionalMultiMapQuorumReadTest.quorumType.name())));
        map.valueCount("123");
        transactionContext.commitTransaction();
    }
}

