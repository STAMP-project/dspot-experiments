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
package io.bootique.help.config;


import io.bootique.Bootique;
import io.bootique.help.ConsoleAppender;
import io.bootique.meta.config.ConfigListMetadata;
import io.bootique.meta.config.ConfigMapMetadata;
import io.bootique.meta.config.ConfigObjectMetadata;
import io.bootique.meta.config.ConfigValueMetadata;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigSectionGeneratorTest {
    @Test
    public void testVisitObjectConfig() {
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).addProperty(ConfigValueMetadata.builder("p2").type(Integer.TYPE).description("Designates an integer value").build()).addProperty(ConfigValueMetadata.builder("p1").type(String.class).build()).addProperty(ConfigValueMetadata.builder("p0").type(Boolean.class).build()).addProperty(ConfigValueMetadata.builder("p3").type(Bootique.class).build()).build();
        ConfigSectionGeneratorTest.assertLines(m1Config, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      p0: <true|false>", "      p1: <string>", "      # Designates an integer value", "      p2: <int>", "      # Resolved as 'io.bootique.Bootique'.", "      p3: <value>");
    }

    @Test
    public void testVisitListOfValues() {
        ConfigValueMetadata listMd1 = ConfigValueMetadata.builder().type(Integer.TYPE).build();
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).addProperty(ConfigListMetadata.builder("p1").elementType(listMd1).build()).build();
        ConfigSectionGeneratorTest.assertLines(m1Config, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      p1:", "            #", "            # Resolved as 'List'.", "            #", "            - <int>");
    }

    @Test
    public void testVisitListOfObjects() {
        ConfigObjectMetadata listMd2 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.ConfigRoot2.class).addProperty(ConfigValueMetadata.builder("p4").type(String.class).build()).addProperty(ConfigValueMetadata.builder("p3").type(Boolean.TYPE).build()).build();
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).addProperty(ConfigListMetadata.builder("p2").elementType(listMd2).description("I am a list").build()).build();
        ConfigSectionGeneratorTest.assertLines(m1Config, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      p2:", "            #", "            # I am a list", "            # Resolved as 'List'.", "            #", "            -", "                  #", "                  # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot2'.", "                  #", "", "                  p3: <true|false>", "                  p4: <string>");
    }

    @Test
    public void testVisitMapOfValues() throws NoSuchFieldException {
        Type genericMapType = ConfigSectionGeneratorTest.ConfigRoot2.class.getField("map").getGenericType();
        ConfigValueMetadata mapValueMd = ConfigValueMetadata.builder().type(String.class).build();
        ConfigMapMetadata mapMd = ConfigMapMetadata.builder("p1").type(genericMapType).keysType(Integer.class).valuesType(mapValueMd).build();
        ConfigObjectMetadata rootMd = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).addProperty(mapMd).build();
        ConfigSectionGeneratorTest.assertLines(rootMd, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      p1:", "            #", "            # Resolved as 'Map<int, String>'.", "            #", "            <int>: <string>");
    }

    @Test
    public void testVisitObjectWithMapOfObjects() {
        ConfigObjectMetadata mapMd = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.ConfigRoot2.class).addProperty(ConfigValueMetadata.builder("p4").type(String.class).build()).addProperty(ConfigValueMetadata.builder("p3").type(Boolean.TYPE).build()).build();
        ConfigObjectMetadata rootMd = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).addProperty(ConfigMapMetadata.builder("p1").keysType(String.class).valuesType(mapMd).build()).build();
        ConfigSectionGeneratorTest.assertLines(rootMd, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      p1:", "            #", "            # Resolved as 'Map'.", "            #", "            <string>:", "                  #", "                  # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot2'.", "                  #", "", "                  p3: <true|false>", "                  p4: <string>");
    }

    @Test
    public void testVisitMapOfMapsOfObjects() {
        ConfigObjectMetadata objectMd = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.ConfigRoot2.class).addProperty(ConfigValueMetadata.builder("p4").type(String.class).build()).addProperty(ConfigValueMetadata.builder("p3").type(Boolean.TYPE).build()).build();
        ConfigMapMetadata subMapMd = ConfigMapMetadata.builder().description("Submap description").keysType(String.class).valuesType(objectMd).build();
        ConfigMapMetadata rootMapMd = ConfigMapMetadata.builder("root").description("Root map").keysType(String.class).valuesType(subMapMd).build();
        ConfigSectionGeneratorTest.assertLines(rootMapMd, "root:", "      #", "      # Root map", "      # Resolved as 'Map'.", "      #", "      <string>:", "            #", "            # Submap description", "            # Resolved as 'Map'.", "            #", "            <string>:", "                  #", "                  # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot2'.", "                  #", "", "                  p3: <true|false>", "                  p4: <string>");
    }

    @Test
    public void testVisitMapOfListsOfObjects() {
        ConfigObjectMetadata objectMd = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.ConfigRoot2.class).addProperty(ConfigValueMetadata.builder("p4").type(String.class).build()).addProperty(ConfigValueMetadata.builder("p3").type(Boolean.TYPE).build()).build();
        ConfigListMetadata subListMd = ConfigListMetadata.builder().description("Sublist description").elementType(objectMd).build();
        ConfigMapMetadata rootMapMd = ConfigMapMetadata.builder("root").description("This is a map of lists.").keysType(String.class).valuesType(subListMd).build();
        ConfigSectionGeneratorTest.assertLines(rootMapMd, "root:", "      #", "      # This is a map of lists.", "      # Resolved as 'Map'.", "      #", "      <string>:", "            #", "            # Sublist description", "            # Resolved as 'List'.", "            #", "            -", "                  #", "                  # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot2'.", "                  #", "", "                  p3: <true|false>", "                  p4: <string>");
    }

    @Test
    public void testVisitObjectConfig_Inheritance() {
        ConfigObjectMetadata sub1 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.Config3.class).typeLabel("c3").description("Subtype desc").addProperty(ConfigValueMetadata.builder("p0").type(Boolean.class).build()).addProperty(ConfigValueMetadata.builder("p1").type(String.class).build()).build();
        ConfigObjectMetadata sub2 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.Config4.class).typeLabel("c4").addProperty(ConfigValueMetadata.builder("p2").type(Integer.TYPE).description("Designates an integer value").build()).addProperty(ConfigValueMetadata.builder("p3").type(Bootique.class).build()).build();
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder("m1root").description("Root config of M1").type(ConfigSectionGeneratorTest.ConfigRoot1.class).abstractType(true).addSubConfig(sub1).addSubConfig(sub2).build();
        ConfigSectionGeneratorTest.assertLines(m1Config, "m1root:", "      #", "      # Root config of M1", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "      #", "", "      #", "      # Type option: c3", "      # Subtype desc", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$Config3'.", "      #", "", "      type: 'c3'", "      p0: <true|false>", "      p1: <string>", "", "      #", "      # Type option: c4", "      # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$Config4'.", "      #", "", "      type: 'c4'", "      # Designates an integer value", "      p2: <int>", "      # Resolved as 'io.bootique.Bootique'.", "      p3: <value>");
    }

    @Test
    public void testVisitMapConfig_ValueInheritance() throws NoSuchFieldException {
        Type genericMapType = ConfigSectionGeneratorTest.ConfigRoot2.class.getField("mapOfRoot1").getGenericType();
        ConfigObjectMetadata sub1 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.Config3.class).typeLabel("c3").addProperty(ConfigValueMetadata.builder("p0").type(Boolean.class).build()).addProperty(ConfigValueMetadata.builder("p1").type(String.class).build()).build();
        ConfigObjectMetadata sub2 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.Config4.class).typeLabel("c4").addProperty(ConfigValueMetadata.builder("p2").type(Integer.TYPE).description("Designates an integer value").build()).addProperty(ConfigValueMetadata.builder("p3").type(Bootique.class).build()).build();
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder().description("One config").type(ConfigSectionGeneratorTest.ConfigRoot1.class).abstractType(true).addSubConfig(sub1).addSubConfig(sub2).build();
        ConfigMapMetadata mapMd = ConfigMapMetadata.builder("root").description("Map of things").type(genericMapType).keysType(String.class).valuesType(m1Config).build();
        ConfigSectionGeneratorTest.assertLines(mapMd, "root:", "      #", "      # Map of things", "      # Resolved as 'Map<String, io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1>'.", "      #", "      <string>:", "            #", "            # One config", "            # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "            #", "", "            #", "            # Type option: c3", "            # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$Config3'.", "            #", "", "            type: 'c3'", "            p0: <true|false>", "            p1: <string>", "", "            #", "            # Type option: c4", "            # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$Config4'.", "            #", "", "            type: 'c4'", "            # Designates an integer value", "            p2: <int>", "            # Resolved as 'io.bootique.Bootique'.", "            p3: <value>");
    }

    @Test
    public void testVisitMapConfig_ValueInheritance_AbstractProps() throws NoSuchFieldException {
        Type genericMapType = ConfigSectionGeneratorTest.ConfigRoot2.class.getField("mapOfRoot1").getGenericType();
        ConfigObjectMetadata sub1 = ConfigObjectMetadata.builder().type(ConfigSectionGeneratorTest.Config3.class).typeLabel("c3").addProperty(ConfigValueMetadata.builder("p0").type(Boolean.class).build()).addProperty(ConfigValueMetadata.builder("p1").type(String.class).build()).build();
        ConfigObjectMetadata m1Config = ConfigObjectMetadata.builder().description("One config").type(ConfigSectionGeneratorTest.ConfigRoot1.class).abstractType(true).addProperty(ConfigValueMetadata.builder("shouldNotBePrinted").type(Bootique.class).build()).addSubConfig(sub1).build();
        ConfigMapMetadata mapMd = ConfigMapMetadata.builder("root").description("Map of things").type(genericMapType).keysType(String.class).valuesType(m1Config).build();
        ConfigSectionGeneratorTest.assertLines(mapMd, "root:", "      #", "      # Map of things", "      # Resolved as 'Map<String, io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1>'.", "      #", "      <string>:", "            #", "            # One config", "            # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$ConfigRoot1'.", "            #", "", "            #", "            # Type option: c3", "            # Resolved as 'io.bootique.help.config.ConfigSectionGeneratorTest$Config3'.", "            #", "", "            type: 'c3'", "            p0: <true|false>", "            p1: <string>");
    }

    @Test
    public void testTypeLabel() throws NoSuchFieldException {
        ConfigSectionGenerator generator = new ConfigSectionGenerator(Mockito.mock(ConsoleAppender.class));
        Assert.assertEquals("int", generator.typeLabel(Integer.class));
        Assert.assertEquals("int", generator.typeLabel(Integer.TYPE));
        Assert.assertEquals("boolean", generator.typeLabel(Boolean.class));
        Assert.assertEquals("boolean", generator.typeLabel(Boolean.TYPE));
        Assert.assertEquals("String", generator.typeLabel(String.class));
        Assert.assertEquals("io.bootique.Bootique", generator.typeLabel(Bootique.class));
        Assert.assertEquals("Map", generator.typeLabel(HashMap.class));
        Assert.assertEquals("List", generator.typeLabel(ArrayList.class));
        Type genericMapType = ConfigSectionGeneratorTest.ConfigRoot2.class.getField("map").getGenericType();
        Assert.assertEquals("Map<int, String>", generator.typeLabel(genericMapType));
        Type genericListType = ConfigSectionGeneratorTest.ConfigRoot2.class.getField("list").getGenericType();
        Assert.assertEquals("List<String>", generator.typeLabel(genericListType));
    }

    public static class ConfigRoot1 {}

    public static class ConfigRoot2 {
        public Map<Integer, String> map;

        public Map<String, ConfigSectionGeneratorTest.ConfigRoot1> mapOfRoot1;

        public List<String> list;
    }

    public static class Config3 {}

    public static class Config4 {}
}

