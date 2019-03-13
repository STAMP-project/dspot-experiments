/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.plugin;


import DataTypes.LONG;
import ESIntegTestCase.ClusterScope;
import ESIntegTestCase.Scope;
import io.crate.metadata.FunctionIdent;
import io.crate.metadata.Functions;
import io.crate.test.CauseMatcher;
import io.crate.types.DataTypes;
import java.util.Collections;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.test.ESIntegTestCase;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


@ClusterScope(scope = Scope.TEST, numDataNodes = 0, numClientNodes = 0)
public class PluginLoaderTest extends ESIntegTestCase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testLoadPlugin() throws Exception {
        String node = PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/simple_plugin");
        PluginsService pluginsService = internalCluster().getInstance(PluginsService.class, node);
        PluginLoaderPlugin corePlugin = getCratePlugin(pluginsService);
        PluginLoader pluginLoader = corePlugin.pluginLoader;
        assertThat(pluginLoader.plugins.size(), Matchers.is(1));
        assertThat(pluginLoader.plugins.get(0).getClass().getCanonicalName(), Matchers.is("io.crate.plugin.ExamplePlugin"));
    }

    @Test
    public void testPluginWithCrateSettings() throws Exception {
        String node = PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/plugin_with_crate_settings");
        PluginsService pluginsService = internalCluster().getInstance(PluginsService.class, node);
        PluginLoaderPlugin corePlugin = getCratePlugin(pluginsService);
        Settings settings = corePlugin.settings;
        assertThat(settings.get("setting.for.crate"), Matchers.is("foo"));
    }

    @Test
    public void testLoadPluginRegisteringScalarFunction() throws Exception {
        String node = PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/simple_plugin_registering_scalar_function");
        PluginsService pluginsService = internalCluster().getInstance(PluginsService.class, node);
        PluginLoaderPlugin corePlugin = getCratePlugin(pluginsService);
        PluginLoader pluginLoader = corePlugin.pluginLoader;
        assertThat(pluginLoader.plugins.size(), Matchers.is(1));
        assertThat(pluginLoader.plugins.get(0).getClass().getCanonicalName(), Matchers.is("io.crate.plugin.ExamplePlugin"));
        Functions functions = internalCluster().getInstance(Functions.class);
        FunctionIdent isEven = new FunctionIdent("is_even", Collections.singletonList(LONG));
        assertThat(functions.getQualified(isEven).info(), Matchers.is(new io.crate.metadata.FunctionInfo(isEven, DataTypes.BOOLEAN)));
        // Also check that the built-in functions are not lost
        FunctionIdent abs = new FunctionIdent("abs", Collections.singletonList(LONG));
        assertThat(functions.getQualified(abs).info(), Matchers.is(new io.crate.metadata.FunctionInfo(abs, DataTypes.LONG)));
    }

    @Test
    public void testLoadPluginWithAlreadyLoadedClass() throws Exception {
        // test that JarHell is used and plugin is not loaded because it contains an already loaded class
        expectedException.expect(CauseMatcher.causeOfCause(RuntimeException.class));
        PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/plugin_with_already_loaded_class");
    }

    @Test
    public void testDuplicates() throws Exception {
        // test that node will die due to jarHell (same plugin jar loaded twice)
        expectedException.expect(CauseMatcher.causeOfCause(RuntimeException.class));
        PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/duplicates");
    }

    @Test
    public void testInvalidPluginEmptyDirectory() throws Exception {
        // test that node will die because of an invalid plugin (in this case, just an empty directory)
        expectedException.expect(CauseMatcher.causeOfCause(RuntimeException.class));
        PluginLoaderTest.startNodeWithPlugins("/io/crate/plugin/invalid");
    }
}

