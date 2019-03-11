/**
 * Copyright 2005-2019 Dozer Project
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
package com.github.dozermapper.protobuf.functional_tests;


import ProtoEnum.VALUE1;
import SimpleProtoTestObject.Builder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.protobuf.vo.proto.FieldNaming;
import com.github.dozermapper.protobuf.vo.proto.LiteTestObject;
import com.github.dozermapper.protobuf.vo.proto.MapExample;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithCollection;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithEnumCollection;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithEnumField;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoObjectWithEnumField;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoTestObjectWithNestedProtoObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufFieldNaming;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufMapExample;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithEnumCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObjectWithoutRequired;
import com.github.dozermapper.protobuf.vo.proto.SimpleEnum;
import com.github.dozermapper.protobuf.vo.proto.TestObject;
import com.github.dozermapper.protobuf.vo.proto.TestObjectContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ProtoBeansMappingTest {
    private static Mapper mapper;

    @Rule
    public ExpectedException badMapToProtoExpectedException = ExpectedException.none();

    @Rule
    public ExpectedException badMapFromProtoExpectedException = ExpectedException.none();

    @Test
    public void canSimpleToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("ABC");
        SimpleProtoTestObject result = ProtoBeansMappingTest.mapper.map(testObject, SimpleProtoTestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals(testObject.getOne(), result.getOne());
    }

    @Test
    public void canSimpleFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("ABC");
        SimpleProtoTestObject simpleProtoTestObject = simpleProtoTestObjectBuilder.build();
        TestObject result = ProtoBeansMappingTest.mapper.map(simpleProtoTestObject, TestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals(simpleProtoTestObject.getOne(), result.getOne());
    }

    @Test
    public void canSimpleWildcardToProto() {
        LiteTestObject liteTestObject = new LiteTestObject();
        liteTestObject.setOne("ABC");
        SimpleProtoTestObject result = ProtoBeansMappingTest.mapper.map(liteTestObject, SimpleProtoTestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals("ABC", result.getOne());
    }

    @Test
    public void canSimpleWildcardFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("ABC");
        LiteTestObject result = ProtoBeansMappingTest.mapper.map(simpleProtoTestObjectBuilder.build(), LiteTestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals("ABC", result.getOne());
    }

    @Test
    public void canSimpleFromProtoWithNull() {
        SimpleProtoTestObjectWithoutRequired.Builder simpleProtoTestObjectWithoutRequiredBuilder = SimpleProtoTestObjectWithoutRequired.newBuilder();
        SimpleProtoTestObjectWithoutRequired simpleProtoTestObjectWithoutRequired = simpleProtoTestObjectWithoutRequiredBuilder.build();
        TestObject result = ProtoBeansMappingTest.mapper.map(simpleProtoTestObjectWithoutRequired, TestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNull(result.getOne());
    }

    @Test
    public void canSimpleToProtoWithNull() {
        TestObject testObject = new TestObject();
        SimpleProtoTestObjectWithoutRequired result = ProtoBeansMappingTest.mapper.map(testObject, SimpleProtoTestObjectWithoutRequired.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertTrue(((result.getOne().length()) == 0));
    }

    @Test
    public void canNestedProtoFieldToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("InnerName");
        TestObjectContainer testObjectContainer = new TestObjectContainer(testObject, "Name");
        ProtoTestObjectWithNestedProtoObject result = ProtoBeansMappingTest.mapper.map(testObjectContainer, ProtoTestObjectWithNestedProtoObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertNotNull(result.getNestedObject());
        Assert.assertEquals(testObjectContainer.getOne(), result.getOne());
        Assert.assertEquals(testObjectContainer.getNested().getOne(), result.getNestedObject().getOne());
    }

    @Test
    public void canNestedProtoFieldFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("InnerName");
        ProtoTestObjectWithNestedProtoObject.Builder protoTestObjectWithNestedProtoObjectBuilder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        protoTestObjectWithNestedProtoObjectBuilder.setNestedObject(simpleProtoTestObjectBuilder.build());
        protoTestObjectWithNestedProtoObjectBuilder.setOne("Name");
        ProtoTestObjectWithNestedProtoObject protoTestObjectWithNestedProtoObject = protoTestObjectWithNestedProtoObjectBuilder.build();
        TestObjectContainer result = ProtoBeansMappingTest.mapper.map(protoTestObjectWithNestedProtoObject, TestObjectContainer.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertNotNull(result.getNested());
        Assert.assertNotNull(result.getNested().getOne());
        Assert.assertEquals(protoTestObjectWithNestedProtoObject.getOne(), result.getOne());
        Assert.assertEquals(protoTestObjectWithNestedProtoObject.getNestedObject().getOne(), result.getNested().getOne());
    }

    @Test
    public void canEnumProtoFieldToProto() {
        ObjectWithEnumField objectWithEnumField = new ObjectWithEnumField();
        objectWithEnumField.setEnumField(SimpleEnum.VALUE1);
        ProtoObjectWithEnumField result = ProtoBeansMappingTest.mapper.map(objectWithEnumField, ProtoObjectWithEnumField.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEnumField());
        Assert.assertEquals(objectWithEnumField.getEnumField().name(), result.getEnumField().name());
    }

    @Test
    public void canEnumProtoFieldFromProto() {
        ProtoObjectWithEnumField.Builder protoObjectWithEnumFieldBuilder = ProtoObjectWithEnumField.newBuilder();
        protoObjectWithEnumFieldBuilder.setEnumField(VALUE1);
        ProtoObjectWithEnumField protoObjectWithEnumField = protoObjectWithEnumFieldBuilder.build();
        ObjectWithEnumField result = ProtoBeansMappingTest.mapper.map(protoObjectWithEnumField, ObjectWithEnumField.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEnumField());
        Assert.assertEquals(protoObjectWithEnumField.getEnumField().name(), result.getEnumField().name());
    }

    @Test
    public void canRepeatedFieldToProto() {
        TestObject testObject = new TestObject();
        testObject.setOne("One");
        ObjectWithCollection objectWithCollection = new ObjectWithCollection();
        objectWithCollection.setObjects(Arrays.asList(testObject));
        ProtobufWithSimpleCollection result = ProtoBeansMappingTest.mapper.map(objectWithCollection, ProtobufWithSimpleCollection.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObjectList());
        Assert.assertEquals(1, result.getObjectCount());
        Assert.assertNotNull(result.getObject(0));
        Assert.assertNotNull(result.getObject(0).getOne());
        Assert.assertEquals(objectWithCollection.getObjects().get(0).getOne(), result.getObject(0).getOne());
    }

    @Test
    public void canRepeatedFieldFromProto() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("One");
        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder.build()));
        ProtobufWithSimpleCollection protobufWithSimpleCollection = protobufWithSimpleCollectionBuilder.build();
        ObjectWithCollection result = ProtoBeansMappingTest.mapper.map(protobufWithSimpleCollection, ObjectWithCollection.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObjects());
        Assert.assertEquals(1, result.getObjects().size());
        Assert.assertNotNull(result.getObjects().get(0));
        Assert.assertNotNull(result.getObjects().get(0).getOne());
        Assert.assertEquals(protobufWithSimpleCollection.getObject(0).getOne(), result.getObjects().get(0).getOne());
    }

    @Test
    public void canRepeatedEnumFieldToProto() {
        ObjectWithEnumCollection objectWithEnumCollection = new ObjectWithEnumCollection();
        objectWithEnumCollection.setEnums(Arrays.asList(SimpleEnum.VALUE1));
        ProtobufWithEnumCollection result = ProtoBeansMappingTest.mapper.map(objectWithEnumCollection, ProtobufWithEnumCollection.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObjectList());
        Assert.assertEquals(1, result.getObjectCount());
        Assert.assertNotNull(result.getObject(0));
        Assert.assertEquals(objectWithEnumCollection.getEnums().get(0).name(), result.getObject(0).name());
    }

    @Test
    public void canRepeatedEnumFieldFromProto() {
        ProtobufWithEnumCollection.Builder protobufWithEnumCollectionBuilder = ProtobufWithEnumCollection.newBuilder();
        protobufWithEnumCollectionBuilder.addAllObject(Arrays.asList(VALUE1));
        ProtobufWithEnumCollection protobufWithEnumCollection = protobufWithEnumCollectionBuilder.build();
        ObjectWithEnumCollection result = ProtoBeansMappingTest.mapper.map(protobufWithEnumCollection, ObjectWithEnumCollection.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEnums());
        Assert.assertEquals(1, result.getEnums().size());
        Assert.assertNotNull(result.getEnums().get(0));
        Assert.assertEquals(protobufWithEnumCollection.getObject(0).name(), result.getEnums().get(0).name());
    }

    @Test
    public void canBadMapToProto() {
        badMapToProtoExpectedException.expect(MappingException.class);
        badMapToProtoExpectedException.expectMessage("Could not call map setter method putAllValue");
        badMapToProtoExpectedException.expectCause(IsInstanceOf.instanceOf(InvocationTargetException.class));
        MapExample mapExample = new MapExample();
        mapExample.put("test", null);
        mapExample.put("foo", "bar");
        ProtoBeansMappingTest.mapper.map(mapExample, ProtobufMapExample.class);
    }

    @Test
    public void canMapToProto() {
        MapExample mapExample = new MapExample();
        mapExample.put("test", "value");
        ProtobufMapExample result = ProtoBeansMappingTest.mapper.map(mapExample, ProtobufMapExample.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getValueMap());
        Assert.assertEquals(mapExample.getValue(), result.getValueMap());
    }

    @Test
    public void canBadMapFromProto() {
        badMapFromProtoExpectedException.expect(NullPointerException.class);
        ProtobufMapExample.Builder protoMapExample = ProtobufMapExample.newBuilder();
        protoMapExample.putValue("test", null);
    }

    @Test
    public void canMapFromProto() {
        ProtobufMapExample.Builder protobufMapExampleBuilder = ProtobufMapExample.newBuilder();
        protobufMapExampleBuilder.putValue("test", "value");
        protobufMapExampleBuilder.putValue("foo", "bar");
        MapExample result = ProtoBeansMappingTest.mapper.map(protobufMapExampleBuilder, MapExample.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getValue());
        Assert.assertEquals(protobufMapExampleBuilder.getValueMap(), result.getValue());
    }

    @Test
    public void canSnakeCaseFieldFromProto() {
        ProtobufFieldNaming.Builder protobufFieldNamingBuilder = ProtobufFieldNaming.newBuilder();
        protobufFieldNamingBuilder.setSnakeCaseField("some value");
        ProtobufFieldNaming protobufFieldNaming = protobufFieldNamingBuilder.build();
        FieldNaming result = ProtoBeansMappingTest.mapper.map(protobufFieldNaming, FieldNaming.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSnakeCaseField());
        Assert.assertEquals(protobufFieldNaming.getSnakeCaseField(), result.getSnakeCaseField());
    }

    @Test
    public void canSnakeCaseFieldToProto() {
        FieldNaming fieldNaming = new FieldNaming();
        fieldNaming.setSnakeCaseField("some value");
        ProtobufFieldNaming result = ProtoBeansMappingTest.mapper.map(fieldNaming, ProtobufFieldNaming.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSnakeCaseField());
        Assert.assertEquals(fieldNaming.getSnakeCaseField(), result.getSnakeCaseField());
    }
}

