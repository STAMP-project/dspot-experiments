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
package org.apache.hadoop.fs.s3a;


import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


/**
 * Tests of the S3A FileSystem which is closed; just make sure
 * that that basic file Ops fail meaningfully.
 */
public class ITestS3AClosedFS extends AbstractS3ATestBase {
    private Path root = new Path("/");

    @Test
    public void testClosedGetFileStatus() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().getFileStatus(root));
    }

    @Test
    public void testClosedListStatus() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().listStatus(root));
    }

    @Test
    public void testClosedListFile() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().listFiles(root, false));
    }

    @Test
    public void testClosedListLocatedStatus() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().listLocatedStatus(root));
    }

    @Test
    public void testClosedCreate() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().create(path("to-create")).close());
    }

    @Test
    public void testClosedDelete() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().delete(path("to-delete"), false));
    }

    @Test
    public void testClosedOpen() throws Exception {
        intercept(IOException.class, S3AUtils.E_FS_CLOSED, () -> getFileSystem().open(path("to-open")));
    }
}

