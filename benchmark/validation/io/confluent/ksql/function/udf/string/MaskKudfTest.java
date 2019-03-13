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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MaskKudfTest {
    private final MaskKudf udf = new MaskKudf();

    @Test
    public void shouldApplyAllDefaultTypeMasks() {
        final String result = udf.mask("AbCd#$123xy Z");
        Assert.assertThat(result, CoreMatchers.is("XxXx--nnnxx-X"));
    }

    @Test
    public void shouldApplyAllExplicitTypeMasks() {
        final String result = udf.mask("AbCd#$123xy Z", "Q", "q", "9", "@");
        Assert.assertThat(result, CoreMatchers.is("QqQq@@999qq@Q"));
    }

    @Test
    public void shouldMaskNothingIfNullMasks() {
        final String result = udf.mask("AbCd#$123xy Z", null, null, null, null);
        Assert.assertThat(result, CoreMatchers.is("AbCd#$123xy Z"));
    }

    @Test
    public void shouldMaskOnlySpecifiedCharTypes() {
        final String result = udf.mask("AbCd#$123xy Z", null, "q", null, "=");
        Assert.assertThat(result, CoreMatchers.is("AqCq==123qq=Z"));
    }

    @Test
    public void shouldReturnNullForNullInput() {
        final String result = udf.mask(null);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

