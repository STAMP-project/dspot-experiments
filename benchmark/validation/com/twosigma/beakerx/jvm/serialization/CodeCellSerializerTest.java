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
package com.twosigma.beakerx.jvm.serialization;


import CodeCell.Serializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.twosigma.beakerx.CodeCell;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class CodeCellSerializerTest {
    private CodeCell codeCell;

    private static Serializer serializer;

    private static SerializationTestHelper<CodeCell.Serializer, CodeCell> helper;

    @Test
    public void serializeOutputContainer_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("CodeCell");
    }

    @Test
    public void serializeCellTyped_resultJsonHasCellType() throws IOException {
        // given
        codeCell.setCellType("code");
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("cell_type")).isTrue();
        Assertions.assertThat(actualObj.get("cell_type").asText()).isEqualTo("code");
    }

    @Test
    public void serializeExecutionCount_resultJsonHasExecutionCount() throws IOException {
        // given
        codeCell.setExecutionCount("1");
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("execution_count")).isTrue();
        Assertions.assertThat(actualObj.get("execution_count").asText()).isEqualTo("1");
    }

    @Test
    public void serializeSource_resultJsonHasSource() throws IOException {
        // given
        codeCell.setSource("test source");
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("source")).isTrue();
        Assertions.assertThat(actualObj.get("source").asText()).isEqualTo("test source");
    }

    @Test
    public void serializeMetadata_resultJsonHasMetadatas() throws IOException {
        // given
        codeCell.setMetadata("test metadata");
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("metadata")).isTrue();
        Assertions.assertThat(actualObj.get("metadata").asText()).isEqualTo("test metadata");
    }

    @Test
    public void serializeTags_resultJsonHasTags() throws IOException {
        // given
        codeCell.setOutputs("test output");
        // when
        JsonNode actualObj = CodeCellSerializerTest.helper.serializeObject(codeCell);
        // then
        Assertions.assertThat(actualObj.has("outputs")).isTrue();
        Assertions.assertThat(actualObj.get("outputs").asText()).isEqualTo("test output");
    }
}

