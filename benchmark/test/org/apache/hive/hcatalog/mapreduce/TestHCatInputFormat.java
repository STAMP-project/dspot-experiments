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


import java.io.IOException;
import junit.framework.Assert;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.junit.Test;


public class TestHCatInputFormat extends HCatBaseTest {
    private boolean setUpComplete = false;

    @Test
    public void testBadRecordHandlingPasses() throws Exception {
        Assert.assertTrue(runJob(0.1F));
    }

    @Test
    public void testBadRecordHandlingFails() throws Exception {
        Assert.assertFalse(runJob(0.01F));
    }

    public static class MyMapper extends Mapper<NullWritable, HCatRecord, NullWritable, Text> {
        @Override
        public void map(NullWritable key, HCatRecord value, Context context) throws IOException, InterruptedException {
            HCatBaseTest.LOG.info(("HCatRecord: " + value));
            context.write(NullWritable.get(), new Text(value.toString()));
        }
    }
}

