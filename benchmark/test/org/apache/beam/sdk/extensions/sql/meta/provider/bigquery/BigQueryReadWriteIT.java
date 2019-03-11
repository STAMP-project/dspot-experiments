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
package org.apache.beam.sdk.extensions.sql.meta.provider.bigquery;


import CalciteUtils.TIMESTAMP;
import State.DONE;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.PipelineResult.State;
import org.apache.beam.sdk.extensions.sql.impl.BeamSqlEnv;
import org.apache.beam.sdk.extensions.sql.impl.rel.BeamSqlRelUtils;
import org.apache.beam.sdk.extensions.sql.utils.DateTimeUtils;
import org.apache.beam.sdk.io.gcp.bigquery.TestBigQuery;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests form writing to BigQuery with Beam SQL.
 */
@RunWith(JUnit4.class)
public class BigQueryReadWriteIT implements Serializable {
    private static final Schema SOURCE_SCHEMA = Schema.builder().addNullableField("id", FieldType.INT64).addNullableField("name", FieldType.STRING).addNullableField("arr", org.apache.beam.sdk.schemas.Schema.FieldType.array(FieldType.STRING)).build();

    private static final Schema SOURCE_SCHEMA_TWO = Schema.builder().addNullableField("c_bigint", FieldType.INT64).addNullableField("c_tinyint", FieldType.BYTE).addNullableField("c_smallint", FieldType.INT16).addNullableField("c_integer", FieldType.INT32).addNullableField("c_float", FieldType.FLOAT).addNullableField("c_double", FieldType.DOUBLE).addNullableField("c_boolean", FieldType.BOOLEAN).addNullableField("c_timestamp", TIMESTAMP).addNullableField("c_varchar", FieldType.STRING).addNullableField("c_char", FieldType.STRING).addNullableField("c_arr", org.apache.beam.sdk.schemas.Schema.FieldType.array(FieldType.STRING)).build();

    @Rule
    public transient TestPipeline pipeline = TestPipeline.create();

    @Rule
    public transient TestPipeline readPipeline = TestPipeline.create();

    @Rule
    public transient TestBigQuery bigQuery = TestBigQuery.create(BigQueryReadWriteIT.SOURCE_SCHEMA);

    @Rule
    public transient TestBigQuery bigQueryTestingTypes = TestBigQuery.create(BigQueryReadWriteIT.SOURCE_SCHEMA_TWO);

    @Test
    public void testSQLRead() {
        BeamSqlEnv sqlEnv = BeamSqlEnv.inMemory(new BigQueryTableProvider());
        String createTableStatement = (("CREATE EXTERNAL TABLE TEST( \n" + ((((((((((((("   c_bigint BIGINT, \n" + "   c_tinyint TINYINT, \n") + "   c_smallint SMALLINT, \n") + "   c_integer INTEGER, \n") + "   c_float FLOAT, \n") + "   c_double DOUBLE, \n") + "   c_boolean BOOLEAN, \n") + "   c_timestamp TIMESTAMP, \n") + "   c_varchar VARCHAR, \n ") + "   c_char CHAR, \n") + "   c_arr ARRAY<VARCHAR> \n") + ") \n") + "TYPE \'bigquery\' \n") + "LOCATION '")) + (bigQueryTestingTypes.tableSpec())) + "'";
        sqlEnv.executeDdl(createTableStatement);
        String insertStatement = "INSERT INTO TEST VALUES (" + ((((((((((("9223372036854775807, " + "127, ") + "32767, ") + "2147483647, ") + "1.0, ") + "1.0, ") + "TRUE, ") + "TIMESTAMP '2018-05-28 20:17:40.123', ") + "'varchar', ") + "'char', ") + "ARRAY['123', '456']") + ")");
        sqlEnv.parseQuery(insertStatement);
        BeamSqlRelUtils.toPCollection(pipeline, sqlEnv.parseQuery(insertStatement));
        pipeline.run().waitUntilFinish(Duration.standardMinutes(5));
        String selectTableStatement = "SELECT * FROM TEST";
        PCollection<Row> output = BeamSqlRelUtils.toPCollection(readPipeline, sqlEnv.parseQuery(selectTableStatement));
        PAssert.that(output).containsInAnyOrder(row(BigQueryReadWriteIT.SOURCE_SCHEMA_TWO, 9223372036854775807L, ((byte) (127)), ((short) (32767)), 2147483647, ((float) (1.0)), 1.0, true, DateTimeUtils.parseTimestampWithUTCTimeZone("2018-05-28 20:17:40.123"), "varchar", "char", Arrays.asList("123", "456")));
        PipelineResult.State state = readPipeline.run().waitUntilFinish(Duration.standardMinutes(5));
        Assert.assertEquals(state, DONE);
    }

    @Test
    public void testSQLTypes() {
        BeamSqlEnv sqlEnv = BeamSqlEnv.inMemory(new BigQueryTableProvider());
        String createTableStatement = (("CREATE EXTERNAL TABLE TEST( \n" + ((((((((((((("   c_bigint BIGINT, \n" + "   c_tinyint TINYINT, \n") + "   c_smallint SMALLINT, \n") + "   c_integer INTEGER, \n") + "   c_float FLOAT, \n") + "   c_double DOUBLE, \n") + "   c_boolean BOOLEAN, \n") + "   c_timestamp TIMESTAMP, \n") + "   c_varchar VARCHAR, \n ") + "   c_char CHAR, \n") + "   c_arr ARRAY<VARCHAR> \n") + ") \n") + "TYPE \'bigquery\' \n") + "LOCATION '")) + (bigQueryTestingTypes.tableSpec())) + "'";
        sqlEnv.executeDdl(createTableStatement);
        String insertStatement = "INSERT INTO TEST VALUES (" + ((((((((((("9223372036854775807, " + "127, ") + "32767, ") + "2147483647, ") + "1.0, ") + "1.0, ") + "TRUE, ") + "TIMESTAMP '2018-05-28 20:17:40.123', ") + "'varchar', ") + "'char', ") + "ARRAY['123', '456']") + ")");
        sqlEnv.parseQuery(insertStatement);
        BeamSqlRelUtils.toPCollection(pipeline, sqlEnv.parseQuery(insertStatement));
        pipeline.run().waitUntilFinish(Duration.standardMinutes(5));
        Assert.assertThat(bigQueryTestingTypes.getFlatJsonRows(BigQueryReadWriteIT.SOURCE_SCHEMA_TWO), Matchers.containsInAnyOrder(row(BigQueryReadWriteIT.SOURCE_SCHEMA_TWO, 9223372036854775807L, ((byte) (127)), ((short) (32767)), 2147483647, ((float) (1.0)), 1.0, true, DateTimeUtils.parseTimestampWithUTCTimeZone("2018-05-28 20:17:40.123"), "varchar", "char", Arrays.asList("123", "456"))));
    }

    @Test
    public void testInsertSelect() throws Exception {
        BeamSqlEnv sqlEnv = BeamSqlEnv.inMemory(readOnlyTableProvider(pipeline, "ORDERS_IN_MEMORY", row(BigQueryReadWriteIT.SOURCE_SCHEMA, 1L, "foo", Arrays.asList("111", "aaa")), row(BigQueryReadWriteIT.SOURCE_SCHEMA, 2L, "bar", Arrays.asList("222", "bbb")), row(BigQueryReadWriteIT.SOURCE_SCHEMA, 3L, "baz", Arrays.asList("333", "ccc"))), new BigQueryTableProvider());
        String createTableStatement = (("CREATE EXTERNAL TABLE ORDERS_BQ( \n" + ((((("   id BIGINT, \n" + "   name VARCHAR, \n ") + "   arr ARRAY<VARCHAR> \n") + ") \n") + "TYPE \'bigquery\' \n") + "LOCATION '")) + (bigQuery.tableSpec())) + "'";
        sqlEnv.executeDdl(createTableStatement);
        String insertStatement = "INSERT INTO ORDERS_BQ \n" + ((((" SELECT \n" + "    id as `id`, \n") + "    name as `name`, \n") + "    arr as `arr` \n") + " FROM ORDERS_IN_MEMORY");
        BeamSqlRelUtils.toPCollection(pipeline, sqlEnv.parseQuery(insertStatement));
        pipeline.run().waitUntilFinish(Duration.standardMinutes(5));
        List<Row> allJsonRows = bigQuery.getFlatJsonRows(BigQueryReadWriteIT.SOURCE_SCHEMA);
        Assert.assertThat(allJsonRows, Matchers.containsInAnyOrder(row(BigQueryReadWriteIT.SOURCE_SCHEMA, 1L, "foo", Arrays.asList("111", "aaa")), row(BigQueryReadWriteIT.SOURCE_SCHEMA, 2L, "bar", Arrays.asList("222", "bbb")), row(BigQueryReadWriteIT.SOURCE_SCHEMA, 3L, "baz", Arrays.asList("333", "ccc"))));
    }
}

