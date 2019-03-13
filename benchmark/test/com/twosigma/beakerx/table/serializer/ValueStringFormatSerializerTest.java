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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.twosigma.beakerx.table.format.ValueStringFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ValueStringFormatSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private static ObjectMapper mapper;

    private static ValueStringFormatSerializer serializer;

    @Test
    public void serializeValueStringFormat_resultJsonHasType() throws IOException {
        // given
        ValueStringFormat valueStringFormat = new ValueStringFormat("a", Arrays.asList("1", "2"));
        // when
        JsonNode actualObj = serializeValueStringFormat(valueStringFormat);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("value");
    }

    @Test
    public void serializeValues_resultJsonHasValues() throws IOException {
        // given
        Map<String, List<?>> values = new HashMap<String, List<?>>() {
            {
                put("a", Arrays.asList("one", "two"));
            }
        };
        ValueStringFormat valueStringFormat = new ValueStringFormat(values);
        // when
        JsonNode actualObj = serializeValueStringFormat(valueStringFormat);
        // then
        Assertions.assertThat(actualObj.has("values")).isTrue();
        Assertions.assertThat(((ArrayNode) (actualObj.get("values").get("a")))).isNotEmpty();
    }
}

