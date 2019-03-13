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
package com.hazelcast.internal.usercodedeployment.impl.filter;


import com.hazelcast.config.Config;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.starter.HazelcastStarterUtils;
import com.hazelcast.util.FilteringClassLoader;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import usercodedeployment.EntryProcessorWithAnonymousAndInner;
import usercodedeployment.IncrementingEntryProcessor;
import usercodedeployment.blacklisted.BlacklistedEP;
import usercodedeployment.whitelisted.WhitelistedEP;


public abstract class UserCodeDeploymentAbstractTest extends HazelcastTestSupport {
    protected TestHazelcastInstanceFactory factory;

    @Test
    public void testUserCodeDeploymentIsDisabledByDefault() {
        // this test also validate the EP is filtered locally and has to be loaded from the other member
        Config i1Config = new Config();
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        IncrementingEntryProcessor incrementingEntryProcessor = new IncrementingEntryProcessor();
        try {
            executeSimpleTestScenario(i1Config, i2Config, incrementingEntryProcessor);
            Assert.fail();
        } catch (Exception e) {
            HazelcastStarterUtils.assertInstanceOfByClassName(HazelcastSerializationException.class.getName(), e);
        }
    }

    @Test
    public void givenSomeMemberCanAccessTheEP_whenTheEPIsFilteredLocally_thenItWillBeLoadedOverNetwork() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        IncrementingEntryProcessor incrementingEntryProcessor = new IncrementingEntryProcessor();
        executeSimpleTestScenario(i1Config, i2Config, incrementingEntryProcessor);
    }

    @Test
    public void givenSomeMemberCanAccessTheEP_whenTheEPIsFilteredLocally_thenItWillBeLoadedOverNetwork_anonymousInnerClasses() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        EntryProcessorWithAnonymousAndInner incrementingEntryProcessor = new EntryProcessorWithAnonymousAndInner();
        executeSimpleTestScenario(i1Config, i2Config, incrementingEntryProcessor);
    }

    @Test
    public void givenTheEPButItIsBlacklisted_whenTheEPIsFilteredLocally_thenItWillFailToLoadIt() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setBlacklistedPrefixes("usercodedeployment.blacklisted").setClassCacheMode(getClassCacheMode());
        EntryProcessor<Integer, Integer> myEP = new BlacklistedEP();
        try {
            executeSimpleTestScenario(i1Config, i2Config, myEP);
            Assert.fail();
        } catch (Exception e) {
            HazelcastStarterUtils.assertInstanceOfByClassName(HazelcastSerializationException.class.getName(), e);
        }
    }

    @Test
    public void givenTheEPButItIsNotOnTheWhitelist_whenTheEPIsFilteredLocally_thenItWillFailToLoadIt() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setWhitelistedPrefixes("usercodedeployment.whitelisted").setClassCacheMode(getClassCacheMode());
        EntryProcessor<Integer, Integer> myEP = new IncrementingEntryProcessor();
        try {
            executeSimpleTestScenario(i1Config, i2Config, myEP);
            Assert.fail();
        } catch (Exception e) {
            HazelcastStarterUtils.assertInstanceOfByClassName(HazelcastSerializationException.class.getName(), e);
        }
    }

    @Test
    public void givenTheEPIsOnTheWhitelist_whenTheEPIsFilteredLocally_thenItWillLoadIt() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setWhitelistedPrefixes("usercodedeployment.whitelisted, usercodedeployment").setClassCacheMode(getClassCacheMode());
        EntryProcessor<Integer, Integer> myEP = new WhitelistedEP();
        executeSimpleTestScenario(i1Config, i2Config, myEP);
    }

    @Test
    public void givenProviderFilterUsesMemberAttribute_whenNoMemberHasMatchingAttribute_thenClassLoadingRequestFails() {
        Config i1Config = new Config();
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setProviderFilter("HAS_ATTRIBUTE:foo").setClassCacheMode(getClassCacheMode());
        EntryProcessor<Integer, Integer> myEP = new IncrementingEntryProcessor();
        try {
            executeSimpleTestScenario(i1Config, i2Config, myEP);
            Assert.fail();
        } catch (Exception e) {
            HazelcastStarterUtils.assertInstanceOfByClassName(HazelcastSerializationException.class.getName(), e);
        }
    }

    @Test
    public void givenProviderFilterUsesMemberAttribute_whenSomeMemberHasMatchingAttribute_thenClassLoadingRequestSucceed() {
        Config i1Config = new Config();
        i1Config.getMemberAttributeConfig().setStringAttribute("foo", "bar");
        i1Config.getUserCodeDeploymentConfig().setEnabled(true).setClassCacheMode(getClassCacheMode());
        Config i2Config = new Config();
        FilteringClassLoader filteringCL = new FilteringClassLoader(Collections.singletonList("usercodedeployment"), null);
        i2Config.setClassLoader(filteringCL);
        i2Config.getUserCodeDeploymentConfig().setEnabled(true).setProviderFilter("HAS_ATTRIBUTE:foo").setClassCacheMode(getClassCacheMode());
        EntryProcessor<Integer, Integer> myEP = new IncrementingEntryProcessor();
        executeSimpleTestScenario(i1Config, i2Config, myEP);
    }
}

