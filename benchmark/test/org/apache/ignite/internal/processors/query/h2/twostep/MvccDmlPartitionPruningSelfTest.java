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
package org.apache.ignite.internal.processors.query.h2.twostep;


import java.util.List;
import java.util.function.Consumer;
import org.apache.ignite.binary.BinaryObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for use partition pruning at the SELECT step of the UPDATE/DELETE statements execution.
 */
@SuppressWarnings("deprecation")
@RunWith(JUnit4.class)
public class MvccDmlPartitionPruningSelfTest extends AbstractPartitionPruningBaseTest {
    /**
     * Rows count for test tables.
     */
    private static final int ROWS = 10;

    /**
     * Recreate tables before each test statement.
     */
    private boolean recreateTables;

    /**
     * Test UPDATE statement.
     */
    @Test
    public void testUpdate() {
        recreateTables = false;
        recreateTables();
        // Key (not alias).
        execute("UPDATE t1 SET v1 = 'new1' WHERE k1 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "1"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "1"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 1);
        }, "1");
        // Key (alias).
        execute("UPDATE t1 SET v1 = 'new2' WHERE _KEY = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "2"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "2"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 1);
        }, "2");
        // Non-affinity key.
        execute("UPDATE t2 SET v2 = 'new1' WHERE k2 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertNoPartitions();
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 1);
        }, "1");
        // Affinity key.
        execute("UPDATE t2 SET v2 = 'new1' WHERE ak2 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t2", "1"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t2", "1"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 1);
        }, "1");
        // Expression: condition IN (...)
        execute("UPDATE t1 SET v1 = 'new1' WHERE k1 in (?, ?, ?)", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "1"), partition("t1", "2"), partition("t1", "3"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "1"), node("t1", "2"), node("t1", "3"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 3);
        }, "1", "2", "3");
        // Expression: logical
        execute("UPDATE t1 SET v1 = 'new1' WHERE k1 in (?, ?) or k1 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "1"), partition("t1", "2"), partition("t1", "3"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "1"), node("t1", "2"), node("t1", "3"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 3);
        }, "3", "2", "1");
        // No request (empty partitions).
        execute("UPDATE t1 SET v1 = 'new1' WHERE k1 in (?, ?) and k1 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertNoRequests();
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 0);
        }, "3", "2", "1");
        // Complex key.
        BinaryObject key = client().binary().builder("t2_key").setField("k1", "5").setField("ak2", "5").build();
        List<List<?>> res = executeSingle("UPDATE t2 SET v2 = 'new1' WHERE _KEY = ?", key);
        AbstractPartitionPruningBaseTest.assertPartitions(partition("t2", "5"));
        AbstractPartitionPruningBaseTest.assertNodes(node("t2", "5"));
        MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 1);
    }

    /**
     * Test UPDATE statement.
     */
    @Test
    public void testDelete() {
        recreateTables = true;
        // Expression: condition IN (...)
        execute("DELETE FROM t1 WHERE k1 in (?, ?, ?)", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "1"), partition("t1", "2"), partition("t1", "3"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "1"), node("t1", "2"), node("t1", "3"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 3);
        }, "1", "2", "3");
        // Expression: logical OR
        execute("DELETE FROM t1 WHERE k1 in (?, ?) or k1 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertPartitions(partition("t1", "1"), partition("t1", "2"), partition("t1", "3"));
            AbstractPartitionPruningBaseTest.assertNodes(node("t1", "1"), node("t1", "2"), node("t1", "3"));
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 3);
        }, "3", "2", "1");
        // No request (empty partitions).
        execute("DELETE FROM t1  WHERE k1 in (?, ?) and k1 = ?", ( res) -> {
            AbstractPartitionPruningBaseTest.assertNoRequests();
            MvccDmlPartitionPruningSelfTest.assertUpdatedRows(res, 0);
        }, "3", "2", "1");
    }
}

