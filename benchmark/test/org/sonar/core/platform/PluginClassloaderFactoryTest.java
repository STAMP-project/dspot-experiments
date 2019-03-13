/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.platform;


import com.sonarsource.plugins.license.api.FooBar;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;


public class PluginClassloaderFactoryTest {
    static final String BASE_PLUGIN_CLASSNAME = "org.sonar.plugins.base.BasePlugin";

    static final String DEPENDENT_PLUGIN_CLASSNAME = "org.sonar.plugins.dependent.DependentPlugin";

    static final String BASE_PLUGIN_KEY = "base";

    static final String DEPENDENT_PLUGIN_KEY = "dependent";

    PluginClassloaderFactory factory = new PluginClassloaderFactory();

    @Test
    public void create_isolated_classloader() {
        PluginClassLoaderDef def = PluginClassloaderFactoryTest.basePluginDef();
        Map<PluginClassLoaderDef, ClassLoader> map = factory.create(Arrays.asList(def));
        assertThat(map).containsOnlyKeys(def);
        ClassLoader classLoader = map.get(def);
        // plugin can access to API classes, and of course to its own classes !
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, RulesDefinition.class.getCanonicalName())).isTrue();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, PluginClassloaderFactoryTest.BASE_PLUGIN_CLASSNAME)).isTrue();
        // plugin can not access to core classes
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, PluginClassloaderFactory.class.getCanonicalName())).isFalse();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, Test.class.getCanonicalName())).isFalse();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, StringUtils.class.getCanonicalName())).isFalse();
    }

    @Test
    public void classloader_exports_resources_to_other_classloaders() {
        PluginClassLoaderDef baseDef = PluginClassloaderFactoryTest.basePluginDef();
        PluginClassLoaderDef dependentDef = PluginClassloaderFactoryTest.dependentPluginDef();
        Map<PluginClassLoaderDef, ClassLoader> map = factory.create(Arrays.asList(baseDef, dependentDef));
        ClassLoader baseClassloader = map.get(baseDef);
        ClassLoader dependentClassloader = map.get(dependentDef);
        // base-plugin exports its API package to other plugins
        assertThat(PluginClassloaderFactoryTest.canLoadClass(dependentClassloader, "org.sonar.plugins.base.api.BaseApi")).isTrue();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(dependentClassloader, PluginClassloaderFactoryTest.BASE_PLUGIN_CLASSNAME)).isFalse();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(dependentClassloader, PluginClassloaderFactoryTest.DEPENDENT_PLUGIN_CLASSNAME)).isTrue();
        // dependent-plugin does not export its classes
        assertThat(PluginClassloaderFactoryTest.canLoadClass(baseClassloader, PluginClassloaderFactoryTest.DEPENDENT_PLUGIN_CLASSNAME)).isFalse();
        assertThat(PluginClassloaderFactoryTest.canLoadClass(baseClassloader, PluginClassloaderFactoryTest.BASE_PLUGIN_CLASSNAME)).isTrue();
    }

    @Test
    public void classloader_exposes_license_api_from_main_classloader() {
        PluginClassLoaderDef def = PluginClassloaderFactoryTest.basePluginDef();
        Map<PluginClassLoaderDef, ClassLoader> map = factory.create(Arrays.asList(def));
        assertThat(map).containsOnlyKeys(def);
        ClassLoader classLoader = map.get(def);
        assertThat(PluginClassloaderFactoryTest.canLoadClass(classLoader, FooBar.class.getCanonicalName())).isTrue();
    }
}

