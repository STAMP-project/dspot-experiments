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
package org.apache.beam.sdk.io.gcp.bigquery;


import Default.Boolean;
import Validation.Required;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableSchema;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.io.gcp.testing.BigqueryClient;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ExperimentalOptions;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.testing.DataflowPortabilityApiUnsupported;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test for BigqueryIO with DataflowRunner and DirectRunner.
 */
@RunWith(JUnit4.class)
public class BigQueryToTableIT {
    private static final Logger LOG = LoggerFactory.getLogger(BigQueryToTableIT.class);

    private static String project;

    private static final BigqueryClient BQ_CLIENT = new BigqueryClient("BigQueryToTableIT");

    private static final String BIG_QUERY_DATASET_ID = (("bq_query_to_table_" + (System.currentTimeMillis())) + "_") + (new SecureRandom().nextInt(32));

    private static final TableSchema LEGACY_QUERY_TABLE_SCHEMA = new TableSchema().setFields(ImmutableList.of(new TableFieldSchema().setName("fruit").setType("STRING")));

    private static final TableSchema NEW_TYPES_QUERY_TABLE_SCHEMA = new TableSchema().setFields(ImmutableList.of(new TableFieldSchema().setName("bytes").setType("BYTES"), new TableFieldSchema().setName("date").setType("DATE"), new TableFieldSchema().setName("time").setType("TIME")));

    private static final String NEW_TYPES_QUERY_TABLE_NAME = "types";

    private static final List<Map<String, Object>> NEW_TYPES_QUERY_TABLE_DATA = ImmutableList.of(ImmutableMap.of("bytes", "abc=", "date", "2000-01-01", "time", "00:00:00"), ImmutableMap.of("bytes", "dec=", "date", "3000-12-31", "time", "23:59:59.990000"), ImmutableMap.of("bytes", "xyw=", "date", "2011-01-01", "time", "23:59:59.999999"));

    private static final int MAX_RETRY = 5;

    /**
     * Customized PipelineOption for BigQueryToTable Pipeline.
     */
    public interface BigQueryToTableOptions extends ExperimentalOptions , TestPipelineOptions {
        @Description("The BigQuery query to be used for creating the source")
        @Validation.Required
        String getQuery();

        void setQuery(String query);

        @Description("BigQuery table to write to, specified as " + "<project_id>:<dataset_id>.<table_id>. The dataset must already exist.")
        @Validation.Required
        String getOutput();

        void setOutput(String value);

        @Description("BigQuery output table schema.")
        @Validation.Required
        TableSchema getOutputSchema();

        void setOutputSchema(TableSchema value);

        @Description("Whether to force reshuffle.")
        @Default.Boolean(false)
        boolean getReshuffle();

        void setReshuffle(boolean reshuffle);

        @Description("Whether to use the Standard SQL dialect when querying BigQuery.")
        @Default.Boolean(false)
        boolean getUsingStandardSql();

        void setUsingStandardSql(boolean usingStandardSql);
    }

    @Test
    public void testLegacyQueryWithoutReshuffle() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testLegacyQueryWithoutReshuffle";
        this.runBigQueryToTablePipeline(setupLegacyQueryTest(outputTable));
        this.verifyLegacyQueryRes(outputTable);
    }

    @Test
    public void testNewTypesQueryWithoutReshuffle() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testNewTypesQueryWithoutReshuffle";
        this.runBigQueryToTablePipeline(setupNewTypesQueryTest(outputTable));
        this.verifyNewTypesQueryRes(outputTable);
    }

    @Test
    public void testNewTypesQueryWithReshuffle() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testNewTypesQueryWithReshuffle";
        BigQueryToTableIT.BigQueryToTableOptions options = setupNewTypesQueryTest(outputTable);
        options.setReshuffle(true);
        this.runBigQueryToTablePipeline(options);
        this.verifyNewTypesQueryRes(outputTable);
    }

    @Test
    public void testStandardQueryWithoutCustom() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testStandardQueryWithoutCustom";
        this.runBigQueryToTablePipeline(setupStandardQueryTest(outputTable));
        this.verifyStandardQueryRes(outputTable);
    }

    @Test
    @Category(DataflowPortabilityApiUnsupported.class)
    public void testNewTypesQueryWithoutReshuffleWithCustom() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testNewTypesQueryWithoutReshuffleWithCustom";
        BigQueryToTableIT.BigQueryToTableOptions options = this.setupNewTypesQueryTest(outputTable);
        options.setExperiments(ImmutableList.of("enable_custom_bigquery_sink", "enable_custom_bigquery_source"));
        this.runBigQueryToTablePipeline(options);
        this.verifyNewTypesQueryRes(outputTable);
    }

    @Test
    @Category(DataflowPortabilityApiUnsupported.class)
    public void testLegacyQueryWithoutReshuffleWithCustom() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testLegacyQueryWithoutReshuffleWithCustom";
        BigQueryToTableIT.BigQueryToTableOptions options = this.setupLegacyQueryTest(outputTable);
        options.setExperiments(ImmutableList.of("enable_custom_bigquery_sink", "enable_custom_bigquery_source"));
        this.runBigQueryToTablePipeline(options);
        this.verifyLegacyQueryRes(outputTable);
    }

    @Test
    @Category(DataflowPortabilityApiUnsupported.class)
    public void testStandardQueryWithoutReshuffleWithCustom() throws Exception {
        final String outputTable = ((((BigQueryToTableIT.project) + ":") + (BigQueryToTableIT.BIG_QUERY_DATASET_ID)) + ".") + "testStandardQueryWithoutReshuffleWithCustom";
        BigQueryToTableIT.BigQueryToTableOptions options = this.setupStandardQueryTest(outputTable);
        options.setExperiments(ImmutableList.of("enable_custom_bigquery_sink", "enable_custom_bigquery_source"));
        this.runBigQueryToTablePipeline(options);
        this.verifyStandardQueryRes(outputTable);
    }
}

