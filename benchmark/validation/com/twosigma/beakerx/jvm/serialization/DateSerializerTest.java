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
import java.io.IOException;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class DateSerializerTest {
    private Date date;

    private static DateSerializer serializer;

    private static SerializationTestHelper<DateSerializer, Date> helper;

    @Test
    public void serializeDateSerializer_resultJsonHasType() throws IOException {
        // when
        JsonNode actualObj = DateSerializerTest.helper.serializeObject(date);
        // then
        Assertions.assertThat(actualObj.has("type")).isTrue();
        Assertions.assertThat(actualObj.get("type").asText()).isEqualTo("Date");
    }

    @Test
    public void serializeTimestamp_resultJsonHasTimestamp() throws IOException {
        long result = date.getTime();
        // when
        JsonNode actualObj = DateSerializerTest.helper.serializeObject(date);
        // then
        Assertions.assertThat(actualObj.has("timestamp")).isTrue();
        Assertions.assertThat(actualObj.get("timestamp").asLong()).isEqualTo(result);
    }
}

