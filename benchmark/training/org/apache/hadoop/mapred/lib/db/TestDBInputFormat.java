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
package org.apache.hadoop.mapred.lib.db;


import DBConfiguration.DRIVER_CLASS_PROPERTY;
import DBConfiguration.INPUT_CLASS_PROPERTY;
import DBConfiguration.INPUT_CONDITIONS_PROPERTY;
import DBConfiguration.INPUT_COUNT_QUERY;
import DBConfiguration.INPUT_FIELD_NAMES_PROPERTY;
import DBConfiguration.INPUT_ORDER_BY_PROPERTY;
import DBConfiguration.INPUT_QUERY;
import DBConfiguration.INPUT_TABLE_NAME_PROPERTY;
import DBConfiguration.PASSWORD_PROPERTY;
import DBConfiguration.URL_PROPERTY;
import DBConfiguration.USERNAME_PROPERTY;
import MRJobConfig.NUM_MAPS;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.lib.db.DBInputFormat.DBInputSplit;
import org.apache.hadoop.mapred.lib.db.DBInputFormat.DBRecordReader;
import org.apache.hadoop.mapred.lib.db.DBInputFormat.NullDBWritable;
import org.apache.hadoop.mapreduce.lib.db.DriverForTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestDBInputFormat {
    /**
     * test DBInputFormat class. Class should split result for chunks
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 10000)
    public void testDBInputFormat() throws Exception {
        JobConf configuration = new JobConf();
        setupDriver(configuration);
        DBInputFormat<NullDBWritable> format = new DBInputFormat<NullDBWritable>();
        format.setConf(configuration);
        format.setConf(configuration);
        DBInputFormat.DBInputSplit splitter = new DBInputFormat.DBInputSplit(1, 10);
        Reporter reporter = Mockito.mock(Reporter.class);
        RecordReader<LongWritable, NullDBWritable> reader = format.getRecordReader(splitter, configuration, reporter);
        configuration.setInt(NUM_MAPS, 3);
        InputSplit[] lSplits = format.getSplits(configuration, 3);
        Assert.assertEquals(5, lSplits[0].getLength());
        Assert.assertEquals(3, lSplits.length);
        // test reader .Some simple tests
        Assert.assertEquals(LongWritable.class, reader.createKey().getClass());
        Assert.assertEquals(0, reader.getPos());
        Assert.assertEquals(0, reader.getProgress(), 0.001);
        reader.close();
    }

    /**
     * test configuration for db. should works DBConfiguration.* parameters.
     */
    @Test(timeout = 5000)
    public void testSetInput() {
        JobConf configuration = new JobConf();
        String[] fieldNames = new String[]{ "field1", "field2" };
        DBInputFormat.setInput(configuration, NullDBWritable.class, "table", "conditions", "orderBy", fieldNames);
        Assert.assertEquals("org.apache.hadoop.mapred.lib.db.DBInputFormat$NullDBWritable", configuration.getClass(INPUT_CLASS_PROPERTY, null).getName());
        Assert.assertEquals("table", configuration.get(INPUT_TABLE_NAME_PROPERTY, null));
        String[] fields = configuration.getStrings(INPUT_FIELD_NAMES_PROPERTY);
        Assert.assertEquals("field1", fields[0]);
        Assert.assertEquals("field2", fields[1]);
        Assert.assertEquals("conditions", configuration.get(INPUT_CONDITIONS_PROPERTY, null));
        Assert.assertEquals("orderBy", configuration.get(INPUT_ORDER_BY_PROPERTY, null));
        configuration = new JobConf();
        DBInputFormat.setInput(configuration, NullDBWritable.class, "query", "countQuery");
        Assert.assertEquals("query", configuration.get(INPUT_QUERY, null));
        Assert.assertEquals("countQuery", configuration.get(INPUT_COUNT_QUERY, null));
        JobConf jConfiguration = new JobConf();
        DBConfiguration.configureDB(jConfiguration, "driverClass", "dbUrl", "user", "password");
        Assert.assertEquals("driverClass", jConfiguration.get(DRIVER_CLASS_PROPERTY));
        Assert.assertEquals("dbUrl", jConfiguration.get(URL_PROPERTY));
        Assert.assertEquals("user", jConfiguration.get(USERNAME_PROPERTY));
        Assert.assertEquals("password", jConfiguration.get(PASSWORD_PROPERTY));
        jConfiguration = new JobConf();
        DBConfiguration.configureDB(jConfiguration, "driverClass", "dbUrl");
        Assert.assertEquals("driverClass", jConfiguration.get(DRIVER_CLASS_PROPERTY));
        Assert.assertEquals("dbUrl", jConfiguration.get(URL_PROPERTY));
        Assert.assertNull(jConfiguration.get(USERNAME_PROPERTY));
        Assert.assertNull(jConfiguration.get(PASSWORD_PROPERTY));
    }

    /**
     * test DBRecordReader. This reader should creates keys, values, know about position..
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 5000)
    public void testDBRecordReader() throws Exception {
        JobConf job = Mockito.mock(JobConf.class);
        DBConfiguration dbConfig = Mockito.mock(DBConfiguration.class);
        String[] fields = new String[]{ "field1", "filed2" };
        @SuppressWarnings("rawtypes")
        DBRecordReader reader = new DBInputFormat<NullDBWritable>().new DBRecordReader(new DBInputSplit(), NullDBWritable.class, job, DriverForTest.getConnection(), dbConfig, "condition", fields, "table");
        LongWritable key = reader.createKey();
        Assert.assertEquals(0, key.get());
        DBWritable value = reader.createValue();
        Assert.assertEquals("org.apache.hadoop.mapred.lib.db.DBInputFormat$NullDBWritable", value.getClass().getName());
        Assert.assertEquals(0, reader.getPos());
        Assert.assertFalse(reader.next(key, value));
    }
}

