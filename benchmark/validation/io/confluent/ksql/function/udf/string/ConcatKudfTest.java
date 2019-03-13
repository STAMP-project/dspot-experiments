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
package io.confluent.ksql.function.udf.string;


import io.confluent.ksql.function.udf.KudfTester;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ConcatKudfTest {
    private ConcatKudf udf;

    @Test
    public void shouldBeWellBehavedUdf() {
        new KudfTester(ConcatKudf::new).withArgumentTypes(Object.class, Object.class).withUnboundedMaxArgCount().test();
    }

    @Test
    public void shouldConcatStrings() {
        Assert.assertThat(udf.evaluate("Hello", " Mum"), Matchers.is("Hello Mum"));
    }

    @Test
    public void shouldConcatNonStrings() {
        Assert.assertThat(udf.evaluate(1.345, 34), Matchers.is("1.34534"));
    }

    @Test
    public void shouldConcatIgnoringNulls() {
        Assert.assertThat(udf.evaluate(null, "this ", null, "should ", null, "work!", null), Matchers.is("this should work!"));
    }

    @Test
    public void shouldReturnEmptyStringIfAllArgsNull() {
        Assert.assertThat(udf.evaluate(null, null), Matchers.is(""));
    }
}

