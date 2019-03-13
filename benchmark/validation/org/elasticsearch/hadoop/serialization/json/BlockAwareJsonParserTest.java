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
package org.elasticsearch.hadoop.serialization.json;


import Parser.Token.END_ARRAY;
import Parser.Token.END_OBJECT;
import Parser.Token.FIELD_NAME;
import Parser.Token.START_ARRAY;
import Parser.Token.START_OBJECT;
import Parser.Token.VALUE_STRING;
import java.nio.charset.Charset;
import org.elasticsearch.hadoop.serialization.Parser;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BlockAwareJsonParserTest {
    /* first ^ : The block aware parser is created on this token
    ...---^ : The tokens skipped when exitBlock is called, with the ^ pointing to the token stream cursor after the operation.
    |---... : The location in the token stream that exitBlock is called in this test case if it does not exit right after being created.
    X : The current token when exitBlock is called, but the exitBlock function does not exit anything.
     */
    @Test
    public void testNoSkipping() {
        String data = "{\"test\":\"value\"}";
        // ^X
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("test"));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        blockParser.exitBlock();
        Assert.assertThat(parser.currentToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("test"));
    }

    @Test
    public void testSkipping() {
        String data = "{\"test\":\"value\"}";
        // ^     |-------------^
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(0));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(blockParser.text(), Matchers.equalTo("test"));
        blockParser.exitBlock();
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(15));
        Assert.assertThat(parser.currentToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testSkippingAtStart() {
        String data = "{\"test\":\"value\"}";
        // ^    |-------------^
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(0));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(blockParser.text(), Matchers.equalTo("test"));
        blockParser.exitBlock();
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(15));
        Assert.assertThat(parser.currentToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testSkippingArray() {
        String data = "{\"array\":[{\"test\":\"value\"}]}";
        // ^      |-------------------^
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("array"));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(START_ARRAY));
        // assertThat(blockParser.tokenCharOffset(), equalTo(9)); // Doesn't quite work correctly?
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(10));
        blockParser.exitBlock();
        Assert.assertThat(blockParser.tokenCharOffset(), Matchers.equalTo(26));
        Assert.assertThat(parser.currentToken(), Matchers.equalTo(END_ARRAY));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testSkippingAtArrayStart() {
        String data = "{\"array\":[{\"test\":\"value\"}]}";
        // ^--------------------^
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("array"));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_ARRAY));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        blockParser.exitBlock();
        Assert.assertThat(parser.currentToken(), Matchers.equalTo(END_ARRAY));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.nullValue());
    }

    @Test
    public void testSkippingOutOfScope() {
        String data = "{\"array\":[{\"test\":\"value\"}, {\"test2\":\"value2\"}]}";
        // ^------------------^                        X
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("array"));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_ARRAY));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        blockParser.exitBlock();
        Assert.assertThat(blockParser.currentToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(blockParser.text(), Matchers.equalTo("test2"));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(VALUE_STRING));
        Assert.assertThat(blockParser.text(), Matchers.equalTo("value2"));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(blockParser.nextToken(), Matchers.equalTo(END_ARRAY));
        boolean failed = false;
        try {
            blockParser.exitBlock();
        } catch (Exception e) {
            failed = true;
        }
        if (!failed) {
            Assert.fail("Should not have successfully exited a block out of scope of itself.");
        }
    }

    /**
     * In reality this test case is most likely not going to arise unless the underlying parser is used (Jackson
     * defends against invalid JSON already), but it is included for sanity and for code coverage.
     */
    @Test
    public void testSkippingAndEncounterEOF() {
        String data = "{\"array\":[{\"test\":\"value\"}]}";
        // ^                    |X
        Parser parser = new JacksonJsonParser(data.getBytes(Charset.defaultCharset()));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("array"));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_ARRAY));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(START_OBJECT));
        BlockAwareJsonParser blockParser = new BlockAwareJsonParser(parser);
        // Improper use (using underlying parser instead of block parser)
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(FIELD_NAME));
        Assert.assertThat(parser.text(), Matchers.equalTo("test"));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(VALUE_STRING));
        Assert.assertThat(parser.text(), Matchers.equalTo("value"));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(END_OBJECT));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(END_ARRAY));
        Assert.assertThat(parser.nextToken(), Matchers.equalTo(END_OBJECT));
        blockParser.exitBlock();
        Assert.assertThat(parser.currentToken(), Matchers.nullValue());
    }
}

