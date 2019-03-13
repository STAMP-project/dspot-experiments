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
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link FileWatcherByPathValue}
 */
@RunWith(MockitoJUnitRunner.class)
public class FileWatcherByPathValueTest {
    private static final int OPERATION_ID = 0;

    private static final String FILE_NAME = "name.ext";

    @Rule
    public TemporaryFolder rootFolder = new TemporaryFolder();

    @Mock
    FileWatcherEventHandler handler;

    @Mock
    FileWatcherService service;

    @InjectMocks
    FileWatcherByPathValue watcher;

    @Mock
    Consumer<String> create;

    @Mock
    Consumer<String> modify;

    @Mock
    Consumer<String> delete;

    Path root;

    @Test
    public void shouldRegisterInServiceWhenWatchFile() throws Exception {
        Path path = root.resolve(FileWatcherByPathValueTest.FILE_NAME);
        watcher.watch(path, create, modify, delete);
        Mockito.verify(service).register(path.getParent());
    }

    @Test
    public void shouldRegisterInServiceWhenWatchDirectory() throws Exception {
        Path path = root.resolve(FileWatcherByPathValueTest.FILE_NAME);
        watcher.watch(path.getParent(), create, modify, delete);
        Mockito.verify(service).register(path.getParent());
    }

    @Test
    public void shouldRegisterInHandlerWhenWatch() throws Exception {
        Path path = root.resolve(FileWatcherByPathValueTest.FILE_NAME);
        watcher.watch(path, create, modify, delete);
        Mockito.verify(handler).register(path, create, modify, delete);
    }

    @Test
    public void shouldUnRegisterInServiceWhenUnWatch() throws Exception {
        Path path = Mockito.mock(Path.class);
        Mockito.when(handler.unRegister(ArgumentMatchers.anyInt())).thenReturn(path);
        watcher.unwatch(FileWatcherByPathValueTest.OPERATION_ID);
        Mockito.verify(service).unRegister(path);
    }

    @Test
    public void shouldUnRegisterInHandlerWhenUnWatch() throws Exception {
        Path path = Mockito.mock(Path.class);
        Mockito.when(handler.unRegister(ArgumentMatchers.anyInt())).thenReturn(path);
        watcher.unwatch(FileWatcherByPathValueTest.OPERATION_ID);
        Mockito.verify(handler).unRegister(FileWatcherByPathValueTest.OPERATION_ID);
    }
}

