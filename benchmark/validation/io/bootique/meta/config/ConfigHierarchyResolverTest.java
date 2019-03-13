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


import io.bootique.config.PolymorphicConfiguration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class ConfigHierarchyResolverTest {
    @Test
    public void testCreate_Classes() {
        ConfigHierarchyResolver resolver = ConfigHierarchyResolver.create(Arrays.asList(ConfigHierarchyResolverTest.Config4.class, ConfigHierarchyResolverTest.Config3.class, ConfigHierarchyResolverTest.Config2.class, ConfigHierarchyResolverTest.Config1.class));
        Assert.assertEquals(Collections.emptySet(), resolver.directSubclasses(Object.class).collect(Collectors.toSet()));
        Assert.assertEquals(new HashSet(Arrays.asList(ConfigHierarchyResolverTest.Config2.class, ConfigHierarchyResolverTest.Config3.class)), resolver.directSubclasses(ConfigHierarchyResolverTest.Config1.class).collect(Collectors.toSet()));
    }

    @Test
    public void testCreate_BaseInterface() {
        ConfigHierarchyResolver resolver = ConfigHierarchyResolver.create(Arrays.asList(ConfigHierarchyResolverTest.Config5.class, ConfigHierarchyResolverTest.Config6.class, ConfigHierarchyResolverTest.IConfig1.class));
        Assert.assertEquals(new HashSet(Arrays.asList(ConfigHierarchyResolverTest.Config5.class, ConfigHierarchyResolverTest.Config6.class)), resolver.directSubclasses(ConfigHierarchyResolverTest.IConfig1.class).collect(Collectors.toSet()));
    }

    public static interface IConfig1 extends PolymorphicConfiguration {}

    public static class Config1 implements PolymorphicConfiguration {}

    public static class Config2 extends ConfigHierarchyResolverTest.Config1 {}

    public static class Config3 extends ConfigHierarchyResolverTest.Config1 {}

    public static class Config4 extends ConfigHierarchyResolverTest.Config2 {}

    public static class Config5 implements ConfigHierarchyResolverTest.IConfig1 {}

    public static class Config6 implements ConfigHierarchyResolverTest.IConfig1 {}
}

