/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.orm.entities;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for HostGroupConfigEntity.
 */
public class HostGroupConfigEntityTest {
    @Test
    public void testSetGetHostGroupName() {
        HostGroupConfigEntity entity = new HostGroupConfigEntity();
        entity.setHostGroupName("foo");
        Assert.assertEquals("foo", entity.getHostGroupName());
    }

    @Test
    public void testSetGetType() {
        HostGroupConfigEntity entity = new HostGroupConfigEntity();
        entity.setType("testType");
        Assert.assertEquals("testType", entity.getType());
    }

    @Test
    public void testSetGetHostGroupEntity() {
        HostGroupEntity group = new HostGroupEntity();
        HostGroupConfigEntity entity = new HostGroupConfigEntity();
        entity.setHostGroupEntity(group);
        Assert.assertSame(group, entity.getHostGroupEntity());
    }

    @Test
    public void testSetGetBlueprintName() {
        HostGroupConfigEntity entity = new HostGroupConfigEntity();
        entity.setBlueprintName("foo");
        Assert.assertEquals("foo", entity.getBlueprintName());
    }

    @Test
    public void testSetGetConfigData() {
        HostGroupConfigEntity entity = new HostGroupConfigEntity();
        String configData = "{ \"prop_name\" : \"value\" }";
        entity.setConfigData(configData);
        Assert.assertEquals(configData, entity.getConfigData());
    }
}

