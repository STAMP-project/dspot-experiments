/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.server.protocol.http;


import UndertowOptions.ALLOW_ENCODED_SLASH;
import io.undertow.server.HttpServerExchange;
import io.undertow.testutils.category.UnitTest;
import io.undertow.util.BadRequestException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xnio.OptionMap;

import static ParseState.PARSE_COMPLETE;


/**
 * Tests that the parser can resume when it is given partial input
 *
 * @author Stuart Douglas
 */
@Category(UnitTest.class)
public class ParserResumeTestCase {
    public static final String DATA = "GET http://www.somehost.net/apath%20with%20spaces%20and%20I%C3%B1t%C3%ABrn%C3%A2ti%C3%B4n%C3%A0li%C5%BE%C3%A6ti%C3%B8n?key1=value1&key2=I%C3%B1t%C3%ABrn%C3%A2ti%C3%B4n%C3%A0li%C5%BE%C3%A6ti%C3%B8n HTTP/1.1\r\nHost:   www.somehost.net\r\nOtherHeader: some\r\n    value\r\nHostee:another\r\nAccept-garbage:   a\r\n\r\ntttt";

    public static final HttpRequestParser PARSER = HttpRequestParser.instance(OptionMap.create(ALLOW_ENCODED_SLASH, true));

    final ParseState context = new ParseState(10);

    @Test
    public void testMethodSplit() {
        byte[] in = ParserResumeTestCase.DATA.getBytes();
        for (int i = 0; i < ((in.length) - 4); ++i) {
            try {
                testResume(i, in);
            } catch (Throwable e) {
                throw new RuntimeException(("Test failed at split " + i), e);
            }
        }
    }

    @Test
    public void testOneCharacterAtATime() throws BadRequestException {
        context.reset();
        byte[] in = ParserResumeTestCase.DATA.getBytes();
        HttpServerExchange result = new HttpServerExchange(null);
        ByteBuffer buffer = ByteBuffer.wrap(in);
        int oldLimit = buffer.limit();
        buffer.limit(1);
        while ((context.state) != (PARSE_COMPLETE)) {
            ParserResumeTestCase.PARSER.handle(buffer, context, result);
            if ((context.state) != (PARSE_COMPLETE)) {
                buffer.limit(((buffer.limit()) + 1));
            }
        } 
        Assert.assertEquals(oldLimit, ((buffer.limit()) + 4));
        runAssertions(result);
    }
}

