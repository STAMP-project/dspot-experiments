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
package org.apache.dubbo.registry.redis;


import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import redis.embedded.RedisServer;


public class RedisRegistryTest {
    private String service = "org.apache.dubbo.test.injvmServie";

    private URL serviceUrl = URL.valueOf((("redis://redis/" + (service)) + "?notify=false&methods=test1,test2"));

    private RedisServer redisServer;

    private RedisRegistry redisRegistry;

    private URL registryUrl;

    @Test
    public void testRegister() {
        Set<URL> registered = null;
        for (int i = 0; i < 2; i++) {
            redisRegistry.register(serviceUrl);
            registered = redisRegistry.getRegistered();
            MatcherAssert.assertThat(registered.contains(serviceUrl), CoreMatchers.is(true));
        }
        registered = redisRegistry.getRegistered();
        MatcherAssert.assertThat(registered.size(), CoreMatchers.is(1));
    }

    @Test
    public void testAnyHost() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            URL errorUrl = URL.valueOf("multicast://0.0.0.0/");
            new RedisRegistryFactory().createRegistry(errorUrl);
        });
    }

    @Test
    public void testSubscribeAndUnsubscribe() {
        NotifyListener listener = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
            }
        };
        redisRegistry.subscribe(serviceUrl, listener);
        Map<URL, Set<NotifyListener>> subscribed = redisRegistry.getSubscribed();
        MatcherAssert.assertThat(subscribed.size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(subscribed.get(serviceUrl).size(), CoreMatchers.is(1));
        redisRegistry.unsubscribe(serviceUrl, listener);
        subscribed = redisRegistry.getSubscribed();
        MatcherAssert.assertThat(subscribed.get(serviceUrl).size(), CoreMatchers.is(0));
    }

    @Test
    public void testAvailable() {
        redisRegistry.register(serviceUrl);
        MatcherAssert.assertThat(redisRegistry.isAvailable(), CoreMatchers.is(true));
        redisRegistry.destroy();
        MatcherAssert.assertThat(redisRegistry.isAvailable(), CoreMatchers.is(false));
    }

    @Test
    public void testAvailableWithBackup() {
        URL url = URL.valueOf("redis://redisOne:8880").addParameter(BACKUP_KEY, "redisTwo:8881");
        Registry registry = new RedisRegistryFactory().createRegistry(url);
        MatcherAssert.assertThat(registry.isAvailable(), CoreMatchers.is(false));
        url = URL.valueOf(this.registryUrl.toFullString()).addParameter(BACKUP_KEY, "redisTwo:8881");
        registry = new RedisRegistryFactory().createRegistry(url);
        MatcherAssert.assertThat(registry.isAvailable(), CoreMatchers.is(true));
    }
}

