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
package org.apache.hadoop.streaming.mapreduce;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests StreamXmlRecordReader The test creates an XML file, uses
 * StreamXmlRecordReader and compares the expected output against the generated
 * output
 */
public class TestStreamXmlRecordReader {
    private File INPUT_FILE;

    private String input;

    private String outputExpect;

    Path OUTPUT_DIR;

    FileSystem fs;

    public TestStreamXmlRecordReader() throws IOException {
        INPUT_FILE = new File("target/input.xml");
        input = "<xmltag>\t\nroses.are.red\t\nviolets.are.blue\t\n" + "bunnies.are.pink\t\n</xmltag>\t\n";
        outputExpect = input;
    }

    @Test
    public void testStreamXmlRecordReader() throws Exception {
        Job job = Job.getInstance();
        Configuration conf = job.getConfiguration();
        job.setJarByClass(TestStreamXmlRecordReader.class);
        job.setMapperClass(Mapper.class);
        conf.set("stream.recordreader.class", "org.apache.hadoop.streaming.mapreduce.StreamXmlRecordReader");
        conf.set("stream.recordreader.begin", "<PATTERN>");
        conf.set("stream.recordreader.end", "</PATTERN>");
        job.setInputFormatClass(StreamInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path("target/input.xml"));
        OUTPUT_DIR = new Path("target/output");
        fs = FileSystem.get(conf);
        if (fs.exists(OUTPUT_DIR)) {
            fs.delete(OUTPUT_DIR, true);
        }
        FileOutputFormat.setOutputPath(job, OUTPUT_DIR);
        boolean ret = job.waitForCompletion(true);
        Assert.assertEquals(true, ret);
        checkOutput();
    }
}

