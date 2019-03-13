/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.s3a;


import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathExistsException;
import org.junit.Test;


/**
 * Test {@link S3AFileSystem#copyFromLocalFile(boolean, boolean, Path, Path)}.
 * Some of the tests have been disabled pending a fix for HADOOP-15932 and
 * recursive directory copying; the test cases themselves may be obsolete.
 */
public class ITestS3ACopyFromLocalFile extends AbstractS3ATestBase {
    private static final Charset ASCII = StandardCharsets.US_ASCII;

    private File file;

    @Test
    public void testCopyEmptyFile() throws Throwable {
        file = File.createTempFile("test", ".txt");
        Path dest = upload(file, true);
        assertPathExists("uploaded file", dest);
    }

    @Test
    public void testCopyFile() throws Throwable {
        String message = "hello";
        file = createTempFile(message);
        Path dest = upload(file, true);
        assertPathExists("uploaded file not found", dest);
        S3AFileSystem fs = getFileSystem();
        FileStatus status = fs.getFileStatus(dest);
        assertEquals(("File length of " + status), message.getBytes(ITestS3ACopyFromLocalFile.ASCII).length, status.getLen());
        assertFileTextEquals(dest, message);
    }

    @Test
    public void testCopyFileNoOverwrite() throws Throwable {
        file = createTempFile("hello");
        Path dest = upload(file, true);
        // HADOOP-15932: the exception type changes here
        intercept(PathExistsException.class, () -> upload(file, false));
    }

    @Test
    public void testCopyFileOverwrite() throws Throwable {
        file = createTempFile("hello");
        Path dest = upload(file, true);
        String updated = "updated";
        FileUtils.write(file, updated, ITestS3ACopyFromLocalFile.ASCII);
        upload(file, true);
        assertFileTextEquals(dest, updated);
    }

    @Test
    public void testCopyMissingFile() throws Throwable {
        file = File.createTempFile("test", ".txt");
        file.delete();
        // first upload to create
        intercept(FileNotFoundException.class, "", () -> upload(file, true));
    }

    @Test
    public void testLocalFilesOnly() throws Throwable {
        Path dst = path("testLocalFilesOnly");
        intercept(IllegalArgumentException.class, () -> {
            getFileSystem().copyFromLocalFile(false, true, dst, dst);
            return "copy successful";
        });
    }
}

