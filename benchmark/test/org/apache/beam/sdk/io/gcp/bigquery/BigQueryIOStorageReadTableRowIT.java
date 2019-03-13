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


import Validation.Required;
import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.ExperimentalOptions;
import org.apache.beam.sdk.options.Validation;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.KV;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Integration tests for {@link BigQueryIO#readTableRows()} using {@link Method#DIRECT_READ} in
 * combination with {@link TableRowParser} to generate output in {@link TableRow} form.
 */
@RunWith(JUnit4.class)
public class BigQueryIOStorageReadTableRowIT {
    private static final String DATASET_ID = "big_query_import_export";

    private static final String TABLE_PREFIX = "parallel_read_table_row_";

    private BigQueryIOStorageReadTableRowIT.BigQueryIOStorageReadTableRowOptions options;

    /**
     * Private pipeline options for the test.
     */
    public interface BigQueryIOStorageReadTableRowOptions extends ExperimentalOptions , TestPipelineOptions {
        @Description("The table to be read")
        @Validation.Required
        String getInputTable();

        void setInputTable(String table);
    }

    private static class TableRowToKVPairFn extends SimpleFunction<TableRow, KV<String, String>> {
        @Override
        public KV<String, String> apply(TableRow input) {
            CharSequence sampleString = ((CharSequence) (input.get("sample_string")));
            String key = (sampleString != null) ? sampleString.toString() : "null";
            return KV.of(key, BigQueryHelpers.toJsonString(input));
        }
    }

    @Test
    public void testBigQueryStorageReadTableRow1() throws Exception {
        setUpTestEnvironment("1");
        BigQueryIOStorageReadTableRowIT.runPipeline(options);
    }

    @Test
    public void testBigQueryStorageReadTableRow10k() throws Exception {
        setUpTestEnvironment("10k");
        BigQueryIOStorageReadTableRowIT.runPipeline(options);
    }

    @Test
    public void testBigQueryStorageReadTableRow100k() throws Exception {
        setUpTestEnvironment("100k");
        BigQueryIOStorageReadTableRowIT.runPipeline(options);
    }
}

