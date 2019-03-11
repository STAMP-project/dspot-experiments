/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.util;


import org.apache.geode.cache.server.CacheServer;
import org.apache.geode.internal.DistributionLocator;
import org.junit.Assert;
import org.junit.Test;


public class HostUtilsTest {
    @Test
    public void testGetLocatorId() {
        Assert.assertEquals("machine[11235]", HostUtils.getLocatorId("machine", 11235));
        Assert.assertEquals("machine.domain.org[11235]", HostUtils.getLocatorId("machine.domain.org", 11235));
        Assert.assertEquals((("machine[" + (DistributionLocator.DEFAULT_LOCATOR_PORT)) + "]"), HostUtils.getLocatorId("machine", null));
    }

    @Test
    public void testGetServerId() {
        Assert.assertEquals("machine[12480]", HostUtils.getServerId("machine", 12480));
        Assert.assertEquals("machine.domain.org[12480]", HostUtils.getServerId("machine.domain.org", 12480));
        Assert.assertEquals((("machine[" + (CacheServer.DEFAULT_PORT)) + "]"), HostUtils.getServerId("machine", null));
    }
}

