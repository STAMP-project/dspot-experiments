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
package io.confluent.ksql.function.udf.json;


import io.confluent.ksql.function.KsqlFunctionException;
import io.confluent.ksql.function.udf.KudfTester;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonExtractStringKudfTest {
    private static final String JSON_DOC = "{" + (("\"thing1\":{\"thing2\":\"hello\"}," + "\"array\":[101,102]") + "}");

    private JsonExtractStringKudf udf;

    @Test
    public void shouldBeWellBehavedUdf() {
        new KudfTester(JsonExtractStringKudf::new).withArguments(JsonExtractStringKudfTest.JSON_DOC, "$.thing1").withNonNullableArgument(1).test();
    }

    @Test
    public void shouldExtractJsonField() {
        // When:
        final Object result = udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$.thing1.thing2");
        // Then:
        Assert.assertThat(result, CoreMatchers.is("hello"));
    }

    @Test
    public void shouldExtractJsonDoc() {
        // When:
        final Object result = udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$.thing1");
        // Then:
        Assert.assertThat(result, CoreMatchers.is("{\"thing2\":\"hello\"}"));
    }

    @Test
    public void shouldExtractWholeJsonDoc() {
        // When:
        final Object result = udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$");
        // Then:
        Assert.assertThat(result, CoreMatchers.is(JsonExtractStringKudfTest.JSON_DOC));
    }

    @Test
    public void shouldExtractJsonArrayField() {
        // When:
        final Object result = udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$.array.1");
        // Then:
        Assert.assertThat(result, CoreMatchers.is("102"));
    }

    @Test
    public void shouldReturnNullIfNodeNotFound() {
        // When:
        final Object result = udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$.will.not.find.me");
        // Then:
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test(expected = KsqlFunctionException.class)
    public void shouldThrowIfTooFewParameters() {
        udf.evaluate(JsonExtractStringKudfTest.JSON_DOC);
    }

    @Test(expected = KsqlFunctionException.class)
    public void shouldThrowIfTooManyParameters() {
        udf.evaluate(JsonExtractStringKudfTest.JSON_DOC, "$.thing1", "extra");
    }

    @Test(expected = KsqlFunctionException.class)
    public void shouldThrowOnInvalidJsonDoc() {
        udf.evaluate("this is NOT a JSON doc", "$.thing1");
    }

    @Test
    public void shouldBeThreadSafe() {
        IntStream.range(0, 10000).parallel().forEach(( idx) -> shouldExtractJsonField());
    }
}

