/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.config;


import BuilderImpl.DEFAULT_CHANGES_EXECUTOR;
import Config.Builder;
import Config.Type.MISSING;
import io.helidon.common.CollectionsHelper;
import io.helidon.common.reactive.Flow;
import io.helidon.config.spi.ConfigSourceTest;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import java.util.concurrent.Executor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests part of {@link BuilderImpl}.
 *
 * @see BuilderImplMappersTest
 * @see BuilderImplParsersTest
 */
@ExtendWith(RestoreSystemPropertiesExt.class)
public class BuilderImplTest {
    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-BuilderImplTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    @Test
    public void testBuildDefault() {
        Config.Builder builder = Config.builder(ConfigSources.empty());
        BuilderImpl spyBuilder = Mockito.spy(((BuilderImpl) (builder)));
        spyBuilder.disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().build();
        // ConfigMapperManager
        // ConfigSource
        // OverrideSource
        // filterProviders
        // cachingEnabled
        // changesExecutor
        // changesMaxBuffer
        // keyResolving
        Mockito.verify(spyBuilder).createProvider(ArgumentMatchers.notNull(), ArgumentMatchers.eq(ConfigSources.empty()), ArgumentMatchers.eq(OverrideSources.empty()), ArgumentMatchers.eq(CollectionsHelper.listOf()), ArgumentMatchers.eq(true), ArgumentMatchers.eq(DEFAULT_CHANGES_EXECUTOR), ArgumentMatchers.eq(Flow.defaultBufferSize()), ArgumentMatchers.eq(true));
    }

    @Test
    public void testBuildCustomChanges() {
        Executor myExecutor = Runnable::run;
        Config.Builder builder = Config.builder().sources(ConfigSources.empty()).disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().changesExecutor(myExecutor).changesMaxBuffer(1);
        BuilderImpl spyBuilder = Mockito.spy(((BuilderImpl) (builder)));
        spyBuilder.build();
        // ConfigMapperManager
        // ConfigSource
        // OverrideSource
        // filterProviders
        // cachingEnabled
        // changesExecutor
        // changesMaxBuffer
        // keyResolving
        Mockito.verify(spyBuilder).createProvider(ArgumentMatchers.notNull(), ArgumentMatchers.eq(ConfigSources.empty()), ArgumentMatchers.eq(OverrideSources.empty()), ArgumentMatchers.eq(CollectionsHelper.listOf()), ArgumentMatchers.eq(true), ArgumentMatchers.eq(myExecutor), ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
    }

    @Test
    public void testBuildDisableKeyResolving() {
        Executor myExecutor = Runnable::run;
        Config.Builder builder = Config.builder().sources(ConfigSources.empty()).disableKeyResolving().disableValueResolving().disableEnvironmentVariablesSource().disableSystemPropertiesSource().disableFilterServices().changesExecutor(myExecutor).changesMaxBuffer(1);
        BuilderImpl spyBuilder = Mockito.spy(((BuilderImpl) (builder)));
        spyBuilder.build();
        // ConfigMapperManager
        // ConfigSource
        // OverrideSource
        // filterProviders
        // cachingEnabled
        // changesExecutor
        // changesMaxBuffer
        // keyResolving
        Mockito.verify(spyBuilder).createProvider(ArgumentMatchers.notNull(), ArgumentMatchers.eq(ConfigSources.empty()), ArgumentMatchers.eq(OverrideSources.empty()), ArgumentMatchers.eq(CollectionsHelper.listOf()), ArgumentMatchers.eq(true), ArgumentMatchers.eq(myExecutor), ArgumentMatchers.eq(1), ArgumentMatchers.eq(false));
    }

    @Test
    public void testBuildWithDefaultStrategy() {
        System.setProperty(BuilderImplTest.TEST_SYS_PROP_NAME, BuilderImplTest.TEST_SYS_PROP_VALUE);
        System.setProperty(ConfigSourceTest.TEST_ENV_VAR_NAME, "This value is not used, but from Env Vars, see pom.xml!");
        Config config = Config.builder().sources(CompositeConfigSourceTest.initBuilder().build()).build();
        MatcherAssert.assertThat(config.get("prop1").asString().get(), Matchers.is("source-1"));
        MatcherAssert.assertThat(config.get("prop2").asString().get(), Matchers.is("source-2"));
        MatcherAssert.assertThat(config.get("prop3").asString().get(), Matchers.is("source-3"));
        MatcherAssert.assertThat(config.get(BuilderImplTest.TEST_SYS_PROP_NAME).asString().get(), Matchers.is(BuilderImplTest.TEST_SYS_PROP_VALUE));
        MatcherAssert.assertThat(config.get(ConfigSourceTest.TEST_ENV_VAR_NAME).asString().get(), Matchers.is(ConfigSourceTest.TEST_ENV_VAR_VALUE));
    }

    @Test
    public void testBuildDisableEnvVars() {
        System.setProperty(BuilderImplTest.TEST_SYS_PROP_NAME, BuilderImplTest.TEST_SYS_PROP_VALUE);
        Config config = Config.builder().sources(CompositeConfigSourceTest.initBuilder().build()).disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.get("prop1").asString().get(), Matchers.is("source-1"));
        MatcherAssert.assertThat(config.get("prop2").asString().get(), Matchers.is("source-2"));
        MatcherAssert.assertThat(config.get("prop3").asString().get(), Matchers.is("source-3"));
        MatcherAssert.assertThat(config.get(BuilderImplTest.TEST_SYS_PROP_NAME).asString().get(), Matchers.is(BuilderImplTest.TEST_SYS_PROP_VALUE));
        MatcherAssert.assertThat(config.get(ConfigSourceTest.TEST_ENV_VAR_NAME).type(), Matchers.is(MISSING));
    }

    @Test
    public void testBuildDisableSysProps() {
        System.setProperty(BuilderImplTest.TEST_SYS_PROP_NAME, BuilderImplTest.TEST_SYS_PROP_VALUE);
        Config config = Config.builder().sources(CompositeConfigSourceTest.initBuilder().build()).disableSystemPropertiesSource().build();
        MatcherAssert.assertThat(config.get("prop1").asString().get(), Matchers.is("source-1"));
        MatcherAssert.assertThat(config.get("prop2").asString().get(), Matchers.is("source-2"));
        MatcherAssert.assertThat(config.get("prop3").asString().get(), Matchers.is("source-3"));
        MatcherAssert.assertThat(config.get(BuilderImplTest.TEST_SYS_PROP_NAME).type(), Matchers.is(MISSING));
        MatcherAssert.assertThat(config.get(ConfigSourceTest.TEST_ENV_VAR_NAME).asString().get(), Matchers.is(ConfigSourceTest.TEST_ENV_VAR_VALUE));
    }

    @Test
    public void testBuildDisableSysPropsAndEnvVars() {
        System.setProperty(BuilderImplTest.TEST_SYS_PROP_NAME, BuilderImplTest.TEST_SYS_PROP_VALUE);
        Config config = Config.builder().sources(CompositeConfigSourceTest.initBuilder().build()).disableSystemPropertiesSource().disableEnvironmentVariablesSource().build();
        MatcherAssert.assertThat(config.get("prop1").asString().get(), Matchers.is("source-1"));
        MatcherAssert.assertThat(config.get("prop2").asString().get(), Matchers.is("source-2"));
        MatcherAssert.assertThat(config.get("prop3").asString().get(), Matchers.is("source-3"));
        MatcherAssert.assertThat(config.get(BuilderImplTest.TEST_SYS_PROP_NAME).type(), Matchers.is(MISSING));
        MatcherAssert.assertThat(config.get(ConfigSourceTest.TEST_ENV_VAR_NAME).type(), Matchers.is(MISSING));
    }
}

