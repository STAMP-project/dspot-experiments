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


import Color.GREEN;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twosigma.beakerx.chart.Color;
import com.twosigma.beakerx.table.highlight.HighlightStyle;
import com.twosigma.beakerx.table.highlight.ThreeColorHeatmapHighlighter;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class ThreeColorHeatmapHighlighterSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private static ObjectMapper mapper;

    private static ThreeColorHeatmapHighlighterSerializer serializer;

    @Test
    public void serializeThreeColorHeatmapHighlighter_resultJsonHasType() throws IOException {
        // given
        ThreeColorHeatmapHighlighter heatmapHighlighter = new ThreeColorHeatmapHighlighter("a", HighlightStyle.FULL_ROW) {};
        // when
        JsonNode actualObj = serializeHighlighter(heatmapHighlighter);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("ThreeColorHeatmapHighlighter");
    }

    @Test
    public void serializeMidValue_resultJsonHasMidValue() throws IOException {
        // given
        ThreeColorHeatmapHighlighter heatmapHighlighter = new ThreeColorHeatmapHighlighter("a", HighlightStyle.FULL_ROW, 1, 5, 10);
        // when
        JsonNode actualObj = serializeHighlighter(heatmapHighlighter);
        // then
        Assertions.assertThat(actualObj.has("midVal")).isTrue();
        Assertions.assertThat(actualObj.get("midVal").asInt()).isEqualTo(5);
    }

    @Test
    public void serializeMidColor_resultJsonHasMidColor() throws IOException {
        // given
        ThreeColorHeatmapHighlighter heatmapHighlighter = new ThreeColorHeatmapHighlighter("a", HighlightStyle.FULL_ROW, 1, 5, 10, Color.BLACK, Color.GREEN, Color.BLUE);
        // when
        JsonNode actualObj = serializeHighlighter(heatmapHighlighter);
        // then
        Assertions.assertThat(actualObj.has("midColor")).isTrue();
        Assertions.assertThat(actualObj.get("midColor").get("rgb").asInt()).isEqualTo(GREEN.getRGB());
    }
}

