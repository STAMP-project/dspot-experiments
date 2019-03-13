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
package org.apache.flink.yarn;


import java.util.UUID;
import org.apache.flink.util.TestLogger;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for verifying file staging during submission to YARN works with the S3A file system.
 *
 * <p>Note that the setup is similar to <tt>org.apache.flink.fs.s3hadoop.HadoopS3FileSystemITCase</tt>.
 */
public class YarnFileStageTestS3ITCase extends TestLogger {
    private static final String TEST_DATA_DIR = "tests-" + (UUID.randomUUID());

    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * Number of tests executed.
     */
    private static int numRecursiveUploadTests = 0;

    /**
     * Will be updated by {@link #checkCredentialsAndSetup()} if the test is not skipped.
     */
    private static boolean skipTest = true;

    @Test
    public void testRecursiveUploadForYarnS3n() throws Exception {
        try {
            Class.forName("org.apache.hadoop.fs.s3native.NativeS3FileSystem");
        } catch (ClassNotFoundException e) {
            // not in the classpath, cannot run this test
            String msg = "Skipping test because NativeS3FileSystem is not in the class path";
            log.info(msg);
            Assume.assumeNoException(msg, e);
        }
        testRecursiveUploadForYarn("s3n", "testYarn-s3n");
    }

    @Test
    public void testRecursiveUploadForYarnS3a() throws Exception {
        try {
            Class.forName("org.apache.hadoop.fs.s3a.S3AFileSystem");
        } catch (ClassNotFoundException e) {
            // not in the classpath, cannot run this test
            String msg = "Skipping test because S3AFileSystem is not in the class path";
            log.info(msg);
            Assume.assumeNoException(msg, e);
        }
        testRecursiveUploadForYarn("s3a", "testYarn-s3a");
    }
}

