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
package org.apache.hadoop.fs.loadGenerator;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.PathUtils;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.Tool;
import org.junit.Assert;
import org.junit.Test;

import static StructureGenerator.DIR_STRUCTURE_FILE_NAME;
import static StructureGenerator.FILE_STRUCTURE_FILE_NAME;


/**
 * This class tests if a balancer schedules tasks correctly.
 */
public class TestLoadGenerator extends Configured implements Tool {
    private static final Configuration CONF = new HdfsConfiguration();

    private static final int DEFAULT_BLOCK_SIZE = 10;

    private static final File OUT_DIR = PathUtils.getTestDir(TestLoadGenerator.class);

    private static final File DIR_STRUCTURE_FILE = new File(TestLoadGenerator.OUT_DIR, DIR_STRUCTURE_FILE_NAME);

    private static final File FILE_STRUCTURE_FILE = new File(TestLoadGenerator.OUT_DIR, FILE_STRUCTURE_FILE_NAME);

    private static final String DIR_STRUCTURE_FIRST_LINE = "/dir0";

    private static final String DIR_STRUCTURE_SECOND_LINE = "/dir1";

    private static final String FILE_STRUCTURE_FIRST_LINE = "/dir0/_file_0 0.3754598635933768";

    private static final String FILE_STRUCTURE_SECOND_LINE = "/dir1/_file_1 1.4729310851145203";

    static {
        TestLoadGenerator.CONF.setLong(DFS_BLOCK_SIZE_KEY, TestLoadGenerator.DEFAULT_BLOCK_SIZE);
        TestLoadGenerator.CONF.setInt(DFS_BYTES_PER_CHECKSUM_KEY, TestLoadGenerator.DEFAULT_BLOCK_SIZE);
        TestLoadGenerator.CONF.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
    }

    /**
     * Test if the structure generator works fine
     */
    @Test
    public void testStructureGenerator() throws Exception {
        StructureGenerator sg = new StructureGenerator();
        String[] args = new String[]{ "-maxDepth", "2", "-minWidth", "1", "-maxWidth", "2", "-numOfFiles", "2", "-avgFileSize", "1", "-outDir", TestLoadGenerator.OUT_DIR.getAbsolutePath(), "-seed", "1" };
        final int MAX_DEPTH = 1;
        final int MIN_WIDTH = 3;
        final int MAX_WIDTH = 5;
        final int NUM_OF_FILES = 7;
        final int AVG_FILE_SIZE = 9;
        final int SEED = 13;
        try {
            // successful case
            Assert.assertEquals(0, sg.run(args));
            BufferedReader in = new BufferedReader(new FileReader(TestLoadGenerator.DIR_STRUCTURE_FILE));
            Assert.assertEquals(TestLoadGenerator.DIR_STRUCTURE_FIRST_LINE, in.readLine());
            Assert.assertEquals(TestLoadGenerator.DIR_STRUCTURE_SECOND_LINE, in.readLine());
            Assert.assertEquals(null, in.readLine());
            in.close();
            in = new BufferedReader(new FileReader(TestLoadGenerator.FILE_STRUCTURE_FILE));
            Assert.assertEquals(TestLoadGenerator.FILE_STRUCTURE_FIRST_LINE, in.readLine());
            Assert.assertEquals(TestLoadGenerator.FILE_STRUCTURE_SECOND_LINE, in.readLine());
            Assert.assertEquals(null, in.readLine());
            in.close();
            String oldArg = args[MAX_DEPTH];
            args[MAX_DEPTH] = "0";
            Assert.assertEquals((-1), sg.run(args));
            args[MAX_DEPTH] = oldArg;
            oldArg = args[MIN_WIDTH];
            args[MIN_WIDTH] = "-1";
            Assert.assertEquals((-1), sg.run(args));
            args[MIN_WIDTH] = oldArg;
            oldArg = args[MAX_WIDTH];
            args[MAX_WIDTH] = "-1";
            Assert.assertEquals((-1), sg.run(args));
            args[MAX_WIDTH] = oldArg;
            oldArg = args[NUM_OF_FILES];
            args[NUM_OF_FILES] = "-1";
            Assert.assertEquals((-1), sg.run(args));
            args[NUM_OF_FILES] = oldArg;
            oldArg = args[NUM_OF_FILES];
            args[NUM_OF_FILES] = "-1";
            Assert.assertEquals((-1), sg.run(args));
            args[NUM_OF_FILES] = oldArg;
            oldArg = args[AVG_FILE_SIZE];
            args[AVG_FILE_SIZE] = "-1";
            Assert.assertEquals((-1), sg.run(args));
            args[AVG_FILE_SIZE] = oldArg;
            oldArg = args[SEED];
            args[SEED] = "34.d4";
            Assert.assertEquals((-1), sg.run(args));
            args[SEED] = oldArg;
        } finally {
            TestLoadGenerator.DIR_STRUCTURE_FILE.delete();
            TestLoadGenerator.FILE_STRUCTURE_FILE.delete();
        }
    }

    /**
     * Test if the load generator works fine
     */
    @Test
    public void testLoadGenerator() throws Exception {
        final String TEST_SPACE_ROOT = "/test";
        final String SCRIPT_TEST_DIR = TestLoadGenerator.OUT_DIR.getAbsolutePath();
        String script = (SCRIPT_TEST_DIR + "/") + "loadgenscript";
        String script2 = (SCRIPT_TEST_DIR + "/") + "loadgenscript2";
        File scriptFile1 = new File(script);
        File scriptFile2 = new File(script2);
        FileWriter writer = new FileWriter(TestLoadGenerator.DIR_STRUCTURE_FILE);
        writer.write(((TestLoadGenerator.DIR_STRUCTURE_FIRST_LINE) + "\n"));
        writer.write(((TestLoadGenerator.DIR_STRUCTURE_SECOND_LINE) + "\n"));
        writer.close();
        writer = new FileWriter(TestLoadGenerator.FILE_STRUCTURE_FILE);
        writer.write(((TestLoadGenerator.FILE_STRUCTURE_FIRST_LINE) + "\n"));
        writer.write(((TestLoadGenerator.FILE_STRUCTURE_SECOND_LINE) + "\n"));
        writer.close();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestLoadGenerator.CONF).numDataNodes(3).build();
        cluster.waitActive();
        try {
            DataGenerator dg = new DataGenerator();
            dg.setConf(TestLoadGenerator.CONF);
            String[] args = new String[]{ "-inDir", TestLoadGenerator.OUT_DIR.getAbsolutePath(), "-root", TEST_SPACE_ROOT };
            Assert.assertEquals(0, dg.run(args));
            final int READ_PROBABILITY = 1;
            final int WRITE_PROBABILITY = 3;
            final int MAX_DELAY_BETWEEN_OPS = 7;
            final int NUM_OF_THREADS = 9;
            final int START_TIME = 11;
            final int ELAPSED_TIME = 13;
            LoadGenerator lg = new LoadGenerator();
            lg.setConf(TestLoadGenerator.CONF);
            args = new String[]{ "-readProbability", "0.3", "-writeProbability", "0.3", "-root", TEST_SPACE_ROOT, "-maxDelayBetweenOps", "0", "-numOfThreads", "1", "-startTime", Long.toString(Time.now()), "-elapsedTime", "10" };
            Assert.assertEquals(0, lg.run(args));
            String oldArg = args[READ_PROBABILITY];
            args[READ_PROBABILITY] = "1.1";
            Assert.assertEquals((-1), lg.run(args));
            args[READ_PROBABILITY] = "-1.1";
            Assert.assertEquals((-1), lg.run(args));
            args[READ_PROBABILITY] = oldArg;
            oldArg = args[WRITE_PROBABILITY];
            args[WRITE_PROBABILITY] = "1.1";
            Assert.assertEquals((-1), lg.run(args));
            args[WRITE_PROBABILITY] = "-1.1";
            Assert.assertEquals((-1), lg.run(args));
            args[WRITE_PROBABILITY] = "0.9";
            Assert.assertEquals((-1), lg.run(args));
            args[READ_PROBABILITY] = oldArg;
            oldArg = args[MAX_DELAY_BETWEEN_OPS];
            args[MAX_DELAY_BETWEEN_OPS] = "1.x1";
            Assert.assertEquals((-1), lg.run(args));
            args[MAX_DELAY_BETWEEN_OPS] = oldArg;
            oldArg = args[MAX_DELAY_BETWEEN_OPS];
            args[MAX_DELAY_BETWEEN_OPS] = "1.x1";
            Assert.assertEquals((-1), lg.run(args));
            args[MAX_DELAY_BETWEEN_OPS] = oldArg;
            oldArg = args[NUM_OF_THREADS];
            args[NUM_OF_THREADS] = "-1";
            Assert.assertEquals((-1), lg.run(args));
            args[NUM_OF_THREADS] = oldArg;
            oldArg = args[START_TIME];
            args[START_TIME] = "-1";
            Assert.assertEquals((-1), lg.run(args));
            args[START_TIME] = oldArg;
            oldArg = args[ELAPSED_TIME];
            args[ELAPSED_TIME] = "-1";
            Assert.assertEquals((-1), lg.run(args));
            args[ELAPSED_TIME] = oldArg;
            // test scripted operation
            // Test with good script
            FileWriter fw = new FileWriter(scriptFile1);
            fw.write("2 .22 .33\n");
            fw.write("3 .10 .6\n");
            fw.write("6 0 .7\n");
            fw.close();
            String[] scriptArgs = new String[]{ "-root", TEST_SPACE_ROOT, "-maxDelayBetweenOps", "0", "-numOfThreads", "10", "-startTime", Long.toString(Time.now()), "-scriptFile", script };
            Assert.assertEquals(0, lg.run(scriptArgs));
            // Test with bad script
            fw = new FileWriter(scriptFile2);
            fw.write("2 .22 .33\n");
            fw.write("3 blah blah blah .6\n");
            fw.write("6 0 .7\n");
            fw.close();
            scriptArgs[((scriptArgs.length) - 1)] = script2;
            Assert.assertEquals((-1), lg.run(scriptArgs));
        } finally {
            cluster.shutdown();
            TestLoadGenerator.DIR_STRUCTURE_FILE.delete();
            TestLoadGenerator.FILE_STRUCTURE_FILE.delete();
            scriptFile1.delete();
            scriptFile2.delete();
        }
    }
}

