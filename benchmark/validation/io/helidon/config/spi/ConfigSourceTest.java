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
package io.helidon.config.spi;


import PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;
import io.helidon.common.reactive.Flow;
import io.helidon.config.ConfigParsers;
import io.helidon.config.ConfigSources;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.test.infra.RestoreSystemPropertiesExt;
import java.io.StringReader;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static io.helidon.config.internal.PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;


/**
 * Tests {@link ConfigSource}.
 */
@ExtendWith(RestoreSystemPropertiesExt.class)
public class ConfigSourceTest {
    public static final String TEST_ENV_VAR_NAME = "CONFIG_SOURCE_TEST_PROPERTY";

    public static final String TEST_ENV_VAR_VALUE = "This Is My ENV VARS Value.";

    private static final String TEST_SYS_PROP_NAME = "this_is_my_property-ConfigSourceTest";

    private static final String TEST_SYS_PROP_VALUE = "This Is My SYS PROPS Value.";

    @Test
    public void testFromObjectNodeDescription() {
        ConfigSource configSource = ConfigSources.create(ObjectNode.empty());
        MatcherAssert.assertThat(configSource.description(), Matchers.is("InMemoryConfig[ObjectNode]"));
    }

    @Test
    public void testFromObjectNodeLoad() {
        ConfigSource configSource = ConfigSources.create(ObjectNode.empty());
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.load().get().entrySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testFromReadableDescription() {
        ConfigSource configSource = ConfigSources.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES);
        MatcherAssert.assertThat(configSource.description(), Matchers.is("InMemoryConfig[Readable]"));
    }

    @Test
    public void testFromReadableLoad() {
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(ArgumentMatchers.any())).thenReturn(Optional.of(ConfigParsers.properties()));
        ConfigSource configSource = ConfigSources.create(new StringReader("aaa=bbb"), MEDIA_TYPE_TEXT_JAVA_PROPERTIES);
        configSource.init(context);
        MatcherAssert.assertThat(configSource.load().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
    }

    @ExtendWith(RestoreSystemPropertiesExt.class)
    @Test
    public void testFromTextDescription() {
        ConfigSource configSource = ConfigSources.create("aaa=bbb", MEDIA_TYPE_TEXT_JAVA_PROPERTIES);
        MatcherAssert.assertThat(configSource.description(), Matchers.is("InMemoryConfig[String]"));
    }

    @Test
    public void testFromTextLoad() {
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(ArgumentMatchers.argThat(MEDIA_TYPE_TEXT_JAVA_PROPERTIES::equals))).thenReturn(Optional.of(ConfigParsers.properties()));
        ConfigSource configSource = ConfigSources.create("aaa=bbb", MEDIA_TYPE_TEXT_JAVA_PROPERTIES);
        configSource.init(context);
        MatcherAssert.assertThat(configSource.load().get().get("aaa"), ValueNodeMatcher.valueNode("bbb"));
    }

    @Test
    public void testFromSystemPropertiesDescription() {
        ConfigSource configSource = ConfigSources.systemProperties();
        MatcherAssert.assertThat(configSource.description(), Matchers.is("MapConfig[sys-props]"));
    }

    @Test
    public void testFromSystemProperties() {
        System.setProperty(ConfigSourceTest.TEST_SYS_PROP_NAME, ConfigSourceTest.TEST_SYS_PROP_VALUE);
        ConfigSource configSource = ConfigSources.systemProperties();
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.load().get().get(ConfigSourceTest.TEST_SYS_PROP_NAME), ValueNodeMatcher.valueNode(ConfigSourceTest.TEST_SYS_PROP_VALUE));
    }

    @Test
    public void testFromEnvironmentVariablesDescription() {
        ConfigSource configSource = ConfigSources.environmentVariables();
        MatcherAssert.assertThat(configSource.description(), Matchers.is("MapConfig[env-vars]"));
    }

    @Test
    public void testFromEnvironmentVariables() {
        ConfigSource configSource = ConfigSources.environmentVariables();
        configSource.init(Mockito.mock(ConfigContext.class));
        MatcherAssert.assertThat(configSource.load().get().get(ConfigSourceTest.TEST_ENV_VAR_NAME), ValueNodeMatcher.valueNode(ConfigSourceTest.TEST_ENV_VAR_VALUE));
    }

    @Test
    public void testChangesDefault() throws InterruptedException {
        ConfigSource configSource = Optional::empty;
        CountDownLatch onComplete = new CountDownLatch(1);
        configSource.changes().subscribe(new Flow.Subscriber<Optional<ObjectNode>>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Optional<ObjectNode> item) {
                fail("onNext should not be invoked");
            }

            @Override
            public void onError(Throwable throwable) {
                fail("onError should not be invoked");
            }

            @Override
            public void onComplete() {
                onComplete.countDown();
            }
        });
        MatcherAssert.assertThat(onComplete.await(10, TimeUnit.MILLISECONDS), Is.is(true));
    }
}

