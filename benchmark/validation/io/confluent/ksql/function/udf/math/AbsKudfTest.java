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
package io.confluent.ksql.function.udf.math;


import io.confluent.ksql.function.udf.KudfTester;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AbsKudfTest {
    private AbsKudf udf;

    @Test
    public void shouldBeWellBehavedUdf() {
        new KudfTester(AbsKudf::new).withArgumentTypes(Number.class).test();
    }

    @Test
    public void shouldReturnNullWhenArgNull() {
        Assert.assertThat(udf.evaluate(((Object) (null))), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldAbs() {
        Assert.assertThat(udf.evaluate((-1.234)), Matchers.is(1.234));
        Assert.assertThat(udf.evaluate(5567), Matchers.is(5567.0));
    }
}

