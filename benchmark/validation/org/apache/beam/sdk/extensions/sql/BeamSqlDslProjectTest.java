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
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.Row;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;


/**
 * Tests for field-project in queries with BOUNDED PCollection.
 */
public class BeamSqlDslProjectTest extends BeamSqlDslBase {
    /**
     * select all fields with bounded PCollection.
     */
    @Test
    public void testSelectAllWithBounded() throws Exception {
        runSelectAll(boundedInput2);
    }

    /**
     * select all fields with unbounded PCollection.
     */
    @Test
    public void testSelectAllWithUnbounded() throws Exception {
        runSelectAll(unboundedInput2);
    }

    /**
     * select partial fields with bounded PCollection.
     */
    @Test
    public void testPartialFieldsWithBounded() throws Exception {
        runPartialFields(boundedInput2);
    }

    /**
     * select partial fields with unbounded PCollection.
     */
    @Test
    public void testPartialFieldsWithUnbounded() throws Exception {
        runPartialFields(unboundedInput2);
    }

    /**
     * select partial fields for multiple rows with bounded PCollection.
     */
    @Test
    public void testPartialFieldsInMultipleRowWithBounded() throws Exception {
        runPartialFieldsInMultipleRow(boundedInput1);
    }

    /**
     * select partial fields for multiple rows with unbounded PCollection.
     */
    @Test
    public void testPartialFieldsInMultipleRowWithUnbounded() throws Exception {
        runPartialFieldsInMultipleRow(unboundedInput1);
    }

    /**
     * select partial fields with bounded PCollection.
     */
    @Test
    public void testPartialFieldsInRowsWithBounded() throws Exception {
        runPartialFieldsInRows(boundedInput1);
    }

    /**
     * select partial fields with unbounded PCollection.
     */
    @Test
    public void testPartialFieldsInRowsWithUnbounded() throws Exception {
        runPartialFieldsInRows(unboundedInput1);
    }

    /**
     * select literal field with bounded PCollection.
     */
    @Test
    public void testLiteralFieldWithBounded() throws Exception {
        runLiteralField(boundedInput2);
    }

    /**
     * select literal field with unbounded PCollection.
     */
    @Test
    public void testLiteralFieldWithUnbounded() throws Exception {
        runLiteralField(unboundedInput2);
    }

    @Test
    public void testProjectUnknownField() throws Exception {
        exceptions.expect(ParseException.class);
        exceptions.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("Column 'f_int_na' not found in any table")));
        pipeline.enableAbandonedNodeEnforcement(false);
        String sql = "SELECT f_int_na FROM TABLE_A";
        PCollection<Row> result = PCollectionTuple.of(new org.apache.beam.sdk.values.TupleTag("TABLE_A"), boundedInput1).apply("testProjectUnknownField", SqlTransform.query(sql));
        pipeline.run().waitUntilFinish();
    }
}

