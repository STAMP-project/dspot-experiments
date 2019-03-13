/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.processor.idempotent.cassandra;


import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.camel.component.cassandra.BaseCassandraTest;
import org.apache.camel.component.cassandra.CassandraUnitUtils;
import org.cassandraunit.CassandraCQLUnit;
import org.junit.Rule;
import org.junit.Test;


/**
 * Unit test for {@link CassandraIdempotentRepository}
 */
public class CassandraIdempotentRepositoryTest extends BaseCassandraTest {
    @Rule
    public CassandraCQLUnit cassandraRule = CassandraUnitUtils.cassandraCQLUnit("IdempotentDataSet.cql");

    private Cluster cluster;

    private Session session;

    private CassandraIdempotentRepository idempotentRepository;

    @Test
    public void testAddNotExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Add_NotExists";
        assertFalse(exists(key));
        // When
        boolean result = idempotentRepository.add(key);
        // Then
        assertTrue(result);
        assertTrue(exists(key));
    }

    @Test
    public void testAddExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Add_Exists";
        assertTrue(exists(key));
        // When
        boolean result = idempotentRepository.add(key);
        // Then
        assertFalse(result);
        assertTrue(exists(key));
    }

    @Test
    public void testContainsNotExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Contains_NotExists";
        assertFalse(exists(key));
        // When
        boolean result = idempotentRepository.contains(key);
        // Then
        assertFalse(result);
    }

    @Test
    public void testContainsExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Contains_Exists";
        assertTrue(exists(key));
        // When
        boolean result = idempotentRepository.contains(key);
        // Then
        assertTrue(result);
    }

    @Test
    public void testRemoveNotExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Remove_NotExists";
        assertFalse(exists(key));
        // When
        boolean result = idempotentRepository.contains(key);
        // Then
        assertFalse(result);
    }

    @Test
    public void testRemoveExists() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Remove_Exists";
        assertTrue(exists(key));
        // When
        boolean result = idempotentRepository.remove(key);
        // Then
        assertTrue(result);
    }

    @Test
    public void testClear() {
        if (!(BaseCassandraTest.canTest())) {
            return;
        }
        // Given
        String key = "Remove_Exists";
        assertTrue(exists(key));
        // When
        idempotentRepository.clear();
        // Then
        assertFalse(idempotentRepository.contains(key));
    }
}

