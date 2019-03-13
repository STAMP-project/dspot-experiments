/**
 * Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.zeroturnaround.zip;


import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.zeroturnaround.zip.commons.FileUtils;


public class FilePermissionsTest {
    private final File testFile = new File(getClass().getClassLoader().getResource("TestFile.txt").getPath());

    @Test
    public void testPreserveExecuteFlag() throws Exception {
        String dirName = "FilePermissionsTest-e";
        File tmpDir = File.createTempFile(dirName, null);
        tmpDir.delete();
        tmpDir.mkdir();
        File fileA = new File(tmpDir, "fileA.txt");
        File fileB = new File(tmpDir, "fileB.txt");
        FileUtils.copyFile(testFile, fileA);
        FileUtils.copyFile(testFile, fileB);
        Assume.assumeTrue(setExecutable(fileA, false));
        // Avoids failing test on Windows: File.setExecutable(): "If executable is false and the underlying file system does not implement an execute permission, then the operation will fail."
        setExecutable(fileA, true);
        setExecutable(fileB, false);
        // TESTS BEFORE ZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canExecute(fileA));
        Assert.assertFalse(canExecute(fileB));
        Assert.assertTrue(doZipAndUnpack(dirName, ".zip", tmpDir));
        // SAME TESTS AFTER ZIP & UNZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canExecute(fileA));
        Assert.assertFalse(canExecute(fileB));
    }

    @Test
    public void testPreserveReadFlag() throws Exception {
        String dirName = "FilePermissionsTest-r";
        File tmpDir = File.createTempFile(dirName, null);
        tmpDir.delete();
        tmpDir.mkdir();
        File fileA = new File(tmpDir, "fileA.txt");
        File fileB = new File(tmpDir, "fileB.txt");
        FileUtils.copyFile(testFile, fileA);
        FileUtils.copyFile(testFile, fileB);
        Assume.assumeTrue(setReadable(fileA, false));
        // Avoids failing test on Windows: File.setReadable(): "If readable is false and the underlying file system does not implement a read permission, then the operation will fail."
        setReadable(fileA, true);
        setReadable(fileB, false);
        setReadable(fileB, true);// if we set read permission to false, then we can't zip the file, causing the test to fail with a permission exception

        // TESTS BEFORE ZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canRead(fileA));
        Assert.assertTrue(canRead(fileB));
        Assert.assertTrue(doZipAndUnpack(dirName, ".zip", tmpDir));
        // SAME TESTS AFTER ZIP & UNZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canRead(fileA));
        Assert.assertTrue(canRead(fileB));
    }

    /**
     * This is the only test that can be run on Windows to test that permissions are kept after zip and unzip.
     */
    @Test
    public void testPreserveWriteFlag() throws Exception {
        String dirName = "FilePermissionsTest-w";
        File tmpDir = File.createTempFile(dirName, null);
        tmpDir.delete();
        tmpDir.mkdir();
        File fileA = new File(tmpDir, "fileA.txt");
        File fileB = new File(tmpDir, "fileB.txt");
        FileUtils.copyFile(testFile, fileA, true);
        FileUtils.copyFile(testFile, fileB, true);
        // Assume.assumeTrue(setWritable(fileA, false));
        // this is commented because there is no OS-specific logic for returning false in File.setWritable(), only "The operation will fail if the user does not have permission to change the access permissions of this abstract pathname."
        setWritable(fileA, true);
        setWritable(fileB, false);
        // TESTS BEFORE ZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canWrite(fileA));
        Assert.assertFalse(canWrite(fileB));
        Assert.assertTrue(doZipAndUnpack(dirName, ".zip", tmpDir));
        // SAME TESTS AFTER ZIP & UNZIP
        Assert.assertTrue(((fileA.exists()) && (fileB.exists())));
        Assert.assertTrue(canWrite(fileA));
        Assert.assertFalse(canWrite(fileB));
    }
}

