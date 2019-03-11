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
package org.apache.hadoop.hive.common;


import HadoopShims.MiniDFSShim;
import HiveConf.ConfVars.HIVE_EXEC_COPYFILE_MAXNUMFILES.varname;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for {{@link FileUtils}. Tests run against a {@link HadoopShims.MiniDFSShim}.
 */
public class TestFileUtils {
    private static final Path basePath = new Path("/tmp/");

    private static HiveConf conf;

    private static FileSystem fs;

    private static MiniDFSShim dfs;

    @Test
    public void testCopySingleEmptyFile() throws IOException {
        String file1Name = "file1.txt";
        Path copySrc = new Path(TestFileUtils.basePath, "copySrc");
        Path copyDst = new Path(TestFileUtils.basePath, "copyDst");
        try {
            TestFileUtils.fs.create(new Path(TestFileUtils.basePath, new Path(copySrc, file1Name))).close();
            Assert.assertTrue("FileUtils.copy failed to copy data", FileUtils.copy(TestFileUtils.fs, copySrc, TestFileUtils.fs, copyDst, false, false, TestFileUtils.conf));
            Path dstFileName1 = new Path(copyDst, file1Name);
            Assert.assertTrue(TestFileUtils.fs.exists(new Path(copyDst, file1Name)));
            Assert.assertEquals(TestFileUtils.fs.getFileStatus(dstFileName1).getLen(), 0);
        } finally {
            try {
                TestFileUtils.fs.delete(copySrc, true);
                TestFileUtils.fs.delete(copyDst, true);
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    @Test
    public void testCopyWithDistcp() throws IOException {
        String file1Name = "file1.txt";
        String file2Name = "file2.txt";
        Path copySrc = new Path(TestFileUtils.basePath, "copySrc");
        Path copyDst = new Path(TestFileUtils.basePath, "copyDst");
        Path srcFile1 = new Path(TestFileUtils.basePath, new Path(copySrc, file1Name));
        Path srcFile2 = new Path(TestFileUtils.basePath, new Path(copySrc, file2Name));
        try {
            OutputStream os1 = TestFileUtils.fs.create(srcFile1);
            os1.write(new byte[]{ 1, 2, 3 });
            os1.close();
            OutputStream os2 = TestFileUtils.fs.create(srcFile2);
            os2.write(new byte[]{ 1, 2, 3 });
            os2.close();
            TestFileUtils.conf.set(varname, "1");
            TestFileUtils.conf.set(HiveConf.ConfVars.HIVE_EXEC_COPYFILE_MAXSIZE.varname, "1");
            Assert.assertTrue("FileUtils.copy failed to copy data", FileUtils.copy(TestFileUtils.fs, copySrc, TestFileUtils.fs, copyDst, false, false, TestFileUtils.conf));
            Path dstFileName1 = new Path(copyDst, file1Name);
            Assert.assertTrue(TestFileUtils.fs.exists(new Path(copyDst, file1Name)));
            Assert.assertEquals(TestFileUtils.fs.getFileStatus(dstFileName1).getLen(), 3);
            Path dstFileName2 = new Path(copyDst, file2Name);
            Assert.assertTrue(TestFileUtils.fs.exists(new Path(copyDst, file2Name)));
            Assert.assertEquals(TestFileUtils.fs.getFileStatus(dstFileName2).getLen(), 3);
        } finally {
            try {
                TestFileUtils.fs.delete(copySrc, true);
                TestFileUtils.fs.delete(copyDst, true);
            } catch (IOException e) {
                // Do nothing
            }
        }
    }
}

