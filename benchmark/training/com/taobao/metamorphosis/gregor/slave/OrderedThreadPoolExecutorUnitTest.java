/**
 * (C) 2007-2012 Alibaba Group Holding Limited.
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
 * Authors:
 *   wuhua <wq163@163.com> , boyan <killme2008@gmail.com>
 */
package com.taobao.metamorphosis.gregor.slave;


import com.taobao.gecko.service.Connection;
import com.taobao.metamorphosis.gregor.slave.OrderedThreadPoolExecutor.TasksQueue;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;


// import com.taobao.metamorphosis.notifyadapter.OrderedThreadPoolExecutor.TasksQueue;
public class OrderedThreadPoolExecutorUnitTest {
    private OrderedThreadPoolExecutor executor;

    int threadCount = 10;

    private Connection conn;

    private TasksQueue taskQueue;

    @Test
    public void testExecuteInOrderMultiTask() throws Exception {
        final LinkedList<Integer> numbers = new LinkedList<Integer>();
        final CountDownLatch latch = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            final int x = i;
            this.executor.execute(new IoEvent() {
                @Override
                public void run() {
                    numbers.offer(x);
                    latch.countDown();
                }

                @Override
                public IoCatalog getIoCatalog() {
                    return new IoCatalog(OrderedThreadPoolExecutorUnitTest.this.conn, null);
                }
            });
        }
        latch.await();
        Assert.assertEquals(10000, numbers.size());
        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals(i, ((int) (numbers.poll())));
        }
    }

    @Test
    public void testExecuteInOrder() throws Exception {
        final LinkedList<Integer> numbers = new LinkedList<Integer>();
        final CountDownLatch latch = new CountDownLatch(2);
        // ?????????sleep 3?????????1
        this.executor.execute(new IoEvent() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    numbers.offer(1);
                    latch.countDown();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public IoCatalog getIoCatalog() {
                return new IoCatalog(OrderedThreadPoolExecutorUnitTest.this.conn, null);
            }
        });
        // ???????????????????2
        this.executor.execute(new IoEvent() {
            @Override
            public void run() {
                numbers.offer(2);
                latch.countDown();
            }

            @Override
            public IoCatalog getIoCatalog() {
                return new IoCatalog(OrderedThreadPoolExecutorUnitTest.this.conn, null);
            }
        });
        latch.await();
        // ???1?????
        Assert.assertEquals(1, ((int) (numbers.poll())));
        Assert.assertEquals(2, ((int) (numbers.poll())));
        Assert.assertNull(numbers.poll());
    }
}

