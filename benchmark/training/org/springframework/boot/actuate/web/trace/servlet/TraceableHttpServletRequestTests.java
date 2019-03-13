/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.web.trace.servlet;


import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Tests for {@link TraceableHttpServletRequest}.
 *
 * @author Madhura Bhave
 */
public class TraceableHttpServletRequestTests {
    private MockHttpServletRequest request;

    @Test
    public void getUriWithoutQueryStringShouldReturnUri() {
        validate("http://localhost/script");
    }

    @Test
    public void getUriShouldReturnUriWithQueryString() {
        this.request.setQueryString("a=b");
        validate("http://localhost/script?a=b");
    }

    @Test
    public void getUriWithSpecialCharactersInQueryStringShouldEncode() {
        this.request.setQueryString("a=${b}");
        validate("http://localhost/script?a=$%7Bb%7D");
    }

    @Test
    public void getUriWithSpecialCharactersEncodedShouldNotDoubleEncode() {
        this.request.setQueryString("a=$%7Bb%7D");
        validate("http://localhost/script?a=$%7Bb%7D");
    }
}

