/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.runtime;


import bt.TestUtil;
import bt.peer.lan.ILocalServiceDiscoveryService;
import bt.service.ApplicationService;
import bt.service.Version;
import com.google.inject.Inject;
import com.google.inject.Module;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


public class BtRuntimeBuilderTest {
    public interface I1 {}

    public static class S1 implements BtRuntimeBuilderTest.I1 {}

    @Test
    public void testRuntimeBuilder_CustomModule_NewBinding() {
        Module m = ( binder) -> binder.bind(.class).to(.class);
        BtRuntime runtime = new BtRuntimeBuilder().module(m).build();
        Assert.assertNotNull(runtime.service(BtRuntimeBuilderTest.I1.class));
    }

    @Test
    public void testRuntimeBuilder_CustomModule_OverrideDefaultBinding() {
        Module m = ( binder) -> binder.bind(.class).to(.class);
        BtRuntime runtime = new BtRuntimeBuilder().module(m).build();
        ApplicationService service = runtime.service(ApplicationService.class);
        Assert.assertNotNull(service);
        Assert.assertSame(OverridenApplicationService.VERSION, service.getVersion());
    }

    @Test
    public void testRuntimeBuilder_ModuleAutoloading() {
        BtRuntime runtime = new BtRuntimeBuilder().autoLoadModules().build();
        ApplicationService service = runtime.service(ApplicationService.class);
        Assert.assertNotNull(service);
        Assert.assertSame(OverridenApplicationService.VERSION, service.getVersion());
    }

    public static class AnotherOverridenApplicationService implements ApplicationService {
        // we test if objects are the same by identity, but just in case we switch to equals() let's use different values
        private static final Version VERSION = new Version((-2), (-2), false);

        @Override
        public Version getVersion() {
            return BtRuntimeBuilderTest.AnotherOverridenApplicationService.VERSION;
        }
    }

    @Test
    public void testRuntimeBuilder_CustomModule_OverrideAutoloadedBinding() {
        Module m = ( binder) -> binder.bind(.class).to(.class);
        BtRuntime runtime = new BtRuntimeBuilder().module(m).autoLoadModules().build();
        ApplicationService service = runtime.service(ApplicationService.class);
        Assert.assertNotNull(service);
        Assert.assertSame(BtRuntimeBuilderTest.AnotherOverridenApplicationService.VERSION, service.getVersion());
    }

    public interface IConfigHolder {
        Config getConfig();
    }

    public static class ConfigHolder implements BtRuntimeBuilderTest.IConfigHolder {
        @Inject
        public Config config;

        @Override
        public Config getConfig() {
            return config;
        }
    }

    @Test
    public void testRuntimeBuilder_OverrideStandardModule() {
        Module m = ( binder) -> binder.bind(.class).to(.class);
        Config customConfig = new Config();
        BtRuntime runtime = new BtRuntimeBuilder().module(m).module(new bt.module.ServiceModule(customConfig)).autoLoadModules().build();
        Assert.assertSame(customConfig, runtime.getConfig());
        Assert.assertSame(customConfig, runtime.service(BtRuntimeBuilderTest.IConfigHolder.class).getConfig());
    }

    @Test
    public void testRuntimeBuilder_EnableStandardExtensions() {
        BtRuntime runtime = new BtRuntimeBuilder().build();
        Assert.assertNotNull(runtime.service(ILocalServiceDiscoveryService.class));
    }

    @Test
    public void testRuntimeBuilder_DisableStandardExtensions() {
        BtRuntime runtime = new BtRuntimeBuilder().disableStandardExtensions().build();
        TestUtil.assertExceptionWithMessage(( it) -> runtime.service(ILocalServiceDiscoveryService.class), "No implementation for bt.peer.lan.ILocalServiceDiscoveryService was bound");
    }
}

