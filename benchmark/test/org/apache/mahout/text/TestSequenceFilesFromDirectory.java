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
package org.apache.mahout.text;


import org.apache.commons.io.Charsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.MahoutTestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class TestSequenceFilesFromDirectory extends MahoutTestCase {
    private static final Logger logger = LoggerFactory.getLogger(TestSequenceFilesFromDirectory.class);

    private static final String[][] DATA1 = new String[][]{ new String[]{ "test1", "This is the first text." }, new String[]{ "test2", "This is the second text." }, new String[]{ "test3", "This is the third text." } };

    private static final String[][] DATA2 = new String[][]{ new String[]{ "recursive_test1", "This is the first text." }, new String[]{ "recursive_test2", "This is the second text." }, new String[]{ "recursive_test3", "This is the third text." } };

    @Test
    public void testSequenceFileFromDirectoryBasic() throws Exception {
        // parameters
        Configuration configuration = getConfiguration();
        FileSystem fs = FileSystem.get(configuration);
        // create
        Path tmpDir = getTestTempDirPath();
        Path inputDir = new Path(tmpDir, "inputDir");
        fs.mkdirs(inputDir);
        Path outputDir = new Path(tmpDir, "outputDir");
        Path outputDirRecursive = new Path(tmpDir, "outputDirRecursive");
        Path inputDirRecursive = new Path(tmpDir, "inputDirRecur");
        fs.mkdirs(inputDirRecursive);
        // prepare input files
        TestSequenceFilesFromDirectory.createFilesFromArrays(configuration, inputDir, TestSequenceFilesFromDirectory.DATA1);
        SequenceFilesFromDirectory.main(new String[]{ "--input", inputDir.toString(), "--output", outputDir.toString(), "--chunkSize", "64", "--charset", Charsets.UTF_8.name(), "--keyPrefix", "UID", "--method", "sequential" });
        // check output chunk files
        TestSequenceFilesFromDirectory.checkChunkFiles(configuration, outputDir, TestSequenceFilesFromDirectory.DATA1, "UID");
        TestSequenceFilesFromDirectory.createRecursiveDirFilesFromArrays(configuration, inputDirRecursive, TestSequenceFilesFromDirectory.DATA2);
        FileStatus fstInputPath = fs.getFileStatus(inputDirRecursive);
        String dirs = HadoopUtil.buildDirList(fs, fstInputPath);
        System.out.println(("\n\n ----- recursive dirs: " + dirs));
        SequenceFilesFromDirectory.main(new String[]{ "--input", inputDirRecursive.toString(), "--output", outputDirRecursive.toString(), "--chunkSize", "64", "--charset", Charsets.UTF_8.name(), "--keyPrefix", "UID", "--method", "sequential" });
        TestSequenceFilesFromDirectory.checkRecursiveChunkFiles(configuration, outputDirRecursive, TestSequenceFilesFromDirectory.DATA2, "UID");
    }

    @Test
    public void testSequenceFileFromDirectoryMapReduce() throws Exception {
        Configuration conf = getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        // create
        Path tmpDir = getTestTempDirPath();
        Path inputDir = new Path(tmpDir, "inputDir");
        fs.mkdirs(inputDir);
        Path inputDirRecur = new Path(tmpDir, "inputDirRecur");
        fs.mkdirs(inputDirRecur);
        Path mrOutputDir = new Path(tmpDir, "mrOutputDir");
        Path mrOutputDirRecur = new Path(tmpDir, "mrOutputDirRecur");
        TestSequenceFilesFromDirectory.createFilesFromArrays(conf, inputDir, TestSequenceFilesFromDirectory.DATA1);
        SequenceFilesFromDirectory.main(new String[]{ "-Dhadoop.tmp.dir=" + (conf.get("hadoop.tmp.dir")), "--input", inputDir.toString(), "--output", mrOutputDir.toString(), "--chunkSize", "64", "--charset", Charsets.UTF_8.name(), "--method", "mapreduce", "--keyPrefix", "UID", "--fileFilterClass", "org.apache.mahout.text.TestPathFilter" });
        TestSequenceFilesFromDirectory.checkMRResultFiles(conf, mrOutputDir, TestSequenceFilesFromDirectory.DATA1, "UID");
        TestSequenceFilesFromDirectory.createRecursiveDirFilesFromArrays(conf, inputDirRecur, TestSequenceFilesFromDirectory.DATA2);
        FileStatus fst_input_path = fs.getFileStatus(inputDirRecur);
        String dirs = HadoopUtil.buildDirList(fs, fst_input_path);
        TestSequenceFilesFromDirectory.logger.info("\n\n ---- recursive dirs: {}", dirs);
        SequenceFilesFromDirectory.main(new String[]{ "-Dhadoop.tmp.dir=" + (conf.get("hadoop.tmp.dir")), "--input", inputDirRecur.toString(), "--output", mrOutputDirRecur.toString(), "--chunkSize", "64", "--charset", Charsets.UTF_8.name(), "--method", "mapreduce", "--keyPrefix", "UID", "--fileFilterClass", "org.apache.mahout.text.TestPathFilter" });
        TestSequenceFilesFromDirectory.checkMRResultFilesRecursive(conf, mrOutputDirRecur, TestSequenceFilesFromDirectory.DATA2, "UID");
    }
}

