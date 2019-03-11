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
package org.apache.hadoop.hive.ql.session;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Assert;
import org.junit.Test;

import static SessionState.LOCK_FILE_NAME;


public class TestClearDanglingScratchDir {
    private static MiniDFSCluster m_dfs = null;

    private static HiveConf conf;

    private static Path scratchDir;

    private ByteArrayOutputStream stdout;

    private ByteArrayOutputStream stderr;

    private PrintStream origStdoutPs;

    private PrintStream origStderrPs;

    @Test
    public void testClearDanglingScratchDir() throws Exception {
        // No scratch dir initially
        redirectStdOutErr();
        ClearDanglingScratchDir.main(new String[]{ "-v", "-s", (TestClearDanglingScratchDir.m_dfs.getFileSystem().getUri().toString()) + (TestClearDanglingScratchDir.scratchDir.toUri().toString()) });
        rollbackStdOutErr();
        Assert.assertTrue(stderr.toString().contains("Cannot find any scratch directory to clear"));
        // Create scratch dir without lock files
        TestClearDanglingScratchDir.m_dfs.getFileSystem().mkdirs(new Path(new Path(TestClearDanglingScratchDir.scratchDir, "dummy"), UUID.randomUUID().toString()));
        redirectStdOutErr();
        ClearDanglingScratchDir.main(new String[]{ "-v", "-s", (TestClearDanglingScratchDir.m_dfs.getFileSystem().getUri().toString()) + (TestClearDanglingScratchDir.scratchDir.toUri().toString()) });
        rollbackStdOutErr();
        Assert.assertEquals(StringUtils.countMatches(stderr.toString(), ("since it does not contain " + (LOCK_FILE_NAME))), 1);
        Assert.assertTrue(stderr.toString().contains("Cannot find any scratch directory to clear"));
        // One live session
        SessionState ss = SessionState.start(TestClearDanglingScratchDir.conf);
        redirectStdOutErr();
        ClearDanglingScratchDir.main(new String[]{ "-v", "-s", (TestClearDanglingScratchDir.m_dfs.getFileSystem().getUri().toString()) + (TestClearDanglingScratchDir.scratchDir.toUri().toString()) });
        rollbackStdOutErr();
        Assert.assertEquals(StringUtils.countMatches(stderr.toString(), "is being used by live process"), 1);
        // One dead session with dry-run
        ss.releaseSessionLockFile();
        redirectStdOutErr();
        ClearDanglingScratchDir.main(new String[]{ "-r", "-v", "-s", (TestClearDanglingScratchDir.m_dfs.getFileSystem().getUri().toString()) + (TestClearDanglingScratchDir.scratchDir.toUri().toString()) });
        rollbackStdOutErr();
        // Find one session dir to remove
        Assert.assertFalse(stdout.toString().isEmpty());
        // Remove the dead session dir
        redirectStdOutErr();
        ClearDanglingScratchDir.main(new String[]{ "-v", "-s", (TestClearDanglingScratchDir.m_dfs.getFileSystem().getUri().toString()) + (TestClearDanglingScratchDir.scratchDir.toUri().toString()) });
        rollbackStdOutErr();
        Assert.assertTrue(stderr.toString().contains("Removing 1 scratch directories"));
        Assert.assertEquals(StringUtils.countMatches(stderr.toString(), "removed"), 1);
        ss.close();
    }
}

