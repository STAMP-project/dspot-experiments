/**
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.rpc.client;


import Result.FAIL;
import Result.SUCCESS;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author Taejin Koo
 */
public class ConnectFutureTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void setResultTest() {
        ConnectFuture future = new ConnectFuture();
        future.setResult(FAIL);
        future.setResult(SUCCESS);
        Assert.assertEquals(FAIL, future.getResult());
        future = new ConnectFuture();
        future.setResult(SUCCESS);
        future.setResult(FAIL);
        Assert.assertEquals(SUCCESS, future.getResult());
    }

    @Test
    public void awaitTest1() throws InterruptedException {
        ConnectFuture future = new ConnectFuture();
        Thread thread = new Thread(new ConnectFutureTest.SetResultRunnable(future));
        thread.start();
        future.await();
        Assert.assertEquals(SUCCESS, future.getResult());
    }

    @Test
    public void awaitTest2() throws InterruptedException {
        ConnectFuture future = new ConnectFuture();
        Thread thread = new Thread(new ConnectFutureTest.SetResultRunnable(future));
        thread.start();
        future.awaitUninterruptibly();
        Assert.assertEquals(SUCCESS, future.getResult());
    }

    @Test
    public void awaitTest3() throws InterruptedException {
        ConnectFuture future = new ConnectFuture();
        Thread thread = new Thread(new ConnectFutureTest.SetResultRunnable(future));
        thread.start();
        future.await(TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS);
        Assert.assertEquals(SUCCESS, future.getResult());
    }

    @Test
    public void notCompletedTest() throws InterruptedException {
        ConnectFuture future = new ConnectFuture();
        long waitTime = 100;
        Thread thread = new Thread(new ConnectFutureTest.SetResultRunnable(future, waitTime));
        thread.start();
        boolean isReady = future.await((waitTime / 2), TimeUnit.MILLISECONDS);
        Assert.assertFalse(isReady);
        Assert.assertEquals(null, future.getResult());
        isReady = future.await(waitTime, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isReady);
        Assert.assertEquals(SUCCESS, future.getResult());
    }

    class SetResultRunnable implements Runnable {
        private final ConnectFuture future;

        private final long waitTime;

        public SetResultRunnable(ConnectFuture future) {
            this(future, (-1L));
        }

        public SetResultRunnable(ConnectFuture future, long waitTime) {
            this.future = future;
            this.waitTime = waitTime;
        }

        @Override
        public void run() {
            if ((waitTime) > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
            future.setResult(SUCCESS);
        }
    }
}

