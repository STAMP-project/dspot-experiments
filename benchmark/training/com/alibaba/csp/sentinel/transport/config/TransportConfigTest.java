/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.transport.config;


import TransportConfig.HEARTBEAT_CLIENT_IP;
import TransportConfig.HEARTBEAT_INTERVAL_MS;
import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;


public class TransportConfigTest {
    @Test
    public void testGetHeartbeatInterval() {
        long interval = 20000;
        Assert.assertNull(TransportConfig.getHeartbeatIntervalMs());
        // Set valid interval.
        SentinelConfig.setConfig(HEARTBEAT_INTERVAL_MS, String.valueOf(interval));
        Assert.assertEquals(new Long(interval), TransportConfig.getHeartbeatIntervalMs());
        // Set invalid interval.
        SentinelConfig.setConfig(HEARTBEAT_INTERVAL_MS, "Sentinel");
        Assert.assertNull(TransportConfig.getHeartbeatIntervalMs());
    }

    @Test
    public void testGetHeartbeatClientIp() {
        String clientIp = "10.10.10.10";
        SentinelConfig.setConfig(HEARTBEAT_CLIENT_IP, clientIp);
        // Set heartbeat client ip to system property.
        String ip = TransportConfig.getHeartbeatClientIp();
        Assert.assertNotNull(ip);
        Assert.assertEquals(clientIp, ip);
        // Set no heartbeat client ip.
        SentinelConfig.setConfig(HEARTBEAT_CLIENT_IP, "");
        Assert.assertTrue(StringUtil.isNotEmpty(TransportConfig.getHeartbeatClientIp()));
    }
}

