/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.contentpacks.jackson;


import ValueType.BOOLEAN;
import ValueType.DOUBLE;
import ValueType.FLOAT;
import ValueType.INTEGER;
import ValueType.LONG;
import ValueType.PARAMETER;
import ValueType.STRING;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.graylog2.contentpacks.model.entities.references.ValueType;
import org.junit.Test;


public class ValueTypeDeserializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize() throws IOException {
        assertThat(objectMapper.readValue("\"boolean\"", ValueType.class)).isEqualTo(BOOLEAN);
        assertThat(objectMapper.readValue("\"double\"", ValueType.class)).isEqualTo(DOUBLE);
        assertThat(objectMapper.readValue("\"float\"", ValueType.class)).isEqualTo(FLOAT);
        assertThat(objectMapper.readValue("\"integer\"", ValueType.class)).isEqualTo(INTEGER);
        assertThat(objectMapper.readValue("\"long\"", ValueType.class)).isEqualTo(LONG);
        assertThat(objectMapper.readValue("\"string\"", ValueType.class)).isEqualTo(STRING);
        assertThat(objectMapper.readValue("\"parameter\"", ValueType.class)).isEqualTo(PARAMETER);
        assertThatThrownBy(() -> objectMapper.readValue("\"\"", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Can not deserialize value of type org.graylog2.contentpacks.model.entities.references.ValueType from String \"\": No enum constant org.graylog2.contentpacks.model.entities.references.ValueType");
        assertThatThrownBy(() -> objectMapper.readValue("\"UNKNOWN\"", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Can not deserialize value of type org.graylog2.contentpacks.model.entities.references.ValueType from String \"UNKNOWN\": No enum constant org.graylog2.contentpacks.model.entities.references.ValueType");
        assertThatThrownBy(() -> objectMapper.readValue("0", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Unexpected token (VALUE_NUMBER_INT), expected VALUE_STRING: expected String");
        assertThatThrownBy(() -> objectMapper.readValue("true", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Unexpected token (VALUE_TRUE), expected VALUE_STRING: expected String");
        assertThatThrownBy(() -> objectMapper.readValue("{}", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Unexpected token (START_OBJECT), expected VALUE_STRING: expected String");
        assertThatThrownBy(() -> objectMapper.readValue("[]", .class)).isInstanceOf(JsonMappingException.class).hasMessageStartingWith("Unexpected token (START_ARRAY), expected VALUE_STRING: expected String");
    }
}

