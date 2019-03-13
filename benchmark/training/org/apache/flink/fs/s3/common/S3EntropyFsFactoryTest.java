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
package org.apache.flink.fs.s3.common;


import java.net.URI;
import java.util.Collections;
import javax.annotation.Nullable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.fs.s3.common.writer.S3AccessHelper;
import org.apache.flink.util.TestLogger;
import org.apache.hadoop.fs.FileSystem;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests that the file system factory picks up the entropy configuration properly.
 */
public class S3EntropyFsFactoryTest extends TestLogger {
    @Test
    public void testEntropyInjectionConfig() throws Exception {
        final Configuration conf = new Configuration();
        conf.setString("s3.entropy.key", "__entropy__");
        conf.setInteger("s3.entropy.length", 7);
        S3EntropyFsFactoryTest.TestFsFactory factory = new S3EntropyFsFactoryTest.TestFsFactory();
        factory.configure(conf);
        FlinkS3FileSystem fs = ((FlinkS3FileSystem) (create(new URI("s3://test"))));
        Assert.assertEquals("__entropy__", fs.getEntropyInjectionKey());
        Assert.assertEquals(7, fs.generateEntropy().length());
    }

    /**
     * Test validates that the produced by AbstractS3FileSystemFactory object will contains
     * only first path from multiple paths in config.
     */
    @Test
    public void testMultipleTempDirsConfig() throws Exception {
        final Configuration conf = new Configuration();
        String dir1 = "/tmp/dir1";
        String dir2 = "/tmp/dir2";
        conf.setString("io.tmp.dirs", ((dir1 + ",") + dir2));
        S3EntropyFsFactoryTest.TestFsFactory factory = new S3EntropyFsFactoryTest.TestFsFactory();
        factory.configure(conf);
        FlinkS3FileSystem fs = ((FlinkS3FileSystem) (create(new URI("s3://test"))));
        Assert.assertEquals(fs.getLocalTmpDir(), dir1);
    }

    // ------------------------------------------------------------------------
    private static final class TestFsFactory extends AbstractS3FileSystemFactory {
        TestFsFactory() {
            super("testFs", new HadoopConfigLoader(new String[0], new String[0][], "", Collections.emptySet(), Collections.emptySet(), ""));
        }

        @Override
        protected FileSystem createHadoopFileSystem() {
            return Mockito.mock(FileSystem.class);
        }

        @Override
        protected URI getInitURI(URI fsUri, org.apache.hadoop.conf.Configuration hadoopConfig) {
            return fsUri;
        }

        @Nullable
        @Override
        protected S3AccessHelper getS3AccessHelper(FileSystem fs) {
            return null;
        }

        @Override
        public String getScheme() {
            return "test";
        }
    }
}

