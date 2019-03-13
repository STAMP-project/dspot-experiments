/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.utils;


import Constants.THREAD_NAME_KEY;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.common.URL;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class ExecutorUtilTest {
    @Test
    public void testIsTerminated() throws Exception {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.when(executor.isTerminated()).thenReturn(true);
        MatcherAssert.assertThat(ExecutorUtil.isTerminated(executor), Matchers.is(true));
        Executor executor2 = Mockito.mock(Executor.class);
        MatcherAssert.assertThat(ExecutorUtil.isTerminated(executor2), Matchers.is(false));
    }

    @Test
    public void testGracefulShutdown1() throws Exception {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.when(executor.isTerminated()).thenReturn(false, true);
        Mockito.when(executor.awaitTermination(20, TimeUnit.MILLISECONDS)).thenReturn(false);
        ExecutorUtil.gracefulShutdown(executor, 20);
        Mockito.verify(executor).shutdown();
        Mockito.verify(executor).shutdownNow();
    }

    @Test
    public void testGracefulShutdown2() throws Exception {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.when(executor.isTerminated()).thenReturn(false, false, false);
        Mockito.when(executor.awaitTermination(20, TimeUnit.MILLISECONDS)).thenReturn(false);
        Mockito.when(executor.awaitTermination(10, TimeUnit.MILLISECONDS)).thenReturn(false, true);
        ExecutorUtil.gracefulShutdown(executor, 20);
        Thread.sleep(2000);
        Mockito.verify(executor).shutdown();
        Mockito.verify(executor, Mockito.atLeast(2)).shutdownNow();
    }

    @Test
    public void testShutdownNow() throws Exception {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.when(executor.isTerminated()).thenReturn(false, true);
        ExecutorUtil.shutdownNow(executor, 20);
        Mockito.verify(executor).shutdownNow();
        Mockito.verify(executor).awaitTermination(20, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSetThreadName() throws Exception {
        URL url = new URL("dubbo", "localhost", 1234).addParameter(THREAD_NAME_KEY, "custom-thread");
        url = ExecutorUtil.setThreadName(url, "default-name");
        MatcherAssert.assertThat(url.getParameter(THREAD_NAME_KEY), Matchers.equalTo("custom-thread-localhost:1234"));
    }
}

