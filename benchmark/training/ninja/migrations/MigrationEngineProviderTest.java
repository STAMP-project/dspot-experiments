/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.migrations;


import NinjaConstant.MIGRATION_ENGINE_IMPLEMENTATION;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;
import ninja.BaseAndClassicModules;
import ninja.migrations.flyway.MigrationEngineFlyway;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MigrationEngineProviderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void defaultImplementation() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        ninjaProperties.setProperty(MIGRATION_ENGINE_IMPLEMENTATION, null);
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        Provider<MigrationEngine> provider = injector.getProvider(MigrationEngine.class);
        Assert.assertThat(provider.get(), CoreMatchers.instanceOf(MigrationEngineFlyway.class));
    }

    @Test
    public void missingImplementationThrowsExceptionOnUseNotCreate() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        ninjaProperties.setProperty(MIGRATION_ENGINE_IMPLEMENTATION, "not_existing_implementation");
        Injector injector = Guice.createInjector(new BaseAndClassicModules(ninjaProperties));
        Provider<MigrationEngine> provider = injector.getProvider(MigrationEngine.class);
        // this will not work => we expect a runtime exception...
        thrown.expect(RuntimeException.class);
        MigrationEngine migrationEngine = injector.getInstance(MigrationEngine.class);
    }
}

