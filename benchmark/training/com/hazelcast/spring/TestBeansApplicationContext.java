/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spring;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.impl.clientside.HazelcastClientProxy;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "beans-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
public class TestBeansApplicationContext extends HazelcastTestSupport {
    @Autowired
    private ApplicationContext context;

    @Test
    public void testApplicationContext() {
        Assert.assertTrue(HazelcastClient.getAllHazelcastClients().isEmpty());
        context.getBean("map2");
        Assert.assertEquals(1, Hazelcast.getAllHazelcastInstances().size());
        Assert.assertEquals(1, HazelcastClient.getAllHazelcastClients().size());
        HazelcastInstance hazelcast = Hazelcast.getAllHazelcastInstances().iterator().next();
        Assert.assertEquals(2, hazelcast.getDistributedObjects().size());
        context.getBean("client");
        context.getBean("client");
        Assert.assertEquals(3, HazelcastClient.getAllHazelcastClients().size());
        HazelcastClientProxy client = ((HazelcastClientProxy) (HazelcastClient.getAllHazelcastClients().iterator().next()));
        Assert.assertNull(client.getClientConfig().getManagedContext());
        HazelcastInstance instance = ((HazelcastInstance) (context.getBean("instance")));
        Assert.assertEquals(1, Hazelcast.getAllHazelcastInstances().size());
        Assert.assertEquals(instance, Hazelcast.getAllHazelcastInstances().iterator().next());
        Assert.assertNull(instance.getConfig().getManagedContext());
    }

    @Test
    public void testPlaceHolder() {
        HazelcastInstance instance = ((HazelcastInstance) (context.getBean("instance")));
        waitInstanceForSafeState(instance);
        Config config = instance.getConfig();
        Assert.assertEquals("spring-group", config.getGroupConfig().getName());
        Assert.assertTrue(config.getNetworkConfig().getJoin().getTcpIpConfig().isEnabled());
        Assert.assertEquals(6, config.getMapConfig("map1").getBackupCount());
        Assert.assertFalse(config.getMapConfig("map1").isStatisticsEnabled());
        Assert.assertEquals(64, config.getNativeMemoryConfig().getSize().getValue());
    }
}

