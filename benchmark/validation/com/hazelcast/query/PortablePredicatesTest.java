/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.nio.serialization.FieldType;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PortablePredicatesTest {
    private static final short FACTORY_ID = 1;

    private final InternalSerializationService serializationService = new DefaultSerializationServiceBuilder().addPortableFactory(PortablePredicatesTest.FACTORY_ID, new PortablePredicatesTest.TestPortableFactory()).build();

    @Test
    public void testPortablePredicate() {
        PortablePredicatesTest.PortableData data = createData("1", "Clark", "Kent", "Superman", 100);
        Assert.assertTrue(new SqlPredicate("strength >= 75").apply(toQueryEntry("1", data)));
        Assert.assertTrue(new SqlPredicate("firstName like C% and lastName like K%").apply(toQueryEntry("1", data)));
        Assert.assertFalse(new SqlPredicate("character == 'Bizarro'").apply(toQueryEntry("1", data)));
    }

    class TestPortableFactory implements PortableFactory {
        @Override
        public Portable create(int classId) {
            if ((PortablePredicatesTest.PortableData.CLASS_ID) == classId) {
                return new PortablePredicatesTest.PortableData();
            } else {
                return null;
            }
        }
    }

    private static class PortableData implements Portable {
        public static final int CLASS_ID = 1;

        private final Map<String, Object> data = new HashMap<String, Object>();

        @Override
        public int getClassId() {
            return PortablePredicatesTest.PortableData.CLASS_ID;
        }

        @Override
        public int getFactoryId() {
            return 1;
        }

        public void put(String key, Object value) {
            data.put(key, value);
        }

        public Object get(String key) {
            return data.get(key);
        }

        @Override
        public void readPortable(PortableReader reader) throws IOException {
            Set<String> fieldNames = reader.getFieldNames();
            for (String fieldName : fieldNames) {
                FieldType fieldType = reader.getFieldType(fieldName);
                switch (fieldType) {
                    case UTF :
                        data.put(fieldName, reader.readUTF(fieldName));
                        break;
                    case LONG :
                        data.put(fieldName, reader.readLong(fieldName));
                        break;
                    case BOOLEAN :
                        data.put(fieldName, reader.readBoolean(fieldName));
                        break;
                    default :
                        throw new IOException(("Unsupported field type " + fieldType));
                }
            }
        }

        @Override
        public void writePortable(PortableWriter writer) throws IOException {
            for (String key : data.keySet()) {
                Object object = data.get(key);
                if (object instanceof String) {
                    writer.writeUTF(key, ((String) (object)));
                } else
                    if (object instanceof Long) {
                        writer.writeLong(key, ((Long) (object)));
                    } else
                        if (object instanceof Date) {
                            writer.writeLong(key, ((Date) (object)).getTime());
                        } else
                            if (object instanceof Boolean) {
                                writer.writeBoolean(key, ((Boolean) (object)));
                            } else {
                                throw new IOException(("Unsupported field type " + (object.getClass())));
                            }



            }
        }
    }
}

