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
package org.apache.avro.thrift;


import java.nio.ByteBuffer;
import java.util.Collections;
import org.apache.avro.thrift.test.E;
import org.apache.avro.thrift.test.FooOrBar;
import org.apache.avro.thrift.test.Nested;
import org.junit.Test;


public class TestThrift {
    @Test
    public void testStruct() throws Exception {
        System.out.println(ThriftData.get().getSchema(org.apache.avro.thrift.test.Test.class).toString(true));
        org.apache.avro.thrift.test.Test test = new org.apache.avro.thrift.test.Test();
        test.setBoolField(true);
        test.setByteField(((byte) (2)));
        test.setI16Field(((short) (3)));
        test.setI16OptionalField(((short) (14)));
        test.setI32Field(4);
        test.setI64Field(5L);
        test.setDoubleField(2.0);
        test.setStringField("foo");
        test.setBinaryField(ByteBuffer.wrap(new byte[]{ 0, -1 }));
        test.setMapField(Collections.singletonMap("x", 1));
        test.setListField(Collections.singletonList(7));
        test.setSetField(Collections.singleton(8));
        test.setEnumField(E.X);
        test.setStructField(new Nested(9));
        test.setFooOrBar(FooOrBar.foo("x"));
        System.out.println(test);
        check(test);
    }

    @Test
    public void testOptionals() throws Exception {
        org.apache.avro.thrift.test.Test test = new org.apache.avro.thrift.test.Test();
        test.setBoolField(true);
        test.setByteField(((byte) (2)));
        test.setByteOptionalField(((byte) (4)));
        test.setI16Field(((short) (3)));
        test.setI16OptionalField(((short) (15)));
        test.setI64Field(5L);
        test.setDoubleField(2.0);
        System.out.println(test);
        check(test);
    }
}

