/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn;


import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestYarnUncaughtExceptionHandler {
    private static final YarnUncaughtExceptionHandler exHandler = new YarnUncaughtExceptionHandler();

    /**
     * Throw {@code YarnRuntimeException} inside thread and
     * check {@code YarnUncaughtExceptionHandler} instance
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testUncaughtExceptionHandlerWithRuntimeException() throws InterruptedException {
        final YarnUncaughtExceptionHandler spyYarnHandler = Mockito.spy(TestYarnUncaughtExceptionHandler.exHandler);
        final YarnRuntimeException yarnException = new YarnRuntimeException("test-yarn-runtime-exception");
        final Thread yarnThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw yarnException;
            }
        });
        yarnThread.setUncaughtExceptionHandler(spyYarnHandler);
        Assert.assertSame(spyYarnHandler, yarnThread.getUncaughtExceptionHandler());
        yarnThread.start();
        yarnThread.join();
        Mockito.verify(spyYarnHandler).uncaughtException(yarnThread, yarnException);
    }

    /**
     * <p>
     * Throw {@code Error} inside thread and
     * check {@code YarnUncaughtExceptionHandler} instance
     * <p>
     * Used {@code ExitUtil} class to avoid jvm exit through
     * {@code System.exit(-1)}
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testUncaughtExceptionHandlerWithError() throws InterruptedException {
        ExitUtil.disableSystemExit();
        final YarnUncaughtExceptionHandler spyErrorHandler = Mockito.spy(TestYarnUncaughtExceptionHandler.exHandler);
        final Error error = new Error("test-error");
        final Thread errorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw error;
            }
        });
        errorThread.setUncaughtExceptionHandler(spyErrorHandler);
        Assert.assertSame(spyErrorHandler, errorThread.getUncaughtExceptionHandler());
        errorThread.start();
        errorThread.join();
        Mockito.verify(spyErrorHandler).uncaughtException(errorThread, error);
    }

    /**
     * <p>
     * Throw {@code OutOfMemoryError} inside thread and
     * check {@code YarnUncaughtExceptionHandler} instance
     * <p>
     * Used {@code ExitUtil} class to avoid jvm exit through
     * {@code Runtime.getRuntime().halt(-1)}
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testUncaughtExceptionHandlerWithOutOfMemoryError() throws InterruptedException {
        ExitUtil.disableSystemHalt();
        final YarnUncaughtExceptionHandler spyOomHandler = Mockito.spy(TestYarnUncaughtExceptionHandler.exHandler);
        final OutOfMemoryError oomError = new OutOfMemoryError("out-of-memory-error");
        final Thread oomThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw oomError;
            }
        });
        oomThread.setUncaughtExceptionHandler(spyOomHandler);
        Assert.assertSame(spyOomHandler, oomThread.getUncaughtExceptionHandler());
        oomThread.start();
        oomThread.join();
        Mockito.verify(spyOomHandler).uncaughtException(oomThread, oomError);
    }
}

