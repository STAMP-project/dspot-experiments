/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.io.IOException;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SalesforceTimeFormatsTest {
    @XStreamAlias("Dto")
    public static class DateTransferObject<T> {
        private T value;

        public DateTransferObject() {
        }

        public DateTransferObject(final T value) {
            this.value = value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == (this)) {
                return true;
            }
            if (!(obj instanceof SalesforceTimeFormatsTest.DateTransferObject)) {
                return false;
            }
            final SalesforceTimeFormatsTest.DateTransferObject<?> dto = ((SalesforceTimeFormatsTest.DateTransferObject<?>) (obj));
            return Objects.equals(value, dto.value);
        }

        public T getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, value);
        }

        public void setValue(final T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    private static final String JSON_FMT = "{\"value\":\"%s\"}";

    private static final String XML_FMT = "<Dto><value>%s</value></Dto>";

    @Parameterized.Parameter(0)
    public SalesforceTimeFormatsTest.DateTransferObject<?> dto;

    @Parameterized.Parameter(1)
    public String json;

    @Parameterized.Parameter(3)
    public Class<?> parameterType;

    @Parameterized.Parameter(2)
    public String xml;

    private final ObjectMapper objectMapper = JsonUtils.createObjectMapper();

    private final XStream xStream = XStreamUtils.createXStream(SalesforceTimeFormatsTest.DateTransferObject.class);

    @Test
    public void shouldDeserializeJson() throws IOException {
        final JavaType javaType = TypeFactory.defaultInstance().constructParametricType(SalesforceTimeFormatsTest.DateTransferObject.class, parameterType);
        final SalesforceTimeFormatsTest.DateTransferObject<?> deserialized = objectMapper.readerFor(javaType).readValue(json);
        assertDeserializationResult(deserialized);
    }

    @Test
    public void shouldDeserializeXml() {
        xStream.addDefaultImplementation(parameterType, Object.class);
        final SalesforceTimeFormatsTest.DateTransferObject<?> deserialized = ((SalesforceTimeFormatsTest.DateTransferObject<?>) (xStream.fromXML(xml)));
        assertDeserializationResult(deserialized);
    }

    @Test
    public void shouldSerializeJson() throws JsonProcessingException {
        assertThat(objectMapper.writeValueAsString(dto)).isEqualTo(json);
    }

    @Test
    public void shouldSerializeXml() {
        assertThat(xStream.toXML(dto)).isEqualTo(xml);
    }
}

