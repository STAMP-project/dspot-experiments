/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.api.watcher.server.impl;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link FileWatcherService}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherServiceTest {
    private static final int TIMEOUT_VALUE = 3000;

    private static final String FOLDER_NAME = "folder";

    private static final String FILE_NAME = "file";

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Mock
    FileWatcherEventHandler handler;

    @Mock
    FileWatcherExcludePatternsRegistry fileWatcherExcludePatternsRegistry;

    WatchService watchService = FileSystems.getDefault().newWatchService();

    FileWatcherService service;

    public FileWatcherServiceTest() throws IOException {
    }

    @Test
    public void shouldWatchRegisteredFolderForFileCreation() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        Path path = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME).toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldWatchRegisteredFolderForFileRemoval() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        boolean deleted = file.delete();
        Assert.assertTrue(deleted);
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void shouldWatchRegisteredFolderForDirectoryCreation() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        Path path = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME).toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldWatchForRegisteredFolderForFileModification() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        FileUtils.write(file, "");
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Test
    public void shouldWatchRegisteredFolderForFolderRemoval() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        boolean deleted = file.delete();
        Assert.assertTrue(deleted);
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForFileCreation() throws Exception {
        Path path = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME).toPath();
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldWatchForRegisteredFolderForFolderModification() throws Exception {
        if (!(FileWatcherServiceTest.osIsMacOsX())) {
            return;
        }
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Files.createDirectory(path.resolve(FileWatcherServiceTest.FOLDER_NAME));
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForFileRemoval() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        service.unRegister(rootFolder.getRoot().toPath());
        boolean deleted = file.delete();
        Assert.assertTrue(deleted);
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForDirectoryCreation() throws Exception {
        Path path = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME).toPath();
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForFileModification() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        service.unRegister(rootFolder.getRoot().toPath());
        FileUtils.write(file, "");
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForFolderRemoval() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        service.unRegister(rootFolder.getRoot().toPath());
        boolean deleted = file.delete();
        Assert.assertTrue(deleted);
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_DELETE);
    }

    @Test
    public void shouldWatchTwiceRegisteredFolderForFileCreationAfterSingleUnregister() throws Exception {
        Path root = rootFolder.getRoot().toPath();
        service.register(root);
        service.register(root);
        service.unRegister(root);
        Path path = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME).toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldNotWatchTwiceRegisteredFolderForFileCreationAfterDoubleUnRegister() throws Exception {
        Path root = rootFolder.getRoot().toPath();
        service.register(root);
        service.register(root);
        service.unRegister(root);
        service.unRegister(root);
        Path path = rootFolder.newFile(FileWatcherServiceTest.FILE_NAME).toPath();
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
    }

    @Test
    public void shouldNotWatchUnRegisteredFolderForFolderModification() throws Exception {
        service.register(rootFolder.getRoot().toPath());
        File file = rootFolder.newFolder(FileWatcherServiceTest.FOLDER_NAME);
        Path path = file.toPath();
        Mockito.verify(handler, Mockito.timeout(FileWatcherServiceTest.TIMEOUT_VALUE)).handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        service.unRegister(rootFolder.getRoot().toPath());
        Files.createDirectory(path.resolve(FileWatcherServiceTest.FILE_NAME));
        Mockito.verify(handler, Mockito.after(FileWatcherServiceTest.TIMEOUT_VALUE).never()).handle(path, StandardWatchEventKinds.ENTRY_MODIFY);
    }
}

