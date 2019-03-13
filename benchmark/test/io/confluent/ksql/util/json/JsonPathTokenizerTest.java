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
package io.confluent.ksql.util.json;


import com.google.common.collect.ImmutableList;
import java.util.List;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathTokenizerTest {
    @Test
    public void testJsonPathTokenizer() {
        final JsonPathTokenizer jsonPathTokenizer = new JsonPathTokenizer("$.logs[0].cloud.region");
        final ImmutableList<String> tokens = ImmutableList.copyOf(jsonPathTokenizer);
        final List<String> tokenList = tokens.asList();
        Assert.assertThat(tokenList.size(), Is.is(IsEqual.equalTo(4)));
        Assert.assertThat(tokenList.get(0), Is.is(IsEqual.equalTo("logs")));
        Assert.assertThat(tokenList.get(1), Is.is(IsEqual.equalTo("0")));
        Assert.assertThat(tokenList.get(2), Is.is(IsEqual.equalTo("cloud")));
        Assert.assertThat(tokenList.get(3), Is.is(IsEqual.equalTo("region")));
    }

    @Test
    public void shouldToStringWithCarrot() {
        Assert.assertThat(new JsonPathTokenizer("$.logs[0].cloud.region").toString(), Is.is("$?.logs[0].cloud.region"));
    }
}

