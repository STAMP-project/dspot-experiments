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


import SimpleProtoTestObject.Builder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.protobuf.vo.proto.LiteTestObject;
import com.github.dozermapper.protobuf.vo.proto.LiteTestObjectContainer;
import com.github.dozermapper.protobuf.vo.proto.ObjectWithCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtoTestObjectWithNestedProtoObject;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollection;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.ProtobufWithSimpleCollectionContainer;
import com.github.dozermapper.protobuf.vo.proto.ProtoTestObjects.SimpleProtoTestObject;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class ProtoBeansDeepMappingTest {
    private static Mapper mapper;

    @Test
    public void canSrcCopySimpleOneLevelField() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder.setOne("smthOne");
        ProtoTestObjectWithNestedProtoObject.Builder protoTestObjectWithNestedProtoObjectBuilder = ProtoTestObjectWithNestedProtoObject.newBuilder();
        protoTestObjectWithNestedProtoObjectBuilder.setNestedObject(simpleProtoTestObjectBuilder);
        protoTestObjectWithNestedProtoObjectBuilder.setOne("smthAnother-neverMind");
        ProtoTestObjectWithNestedProtoObject protoTestObjectWithNestedProtoObject = protoTestObjectWithNestedProtoObjectBuilder.build();
        LiteTestObject result = ProtoBeansDeepMappingTest.mapper.map(protoTestObjectWithNestedProtoObject, LiteTestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals(protoTestObjectWithNestedProtoObject.getNestedObject().getOne(), result.getOne());
    }

    @Test
    public void canSrcCopyFieldFromListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");
        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));
        ProtobufWithSimpleCollection protobufWithSimpleCollection = protobufWithSimpleCollectionBuilder.build();
        LiteTestObject result = ProtoBeansDeepMappingTest.mapper.map(protobufWithSimpleCollection, LiteTestObject.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getOne());
        Assert.assertEquals(protobufWithSimpleCollection.getObject(1).getOne(), result.getOne());
    }

    @Test
    public void canSrcCopyList() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");
        ProtobufWithSimpleCollection.Builder protoWithCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protoWithCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));
        ProtobufWithSimpleCollectionContainer.Builder protobufWithSimpleCollectionContainerBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
        protobufWithSimpleCollectionContainerBuilder.setObject(protoWithCollectionBuilder);
        ProtobufWithSimpleCollectionContainer src = protobufWithSimpleCollectionContainerBuilder.build();
        ObjectWithCollection result = ProtoBeansDeepMappingTest.mapper.map(src, ObjectWithCollection.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObjects());
        Assert.assertEquals(2, result.getObjects().size());
    }

    @Test
    public void canSrcCopyListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthAnother");
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthOne");
        ProtobufWithSimpleCollection.Builder builder = ProtobufWithSimpleCollection.newBuilder();
        builder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));
        ProtobufWithSimpleCollection protobufWithSimpleCollection = builder.build();
        LiteTestObjectContainer result = ProtoBeansDeepMappingTest.mapper.map(protobufWithSimpleCollection, LiteTestObjectContainer.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObject());
        Assert.assertNotNull(result.getObject().getOne());
        Assert.assertEquals(protobufWithSimpleCollection.getObject(1).getOne(), result.getObject().getOne());
    }

    @Test
    public void canSrcCopyDeepListElement() {
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder1 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder1.setOne("smthOne");
        SimpleProtoTestObject.Builder simpleProtoTestObjectBuilder2 = SimpleProtoTestObject.newBuilder();
        simpleProtoTestObjectBuilder2.setOne("smthAnother");
        ProtobufWithSimpleCollection.Builder protobufWithSimpleCollectionBuilder = ProtobufWithSimpleCollection.newBuilder();
        protobufWithSimpleCollectionBuilder.addAllObject(Arrays.asList(simpleProtoTestObjectBuilder1.build(), simpleProtoTestObjectBuilder2.build()));
        ProtobufWithSimpleCollectionContainer.Builder protobufWithSimpleCollectionContainerBuilder = ProtobufWithSimpleCollectionContainer.newBuilder();
        protobufWithSimpleCollectionContainerBuilder.setObject(protobufWithSimpleCollectionBuilder);
        ProtobufWithSimpleCollectionContainer protobufWithSimpleCollectionContainer = protobufWithSimpleCollectionContainerBuilder.build();
        LiteTestObjectContainer result = ProtoBeansDeepMappingTest.mapper.map(protobufWithSimpleCollectionContainer, LiteTestObjectContainer.class);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getObject().getOne());
        Assert.assertEquals(protobufWithSimpleCollectionContainer.getObject().getObject(0).getOne(), result.getObject().getOne());
    }
}

