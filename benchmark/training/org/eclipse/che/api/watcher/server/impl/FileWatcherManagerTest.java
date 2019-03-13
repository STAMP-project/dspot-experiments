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


import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Consumer;
import org.eclipse.che.api.project.server.impl.RootDirPathProvider;
import org.eclipse.che.api.watcher.server.FileWatcherManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link FileWatcherManager}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherManagerTest {
    private static final String PATH = "/path";

    private static final int ID = 0;

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Mock
    FileWatcherByPathValue fileWatcherByPathValue;

    @Mock
    FileWatcherByPathMatcher fileWatcherByPathMatcher;

    @Mock
    FileWatcherExcludePatternsRegistry fileWatcherExcludePatternsRegistry;

    @Mock
    FileWatcherService service;

    FileWatcherManager manager;

    @Mock
    Consumer<String> create;

    @Mock
    Consumer<String> modify;

    @Mock
    Consumer<String> delete;

    @Mock
    PathMatcher pathMatcher;

    @Mock
    RootDirPathProvider pathProvider;

    @Test
    public void shouldWatchByPath() throws Exception {
        manager.registerByPath(FileWatcherManagerTest.PATH, create, modify, delete);
        Path path = FileWatcherUtils.toNormalPath(rootFolder.getRoot().toPath(), FileWatcherManagerTest.PATH);
        Mockito.verify(fileWatcherByPathValue).watch(path, create, modify, delete);
    }

    @Test
    public void shouldUnWatchByPath() throws Exception {
        manager.unRegisterByPath(FileWatcherManagerTest.ID);
        Mockito.verify(fileWatcherByPathValue).unwatch(FileWatcherManagerTest.ID);
    }

    @Test
    public void shouldWatchByMatcher() throws Exception {
        manager.registerByMatcher(pathMatcher, create, modify, delete);
        Mockito.verify(fileWatcherByPathMatcher).watch(pathMatcher, create, modify, delete);
    }

    @Test
    public void shouldUnWatchByMatcher() throws Exception {
        manager.unRegisterByMatcher(FileWatcherManagerTest.ID);
        Mockito.verify(fileWatcherByPathMatcher).unwatch(FileWatcherManagerTest.ID);
    }
}

