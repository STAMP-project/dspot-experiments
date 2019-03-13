/**
 * Copyright (C) 2014-2018 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pinot.thirdeye.hadoop.derivedcolumn.transformation;


import DerivedColumnTransformationPhaseConstants.DERIVED_COLUMN_TRANSFORMATION_PHASE_OUTPUT_SCHEMA;
import DerivedColumnTransformationPhaseConstants.DERIVED_COLUMN_TRANSFORMATION_PHASE_THIRDEYE_CONFIG;
import DerivedColumnTransformationPhaseConstants.DERIVED_COLUMN_TRANSFORMATION_PHASE_TOPK_PATH;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import junit.framework.Assert;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.types.Pair;
import org.apache.pinot.thirdeye.hadoop.config.DimensionType;
import org.apache.pinot.thirdeye.hadoop.config.ThirdEyeConfig;
import org.apache.pinot.thirdeye.hadoop.config.ThirdEyeConstants;
import org.apache.pinot.thirdeye.hadoop.topk.TopKDimensionValues;
import org.apache.pinot.thirdeye.hadoop.util.ThirdeyeAvroUtils;
import org.junit.Test;


/**
 * This test will test mapper of DerivedColumnTransformation phase,
 * to see if new columns have been added according to the topk values file
 */
public class DerivedColumnTransformationTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String HADOOP_IO_SERIALIZATION = "io.serializations";

    private static final String AVRO_SCHEMA = "schema.avsc";

    private static final String TRANSFORMATION_SCHEMA = "transformation_schema.avsc";

    private static final String TOPK_PATH = "topk_path";

    private String outputPath;

    Properties props = new Properties();

    private MapDriver<AvroKey<GenericRecord>, NullWritable, AvroKey<GenericRecord>, NullWritable> mapDriver;

    @Test
    public void testTopKColumnTransformationPhase() throws Exception {
        int recordCount = 0;
        List<GenericRecord> inputRecords = generateTestData();
        for (GenericRecord record : inputRecords) {
            AvroKey<GenericRecord> inKey = new AvroKey<GenericRecord>();
            inKey.datum(record);
            mapDriver.addInput(new Pair<AvroKey<GenericRecord>, NullWritable>(inKey, NullWritable.get()));
            recordCount++;
        }
        resetAvroSerialization();
        List<Pair<AvroKey<GenericRecord>, NullWritable>> result = mapDriver.run();
        Assert.assertEquals(recordCount, result.size());
        for (Pair<AvroKey<GenericRecord>, NullWritable> pair : result) {
            GenericRecord datum = pair.getFirst().datum();
            Assert.assertEquals("TopKTransformationJob did not add new column for topk column", ((datum.getSchema().getField("d2_topk")) != null), true);
            Long d2 = ((Long) (datum.get("d2")));
            String d2_topk = ((String) (datum.get("d2_topk")));
            Assert.assertEquals("Incorrect topk column transformation", (((d2_topk.equals("other")) && (d2 == 501L)) || ((d2_topk.equals("502")) && (d2 == 502L))), true);
        }
    }

    public static class DerivedColumnTransformationPhaseMapper extends Mapper<AvroKey<GenericRecord>, NullWritable, AvroKey<GenericRecord>, NullWritable> {
        private Schema outputSchema;

        private ThirdEyeConfig thirdeyeConfig;

        private DerivedColumnTransformationPhaseConfig config;

        private List<String> dimensionsNames;

        private List<DimensionType> dimensionTypes;

        private Map<String, String> nonWhitelistValueMap;

        private List<String> metricNames;

        private TopKDimensionValues topKDimensionValues;

        private Map<String, Set<String>> topKDimensionsMap;

        private String timeColumnName;

        private Map<String, List<String>> whitelist;

        @Override
        public void setup(Context context) throws IOException, InterruptedException {
            Configuration configuration = context.getConfiguration();
            FileSystem fs = FileSystem.get(configuration);
            thirdeyeConfig = DerivedColumnTransformationTest.OBJECT_MAPPER.readValue(configuration.get(DERIVED_COLUMN_TRANSFORMATION_PHASE_THIRDEYE_CONFIG.toString()), ThirdEyeConfig.class);
            config = DerivedColumnTransformationPhaseConfig.fromThirdEyeConfig(thirdeyeConfig);
            dimensionsNames = config.getDimensionNames();
            dimensionTypes = config.getDimensionTypes();
            nonWhitelistValueMap = config.getNonWhitelistValue();
            metricNames = config.getMetricNames();
            timeColumnName = config.getTimeColumnName();
            whitelist = config.getWhitelist();
            outputSchema = new Schema.Parser().parse(configuration.get(DERIVED_COLUMN_TRANSFORMATION_PHASE_OUTPUT_SCHEMA.toString()));
            Path topKPath = new Path((((configuration.get(DERIVED_COLUMN_TRANSFORMATION_PHASE_TOPK_PATH.toString())) + (File.separator)) + (ThirdEyeConstants.TOPK_VALUES_FILE)));
            topKDimensionValues = new TopKDimensionValues();
            if (fs.exists(topKPath)) {
                FSDataInputStream topkValuesStream = fs.open(topKPath);
                topKDimensionValues = DerivedColumnTransformationTest.OBJECT_MAPPER.readValue(((DataInput) (topkValuesStream)), TopKDimensionValues.class);
                topkValuesStream.close();
            }
            topKDimensionsMap = topKDimensionValues.getTopKDimensions();
        }

        @Override
        public void map(AvroKey<GenericRecord> key, NullWritable value, Context context) throws IOException, InterruptedException {
            // input record
            GenericRecord inputRecord = key.datum();
            // output record
            GenericRecord outputRecord = new org.apache.avro.generic.GenericData.Record(outputSchema);
            // dimensions
            for (int i = 0; i < (dimensionsNames.size()); i++) {
                String dimensionName = dimensionsNames.get(i);
                DimensionType dimensionType = dimensionTypes.get(i);
                Object dimensionValue = ThirdeyeAvroUtils.getDimensionFromRecord(inputRecord, dimensionName);
                String dimensionValueStr = String.valueOf(dimensionValue);
                // add original dimension value with whitelist applied
                Object whitelistDimensionValue = dimensionValue;
                if ((whitelist) != null) {
                    List<String> whitelistDimensions = whitelist.get(dimensionName);
                    if (CollectionUtils.isNotEmpty(whitelistDimensions)) {
                        // whitelist config exists for this dimension but value not present in whitelist
                        if (!(whitelistDimensions.contains(dimensionValueStr))) {
                            whitelistDimensionValue = dimensionType.getValueFromString(nonWhitelistValueMap.get(dimensionName));
                        }
                    }
                }
                outputRecord.put(dimensionName, whitelistDimensionValue);
                // add column for topk, if topk config exists for that column
                if (topKDimensionsMap.containsKey(dimensionName)) {
                    Set<String> topKDimensionValues = topKDimensionsMap.get(dimensionName);
                    // if topk config exists for that dimension
                    if (CollectionUtils.isNotEmpty(topKDimensionValues)) {
                        String topkDimensionName = dimensionName + (ThirdEyeConstants.TOPK_DIMENSION_SUFFIX);
                        Object topkDimensionValue = dimensionValue;
                        // topk config exists for this dimension, but value not present in topk
                        if ((!(topKDimensionValues.contains(dimensionValueStr))) && ((((whitelist) == null) || ((whitelist.get(dimensionName)) == null)) || (!(whitelist.get(dimensionName).contains(dimensionValueStr))))) {
                            topkDimensionValue = ThirdEyeConstants.OTHER;
                        }
                        outputRecord.put(topkDimensionName, String.valueOf(topkDimensionValue));
                    }
                }
            }
            // metrics
            for (String metric : metricNames) {
                outputRecord.put(metric, ThirdeyeAvroUtils.getMetricFromRecord(inputRecord, metric));
            }
            // time
            outputRecord.put(timeColumnName, ThirdeyeAvroUtils.getMetricFromRecord(inputRecord, timeColumnName));
            AvroKey<GenericRecord> outputKey = new AvroKey<GenericRecord>(outputRecord);
            context.write(outputKey, NullWritable.get());
        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
        }
    }
}

