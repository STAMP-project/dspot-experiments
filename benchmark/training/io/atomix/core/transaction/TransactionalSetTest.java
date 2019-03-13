/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.core.transaction;


import CommitStatus.FAILURE;
import CommitStatus.SUCCESS;
import Isolation.REPEATABLE_READS;
import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.core.set.DistributedSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Transactional set test.
 */
public class TransactionalSetTest extends AbstractPrimitiveTest {
    @Test
    public void testTransactionalSet() throws Throwable {
        Transaction transaction1 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction1.begin();
        TransactionalSet<String> set1 = transaction1.<String>setBuilder("test-transactional-set").withProtocol(protocol()).build();
        Transaction transaction2 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction2.begin();
        TransactionalSet<String> set2 = transaction2.<String>setBuilder("test-transactional-set").withProtocol(protocol()).build();
        try {
            Assert.assertFalse(set1.contains("foo"));
            set1.add("foo");
            Assert.assertTrue(set1.contains("foo"));
        } finally {
            Assert.assertEquals(SUCCESS, transaction1.commit());
        }
        try {
            Assert.assertTrue(set2.contains("foo"));
            Assert.assertFalse(set2.contains("bar"));
            Assert.assertTrue(set2.remove("foo"));
            Assert.assertFalse(set2.contains("foo"));
            Assert.assertTrue(set2.add("bar"));
            Assert.assertTrue(set2.contains("bar"));
        } finally {
            Assert.assertEquals(SUCCESS, transaction2.commit());
        }
        DistributedSet<String> set = atomix().<String>setBuilder("test-transactional-set").withProtocol(protocol()).build();
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.contains("bar"));
        Assert.assertEquals(1, set.size());
        Transaction transaction3 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction3.begin();
        TransactionalSet<String> set3 = transaction3.<String>setBuilder("test-transactional-set").withProtocol(protocol()).build();
        Transaction transaction4 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction4.begin();
        TransactionalSet<String> set4 = transaction4.<String>setBuilder("test-transactional-set").withProtocol(protocol()).build();
        Assert.assertTrue(set3.add("foo"));
        Assert.assertTrue(set4.add("foo"));
        Assert.assertTrue(set3.remove("bar"));
        Assert.assertFalse(set4.add("bar"));
        Assert.assertEquals(SUCCESS, transaction3.commit());
        Assert.assertEquals(FAILURE, transaction4.commit());
    }
}

