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


import HighlightStyle.SINGLE_COLUMN;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twosigma.beakerx.table.highlight.TableDisplayCellHighlighter;
import com.twosigma.beakerx.table.highlight.UniqueEntriesHighlighter;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class UniqueEntriesHighlighterSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private static ObjectMapper mapper;

    private static UniqueEntriesHighlighterSerializer serializer;

    @Test
    public void serializeUniqueEntriesHighlighter_resultJsonHasType() throws IOException {
        // given
        UniqueEntriesHighlighter highlighter = ((UniqueEntriesHighlighter) (TableDisplayCellHighlighter.getUniqueEntriesHighlighter("a")));
        // when
        JsonNode actualObj = serializeHighliter(highlighter);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("UniqueEntriesHighlighter");
    }

    @Test
    public void serializeColumnName_resultJsonHasColumnName() throws IOException {
        // given
        UniqueEntriesHighlighter highlighter = ((UniqueEntriesHighlighter) (TableDisplayCellHighlighter.getUniqueEntriesHighlighter("a")));
        // when
        JsonNode actualObj = serializeHighliter(highlighter);
        // then
        Assertions.assertThat(actualObj.has("colName")).isTrue();
        Assertions.assertThat(actualObj.get("colName").asText()).isEqualTo("a");
    }

    @Test
    public void serializeStyle_resultJsonHasStyle() throws IOException {
        // given
        UniqueEntriesHighlighter highlighter = ((UniqueEntriesHighlighter) (TableDisplayCellHighlighter.getUniqueEntriesHighlighter("a", SINGLE_COLUMN)));
        // when
        JsonNode actualObj = serializeHighliter(highlighter);
        // then
        Assertions.assertThat(actualObj.has("style")).isTrue();
        Assertions.assertThat(actualObj.get("style").asText()).isEqualTo("SINGLE_COLUMN");
    }
}

