/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
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
package org.apache.druid.query.lookup.namespace;


import UriExtractionNamespace.JSONFlatDataParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class JSONFlatDataParserTest {
    private static final ObjectMapper MAPPER = new DefaultObjectMapper();

    private static final String KEY1 = "foo1";

    private static final String KEY2 = "foo2";

    private static final String VAL1 = "bar";

    private static final String VAL2 = "baz";

    private static final String OTHERVAL1 = "3";

    private static final String OTHERVAL2 = null;

    private static final String CANBEEMPTY1 = "";

    private static final String CANBEEMPTY2 = "notEmpty";

    private static final List<Map<String, Object>> MAPPINGS = ImmutableList.of(ImmutableMap.of("key", "foo1", "val", "bar", "otherVal", 3, "canBeEmpty", ""), ImmutableMap.of("key", "foo2", "val", "baz", "canBeEmpty", "notEmpty"));

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File tmpFile;

    @Test
    public void testSimpleParse() throws Exception {
        final UriExtractionNamespace.JSONFlatDataParser parser = new UriExtractionNamespace.JSONFlatDataParser(JSONFlatDataParserTest.MAPPER, "key", "val");
        final Map<String, String> map = new HashMap<>();
        new org.apache.druid.data.input.MapPopulator(parser.getParser()).populate(Files.asByteSource(tmpFile), map);
        Assert.assertEquals(JSONFlatDataParserTest.VAL1, map.get(JSONFlatDataParserTest.KEY1));
        Assert.assertEquals(JSONFlatDataParserTest.VAL2, map.get(JSONFlatDataParserTest.KEY2));
    }

    @Test
    public void testParseWithNullValues() throws Exception {
        final UriExtractionNamespace.JSONFlatDataParser parser = new UriExtractionNamespace.JSONFlatDataParser(JSONFlatDataParserTest.MAPPER, "key", "otherVal");
        final Map<String, String> map = new HashMap<>();
        new org.apache.druid.data.input.MapPopulator(parser.getParser()).populate(Files.asByteSource(tmpFile), map);
        Assert.assertEquals(JSONFlatDataParserTest.OTHERVAL1, map.get(JSONFlatDataParserTest.KEY1));
        Assert.assertEquals(JSONFlatDataParserTest.OTHERVAL2, map.get(JSONFlatDataParserTest.KEY2));
    }

    @Test
    public void testParseWithEmptyValues() throws Exception {
        final UriExtractionNamespace.JSONFlatDataParser parser = new UriExtractionNamespace.JSONFlatDataParser(JSONFlatDataParserTest.MAPPER, "key", "canBeEmpty");
        final Map<String, String> map = new HashMap<>();
        new org.apache.druid.data.input.MapPopulator(parser.getParser()).populate(Files.asByteSource(tmpFile), map);
        Assert.assertEquals(JSONFlatDataParserTest.CANBEEMPTY1, map.get(JSONFlatDataParserTest.KEY1));
        Assert.assertEquals(JSONFlatDataParserTest.CANBEEMPTY2, map.get(JSONFlatDataParserTest.KEY2));
    }

    @Test
    public void testFailParseOnKeyMissing() throws Exception {
        final UriExtractionNamespace.JSONFlatDataParser parser = new UriExtractionNamespace.JSONFlatDataParser(JSONFlatDataParserTest.MAPPER, "keyWHOOPS", "val");
        final Map<String, String> map = new HashMap<>();
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Key column [keyWHOOPS] missing data in line");
        new org.apache.druid.data.input.MapPopulator(parser.getParser()).populate(Files.asByteSource(tmpFile), map);
    }
}

