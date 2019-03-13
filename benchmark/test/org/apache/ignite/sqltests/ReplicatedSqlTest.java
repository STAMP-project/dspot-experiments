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
package org.apache.ignite.sqltests;


import org.junit.Test;


/**
 * Includes all base sql test plus tests that make sense in replicated mode.
 */
public class ReplicatedSqlTest extends BaseSqlTest {
    /**
     * Name of the department table created in partitioned mode.
     */
    private String DEP_PART_TAB = "DepartmentPart";

    /**
     * Checks distributed INNER JOIN of replicated and replicated tables.
     */
    @Test
    public void testInnerDistributedJoinReplicatedReplicated() {
        checkInnerDistJoinWithReplicated(DEP_TAB);
    }

    /**
     * Checks distributed INNER JOIN of partitioned and replicated tables.
     */
    @Test
    public void testInnerDistJoinPartitionedReplicated() {
        checkInnerDistJoinWithReplicated(DEP_PART_TAB);
    }

    /**
     * Checks distributed INNER JOIN of replicated and partitioned tables.
     */
    @Test
    public void testMixedInnerDistJoinReplicatedPartitioned() {
        checkInnerDistJoinReplicatedWith(DEP_PART_TAB);
    }

    /**
     * Checks distributed LEFT JOIN of replicated and replicated tables.
     */
    @Test
    public void testLeftDistributedJoinReplicatedReplicated() {
        checkLeftDistributedJoinWithReplicated(DEP_TAB);
    }

    /**
     * Checks distributed LEFT JOIN of partitioned and replicated tables.
     */
    @Test
    public void testLeftDistributedJoinPartitionedReplicated() {
        BaseSqlTest.setExplain(true);
        checkLeftDistributedJoinWithReplicated(DEP_PART_TAB);
    }

    /**
     * Checks distributed RIGHT JOIN of replicated and replicated tables.
     */
    @Test
    public void testRightDistributedJoinReplicatedReplicated() {
        checkRightDistributedJoinWithReplicated(DEP_TAB);
    }

    /**
     * Checks distributed RIGHT JOIN of replicated and partitioned tables.
     */
    @Test
    public void testRightDistributedJoinReplicatedPartitioned() {
        BaseSqlTest.setExplain(true);
        checkRightDistributedJoinReplicatedWith(DEP_PART_TAB);
    }

    /**
     * Check INNER JOIN with collocated data of replicated and partitioned tables.
     */
    @Test
    public void testInnerJoinReplicatedPartitioned() {
        checkInnerJoinEmployeeDepartment(DEP_PART_TAB);
    }

    /**
     * Check INNER JOIN with collocated data of partitioned and replicated tables.
     */
    @Test
    public void testInnerJoinPartitionedReplicated() {
        checkInnerJoinDepartmentEmployee(DEP_PART_TAB);
    }

    /**
     * Check LEFT JOIN with collocated data of partitioned and replicated tables.
     */
    @Test
    public void testLeftJoinPartitionedReplicated() {
        checkLeftJoinDepartmentEmployee(DEP_PART_TAB);
    }

    /**
     * Check RIGHT JOIN with collocated data of replicated and partitioned tables.
     */
    @Test
    public void testRightJoinReplicatedPartitioned() {
        checkRightJoinEmployeeDepartment(DEP_PART_TAB);
    }
}

