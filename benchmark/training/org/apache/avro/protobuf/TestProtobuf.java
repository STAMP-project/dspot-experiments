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
package org.apache.avro.protobuf;


import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificData;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.avro.protobuf.Test.A.X;
import static org.apache.avro.protobuf.Test.A.Y;
import static org.apache.avro.protobuf.Test.Foo.newBuilder;


public class TestProtobuf {
    @Test
    public void testMessage() throws Exception {
        System.out.println(ProtobufData.get().getSchema(Test.Foo.class).toString(true));
        Test.Foo.Builder builder = Test.Foo.newBuilder();
        builder.setInt32(0);
        builder.setInt64(2);
        builder.setUint32(3);
        builder.setUint64(4);
        builder.setSint32(5);
        builder.setSint64(6);
        builder.setFixed32(7);
        builder.setFixed64(8);
        builder.setSfixed32(9);
        builder.setSfixed64(10);
        builder.setFloat(1.0F);
        builder.setDouble(2.0);
        builder.setBool(true);
        builder.setString("foo");
        builder.setBytes(ByteString.copyFromUtf8("bar"));
        builder.setEnum(X);
        builder.addIntArray(27);
        builder.addSyms(Y);
        Test.Foo fooInner = builder.build();
        Test.Foo fooInArray = builder.build();
        builder = newBuilder(fooInArray);
        builder.addFooArray(fooInArray);
        builder = newBuilder(fooInner);
        builder.setFoo(fooInner);
        Test.Foo foo = builder.build();
        System.out.println(foo);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        ProtobufDatumWriter<Test.Foo> w = new ProtobufDatumWriter(Test.Foo.class);
        Encoder e = EncoderFactory.get().binaryEncoder(bao, null);
        w.write(foo, e);
        e.flush();
        Object o = new ProtobufDatumReader(Test.Foo.class).read(null, DecoderFactory.get().createBinaryDecoder(new ByteArrayInputStream(bao.toByteArray()), null));
        Assert.assertEquals(foo, o);
    }

    @Test
    public void testNestedEnum() throws Exception {
        Schema s = ProtobufData.get().getSchema(Test.M.N.class);
        Assert.assertEquals(Test.M.N.class.getName(), SpecificData.get().getClass(s).getName());
    }

    @Test
    public void testNestedClassNamespace() throws Exception {
        Schema s = ProtobufData.get().getSchema(Test.Foo.class);
        Assert.assertEquals(Test.class.getName(), s.getNamespace());
    }
}

