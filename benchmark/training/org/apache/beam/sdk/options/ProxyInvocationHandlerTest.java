/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.options;


import Default.Boolean;
import Default.Byte;
import Default.Character;
import Default.Class;
import Default.Double;
import Default.Float;
import Default.InstanceFactory;
import Default.Integer;
import Default.Long;
import Default.Short;
import DisplayData.Type.STRING;
import ProxyInvocationHandler.PipelineOptionsDisplayData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataMatchers;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.util.common.ReflectHelpers;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Maps;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ProxyInvocationHandler}.
 */
@RunWith(JUnit4.class)
public class ProxyInvocationHandlerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TestRule resetPipelineOptionsRegistry = new ExternalResource() {
        @Override
        protected void before() {
            PipelineOptionsFactory.resetCache();
        }
    };

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModules(ObjectMapper.findModules(ReflectHelpers.findClassLoader()));

    /**
     * A test interface with some primitives and objects.
     */
    public interface Simple extends PipelineOptions {
        boolean isOptionEnabled();

        void setOptionEnabled(boolean value);

        int getPrimitive();

        void setPrimitive(int value);

        String getString();

        void setString(String value);
    }

    @Rule
    public TestPipeline p = TestPipeline.create();

    @Test
    public void testPropertySettingAndGetting() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.Simple proxy = handler.as(ProxyInvocationHandlerTest.Simple.class);
        proxy.setString("OBJECT");
        proxy.setOptionEnabled(true);
        proxy.setPrimitive(4);
        Assert.assertEquals("OBJECT", proxy.getString());
        Assert.assertTrue(proxy.isOptionEnabled());
        Assert.assertEquals(4, proxy.getPrimitive());
    }

    /**
     * A test interface containing all the JLS default values.
     */
    public interface JLSDefaults extends PipelineOptions {
        boolean getBoolean();

        void setBoolean(boolean value);

        char getChar();

        void setChar(char value);

        byte getByte();

        void setByte(byte value);

        short getShort();

        void setShort(short value);

        int getInt();

        void setInt(int value);

        long getLong();

        void setLong(long value);

        float getFloat();

        void setFloat(float value);

        double getDouble();

        void setDouble(double value);

        Object getObject();

        void setObject(Object value);
    }

    @Test
    public void testGettingJLSDefaults() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.JLSDefaults proxy = handler.as(ProxyInvocationHandlerTest.JLSDefaults.class);
        Assert.assertFalse(proxy.getBoolean());
        Assert.assertEquals('\u0000', proxy.getChar());
        Assert.assertEquals(((byte) (0)), proxy.getByte());
        Assert.assertEquals(((short) (0)), proxy.getShort());
        Assert.assertEquals(0, proxy.getInt());
        Assert.assertEquals(0L, proxy.getLong());
        Assert.assertEquals(0.0F, proxy.getFloat(), 0.0F);
        Assert.assertEquals(0.0, proxy.getDouble(), 0.0);
        Assert.assertNull(proxy.getObject());
    }

    /**
     * A {@link DefaultValueFactory} that is used for testing.
     */
    public static class TestOptionFactory implements DefaultValueFactory<String> {
        @Override
        public String create(PipelineOptions options) {
            return "testOptionFactory";
        }
    }

    /**
     * A test enum for testing {@link Default.Enum @Default.Enum}.
     */
    public enum EnumType {

        MyEnum("MyTestEnum");
        private final String value;

        EnumType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * A test interface containing all the {@link Default} annotations.
     */
    public interface DefaultAnnotations extends PipelineOptions {
        @Default.Boolean(true)
        boolean getBoolean();

        void setBoolean(boolean value);

        @Default.Character('a')
        char getChar();

        void setChar(char value);

        @Default.Byte(((byte) (4)))
        byte getByte();

        void setByte(byte value);

        @Default.Short(((short) (5)))
        short getShort();

        void setShort(short value);

        @Default.Integer(6)
        int getInt();

        void setInt(int value);

        @Default.Long(7L)
        long getLong();

        void setLong(long value);

        @Default.Float(8.0F)
        float getFloat();

        void setFloat(float value);

        @Default.Double(9.0)
        double getDouble();

        void setDouble(double value);

        @Default.String("testString")
        String getString();

        void setString(String value);

        @Default.Class(ProxyInvocationHandlerTest.DefaultAnnotations.class)
        Class<?> getClassOption();

        void setClassOption(Class<?> value);

        @Default.Enum("MyEnum")
        ProxyInvocationHandlerTest.EnumType getEnum();

        void setEnum(ProxyInvocationHandlerTest.EnumType value);

        @Default.InstanceFactory(ProxyInvocationHandlerTest.TestOptionFactory.class)
        String getComplex();

        void setComplex(String value);
    }

    @Test
    public void testAnnotationDefaults() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.DefaultAnnotations proxy = handler.as(ProxyInvocationHandlerTest.DefaultAnnotations.class);
        Assert.assertTrue(proxy.getBoolean());
        Assert.assertEquals('a', proxy.getChar());
        Assert.assertEquals(((byte) (4)), proxy.getByte());
        Assert.assertEquals(((short) (5)), proxy.getShort());
        Assert.assertEquals(6, proxy.getInt());
        Assert.assertEquals(7, proxy.getLong());
        Assert.assertEquals(8.0F, proxy.getFloat(), 0.0F);
        Assert.assertEquals(9.0, proxy.getDouble(), 0.0);
        Assert.assertEquals("testString", proxy.getString());
        Assert.assertEquals(ProxyInvocationHandlerTest.DefaultAnnotations.class, proxy.getClassOption());
        Assert.assertEquals(ProxyInvocationHandlerTest.EnumType.MyEnum, proxy.getEnum());
        Assert.assertEquals("testOptionFactory", proxy.getComplex());
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.Simple proxy = handler.as(ProxyInvocationHandlerTest.Simple.class);
        ProxyInvocationHandlerTest.JLSDefaults sameAsProxy = as(ProxyInvocationHandlerTest.JLSDefaults.class);
        ProxyInvocationHandler handler2 = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.Simple proxy2 = handler2.as(ProxyInvocationHandlerTest.Simple.class);
        ProxyInvocationHandlerTest.JLSDefaults sameAsProxy2 = as(ProxyInvocationHandlerTest.JLSDefaults.class);
        new EqualsTester().addEqualityGroup(handler, proxy, sameAsProxy).addEqualityGroup(handler2, proxy2, sameAsProxy2).testEquals();
    }

    /**
     * A test interface for string with default.
     */
    public interface StringWithDefault extends PipelineOptions {
        @Default.String("testString")
        String getString();

        void setString(String value);
    }

    @Test
    public void testToString() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.StringWithDefault proxy = handler.as(ProxyInvocationHandlerTest.StringWithDefault.class);
        proxy.setString("stringValue");
        ProxyInvocationHandlerTest.DefaultAnnotations proxy2 = proxy.as(ProxyInvocationHandlerTest.DefaultAnnotations.class);
        proxy2.setLong(57L);
        Assert.assertEquals(String.format(("Current Settings:%n" + ("  long: 57%n" + "  string: stringValue%n"))), proxy.toString());
    }

    @Test
    public void testToStringAfterDeserializationContainsJsonEntries() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.StringWithDefault proxy = handler.as(ProxyInvocationHandlerTest.StringWithDefault.class);
        Long optionsId = getOptionsId();
        proxy.setString("stringValue");
        ProxyInvocationHandlerTest.DefaultAnnotations proxy2 = proxy.as(ProxyInvocationHandlerTest.DefaultAnnotations.class);
        proxy2.setLong(57L);
        Assert.assertEquals(String.format(("Current Settings:%n" + (("  long: 57%n" + "  optionsId: %d%n") + "  string: \"stringValue\"%n")), optionsId), serializeDeserialize(PipelineOptions.class, proxy2).toString());
    }

    @Test
    public void testToStringAfterDeserializationContainsOverriddenEntries() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.StringWithDefault proxy = handler.as(ProxyInvocationHandlerTest.StringWithDefault.class);
        Long optionsId = getOptionsId();
        proxy.setString("stringValue");
        ProxyInvocationHandlerTest.DefaultAnnotations proxy2 = proxy.as(ProxyInvocationHandlerTest.DefaultAnnotations.class);
        proxy2.setLong(57L);
        ProxyInvocationHandlerTest.Simple deserializedOptions = serializeDeserialize(ProxyInvocationHandlerTest.Simple.class, proxy2);
        deserializedOptions.setString("overriddenValue");
        Assert.assertEquals(String.format(("Current Settings:%n" + (("  long: 57%n" + "  optionsId: %d%n") + "  string: overriddenValue%n")), optionsId), deserializedOptions.toString());
    }

    /**
     * A test interface containing an unknown method.
     */
    public interface UnknownMethod {
        void unknownMethod();
    }

    @Test
    public void testInvokeWithUnknownMethod() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(("Unknown method [public abstract void " + ("org.apache.beam.sdk.options.ProxyInvocationHandlerTest$UnknownMethod.unknownMethod()] " + "invoked with args [null].")));
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        handler.invoke(handler, ProxyInvocationHandlerTest.UnknownMethod.class.getMethod("unknownMethod"), null);
    }

    /**
     * A test interface that extends another interface.
     */
    public interface SubClass extends ProxyInvocationHandlerTest.Simple {
        String getExtended();

        void setExtended(String value);
    }

    @Test
    public void testSubClassStoresSuperInterfaceValues() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SubClass extended = handler.as(ProxyInvocationHandlerTest.SubClass.class);
        extended.setString("parentValue");
        Assert.assertEquals("parentValue", extended.getString());
    }

    @Test
    public void testUpCastRetainsSuperInterfaceValues() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SubClass extended = handler.as(ProxyInvocationHandlerTest.SubClass.class);
        extended.setString("parentValue");
        ProxyInvocationHandlerTest.Simple simple = extended.as(ProxyInvocationHandlerTest.Simple.class);
        Assert.assertEquals("parentValue", simple.getString());
    }

    @Test
    public void testUpCastRetainsSubClassValues() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SubClass extended = handler.as(ProxyInvocationHandlerTest.SubClass.class);
        extended.setExtended("subClassValue");
        ProxyInvocationHandlerTest.SubClass extended2 = extended.as(ProxyInvocationHandlerTest.Simple.class).as(ProxyInvocationHandlerTest.SubClass.class);
        Assert.assertEquals("subClassValue", extended2.getExtended());
    }

    /**
     * A test interface that is a sibling to {@link SubClass}.
     */
    public interface Sibling extends ProxyInvocationHandlerTest.Simple {
        String getSibling();

        void setSibling(String value);
    }

    @Test
    public void testAsSiblingRetainsSuperInterfaceValues() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SubClass extended = handler.as(ProxyInvocationHandlerTest.SubClass.class);
        extended.setString("parentValue");
        ProxyInvocationHandlerTest.Sibling sibling = extended.as(ProxyInvocationHandlerTest.Sibling.class);
        Assert.assertEquals("parentValue", sibling.getString());
    }

    /**
     * A test interface that has the same methods as the parent.
     */
    public interface MethodConflict extends ProxyInvocationHandlerTest.Simple {
        @Override
        String getString();

        @Override
        void setString(String value);
    }

    @Test
    public void testMethodConflictProvidesSameValue() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.MethodConflict methodConflict = handler.as(ProxyInvocationHandlerTest.MethodConflict.class);
        methodConflict.setString("conflictValue");
        Assert.assertEquals("conflictValue", methodConflict.getString());
        Assert.assertEquals("conflictValue", methodConflict.as(ProxyInvocationHandlerTest.Simple.class).getString());
    }

    /**
     * A test interface that has the same methods as its parent and grandparent.
     */
    public interface DeepMethodConflict extends ProxyInvocationHandlerTest.MethodConflict {
        @Override
        String getString();

        @Override
        void setString(String value);

        @Override
        int getPrimitive();

        @Override
        void setPrimitive(int value);
    }

    @Test
    public void testDeepMethodConflictProvidesSameValue() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.DeepMethodConflict deepMethodConflict = handler.as(ProxyInvocationHandlerTest.DeepMethodConflict.class);
        // Tests overriding an already overridden method
        deepMethodConflict.setString("conflictValue");
        Assert.assertEquals("conflictValue", deepMethodConflict.getString());
        Assert.assertEquals("conflictValue", deepMethodConflict.as(ProxyInvocationHandlerTest.MethodConflict.class).getString());
        Assert.assertEquals("conflictValue", deepMethodConflict.as(ProxyInvocationHandlerTest.Simple.class).getString());
        // Tests overriding a method from an ancestor class
        deepMethodConflict.setPrimitive(5);
        Assert.assertEquals(5, deepMethodConflict.getPrimitive());
        Assert.assertEquals(5, deepMethodConflict.as(ProxyInvocationHandlerTest.MethodConflict.class).getPrimitive());
        Assert.assertEquals(5, deepMethodConflict.as(ProxyInvocationHandlerTest.Simple.class).getPrimitive());
    }

    /**
     * A test interface that shares the same methods as {@link Sibling}.
     */
    public interface SimpleSibling extends PipelineOptions {
        String getString();

        void setString(String value);
    }

    @Test
    public void testDisjointSiblingsShareValues() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SimpleSibling proxy = handler.as(ProxyInvocationHandlerTest.SimpleSibling.class);
        proxy.setString("siblingValue");
        Assert.assertEquals("siblingValue", proxy.getString());
        Assert.assertEquals("siblingValue", proxy.as(ProxyInvocationHandlerTest.Simple.class).getString());
    }

    /**
     * A test interface that joins two sibling interfaces that have conflicting methods.
     */
    public interface SiblingMethodConflict extends ProxyInvocationHandlerTest.Simple , ProxyInvocationHandlerTest.SimpleSibling {}

    @Test
    public void testSiblingMethodConflict() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.SiblingMethodConflict siblingMethodConflict = handler.as(ProxyInvocationHandlerTest.SiblingMethodConflict.class);
        siblingMethodConflict.setString("siblingValue");
        Assert.assertEquals("siblingValue", siblingMethodConflict.getString());
        Assert.assertEquals("siblingValue", siblingMethodConflict.as(ProxyInvocationHandlerTest.Simple.class).getString());
        Assert.assertEquals("siblingValue", siblingMethodConflict.as(ProxyInvocationHandlerTest.SimpleSibling.class).getString());
    }

    /**
     * A test interface that has only the getter and only a setter overriden.
     */
    public interface PartialMethodConflict extends ProxyInvocationHandlerTest.Simple {
        @Override
        String getString();

        @Override
        void setPrimitive(int value);
    }

    @Test
    public void testPartialMethodConflictProvidesSameValue() throws Exception {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        ProxyInvocationHandlerTest.PartialMethodConflict partialMethodConflict = handler.as(ProxyInvocationHandlerTest.PartialMethodConflict.class);
        // Tests overriding a getter property that is only partially bound
        partialMethodConflict.setString("conflictValue");
        Assert.assertEquals("conflictValue", partialMethodConflict.getString());
        Assert.assertEquals("conflictValue", partialMethodConflict.as(ProxyInvocationHandlerTest.Simple.class).getString());
        // Tests overriding a setter property that is only partially bound
        partialMethodConflict.setPrimitive(5);
        Assert.assertEquals(5, partialMethodConflict.getPrimitive());
        Assert.assertEquals(5, partialMethodConflict.as(ProxyInvocationHandlerTest.Simple.class).getPrimitive());
    }

    @Test
    public void testResetRegistry() {
        Set<Class<? extends PipelineOptions>> defaultRegistry = new java.util.HashSet(PipelineOptionsFactory.getRegisteredOptions());
        Assert.assertThat(defaultRegistry, Matchers.not(Matchers.hasItem(ProxyInvocationHandlerTest.FooOptions.class)));
        PipelineOptionsFactory.register(ProxyInvocationHandlerTest.FooOptions.class);
        Assert.assertThat(PipelineOptionsFactory.getRegisteredOptions(), Matchers.hasItem(ProxyInvocationHandlerTest.FooOptions.class));
        PipelineOptionsFactory.resetCache();
        Assert.assertEquals(defaultRegistry, PipelineOptionsFactory.getRegisteredOptions());
    }

    @Test
    public void testJsonConversionForDefault() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        Assert.assertNotNull(serializeDeserialize(PipelineOptions.class, options));
    }

    /**
     * Test interface for JSON conversion of simple types.
     */
    public interface SimpleTypes extends PipelineOptions {
        int getInteger();

        void setInteger(int value);

        String getString();

        void setString(String value);
    }

    @Test
    public void testJsonConversionForSimpleTypes() throws Exception {
        ProxyInvocationHandlerTest.SimpleTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.SimpleTypes.class);
        options.setString("TestValue");
        options.setInteger(5);
        ProxyInvocationHandlerTest.SimpleTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, options);
        Assert.assertEquals(5, options2.getInteger());
        Assert.assertEquals("TestValue", options2.getString());
    }

    @Test
    public void testJsonConversionOfAJsonConvertedType() throws Exception {
        ProxyInvocationHandlerTest.SimpleTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.SimpleTypes.class);
        options.setString("TestValue");
        options.setInteger(5);
        // It is important here that our first serialization goes to our most basic
        // type so that we handle the case when we don't know the types of certain
        // properties because the intermediate instance of PipelineOptions never
        // saw their interface.
        ProxyInvocationHandlerTest.SimpleTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, serializeDeserialize(PipelineOptions.class, options));
        Assert.assertEquals(5, options2.getInteger());
        Assert.assertEquals("TestValue", options2.getString());
    }

    @Test
    public void testJsonConversionForPartiallySerializedValues() throws Exception {
        ProxyInvocationHandlerTest.SimpleTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.SimpleTypes.class);
        options.setInteger(5);
        ProxyInvocationHandlerTest.SimpleTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, options);
        options2.setString("TestValue");
        ProxyInvocationHandlerTest.SimpleTypes options3 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, options2);
        Assert.assertEquals(5, options3.getInteger());
        Assert.assertEquals("TestValue", options3.getString());
    }

    @Test
    public void testJsonConversionForOverriddenSerializedValues() throws Exception {
        ProxyInvocationHandlerTest.SimpleTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.SimpleTypes.class);
        options.setInteger((-5));
        options.setString("NeedsToBeOverridden");
        ProxyInvocationHandlerTest.SimpleTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, options);
        options2.setInteger(5);
        options2.setString("TestValue");
        ProxyInvocationHandlerTest.SimpleTypes options3 = serializeDeserialize(ProxyInvocationHandlerTest.SimpleTypes.class, options2);
        Assert.assertEquals(5, options3.getInteger());
        Assert.assertEquals("TestValue", options3.getString());
    }

    /**
     * Test interface for JSON conversion of container types.
     */
    public interface ContainerTypes extends PipelineOptions {
        List<String> getList();

        void setList(List<String> values);

        Map<String, String> getMap();

        void setMap(Map<String, String> values);

        Set<String> getSet();

        void setSet(Set<String> values);
    }

    @Test
    public void testJsonConversionForContainerTypes() throws Exception {
        List<String> list = ImmutableList.of("a", "b", "c");
        Map<String, String> map = ImmutableMap.of("d", "x", "e", "y", "f", "z");
        Set<String> set = ImmutableSet.of("g", "h", "i");
        ProxyInvocationHandlerTest.ContainerTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.ContainerTypes.class);
        options.setList(list);
        options.setMap(map);
        options.setSet(set);
        ProxyInvocationHandlerTest.ContainerTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.ContainerTypes.class, options);
        Assert.assertEquals(list, options2.getList());
        Assert.assertEquals(map, options2.getMap());
        Assert.assertEquals(set, options2.getSet());
    }

    /**
     * Test interface for conversion of inner types.
     */
    public static class InnerType {
        public double doubleField;

        static ProxyInvocationHandlerTest.InnerType of(double value) {
            ProxyInvocationHandlerTest.InnerType rval = new ProxyInvocationHandlerTest.InnerType();
            rval.doubleField = value;
            return rval;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return ((obj != null) && (getClass().equals(obj.getClass()))) && (Objects.equals(doubleField, ((ProxyInvocationHandlerTest.InnerType) (obj)).doubleField));
        }
    }

    /**
     * Test interface for conversion of generics and inner types.
     */
    public static class ComplexType {
        public String stringField;

        public Integer intField;

        public List<ProxyInvocationHandlerTest.InnerType> genericType;

        public ProxyInvocationHandlerTest.InnerType innerType;

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return (((((obj != null) && (getClass().equals(obj.getClass()))) && (Objects.equals(stringField, ((ProxyInvocationHandlerTest.ComplexType) (obj)).stringField))) && (Objects.equals(intField, ((ProxyInvocationHandlerTest.ComplexType) (obj)).intField))) && (Objects.equals(genericType, ((ProxyInvocationHandlerTest.ComplexType) (obj)).genericType))) && (Objects.equals(innerType, ((ProxyInvocationHandlerTest.ComplexType) (obj)).innerType));
        }
    }

    /**
     * Test interface.
     */
    public interface ComplexTypes extends PipelineOptions {
        ProxyInvocationHandlerTest.ComplexType getComplexType();

        void setComplexType(ProxyInvocationHandlerTest.ComplexType value);
    }

    @Test
    public void testJsonConversionForComplexType() throws Exception {
        ProxyInvocationHandlerTest.ComplexType complexType = new ProxyInvocationHandlerTest.ComplexType();
        complexType.stringField = "stringField";
        complexType.intField = 12;
        complexType.innerType = ProxyInvocationHandlerTest.InnerType.of(12);
        complexType.genericType = ImmutableList.of(ProxyInvocationHandlerTest.InnerType.of(16234), ProxyInvocationHandlerTest.InnerType.of(24));
        ProxyInvocationHandlerTest.ComplexTypes options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.ComplexTypes.class);
        options.setComplexType(complexType);
        ProxyInvocationHandlerTest.ComplexTypes options2 = serializeDeserialize(ProxyInvocationHandlerTest.ComplexTypes.class, options);
        Assert.assertEquals(complexType, options2.getComplexType());
    }

    /**
     * Test interface for testing ignored properties during serialization.
     */
    public interface IgnoredProperty extends PipelineOptions {
        @JsonIgnore
        String getValue();

        void setValue(String value);
    }

    @Test
    public void testJsonConversionOfIgnoredProperty() throws Exception {
        ProxyInvocationHandlerTest.IgnoredProperty options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.IgnoredProperty.class);
        options.setValue("TestValue");
        ProxyInvocationHandlerTest.IgnoredProperty options2 = serializeDeserialize(ProxyInvocationHandlerTest.IgnoredProperty.class, options);
        Assert.assertNull(options2.getValue());
    }

    /**
     * Test class that is not serializable by Jackson.
     */
    public static class NotSerializable {
        private String value;

        public NotSerializable(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Test interface containing a class that is not serializable by Jackson.
     */
    public interface NotSerializableProperty extends PipelineOptions {
        ProxyInvocationHandlerTest.NotSerializable getValue();

        void setValue(ProxyInvocationHandlerTest.NotSerializable value);
    }

    @Test
    public void testJsonConversionOfNotSerializableProperty() throws Exception {
        ProxyInvocationHandlerTest.NotSerializableProperty options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.NotSerializableProperty.class);
        options.setValue(new ProxyInvocationHandlerTest.NotSerializable("TestString"));
        expectedException.expect(JsonMappingException.class);
        expectedException.expectMessage("Failed to serialize and deserialize property 'value'");
        serializeDeserialize(ProxyInvocationHandlerTest.NotSerializableProperty.class, options);
    }

    /**
     * Test interface that has {@link JsonIgnore @JsonIgnore} on a property that Jackson can't
     * serialize.
     */
    public interface IgnoredNotSerializableProperty extends PipelineOptions {
        @JsonIgnore
        ProxyInvocationHandlerTest.NotSerializable getValue();

        void setValue(ProxyInvocationHandlerTest.NotSerializable value);
    }

    @Test
    public void testJsonConversionOfIgnoredNotSerializableProperty() throws Exception {
        ProxyInvocationHandlerTest.IgnoredNotSerializableProperty options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.IgnoredNotSerializableProperty.class);
        options.setValue(new ProxyInvocationHandlerTest.NotSerializable("TestString"));
        ProxyInvocationHandlerTest.IgnoredNotSerializableProperty options2 = serializeDeserialize(ProxyInvocationHandlerTest.IgnoredNotSerializableProperty.class, options);
        Assert.assertNull(options2.getValue());
    }

    /**
     * Test class that is only serializable by Jackson with the added metadata.
     */
    public static class SerializableWithMetadata {
        private String value;

        public SerializableWithMetadata(@JsonProperty("value")
        String value) {
            this.value = value;
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }
    }

    /**
     * Test interface containing a property that is serializable by Jackson only with the additional
     * metadata.
     */
    public interface SerializableWithMetadataProperty extends PipelineOptions {
        ProxyInvocationHandlerTest.SerializableWithMetadata getValue();

        void setValue(ProxyInvocationHandlerTest.SerializableWithMetadata value);
    }

    @Test
    public void testJsonConversionOfSerializableWithMetadataProperty() throws Exception {
        ProxyInvocationHandlerTest.SerializableWithMetadataProperty options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.SerializableWithMetadataProperty.class);
        options.setValue(new ProxyInvocationHandlerTest.SerializableWithMetadata("TestString"));
        ProxyInvocationHandlerTest.SerializableWithMetadataProperty options2 = serializeDeserialize(ProxyInvocationHandlerTest.SerializableWithMetadataProperty.class, options);
        Assert.assertEquals("TestString", options2.getValue().getValue());
    }

    @Test
    public void testDisplayDataItemProperties() {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.setTempLocation("myTemp");
        DisplayData displayData = DisplayData.from(options);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("tempLocation"), DisplayDataMatchers.hasType(STRING), DisplayDataMatchers.hasValue("myTemp"), DisplayDataMatchers.hasNamespace(PipelineOptions.class))));
    }

    @Test
    public void testDisplayDataTypes() {
        Instant now = Instant.now();
        ProxyInvocationHandlerTest.TypedOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.TypedOptions.class);
        options.setInteger(1234);
        options.setTimestamp(now);
        options.setJavaClass(ProxyInvocationHandlerTest.class);
        options.setObject(new Serializable() {
            @Override
            public String toString() {
                return "foobar";
            }
        });
        DisplayData displayData = DisplayData.from(options);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("integer", 1234));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("timestamp", now));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("javaClass", ProxyInvocationHandlerTest.class));
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("object", "foobar"));
    }

    /**
     * Test interface.
     */
    public interface TypedOptions extends PipelineOptions {
        int getInteger();

        void setInteger(int value);

        Instant getTimestamp();

        void setTimestamp(Instant value);

        Class<?> getJavaClass();

        void setJavaClass(Class<?> value);

        Object getObject();

        void setObject(Object value);
    }

    @Test
    @Category(NeedsRunner.class)
    public void pipelineOptionsDisplayDataExceptionShouldFail() {
        Object brokenValueType = new Object() {
            @JsonValue
            public int getValue() {
                return 42;
            }

            @Override
            public String toString() {
                throw new RuntimeException("oh noes!!");
            }
        };
        p.getOptions().as(ProxyInvocationHandlerTest.ObjectPipelineOptions.class).setValue(brokenValueType);
        p.apply(Create.of(1, 2, 3));
        expectedException.expectMessage(PipelineOptionsDisplayData.class.getName());
        expectedException.expectMessage("oh noes!!");
        p.run();
    }

    /**
     * {@link PipelineOptions} to inject bad object implementations.
     */
    public interface ObjectPipelineOptions extends PipelineOptions {
        Object getValue();

        void setValue(Object value);
    }

    @Test
    public void testDisplayDataInheritanceNamespace() {
        ProxyInvocationHandlerTest.ExtendsBaseOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.ExtendsBaseOptions.class);
        options.setFoo("bar");
        DisplayData displayData = DisplayData.from(options);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("foo"), DisplayDataMatchers.hasValue("bar"), DisplayDataMatchers.hasNamespace(ProxyInvocationHandlerTest.ExtendsBaseOptions.class))));
    }

    /**
     * Test interface.
     */
    public interface BaseOptions extends PipelineOptions {
        String getFoo();

        void setFoo(String value);
    }

    /**
     * Test interface.
     */
    public interface ExtendsBaseOptions extends ProxyInvocationHandlerTest.BaseOptions {
        @Override
        String getFoo();

        @Override
        void setFoo(String value);
    }

    @Test
    public void testDisplayDataExcludedFromOverriddenBaseClass() {
        ProxyInvocationHandlerTest.ExtendsBaseOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.ExtendsBaseOptions.class);
        options.setFoo("bar");
        DisplayData displayData = DisplayData.from(options);
        Assert.assertThat(displayData, Matchers.not(DisplayDataMatchers.hasDisplayItem(DisplayDataMatchers.hasNamespace(ProxyInvocationHandlerTest.BaseOptions.class))));
    }

    @Test
    public void testDisplayDataIncludedForDisjointInterfaceHierarchies() {
        ProxyInvocationHandlerTest.FooOptions fooOptions = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.FooOptions.class);
        fooOptions.setFoo("foo");
        ProxyInvocationHandlerTest.BarOptions barOptions = fooOptions.as(ProxyInvocationHandlerTest.BarOptions.class);
        barOptions.setBar("bar");
        DisplayData data = DisplayData.from(barOptions);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("foo"), DisplayDataMatchers.hasNamespace(ProxyInvocationHandlerTest.FooOptions.class))));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem(Matchers.allOf(DisplayDataMatchers.hasKey("bar"), DisplayDataMatchers.hasNamespace(ProxyInvocationHandlerTest.BarOptions.class))));
    }

    /**
     * Test interface.
     */
    public interface FooOptions extends PipelineOptions {
        String getFoo();

        void setFoo(String value);
    }

    /**
     * Test interface.
     */
    public interface BarOptions extends PipelineOptions {
        String getBar();

        void setBar(String value);
    }

    @Test
    public void testDisplayDataExcludesDefaultValues() {
        PipelineOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.HasDefaults.class);
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, Matchers.not(DisplayDataMatchers.hasDisplayItem("foo")));
    }

    /**
     * Test interface.
     */
    public interface HasDefaults extends PipelineOptions {
        @Default.String("bar")
        String getFoo();

        void setFoo(String value);
    }

    @Test
    public void testDisplayDataExcludesValuesAccessedButNeverSet() {
        ProxyInvocationHandlerTest.HasDefaults options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.HasDefaults.class);
        Assert.assertEquals("bar", options.getFoo());
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, Matchers.not(DisplayDataMatchers.hasDisplayItem("foo")));
    }

    @Test
    public void testDisplayDataIncludesExplicitlySetDefaults() {
        ProxyInvocationHandlerTest.HasDefaults options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.HasDefaults.class);
        String defaultValue = options.getFoo();
        options.setFoo(defaultValue);
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", defaultValue));
    }

    @Test
    public void testDisplayDataNullValuesConvertedToEmptyString() throws Exception {
        ProxyInvocationHandlerTest.FooOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.FooOptions.class);
        options.setFoo(null);
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", ""));
        ProxyInvocationHandlerTest.FooOptions deserializedOptions = serializeDeserialize(ProxyInvocationHandlerTest.FooOptions.class, options);
        DisplayData deserializedData = DisplayData.from(deserializedOptions);
        Assert.assertThat(deserializedData, DisplayDataMatchers.hasDisplayItem("foo", ""));
    }

    @Test
    public void testDisplayDataArrayValue() throws Exception {
        ProxyInvocationHandlerTest.ArrayOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.ArrayOptions.class);
        options.setDeepArray(new String[][]{ new String[]{ "a", "b" }, new String[]{ "c" } });
        options.setDeepPrimitiveArray(new int[][]{ new int[]{ 1, 2 }, new int[]{ 3 } });
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("deepArray", "[[a, b], [c]]"));
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("deepPrimitiveArray", "[[1, 2], [3]]"));
        ProxyInvocationHandlerTest.ArrayOptions deserializedOptions = serializeDeserialize(ProxyInvocationHandlerTest.ArrayOptions.class, options);
        DisplayData deserializedData = DisplayData.from(deserializedOptions);
        Assert.assertThat(deserializedData, DisplayDataMatchers.hasDisplayItem("deepPrimitiveArray", "[[1, 2], [3]]"));
    }

    /**
     * Test interface.
     */
    public interface ArrayOptions extends PipelineOptions {
        String[][] getDeepArray();

        void setDeepArray(String[][] value);

        int[][] getDeepPrimitiveArray();

        void setDeepPrimitiveArray(int[][] value);
    }

    @Test
    public void testDisplayDataJsonSerialization() throws IOException {
        ProxyInvocationHandlerTest.FooOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.FooOptions.class);
        options.setFoo("bar");
        @SuppressWarnings("unchecked")
        Map<String, Object> map = ProxyInvocationHandlerTest.MAPPER.readValue(ProxyInvocationHandlerTest.MAPPER.writeValueAsBytes(options), Map.class);
        Assert.assertThat("main pipeline options data keyed as 'options'", map, Matchers.hasKey("options"));
        Assert.assertThat("display data keyed as 'display_data'", map, Matchers.hasKey("display_data"));
        Map<?, ?> expectedDisplayItem = ImmutableMap.<String, String>builder().put("namespace", ProxyInvocationHandlerTest.FooOptions.class.getName()).put("key", "foo").put("value", "bar").put("type", "STRING").build();
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> deserializedDisplayData = ((List<Map<?, ?>>) (map.get("display_data")));
        Assert.assertThat(deserializedDisplayData, Matchers.hasItem(expectedDisplayItem));
    }

    @Test
    public void testDisplayDataFromDeserializedJson() throws Exception {
        ProxyInvocationHandlerTest.FooOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.FooOptions.class);
        options.setFoo("bar");
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "bar"));
        ProxyInvocationHandlerTest.FooOptions deserializedOptions = serializeDeserialize(ProxyInvocationHandlerTest.FooOptions.class, options);
        DisplayData dataAfterDeserialization = DisplayData.from(deserializedOptions);
        Assert.assertEquals(data, dataAfterDeserialization);
    }

    @Test
    public void testDisplayDataDeserializationWithRegistration() throws Exception {
        PipelineOptionsFactory.register(ProxyInvocationHandlerTest.HasClassOptions.class);
        ProxyInvocationHandlerTest.HasClassOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.HasClassOptions.class);
        options.setClassOption(ProxyInvocationHandlerTest.class);
        PipelineOptions deserializedOptions = serializeDeserialize(PipelineOptions.class, options);
        DisplayData displayData = DisplayData.from(deserializedOptions);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("classOption", ProxyInvocationHandlerTest.class));
    }

    @Test
    public void testDisplayDataMissingPipelineOptionsRegistration() throws Exception {
        ProxyInvocationHandlerTest.HasClassOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.HasClassOptions.class);
        options.setClassOption(ProxyInvocationHandlerTest.class);
        PipelineOptions deserializedOptions = serializeDeserialize(PipelineOptions.class, options);
        DisplayData displayData = DisplayData.from(deserializedOptions);
        String expectedJsonValue = ProxyInvocationHandlerTest.MAPPER.writeValueAsString(ProxyInvocationHandlerTest.class);
        Assert.assertThat(displayData, DisplayDataMatchers.hasDisplayItem("classOption", expectedJsonValue));
    }

    /**
     * Test interface.
     */
    public interface HasClassOptions extends PipelineOptions {
        Class<?> getClassOption();

        void setClassOption(Class<?> value);
    }

    @Test
    public void testDisplayDataJsonValueSetAfterDeserialization() throws Exception {
        ProxyInvocationHandlerTest.FooOptions options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.FooOptions.class);
        options.setFoo("bar");
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, DisplayDataMatchers.hasDisplayItem("foo", "bar"));
        ProxyInvocationHandlerTest.FooOptions deserializedOptions = serializeDeserialize(ProxyInvocationHandlerTest.FooOptions.class, options);
        deserializedOptions.setFoo("baz");
        DisplayData dataAfterDeserialization = DisplayData.from(deserializedOptions);
        Assert.assertThat(dataAfterDeserialization, DisplayDataMatchers.hasDisplayItem("foo", "baz"));
    }

    @Test
    public void testDisplayDataExcludesJsonIgnoreOptions() {
        ProxyInvocationHandlerTest.IgnoredProperty options = PipelineOptionsFactory.as(ProxyInvocationHandlerTest.IgnoredProperty.class);
        options.setValue("foobar");
        DisplayData data = DisplayData.from(options);
        Assert.assertThat(data, Matchers.not(DisplayDataMatchers.hasDisplayItem("value")));
    }

    private static class CapturesOptions implements Serializable {
        PipelineOptions options = PipelineOptionsFactory.create();
    }

    @Test
    public void testOptionsAreNotSerializable() {
        expectedException.expectCause(Matchers.instanceOf(NotSerializableException.class));
        SerializableUtils.clone(new ProxyInvocationHandlerTest.CapturesOptions());
    }

    @Test
    public void testGetOptionNameFromMethod() throws NoSuchMethodException {
        ProxyInvocationHandler handler = new ProxyInvocationHandler(Maps.newHashMap());
        handler.as(ProxyInvocationHandlerTest.BaseOptions.class);
        Assert.assertEquals("foo", handler.getOptionName(ProxyInvocationHandlerTest.BaseOptions.class.getMethod("getFoo")));
    }
}

