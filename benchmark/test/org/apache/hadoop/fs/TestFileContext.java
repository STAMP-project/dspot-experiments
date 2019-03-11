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
package org.apache.hadoop.fs;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import FileSystem.FS_DEFAULT_NAME_KEY;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileContext {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileContext.class);

    @Test
    public void testDefaultURIWithoutScheme() throws Exception {
        final Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, "/");
        try {
            FileContext.getFileContext(conf);
            Assert.fail(((UnsupportedFileSystemException.class) + " not thrown!"));
        } catch (UnsupportedFileSystemException ufse) {
            TestFileContext.LOG.info("Expected exception: ", ufse);
        }
    }

    @Test
    public void testConfBasedAndAPIBasedSetUMask() throws Exception {
        Configuration conf = new Configuration();
        String defaultlUMask = conf.get(FS_PERMISSIONS_UMASK_KEY);
        Assert.assertEquals("Default UMask changed!", "022", defaultlUMask);
        URI uri1 = new URI("file://mydfs:50070/");
        URI uri2 = new URI("file://tmp");
        FileContext fc1 = FileContext.getFileContext(uri1, conf);
        FileContext fc2 = FileContext.getFileContext(uri2, conf);
        Assert.assertEquals("Umask for fc1 is incorrect", 18, fc1.getUMask().toShort());
        Assert.assertEquals("Umask for fc2 is incorrect", 18, fc2.getUMask().toShort());
        // Till a user explicitly calls FileContext.setUMask(), the updates through
        // configuration should be reflected..
        conf.set(FS_PERMISSIONS_UMASK_KEY, "011");
        Assert.assertEquals("Umask for fc1 is incorrect", 9, fc1.getUMask().toShort());
        Assert.assertEquals("Umask for fc2 is incorrect", 9, fc2.getUMask().toShort());
        // Stop reflecting the conf update for specific FileContexts, once an
        // explicit setUMask is done.
        conf.set(FS_PERMISSIONS_UMASK_KEY, "066");
        fc1.setUMask(FsPermission.createImmutable(((short) (27))));
        Assert.assertEquals("Umask for fc1 is incorrect", 27, fc1.getUMask().toShort());
        Assert.assertEquals("Umask for fc2 is incorrect", 54, fc2.getUMask().toShort());
        conf.set(FS_PERMISSIONS_UMASK_KEY, "077");
        fc2.setUMask(FsPermission.createImmutable(((short) (36))));
        Assert.assertEquals("Umask for fc1 is incorrect", 27, fc1.getUMask().toShort());
        Assert.assertEquals("Umask for fc2 is incorrect", 36, fc2.getUMask().toShort());
    }
}

