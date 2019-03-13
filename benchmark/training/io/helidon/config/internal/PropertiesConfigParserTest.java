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
package io.helidon.config.internal;


import ConfigNode.ObjectNode;
import io.helidon.config.ValueNodeMatcher;
import io.helidon.config.spi.ConfigNode;
import io.helidon.config.spi.ConfigParser;
import io.helidon.config.spi.ConfigParserException;
import java.io.Reader;
import java.io.StringReader;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link PropertiesConfigParser}.
 */
public class PropertiesConfigParserTest {
    @Test
    public void testGetSupportedMediaTypes() {
        PropertiesConfigParser parser = new PropertiesConfigParser();
        MatcherAssert.assertThat(parser.supportedMediaTypes(), Matchers.containsInAnyOrder(PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES));
    }

    @Test
    public void testParse() {
        PropertiesConfigParser parser = new PropertiesConfigParser();
        ConfigNode.ObjectNode node = parser.parse(((PropertiesConfigParserTest.StringContent) (() -> "aaa = bbb")));
        MatcherAssert.assertThat(node.entrySet(), Matchers.hasSize(1));
        MatcherAssert.assertThat(node.get("aaa"), ValueNodeMatcher.valueNode("bbb"));
    }

    @Test
    public void testParseThrowsConfigParserException() {
        Assertions.assertThrows(ConfigParserException.class, () -> {
            PropertiesConfigParser parser = new PropertiesConfigParser();
            parser.parse(((PropertiesConfigParserTest.StringContent) (() -> null)));
        });
    }

    // 
    // helper
    // 
    @FunctionalInterface
    private interface StringContent extends ConfigParser.Content {
        @Override
        default String mediaType() {
            return PropertiesConfigParser.MEDIA_TYPE_TEXT_JAVA_PROPERTIES;
        }

        @Override
        default Reader asReadable() {
            return new StringReader(getContent());
        }

        String getContent();
    }
}

