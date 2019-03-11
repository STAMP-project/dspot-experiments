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
package org.graylog2.shared.bindings.providers;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.Nullable;
import org.junit.Test;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXTERNAL_PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


public class SerializeSubtypedOptionalPropertiesTest {
    @AutoValue
    @JsonAutoDetect
    @JsonDeserialize(builder = SerializeSubtypedOptionalPropertiesTest.Foo.Builder.class)
    abstract static class Foo {
        static final String FIELD_TYPE = "type";

        static final String FIELD_BAR = "bar";

        @JsonProperty(SerializeSubtypedOptionalPropertiesTest.Foo.FIELD_TYPE)
        public abstract String type();

        @JsonProperty(SerializeSubtypedOptionalPropertiesTest.Foo.FIELD_BAR)
        public abstract Optional<SerializeSubtypedOptionalPropertiesTest.Bar> bar();

        @AutoValue.Builder
        public abstract static class Builder {
            @JsonProperty(SerializeSubtypedOptionalPropertiesTest.Foo.FIELD_TYPE)
            public abstract SerializeSubtypedOptionalPropertiesTest.Foo.Builder type(String type);

            @JsonProperty(SerializeSubtypedOptionalPropertiesTest.Foo.FIELD_BAR)
            @JsonTypeInfo(use = NAME, include = EXTERNAL_PROPERTY, property = SerializeSubtypedOptionalPropertiesTest.Foo.FIELD_TYPE)
            @Nullable
            public abstract SerializeSubtypedOptionalPropertiesTest.Foo.Builder bar(SerializeSubtypedOptionalPropertiesTest.Bar bar);

            public abstract SerializeSubtypedOptionalPropertiesTest.Foo build();

            @JsonCreator
            public static SerializeSubtypedOptionalPropertiesTest.Foo.Builder builder() {
                return new AutoValue_SerializeSubtypedOptionalPropertiesTest_Foo.Builder();
            }
        }
    }

    interface Bar {}

    @JsonAutoDetect
    @AutoValue
    abstract static class Qux implements SerializeSubtypedOptionalPropertiesTest.Bar {
        @JsonProperty("value")
        public abstract Integer value();

        @JsonCreator
        public static SerializeSubtypedOptionalPropertiesTest.Qux create(@JsonProperty("value")
        Integer value) {
            return new AutoValue_SerializeSubtypedOptionalPropertiesTest_Qux(value);
        }
    }

    @Test
    public void deserializingSubtypeWithExternalPropertyWorks() throws IOException {
        final ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider(this.getClass().getClassLoader(), ImmutableSet.of(new NamedType(SerializeSubtypedOptionalPropertiesTest.Qux.class, "Qux")));
        final ObjectMapper objectMapper = objectMapperProvider.get();
        final SerializeSubtypedOptionalPropertiesTest.Foo foo = objectMapper.readValue("{ \"type\":\"Qux\", \"bar\": { \"value\": 42 } }", SerializeSubtypedOptionalPropertiesTest.Foo.class);
        assertThat(foo.bar()).isPresent().containsInstanceOf(SerializeSubtypedOptionalPropertiesTest.Qux.class);
    }

    @Test
    public void deserializingOptionalSubtypeWithExternalPropertyWorks() throws IOException {
        final ObjectMapperProvider objectMapperProvider = new ObjectMapperProvider(this.getClass().getClassLoader(), ImmutableSet.of(new NamedType(SerializeSubtypedOptionalPropertiesTest.Qux.class, "Qux")));
        final ObjectMapper objectMapper = objectMapperProvider.get();
        final SerializeSubtypedOptionalPropertiesTest.Foo foo = objectMapper.readValue("{ \"type\":\"Qux\" }", SerializeSubtypedOptionalPropertiesTest.Foo.class);
        assertThat(foo.bar()).isEmpty();
    }
}

