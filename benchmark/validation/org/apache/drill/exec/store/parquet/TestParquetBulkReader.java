/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.parquet;


import org.apache.drill.test.ClusterTest;
import org.junit.Test;


/**
 * Tests the Parquet bulk reader
 */
public class TestParquetBulkReader extends ClusterTest {
    private static final String DATAFILE = "cp.`parquet/fourvarchar_asc_nulls.parquet`";

    /**
     * Load variable length data which has nulls
     */
    @Test
    public void testNullCount() throws Exception {
        try {
            alterSession();
            testBuilder().sqlQuery("select count(*) as c from %s where VarbinaryValue3 is null", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(71L).go();
            testBuilder().sqlQuery("select count(*) as c from %s where VarbinaryValue1 is null", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(44L).go();
        } finally {
            resetSession();
        }
    }

    /**
     * Load variable length data which has non-nulls data
     */
    @Test
    public void testNotNullCount() throws Exception {
        try {
            alterSession();
            testBuilder().sqlQuery("select count(*) as c from %s where VarbinaryValue3 is not null", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(0L).go();
            testBuilder().sqlQuery("select count(*) as c from %s where VarbinaryValue1 is not null", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(27L).go();
        } finally {
            resetSession();
        }
    }

    /**
     * Load variable columns with fixed length data with large precision and null values
     */
    @Test
    public void testFixedLengthWithLargePrecisionAndNulls() throws Exception {
        try {
            alterSession();
            testBuilder().sqlQuery("select count(*) as c from %s where index < 50 and length(VarbinaryValue1) = 400", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(25L).go();
        } finally {
            resetSession();
        }
    }

    /**
     * Load variable length data which was originally fixed length and then became variable length
     */
    @Test
    public void testFixedLengthToVarlen() throws Exception {
        try {
            alterSession();
            testBuilder().sqlQuery("select count(*) as c from %s where index < 60 and length(VarbinaryValue1) <= 800", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(27L).go();
        } finally {
            resetSession();
        }
    }

    /**
     * Load variable length data with values larger than chunk size (4k)
     */
    @Test
    public void testLargeVarlen() throws Exception {
        try {
            alterSession();
            testBuilder().sqlQuery("select count(*) as c from %s where length(VarbinaryValue2) = 4500", TestParquetBulkReader.DATAFILE).unOrdered().baselineColumns("c").baselineValues(19L).go();
        } finally {
            resetSession();
        }
    }
}

