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
package com.hazelcast.spring.hibernate;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.hibernate.HazelcastCacheRegionFactory;
import com.hazelcast.hibernate.HazelcastLocalCacheRegionFactory;
import com.hazelcast.spring.CustomSpringJUnit4ClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.net.InetSocketAddress;
import java.util.Set;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;


@RunWith(CustomSpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "hibernate-applicationContext-hazelcast.xml" })
@Category(QuickTest.class)
public class TestHibernateApplicationContext {
    @Resource(name = "instance")
    private HazelcastInstance instance;

    @Resource(name = "regionFactory")
    private HazelcastCacheRegionFactory regionFactory;

    @Resource(name = "localRegionFactory")
    private HazelcastLocalCacheRegionFactory localRegionFactory;

    @Resource(name = "localRegionFactory2")
    private HazelcastLocalCacheRegionFactory localRegionFactory2;

    @Test
    public void testInstance() {
        Assert.assertNotNull(instance);
        Set<Member> members = instance.getCluster().getMembers();
        Assert.assertEquals(1, members.size());
        Member member = members.iterator().next();
        InetSocketAddress inetSocketAddress = member.getSocketAddress();
        Assert.assertEquals(5700, inetSocketAddress.getPort());
    }

    @Test
    public void testRegionFactory() {
        Assert.assertNotNull(regionFactory);
        Assert.assertEquals(regionFactory.getHazelcastInstance(), instance);
        Assert.assertNotNull(localRegionFactory);
        Assert.assertEquals(localRegionFactory.getHazelcastInstance(), instance);
        Assert.assertNotNull(localRegionFactory2);
        Assert.assertEquals(localRegionFactory2.getHazelcastInstance(), instance);
    }
}

