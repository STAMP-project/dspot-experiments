/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl;


import HadoopTaskType.COMBINE;
import HadoopTaskType.REDUCE;
import com.google.common.base.Joiner;
import java.io.PrintWriter;
import java.net.URI;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.hadoop.HadoopFileBlock;
import org.apache.ignite.internal.processors.hadoop.HadoopJobEx;
import org.apache.ignite.internal.processors.hadoop.HadoopTaskInfo;
import org.apache.ignite.internal.processors.hadoop.HadoopTaskType;
import org.apache.ignite.internal.processors.hadoop.impl.examples.HadoopWordCount2;
import org.junit.Test;


/**
 * Tests of Map, Combine and Reduce task executions of any version of hadoop API.
 */
abstract class HadoopTasksVersionsAbstractTest extends HadoopAbstractWordCountTest {
    /**
     * Empty hosts array.
     */
    private static final String[] HOSTS = new String[0];

    /**
     * Tests map task execution.
     *
     * @throws Exception
     * 		If fails.
     */
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testMapTask() throws Exception {
        IgfsPath inDir = new IgfsPath(HadoopAbstractWordCountTest.PATH_INPUT);
        igfs.mkdirs(inDir);
        IgfsPath inFile = new IgfsPath(inDir, ((HadoopWordCount2.class.getSimpleName()) + "-input"));
        URI inFileUri = URI.create(((igfsScheme()) + (inFile.toString())));
        try (PrintWriter pw = new PrintWriter(igfs.create(inFile, true))) {
            pw.println("hello0 world0");
            pw.println("world1 hello1");
        }
        HadoopFileBlock fileBlock1 = new HadoopFileBlock(HadoopTasksVersionsAbstractTest.HOSTS, inFileUri, 0, ((igfs.info(inFile).length()) - 1));
        try (PrintWriter pw = new PrintWriter(igfs.append(inFile, false))) {
            pw.println("hello2 world2");
            pw.println("world3 hello3");
        }
        HadoopFileBlock fileBlock2 = new HadoopFileBlock(HadoopTasksVersionsAbstractTest.HOSTS, inFileUri, fileBlock1.length(), ((igfs.info(inFile).length()) - (fileBlock1.length())));
        HadoopJobEx gridJob = getHadoopJob(((igfsScheme()) + (inFile.toString())), ((igfsScheme()) + (HadoopAbstractWordCountTest.PATH_OUTPUT)));
        HadoopTaskInfo taskInfo = new HadoopTaskInfo(HadoopTaskType.MAP, gridJob.id(), 0, 0, fileBlock1);
        HadoopTestTaskContext ctx = new HadoopTestTaskContext(taskInfo, gridJob);
        ctx.mockOutput().clear();
        run();
        assertEquals("hello0,1; world0,1; world1,1; hello1,1", Joiner.on("; ").join(ctx.mockOutput()));
        ctx.mockOutput().clear();
        taskInfo(new HadoopTaskInfo(HadoopTaskType.MAP, gridJob.id(), 0, 0, fileBlock2));
        run();
        assertEquals("hello2,1; world2,1; world3,1; hello3,1", Joiner.on("; ").join(ctx.mockOutput()));
    }

    /**
     * Tests reduce task execution.
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testReduceTask() throws Exception {
        HadoopJobEx gridJob = getHadoopJob(((igfsScheme()) + (HadoopAbstractWordCountTest.PATH_INPUT)), ((igfsScheme()) + (HadoopAbstractWordCountTest.PATH_OUTPUT)));
        runTaskWithInput(gridJob, REDUCE, 0, "word1", "5", "word2", "10");
        runTaskWithInput(gridJob, REDUCE, 1, "word3", "7", "word4", "15");
        assertEquals(("word1\t5\n" + "word2\t10\n"), readAndSortFile(((((HadoopAbstractWordCountTest.PATH_OUTPUT) + "/_temporary/0/task_00000000-0000-0000-0000-000000000000_0000_r_000000/") + (getOutputFileNamePrefix())) + "00000")));
        assertEquals(("word3\t7\n" + "word4\t15\n"), readAndSortFile(((((HadoopAbstractWordCountTest.PATH_OUTPUT) + "/_temporary/0/task_00000000-0000-0000-0000-000000000000_0000_r_000001/") + (getOutputFileNamePrefix())) + "00001")));
    }

    /**
     * Tests combine task execution.
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testCombinerTask() throws Exception {
        HadoopJobEx gridJob = getHadoopJob("/", "/");
        HadoopTestTaskContext ctx = runTaskWithInput(gridJob, COMBINE, 0, "word1", "5", "word2", "10");
        assertEquals("word1,5; word2,10", Joiner.on("; ").join(ctx.mockOutput()));
        ctx = runTaskWithInput(gridJob, COMBINE, 1, "word3", "7", "word4", "15");
        assertEquals("word3,7; word4,15", Joiner.on("; ").join(ctx.mockOutput()));
    }

    /**
     * Tests all job in complex.
     * Runs 2 chains of map-combine tasks and sends result into one reduce task.
     *
     * @throws Exception
     * 		If fails.
     */
    @SuppressWarnings("ConstantConditions")
    @Test
    public void testAllTasks() throws Exception {
        IgfsPath inDir = new IgfsPath(HadoopAbstractWordCountTest.PATH_INPUT);
        igfs.mkdirs(inDir);
        IgfsPath inFile = new IgfsPath(inDir, ((HadoopWordCount2.class.getSimpleName()) + "-input"));
        URI inFileUri = URI.create(((igfsScheme()) + (inFile.toString())));
        generateTestFile(inFile.toString(), "red", 100, "blue", 200, "green", 150, "yellow", 70);
        // Split file into two blocks
        long fileLen = igfs.info(inFile).length();
        Long l = fileLen / 2;
        HadoopFileBlock fileBlock1 = new HadoopFileBlock(HadoopTasksVersionsAbstractTest.HOSTS, inFileUri, 0, l);
        HadoopFileBlock fileBlock2 = new HadoopFileBlock(HadoopTasksVersionsAbstractTest.HOSTS, inFileUri, l, (fileLen - l));
        HadoopJobEx gridJob = getHadoopJob(inFileUri.toString(), ((igfsScheme()) + (HadoopAbstractWordCountTest.PATH_OUTPUT)));
        HadoopTestTaskContext combine1Ctx = runMapCombineTask(fileBlock1, gridJob);
        HadoopTestTaskContext combine2Ctx = runMapCombineTask(fileBlock2, gridJob);
        // Prepare input for combine
        HadoopTaskInfo taskInfo = new HadoopTaskInfo(HadoopTaskType.REDUCE, gridJob.id(), 0, 0, null);
        HadoopTestTaskContext reduceCtx = new HadoopTestTaskContext(taskInfo, gridJob);
        reduceCtx.makeTreeOfWritables(combine1Ctx.mockOutput());
        reduceCtx.makeTreeOfWritables(combine2Ctx.mockOutput());
        run();
        taskInfo(new HadoopTaskInfo(HadoopTaskType.COMMIT, gridJob.id(), 0, 0, null));
        run();
        assertEquals(("blue\t200\n" + (("green\t150\n" + "red\t100\n") + "yellow\t70\n")), readAndSortFile(((((HadoopAbstractWordCountTest.PATH_OUTPUT) + "/") + (getOutputFileNamePrefix())) + "00000")));
    }
}

