/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.protocol.dubbo;


import Constants.CALLBACK_INSTANCES_LIMIT_KEY;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Exporter;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class ExplicitCallbackTest {
    protected Exporter<ExplicitCallbackTest.IDemoService> exporter = null;

    protected Exporter<ExplicitCallbackTest.IHelloService> hello_exporter = null;

    protected Invoker<ExplicitCallbackTest.IDemoService> reference = null;

    protected URL serviceURL = null;

    protected URL consumerUrl = null;

    // ============================A gorgeous line of segmentation================================================
    ExplicitCallbackTest.IDemoService demoProxy = null;

    @Test
    public void TestCallbackNormal() throws Exception {
        initOrResetUrl(1, 10000000);
        initOrResetService();
        final AtomicInteger count = new AtomicInteger(0);
        demoProxy.xxx(new ExplicitCallbackTest.IDemoCallback() {
            public String yyy(String msg) {
                System.out.println(("Recived callback: " + msg));
                count.incrementAndGet();
                return "ok";
            }
        }, "other custom args", 10, 100);
        System.out.println("Async...");
        // Thread.sleep(10000000);
        assertCallbackCount(10, 100, count);
        destroyService();
    }

    @Test
    public void TestCallbackMultiInstans() throws Exception {
        initOrResetUrl(2, 3000);
        initOrResetService();
        ExplicitCallbackTest.IDemoCallback callback = new ExplicitCallbackTest.IDemoCallback() {
            public String yyy(String msg) {
                System.out.println(("callback1:" + msg));
                return "callback1 onChanged ," + msg;
            }
        };
        ExplicitCallbackTest.IDemoCallback callback2 = new ExplicitCallbackTest.IDemoCallback() {
            public String yyy(String msg) {
                System.out.println(("callback2:" + msg));
                return "callback2 onChanged ," + msg;
            }
        };
        {
            demoProxy.xxx2(callback);
            Assertions.assertEquals(1, demoProxy.getCallbackCount());
            Thread.sleep(500);
            demoProxy.unxxx2(callback);
            Assertions.assertEquals(0, demoProxy.getCallbackCount());
            demoProxy.xxx2(callback2);
            Assertions.assertEquals(1, demoProxy.getCallbackCount());
            Thread.sleep(500);
            demoProxy.unxxx2(callback2);
            Assertions.assertEquals(0, demoProxy.getCallbackCount());
            demoProxy.xxx2(callback);
            Thread.sleep(500);
            Assertions.assertEquals(1, demoProxy.getCallbackCount());
            demoProxy.unxxx2(callback);
            Assertions.assertEquals(0, demoProxy.getCallbackCount());
        }
        {
            demoProxy.xxx2(callback);
            Assertions.assertEquals(1, demoProxy.getCallbackCount());
            demoProxy.xxx2(callback);
            Assertions.assertEquals(1, demoProxy.getCallbackCount());
            demoProxy.xxx2(callback2);
            Assertions.assertEquals(2, demoProxy.getCallbackCount());
        }
        destroyService();
    }

    @Test
    public void TestCallbackConsumerLimit() throws Exception {
        Assertions.assertThrows(RpcException.class, () -> {
            initOrResetUrl(1, 1000);
            // URL cannot be transferred automatically from the server side to the client side by using API, instead,
            // it needs manually specified.
            initOrResetService();
            final AtomicInteger count = new AtomicInteger(0);
            demoProxy.xxx(new ExplicitCallbackTest.IDemoCallback() {
                public String yyy(String msg) {
                    System.out.println(("Recived callback: " + msg));
                    count.incrementAndGet();
                    return "ok";
                }
            }, "other custom args", 10, 100);
            demoProxy.xxx(new ExplicitCallbackTest.IDemoCallback() {
                public String yyy(String msg) {
                    System.out.println(("Recived callback: " + msg));
                    count.incrementAndGet();
                    return "ok";
                }
            }, "other custom args", 10, 100);
            destroyService();
        });
    }

    @Test
    public void TestCallbackProviderLimit() throws Exception {
        Assertions.assertThrows(RpcException.class, () -> {
            initOrResetUrl(1, 1000);
            // URL cannot be transferred automatically from the server side to the client side by using API, instead,
            // it needs manually specified.
            serviceURL = serviceURL.addParameter(CALLBACK_INSTANCES_LIMIT_KEY, (1 + ""));
            initOrResetService();
            final AtomicInteger count = new AtomicInteger(0);
            demoProxy.xxx(new ExplicitCallbackTest.IDemoCallback() {
                public String yyy(String msg) {
                    System.out.println(("Recived callback: " + msg));
                    count.incrementAndGet();
                    return "ok";
                }
            }, "other custom args", 10, 100);
            demoProxy.xxx(new ExplicitCallbackTest.IDemoCallback() {
                public String yyy(String msg) {
                    System.out.println(("Recived callback: " + msg));
                    count.incrementAndGet();
                    return "ok";
                }
            }, "other custom args", 10, 100);
            destroyService();
        });
    }

    interface IDemoCallback {
        String yyy(String msg);
    }

    interface IHelloService {
        public String sayHello();
    }

    interface IDemoService {
        public String get();

        public int getCallbackCount();

        public void xxx(ExplicitCallbackTest.IDemoCallback callback, String arg1, int runs, int sleep);

        public void xxx2(ExplicitCallbackTest.IDemoCallback callback);

        public void unxxx2(ExplicitCallbackTest.IDemoCallback callback);
    }

    class HelloServiceImpl implements ExplicitCallbackTest.IHelloService {
        public String sayHello() {
            return "hello";
        }
    }

    class DemoServiceImpl implements ExplicitCallbackTest.IDemoService {
        private List<ExplicitCallbackTest.IDemoCallback> callbacks = new ArrayList<ExplicitCallbackTest.IDemoCallback>();

        private volatile Thread t = null;

        private volatile Lock lock = new ReentrantLock();

        public String get() {
            return "ok";
        }

        public void xxx(final ExplicitCallbackTest.IDemoCallback callback, String arg1, final int runs, final int sleep) {
            callback.yyy(("Sync callback msg .This is callback data. arg1:" + arg1));
            Thread t = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; i < runs; i++) {
                        String ret = callback.yyy(("server invoke callback : arg:" + (System.currentTimeMillis())));
                        System.out.println(("callback result is :" + ret));
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
            System.out.println("xxx invoke complete");
        }

        public int getCallbackCount() {
            return callbacks.size();
        }

        public void xxx2(ExplicitCallbackTest.IDemoCallback callback) {
            if (!(callbacks.contains(callback))) {
                callbacks.add(callback);
            }
            startThread();
        }

        private void startThread() {
            if (((t) == null) || ((callbacks.size()) == 0)) {
                try {
                    lock.lock();
                    t = new Thread(new Runnable() {
                        public void run() {
                            while ((callbacks.size()) > 0) {
                                try {
                                    List<ExplicitCallbackTest.IDemoCallback> callbacksCopy = new ArrayList<ExplicitCallbackTest.IDemoCallback>(callbacks);
                                    for (ExplicitCallbackTest.IDemoCallback callback : callbacksCopy) {
                                        try {
                                            callback.yyy(("this is callback msg,current time is :" + (System.currentTimeMillis())));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            callbacks.remove(callback);
                                        }
                                    }
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } 
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void unxxx2(ExplicitCallbackTest.IDemoCallback callback) {
            if (!(callbacks.contains(callback))) {
                throw new IllegalStateException("callback instance not found");
            }
            callbacks.remove(callback);
        }
    }
}

