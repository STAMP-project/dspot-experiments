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
package org.springframework.boot.autoconfigure.template;


import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.springframework.util.MimeTypeUtils;


/**
 * Tests for {@link AbstractViewResolverProperties}.
 *
 * @author Stephane Nicoll
 */
public class ViewResolverPropertiesTests {
    @Test
    public void defaultContentType() {
        assertThat(getContentType()).hasToString("text/html;charset=UTF-8");
    }

    @Test
    public void customContentTypeDefaultCharset() {
        ViewResolverPropertiesTests.ViewResolverProperties properties = new ViewResolverPropertiesTests.ViewResolverProperties();
        properties.setContentType(MimeTypeUtils.parseMimeType("text/plain"));
        assertThat(getContentType()).hasToString("text/plain;charset=UTF-8");
    }

    @Test
    public void defaultContentTypeCustomCharset() {
        ViewResolverPropertiesTests.ViewResolverProperties properties = new ViewResolverPropertiesTests.ViewResolverProperties();
        setCharset(StandardCharsets.UTF_16);
        assertThat(getContentType()).hasToString("text/html;charset=UTF-16");
    }

    @Test
    public void customContentTypeCustomCharset() {
        ViewResolverPropertiesTests.ViewResolverProperties properties = new ViewResolverPropertiesTests.ViewResolverProperties();
        properties.setContentType(MimeTypeUtils.parseMimeType("text/plain"));
        setCharset(StandardCharsets.UTF_16);
        assertThat(getContentType()).hasToString("text/plain;charset=UTF-16");
    }

    @Test
    public void customContentTypeWithPropertyAndCustomCharset() {
        ViewResolverPropertiesTests.ViewResolverProperties properties = new ViewResolverPropertiesTests.ViewResolverProperties();
        properties.setContentType(MimeTypeUtils.parseMimeType("text/plain;foo=bar"));
        setCharset(StandardCharsets.UTF_16);
        assertThat(getContentType()).hasToString("text/plain;charset=UTF-16;foo=bar");
    }

    private static class ViewResolverProperties extends AbstractViewResolverProperties {}
}

