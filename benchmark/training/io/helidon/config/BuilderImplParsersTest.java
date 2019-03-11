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


import BuilderImpl.ConfigContextImpl;
import ConfigParser.Content;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.spi.ConfigNode.ObjectNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigParserException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


/**
 * Tests part of {@link BuilderImpl} related to {@link ConfigParser}s.
 */
public class BuilderImplParsersTest {
    private static final String TEST_MEDIA_TYPE = "my/media/type";

    @Test
    public void testServicesDisabled() {
        List<ConfigParser> parsers = BuilderImpl.buildParsers(false, CollectionsHelper.listOf());
        MatcherAssert.assertThat(parsers, Matchers.hasSize(0));
    }

    @Test
    public void testBuiltInParserLoaded() {
        List<ConfigParser> parsers = BuilderImpl.buildParsers(true, CollectionsHelper.listOf());
        MatcherAssert.assertThat(parsers, Matchers.hasSize(1));
        MatcherAssert.assertThat(parsers.get(0), Matchers.instanceOf(io.helidon.config.internal.PropertiesConfigParser.class));
    }

    @Test
    public void testUserDefinedHasPrecedence() {
        List<ConfigParser> parsers = BuilderImpl.buildParsers(true, CollectionsHelper.listOf(new BuilderImplParsersTest.MyConfigParser()));
        MatcherAssert.assertThat(parsers, Matchers.hasSize(2));
        MatcherAssert.assertThat(parsers.get(0), Matchers.instanceOf(BuilderImplParsersTest.MyConfigParser.class));
        MatcherAssert.assertThat(parsers.get(1), Matchers.instanceOf(io.helidon.config.internal.PropertiesConfigParser.class));
    }

    @Test
    public void testContextFindParserEmpty() {
        BuilderImpl.ConfigContextImpl context = new BuilderImpl.ConfigContextImpl(CollectionsHelper.listOf());
        MatcherAssert.assertThat(context.findParser("_WHATEVER_"), Matchers.is(Optional.empty()));
    }

    @Test
    public void testContextFindParserNotAvailable() {
        ConfigParser.Content content = Mockito.mock(Content.class);
        Mockito.when(content.mediaType()).thenReturn(BuilderImplParsersTest.TEST_MEDIA_TYPE);
        BuilderImpl.ConfigContextImpl context = new BuilderImpl.ConfigContextImpl(CollectionsHelper.listOf(mockParser("application/hocon", "application/json"), mockParser(), mockParser("application/x-yaml")));
        MatcherAssert.assertThat(context.findParser(content.mediaType()), Matchers.is(Optional.empty()));
    }

    @Test
    public void testContextFindParserFindFirst() {
        ConfigParser.Content content = Mockito.mock(Content.class);
        Mockito.when(content.mediaType()).thenReturn(BuilderImplParsersTest.TEST_MEDIA_TYPE);
        ConfigParser firstParser = mockParser(BuilderImplParsersTest.TEST_MEDIA_TYPE);
        BuilderImpl.ConfigContextImpl context = new BuilderImpl.ConfigContextImpl(CollectionsHelper.listOf(mockParser("application/hocon", "application/json"), firstParser, mockParser(BuilderImplParsersTest.TEST_MEDIA_TYPE), mockParser("application/x-yaml")));
        MatcherAssert.assertThat(context.findParser(content.mediaType()).get(), Matchers.is(firstParser));
    }

    // 
    // MyConfigParser
    // 
    private static class MyConfigParser implements ConfigParser {
        @Override
        public Set<String> supportedMediaTypes() {
            return CollectionsHelper.setOf();
        }

        @Override
        public ObjectNode parse(Content content) throws ConfigParserException {
            return ObjectNode.empty();
        }
    }
}

