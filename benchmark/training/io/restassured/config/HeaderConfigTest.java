/**
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.restassured.config;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HeaderConfigTest {
    @Test
    public void shouldOverwriteHeaderWithName_returns_true_when_applicable() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.overwriteHeadersWithName("header");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("header"), Matchers.is(true));
    }

    @Test
    public void shouldOverwriteHeaderWithName_returns_false_when_applicable() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.overwriteHeadersWithName("header2");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("header"), Matchers.is(false));
    }

    @Test
    public void shouldOverwriteHeaderWithName_is_case_insensitive() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.overwriteHeadersWithName("HeadEr");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("header"), Matchers.is(true));
    }

    @Test
    public void shouldOverwriteHeaderWithName_returns_true_when_defining_multiple_headers_at_once() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.overwriteHeadersWithName("Header2", "header2", "heaDer1");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("header2"), Matchers.is(true));
        Assert.assertThat(config.shouldOverwriteHeaderWithName("Header1"), Matchers.is(true));
    }

    @Test
    public void content_type_header_is_overwritable_by_default() {
        HeaderConfig headerConfig = new HeaderConfig();
        Assert.assertThat(headerConfig.shouldOverwriteHeaderWithName("content-type"), Matchers.is(true));
    }

    @Test
    public void accept_header_is_overwritable_by_default() {
        HeaderConfig headerConfig = new HeaderConfig();
        Assert.assertThat(headerConfig.shouldOverwriteHeaderWithName("content-type"), Matchers.is(true));
    }

    @Test
    public void accept_header_is_mergeable_if_configured_accordingly() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.mergeHeadersWithName("Accept");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("accept"), Matchers.is(false));
    }

    @Test
    public void content_type_header_is_mergeable_if_configured_accordingly() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.mergeHeadersWithName("Content-type");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("content-type"), Matchers.is(false));
    }

    @Test
    public void mergeHeadersWithName_works_as_expected() {
        // Given
        HeaderConfig headerConfig = new HeaderConfig();
        // When
        HeaderConfig config = headerConfig.mergeHeadersWithName("Content-type", "accept");
        // Then
        Assert.assertThat(config.shouldOverwriteHeaderWithName("content-type"), Matchers.is(false));
        Assert.assertThat(config.shouldOverwriteHeaderWithName("Accept"), Matchers.is(false));
    }
}

