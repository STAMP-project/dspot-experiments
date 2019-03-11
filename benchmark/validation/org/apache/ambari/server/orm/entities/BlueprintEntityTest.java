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


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


/**
 * BlueprintEntity unit tests
 */
public class BlueprintEntityTest {
    private StackEntity stackEntity = new StackEntity();

    @Test
    public void testSetGetBlueprintName() {
        BlueprintEntity entity = new BlueprintEntity();
        entity.setBlueprintName("foo");
        Assert.assertEquals("foo", entity.getBlueprintName());
    }

    @Test
    public void testSetGetStack() {
        BlueprintEntity entity = new BlueprintEntity();
        entity.setStack(stackEntity);
        Assert.assertEquals(stackEntity, entity.getStack());
    }

    @Test
    public void testSetGetHostGroups() {
        BlueprintEntity entity = new BlueprintEntity();
        Collection<HostGroupEntity> hostGroups = Collections.emptyList();
        entity.setHostGroups(hostGroups);
        Assert.assertSame(hostGroups, entity.getHostGroups());
    }

    @Test
    public void testSetGetConfigurations() {
        BlueprintEntity entity = new BlueprintEntity();
        Collection<BlueprintConfigEntity> configurations = Collections.emptyList();
        entity.setConfigurations(configurations);
        Assert.assertSame(configurations, entity.getConfigurations());
    }

    /**
     * Test get and set of Setting object.
     */
    @Test
    public void testSetGetSetting() {
        BlueprintEntity entity = new BlueprintEntity();
        Collection<BlueprintSettingEntity> setting = Collections.emptyList();
        entity.setSettings(setting);
        Assert.assertSame(setting, entity.getSettings());
    }
}

