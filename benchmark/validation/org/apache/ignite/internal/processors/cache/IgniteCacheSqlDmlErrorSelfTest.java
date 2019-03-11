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
package org.apache.ignite.internal.processors.cache;


import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Negative java API tests for dml queries (insert, merge, update).
 */
public class IgniteCacheSqlDmlErrorSelfTest extends GridCommonAbstractTest {
    /**
     * Dummy cache, just cache api entry point.
     */
    private static IgniteCache<?, ?> cache;

    /**
     * Check it's forbidden to specify any two of _key, _key alias or key field (column that belongs to key) together in
     * the insert/merge dml statement. Same constraints are right for (_val, _val alias, val fields).
     */
    @Test
    public void testInsertMixingPlaceholderAndFields() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO COMPOSITE (_key, id2, name1, name2) VALUES (?, ?, ?, ?)", new IgniteCacheSqlDmlErrorSelfTest.CompositeKey(), 42, "name#1", "name#2"), "Column _KEY refers to entire key cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO COMPOSITE (id1, id2, _val, name2) VALUES (?, ?, ?, ?)", 1, 2, new IgniteCacheSqlDmlErrorSelfTest.CompositeValue(), "name#2"), "Column _VAL refers to entire value cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (_key, id, name) VALUES (?, ?, ?)", 42, 43, "some name"), "Columns _KEY and ID both refer to entire cache key object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (_key, _val, name) VALUES (?, ?, ?)", 42, "name#1", "name#2"), "Columns _VAL and NAME both refer to entire cache value object.");
        // And the same asserts for the MERGE:
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO COMPOSITE (_key, id2, name1, name2) VALUES (?, ?, ?, ?)", new IgniteCacheSqlDmlErrorSelfTest.CompositeKey(), 42, "name#1", "name#2"), "Column _KEY refers to entire key cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO COMPOSITE (id1, id2, _val, name2) VALUES (?, ?, ?, ?)", 1, 2, new IgniteCacheSqlDmlErrorSelfTest.CompositeValue(), "name#2"), "Column _VAL refers to entire value cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (_key, id, name) VALUES (?, ?, ?)", 42, 43, "some name"), "Columns _KEY and ID both refer to entire cache key object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (_key, _val, name) VALUES (?, ?, ?)", 42, "name#1", "name#2"), "Columns _VAL and NAME both refer to entire cache value object.");
    }

    /**
     * Check it's forbidden to specify any two of _key, _key alias or key field (column that belongs to key) together in
     * the COPY (aka bulk load) sql statement. Same constraints are right for (_val, _val alias, val fields).
     */
    @Test
    public void testCopyMixingPlaceholderAndFields() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute(("COPY FROM \'stub/file/path\' " + "INTO SIMPLE (_key, id, name) FORMAT CSV")), "Columns _KEY and ID both refer to entire cache key object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute(("COPY FROM \'stub/file/path\' " + "INTO SIMPLE_WRAPPED (_key, id, name) FORMAT CSV")), "Column _KEY refers to entire key cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute(("COPY FROM \'stub/file/path\' " + "INTO SIMPLE (id, _val, name) FORMAT CSV")), "Columns _VAL and NAME both refer to entire cache value object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute(("COPY FROM \'stub/file/path\' " + "INTO SIMPLE_WRAPPED (id, _val, name) FORMAT CSV")), "Column _VAL refers to entire value cache object.");
    }

    /**
     * Check update statements that modify any two of _val, _val alias or val field (column that belongs to cache value
     * object) are forbidden.
     */
    @Test
    public void testUpdateMixingValueAndValueFields() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE COMPOSITE SET _val = ?, name2 = ?", new IgniteCacheSqlDmlErrorSelfTest.CompositeValue(), "name#2"), "Column _VAL refers to entire value cache object.");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET _val = ?, name = ?", "name#1", "name#2"), "Columns _VAL and NAME both refer to entire cache value object.");
    }

    /**
     * Check that null values for entire key or value are disallowed.
     */
    @Test
    public void testInsertNullKeyValue() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO COMPOSITE (_key, _val) VALUES (?, ?)", null, new IgniteCacheSqlDmlErrorSelfTest.CompositeKey()), "Key for INSERT, COPY, or MERGE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO COMPOSITE (_key, _val) VALUES (?, ?)", new IgniteCacheSqlDmlErrorSelfTest.CompositeKey(), null), "Value for INSERT, COPY, MERGE, or UPDATE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (_key, _val) VALUES(?, ?)", null, "name#1"), "Null value is not allowed for column 'ID'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (id, _val) VALUES(?, ?)", null, "name#1"), "Null value is not allowed for column 'ID'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (_key, _val) VALUES(?, ?)", 42, null), "Null value is not allowed for column 'NAME'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("INSERT INTO SIMPLE (_key, name) VALUES(?, ?)", 42, null), "Null value is not allowed for column 'NAME'");
        // And the same checks for the MERGE:
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO COMPOSITE (_key, _val) VALUES (?, ?)", null, new IgniteCacheSqlDmlErrorSelfTest.CompositeKey()), "Key for INSERT, COPY, or MERGE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO COMPOSITE (_key, _val) VALUES (?, ?)", new IgniteCacheSqlDmlErrorSelfTest.CompositeKey(), null), "Value for INSERT, COPY, MERGE, or UPDATE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (_key, _val) VALUES(?, ?)", null, "name#1"), "Null value is not allowed for column 'ID'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (id, _val) VALUES(?, ?)", null, "name#1"), "Null value is not allowed for column 'ID'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (_key, _val) VALUES(?, ?)", 42, null), "Null value is not allowed for column 'NAME'");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("MERGE INTO SIMPLE (_key, name) VALUES(?, ?)", 42, null), "Null value is not allowed for column 'NAME'");
    }

    /**
     * Check that updates of key or key fields are disallowed.
     */
    @Test
    public void testUpdateKey() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE COMPOSITE SET _key = ?, _val = ?", new IgniteCacheSqlDmlErrorSelfTest.CompositeKey(), new IgniteCacheSqlDmlErrorSelfTest.CompositeValue()), "SQL UPDATE can't modify key or its fields directly");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE COMPOSITE SET id1 = ?, _val = ?", 42, new IgniteCacheSqlDmlErrorSelfTest.CompositeValue()), "SQL UPDATE can't modify key or its fields directly");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET _key = ?, _val = ?", 42, "simple name"), "SQL UPDATE can't modify key or its fields directly");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET id = ?, _val = ?", 42, "simple name"), "SQL UPDATE can't modify key or its fields directly");
    }

    /**
     * Check that setting entire cache key to {@code null} via sql is forbidden.
     */
    @Test
    public void testUpdateKeyToNull() {
        // It's ok to assert just fact of failure if we update key to null.
        // Both reasons (the fact of updating key and setting _key to null) are correct.
        // Empty string is contained by any exception message.
        final String ANY_MESSAGE = "";
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE COMPOSITE SET _key = ?, _val = ?", null, new IgniteCacheSqlDmlErrorSelfTest.CompositeValue()), ANY_MESSAGE);
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET id = ?, _val = ?", null, "simple name"), ANY_MESSAGE);
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET id = ?, _val = ?", null, "simple name"), ANY_MESSAGE);
    }

    /**
     * Check that setting entire cache value to {@code null} via sql is forbidden.
     */
    @Test
    public void testUpdateValToNull() {
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE COMPOSITE SET _val = ?", ((Object) (null))), "New value for UPDATE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET _val = ?", ((Object) (null))), "New value for UPDATE must not be null");
        assertThrows(() -> IgniteCacheSqlDmlErrorSelfTest.execute("UPDATE SIMPLE SET name = ?", ((Object) (null))), "New value for UPDATE must not be null");
    }

    /**
     * Class which instance can be (de)serialized to(from) key object.
     */
    private static class CompositeKey {
        /**
         * First key field.
         */
        int id1;

        /**
         * Second key field.
         */
        int id2;

        /**
         * Constructs key with random fields.
         */
        public CompositeKey() {
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            id1 = rnd.nextInt();
            id2 = rnd.nextInt();
        }
    }

    /**
     * Class which instance can be (de)serialized to(from) value object.
     */
    private static class CompositeValue {
        /**
         * First value field.
         */
        String name1;

        /**
         * Second value field.
         */
        String name2;

        /**
         * Creates value with random fields.
         */
        public CompositeValue() {
            name1 = UUID.randomUUID().toString();
            name2 = UUID.randomUUID().toString();
        }
    }
}

