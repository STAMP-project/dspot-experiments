/**
 * Copyright 2017 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beakerx.table.serializer;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twosigma.beakerx.table.format.DecimalStringFormat;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class DecimalStringFormatSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private static ObjectMapper mapper;

    private static DecimalStringFormatSerializer serializer;

    @Test
    public void serializeDecimalStringFormat_resultJsonHasDecimalType() throws IOException {
        // given
        DecimalStringFormat decimalStringFormat = new DecimalStringFormat();
        // when
        JsonNode actualObj = serializeDecimalStringFormat(decimalStringFormat);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("decimal");
    }

    @Test
    public void serializeDecimalStringFormat_resultJsonHasMinAndMaxDecimalEqualFour() throws IOException {
        // given
        DecimalStringFormat decimalStringFormat = new DecimalStringFormat();
        // when
        JsonNode actualObj = serializeDecimalStringFormat(decimalStringFormat);
        // then
        Assertions.assertThat(actualObj.has("minDecimals")).isTrue();
        Assertions.assertThat(actualObj.get("minDecimals").asInt()).isEqualTo(4);
        Assertions.assertThat(actualObj.has("maxDecimals")).isTrue();
        Assertions.assertThat(actualObj.get("maxDecimals").asInt()).isEqualTo(4);
    }

    @Test
    public void serializeMinAndMaxDecimalsWithZeroValue_resultJsonHasMinAndMaxDecimalEqualZero() throws IOException {
        // given
        DecimalStringFormat decimalStringFormat = new DecimalStringFormat(0, 0);
        // when
        JsonNode actualObj = serializeDecimalStringFormat(decimalStringFormat);
        // then
        Assertions.assertThat(actualObj.has("minDecimals")).isTrue();
        Assertions.assertThat(actualObj.get("minDecimals").asInt()).isEqualTo(0);
        Assertions.assertThat(actualObj.has("maxDecimals")).isTrue();
        Assertions.assertThat(actualObj.get("maxDecimals").asInt()).isEqualTo(0);
    }
}

