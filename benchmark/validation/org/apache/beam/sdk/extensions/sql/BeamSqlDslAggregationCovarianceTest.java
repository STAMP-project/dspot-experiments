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


import org.apache.beam.sdk.extensions.sql.utils.RowAsserts;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for {@code COVAR_POP} and {@code COVAR_SAMP}.
 */
public class BeamSqlDslAggregationCovarianceTest {
    private static final double PRECISION = 1.0E-7;

    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    private PCollection<Row> boundedInput;

    @Test
    public void testPopulationVarianceDouble() {
        String sql = "SELECT COVAR_POP(f_double1, f_double2) FROM PCOLLECTION GROUP BY f_int3";
        PAssert.that(boundedInput.apply(SqlTransform.query(sql))).satisfies(RowAsserts.matchesScalar(1.84, BeamSqlDslAggregationCovarianceTest.PRECISION));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testPopulationVarianceInt() {
        String sql = "SELECT COVAR_POP(f_int1, f_int2) FROM PCOLLECTION GROUP BY f_int3";
        PAssert.that(boundedInput.apply(SqlTransform.query(sql))).satisfies(RowAsserts.matchesScalar(1));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testSampleVarianceDouble() {
        String sql = "SELECT COVAR_SAMP(f_double1, f_double2) FROM PCOLLECTION GROUP BY f_int3";
        PAssert.that(boundedInput.apply(SqlTransform.query(sql))).satisfies(RowAsserts.matchesScalar(2.3, BeamSqlDslAggregationCovarianceTest.PRECISION));
        pipeline.run().waitUntilFinish();
    }

    @Test
    public void testSampleVarianceInt() {
        String sql = "SELECT COVAR_SAMP(f_int1, f_int2) FROM PCOLLECTION GROUP BY f_int3";
        PAssert.that(boundedInput.apply(SqlTransform.query(sql))).satisfies(RowAsserts.matchesScalar(2));
        pipeline.run().waitUntilFinish();
    }
}

