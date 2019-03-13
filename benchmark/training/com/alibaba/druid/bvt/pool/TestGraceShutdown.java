/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool;


import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DataSourceDisableException;
import com.alibaba.druid.pool.DruidDataSource;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestGraceShutdown extends TestCase {
    private MockDriver driver;

    private DruidDataSource dataSource;

    public void test_close() throws Exception {
        final int threadCount = 100;
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread(("thread-" + i)) {
                public void run() {
                    try {
                        startLatch.await();
                        Connection conn = dataSource.getConnection();
                    } catch (DataSourceDisableException ex) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }
        startLatch.countDown();
        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        Thread.sleep(1000);
        new Thread("close thread") {
            public void run() {
                dataSource.close();
            }
        }.start();
        Assert.assertTrue(endLatch.await(60, TimeUnit.SECONDS));
    }
}

