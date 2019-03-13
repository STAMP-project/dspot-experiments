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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.igfs.IgfsEx;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test of integration with Hadoop client via command line interface.
 */
public class HadoopCommandLineTest extends GridCommonAbstractTest {
    /**
     * IGFS instance.
     */
    private static IgfsEx igfs;

    /**
     *
     */
    private static final String igfsName = "igfs";

    /**
     *
     */
    private static File testWorkDir;

    /**
     *
     */
    private static String hadoopHome;

    /**
     *
     */
    private static String hiveHome;

    /**
     *
     */
    private static File examplesJar;

    /**
     * Tests Hadoop command line integration.
     */
    @Test
    public void testHadoopCommandLine() throws Exception {
        assertEquals(0, executeHadoopCmd("fs", "-ls", "/"));
        assertEquals(0, executeHadoopCmd("fs", "-mkdir", "/input"));
        assertEquals(0, executeHadoopCmd("fs", "-put", new File(HadoopCommandLineTest.testWorkDir, "test-data").getAbsolutePath(), "/input"));
        assertTrue(HadoopCommandLineTest.igfs.exists(new IgfsPath("/input/test-data")));
        assertEquals(0, executeHadoopCmd("jar", HadoopCommandLineTest.examplesJar.getAbsolutePath(), "wordcount", "/input", "/output"));
        IgfsPath path = new IgfsPath((("/user/" + (System.getProperty("user.name"))) + "/"));
        assertTrue(HadoopCommandLineTest.igfs.exists(path));
        IgfsPath jobStatPath = null;
        for (IgfsPath jobPath : HadoopCommandLineTest.igfs.listPaths(path)) {
            assertNull(jobStatPath);
            jobStatPath = jobPath;
        }
        File locStatFile = new File(HadoopCommandLineTest.testWorkDir, "performance");
        assertEquals(0, executeHadoopCmd("fs", "-get", ((jobStatPath.toString()) + "/performance"), locStatFile.toString()));
        long evtCnt = HadoopTestUtils.simpleCheckJobStatFile(new BufferedReader(new FileReader(locStatFile)));
        assertTrue((evtCnt >= 22));// It's the minimum amount of events for job with combiner.

        assertTrue(HadoopCommandLineTest.igfs.exists(new IgfsPath("/output")));
        BufferedReader in = new BufferedReader(new InputStreamReader(HadoopCommandLineTest.igfs.open(new IgfsPath("/output/part-r-00000"))));
        List<String> res = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null)
            res.add(line);

        Collections.sort(res);
        assertEquals("[blue\t150, green\t200, red\t100, yellow\t50]", res.toString());
    }
}

