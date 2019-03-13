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
package io.restassured.internal;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UriValidatorTest {
    @Test
    public void returns_false_when_uri_is_empty() {
        Assert.assertThat(UriValidator.isUri(""), Matchers.is(false));
    }

    @Test
    public void returns_false_when_uri_is_null() {
        Assert.assertThat(UriValidator.isUri(null), Matchers.is(false));
    }

    @Test
    public void returns_false_when_uri_is_blank() {
        Assert.assertThat(UriValidator.isUri("   "), Matchers.is(false));
    }

    @Test
    public void returns_false_when_uri_doesnt_contain_scheme() {
        Assert.assertThat(UriValidator.isUri("127.0.0.1"), Matchers.is(false));
    }

    @Test
    public void returns_false_when_uri_doesnt_contain_host() {
        Assert.assertThat(UriValidator.isUri("http://"), Matchers.is(false));
    }

    @Test
    public void returns_false_when_uri_is_malformed() {
        Assert.assertThat(UriValidator.isUri("&%!!"), Matchers.is(false));
    }

    @Test
    public void returns_true_when_uri_contains_scheme_and_host() {
        Assert.assertThat(UriValidator.isUri("http://127.0.0.1"), Matchers.is(true));
    }
}

