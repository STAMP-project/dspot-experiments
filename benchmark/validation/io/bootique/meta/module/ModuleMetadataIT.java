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


import BQModule.Builder;
import com.google.inject.Binder;
import com.google.inject.Module;
import io.bootique.BQModuleProvider;
import io.bootique.BQRuntime;
import io.bootique.unit.BQInternalTestFactory;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ModuleMetadataIT {
    @Rule
    public BQInternalTestFactory runtimeFactory = new BQInternalTestFactory();

    @Test
    public void testDefault() {
        ModulesMetadata md = runtimeFactory.app().createRuntime().getInstance(ModulesMetadata.class);
        Assert.assertEquals("Expected BQCoreModule + 2 test modules", 3, md.getModules().size());
        Optional<ModuleMetadata> coreMd = md.getModules().stream().filter(( m) -> "BQCoreModule".equals(m.getName())).findFirst();
        Assert.assertTrue(coreMd.isPresent());
        Assert.assertEquals("The core of Bootique runtime.", coreMd.get().getDescription());
    }

    @Test
    public void testCustomModule() {
        ModulesMetadata md = runtimeFactory.app().module(( b) -> {
        }).createRuntime().getInstance(ModulesMetadata.class);
        Assert.assertEquals("Expected BQCoreModule + 2 test modules + custom module", 4, md.getModules().size());
    }

    @Test
    public void testCustomNamedModule() {
        BQRuntime runtime = runtimeFactory.app().module(new BQModuleProvider() {
            @Override
            public Module module() {
                return ( b) -> {
                };
            }

            @Override
            public Builder moduleBuilder() {
                return BQModuleProvider.super.moduleBuilder().name("mymodule");
            }
        }).createRuntime();
        ModulesMetadata md = runtime.getInstance(ModulesMetadata.class);
        Assert.assertEquals("Expected BQCoreModule + 2 test modules + custom module", 4, md.getModules().size());
        Optional<ModuleMetadata> myMd = md.getModules().stream().filter(( m) -> "mymodule".equals(m.getName())).findFirst();
        Assert.assertTrue(myMd.isPresent());
    }

    @Test
    public void testProvider() {
        ModulesMetadata md = runtimeFactory.app().module(new ModuleMetadataIT.M1Provider()).createRuntime().getInstance(ModulesMetadata.class);
        Assert.assertEquals("Expected BQCoreModule + 2 test modules + custom module", 4, md.getModules().size());
        Optional<ModuleMetadata> m1Md = md.getModules().stream().filter(( m) -> "M1Module".equals(m.getName())).findFirst();
        Assert.assertTrue(m1Md.isPresent());
    }

    static class M1Provider implements BQModuleProvider {
        @Override
        public Module module() {
            return new ModuleMetadataIT.M1Module();
        }
    }

    static class M1Module implements Module {
        @Override
        public void configure(Binder binder) {
        }
    }
}

