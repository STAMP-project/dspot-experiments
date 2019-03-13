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


import org.junit.Test;


/**
 * Test case that uses multiple threads to read and write multifamily rows into a table, verifying
 * that reads never see partially-complete writes. This can run as a junit test, or with a main()
 * function which runs against a real cluster (eg for testing with failures, region movement, etc)
 */
public abstract class AcidGuaranteesTestBase {
    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private AcidGuaranteesTestTool tool = new AcidGuaranteesTestTool();

    @Test
    public void testGetAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 5, 0, 3);
    }

    @Test
    public void testScanAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 0, 5, 3);
    }

    @Test
    public void testMixedAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 2, 2, 3);
    }

    @Test
    public void testMobGetAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 5, 0, 3, true);
    }

    @Test
    public void testMobScanAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 0, 5, 3, true);
    }

    @Test
    public void testMobMixedAtomicity() throws Exception {
        runTestAtomicity(20000, 5, 2, 2, 3, true);
    }
}

