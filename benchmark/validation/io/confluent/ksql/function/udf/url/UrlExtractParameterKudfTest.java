/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udf.url;


import io.confluent.ksql.util.KsqlException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class UrlExtractParameterKudfTest {
    private UrlExtractParameterKudf extractUdf;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldExtractParamValueIfPresent() {
        MatcherAssert.assertThat(extractUdf.extractParam("https://docs.confluent.io?foo%20bar=baz%20zab&blank#scalar-functions", "foo bar"), CoreMatchers.equalTo("baz zab"));
    }

    @Test
    public void shouldExtractParamValueCaseSensitive() {
        MatcherAssert.assertThat(extractUdf.extractParam("https://docs.confluent.io?foo%20bar=baz&blank#scalar-functions", "foo Bar"), CoreMatchers.nullValue());
    }

    @Test
    public void shouldReturnEmptyStringIfParamHasNoValue() {
        MatcherAssert.assertThat(extractUdf.extractParam("https://docs.confluent.io?foo%20bar=baz&blank#scalar-functions", "blank"), CoreMatchers.equalTo(""));
    }

    @Test
    public void shouldReturnNullIfParamNotPresent() {
        MatcherAssert.assertThat(extractUdf.extractParam("https://docs.confluent.io?foo%20bar=baz&blank#scalar-functions", "absent"), CoreMatchers.nullValue());
    }

    @Test
    public void shouldThrowExceptionForMalformedURL() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("URL input has invalid syntax: http://257.1/bogus/[url");
        // When:
        extractUdf.extractParam("http://257.1/bogus/[url", "foo bar");
    }
}

