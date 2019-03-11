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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.mapreduce;


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Test;


public class TestSequenceFileReadWrite {
    private File dataDir;

    private String warehouseDir;

    private String inputFileName;

    private IDriver driver;

    private PigServer server;

    private String[] input;

    private HiveConf hiveConf;

    @Test
    public void testSequenceTableWriteRead() throws Exception {
        String createTable = "CREATE TABLE demo_table(a0 int, a1 String, a2 String) STORED AS SEQUENCEFILE";
        driver.run("drop table demo_table");
        int retCode1 = driver.run(createTable).getResponseCode();
        Assert.assertTrue((retCode1 == 0));
        server.setBatchOn();
        server.registerQuery((("A = load '" + (inputFileName)) + "' using PigStorage(',') as (a0:int,a1:chararray,a2:chararray);"));
        server.registerQuery("store A into 'demo_table' using org.apache.hive.hcatalog.pig.HCatStorer();");
        server.executeBatch();
        server.registerQuery("B = load 'demo_table' using org.apache.hive.hcatalog.pig.HCatLoader();");
        Iterator<Tuple> XIter = server.openIterator("B");
        int numTuplesRead = 0;
        while (XIter.hasNext()) {
            Tuple t = XIter.next();
            Assert.assertEquals(3, t.size());
            Assert.assertEquals(t.get(0).toString(), ("" + numTuplesRead));
            Assert.assertEquals(t.get(1).toString(), ("a" + numTuplesRead));
            Assert.assertEquals(t.get(2).toString(), ("b" + numTuplesRead));
            numTuplesRead++;
        } 
        Assert.assertEquals(input.length, numTuplesRead);
    }

    @Test
    public void testTextTableWriteRead() throws Exception {
        String createTable = "CREATE TABLE demo_table_1(a0 int, a1 String, a2 String) STORED AS TEXTFILE";
        driver.run("drop table demo_table_1");
        int retCode1 = driver.run(createTable).getResponseCode();
        Assert.assertTrue((retCode1 == 0));
        server.setBatchOn();
        server.registerQuery((("A = load '" + (inputFileName)) + "' using PigStorage(',') as (a0:int,a1:chararray,a2:chararray);"));
        server.registerQuery("store A into 'demo_table_1' using org.apache.hive.hcatalog.pig.HCatStorer();");
        server.executeBatch();
        server.registerQuery("B = load 'demo_table_1' using org.apache.hive.hcatalog.pig.HCatLoader();");
        Iterator<Tuple> XIter = server.openIterator("B");
        int numTuplesRead = 0;
        while (XIter.hasNext()) {
            Tuple t = XIter.next();
            Assert.assertEquals(3, t.size());
            Assert.assertEquals(t.get(0).toString(), ("" + numTuplesRead));
            Assert.assertEquals(t.get(1).toString(), ("a" + numTuplesRead));
            Assert.assertEquals(t.get(2).toString(), ("b" + numTuplesRead));
            numTuplesRead++;
        } 
        Assert.assertEquals(input.length, numTuplesRead);
    }

    public static class Map extends Mapper<LongWritable, Text, NullWritable, DefaultHCatRecord> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] cols = value.toString().split(",");
            DefaultHCatRecord record = new DefaultHCatRecord(3);
            record.set(0, Integer.parseInt(cols[0]));
            record.set(1, cols[1]);
            record.set(2, cols[2]);
            context.write(NullWritable.get(), record);
        }
    }
}

