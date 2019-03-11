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


import io.helidon.common.CollectionsHelper;
import io.helidon.config.spi.ConfigContext;
import java.util.Optional;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests {@link UseFirstAvailableConfigSource}.
 */
public class UseFirstAvailableConfigSourceTest {
    @Test
    public void testDescriptionAvailable() {
        UseFirstAvailableConfigSource configSource = new UseFirstAvailableConfigSource(ConfigSources.classpath("application.yaml").optional().build(), ConfigSources.classpath("io/helidon/config/application.properties").optional().build(), ConfigSources.classpath("io/helidon/config/application.conf").build(), ConfigSources.classpath("io/helidon/config/application.json").optional().build());
        MatcherAssert.assertThat(configSource.description(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("ClasspathConfig[application.yaml]?->ClasspathConfig[", "io/helidon/config/application.properties]?->ClasspathConfig[", "io/helidon/config/application.conf]->ClasspathConfig[", "io/helidon/config/application.json]?")));
        // whenever loaded mark (empty), *used* and ?ignored? config sources
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(ArgumentMatchers.any())).thenReturn(Optional.of(ConfigParsers.properties()));
        configSource.init(context);
        configSource.load();
        MatcherAssert.assertThat(configSource.description(), Matchers.stringContainsInOrder(CollectionsHelper.listOf("(ClasspathConfig[application.yaml]?)->*ClasspathConfig[", "io/helidon/config/application.properties]?*->/ClasspathConfig[", "io/helidon/config/application.conf]/->/ClasspathConfig[", "io/helidon/config/application.json]?/")));
    }

    @Test
    public void testDescriptionNotAvailable() {
        UseFirstAvailableConfigSource configSource = new UseFirstAvailableConfigSource(ConfigSources.classpath("application.yaml").optional().build(), ConfigSources.classpath("application.conf").optional().build(), ConfigSources.classpath("application.json").optional().build(), ConfigSources.classpath("application.properties").optional().build());
        MatcherAssert.assertThat(configSource.description(), Matchers.is(("ClasspathConfig[application.yaml]?->" + (("ClasspathConfig[application.conf]?->" + "ClasspathConfig[application.json]?->") + "ClasspathConfig[application.properties]?"))));
        // whenever loaded mark (empty), *used* and ?ignored? config sources
        ConfigContext context = Mockito.mock(ConfigContext.class);
        Mockito.when(context.findParser(ArgumentMatchers.any())).thenReturn(Optional.of(ConfigParsers.properties()));
        configSource.init(context);
        configSource.load();
        MatcherAssert.assertThat(configSource.description(), Matchers.is(("(ClasspathConfig[application.yaml]?)->" + (("(ClasspathConfig[application.conf]?)->" + "(ClasspathConfig[application.json]?)->") + "(ClasspathConfig[application.properties]?)"))));
    }
}

