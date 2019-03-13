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
package com.hazelcast.util;


import GroupProperty.PHONE_HOME_ENABLED;
import GroupProperty.VERSION_CHECK_ENABLED;
import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.Node;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PhoneHomeTest extends HazelcastTestSupport {
    @Test
    public void testPhoneHomeParameters() {
        HazelcastInstance hz = createHazelcastInstance();
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        HazelcastTestSupport.sleepAtLeastMillis(1);
        Map<String, String> parameters = phoneHome.phoneHome(node);
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean osMxBean = ManagementFactory.getOperatingSystemMXBean();
        Assert.assertEquals(parameters.get("version"), BuildInfoProvider.getBuildInfo().getVersion());
        Assert.assertEquals(parameters.get("m"), node.getLocalMember().getUuid());
        Assert.assertEquals(parameters.get("e"), null);
        Assert.assertEquals(parameters.get("oem"), null);
        Assert.assertEquals(parameters.get("l"), null);
        Assert.assertEquals(parameters.get("hdgb"), null);
        Assert.assertEquals(parameters.get("p"), "source");
        Assert.assertEquals(parameters.get("crsz"), "A");
        Assert.assertEquals(parameters.get("cssz"), "A");
        Assert.assertEquals(parameters.get("ccpp"), "0");
        Assert.assertEquals(parameters.get("cdn"), "0");
        Assert.assertEquals(parameters.get("cjv"), "0");
        Assert.assertEquals(parameters.get("cnjs"), "0");
        Assert.assertEquals(parameters.get("cpy"), "0");
        Assert.assertEquals(parameters.get("cgo"), "0");
        Assert.assertEquals(parameters.get("jetv"), "");
        Assert.assertFalse(((Integer.parseInt(parameters.get("cuptm"))) < 0));
        Assert.assertNotEquals(parameters.get("nuptm"), "0");
        Assert.assertNotEquals(parameters.get("nuptm"), parameters.get("cuptm"));
        Assert.assertEquals(parameters.get("osn"), osMxBean.getName());
        Assert.assertEquals(parameters.get("osa"), osMxBean.getArch());
        Assert.assertEquals(parameters.get("osv"), osMxBean.getVersion());
        Assert.assertEquals(parameters.get("jvmn"), runtimeMxBean.getVmName());
        Assert.assertEquals(parameters.get("jvmv"), System.getProperty("java.version"));
        Assert.assertEquals(parameters.get("mcver"), "MC_NOT_CONFIGURED");
        Assert.assertEquals(parameters.get("mclicense"), "MC_NOT_CONFIGURED");
    }

    @Test
    public void testPhoneHomeParameters_withManagementCenterConfiguredButNotAvailable() {
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig().setEnabled(true).setUrl("http://localhost:11111/mancen");
        Config config = new Config().setManagementCenterConfig(managementCenterConfig);
        HazelcastInstance hz = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        HazelcastTestSupport.sleepAtLeastMillis(1);
        Map<String, String> parameters = phoneHome.phoneHome(node);
        Assert.assertEquals(parameters.get("mcver"), "MC_NOT_AVAILABLE");
        Assert.assertEquals(parameters.get("mclicense"), "MC_NOT_AVAILABLE");
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testScheduling_whenVersionCheckIsDisabled() {
        Config config = new Config().setProperty(VERSION_CHECK_ENABLED.getName(), "false");
        HazelcastInstance hz = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        phoneHome.check(node);
        Assert.assertNull(phoneHome.phoneHomeFuture);
    }

    @Test
    public void testScheduling_whenPhoneHomeIsDisabled() {
        Config config = new Config().setProperty(PHONE_HOME_ENABLED.getName(), "false");
        HazelcastInstance hz = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        phoneHome.check(node);
        Assert.assertNull(phoneHome.phoneHomeFuture);
    }

    @Test
    public void testShutdown() {
        Assume.assumeFalse("Skipping. The PhoneHome is disabled by the Environment variable", "false".equals(System.getenv("HZ_PHONE_HOME_ENABLED")));
        Config config = new Config().setProperty(PHONE_HOME_ENABLED.getName(), "true");
        HazelcastInstance hz = createHazelcastInstance(config);
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        phoneHome.check(node);
        Assert.assertNotNull(phoneHome.phoneHomeFuture);
        Assert.assertFalse(phoneHome.phoneHomeFuture.isDone());
        Assert.assertFalse(phoneHome.phoneHomeFuture.isCancelled());
        phoneHome.shutdown();
        Assert.assertTrue(phoneHome.phoneHomeFuture.isCancelled());
    }

    @Test
    public void testConvertToLetter() {
        HazelcastInstance hz = createHazelcastInstance();
        Node node = HazelcastTestSupport.getNode(hz);
        PhoneHome phoneHome = new PhoneHome(node);
        Assert.assertEquals("A", phoneHome.convertToLetter(4));
        Assert.assertEquals("B", phoneHome.convertToLetter(9));
        Assert.assertEquals("C", phoneHome.convertToLetter(19));
        Assert.assertEquals("D", phoneHome.convertToLetter(39));
        Assert.assertEquals("E", phoneHome.convertToLetter(59));
        Assert.assertEquals("F", phoneHome.convertToLetter(99));
        Assert.assertEquals("G", phoneHome.convertToLetter(149));
        Assert.assertEquals("H", phoneHome.convertToLetter(299));
        Assert.assertEquals("J", phoneHome.convertToLetter(599));
        Assert.assertEquals("I", phoneHome.convertToLetter(1000));
    }
}

