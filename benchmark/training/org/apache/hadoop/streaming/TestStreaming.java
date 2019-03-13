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
package org.apache.hadoop.streaming;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.util.JarFinder;
import org.apache.hadoop.util.Shell;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests hadoopStreaming in MapReduce local mode.
 */
public class TestStreaming {
    public static final String STREAMING_JAR = JarFinder.getJar(StreamJob.class);

    /**
     * cat command used for copying stdin to stdout as mapper or reducer function.
     * On Windows, use a cmd script that approximates the functionality of cat.
     */
    static final String CAT = (Shell.WINDOWS) ? "cmd /c " + (new File("target/bin/cat.cmd").getAbsolutePath()) : "cat";

    /**
     * Command used for iterating through file names on stdin and copying each
     * file's contents to stdout, used as mapper or reducer function.  On Windows,
     * use a cmd script that approximates the functionality of xargs cat.
     */
    static final String XARGS_CAT = (Shell.WINDOWS) ? "cmd /c " + (new File("target/bin/xargs_cat.cmd").getAbsolutePath()) : "xargs cat";

    // "map" command: grep -E (red|green|blue)
    // reduce command: uniq
    protected File TEST_DIR;

    protected File INPUT_FILE;

    protected File OUTPUT_DIR;

    protected String inputFile;

    protected String outDir;

    protected String input = "roses.are.red\nviolets.are.blue\nbunnies.are.pink\n";

    // map behaves like "/usr/bin/tr . \\n"; (split words into lines)
    protected String map = UtilTest.makeJavaCommand(TrApp.class, new String[]{ ".", "\\n" });

    // reduce behave like /usr/bin/uniq. But also prepend lines with R.
    // command-line combiner does not have any effect any more.
    protected String reduce = UtilTest.makeJavaCommand(UniqApp.class, new String[]{ "R" });

    protected String outputExpect = "Rare\t\nRblue\t\nRbunnies\t\nRpink\t\nRred\t\nRroses\t\nRviolets\t\n";

    protected ArrayList<String> args = new ArrayList<String>();

    protected StreamJob job;

    public TestStreaming() throws IOException {
        UtilTest utilTest = new UtilTest(getClass().getName());
        utilTest.checkUserDir();
        utilTest.redirectIfAntJunit();
        setTestDir(new File("target/TestStreaming").getAbsoluteFile());
    }

    @Test
    public void testCommandLine() throws Exception {
        int ret = runStreamJob();
        Assert.assertEquals(0, ret);
        checkOutput();
    }
}

