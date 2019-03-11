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


import java.util.Iterator;
import junit.framework.Assert;
import org.apache.hadoop.fs.Path;
import org.apache.pig.PigServer;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.junit.Test;


public class TestHCatHiveThriftCompatibility extends HCatBaseTest {
    private boolean setUpComplete = false;

    private Path intStringSeq;

    /**
     * Create a table with no explicit schema and ensure its correctly
     *  discovered from the thrift struct.
     */
    @Test
    public void testDynamicCols() throws Exception {
        Assert.assertEquals(0, driver.run("drop table if exists test_thrift").getResponseCode());
        Assert.assertEquals(0, driver.run(("create external table test_thrift " + ((((((("partitioned by (year string) " + "row format serde 'org.apache.hadoop.hive.serde2.thrift.ThriftDeserializer' ") + "with serdeproperties ( ") + "  'serialization.class'='org.apache.hadoop.hive.serde2.thrift.test.IntString', ") + "  'serialization.format'='org.apache.thrift.protocol.TBinaryProtocol') ") + "stored as") + "  inputformat 'org.apache.hadoop.mapred.SequenceFileInputFormat'") + "  outputformat 'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat'"))).getResponseCode());
        Assert.assertEquals(0, driver.run((("alter table test_thrift add partition (year = '2012') location '" + (intStringSeq.getParent())) + "'")).getResponseCode());
        PigServer pigServer = createPigServer(false);
        pigServer.registerQuery("A = load 'test_thrift' using org.apache.hive.hcatalog.pig.HCatLoader();");
        Schema expectedSchema = new Schema();
        expectedSchema.add(new Schema.FieldSchema("myint", DataType.INTEGER));
        expectedSchema.add(new Schema.FieldSchema("mystring", DataType.CHARARRAY));
        expectedSchema.add(new Schema.FieldSchema("underscore_int", DataType.INTEGER));
        expectedSchema.add(new Schema.FieldSchema("year", DataType.CHARARRAY));
        Assert.assertEquals(expectedSchema, pigServer.dumpSchema("A"));
        Iterator<Tuple> iterator = pigServer.openIterator("A");
        Tuple t = iterator.next();
        Assert.assertEquals(1, t.get(0));
        Assert.assertEquals("one", t.get(1));
        Assert.assertEquals(1, t.get(2));
        Assert.assertEquals("2012", t.get(3));
        Assert.assertFalse(iterator.hasNext());
    }
}

