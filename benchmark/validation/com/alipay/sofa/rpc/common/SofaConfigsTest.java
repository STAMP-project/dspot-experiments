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
package com.alipay.sofa.rpc.common;


import SofaConfigs.ExternalConfigLoader;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="mailto:zhanggeng.zg@antfin.com">GengZhang</a>
 */
public class SofaConfigsTest {
    @Test
    public void testConfigLoader() throws Exception {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("xx", "yy");
        SofaConfigs.ExternalConfigLoader loader = new SofaConfigs.ExternalConfigLoader() {
            @Override
            public String getValue(String key) {
                return map.get(key);
            }

            @Override
            public String getValue(String appName, String key) {
                return map.get(key);
            }
        };
        try {
            SofaConfigs.registerExternalConfigLoader(loader);
            Assert.assertNull(SofaConfigs.getStringValue("zzzz", null));
            Assert.assertEquals(SofaConfigs.getStringValue("zzzz", "ddd"), "ddd");
            Assert.assertEquals(SofaConfigs.getStringValue("xx", null), "yy");
            System.setProperty("zzzz", "aaa");
            System.setProperty("xx", "bbbb");
            Assert.assertEquals(SofaConfigs.getStringValue("zzzz", null), "aaa");
            Assert.assertEquals(SofaConfigs.getStringValue("xx", null), "bbbb");
        } finally {
            SofaConfigs.unRegisterExternalConfigLoader(loader);
        }
    }

    @Test
    public void readAndWriteLock() {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("xx", "yy");
        final SofaConfigs.ExternalConfigLoader loader = new SofaConfigs.ExternalConfigLoader() {
            @Override
            public String getValue(String key) {
                return map.get(key);
            }

            @Override
            public String getValue(String appName, String key) {
                return map.get(key);
            }
        };
        final AtomicBoolean run = new AtomicBoolean(true);
        final AtomicBoolean error = new AtomicBoolean(false);// ?????????????

        final CountDownLatch latch = new CountDownLatch(1);// ???? ????

        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (run.get()) {
                    try {
                        // ?????
                        SofaConfigs.getStringValue("xx", null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        error.set(true);
                        latch.countDown();
                    }
                } 
            }
        }, "readThread");
        Thread writeThread = new Thread(new Runnable() {
            boolean sw = false;

            @Override
            public void run() {
                while (run.get()) {
                    try {
                        // ????? ???
                        if (!(sw)) {
                            SofaConfigs.registerExternalConfigLoader(loader);
                        } else {
                            SofaConfigs.unRegisterExternalConfigLoader(loader);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        error.set(true);
                        latch.countDown();
                    } finally {
                        sw = !(sw);
                    }
                } 
            }
        }, "writeThread");
        readThread.start();
        writeThread.start();
        // ???3? ?????
        try {
            latch.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            run.set(false);
        }
        Assert.assertFalse(error.get());
    }
}

