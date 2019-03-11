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
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.function.Consumer;
import org.eclipse.che.api.project.server.impl.RootDirPathProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link FileWatcherEventHandler}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherEventHandlerTest {
    private static final String PROJECT_FILE = "/project/file";

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    FileWatcherEventHandler handler;

    @Mock
    Consumer<String> create;

    @Mock
    Consumer<String> modify;

    @Mock
    Consumer<String> delete;

    Path root;

    @Test
    public void shouldHandleRegisteredPathWhenCreate() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path, create, modify, delete);
        handler.handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Mockito.verify(create).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldHandleRegisteredPathWhenModify() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path, create, modify, delete);
        handler.handle(path, StandardWatchEventKinds.ENTRY_MODIFY);
        Mockito.verify(modify).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldHandleRegisteredPathWhenDelete() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path, create, modify, delete);
        handler.handle(path, StandardWatchEventKinds.ENTRY_DELETE);
        Mockito.verify(delete).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldHandleRegisteredPathWhenCreateFileForFileAndForParent() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path, create, modify, delete);
        handler.register(path.getParent(), create, modify, delete);
        handler.handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Mockito.verify(create, Mockito.times(2)).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldNotHandleNotRegisteredPath() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path.resolve("one"), create, modify, delete);
        handler.register(path.resolve("two"), create, modify, delete);
        handler.handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Mockito.verify(create, Mockito.never()).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldNotHandleUnRegisteredPath() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        int id = handler.register(path, create, modify, delete);
        handler.unRegister(id);
        handler.handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Mockito.verify(create, Mockito.never()).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    @Test
    public void shouldNotHandleUnRegisteredFileButShouldHandleFilesParent() throws Exception {
        Path path = root.resolve(FileWatcherEventHandlerTest.PROJECT_FILE);
        handler.register(path.getParent(), create, modify, delete);
        int id = handler.register(path, create, modify, delete);
        handler.unRegister(id);
        handler.handle(path, StandardWatchEventKinds.ENTRY_CREATE);
        Mockito.verify(create).accept(FileWatcherUtils.toInternalPath(root, path));
    }

    private static class DummyRootProvider extends RootDirPathProvider {
        public DummyRootProvider(File folder) {
            this.rootFile = folder;
        }
    }
}

