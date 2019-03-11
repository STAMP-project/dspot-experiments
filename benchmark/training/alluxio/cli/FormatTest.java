/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.cli;


import Format.Mode.WORKER;
import PropertyKey.WORKER_DATA_FOLDER_PERMISSIONS;
import alluxio.conf.ServerConfiguration;
import alluxio.util.CommonUtils;
import alluxio.util.io.FileUtils;
import alluxio.util.io.PathUtils;
import java.io.Closeable;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Unit tests for {@link Format}.
 */
public final class FormatTest {
    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void formatWorker() throws Exception {
        final int storageLevels = 3;
        final String perms = "rwx------";
        String workerDataFolder;
        final File[] dirs = new File[]{ mTemporaryFolder.newFolder("level0"), mTemporaryFolder.newFolder("level1"), mTemporaryFolder.newFolder("level2") };
        for (File dir : dirs) {
            workerDataFolder = CommonUtils.getWorkerDataDirectory(dir.getPath(), ServerConfiguration.global());
            FileUtils.createDir(PathUtils.concatPath(workerDataFolder, "subdir"));
            FileUtils.createFile(PathUtils.concatPath(workerDataFolder, "file"));
        }
        try (Closeable r = toResource()) {
            Format.format(WORKER, ServerConfiguration.global());
            for (File dir : dirs) {
                workerDataFolder = CommonUtils.getWorkerDataDirectory(dir.getPath(), ServerConfiguration.global());
                Assert.assertTrue(FileUtils.exists(dir.getPath()));
                Assert.assertTrue(FileUtils.exists(workerDataFolder));
                Assert.assertEquals(PosixFilePermissions.fromString(perms), Files.getPosixFilePermissions(Paths.get(workerDataFolder)));
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(workerDataFolder))) {
                    for (Path child : directoryStream) {
                        Assert.fail(("No sub dirs or files are expected in " + (child.toString())));
                    }
                }
            }
        }
    }

    @Test
    public void formatWorkerDeleteFileSameName() throws Exception {
        final int storageLevels = 3;
        String workerDataFolder;
        final File[] dirs = new File[]{ mTemporaryFolder.newFolder("level0"), mTemporaryFolder.newFolder("level1"), mTemporaryFolder.newFolder("level2") };
        // Have files of same name as the target worker data dir in each tier
        for (File dir : dirs) {
            workerDataFolder = CommonUtils.getWorkerDataDirectory(dir.getPath(), ServerConfiguration.global());
            FileUtils.createFile(workerDataFolder);
        }
        try (Closeable r = toResource()) {
            final String perms = ServerConfiguration.get(WORKER_DATA_FOLDER_PERMISSIONS);
            Format.format(WORKER, ServerConfiguration.global());
            for (File dir : dirs) {
                workerDataFolder = CommonUtils.getWorkerDataDirectory(dir.getPath(), ServerConfiguration.global());
                Assert.assertTrue(Files.isDirectory(Paths.get(workerDataFolder)));
                Assert.assertEquals(PosixFilePermissions.fromString(perms), Files.getPosixFilePermissions(Paths.get(workerDataFolder)));
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(workerDataFolder))) {
                    for (Path child : directoryStream) {
                        Assert.fail(("No sub dirs or files are expected in " + (child.toString())));
                    }
                }
            }
        }
    }
}

