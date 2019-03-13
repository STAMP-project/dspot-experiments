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
package org.apache.flink.api.java.typeutils.runtime.kryo;


import java.util.ArrayList;
import java.util.HashSet;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.core.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class SerializersTest {
    // recursive
    public static class Node {
        private SerializersTest.Node parent;
    }

    public static class FromNested {
        SerializersTest.Node recurseMe;
    }

    public static class FromGeneric1 {}

    public static class FromGeneric2 {}

    public static class Nested1 {
        private SerializersTest.FromNested fromNested;

        private Path yodaInterval;
    }

    public static class ClassWithNested {
        SerializersTest.Nested1 nested;

        int ab;

        ArrayList<SerializersTest.FromGeneric1> addGenType;

        SerializersTest.FromGeneric2[] genericArrayType;
    }

    @Test
    public void testTypeRegistration() {
        ExecutionConfig conf = new ExecutionConfig();
        Serializers.recursivelyRegisterType(SerializersTest.ClassWithNested.class, conf, new HashSet<Class<?>>());
        KryoSerializer<String> kryo = new KryoSerializer(String.class, conf);// we create Kryo from another type.

        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromNested.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.ClassWithNested.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(Path.class).getId()) > 0));
        // check if the generic type from one field is also registered (its very likely that
        // generic types are also used as fields somewhere.
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromGeneric1.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromGeneric2.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.Node.class).getId()) > 0));
        // register again and make sure classes are still registered
        ExecutionConfig conf2 = new ExecutionConfig();
        Serializers.recursivelyRegisterType(SerializersTest.ClassWithNested.class, conf2, new HashSet<Class<?>>());
        KryoSerializer<String> kryo2 = new KryoSerializer(String.class, conf);
        Assert.assertTrue(((kryo2.getKryo().getRegistration(SerializersTest.FromNested.class).getId()) > 0));
    }

    @Test
    public void testTypeRegistrationFromTypeInfo() {
        ExecutionConfig conf = new ExecutionConfig();
        Serializers.recursivelyRegisterType(new org.apache.flink.api.java.typeutils.GenericTypeInfo(SerializersTest.ClassWithNested.class), conf, new HashSet<Class<?>>());
        KryoSerializer<String> kryo = new KryoSerializer(String.class, conf);// we create Kryo from another type.

        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromNested.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.ClassWithNested.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(Path.class).getId()) > 0));
        // check if the generic type from one field is also registered (its very likely that
        // generic types are also used as fields somewhere.
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromGeneric1.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.FromGeneric2.class).getId()) > 0));
        Assert.assertTrue(((kryo.getKryo().getRegistration(SerializersTest.Node.class).getId()) > 0));
    }
}

