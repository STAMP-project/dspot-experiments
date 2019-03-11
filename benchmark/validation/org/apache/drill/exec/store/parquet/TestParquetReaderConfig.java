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


import ExecConstants.PARQUET_READER_STRINGS_SIGNED_MIN_MAX;
import ParquetReaderConfig.ENABLE_BYTES_READ_COUNTER;
import ParquetReaderConfig.ENABLE_BYTES_TOTAL_COUNTER;
import ParquetReaderConfig.ENABLE_TIME_READ_COUNTER;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.drill.exec.server.options.SystemOptionManager;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.ParquetReadOptions;
import org.junit.Assert;
import org.junit.Test;


public class TestParquetReaderConfig {
    @Test
    public void testDefaultsDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ParquetReaderConfig readerConfig = ParquetReaderConfig.builder().build();// all defaults

        String value = mapper.writeValueAsString(readerConfig);
        Assert.assertEquals(ParquetReaderConfig.getDefaultInstance(), readerConfig);// compare with default instance

        Assert.assertEquals("{}", value);
        readerConfig = mapper.readValue(value, ParquetReaderConfig.class);
        Assert.assertTrue(readerConfig.autoCorrectCorruptedDates());// check that default value is restored

        // change the default: set autoCorrectCorruptedDates to false
        // keep the default: set enableStringsSignedMinMax to false
        readerConfig = new ParquetReaderConfig(false, false, false, false, false);
        value = mapper.writeValueAsString(readerConfig);
        Assert.assertEquals("{\"autoCorrectCorruptedDates\":false}", value);
    }

    @Test
    public void testAddConfigToConf() {
        Configuration conf = new Configuration();
        conf.setBoolean(ENABLE_BYTES_READ_COUNTER, true);
        conf.setBoolean(ENABLE_BYTES_TOTAL_COUNTER, true);
        conf.setBoolean(ENABLE_TIME_READ_COUNTER, true);
        ParquetReaderConfig readerConfig = ParquetReaderConfig.builder().withConf(conf).build();
        Configuration newConf = readerConfig.addCountersToConf(new Configuration());
        checkConfigValue(newConf, ENABLE_BYTES_READ_COUNTER, "true");
        checkConfigValue(newConf, ENABLE_BYTES_TOTAL_COUNTER, "true");
        checkConfigValue(newConf, ENABLE_TIME_READ_COUNTER, "true");
        conf = new Configuration();
        conf.setBoolean(ENABLE_BYTES_READ_COUNTER, false);
        conf.setBoolean(ENABLE_BYTES_TOTAL_COUNTER, false);
        conf.setBoolean(ENABLE_TIME_READ_COUNTER, false);
        readerConfig = ParquetReaderConfig.builder().withConf(conf).build();
        newConf = readerConfig.addCountersToConf(new Configuration());
        checkConfigValue(newConf, ENABLE_BYTES_READ_COUNTER, "false");
        checkConfigValue(newConf, ENABLE_BYTES_TOTAL_COUNTER, "false");
        checkConfigValue(newConf, ENABLE_TIME_READ_COUNTER, "false");
    }

    @Test
    public void testReadOptions() {
        // set enableStringsSignedMinMax to true
        ParquetReaderConfig readerConfig = new ParquetReaderConfig(false, false, false, true, true);
        ParquetReadOptions readOptions = readerConfig.toReadOptions();
        Assert.assertTrue(readOptions.useSignedStringMinMax());
        // set enableStringsSignedMinMax to false
        readerConfig = new ParquetReaderConfig(false, false, false, true, false);
        readOptions = readerConfig.toReadOptions();
        Assert.assertFalse(readOptions.useSignedStringMinMax());
    }

    @Test
    public void testPriorityAssignmentForStringsSignedMinMax() throws Exception {
        SystemOptionManager options = init();
        // use value from format config
        ParquetFormatConfig formatConfig = new ParquetFormatConfig();
        ParquetReaderConfig readerConfig = ParquetReaderConfig.builder().withFormatConfig(formatConfig).build();
        Assert.assertEquals(formatConfig.isStringsSignedMinMaxEnabled(), readerConfig.enableStringsSignedMinMax());
        // change format config value
        formatConfig.enableStringsSignedMinMax = true;
        readerConfig = ParquetReaderConfig.builder().withFormatConfig(formatConfig).build();
        Assert.assertEquals(formatConfig.isStringsSignedMinMaxEnabled(), readerConfig.enableStringsSignedMinMax());
        // set option, option value should have higher priority
        options.setLocalOption(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, "false");
        readerConfig = ParquetReaderConfig.builder().withFormatConfig(formatConfig).withOptions(options).build();
        Assert.assertFalse(readerConfig.enableStringsSignedMinMax());
        // set option as empty (undefined), config should have higher priority
        options.setLocalOption(PARQUET_READER_STRINGS_SIGNED_MIN_MAX, "");
        readerConfig = ParquetReaderConfig.builder().withFormatConfig(formatConfig).withOptions(options).build();
        Assert.assertEquals(formatConfig.isStringsSignedMinMaxEnabled(), readerConfig.enableStringsSignedMinMax());
    }
}

