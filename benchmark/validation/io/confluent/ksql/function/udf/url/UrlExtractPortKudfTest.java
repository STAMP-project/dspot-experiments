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


public class UrlExtractPortKudfTest {
    private UrlExtractPortKudf extractUdf;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldExtractPortIfPresent() {
        MatcherAssert.assertThat(extractUdf.extractPort("https://docs.confluent.io:8080/current/ksql/docs/syntax-reference.html#scalar-functions"), CoreMatchers.equalTo(8080));
    }

    @Test
    public void shouldReturnNullIfNoPort() {
        MatcherAssert.assertThat(extractUdf.extractPort("https://docs.confluent.io/current/ksql/docs/syntax-reference.html#scalar-functions"), CoreMatchers.equalTo(null));
    }

    @Test
    public void shouldThrowExceptionForMalformedURL() {
        // Given:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("URL input has invalid syntax: http://257.1/bogus/[url");
        // When:
        extractUdf.extractPort("http://257.1/bogus/[url");
    }
}

