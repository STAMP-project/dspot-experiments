/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.mahout.utils.regex;


import Mapper.Context;
import RegexMapper.FORMATTER_CLASS;
import RegexMapper.GROUP_MATCHERS;
import RegexMapper.REGEX;
import RegexMapper.TRANSFORMER_CLASS;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.mahout.common.DummyRecordWriter;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;


public final class RegexMapperTest extends MahoutTestCase {
    @Test
    public void testRegex() throws Exception {
        RegexMapper mapper = new RegexMapper();
        Configuration conf = getConfiguration();
        conf.set(REGEX, "(?<=(\\?|&)q=).*?(?=&|$)");
        conf.set(TRANSFORMER_CLASS, URLDecodeTransformer.class.getName());
        DummyRecordWriter<LongWritable, Text> mapWriter = new DummyRecordWriter();
        Context mapContext = DummyRecordWriter.build(mapper, conf, mapWriter);
        mapper.setup(mapContext);
        for (int i = 0; i < (RegexUtilsTest.TEST_STRS.length); i++) {
            String testStr = RegexUtilsTest.TEST_STRS[i];
            LongWritable key = new LongWritable(i);
            mapper.map(key, new Text(testStr), mapContext);
            List<Text> value = mapWriter.getValue(key);
            if (!(RegexUtilsTest.GOLD[i].isEmpty())) {
                assertEquals(1, value.size());
                assertEquals(RegexUtilsTest.GOLD[i], value.get(0).toString());
            }
        }
    }

    @Test
    public void testGroups() throws Exception {
        RegexMapper mapper = new RegexMapper();
        Configuration conf = getConfiguration();
        conf.set(REGEX, "(\\d+)\\.(\\d+)\\.(\\d+)");
        conf.set(TRANSFORMER_CLASS, URLDecodeTransformer.class.getName());
        conf.setStrings(GROUP_MATCHERS, "1", "3");
        DummyRecordWriter<LongWritable, Text> mapWriter = new DummyRecordWriter();
        Context mapContext = DummyRecordWriter.build(mapper, conf, mapWriter);
        mapper.setup(mapContext);
        for (int i = 0; i < (RegexUtilsTest.TEST_STRS.length); i++) {
            String testStr = RegexUtilsTest.TEST_STRS[i];
            LongWritable key = new LongWritable(i);
            mapper.map(key, new Text(testStr), mapContext);
            List<Text> value = mapWriter.getValue(key);
            assertEquals(1, value.size());
            assertEquals("127 0", value.get(0).toString());
        }
    }

    @Test
    public void testFPGFormatter() throws Exception {
        RegexMapper mapper = new RegexMapper();
        Configuration conf = getConfiguration();
        conf.set(REGEX, "(?<=(\\?|&)q=).*?(?=&|$)");
        conf.set(TRANSFORMER_CLASS, URLDecodeTransformer.class.getName());
        conf.set(FORMATTER_CLASS, FPGFormatter.class.getName());
        DummyRecordWriter<LongWritable, Text> mapWriter = new DummyRecordWriter();
        Context mapContext = DummyRecordWriter.build(mapper, conf, mapWriter);
        mapper.setup(mapContext);
        RegexFormatter formatter = new FPGFormatter();
        for (int i = 0; i < (RegexUtilsTest.TEST_STRS.length); i++) {
            String testStr = RegexUtilsTest.TEST_STRS[i];
            LongWritable key = new LongWritable(i);
            mapper.map(key, new Text(testStr), mapContext);
            List<Text> value = mapWriter.getValue(key);
            if (!(RegexUtilsTest.GOLD[i].isEmpty())) {
                assertEquals(1, value.size());
                assertEquals(formatter.format(RegexUtilsTest.GOLD[i]), value.get(0).toString());
            }
        }
    }
}

