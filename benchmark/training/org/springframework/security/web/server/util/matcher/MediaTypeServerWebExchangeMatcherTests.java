/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.web.server.util.matcher;


import MediaType.ALL;
import MediaType.TEXT_HTML;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.springframework.http.MediaType;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class MediaTypeServerWebExchangeMatcherTests {
    private MediaTypeServerWebExchangeMatcher matcher;

    @Test(expected = IllegalArgumentException.class)
    public void constructorMediaTypeArrayWhenNullThenThrowsIllegalArgumentException() {
        MediaType[] types = null;
        new MediaTypeServerWebExchangeMatcher(types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMediaTypeArrayWhenContainsNullThenThrowsIllegalArgumentException() {
        MediaType[] types = new MediaType[]{ null };
        new MediaTypeServerWebExchangeMatcher(types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMediaTypeListWhenNullThenThrowsIllegalArgumentException() {
        List<MediaType> types = null;
        new MediaTypeServerWebExchangeMatcher(types);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorMediaTypeListWhenContainsNullThenThrowsIllegalArgumentException() {
        List<MediaType> types = Collections.singletonList(null);
        new MediaTypeServerWebExchangeMatcher(types);
    }

    @Test
    public void matchWhenDefaultResolverAndAcceptEqualThenMatch() {
        MediaType acceptType = MediaType.TEXT_HTML;
        MediaTypeServerWebExchangeMatcher matcher = new MediaTypeServerWebExchangeMatcher(acceptType);
        assertThat(matcher.matches(MediaTypeServerWebExchangeMatcherTests.exchange(acceptType)).block().isMatch()).isTrue();
    }

    @Test
    public void matchWhenDefaultResolverAndAcceptEqualAndIgnoreThenMatch() {
        MediaType acceptType = MediaType.TEXT_HTML;
        MediaTypeServerWebExchangeMatcher matcher = new MediaTypeServerWebExchangeMatcher(acceptType);
        matcher.setIgnoredMediaTypes(Collections.singleton(ALL));
        assertThat(matcher.matches(MediaTypeServerWebExchangeMatcherTests.exchange(acceptType)).block().isMatch()).isTrue();
    }

    @Test
    public void matchWhenDefaultResolverAndAcceptEqualAndIgnoreThenNotMatch() {
        MediaType acceptType = MediaType.TEXT_HTML;
        MediaTypeServerWebExchangeMatcher matcher = new MediaTypeServerWebExchangeMatcher(acceptType);
        matcher.setIgnoredMediaTypes(Collections.singleton(ALL));
        assertThat(matcher.matches(MediaTypeServerWebExchangeMatcherTests.exchange(ALL)).block().isMatch()).isFalse();
    }

    @Test
    public void matchWhenDefaultResolverAndAcceptImpliedThenMatch() {
        MediaTypeServerWebExchangeMatcher matcher = new MediaTypeServerWebExchangeMatcher(MediaType.parseMediaTypes("text/*"));
        assertThat(matcher.matches(MediaTypeServerWebExchangeMatcherTests.exchange(TEXT_HTML)).block().isMatch()).isTrue();
    }

    @Test
    public void matchWhenDefaultResolverAndAcceptImpliedAndUseEqualsThenNotMatch() {
        MediaTypeServerWebExchangeMatcher matcher = new MediaTypeServerWebExchangeMatcher(MediaType.ALL);
        matcher.setUseEquals(true);
        assertThat(matcher.matches(MediaTypeServerWebExchangeMatcherTests.exchange(TEXT_HTML)).block().isMatch()).isFalse();
    }
}

