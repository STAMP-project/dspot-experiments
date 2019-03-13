/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.vm;


import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.teavm.vm.spi.After;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.Requires;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;


public class PluginLoaderTest {
    @Test
    public void loadsPlugins() {
        List<String> plugins = order(PluginLoaderTest.A.class, PluginLoaderTest.B.class);
        Assert.assertThat(plugins.size(), CoreMatchers.is(2));
        Assert.assertThat(plugins.get(0), CoreMatchers.is(PluginLoaderTest.B.class.getName()));
        Assert.assertThat(plugins.get(1), CoreMatchers.is(PluginLoaderTest.A.class.getName()));
        plugins = order(PluginLoaderTest.C.class, PluginLoaderTest.D.class);
        Assert.assertThat(plugins.size(), CoreMatchers.is(2));
        Assert.assertThat(plugins.get(0), CoreMatchers.is(PluginLoaderTest.C.class.getName()));
        Assert.assertThat(plugins.get(1), CoreMatchers.is(PluginLoaderTest.D.class.getName()));
    }

    @Test
    public void respectsPluginDependency() {
        List<String> plugins = order(PluginLoaderTest.B.class);
        Assert.assertThat(plugins.size(), CoreMatchers.is(0));
        plugins = order(PluginLoaderTest.A.class);
        Assert.assertThat(plugins.size(), CoreMatchers.is(1));
    }

    @Test(expected = IllegalStateException.class)
    public void detectsCircularDependency() {
        order(PluginLoaderTest.Pre.class, PluginLoaderTest.Head.class, PluginLoaderTest.Tail.class);
    }

    static class A implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    @Before(PluginLoaderTest.A.class)
    @Requires(PluginLoaderTest.A.class)
    static class B implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    static class C implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    @After(PluginLoaderTest.C.class)
    @Requires(PluginLoaderTest.C.class)
    static class D implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    @Before(PluginLoaderTest.Head.class)
    static class Pre implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    @Before(PluginLoaderTest.Tail.class)
    static class Head implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }

    @Before(PluginLoaderTest.Head.class)
    static class Tail implements TeaVMPlugin {
        @Override
        public void install(TeaVMHost host) {
        }
    }
}

