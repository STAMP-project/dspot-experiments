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


import com.google.common.collect.ImmutableMap;
import org.apache.kafka.common.config.ConfigException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SubstringTest {
    private Substring udf;

    @Test
    public void shouldReturnNullOnNullValue() {
        Assert.assertThat(udf.substring(null, 1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(udf.substring(null, 1, 1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(udf.substring("some string", null, 1), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(udf.substring("some string", 1, null), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldUseOneBasedIndexing() {
        Assert.assertThat(udf.substring("a test string", 1, 1), Matchers.is("a"));
        Assert.assertThat(udf.substring("a test string", (-1), 1), Matchers.is("g"));
    }

    @Test
    public void shouldExtractFromStartForPositivePositions() {
        Assert.assertThat(udf.substring("a test string", 3), Matchers.is("test string"));
        Assert.assertThat(udf.substring("a test string", 3, 4), Matchers.is("test"));
    }

    @Test
    public void shouldExtractFromEndForNegativePositions() {
        Assert.assertThat(udf.substring("a test string", (-6)), Matchers.is("string"));
        Assert.assertThat(udf.substring("a test string", (-6), 2), Matchers.is("st"));
    }

    @Test
    public void shouldTruncateOutOfBoundIndexes() {
        Assert.assertThat(udf.substring("a test string", 0), Matchers.is("a test string"));
        Assert.assertThat(udf.substring("a test string", 100), Matchers.is(""));
        Assert.assertThat(udf.substring("a test string", (-100)), Matchers.is("a test string"));
        Assert.assertThat(udf.substring("a test string", 3, 100), Matchers.is("test string"));
        Assert.assertThat(udf.substring("a test string", 3, (-100)), Matchers.is(""));
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullValueWithTwoArgs() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring(null, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullValueWithThreeArgs() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring(null, 0, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullStartIndexWithTwoArgs() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("some-string", null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullStartIndexWithThreeArgs() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("some-string", null, 0);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEOnNullEndIndex() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("some-string", 1, null);
    }

    @Test
    public void shouldUseZeroBasedIndexingIfInLegacyMode() {
        // Given:
        givenInLegacyMode();
        // Then:
        Assert.assertThat(udf.substring("a test string", 0, 1), Matchers.is("a"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowInLegacyModeIfStartIndexIsNegative() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("a test string", (-1), 1);
    }

    @Test
    public void shouldExtractFromStartInLegacyMode() {
        // Given:
        givenInLegacyMode();
        // Then:
        Assert.assertThat(udf.substring("a test string", 2), Matchers.is("test string"));
        Assert.assertThat(udf.substring("a test string", 2, 6), Matchers.is("test"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowInLegacyModeIfEndIndexIsLessThanStartIndex() {
        // Given:
        givenInLegacyMode();
        // Then:
        Assert.assertThat(udf.substring("a test string", 4, 2), Matchers.is("st"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowInLegacyModeIfStartIndexOutOfBounds() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("a test string", 100);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void shouldThrowInLegacyModeIfEndIndexOutOfBounds() {
        // Given:
        givenInLegacyMode();
        // Then:
        udf.substring("a test string", 3, 100);
    }

    @Test
    public void shouldNotEnterLegacyModeIfConfigMissing() {
        // When:
        udf.configure(ImmutableMap.of());
        // Then:
        Assert.assertThat(udfIsInLegacyMode(), Matchers.is(false));
    }

    @Test
    public void shouldEnterLegacyModeWithTrueStringConfig() {
        // When:
        configure("true");
        // Then:
        Assert.assertThat(udfIsInLegacyMode(), Matchers.is(true));
    }

    @Test
    public void shouldEnterLegacyModeWithTrueBooleanConfig() {
        // When:
        configure(true);
        // Then:
        Assert.assertThat(udfIsInLegacyMode(), Matchers.is(true));
    }

    @Test
    public void shouldNotEnterLegacyModeWithFalseStringConfig() {
        // When:
        configure("false");
        // Then:
        Assert.assertThat(udfIsInLegacyMode(), Matchers.is(false));
    }

    @Test
    public void shouldNotEnterLegacyModeWithFalseBooleanConfig() {
        // When:
        configure(false);
        // Then:
        Assert.assertThat(udfIsInLegacyMode(), Matchers.is(false));
    }

    @Test(expected = ConfigException.class)
    public void shouldThrowOnInvalidLegacyModeValueType() {
        configure(1.0);
    }
}

