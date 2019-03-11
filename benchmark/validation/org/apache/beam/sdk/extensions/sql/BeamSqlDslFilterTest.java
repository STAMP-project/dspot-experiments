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
package org.apache.beam.sdk.extensions.sql;


import org.apache.beam.sdk.extensions.sql.impl.ParseException;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;


/**
 * Tests for WHERE queries with BOUNDED PCollection.
 */
public class BeamSqlDslFilterTest extends BeamSqlDslBase {
    /**
     * single filter with bounded PCollection.
     */
    @Test
    public void testSingleFilterWithBounded() throws Exception {
        runSingleFilter(boundedInput1);
    }

    /**
     * single filter with unbounded PCollection.
     */
    @Test
    public void testSingleFilterWithUnbounded() throws Exception {
        runSingleFilter(unboundedInput1);
    }

    /**
     * composite filters with bounded PCollection.
     */
    @Test
    public void testCompositeFilterWithBounded() throws Exception {
        runCompositeFilter(boundedInput1);
    }

    /**
     * composite filters with unbounded PCollection.
     */
    @Test
    public void testCompositeFilterWithUnbounded() throws Exception {
        runCompositeFilter(unboundedInput1);
    }

    /**
     * nothing return with filters in bounded PCollection.
     */
    @Test
    public void testNoReturnFilterWithBounded() throws Exception {
        runNoReturnFilter(boundedInput1);
    }

    /**
     * nothing return with filters in unbounded PCollection.
     */
    @Test
    public void testNoReturnFilterWithUnbounded() throws Exception {
        runNoReturnFilter(unboundedInput1);
    }

    @Test
    public void testFromInvalidTableName1() throws Exception {
        exceptions.expect(ParseException.class);
        exceptions.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Object 'TABLE_B' not found")));
        pipeline.enableAbandonedNodeEnforcement(false);
        String sql = "SELECT * FROM TABLE_B WHERE f_int < 1";
        PCollectionTuple.of(new org.apache.beam.sdk.values.TupleTag("TABLE_A"), boundedInput1).apply("testFromInvalidTableName1", SqlTransform.query(sql));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testInvalidFilter() throws Exception {
        exceptions.expect(ParseException.class);
        exceptions.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Column 'f_int_na' not found in any table")));
        pipeline.enableAbandonedNodeEnforcement(false);
        String sql = "SELECT * FROM PCOLLECTION WHERE f_int_na = 0";
        boundedInput1.apply(SqlTransform.query(sql));
        pipeline.run().waitUntilFinish();
    }
}

