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
package org.graylog2.jackson;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import java.io.IOException;
import org.junit.Test;


public class AutoValueSubtypeResolverTest {
    private ObjectMapper objectMapper;

    @Test
    public void test() throws IOException {
        final ValueType valueType = ValueType.builder().text("Test").foobar("foobar").build();
        final String json = objectMapper.writeValueAsString(valueType);
        final ValueType readValue = objectMapper.readValue(json, ValueType.class);
        assertThat(readValue).isEqualTo(valueType);
    }

    @Test
    public void testNested() throws IOException {
        final AutoValueSubtypeResolverTest.NestedValueType valueType = AutoValueSubtypeResolverTest.NestedValueType.builder().text("Test").baz("baz").build();
        final String json = objectMapper.writeValueAsString(valueType);
        final AutoValueSubtypeResolverTest.NestedValueType readValue = objectMapper.readValue(json, AutoValueSubtypeResolverTest.NestedValueType.class);
        assertThat(readValue).isEqualTo(valueType);
    }

    @AutoValue
    @JsonDeserialize(builder = AutoValue_AutoValueSubtypeResolverTest_NestedValueType.Builder.class)
    public abstract static class NestedValueType implements Parent {
        static final String VERSION = "2";

        private static final String FIELD_BAZ = "baz";

        @JsonProperty(AutoValueSubtypeResolverTest.NestedValueType.FIELD_BAZ)
        public abstract String baz();

        public static AutoValueSubtypeResolverTest.NestedValueType.Builder builder() {
            return new AutoValue_AutoValueSubtypeResolverTest_NestedValueType.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder implements Parent.ParentBuilder<AutoValueSubtypeResolverTest.NestedValueType.Builder> {
            @JsonProperty(AutoValueSubtypeResolverTest.NestedValueType.FIELD_BAZ)
            public abstract AutoValueSubtypeResolverTest.NestedValueType.Builder baz(String baz);

            abstract AutoValueSubtypeResolverTest.NestedValueType autoBuild();

            public AutoValueSubtypeResolverTest.NestedValueType build() {
                version(AutoValueSubtypeResolverTest.NestedValueType.VERSION);
                return autoBuild();
            }
        }
    }
}

