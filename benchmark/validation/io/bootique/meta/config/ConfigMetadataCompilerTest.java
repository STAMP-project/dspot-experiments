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


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import io.bootique.config.PolymorphicConfiguration;
import io.bootique.type.TypeRef;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


public class ConfigMetadataCompilerTest {
    @Test
    public void testCompile() {
        ConfigObjectMetadata md = ((ConfigObjectMetadata) (createCompiler().compile("prefix", ConfigMetadataCompilerTest.Config1.class)));
        Assert.assertNotNull(md);
        Assert.assertEquals("prefix", md.getName());
        Assert.assertEquals("Describes Config1", md.getDescription());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config1.class, md.getType());
        Assert.assertEquals(10, md.getProperties().size());
        Map<String, ConfigMetadataNode> propMap = md.getProperties().stream().collect(Collectors.toMap(MetadataNode::getName, Function.identity()));
        ConfigValueMetadata config1 = ((ConfigValueMetadata) (propMap.get("config1")));
        Assert.assertEquals(String.class, config1.getType());
        Assert.assertEquals("constructor with params", config1.getDescription());
        ConfigValueMetadata p1 = ((ConfigValueMetadata) (propMap.get("p1")));
        Assert.assertEquals(Integer.TYPE, p1.getType());
        Assert.assertNull(p1.getDescription());
        ConfigValueMetadata p2 = ((ConfigValueMetadata) (propMap.get("p2")));
        Assert.assertEquals(BigDecimal.class, p2.getType());
        Assert.assertEquals("description of p2", p2.getDescription());
        ConfigMetadataNode p3 = propMap.get("p3");
        Assert.assertEquals(ConfigMetadataCompilerTest.Config2.class, p3.getType());
        ConfigListMetadata p4 = ((ConfigListMetadata) (propMap.get("p4")));
        Assert.assertEquals("java.util.List<java.lang.String>", p4.getType().getTypeName());
        Assert.assertEquals(String.class, p4.getElementType().getType());
        Assert.assertEquals(ConfigValueMetadata.class, p4.getElementType().getClass());
        ConfigListMetadata p5 = ((ConfigListMetadata) (propMap.get("p5")));
        Assert.assertEquals("java.util.List<io.bootique.meta.config.ConfigMetadataCompilerTest$Config2>", p5.getType().getTypeName());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config2.class, p5.getElementType().getType());
        ConfigMapMetadata p6 = ((ConfigMapMetadata) (propMap.get("p6")));
        Assert.assertEquals("java.util.Map<java.lang.String, java.math.BigDecimal>", p6.getType().getTypeName());
        Assert.assertEquals(String.class, p6.getKeysType());
        Assert.assertEquals(BigDecimal.class, p6.getValuesType().getType());
        Assert.assertEquals(ConfigValueMetadata.class, p6.getValuesType().getClass());
        ConfigMapMetadata p7 = ((ConfigMapMetadata) (propMap.get("p7")));
        Assert.assertEquals("java.util.Map<java.lang.Integer, io.bootique.meta.config.ConfigMetadataCompilerTest$Config2>", p7.getType().getTypeName());
        Assert.assertEquals(Integer.class, p7.getKeysType());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config2.class, p7.getValuesType().getType());
    }

    @Test
    public void testCompile_Cycle() {
        ConfigObjectMetadata md = ((ConfigObjectMetadata) (createCompiler().compile("prefix", ConfigMetadataCompilerTest.Config3.class)));
        Assert.assertNotNull(md);
        Assert.assertEquals("prefix", md.getName());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config3.class, md.getType());
        Assert.assertEquals(1, md.getProperties().size());
        ConfigListMetadata p1 = ((ConfigListMetadata) (md.getProperties().iterator().next()));
        Assert.assertEquals("java.util.Collection<io.bootique.meta.config.ConfigMetadataCompilerTest$Config4>", p1.getType().toString());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config4.class, p1.getElementType().getType());
    }

    @Test
    public void testCompile_Inheritance() {
        ConfigObjectMetadata c5 = ((ConfigObjectMetadata) (createCompiler(( t) -> {
            if (ConfigMetadataCompilerTest.Config5.class.equals(t)) {
                return Stream.of(ConfigMetadataCompilerTest.Config6.class, ConfigMetadataCompilerTest.Config7.class);
            }
            if (ConfigMetadataCompilerTest.Config6.class.equals(t)) {
                return Stream.of(ConfigMetadataCompilerTest.Config8.class);
            }
            return Stream.empty();
        }).compile("prefix", ConfigMetadataCompilerTest.Config5.class)));
        Assert.assertNotNull(c5);
        Assert.assertEquals("prefix", c5.getName());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config5.class, c5.getType());
        Assert.assertTrue(c5.isAbstractType());
        Map<Type, ConfigMetadataNode> sc5 = c5.getSubConfigs().stream().collect(Collectors.toMap(ConfigMetadataNode::getType, Function.identity()));
        Assert.assertEquals(2, sc5.size());
        ConfigObjectMetadata c6 = ((ConfigObjectMetadata) (sc5.get(ConfigMetadataCompilerTest.Config6.class)));
        Assert.assertNotNull(c6);
        Assert.assertEquals("c6", c6.getTypeLabel());
        Assert.assertFalse(c6.isAbstractType());
        Map<Type, ConfigMetadataNode> sc6 = c6.getSubConfigs().stream().collect(Collectors.toMap(ConfigMetadataNode::getType, Function.identity()));
        Assert.assertEquals(1, sc6.size());
        ConfigObjectMetadata c8 = ((ConfigObjectMetadata) (sc6.get(ConfigMetadataCompilerTest.Config8.class)));
        Assert.assertNotNull(c8);
        Assert.assertEquals("c8", c8.getTypeLabel());
        Assert.assertFalse(c8.isAbstractType());
        Assert.assertEquals(0, c8.getSubConfigs().size());
        ConfigObjectMetadata c7 = ((ConfigObjectMetadata) (sc5.get(ConfigMetadataCompilerTest.Config7.class)));
        Assert.assertNotNull(c7);
        Assert.assertEquals("c7", c7.getTypeLabel());
        Assert.assertFalse(c7.isAbstractType());
        Assert.assertEquals(0, c7.getSubConfigs().size());
    }

    @Test
    public void testCompile_Map() {
        TypeRef<Map<String, BigDecimal>> tr1 = new TypeRef<Map<String, BigDecimal>>() {};
        ConfigMapMetadata md = ((ConfigMapMetadata) (createCompiler(( t) -> Stream.empty()).compile("prefix", tr1.getType())));
        Assert.assertNotNull(md);
        Assert.assertEquals("prefix", md.getName());
        Assert.assertEquals(tr1.getType(), md.getType());
        Assert.assertEquals(String.class, md.getKeysType());
        Assert.assertEquals(BigDecimal.class, md.getValuesType().getType());
        Assert.assertEquals(ConfigValueMetadata.class, md.getValuesType().getClass());
    }

    @Test
    public void testCompile_Map_Inheritance() {
        TypeRef<Map<String, ConfigMetadataCompilerTest.Config5>> tr1 = new TypeRef<Map<String, ConfigMetadataCompilerTest.Config5>>() {};
        ConfigMapMetadata md = ((ConfigMapMetadata) (createCompiler(( t) -> {
            if (ConfigMetadataCompilerTest.Config5.class.equals(t)) {
                return Stream.of(ConfigMetadataCompilerTest.Config6.class, ConfigMetadataCompilerTest.Config7.class);
            }
            if (ConfigMetadataCompilerTest.Config6.class.equals(t)) {
                return Stream.of(ConfigMetadataCompilerTest.Config8.class);
            }
            return Stream.empty();
        }).compile("prefix", tr1.getType())));
        Assert.assertNotNull(md);
        Assert.assertEquals("prefix", md.getName());
        Assert.assertEquals(tr1.getType(), md.getType());
        Assert.assertEquals(String.class, md.getKeysType());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config5.class, md.getValuesType().getType());
        Assert.assertEquals(ConfigObjectMetadata.class, md.getValuesType().getClass());
        ConfigObjectMetadata c5 = ((ConfigObjectMetadata) (md.getValuesType()));
        Assert.assertNotNull(c5);
        Assert.assertEquals(ConfigMetadataCompilerTest.Config5.class, c5.getType());
        Assert.assertTrue(c5.isAbstractType());
        Map<Type, ConfigMetadataNode> sc5 = c5.getSubConfigs().stream().collect(Collectors.toMap(ConfigMetadataNode::getType, Function.identity()));
        Assert.assertEquals(2, sc5.size());
        ConfigObjectMetadata c6 = ((ConfigObjectMetadata) (sc5.get(ConfigMetadataCompilerTest.Config6.class)));
        Assert.assertNotNull(c6);
        Assert.assertEquals("c6", c6.getTypeLabel());
        Assert.assertFalse(c6.isAbstractType());
        Map<Type, ConfigMetadataNode> sc6 = c6.getSubConfigs().stream().collect(Collectors.toMap(ConfigMetadataNode::getType, Function.identity()));
        Assert.assertEquals(1, sc6.size());
        ConfigObjectMetadata c8 = ((ConfigObjectMetadata) (sc6.get(ConfigMetadataCompilerTest.Config8.class)));
        Assert.assertNotNull(c8);
        Assert.assertEquals("c8", c8.getTypeLabel());
        Assert.assertFalse(c8.isAbstractType());
        Assert.assertEquals(0, c8.getSubConfigs().size());
        ConfigObjectMetadata c7 = ((ConfigObjectMetadata) (sc5.get(ConfigMetadataCompilerTest.Config7.class)));
        Assert.assertNotNull(c7);
        Assert.assertEquals("c7", c7.getTypeLabel());
        Assert.assertFalse(c7.isAbstractType());
        Assert.assertEquals(0, c7.getSubConfigs().size());
    }

    @Test
    public void testCompile_InheritanceFromInterface() {
        ConfigObjectMetadata c9 = ((ConfigObjectMetadata) (createCompiler(( t) -> {
            if (ConfigMetadataCompilerTest.Config9.class.equals(t)) {
                return Stream.of(ConfigMetadataCompilerTest.Config10.class, ConfigMetadataCompilerTest.Config11.class);
            }
            return Stream.empty();
        }).compile("prefix", ConfigMetadataCompilerTest.Config9.class)));
        Assert.assertNotNull(c9);
        Assert.assertEquals("prefix", c9.getName());
        Assert.assertEquals(ConfigMetadataCompilerTest.Config9.class, c9.getType());
        Assert.assertTrue(c9.isAbstractType());
        Map<Type, ConfigMetadataNode> sc9 = c9.getSubConfigs().stream().collect(Collectors.toMap(ConfigMetadataNode::getType, Function.identity()));
        Assert.assertEquals(2, sc9.size());
        ConfigObjectMetadata c10 = ((ConfigObjectMetadata) (sc9.get(ConfigMetadataCompilerTest.Config10.class)));
        Assert.assertNotNull(c10);
        Assert.assertEquals("c10", c10.getTypeLabel());
        Assert.assertFalse(c10.isAbstractType());
        Assert.assertEquals(0, c10.getSubConfigs().size());
        ConfigObjectMetadata c11 = ((ConfigObjectMetadata) (sc9.get(ConfigMetadataCompilerTest.Config11.class)));
        Assert.assertNotNull(c11);
        Assert.assertEquals("c11", c11.getTypeLabel());
        Assert.assertFalse(c11.isAbstractType());
        Assert.assertEquals(0, c11.getSubConfigs().size());
    }

    @BQConfig
    @JsonTypeInfo(use = NAME, property = "type")
    public static interface Config9 extends PolymorphicConfiguration {}

    @BQConfig("Describes Config1")
    public static class Config1 {
        @BQConfigProperty("constructor with params")
        public Config1(String s) {
        }

        @BQConfigProperty
        public void setP1(int v) {
        }

        @BQConfigProperty("description of p2")
        public void setP2(BigDecimal v) {
        }

        @BQConfigProperty
        public void setP3(ConfigMetadataCompilerTest.Config2 v) {
        }

        @BQConfigProperty
        public void setP4(List<String> v) {
        }

        @BQConfigProperty
        public void setP5(List<ConfigMetadataCompilerTest.Config2> v) {
        }

        @BQConfigProperty
        public void setP6(Map<String, BigDecimal> v) {
        }

        @BQConfigProperty
        public void setP7(Map<Integer, ConfigMetadataCompilerTest.Config2> v) {
        }

        @BQConfigProperty
        public ConfigMetadataCompilerTest.Config1 setP8(String v) {
            return this;
        }

        @BQConfigProperty
        public String setP9(String v) {
            return v;
        }

        public void setNonP(String v) {
        }
    }

    @BQConfig("Describes Config2")
    public static class Config2 {
        @BQConfigProperty
        public void setP1(byte v) {
        }
    }

    @BQConfig
    public static class Config3 {
        @BQConfigProperty
        public void setC4s(Collection<ConfigMetadataCompilerTest.Config4> v) {
        }
    }

    @BQConfig
    public static class Config4 {
        @BQConfigProperty
        public void setC3(ConfigMetadataCompilerTest.Config3 v) {
        }
    }

    @BQConfig
    @JsonTypeInfo(use = NAME, property = "type")
    public abstract static class Config5 implements PolymorphicConfiguration {
        @BQConfigProperty
        public void setP1(long v) {
        }
    }

    @BQConfig
    @JsonTypeName("c6")
    public static class Config6 extends ConfigMetadataCompilerTest.Config5 {
        @BQConfigProperty
        public void setP2(String v) {
        }
    }

    @BQConfig
    @JsonTypeName("c7")
    public static class Config7 extends ConfigMetadataCompilerTest.Config5 {
        @BQConfigProperty
        public void setP3(String v) {
        }
    }

    @BQConfig
    @JsonTypeName("c8")
    public static class Config8 extends ConfigMetadataCompilerTest.Config6 {
        @BQConfigProperty
        public void setP4(BigDecimal v) {
        }
    }

    @BQConfig
    @JsonTypeName("c10")
    public static class Config10 implements ConfigMetadataCompilerTest.Config9 {
        @BQConfigProperty
        public void setP2(String v) {
        }
    }

    @BQConfig
    @JsonTypeName("c11")
    public static class Config11 implements ConfigMetadataCompilerTest.Config9 {
        @BQConfigProperty
        public void setP3(String v) {
        }
    }
}

