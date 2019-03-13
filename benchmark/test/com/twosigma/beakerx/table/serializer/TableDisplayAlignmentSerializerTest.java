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


import TableDisplayAlignmentProvider.LEFT_ALIGNMENT;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import java.io.IOException;
import java.io.StringWriter;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class TableDisplayAlignmentSerializerTest {
    private JsonGenerator jgen;

    private StringWriter sw;

    private static ObjectMapper mapper;

    private static TableDisplayAlignmentSerializer serializer;

    @Test
    public void serializeTableDisplayLeftAligment_resultJsonHasLValue() throws IOException {
        // when
        TableDisplayAlignmentSerializerTest.serializer.serialize(LEFT_ALIGNMENT, jgen, new DefaultSerializerProvider.Impl());
        jgen.flush();
        JsonNode actualObj = TableDisplayAlignmentSerializerTest.mapper.readTree(sw.toString());
        // then
        Assertions.assertThat(actualObj.asText()).isEqualTo("L");
    }
}

