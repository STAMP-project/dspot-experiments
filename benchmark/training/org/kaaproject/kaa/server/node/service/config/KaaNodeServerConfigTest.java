/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.node.service.config;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Igor Kulikov
 */
public class KaaNodeServerConfigTest {
    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#getThriftHost()}.
     */
    @Test
    public void testGetThriftHost() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setThriftHost("asdcasdc");
        Assert.assertEquals("asdcasdc", config.getThriftHost());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#getThriftPort()}.
     */
    @Test
    public void testGetThriftPort() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setThriftPort(2323);
        Assert.assertEquals(2323, config.getThriftPort());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#isZkEnabled()}.
     */
    @Test
    public void testIsZkEnabled() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setZkEnabled(true);
        Assert.assertEquals(true, config.isZkEnabled());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#getZkHostPortList()}.
     */
    @Test
    public void testGetZkHostPortList() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setZkHostPortList("asdcasdc");
        Assert.assertEquals("asdcasdc", config.getZkHostPortList());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#getZkMaxRetryTime()}.
     */
    @Test
    public void testGetZkMaxRetryTime() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setZkMaxRetryTime(111);
        Assert.assertEquals(111, config.getZkMaxRetryTime());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#getZkSleepTime()}.
     */
    @Test
    public void testGetZkSleepTime() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setZkSleepTime(222);
        Assert.assertEquals(222, config.getZkSleepTime());
    }

    /**
     * Test method for {@link org.kaaproject.kaa.server.node.service.config.KaaNodeServerConfig#isZkIgnoreErrors()}.
     */
    @Test
    public void testIsZkIgnoreErrors() {
        KaaNodeServerConfig config = new KaaNodeServerConfig();
        Assert.assertNotNull(config);
        config.setZkIgnoreErrors(true);
        Assert.assertEquals(true, config.isZkIgnoreErrors());
    }
}

