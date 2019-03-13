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
import com.twosigma.beakerx.table.highlight.ValueHighlighter;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ValueHighlighterSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private ValueHighlighter valueHighlighter;

    private static ObjectMapper mapper;

    private static ValueHighlighterSerializer serializer;

    @Test
    public void serializeValueHighlighter_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = serializeValueHighlighter(valueHighlighter);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("ValueHighlighter");
    }

    @Test
    public void serializeColumnName_resultJsonHasColumnName() throws IOException {
        // when
        JsonNode actualObj = serializeValueHighlighter(valueHighlighter);
        // then
        Assertions.assertThat(actualObj.has("colName")).isTrue();
        Assertions.assertThat(actualObj.get("colName").asText()).isEqualTo(valueHighlighter.getColName());
    }

    @Test
    public void serializeColors_resultJsonHasColors() throws IOException {
        // when
        JsonNode actualObj = serializeValueHighlighter(valueHighlighter);
        // then
        Assertions.assertThat(actualObj.has("colors")).isTrue();
        Assertions.assertThat(((ArrayNode) (actualObj.get("colors")))).isNotEmpty();
    }
}

