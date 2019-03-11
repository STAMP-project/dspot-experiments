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
package org.apache.hadoop.mapreduce;


import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;


public class TestMROutputFormat {
    @Test
    public void testJobSubmission() throws Exception {
        JobConf conf = new JobConf();
        Job job = new Job(conf);
        job.setInputFormatClass(TestInputFormat.class);
        job.setMapperClass(TestMROutputFormat.TestMapper.class);
        job.setOutputFormatClass(TestOutputFormat.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(IntWritable.class);
        job.waitForCompletion(true);
        Assert.assertTrue(job.isSuccessful());
    }

    public static class TestMapper extends Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void map(IntWritable key, IntWritable value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
        }
    }
}

