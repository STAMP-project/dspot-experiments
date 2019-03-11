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
package org.apache.beam.sdk.io.hadoop.format;


import ConfigurationOptions.ES_QUERY;
import java.io.Serializable;
import org.apache.beam.sdk.io.common.HashingFn;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Combine;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * A test of {@link org.apache.beam.sdk.io.hadoop.format.HadoopFormatIO.Read} on an independent
 * Elasticsearch instance.
 *
 * <p>This test requires a running instance of Elasticsearch, and the test dataset must exist in the
 * database.
 *
 * <p>You can run this test by doing the following:
 *
 * <pre>
 *  ./gradlew integrationTest -p sdks/java/io/hadoop-format/
 *  -Dit.test=org.apache.beam.sdk.io.hadoop.format.HadoopFormatIOElasticIT
 *  -DintegrationTestPipelineOptions='[
 *  "--elasticServerIp=1.2.3.4",
 *  "--elasticServerPort=port",
 *  "--elasticUserName=user",
 *  "--elasticPassword=mypass" ]'
 *  --tests org.apache.beam.sdk.io.hadoop.format.HadoopFormatIOElasticIT
 *  -DintegrationTestRunner=direct
 * </pre>
 *
 * <p>If you want to run this with a runner besides directrunner, there are profiles for dataflow
 * and spark in the pom. You'll want to activate those in addition to the normal test runner
 * invocation pipeline options.
 */
@RunWith(JUnit4.class)
public class HadoopFormatIOElasticIT implements Serializable {
    private static final String ELASTIC_INTERNAL_VERSION = "5.x";

    private static final String TRUE = "true";

    private static final String ELASTIC_INDEX_NAME = "test_data";

    private static final String ELASTIC_TYPE_NAME = "test_type";

    private static final String ELASTIC_RESOURCE = (("/" + (HadoopFormatIOElasticIT.ELASTIC_INDEX_NAME)) + "/") + (HadoopFormatIOElasticIT.ELASTIC_TYPE_NAME);

    private static HadoopFormatIOTestOptions options;

    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create();

    /**
     * This test reads data from the Elasticsearch instance and verifies whether data is read
     * successfully.
     */
    @Test
    public void testHifIOWithElastic() throws SecurityException {
        // Expected hashcode is evaluated during insertion time one time and hardcoded here.
        final long expectedRowCount = 1000L;
        String expectedHashCode = "42e254c8689050ed0a617ff5e80ea392";
        Configuration conf = HadoopFormatIOElasticIT.getConfiguration(HadoopFormatIOElasticIT.options);
        PCollection<KV<Text, LinkedMapWritable>> esData = pipeline.apply(HadoopFormatIO.<Text, LinkedMapWritable>read().withConfiguration(conf));
        // Verify that the count of objects fetched using HIFInputFormat IO is correct.
        PCollection<Long> count = esData.apply(Count.globally());
        PAssert.thatSingleton(count).isEqualTo(expectedRowCount);
        PCollection<LinkedMapWritable> values = esData.apply(Values.create());
        PCollection<String> textValues = values.apply(transformFunc);
        // Verify the output values using checksum comparison.
        PCollection<String> consolidatedHashcode = textValues.apply(Combine.globally(new HashingFn()).withoutDefaults());
        PAssert.that(consolidatedHashcode).containsInAnyOrder(expectedHashCode);
        pipeline.run().waitUntilFinish();
    }

    private final MapElements<LinkedMapWritable, String> transformFunc = MapElements.via(new org.apache.beam.sdk.transforms.SimpleFunction<LinkedMapWritable, String>() {
        @Override
        public String apply(LinkedMapWritable mapw) {
            String rowValue = "";
            rowValue = convertMapWRowToString(mapw);
            return rowValue;
        }
    });

    /**
     * This test reads data from the Elasticsearch instance based on a query and verifies if data is
     * read successfully.
     */
    @Test
    public void testHifIOWithElasticQuery() {
        String expectedHashCode = "d7a7e4e42c2ca7b83ef7c1ad1ebce000";
        Long expectedRecordsCount = 1L;
        Configuration conf = HadoopFormatIOElasticIT.getConfiguration(HadoopFormatIOElasticIT.options);
        String query = "{" + (((((((("  \"query\": {" + "  \"match\" : {") + "    \"Title\" : {") + "      \"query\" : \"Title9\",") + "      \"type\" : \"boolean\"") + "    }") + "  }") + "  }") + "}");
        conf.set(ES_QUERY, query);
        PCollection<KV<Text, LinkedMapWritable>> esData = pipeline.apply(HadoopFormatIO.<Text, LinkedMapWritable>read().withConfiguration(conf));
        PCollection<Long> count = esData.apply(Count.globally());
        // Verify that the count of objects fetched using HIFInputFormat IO is correct.
        PAssert.thatSingleton(count).isEqualTo(expectedRecordsCount);
        PCollection<LinkedMapWritable> values = esData.apply(Values.create());
        PCollection<String> textValues = values.apply(transformFunc);
        // Verify the output values using checksum comparison.
        PCollection<String> consolidatedHashcode = textValues.apply(Combine.globally(new HashingFn()).withoutDefaults());
        PAssert.that(consolidatedHashcode).containsInAnyOrder(expectedHashCode);
        pipeline.run().waitUntilFinish();
    }
}

