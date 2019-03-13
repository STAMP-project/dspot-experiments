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
package io.bootique.meta.module;


import io.bootique.meta.config.ConfigMapMetadata;
import io.bootique.meta.config.ConfigMetadataNode;
import io.bootique.meta.config.ConfigObjectMetadata;
import io.bootique.meta.config.ConfigValueMetadata;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class ModuleMetadataTest {
    @Test
    public void testFindConfig_NotFound() {
        ConfigValueMetadata c1 = ConfigValueMetadata.builder("r1_p1").description("r1_p1 desc").type(String.class).build();
        ConfigMapMetadata c2 = ConfigMapMetadata.builder("r2").description("r2 desc").keysType(String.class).valuesType(c1).build();
        ConfigObjectMetadata c3 = ConfigObjectMetadata.builder("r3").description("r3 desc").type(Object.class).addProperty(c2).build();
        ModuleMetadata md = ModuleMetadata.builder("x").addConfig(c3).build();
        Optional<ConfigMetadataNode> missing1 = md.findConfig("r3.rX");
        Assert.assertFalse(missing1.isPresent());
        Optional<ConfigMetadataNode> missing2 = md.findConfig("r2");
        Assert.assertFalse(missing2.isPresent());
    }

    @Test
    public void testFindConfig() {
        ConfigObjectMetadata c1 = ConfigObjectMetadata.builder("r1").description("r1 desc").type(Object.class).addProperty(ConfigValueMetadata.builder("r1_p1").description("r1_p1 desc").type(String.class).build()).addProperty(ConfigValueMetadata.builder("r1_p2").description("r1_p2 desc").type(Boolean.TYPE).build()).build();
        ConfigMapMetadata c2 = ConfigMapMetadata.builder("r2").description("r2 desc").keysType(String.class).valuesType(c1).build();
        ConfigObjectMetadata c3 = ConfigObjectMetadata.builder("r3").description("r3 desc").type(Object.class).addProperty(c2).build();
        ModuleMetadata md = ModuleMetadata.builder("x").addConfig(c3).build();
        Optional<ConfigMetadataNode> r1 = md.findConfig("r3.r2.somekey");
        Assert.assertTrue(r1.isPresent());
        Assert.assertEquals("r1", r1.get().getName());
        Assert.assertEquals("r1 desc", r1.get().getDescription());
        Assert.assertEquals(Object.class, r1.get().getType());
        Optional<ConfigMetadataNode> r1P2 = md.findConfig("r3.r2.somekey.r1_p2");
        Assert.assertTrue(r1P2.isPresent());
        Assert.assertEquals("r1_p2", r1P2.get().getName());
        Assert.assertEquals("r1_p2 desc", r1P2.get().getDescription());
        Assert.assertEquals(Boolean.TYPE, r1P2.get().getType());
    }

    @Test
    public void testFindConfig_Inheritance() {
        ConfigObjectMetadata sub1 = ConfigObjectMetadata.builder().description("sub1 desc").type(Object.class).typeLabel("sub1label").addProperty(ConfigValueMetadata.builder("sub1p1").description("sub1p1 desc").type(String.class).build()).build();
        ConfigObjectMetadata sub2 = ConfigObjectMetadata.builder().description("sub2 desc").type(Object.class).typeLabel("sub2label").addProperty(ConfigValueMetadata.builder("sub2p1").description("sub2p1 desc").type(String.class).build()).build();
        ConfigObjectMetadata super1 = ConfigObjectMetadata.builder("r1").description("super1 desc").type(Object.class).addProperty(ConfigValueMetadata.builder("super1p1").description("super1p1 desc").type(String.class).build()).addSubConfig(sub1).addSubConfig(sub2).build();
        ModuleMetadata md = ModuleMetadata.builder("x").addConfig(super1).build();
        Optional<ConfigMetadataNode> super1P = md.findConfig("r1.super1p1");
        Assert.assertTrue(super1P.isPresent());
        Assert.assertEquals("super1p1", super1P.get().getName());
        Assert.assertEquals("super1p1 desc", super1P.get().getDescription());
        Assert.assertEquals(String.class, super1P.get().getType());
        Optional<ConfigMetadataNode> sub1P = md.findConfig("r1.sub1p1");
        Assert.assertTrue(sub1P.isPresent());
        Assert.assertEquals("sub1p1", sub1P.get().getName());
        Assert.assertEquals("sub1p1 desc", sub1P.get().getDescription());
        Assert.assertEquals(String.class, sub1P.get().getType());
        Optional<ConfigMetadataNode> sub2P = md.findConfig("r1.sub2p1");
        Assert.assertTrue(sub2P.isPresent());
        Assert.assertEquals("sub2p1", sub2P.get().getName());
        Assert.assertEquals("sub2p1 desc", sub2P.get().getDescription());
        Assert.assertEquals(String.class, sub2P.get().getType());
    }
}

