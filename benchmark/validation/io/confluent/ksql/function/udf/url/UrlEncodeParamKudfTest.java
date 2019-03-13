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


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class UrlEncodeParamKudfTest {
    private UrlEncodeParamKudf encodeUdf;

    @Test
    public void shouldEncodeValue() {
        MatcherAssert.assertThat(encodeUdf.encodeParam("?foo $bar"), CoreMatchers.equalTo("%3Ffoo+%24bar"));
    }

    @Test
    public void shouldReturnSpecialCharsIntact() {
        MatcherAssert.assertThat(".-*_ should all pass through without being encoded", encodeUdf.encodeParam("foo.-*_bar"), CoreMatchers.equalTo("foo.-*_bar"));
    }

    @Test
    public void shouldReturnEmptyStringForEmptyInput() {
        MatcherAssert.assertThat(encodeUdf.encodeParam(""), CoreMatchers.equalTo(""));
    }
}

