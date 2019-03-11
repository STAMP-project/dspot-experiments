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
package io.restassured.http;


import ContentType.JSON;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


public class ContentTypeTest {
    @Test
    public void content_type_with_charset_returns_the_content_type_with_the_given_charset() {
        // When
        final String contentType = JSON.withCharset("UTF-8");
        // Then
        Assert.assertThat(contentType, Matchers.equalTo("application/json; charset=UTF-8"));
    }

    @Test
    public void content_type_with_java_charset_returns_the_content_type_with_the_given_charset() {
        // When
        final String contentType = JSON.withCharset(Charset.forName("ISO-8859-1"));
        // Then
        Assert.assertThat(contentType, Matchers.equalTo("application/json; charset=ISO-8859-1"));
    }

    @Test
    public void content_type_matches_expected_content_type_using_ignore_case() {
        // Given
        final String expected = "appliCatIon/JSON";
        // When
        boolean matches = JSON.matches(expected);
        // Then
        Assert.assertThat(matches, Matchers.is(true));
    }

    @Test
    public void content_type_doesnt_match_when_expected_content_type_is_not_equal_to_actual() {
        // Given
        final String expected = "application/json2";
        // When
        boolean matches = JSON.matches(expected);
        // Then
        Assert.assertThat(matches, Matchers.is(false));
    }

    @Test
    public void content_type_doesnt_match_when_expected_content_type_is_null() {
        // Given
        final String expected = null;
        // When
        boolean matches = JSON.matches(expected);
        // Then
        Assert.assertThat(matches, Matchers.is(false));
    }

    @RunWith(Parameterized.class)
    public static class ContentTypeFromStringTest {
        @Parameterized.Parameters(name = "string=''{0}'', expected=''{1}''")
        public static Collection<Object[]> data() {
            ArrayList<Object[]> data = new ArrayList<Object[]>();
            for (ContentType ct : ContentType.values()) {
                for (String cts : ct.getContentTypeStrings()) {
                    data.add(new Object[]{ ct, cts });
                }
            }
            return data;
        }

        @Parameterized.Parameter(0)
        public ContentType expected;

        @Parameterized.Parameter(1)
        public String contentTypeString;

        @Test
        public void should_find_content_type_from_string() {
            // When
            ContentType content = ContentType.fromContentType(contentTypeString);
            // Then
            Assert.assertThat(content, Matchers.is(expected));
        }
    }
}

