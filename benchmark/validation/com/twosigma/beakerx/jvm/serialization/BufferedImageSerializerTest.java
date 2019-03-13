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


import com.fasterxml.jackson.databind.JsonNode;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class BufferedImageSerializerTest {
    private BufferedImage bufferedImage;

    private static BufferedImageSerializer serializer;

    private static SerializationTestHelper<BufferedImageSerializer, BufferedImage> helper;

    @Test
    public void serializeBufferedImage_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = BufferedImageSerializerTest.helper.serializeObject(bufferedImage);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("ImageIcon");
    }

    @Test
    public void serializeImageData_resultJsonHasImageData() throws IOException {
        // when
        JsonNode actualObj = BufferedImageSerializerTest.helper.serializeObject(bufferedImage);
        // then
        Assertions.assertThat(actualObj.has("imageData")).isTrue();
        Assertions.assertThat(actualObj.get("imageData").asText()).isNotEmpty();
    }

    @Test
    public void serializeWidth_resultJsonHasWidth() throws IOException {
        int width = bufferedImage.getWidth();
        // when
        JsonNode actualObj = BufferedImageSerializerTest.helper.serializeObject(bufferedImage);
        // then
        Assertions.assertThat(actualObj.has("width")).isTrue();
        Assertions.assertThat(actualObj.get("width").asInt()).isEqualTo(width);
    }

    @Test
    public void serializeHeight_resultJsonHasHeight() throws IOException {
        int height = bufferedImage.getHeight();
        // when
        JsonNode actualObj = BufferedImageSerializerTest.helper.serializeObject(bufferedImage);
        // then
        Assertions.assertThat(actualObj.has("height")).isTrue();
        Assertions.assertThat(actualObj.get("height").asInt()).isEqualTo(height);
    }
}

