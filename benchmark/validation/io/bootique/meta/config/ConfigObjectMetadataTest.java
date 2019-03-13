/**
 * Licensed to ObjectStyle LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ObjectStyle LLC licenses
 * this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.bootique.meta.config;


import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class ConfigObjectMetadataTest {
    @Test
    public void testGetAllSubconfigs() {
        ConfigObjectMetadata o1 = ConfigObjectMetadata.builder().build();
        ConfigObjectMetadata o2 = ConfigObjectMetadata.builder().build();
        ConfigObjectMetadata o3 = ConfigObjectMetadata.builder().build();
        ConfigObjectMetadata o4 = ConfigObjectMetadata.builder().addSubConfig(o1).build();
        ConfigObjectMetadata o5 = ConfigObjectMetadata.builder().addSubConfig(o2).addSubConfig(o3).build();
        ConfigObjectMetadata o6 = ConfigObjectMetadata.builder().addSubConfig(o4).addSubConfig(o5).build();
        List<ConfigMetadataNode> all6 = o6.getAllSubConfigs().collect(Collectors.toList());
        Assert.assertEquals(6, all6.size());
        Assert.assertTrue(all6.contains(o1));
        Assert.assertTrue(all6.contains(o2));
        Assert.assertTrue(all6.contains(o3));
        Assert.assertTrue(all6.contains(o4));
        Assert.assertTrue(all6.contains(o5));
        Assert.assertTrue(all6.contains(o6));
        List<ConfigMetadataNode> all4 = o4.getAllSubConfigs().collect(Collectors.toList());
        Assert.assertEquals(2, all4.size());
        Assert.assertTrue(all4.contains(o1));
        Assert.assertTrue(all4.contains(o4));
        List<ConfigMetadataNode> all1 = o1.getAllSubConfigs().collect(Collectors.toList());
        Assert.assertEquals(1, all1.size());
        Assert.assertTrue(all1.contains(o1));
    }
}

