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
package org.apache.flink.formats.avro;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.flink.api.common.ExecutionConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that the set of Kryo registrations is the same across compatible
 * Flink versions.
 *
 * <p>Special version of {@code KryoSerializerRegistrationsTest} that sits in the Avro module
 * and verifies that we correctly register Avro types at the {@link KryoSerializer} when
 * Avro is present.
 */
public class AvroKryoSerializerRegistrationsTest {
    /**
     * Tests that the registered classes in Kryo did not change.
     *
     * <p>Once we have proper serializer versioning this test will become obsolete.
     * But currently a change in the serializers can break savepoint backwards
     * compatibility between Flink versions.
     */
    @Test
    public void testDefaultKryoRegisteredClassesDidNotChange() throws Exception {
        final Kryo kryo = new org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer(Integer.class, new ExecutionConfig()).getKryo();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("flink_11-kryo_registrations")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] split = line.split(",");
                final int tag = Integer.parseInt(split[0]);
                final String registeredClass = split[1];
                Registration registration = kryo.getRegistration(tag);
                if (registration == null) {
                    Assert.fail(String.format("Registration for %d = %s got lost", tag, registeredClass));
                } else
                    if (!(registeredClass.equals(registration.getType().getName()))) {
                        Assert.fail(String.format("Registration for %d = %s changed to %s", tag, registeredClass, registration.getType().getName()));
                    }

            } 
        }
    }
}

