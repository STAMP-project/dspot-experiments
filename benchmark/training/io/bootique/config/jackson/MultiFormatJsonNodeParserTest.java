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
package io.bootique.config.jackson;


import ParserType.JSON;
import ParserType.YAML;
import com.fasterxml.jackson.databind.JsonNode;
import io.bootique.config.jackson.MultiFormatJsonNodeParser.ParserType;
import io.bootique.log.BootLogger;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MultiFormatJsonNodeParserTest {
    @Test
    public void testParser() {
        Map<ParserType, Function<InputStream, Optional<JsonNode>>> parsers = createParsersMap(JSON, YAML);
        MultiFormatJsonNodeParser parser = new MultiFormatJsonNodeParser(parsers, Mockito.mock(BootLogger.class));
        Assert.assertSame(parsers.get(YAML), parser.parser(YAML));
        Assert.assertSame(parsers.get(JSON), parser.parser(JSON));
    }

    @Test(expected = IllegalStateException.class)
    public void testParser_MissingYaml() {
        Map<ParserType, Function<InputStream, Optional<JsonNode>>> parsers = createParsersMap(JSON);
        new MultiFormatJsonNodeParser(parsers, Mockito.mock(BootLogger.class)).parser(YAML);
    }

    @Test
    public void testParserTypeFromExtension_Unknown() throws MalformedURLException {
        MultiFormatJsonNodeParser parser = new MultiFormatJsonNodeParser(Collections.emptyMap(), Mockito.mock(BootLogger.class));
        Assert.assertNull(parser.parserTypeFromExtension(new URL("http://example.org/test")));
        Assert.assertNull(parser.parserTypeFromExtension(new URL("http://example.org/")));
        Assert.assertNull(parser.parserTypeFromExtension(new URL("http://example.org/test.txt")));
    }

    @Test
    public void testParserTypeFromExtension() throws MalformedURLException {
        MultiFormatJsonNodeParser parser = new MultiFormatJsonNodeParser(Collections.emptyMap(), Mockito.mock(BootLogger.class));
        Assert.assertEquals(YAML, parser.parserTypeFromExtension(new URL("http://example.org/test.yml")));
        Assert.assertEquals(YAML, parser.parserTypeFromExtension(new URL("http://example.org/test.yaml")));
        Assert.assertEquals(JSON, parser.parserTypeFromExtension(new URL("http://example.org/test.json")));
    }

    @Test
    public void testParserTypeFromExtension_IgnoreQuery() throws MalformedURLException {
        MultiFormatJsonNodeParser parser = new MultiFormatJsonNodeParser(Collections.emptyMap(), Mockito.mock(BootLogger.class));
        Assert.assertEquals(JSON, parser.parserTypeFromExtension(new URL("http://example.org/test.json?a=b")));
    }

    @Test
    public void testParserTypeFromExtension_FileUrl() throws MalformedURLException {
        MultiFormatJsonNodeParser parser = new MultiFormatJsonNodeParser(Collections.emptyMap(), Mockito.mock(BootLogger.class));
        Assert.assertEquals(YAML, parser.parserTypeFromExtension(new URL("file://example.org/test.yml")));
    }
}

