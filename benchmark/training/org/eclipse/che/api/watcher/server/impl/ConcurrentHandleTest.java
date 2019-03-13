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


import com.google.common.collect.ImmutableList;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.eclipse.che.api.project.server.impl.RootDirPathProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;


public class ConcurrentHandleTest {
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
    public void testConcurrentHandle() throws Exception {
        class HandleTask implements Callable<Void> {
            WatchEvent.Kind<Path> eventType;

            public HandleTask(WatchEvent.Kind<Path> eventType) {
                this.eventType = eventType;
            }

            @Override
            public Void call() {
                final Path path = root.resolve(ConcurrentHandleTest.PROJECT_FILE);
                handler.register(path, create, modify, delete);
                handler.handle(path, eventType);
                return null;
            }
        }
        final int n = 50;
        final ExecutorService executor = Executors.newFixedThreadPool(5);
        final ArrayList<Callable<Void>> tasks = new ArrayList<>(n);
        final ImmutableList<WatchEvent.Kind<Path>> operations = ImmutableList.of(StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        for (int i = 0; i < n; i++) {
            tasks.add(new HandleTask(operations.get(ThreadLocalRandom.current().nextInt(0, operations.size()))));
        }
        final List<Future<Void>> futures = executor.invokeAll(tasks, n, TimeUnit.SECONDS);
        long count = futures.stream().filter(( future) -> {
            try {
                future.get();
                return false;
            } catch (ExecutionException ex) {
                System.out.println(ex.getMessage());
                return (ex.getCause()) instanceof ConcurrentModificationException;
            } catch (Exception ignored) {
                return false;
            }
        }).count();
        Assert.assertEquals(count, 0);
    }

    private static class DummyRootProvider extends RootDirPathProvider {
        public DummyRootProvider(File folder) {
            this.rootFile = folder;
        }
    }
}

