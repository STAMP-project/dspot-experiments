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
package org.apache.flink.fs.s3presto;


import com.facebook.presto.hive.s3.PrestoS3FileSystem;
import java.io.IOException;
import java.util.UUID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.fs.hdfs.AbstractHadoopFileSystemITTest;
import org.apache.flink.testutils.s3.S3TestCredentials;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for the S3 file system support via Presto's {@link com.facebook.presto.hive.s3.PrestoS3FileSystem}.
 *
 * <p><strong>BEWARE</strong>: tests must take special care of S3's
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/dev/Introduction.html#ConsistencyModel">consistency guarantees</a>
 * and what the {@link com.facebook.presto.hive.s3.PrestoS3FileSystem} offers.
 */
@RunWith(Parameterized.class)
public class PrestoS3FileSystemITCase extends AbstractHadoopFileSystemITTest {
    @Parameterized.Parameter
    public String scheme;

    private static final String TEST_DATA_DIR = "tests-" + (UUID.randomUUID());

    @Test
    public void testConfigKeysForwarding() throws Exception {
        final Path path = basePath;
        // access without credentials should fail
        {
            Configuration conf = new Configuration();
            // fail fast and do not fall back to trying EC2 credentials
            conf.setString(PrestoS3FileSystem.S3_USE_INSTANCE_CREDENTIALS, "false");
            FileSystem.initialize(conf);
            try {
                path.getFileSystem().exists(path);
                Assert.fail("should fail with an exception");
            } catch (IOException ignored) {
            }
        }
        // standard Presto-style credential keys
        {
            Configuration conf = new Configuration();
            conf.setString(PrestoS3FileSystem.S3_USE_INSTANCE_CREDENTIALS, "false");
            conf.setString("presto.s3.access-key", S3TestCredentials.getS3AccessKey());
            conf.setString("presto.s3.secret-key", S3TestCredentials.getS3SecretKey());
            FileSystem.initialize(conf);
            path.getFileSystem().exists(path);
        }
        // shortened Presto-style credential keys
        {
            Configuration conf = new Configuration();
            conf.setString(PrestoS3FileSystem.S3_USE_INSTANCE_CREDENTIALS, "false");
            conf.setString("s3.access-key", S3TestCredentials.getS3AccessKey());
            conf.setString("s3.secret-key", S3TestCredentials.getS3SecretKey());
            FileSystem.initialize(conf);
            path.getFileSystem().exists(path);
        }
        // shortened Hadoop-style credential keys
        {
            Configuration conf = new Configuration();
            conf.setString(PrestoS3FileSystem.S3_USE_INSTANCE_CREDENTIALS, "false");
            conf.setString("s3.access.key", S3TestCredentials.getS3AccessKey());
            conf.setString("s3.secret.key", S3TestCredentials.getS3SecretKey());
            FileSystem.initialize(conf);
            path.getFileSystem().exists(path);
        }
        // shortened Hadoop-style credential keys with presto prefix
        {
            Configuration conf = new Configuration();
            conf.setString(PrestoS3FileSystem.S3_USE_INSTANCE_CREDENTIALS, "false");
            conf.setString("presto.s3.access.key", S3TestCredentials.getS3AccessKey());
            conf.setString("presto.s3.secret.key", S3TestCredentials.getS3SecretKey());
            FileSystem.initialize(conf);
            path.getFileSystem().exists(path);
        }
        // re-set configuration
        FileSystem.initialize(new Configuration());
    }
}

