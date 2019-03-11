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
package org.apache.avro.mapred;


import GenericData.Record;
import Schema.Type.LONG;
import java.io.IOException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


@SuppressWarnings("deprecation")
public class TestGenericJob {
    @Rule
    public TemporaryFolder DIR = new TemporaryFolder();

    static class AvroTestConverter extends MapReduceBase implements Mapper<LongWritable, Text, AvroWrapper<Pair<Long, GenericData.Record>>, NullWritable> {
        public void map(LongWritable key, Text value, OutputCollector<AvroWrapper<Pair<Long, GenericData.Record>>, NullWritable> out, Reporter reporter) throws IOException {
            GenericData.Record optional_entry = new GenericData.Record(TestGenericJob.createInnerSchema("optional_field_1"));
            optional_entry.put("optional_field_1", 0L);
            GenericData.Array<GenericData.Record> array = new GenericData.Array<>(1, createArraySchema());
            array.add(optional_entry);
            GenericData.Record container = new GenericData.Record(TestGenericJob.createSchema());
            container.put("Optional", array);
            out.collect(new AvroWrapper(new Pair(key.get(), container)), NullWritable.get());
        }
    }

    @Test
    public void testJob() throws Exception {
        JobConf job = new JobConf();
        Path outputPath = new Path(((DIR.getRoot().getPath()) + "/out"));
        outputPath.getFileSystem(job).delete(outputPath);
        job.setInputFormat(TextInputFormat.class);
        FileInputFormat.setInputPaths(job, ((DIR.getRoot().getPath()) + "/in"));
        job.setMapperClass(TestGenericJob.AvroTestConverter.class);
        job.setNumReduceTasks(0);
        FileOutputFormat.setOutputPath(job, outputPath);
        System.out.println(TestGenericJob.createSchema());
        AvroJob.setOutputSchema(job, Pair.getPairSchema(Schema.create(LONG), TestGenericJob.createSchema()));
        job.setOutputFormat(AvroOutputFormat.class);
        JobClient.runJob(job);
    }
}

