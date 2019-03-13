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
package com.hazelcast.cluster;


import GroupProperty.APPLICATION_VALIDATION_TOKEN;
import GroupProperty.PARTITION_COUNT;
import PartitionGroupConfig.MemberGroupType.CUSTOM;
import PartitionGroupConfig.MemberGroupType.PER_MEMBER;
import com.hazelcast.config.Config;
import com.hazelcast.internal.cluster.impl.ConfigCheck;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ConfigCheckTest {
    @Test
    public void whenGroupNameDifferent_thenFalse() {
        Config config1 = new Config();
        config1.getGroupConfig().setName("foo");
        Config config2 = new Config();
        config2.getGroupConfig().setName("bar");
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleFalse(configCheck1, configCheck2);
    }

    @Test
    public void whenGroupPasswordDifferent_thenJoin() {
        Config config1 = new Config();
        config1.getGroupConfig().setName("foo");
        config1.getGroupConfig().setPassword("Here");
        Config config2 = new Config();
        config2.getGroupConfig().setName("foo");
        config2.getGroupConfig().setPassword("There");
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleTrue(configCheck1, configCheck2);
    }

    @Test
    public void testGroupPasswordNotLeak_whenVersionAboveThreeNine() {
        final Config config = new Config();
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true).setMulticastTimeoutSeconds(3);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        final AtomicBoolean leaked = new AtomicBoolean(false);
        ObjectDataOutput odo = Mockito.mock(ObjectDataOutput.class);
        try {
            ConfigCheck configCheck = new ConfigCheck(config, "multicast");
            configCheck.writeData(odo);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        try {
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(odo, Mockito.times(7)).writeUTF(captor.capture());
            List<String> values = captor.getAllValues();
            if (values.contains(config.getGroupConfig().getPassword())) {
                leaked.set(true);
            }
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals("Password leaked in output stream.", false, leaked.get());
    }

    @Test
    public void whenJoinerTypeDifferent_thenConfigMismatchException() {
        Config config1 = new Config();
        Config config2 = new Config();
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner1");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner2");
        assertIsCompatibleThrowsConfigMismatchException(configCheck1, configCheck2);
    }

    @Test
    public void whenDifferentPartitionCount_thenConfigurationMismatchException() {
        Config config1 = new Config();
        config1.setProperty(PARTITION_COUNT.getName(), "100");
        Config config2 = new Config();
        config2.setProperty(PARTITION_COUNT.getName(), "200");
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleThrowsConfigMismatchException(configCheck1, configCheck2);
    }

    @Test
    public void whenDifferentApplicationValidationToken_thenConfigurationMismatchException() {
        Config config1 = new Config();
        config1.setProperty(APPLICATION_VALIDATION_TOKEN.getName(), "foo");
        Config config2 = new Config();
        config2.setProperty(APPLICATION_VALIDATION_TOKEN.getName(), "bar");
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleThrowsConfigMismatchException(configCheck1, configCheck2);
    }

    @Test
    public void whenGroupPartitionEnabledMismatch_thenConfigurationMismatchException() {
        Config config1 = new Config();
        config1.getPartitionGroupConfig().setEnabled(false);
        Config config2 = new Config();
        config2.getPartitionGroupConfig().setEnabled(true);
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleThrowsConfigMismatchException(configCheck1, configCheck2);
    }

    @Test
    public void whenPartitionGroupGroupTypeMismatch_thenConfigurationMismatchException() {
        Config config1 = new Config();
        config1.getPartitionGroupConfig().setEnabled(true);
        config1.getPartitionGroupConfig().setGroupType(CUSTOM);
        Config config2 = new Config();
        config2.getPartitionGroupConfig().setEnabled(true);
        config2.getPartitionGroupConfig().setGroupType(PER_MEMBER);
        ConfigCheck configCheck1 = new ConfigCheck(config1, "joiner");
        ConfigCheck configCheck2 = new ConfigCheck(config2, "joiner");
        assertIsCompatibleThrowsConfigMismatchException(configCheck1, configCheck2);
    }
}

