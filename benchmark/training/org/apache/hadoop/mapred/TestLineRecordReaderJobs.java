/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.mapred;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class TestLineRecordReaderJobs {
    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestTextInputFormat");

    private static Path inputDir = new Path(TestLineRecordReaderJobs.workDir, "input");

    private static Path outputDir = new Path(TestLineRecordReaderJobs.workDir, "output");

    /**
     * Test the case when a custom record delimiter is specified using the
     * textinputformat.record.delimiter configuration property
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws ClassNotFoundException
     * 		
     */
    @Test
    public void testCustomRecordDelimiters() throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        conf.set("textinputformat.record.delimiter", "\t\n");
        conf.setInt("mapreduce.job.maps", 1);
        FileSystem localFs = FileSystem.getLocal(conf);
        // cleanup
        localFs.delete(TestLineRecordReaderJobs.workDir, true);
        // creating input test file
        createInputFile(conf);
        createAndRunJob(conf);
        String expected = "0\tabc\ndef\n9\tghi\njkl\n";
        Assert.assertEquals(expected, readOutputFile(conf));
    }

    /**
     * Test the default behavior when the textinputformat.record.delimiter
     * configuration property is not specified
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws ClassNotFoundException
     * 		
     */
    @Test
    public void testDefaultRecordDelimiters() throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        FileSystem localFs = FileSystem.getLocal(conf);
        // cleanup
        localFs.delete(TestLineRecordReaderJobs.workDir, true);
        // creating input test file
        createInputFile(conf);
        createAndRunJob(conf);
        String expected = "0\tabc\n4\tdef\t\n9\tghi\n13\tjkl\n";
        Assert.assertEquals(expected, readOutputFile(conf));
    }
}

