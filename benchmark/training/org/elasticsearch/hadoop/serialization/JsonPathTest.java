/**
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.hadoop.serialization;


import Token.VALUE_NUMBER;
import Token.VALUE_STRING;
import java.util.Collections;
import org.elasticsearch.hadoop.serialization.Parser.Token;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathTest {
    private Parser parser;

    private Parser nestedJsonParser;

    @Test
    public void testNoPath() throws Exception {
        Assert.assertNull(ParsingUtils.seek(parser, ((String) (null))));
        Assert.assertNull(parser.currentToken());
        Assert.assertNull(ParsingUtils.seek(parser, ""));
        Assert.assertNull(parser.currentToken());
        Assert.assertNull(ParsingUtils.seek(parser, " "));
        Assert.assertNull(parser.currentToken());
    }

    @Test
    public void testNonExistingToken() throws Exception {
        Assert.assertNull(ParsingUtils.seek(parser, "nosuchtoken"));
        Assert.assertNull(parser.nextToken());
    }

    @Test
    public void testFieldName() throws Exception {
        Assert.assertNotNull(ParsingUtils.seek(parser, "age"));
        Assert.assertEquals(VALUE_NUMBER, parser.currentToken());
        Assert.assertEquals("age", parser.currentName());
    }

    @Test
    public void testOneLevelNestedField() throws Exception {
        Assert.assertNotNull(ParsingUtils.seek(parser, "address.state"));
        Assert.assertEquals(VALUE_STRING, parser.currentToken());
        Assert.assertEquals("state", parser.currentName());
    }

    @Test
    public void testFieldNestedButNotOnFirstLevel() throws Exception {
        Assert.assertNull(ParsingUtils.seek(parser, "state"));
        Assert.assertNull(parser.nextToken());
        Assert.assertNull(parser.currentToken());
    }

    @Test
    public void testNestedFieldSeek() throws Exception {
        Token seek = ParsingUtils.seek(nestedJsonParser, "nested.field");
        Assert.assertNotNull(seek);
    }

    @Test
    public void testNestedFieldValue() throws Exception {
        Assert.assertEquals(Collections.singletonList("value"), ParsingUtils.values(nestedJsonParser, "nested.field"));
    }

    @Test
    public void testNestedSecondFieldValue() throws Exception {
        Assert.assertEquals(Collections.singletonList(1), ParsingUtils.values(nestedJsonParser, "nested.foo"));
    }

    @Test
    public void testNested2FieldValue() throws Exception {
        Assert.assertEquals(Collections.singletonList("halen"), ParsingUtils.values(nestedJsonParser, "nested2.van"));
    }
}

