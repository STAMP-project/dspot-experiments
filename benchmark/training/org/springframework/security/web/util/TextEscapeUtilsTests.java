/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.web.util;


import org.junit.Test;


public class TextEscapeUtilsTests {
    /**
     * &amp;, &lt;, &gt;, &#34;, &#39 and&#32;(space) escaping
     */
    @Test
    public void charactersAreEscapedCorrectly() {
        assertThat(TextEscapeUtils.escapeEntities("& a<script>\"\'")).isEqualTo("&amp;&#32;a&lt;script&gt;&#34;&#39;");
    }

    @Test
    public void nullOrEmptyStringIsHandled() throws Exception {
        assertThat(TextEscapeUtils.escapeEntities("")).isEqualTo("");
        assertThat(TextEscapeUtils.escapeEntities(null)).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidLowSurrogateIsDetected() throws Exception {
        TextEscapeUtils.escapeEntities("abc\udcccdef");
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingLowSurrogateIsDetected() throws Exception {
        TextEscapeUtils.escapeEntities("abc\ud888a");
    }

    @Test(expected = IllegalArgumentException.class)
    public void highSurrogateAtEndOfStringIsRejected() throws Exception {
        TextEscapeUtils.escapeEntities("abc\ud888");
    }

    /**
     * Delta char: &#66560;
     */
    @Test
    public void validSurrogatePairIsAccepted() throws Exception {
        assertThat(TextEscapeUtils.escapeEntities("abc\ud801\udc00a")).isEqualTo("abc&#66560;a");
    }

    @Test
    public void undefinedSurrogatePairIsIgnored() throws Exception {
        assertThat(TextEscapeUtils.escapeEntities("abc\ud888\udc00a")).isEqualTo("abca");
    }
}

