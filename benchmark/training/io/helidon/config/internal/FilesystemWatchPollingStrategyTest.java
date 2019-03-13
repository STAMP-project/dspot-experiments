/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.internal;


import PollingStrategy.PollingEvent;
import com.sun.nio.file.SensitivityWatchEventModifier;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.common.reactive.SubmissionPublisher;
import io.helidon.config.spi.PollingStrategy;
import io.helidon.config.test.infra.TemporaryFolderExt;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;


/**
 * Tests {@link FilesystemWatchPollingStrategy}.
 */
// TODO tests are still TOO slow to be unit tests -> refactor it to run much quicker
@Disabled
public class FilesystemWatchPollingStrategyTest {
    private static final String WATCHED_FILE = "watched-file.yaml";

    @RegisterExtension
    static TemporaryFolderExt dir = TemporaryFolderExt.build();

    @Test
    public void testPollingDirectoryDeleted() throws IOException, InterruptedException {
        CountDownLatch subscribeLatch = new CountDownLatch(1);
        CountDownLatch watchedDirLatch = new CountDownLatch(1);
        File watchedDir = FilesystemWatchPollingStrategyTest.dir.newFolder();
        Files.write(Files.createFile(new File(watchedDir, "username").toPath()), "libor".getBytes());
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedDir.toPath(), null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        SubmissionPublisher<PollingStrategy.PollingEvent> publisher = new SubmissionPublisher();
        Mockito.when(mockPollingStrategy.ticksSubmitter()).thenReturn(publisher);
        publisher.subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscribeLatch.countDown();
                subscription.request(1);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                watchedDirLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        mockPollingStrategy.startWatchService();
        MatcherAssert.assertThat(subscribeLatch.await(10, TimeUnit.MILLISECONDS), CoreMatchers.is(true));
        deleteDir(watchedDir);
        MatcherAssert.assertThat(watchedDirLatch.await(60, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testPolling() throws IOException, InterruptedException {
        CountDownLatch subscribeLatch = new CountDownLatch(1);
        CountDownLatch watchedFileLatch = new CountDownLatch(1);
        SubmissionPublisher<PollingStrategy.PollingEvent> publisher = new SubmissionPublisher();
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path watchedPath = dirPath.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        Mockito.when(mockPollingStrategy.ticksSubmitter()).thenReturn(publisher);
        publisher.subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscribeLatch.countDown();
                subscription.request(1);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                watchedFileLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        mockPollingStrategy.startWatchService();
        MatcherAssert.assertThat(subscribeLatch.await(10, TimeUnit.MILLISECONDS), CoreMatchers.is(true));
        FilesystemWatchPollingStrategyTest.dir.newFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        MatcherAssert.assertThat(watchedFileLatch.await(40, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testPollingNotYetExisting() throws IOException, InterruptedException {
        CountDownLatch subscribeLatch = new CountDownLatch(1);
        CountDownLatch watchedFileLatch = new CountDownLatch(1);
        SubmissionPublisher<PollingStrategy.PollingEvent> publisher = new SubmissionPublisher();
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path subdir = dirPath.resolve("subdir");
        Path watchedPath = subdir.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        Mockito.when(mockPollingStrategy.ticksSubmitter()).thenReturn(publisher);
        publisher.subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscribeLatch.countDown();
                subscription.request(1);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                watchedFileLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        mockPollingStrategy.startWatchService();
        MatcherAssert.assertThat(subscribeLatch.await(10, TimeUnit.MILLISECONDS), CoreMatchers.is(true));
        FilesystemWatchPollingStrategyTest.dir.newFolder("subdir");
        FilesystemWatchPollingStrategyTest.dir.newFile(("subdir/" + (FilesystemWatchPollingStrategyTest.WATCHED_FILE)));
        MatcherAssert.assertThat(watchedFileLatch.await(40, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testPollingSymLink() throws IOException, InterruptedException {
        CountDownLatch subscribeLatch = new CountDownLatch(1);
        CountDownLatch firstEventLatch = new CountDownLatch(1);
        CountDownLatch secondEventLatch = new CountDownLatch(1);
        CountDownLatch thirdEventLatch = new CountDownLatch(1);
        SubmissionPublisher<PollingStrategy.PollingEvent> publisher = new SubmissionPublisher();
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path watchedPath = dirPath.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        Mockito.when(mockPollingStrategy.ticksSubmitter()).thenReturn(publisher);
        publisher.subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscribeLatch.countDown();
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                System.out.println("on next");
                if ((firstEventLatch.getCount()) > 0) {
                    firstEventLatch.countDown();
                    System.out.println("first event received");
                } else
                    if ((secondEventLatch.getCount()) > 0) {
                        secondEventLatch.countDown();
                        System.out.println("second event received");
                    } else {
                        thirdEventLatch.countDown();
                        System.out.println("third event received");
                    }

            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        MatcherAssert.assertThat(subscribeLatch.await(10, TimeUnit.MILLISECONDS), CoreMatchers.is(true));
        Path targetDir = createFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE, CollectionsHelper.listOf("a: a"));
        Path symlinkToDir = Files.createSymbolicLink(Paths.get(FilesystemWatchPollingStrategyTest.dir.getRoot().toString(), "symlink-to-target-dir"), targetDir);
        Path symlink = Files.createSymbolicLink(Paths.get(FilesystemWatchPollingStrategyTest.dir.getRoot().toString(), FilesystemWatchPollingStrategyTest.WATCHED_FILE), Paths.get(symlinkToDir.toString(), FilesystemWatchPollingStrategyTest.WATCHED_FILE));
        mockPollingStrategy.startWatchService();
        Path newTarget = createFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE, CollectionsHelper.listOf("a: b"));
        Files.walk(targetDir).map(Path::toFile).forEach(File::delete);
        Files.delete(targetDir);
        Files.delete(symlinkToDir);
        symlinkToDir = Files.createSymbolicLink(Paths.get(FilesystemWatchPollingStrategyTest.dir.getRoot().toString(), "symlink-to-target-dir"), newTarget);
        printDir();
        MatcherAssert.assertThat(firstEventLatch.await(30, TimeUnit.SECONDS), CoreMatchers.is(true));
        targetDir = newTarget;
        newTarget = createFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE, CollectionsHelper.listOf("a: c"));
        Files.walk(targetDir).map(Path::toFile).forEach(File::delete);
        Files.delete(targetDir);
        Files.delete(symlinkToDir);
        symlinkToDir = Files.createSymbolicLink(Paths.get(FilesystemWatchPollingStrategyTest.dir.getRoot().toString(), "symlink-to-target-dir"), newTarget);
        printDir();
        MatcherAssert.assertThat(secondEventLatch.await(40, TimeUnit.SECONDS), CoreMatchers.is(true));
        targetDir = newTarget;
        newTarget = createFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE, CollectionsHelper.listOf("a: d"));
        Files.walk(targetDir).map(Path::toFile).forEach(File::delete);
        Files.delete(targetDir);
        Files.delete(symlinkToDir);
        symlinkToDir = Files.createSymbolicLink(Paths.get(FilesystemWatchPollingStrategyTest.dir.getRoot().toString(), "symlink-to-target-dir"), newTarget);
        printDir();
        MatcherAssert.assertThat(thirdEventLatch.await(40, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testPollingAfterRestartWatchService() throws IOException, InterruptedException {
        CountDownLatch subscribeLatch = new CountDownLatch(1);
        CountDownLatch watchedFileLatch = new CountDownLatch(1);
        SubmissionPublisher<PollingStrategy.PollingEvent> publisher = new SubmissionPublisher();
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path watchedPath = dirPath.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        Mockito.when(mockPollingStrategy.ticksSubmitter()).thenReturn(publisher);
        publisher.subscribe(new Flow.Subscriber<PollingStrategy.PollingEvent>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscribeLatch.countDown();
                subscription.request(1);
            }

            @Override
            public void onNext(PollingStrategy.PollingEvent item) {
                watchedFileLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                fail(throwable);
            }

            @Override
            public void onComplete() {
            }
        });
        mockPollingStrategy.startWatchService();
        mockPollingStrategy.stopWatchService();
        mockPollingStrategy.startWatchService();
        MatcherAssert.assertThat(subscribeLatch.await(10, TimeUnit.MILLISECONDS), CoreMatchers.is(true));
        FilesystemWatchPollingStrategyTest.dir.newFile(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        MatcherAssert.assertThat(watchedFileLatch.await(40, TimeUnit.SECONDS), CoreMatchers.is(true));
    }

    @Test
    public void testWatchThreadFuture() {
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path watchedPath = dirPath.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        mockPollingStrategy.startWatchService();
        MatcherAssert.assertThat(mockPollingStrategy.watchThreadFuture(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mockPollingStrategy.watchThreadFuture().isCancelled(), CoreMatchers.is(false));
        mockPollingStrategy.startWatchService();
    }

    @Test
    public void testWatchThreadFutureCanceled() {
        Path dirPath = FileSystems.getDefault().getPath(FilesystemWatchPollingStrategyTest.dir.getRoot().getAbsolutePath());
        Path watchedPath = dirPath.resolve(FilesystemWatchPollingStrategyTest.WATCHED_FILE);
        FilesystemWatchPollingStrategy mockPollingStrategy = Mockito.spy(new FilesystemWatchPollingStrategy(watchedPath, null));
        mockPollingStrategy.initWatchServiceModifiers(SensitivityWatchEventModifier.HIGH);
        mockPollingStrategy.startWatchService();
        mockPollingStrategy.stopWatchService();
        MatcherAssert.assertThat(mockPollingStrategy.watchThreadFuture(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mockPollingStrategy.watchThreadFuture().isCancelled(), CoreMatchers.is(true));
    }
}

