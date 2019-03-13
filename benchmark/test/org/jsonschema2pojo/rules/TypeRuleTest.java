/**
 * Copyright ? 2010-2017 Nokia
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
package org.jsonschema2pojo.rules;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.hamcrest.MatcherAssert;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Schema;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TypeRuleTest {
    private GenerationConfig config = Mockito.mock(GenerationConfig.class);

    private RuleFactory ruleFactory = Mockito.mock(RuleFactory.class);

    private TypeRule rule = new TypeRule(ruleFactory);

    @Test
    public void applyGeneratesString() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "string");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(String.class.getName()));
    }

    @Test
    public void applyGeneratesDate() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "string");
        TextNode formatNode = TextNode.valueOf("date-time");
        objectNode.set("format", formatNode);
        JType mockDateType = Mockito.mock(JType.class);
        FormatRule mockFormatRule = Mockito.mock(FormatRule.class);
        Mockito.when(mockFormatRule.apply(ArgumentMatchers.eq("fooBar"), ArgumentMatchers.eq(formatNode), ArgumentMatchers.any(), Mockito.isA(JType.class), ArgumentMatchers.isNull(Schema.class))).thenReturn(mockDateType);
        Mockito.when(ruleFactory.getFormatRule()).thenReturn(mockFormatRule);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result, equalTo(mockDateType));
    }

    @Test
    public void applyGeneratesInteger() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Integer.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("int"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeIntegerPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("existingJavaType", "int");
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("int"));
    }

    @Test
    public void applyGeneratesBigInteger() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        Mockito.when(config.isUseBigIntegers()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(BigInteger.class.getName()));
    }

    @Test
    public void applyGeneratesBigIntegerOverridingLong() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        // isUseBigIntegers should override isUseLongIntegers
        Mockito.when(config.isUseBigIntegers()).thenReturn(true);
        Mockito.when(config.isUseLongIntegers()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(BigInteger.class.getName()));
    }

    @Test
    public void applyGeneratesBigDecimal() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        Mockito.when(config.isUseBigDecimals()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(BigDecimal.class.getName()));
    }

    @Test
    public void applyGeneratesBigDecimalOverridingDouble() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        // this shows that isUseBigDecimals overrides isUseDoubleNumbers
        Mockito.when(config.isUseDoubleNumbers()).thenReturn(true);
        Mockito.when(config.isUseBigDecimals()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(BigDecimal.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeInteger() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("existingJavaType", "java.lang.Integer");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.lang.Integer"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("existingJavaType", "long");
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLong() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("existingJavaType", "java.lang.Long");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.lang.Long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongPrimitiveWhenMaximumGreaterThanIntegerMax() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("maximum", ((Integer.MAX_VALUE) + 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongWhenMaximumGreaterThanIntegerMax() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("maximum", ((Integer.MAX_VALUE) + 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Long.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongPrimitiveWhenMaximumLessThanIntegerMin() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("maximum", ((Integer.MIN_VALUE) - 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongWhenMaximumLessThanIntegerMin() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("maximum", ((Integer.MIN_VALUE) - 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Long.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongPrimitiveWhenMinimumLessThanIntegerMin() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("minimum", ((Integer.MIN_VALUE) - 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongWhenMinimumLessThanIntegerMin() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("minimum", ((Integer.MIN_VALUE) - 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Long.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongPrimitiveWhenMinimumGreaterThanIntegerMax() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("minimum", ((Integer.MAX_VALUE) + 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("long"));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeLongWhenMinimumGreaterThanIntegerMax() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("minimum", ((Integer.MAX_VALUE) + 1L));
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Long.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerUsingJavaTypeBigInteger() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");
        objectNode.put("existingJavaType", "java.math.BigInteger");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.math.BigInteger"));
    }

    @Test
    public void applyGeneratesNumber() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        Mockito.when(config.isUseDoubleNumbers()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Double.class.getName()));
    }

    @Test
    public void applyGeneratesNumberPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        Mockito.when(config.isUseDoubleNumbers()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("double"));
    }

    @Test
    public void applyGeneratesNumberUsingJavaTypeFloatPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        objectNode.put("existingJavaType", "float");
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("float"));
    }

    @Test
    public void applyGeneratesNumberUsingJavaTypeFloat() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        objectNode.put("existingJavaType", "java.lang.Float");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.lang.Float"));
    }

    @Test
    public void applyGeneratesNumberUsingJavaTypeDoublePrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        objectNode.put("existingJavaType", "double");
        Mockito.when(config.isUsePrimitives()).thenReturn(false);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("double"));
    }

    @Test
    public void applyGeneratesNumberUsingJavaTypeDouble() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        objectNode.put("existingJavaType", "java.lang.Double");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.lang.Double"));
    }

    @Test
    public void applyGeneratesNumberUsingJavaTypeBigDecimal() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");
        objectNode.put("existingJavaType", "java.math.BigDecimal");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("java.math.BigDecimal"));
    }

    @Test
    public void applyGeneratesBoolean() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "boolean");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Boolean.class.getName()));
    }

    @Test
    public void applyGeneratesBooleanPrimitive() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "boolean");
        Mockito.when(config.isUsePrimitives()).thenReturn(true);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is("boolean"));
    }

    @Test
    public void applyGeneratesAnyAsObject() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "any");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Object.class.getName()));
    }

    @Test
    public void applyGeneratesNullAsObject() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "null");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Object.class.getName()));
    }

    @Test
    public void applyGeneratesArray() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "array");
        JClass mockArrayType = Mockito.mock(JClass.class);
        ArrayRule mockArrayRule = Mockito.mock(ArrayRule.class);
        Mockito.when(mockArrayRule.apply("fooBar", objectNode, null, jpackage, null)).thenReturn(mockArrayType);
        Mockito.when(ruleFactory.getArrayRule()).thenReturn(mockArrayRule);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result, is(((JType) (mockArrayType))));
    }

    @Test
    public void applyGeneratesCustomObject() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "object");
        JDefinedClass mockObjectType = Mockito.mock(JDefinedClass.class);
        ObjectRule mockObjectRule = Mockito.mock(ObjectRule.class);
        Mockito.when(mockObjectRule.apply("fooBar", objectNode, null, jpackage, null)).thenReturn(mockObjectType);
        Mockito.when(ruleFactory.getObjectRule()).thenReturn(mockObjectRule);
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result, is(((JType) (mockObjectType))));
    }

    @Test
    public void applyChoosesObjectOnUnrecognizedType() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "unknown");
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Object.class.getName()));
    }

    @Test
    public void applyDefaultsToTypeAnyObject() {
        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());
        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        JType result = rule.apply("fooBar", objectNode, null, jpackage, null);
        MatcherAssert.assertThat(result.fullName(), is(Object.class.getName()));
    }
}

