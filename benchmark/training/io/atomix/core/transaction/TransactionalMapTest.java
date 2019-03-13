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


import CommitStatus.SUCCESS;
import Isolation.REPEATABLE_READS;
import io.atomix.core.AbstractPrimitiveTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Transactional map test.
 */
public class TransactionalMapTest extends AbstractPrimitiveTest {
    @Test
    public void testTransactionalMap() throws Throwable {
        Transaction transaction1 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction1.begin();
        TransactionalMap<String, String> map1 = transaction1.<String, String>mapBuilder("test-map").withProtocol(protocol()).build();
        Transaction transaction2 = atomix().transactionBuilder().withIsolation(REPEATABLE_READS).build();
        transaction2.begin();
        TransactionalMap<String, String> map2 = transaction2.<String, String>mapBuilder("test-map").withProtocol(protocol()).build();
        try {
            Assert.assertNull(map1.get("foo"));
            map1.put("foo", "bar");
            Assert.assertEquals("bar", map1.get("foo"));
        } finally {
            Assert.assertEquals(SUCCESS, transaction1.commit());
        }
        try {
            Assert.assertEquals("bar", map2.get("foo"));
        } finally {
            Assert.assertEquals(SUCCESS, transaction2.commit());
        }
    }
}

