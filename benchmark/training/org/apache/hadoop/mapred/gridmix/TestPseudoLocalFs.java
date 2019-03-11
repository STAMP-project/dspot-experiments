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
package org.apache.hadoop.mapred.gridmix;


import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the basic functionality of PseudoLocalFs
 */
public class TestPseudoLocalFs {
    /**
     * Test if a file on PseudoLocalFs of a specific size can be opened and read.
     * Validate the size of the data read.
     * Test the read methods of {@link PseudoLocalFs.RandomInputStream}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPseudoLocalFsFileSize() throws Exception {
        long fileSize = 10000;
        Path path = PseudoLocalFs.generateFilePath("myPsedoFile", fileSize);
        PseudoLocalFs pfs = new PseudoLocalFs();
        pfs.create(path);
        // Read 1 byte at a time and validate file size.
        InputStream in = pfs.open(path, 0);
        long totalSize = 0;
        while ((in.read()) >= 0) {
            ++totalSize;
        } 
        in.close();
        Assert.assertEquals("File size mismatch with read().", fileSize, totalSize);
        // Read data from PseudoLocalFs-based file into buffer to
        // validate read(byte[]) and file size.
        in = pfs.open(path, 0);
        totalSize = 0;
        byte[] b = new byte[1024];
        int bytesRead = in.read(b);
        while (bytesRead >= 0) {
            totalSize += bytesRead;
            bytesRead = in.read(b);
        } 
        Assert.assertEquals("File size mismatch with read(byte[]).", fileSize, totalSize);
    }

    /**
     * Test Pseudo Local File System methods like getFileStatus(), create(),
     *  open(), exists() for <li> valid file paths and <li> invalid file paths.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testPseudoLocalFsFileNames() throws IOException {
        PseudoLocalFs pfs = new PseudoLocalFs();
        Configuration conf = new Configuration();
        conf.setClass("fs.pseudo.impl", PseudoLocalFs.class, FileSystem.class);
        Path path = new Path("pseudo:///myPsedoFile.1234");
        FileSystem testFs = path.getFileSystem(conf);
        Assert.assertEquals("Failed to obtain a pseudo local file system object from path", pfs.getUri().getScheme(), testFs.getUri().getScheme());
        // Validate PseudoLocalFS operations on URI of some other file system
        path = new Path("file:///myPsedoFile.12345");
        validateGetFileStatus(pfs, path, false);
        validateCreate(pfs, path, false);
        validateOpen(pfs, path, false);
        validateExists(pfs, path, false);
        path = new Path("pseudo:///myPsedoFile");// .<fileSize> missing

        validateGetFileStatus(pfs, path, false);
        validateCreate(pfs, path, false);
        validateOpen(pfs, path, false);
        validateExists(pfs, path, false);
        // thing after final '.' is not a number
        path = new Path("pseudo:///myPsedoFile.txt");
        validateGetFileStatus(pfs, path, false);
        validateCreate(pfs, path, false);
        validateOpen(pfs, path, false);
        validateExists(pfs, path, false);
        // Generate valid file name(relative path) and validate operations on it
        long fileSize = 231456;
        path = PseudoLocalFs.generateFilePath("my.Psedo.File", fileSize);
        // Validate the above generateFilePath()
        Assert.assertEquals("generateFilePath() failed.", fileSize, pfs.validateFileNameFormat(path));
        validateGetFileStatus(pfs, path, true);
        validateCreate(pfs, path, true);
        validateOpen(pfs, path, true);
        validateExists(pfs, path, true);
        // Validate operations on valid qualified path
        path = new Path("myPsedoFile.1237");
        path = pfs.makeQualified(path);
        validateGetFileStatus(pfs, path, true);
        validateCreate(pfs, path, true);
        validateOpen(pfs, path, true);
        validateExists(pfs, path, true);
    }
}

