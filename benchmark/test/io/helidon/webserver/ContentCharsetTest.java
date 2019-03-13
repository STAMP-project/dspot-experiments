/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
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
package io.helidon.webserver;


import Request.DEFAULT_CHARSET;
import io.helidon.common.CollectionsHelper;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * The StringContentReaderTest.
 */
public class ContentCharsetTest {
    @Test
    public void requestContentCharset() {
        RequestTestStub request = charset(mapOf("content-type", listOf("application/json; charset=cp1250")));
        MatcherAssert.assertThat(Request.requestContentCharset(request), CoreMatchers.is(Charset.forName("cp1250")));
    }

    @Test
    public void invalidRequestContentCharset() {
        RequestTestStub request = charset(mapOf("content-type", listOf("application/json; charset=invalid-charset-name")));
        Assertions.assertThrows(UnsupportedCharsetException.class, () -> Request.requestContentCharset(request));
    }

    @Test
    public void nonexistentCharset() {
        RequestTestStub request = charset(mapOf("content-type", listOf("application/json")));
        MatcherAssert.assertThat(Request.requestContentCharset(request), CoreMatchers.is(DEFAULT_CHARSET));
    }

    @Test
    public void missingContentType() {
        RequestTestStub request = charset(CollectionsHelper.mapOf());
        MatcherAssert.assertThat(Request.requestContentCharset(request), CoreMatchers.is(DEFAULT_CHARSET));
    }
}

