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
package org.apache.dubbo.registry.etcd;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.RegistryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
public class EtcdRegistryTest {
    String service = "org.apache.dubbo.internal.test.DemoServie";

    String outerService = "org.apache.dubbo.outer.test.OuterDemoServie";

    URL serviceUrl = URL.valueOf((((("dubbo://" + (NetUtils.getLocalHost())) + "/") + (service)) + "?methods=test1,test2"));

    URL serviceUrl2 = URL.valueOf((((("dubbo://" + (NetUtils.getLocalHost())) + "/") + (service)) + "?methods=test1,test2,test3"));

    URL serviceUrl3 = URL.valueOf((((("dubbo://" + (NetUtils.getLocalHost())) + "/") + (outerService)) + "?methods=test1,test2"));

    URL registryUrl = URL.valueOf("etcd3://127.0.0.1:2379/org.apache.dubbo.registry.RegistryService");

    URL consumerUrl = URL.valueOf(((((("dubbo://" + (NetUtils.getLocalHost())) + ":2018") + "/") + (service)) + "?methods=test1,test2"));

    RegistryFactory registryFactory = ExtensionLoader.getExtensionLoader(RegistryFactory.class).getAdaptiveExtension();

    EtcdRegistry registry;

    URL subscribe = new URL(Constants.ADMIN_PROTOCOL, NetUtils.getLocalHost(), 0, "", Constants.INTERFACE_KEY, Constants.ANY_VALUE, Constants.GROUP_KEY, Constants.ANY_VALUE, Constants.VERSION_KEY, Constants.ANY_VALUE, Constants.CLASSIFIER_KEY, Constants.ANY_VALUE, Constants.CATEGORY_KEY, (((((((Constants.PROVIDERS_CATEGORY) + ",") + (Constants.CONSUMERS_CATEGORY)) + ",") + (Constants.ROUTERS_CATEGORY)) + ",") + (Constants.CONFIGURATORS_CATEGORY)), Constants.ENABLED_KEY, Constants.ANY_VALUE, Constants.CHECK_KEY, String.valueOf(false));

    @Test
    public void test_register() {
        registry.register(serviceUrl);
        Set<URL> registered = registry.getRegistered();
        Assertions.assertEquals(1, registered.size());
        Assertions.assertTrue(registered.contains(serviceUrl));
        registry.unregister(serviceUrl);
    }

    @Test
    public void test_unregister() {
        registry.register(serviceUrl);
        Set<URL> registered = registry.getRegistered();
        Assertions.assertTrue(((registered.size()) == 1));
        Assertions.assertTrue(registered.contains(serviceUrl));
        registry.unregister(serviceUrl);
        registered = registry.getRegistered();
        Assertions.assertTrue(((registered.size()) == 0));
    }

    @Test
    public void test_subscribe() {
        registry.register(serviceUrl);
        final AtomicReference<URL> notifiedUrl = new AtomicReference<URL>();
        registry.subscribe(consumerUrl, new NotifyListener() {
            public void notify(List<URL> urls) {
                notifiedUrl.set(urls.get(0));
            }
        });
        Assertions.assertEquals(serviceUrl.toFullString(), notifiedUrl.get().toFullString());
        Map<URL, Set<NotifyListener>> arg = registry.getSubscribed();
        Assertions.assertEquals(consumerUrl, arg.keySet().iterator().next());
    }

    @Test
    public void test_subscribe_when_register() throws InterruptedException {
        Assertions.assertTrue(((registry.getRegistered().size()) == 0));
        Assertions.assertTrue(((registry.getSubscribed().size()) == 0));
        CountDownLatch notNotified = new CountDownLatch(2);
        final AtomicReference<URL> notifiedUrl = new AtomicReference<URL>();
        registry.subscribe(consumerUrl, new NotifyListener() {
            public void notify(List<URL> urls) {
                notifiedUrl.set(urls.get(0));
                notNotified.countDown();
            }
        });
        registry.register(serviceUrl);
        Assertions.assertTrue(notNotified.await(15, TimeUnit.SECONDS));
        Assertions.assertEquals(serviceUrl.toFullString(), notifiedUrl.get().toFullString());
        Map<URL, Set<NotifyListener>> subscribed = registry.getSubscribed();
        Assertions.assertEquals(consumerUrl, subscribed.keySet().iterator().next());
    }

    @Test
    public void test_subscribe_when_register0() throws InterruptedException {
        Assertions.assertTrue(((registry.getRegistered().size()) == 0));
        Assertions.assertTrue(((registry.getSubscribed().size()) == 0));
        CountDownLatch notNotified = new CountDownLatch(3);
        ConcurrentHashMap<URL, Boolean> notifiedUrls = new ConcurrentHashMap<>();
        registry.subscribe(consumerUrl, new NotifyListener() {
            public void notify(List<URL> urls) {
                if ((urls != null) && ((urls.size()) > 0)) {
                    if (!(urls.get(0).getProtocol().equals("empty"))) {
                        for (Iterator<URL> iterator = urls.iterator(); iterator.hasNext();) {
                            notifiedUrls.put(iterator.next(), true);
                        }
                    }
                }
                notNotified.countDown();
            }
        });
        registry.register(serviceUrl);
        registry.register(serviceUrl2);
        Assertions.assertTrue(notNotified.await(15, TimeUnit.SECONDS));
        Assertions.assertTrue(notifiedUrls.containsKey(serviceUrl));
        Assertions.assertTrue(notifiedUrls.containsKey(serviceUrl2));
        Map<URL, Set<NotifyListener>> subscribed = registry.getSubscribed();
        Assertions.assertEquals(consumerUrl, subscribed.keySet().iterator().next());
    }

    @Test
    public void test_subscribe_when_register1() throws InterruptedException {
        Assertions.assertTrue(((registry.getRegistered().size()) == 0));
        Assertions.assertTrue(((registry.getSubscribed().size()) == 0));
        CountDownLatch notNotified = new CountDownLatch(2);
        final AtomicReference<URL> notifiedUrls = new AtomicReference<URL>();
        registry.subscribe(consumerUrl, new NotifyListener() {
            public void notify(List<URL> urls) {
                notifiedUrls.set(urls.get(0));
                notNotified.countDown();
            }
        });
        registry.register(serviceUrl);
        // register service3 should not trigger notify
        registry.register(serviceUrl3);
        Assertions.assertTrue(notNotified.await(15, TimeUnit.SECONDS));
        Assertions.assertEquals(serviceUrl, notifiedUrls.get());
        Map<URL, Set<NotifyListener>> subscribed = registry.getSubscribed();
        Assertions.assertEquals(consumerUrl, subscribed.keySet().iterator().next());
    }

    @Test
    public void test_subscribe_when_register2() throws InterruptedException {
        Assertions.assertTrue(((registry.getRegistered().size()) == 0));
        Assertions.assertTrue(((registry.getSubscribed().size()) == 0));
        CountDownLatch notNotified = new CountDownLatch(3);
        ConcurrentHashMap<URL, Boolean> notifiedUrls = new ConcurrentHashMap<>();
        registry.subscribe(subscribe, new NotifyListener() {
            public void notify(List<URL> urls) {
                if ((urls != null) && ((urls.size()) > 0)) {
                    if (!(urls.get(0).getProtocol().equals("empty"))) {
                        for (Iterator<URL> iterator = urls.iterator(); iterator.hasNext();) {
                            notifiedUrls.put(iterator.next(), true);
                        }
                        notNotified.countDown();
                    }
                }
            }
        });
        registry.register(serviceUrl);
        registry.register(serviceUrl2);
        // service3 interface is not equals server2
        registry.register(serviceUrl3);
        Assertions.assertTrue(notNotified.await(15, TimeUnit.SECONDS));
        Assertions.assertTrue(((notifiedUrls.size()) == 3));
        Assertions.assertTrue(notifiedUrls.containsKey(serviceUrl));
        Assertions.assertTrue(notifiedUrls.containsKey(serviceUrl2));
        Assertions.assertTrue(notifiedUrls.containsKey(serviceUrl3));
    }

    @Test
    public void test_unsubscribe() throws InterruptedException {
        Assertions.assertTrue(((registry.getRegistered().size()) == 0));
        Assertions.assertTrue(((registry.getSubscribed().size()) == 0));
        CountDownLatch notNotified = new CountDownLatch(2);
        final AtomicReference<URL> notifiedUrl = new AtomicReference<URL>();
        NotifyListener listener = new NotifyListener() {
            public void notify(List<URL> urls) {
                if (urls != null) {
                    for (Iterator<URL> iterator = urls.iterator(); iterator.hasNext();) {
                        URL url = iterator.next();
                        if (!(url.getProtocol().equals("empty"))) {
                            notifiedUrl.set(url);
                            notNotified.countDown();
                        }
                    }
                }
            }
        };
        registry.subscribe(consumerUrl, listener);
        registry.unsubscribe(consumerUrl, listener);
        registry.register(serviceUrl);
        Assertions.assertFalse(notNotified.await(2, TimeUnit.SECONDS));
        // expect nothing happen
        Assertions.assertTrue(((notifiedUrl.get()) == null));
    }
}

