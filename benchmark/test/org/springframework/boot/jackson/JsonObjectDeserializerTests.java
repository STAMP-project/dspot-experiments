/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.NullNode;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;


/**
 * Tests for {@link JsonObjectDeserializer}.
 *
 * @author Phillip Webb
 */
public class JsonObjectDeserializerTests {
    private JsonObjectDeserializerTests.TestJsonObjectDeserializer<Object> testDeserializer = new JsonObjectDeserializerTests.TestJsonObjectDeserializer<>();

    @Test
    public void deserializeObjectShouldReadJson() throws Exception {
        NameAndAgeJsonComponent.Deserializer deserializer = new NameAndAgeJsonComponent.Deserializer();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(NameAndAge.class, deserializer);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        NameAndAge nameAndAge = mapper.readValue("{\"name\":\"spring\",\"age\":100}", NameAndAge.class);
        assertThat(nameAndAge.getName()).isEqualTo("spring");
        assertThat(nameAndAge.getAge()).isEqualTo(100);
    }

    @Test
    public void nullSafeValueWhenValueIsNullShouldReturnNull() {
        String value = this.testDeserializer.testNullSafeValue(null, String.class);
        assertThat(value).isNull();
    }

    @Test
    public void nullSafeValueWhenClassIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.testDeserializer.testNullSafeValue(mock(.class), null)).withMessageContaining("Type must not be null");
    }

    @Test
    public void nullSafeValueWhenClassIsStringShouldReturnString() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.textValue()).willReturn("abc");
        String value = this.testDeserializer.testNullSafeValue(node, String.class);
        assertThat(value).isEqualTo("abc");
    }

    @Test
    public void nullSafeValueWhenClassIsBooleanShouldReturnBoolean() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.booleanValue()).willReturn(true);
        Boolean value = this.testDeserializer.testNullSafeValue(node, Boolean.class);
        assertThat(value).isTrue();
    }

    @Test
    public void nullSafeValueWhenClassIsLongShouldReturnLong() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.longValue()).willReturn(10L);
        Long value = this.testDeserializer.testNullSafeValue(node, Long.class);
        assertThat(value).isEqualTo(10L);
    }

    @Test
    public void nullSafeValueWhenClassIsIntegerShouldReturnInteger() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.intValue()).willReturn(10);
        Integer value = this.testDeserializer.testNullSafeValue(node, Integer.class);
        assertThat(value).isEqualTo(10);
    }

    @Test
    public void nullSafeValueWhenClassIsShortShouldReturnShort() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.shortValue()).willReturn(((short) (10)));
        Short value = this.testDeserializer.testNullSafeValue(node, Short.class);
        assertThat(value).isEqualTo(((short) (10)));
    }

    @Test
    public void nullSafeValueWhenClassIsDoubleShouldReturnDouble() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.doubleValue()).willReturn(1.1);
        Double value = this.testDeserializer.testNullSafeValue(node, Double.class);
        assertThat(value).isEqualTo(1.1);
    }

    @Test
    public void nullSafeValueWhenClassIsFloatShouldReturnFloat() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.floatValue()).willReturn(1.1F);
        Float value = this.testDeserializer.testNullSafeValue(node, Float.class);
        assertThat(value).isEqualTo(1.1F);
    }

    @Test
    public void nullSafeValueWhenClassIsBigDecimalShouldReturnBigDecimal() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.decimalValue()).willReturn(BigDecimal.TEN);
        BigDecimal value = this.testDeserializer.testNullSafeValue(node, BigDecimal.class);
        assertThat(value).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void nullSafeValueWhenClassIsBigIntegerShouldReturnBigInteger() {
        JsonNode node = Mockito.mock(JsonNode.class);
        BDDMockito.given(node.bigIntegerValue()).willReturn(BigInteger.TEN);
        BigInteger value = this.testDeserializer.testNullSafeValue(node, BigInteger.class);
        assertThat(value).isEqualTo(BigInteger.TEN);
    }

    @Test
    public void nullSafeValueWhenClassIsUnknownShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.testDeserializer.testNullSafeValue(mock(.class), .class)).withMessageContaining("Unsupported value type java.io.InputStream");
    }

    @Test
    public void getRequiredNodeWhenTreeIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> this.testDeserializer.testGetRequiredNode(null, "test")).withMessageContaining("Tree must not be null");
    }

    @Test
    public void getRequiredNodeWhenNodeIsNullShouldThrowException() {
        JsonNode tree = Mockito.mock(JsonNode.class);
        BDDMockito.given(tree.get("test")).willReturn(null);
        assertThatIllegalStateException().isThrownBy(() -> this.testDeserializer.testGetRequiredNode(tree, "test")).withMessageContaining("Missing JSON field 'test'");
    }

    @Test
    public void getRequiredNodeWhenNodeIsNullNodeShouldThrowException() {
        JsonNode tree = Mockito.mock(JsonNode.class);
        BDDMockito.given(tree.get("test")).willReturn(NullNode.instance);
        assertThatIllegalStateException().isThrownBy(() -> this.testDeserializer.testGetRequiredNode(tree, "test")).withMessageContaining("Missing JSON field 'test'");
    }

    @Test
    public void getRequiredNodeWhenNodeIsFoundShouldReturnNode() {
        JsonNode node = Mockito.mock(JsonNode.class);
        JsonNode tree = node;
        BDDMockito.given(tree.get("test")).willReturn(node);
        assertThat(this.testDeserializer.testGetRequiredNode(tree, "test")).isEqualTo(node);
    }

    static class TestJsonObjectDeserializer<T> extends JsonObjectDeserializer<T> {
        @Override
        protected T deserializeObject(JsonParser jsonParser, DeserializationContext context, ObjectCodec codec, JsonNode tree) {
            return null;
        }

        public <D> D testNullSafeValue(JsonNode jsonNode, Class<D> type) {
            return JsonObjectDeserializerTests.TestJsonObjectDeserializer.nullSafeValue(jsonNode, type);
        }

        public JsonNode testGetRequiredNode(JsonNode tree, String fieldName) {
            return JsonObjectDeserializerTests.TestJsonObjectDeserializer.getRequiredNode(tree, fieldName);
        }
    }
}

