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
package org.apache.hadoop.fs.s3a.yarn;


import CreateFlag.CREATE;
import FileContext.DIR_DEFAULT_PERM;
import java.util.EnumSet;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * S3A tests through the {@link FileContext} API.
 */
public class ITestS3A {
    private FileContext fc;

    @Rule
    public final Timeout testTimeout = new Timeout(90000);

    @Test
    public void testS3AStatus() throws Exception {
        FsStatus fsStatus = fc.getFsStatus(null);
        Assert.assertNotNull(fsStatus);
        Assert.assertTrue(("Used capacity should be positive: " + (fsStatus.getUsed())), ((fsStatus.getUsed()) >= 0));
        Assert.assertTrue(("Remaining capacity should be positive: " + (fsStatus.getRemaining())), ((fsStatus.getRemaining()) >= 0));
        Assert.assertTrue(("Capacity should be positive: " + (fsStatus.getCapacity())), ((fsStatus.getCapacity()) >= 0));
    }

    @Test
    public void testS3ACreateFileInSubDir() throws Exception {
        Path dirPath = getTestPath();
        fc.mkdir(dirPath, DIR_DEFAULT_PERM, true);
        Path filePath = new Path(dirPath, "file");
        try (FSDataOutputStream file = fc.create(filePath, EnumSet.of(CREATE))) {
            file.write(666);
        }
    }
}

