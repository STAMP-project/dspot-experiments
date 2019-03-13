/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing.eventstore.jdbc;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.util.UUID;
import junit.framework.TestCase;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the JdbcEventStorageEngine using the MySQL database.
 *
 * @author Albert Attard (JavaCreed)
 */
public class MysqlJdbcEventStorageEngineTest {
    private MysqlDataSource dataSource;

    private JdbcEventStorageEngine testSubject;

    /**
     * Issue #636 - The JdbcEventStorageEngine when used with the MySQL database
     * returns 0 instead of an empty optional when retrieving the last sequence
     * number for an aggregate that does not exist. This test replicates this
     * problem.
     */
    @Test
    public void testLoadLastSequenceNumber() {
        final String aggregateId = UUID.randomUUID().toString();
        testSubject.appendEvents(EventStoreTestUtils.createEvent(aggregateId, 0), EventStoreTestUtils.createEvent(aggregateId, 1));
        TestCase.assertEquals(1L, ((long) (testSubject.lastSequenceNumberFor(aggregateId).orElse((-1L)))));
        Assert.assertFalse(testSubject.lastSequenceNumberFor("inexistent").isPresent());
    }
}

