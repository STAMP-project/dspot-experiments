/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.resource;


import ResourceType.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;

import static ResourceType.ANY;
import static ResourceType.CLUSTER;
import static ResourceType.DELEGATION_TOKEN;
import static ResourceType.GROUP;
import static ResourceType.TOPIC;
import static ResourceType.TRANSACTIONAL_ID;
import static ResourceType.UNKNOWN;


public class ResourceTypeTest {
    private static class AclResourceTypeTestInfo {
        private final ResourceType resourceType;

        private final int code;

        private final String name;

        private final boolean unknown;

        AclResourceTypeTestInfo(ResourceType resourceType, int code, String name, boolean unknown) {
            this.resourceType = resourceType;
            this.code = code;
            this.name = name;
            this.unknown = unknown;
        }
    }

    private static final ResourceTypeTest.AclResourceTypeTestInfo[] INFOS = new ResourceTypeTest.AclResourceTypeTestInfo[]{ new ResourceTypeTest.AclResourceTypeTestInfo(UNKNOWN, 0, "unknown", true), new ResourceTypeTest.AclResourceTypeTestInfo(ANY, 1, "any", false), new ResourceTypeTest.AclResourceTypeTestInfo(TOPIC, 2, "topic", false), new ResourceTypeTest.AclResourceTypeTestInfo(GROUP, 3, "group", false), new ResourceTypeTest.AclResourceTypeTestInfo(CLUSTER, 4, "cluster", false), new ResourceTypeTest.AclResourceTypeTestInfo(TRANSACTIONAL_ID, 5, "transactional_id", false), new ResourceTypeTest.AclResourceTypeTestInfo(DELEGATION_TOKEN, 6, "delegation_token", false) };

    @Test
    public void testIsUnknown() {
        for (ResourceTypeTest.AclResourceTypeTestInfo info : ResourceTypeTest.INFOS) {
            Assert.assertEquals((((info.resourceType) + " was supposed to have unknown == ") + (info.unknown)), info.unknown, info.resourceType.isUnknown());
        }
    }

    @Test
    public void testCode() {
        Assert.assertEquals(ResourceType.values().length, ResourceTypeTest.INFOS.length);
        for (ResourceTypeTest.AclResourceTypeTestInfo info : ResourceTypeTest.INFOS) {
            Assert.assertEquals((((info.resourceType) + " was supposed to have code == ") + (info.code)), info.code, info.resourceType.code());
            Assert.assertEquals(((("AclResourceType.fromCode(" + (info.code)) + ") was supposed to be ") + (info.resourceType)), info.resourceType, ResourceType.fromCode(((byte) (info.code))));
        }
        Assert.assertEquals(UNKNOWN, ResourceType.fromCode(((byte) (120))));
    }

    @Test
    public void testName() {
        for (ResourceTypeTest.AclResourceTypeTestInfo info : ResourceTypeTest.INFOS) {
            Assert.assertEquals(((("ResourceType.fromString(" + (info.name)) + ") was supposed to be ") + (info.resourceType)), info.resourceType, ResourceType.fromString(info.name));
        }
        Assert.assertEquals(UNKNOWN, ResourceType.fromString("something"));
    }

    @Test
    public void testExhaustive() {
        Assert.assertEquals(ResourceTypeTest.INFOS.length, ResourceType.values().length);
        for (int i = 0; i < (ResourceTypeTest.INFOS.length); i++) {
            Assert.assertEquals(ResourceTypeTest.INFOS[i].resourceType, ResourceType.values()[i]);
        }
    }
}

