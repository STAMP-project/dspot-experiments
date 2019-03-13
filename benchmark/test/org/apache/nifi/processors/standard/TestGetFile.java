/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CoreAttributes.ABSOLUTE_PATH;
import GetFile.DIRECTORY;
import GetFile.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import static GetFile.FILE_MODIFY_DATE_ATTR_FORMAT;


public class TestGetFile {
    @Test
    public void testWithInaccessibleDir() throws IOException {
        // Some systems don't support POSIX (Windows) and will fail if run. Should ignore the test in that event
        if (!(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"))) {
            return;
        }
        File inaccessibleDir = new File("target/inaccessible");
        inaccessibleDir.deleteOnExit();
        inaccessibleDir.mkdir();
        Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
        Files.setPosixFilePermissions(inaccessibleDir.toPath(), posixFilePermissions);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, inaccessibleDir.getAbsolutePath());
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getMessage().endsWith("does not have sufficient permissions (i.e., not writable and readable)"));
        }
    }

    @Test
    public void testWithUnreadableDir() throws IOException {
        // Some systems don't support POSIX (Windows) and will fail if run. Should ignore the test in that event
        if (!(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"))) {
            return;
        }
        File unreadableDir = new File("target/unreadable");
        unreadableDir.deleteOnExit();
        unreadableDir.mkdir();
        Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
        posixFilePermissions.add(PosixFilePermission.GROUP_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.GROUP_WRITE);
        posixFilePermissions.add(PosixFilePermission.OTHERS_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.OTHERS_WRITE);
        posixFilePermissions.add(PosixFilePermission.OWNER_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.OWNER_WRITE);
        Files.setPosixFilePermissions(unreadableDir.toPath(), posixFilePermissions);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, unreadableDir.getAbsolutePath());
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            Throwable ex = e.getCause();
            Assert.assertTrue(e.getCause().getMessage().endsWith("does not have sufficient permissions (i.e., not writable and readable)"));
        }
    }

    @Test
    public void testWithUnwritableDir() throws IOException {
        // Some systems don't support POSIX (Windows) and will fail if run. Should ignore the test in that event
        if (!(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"))) {
            return;
        }
        File unwritableDir = new File("target/unwritable");
        unwritableDir.deleteOnExit();
        unwritableDir.mkdir();
        Set<PosixFilePermission> posixFilePermissions = new HashSet<>();
        posixFilePermissions.add(PosixFilePermission.GROUP_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.GROUP_READ);
        posixFilePermissions.add(PosixFilePermission.OTHERS_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.OTHERS_READ);
        posixFilePermissions.add(PosixFilePermission.OWNER_EXECUTE);
        posixFilePermissions.add(PosixFilePermission.OWNER_READ);
        Files.setPosixFilePermissions(unwritableDir.toPath(), posixFilePermissions);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, unwritableDir.getAbsolutePath());
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            Assert.assertTrue(e.getCause().getMessage().endsWith("does not have sufficient permissions (i.e., not writable and readable)"));
        }
    }

    @Test
    public void testFilePickedUp() throws IOException {
        final File directory = new File("target/test/data/in");
        deleteDirectory(directory);
        Assert.assertTrue(("Unable to create test data directory " + (directory.getAbsolutePath())), ((directory.exists()) || (directory.mkdirs())));
        final File inFile = new File("src/test/resources/hello.txt");
        final Path inPath = inFile.toPath();
        final File destFile = new File(directory, inFile.getName());
        final Path targetPath = destFile.toPath();
        final Path absTargetPath = targetPath.toAbsolutePath();
        final String absTargetPathStr = (absTargetPath.getParent()) + "/";
        Files.copy(inPath, targetPath);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, directory.getAbsolutePath());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        successFiles.get(0).assertContentEquals("Hello, World!".getBytes("UTF-8"));
        final String path = successFiles.get(0).getAttribute("path");
        Assert.assertEquals("/", path);
        final String absolutePath = successFiles.get(0).getAttribute(ABSOLUTE_PATH.key());
        Assert.assertEquals(absTargetPathStr, absolutePath);
    }

    @Test
    public void testTodaysFilesPickedUp() throws IOException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        final String dirStruc = sdf.format(new Date());
        final File directory = new File(("target/test/data/in/" + dirStruc));
        deleteDirectory(directory);
        Assert.assertTrue(("Unable to create test data directory " + (directory.getAbsolutePath())), ((directory.exists()) || (directory.mkdirs())));
        final File inFile = new File("src/test/resources/hello.txt");
        final Path inPath = inFile.toPath();
        final File destFile = new File(directory, inFile.getName());
        final Path targetPath = destFile.toPath();
        Files.copy(inPath, targetPath);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, "target/test/data/in/${now():format('yyyy/MM/dd')}");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        successFiles.get(0).assertContentEquals("Hello, World!".getBytes("UTF-8"));
    }

    @Test
    public void testPath() throws IOException {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/", Locale.US);
        final String dirStruc = sdf.format(new Date());
        final File directory = new File(("target/test/data/in/" + dirStruc));
        deleteDirectory(new File("target/test/data/in"));
        Assert.assertTrue(("Unable to create test data directory " + (directory.getAbsolutePath())), ((directory.exists()) || (directory.mkdirs())));
        final File inFile = new File("src/test/resources/hello.txt");
        final Path inPath = inFile.toPath();
        final File destFile = new File(directory, inFile.getName());
        final Path targetPath = destFile.toPath();
        final Path absTargetPath = targetPath.toAbsolutePath();
        final String absTargetPathStr = (absTargetPath.getParent().toString()) + "/";
        Files.copy(inPath, targetPath);
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, "target/test/data/in");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        successFiles.get(0).assertContentEquals("Hello, World!".getBytes("UTF-8"));
        final String path = successFiles.get(0).getAttribute("path");
        Assert.assertEquals(dirStruc, path.replace('\\', '/'));
        final String absolutePath = successFiles.get(0).getAttribute(ABSOLUTE_PATH.key());
        Assert.assertEquals(absTargetPathStr, absolutePath);
    }

    @Test
    public void testAttributes() throws IOException {
        final File directory = new File("target/test/data/in/");
        deleteDirectory(directory);
        Assert.assertTrue(("Unable to create test data directory " + (directory.getAbsolutePath())), ((directory.exists()) || (directory.mkdirs())));
        final File inFile = new File("src/test/resources/hello.txt");
        final Path inPath = inFile.toPath();
        final File destFile = new File(directory, inFile.getName());
        final Path targetPath = destFile.toPath();
        Files.copy(inPath, targetPath);
        boolean verifyLastModified = false;
        try {
            destFile.setLastModified(1000000000);
            verifyLastModified = true;
        } catch (Exception doNothing) {
        }
        boolean verifyPermissions = false;
        try {
            /* If you mount an NTFS partition in Linux, you are unable to change the permissions of the files,
            because every file has the same permissions, controlled by the 'fmask' and 'dmask' mount options.
            Executing a chmod command will not fail, but it does not change the file's permissions.
            From Java perspective the NTFS mount point, as a FileStore supports the 'unix' and 'posix' file
            attribute views, but the setPosixFilePermissions() has no effect.

            If you set verifyPermissions to true without the following extra check, the test case will fail
            on a file system, where Nifi source is located on a NTFS mount point in Linux.
            The purpose of the extra check is to ensure, that setPosixFilePermissions() changes the file's
            permissions, and set verifyPermissions, after we are convinced.
             */
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("r--r-----");
            Files.setPosixFilePermissions(targetPath, perms);
            Set<PosixFilePermission> permsAfterSet = Files.getPosixFilePermissions(targetPath);
            if (perms.equals(permsAfterSet)) {
                verifyPermissions = true;
            }
        } catch (Exception doNothing) {
        }
        final TestRunner runner = TestRunners.newTestRunner(new GetFile());
        runner.setProperty(DIRECTORY, "target/test/data/in");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final List<MockFlowFile> successFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        if (verifyLastModified) {
            try {
                final DateFormat formatter = new SimpleDateFormat(FILE_MODIFY_DATE_ATTR_FORMAT, Locale.US);
                final Date fileModifyTime = formatter.parse(successFiles.get(0).getAttribute("file.lastModifiedTime"));
                Assert.assertEquals(new Date(1000000000), fileModifyTime);
            } catch (ParseException e) {
                Assert.fail();
            }
        }
        if (verifyPermissions) {
            successFiles.get(0).assertAttributeEquals("file.permissions", "r--r-----");
        }
    }
}

