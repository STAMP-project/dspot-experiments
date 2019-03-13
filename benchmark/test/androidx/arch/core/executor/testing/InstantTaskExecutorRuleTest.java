/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.arch.core.executor.testing;


import androidx.arch.core.executor.ArchTaskExecutor;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class InstantTaskExecutorRuleTest {
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void executeOnMain() throws InterruptedException, ExecutionException, TimeoutException {
        final Thread current = Thread.currentThread();
        FutureTask<Void> check = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Assert.assertTrue(((Thread.currentThread()) == current));
                return null;
            }
        });
        ArchTaskExecutor.getInstance().executeOnMainThread(check);
        check.get(1, TimeUnit.SECONDS);
    }

    @Test
    public void executeOnIO() throws InterruptedException, ExecutionException, TimeoutException {
        final Thread current = Thread.currentThread();
        FutureTask<Void> check = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Assert.assertTrue(((Thread.currentThread()) == current));
                return null;
            }
        });
        ArchTaskExecutor.getInstance().executeOnDiskIO(check);
        check.get(1, TimeUnit.SECONDS);
    }
}

