/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase;


import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * This Integration Test verifies acid guarantees across column families by frequently writing
 * values to rows with multiple column families and concurrently reading entire rows that expect all
 * column families.
 * <p>
 * Sample usage:
 *
 * <pre>
 * hbase org.apache.hadoop.hbase.IntegrationTestAcidGuarantees -Dmillis=10000 -DnumWriters=50
 * -DnumGetters=2 -DnumScanners=2 -DnumUniqueRows=5
 * </pre>
 */
@Category(IntegrationTests.class)
public class IntegrationTestAcidGuarantees extends IntegrationTestBase {
    private static final int SERVER_COUNT = 1;// number of slaves for the smallest cluster


    // The unit test version.
    AcidGuaranteesTestTool tool;

    // ***** Actual integration tests
    @Test
    public void testGetAtomicity() throws Exception {
        runTestAtomicity(20000, 4, 4, 0, 3);
    }

    @Test
    public void testScanAtomicity() throws Exception {
        runTestAtomicity(20000, 3, 0, 2, 3);
    }

    @Test
    public void testMixedAtomicity() throws Exception {
        runTestAtomicity(20000, 4, 2, 2, 3);
    }
}

