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
package org.apache.dubbo.registry.dubbo;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.RegistryService;
import org.apache.dubbo.registry.support.FailbackRegistry;
import org.apache.dubbo.rpc.Invoker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class DubboRegistryTest {
    private static final Logger logger = LoggerFactory.getLogger(DubboRegistryTest.class);

    private DubboRegistry dubboRegistry;

    private URL registryURL;

    private URL serviceURL;

    private NotifyListener notifyListener;

    private Invoker<RegistryService> invoker;

    private RegistryService registryService;

    @Test
    public void testRegister() {
        dubboRegistry.register(serviceURL);
        Assertions.assertEquals(1, getRegisteredSize());
    }

    @Test
    public void testUnRegister() {
        Assertions.assertEquals(0, getRegisteredSize());
        dubboRegistry.register(serviceURL);
        Assertions.assertEquals(1, getRegisteredSize());
        dubboRegistry.unregister(serviceURL);
        Assertions.assertEquals(0, getRegisteredSize());
    }

    @Test
    public void testSubscribe() {
        dubboRegistry.register(serviceURL);
        Assertions.assertEquals(1, getRegisteredSize());
        dubboRegistry.subscribe(serviceURL, notifyListener);
        Assertions.assertEquals(1, getSubscribedSize());
        Assertions.assertEquals(1, getNotifiedListeners());
    }

    @Test
    public void testUnsubscribe() {
        dubboRegistry.subscribe(serviceURL, notifyListener);
        Assertions.assertEquals(1, getSubscribedSize());
        Assertions.assertEquals(1, getNotifiedListeners());
        dubboRegistry.unsubscribe(serviceURL, notifyListener);
        Assertions.assertEquals(0, getNotifiedListeners());
    }

    private class MockDubboRegistry extends FailbackRegistry {
        private volatile boolean isAvailable = false;

        public MockDubboRegistry(URL url) {
            super(url);
        }

        @Override
        public void doRegister(URL url) {
            DubboRegistryTest.logger.info(("Begin to register: " + url));
            isAvailable = true;
        }

        @Override
        public void doUnregister(URL url) {
            DubboRegistryTest.logger.info(("Begin to ungister: " + url));
            isAvailable = false;
        }

        @Override
        public void doSubscribe(URL url, NotifyListener listener) {
            DubboRegistryTest.logger.info(("Begin to subscribe: " + url));
        }

        @Override
        public void doUnsubscribe(URL url, NotifyListener listener) {
            DubboRegistryTest.logger.info(("Begin to unSubscribe: " + url));
        }

        @Override
        public boolean isAvailable() {
            return isAvailable;
        }
    }
}

