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
package org.apache.beam.sdk.schemas;


import com.google.auto.service.AutoService;
import java.util.List;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.utils.TestJavaBeans;
import org.apache.beam.sdk.schemas.utils.TestPOJOs;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link SchemaRegistry}.
 */
public class SchemaRegistryTest {
    static final Schema EMPTY_SCHEMA = Schema.builder().build();

    static final Schema STRING_SCHEMA = Schema.builder().addStringField("string").build();

    static final Schema INTEGER_SCHEMA = Schema.builder().addInt32Field("integer").build();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testRegisterForClass() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        registry.registerSchemaForClass(String.class, SchemaRegistryTest.STRING_SCHEMA, ( s) -> Row.withSchema(STRING_SCHEMA).addValue(s).build(), ( r) -> r.getString("string"));
        registry.registerSchemaForClass(Integer.class, SchemaRegistryTest.INTEGER_SCHEMA, ( s) -> Row.withSchema(INTEGER_SCHEMA).addValue(s).build(), ( r) -> r.getInt32("integer"));
        tryGetters(registry);
    }

    @Test
    public void testRegisterForType() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        registry.registerSchemaForType(TypeDescriptors.strings(), SchemaRegistryTest.STRING_SCHEMA, ( s) -> Row.withSchema(STRING_SCHEMA).addValue(s).build(), ( r) -> r.getString("string"));
        registry.registerSchemaForType(TypeDescriptors.integers(), SchemaRegistryTest.INTEGER_SCHEMA, ( s) -> Row.withSchema(INTEGER_SCHEMA).addValue(s).build(), ( r) -> r.getInt32("integer"));
        tryGetters(registry);
    }

    /**
     * A test SchemaProvider.
     */
    public static final class Provider implements SchemaProvider {
        @Override
        public <T> Schema schemaFor(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptors.strings())) {
                return SchemaRegistryTest.STRING_SCHEMA;
            } else
                if (typeDescriptor.equals(TypeDescriptors.integers())) {
                    return SchemaRegistryTest.INTEGER_SCHEMA;
                } else {
                    return null;
                }

        }

        @Override
        public <T> SerializableFunction<T, Row> toRowFunction(TypeDescriptor<T> typeDescriptor) {
            return ( v) -> Row.withSchema(schemaFor(typeDescriptor)).addValue(v).build();
        }

        @Override
        public <T> SerializableFunction<Row, T> fromRowFunction(TypeDescriptor<T> typeDescriptor) {
            return ( r) -> r.getValue(0);
        }
    }

    @Test
    public void testRegisterProvider() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        registry.registerSchemaProvider(new SchemaRegistryTest.Provider());
        tryGetters(registry);
    }

    static class TestSchemaClass {}

    static final class TestAutoProvider implements SchemaProvider {
        @Override
        public <T> Schema schemaFor(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestSchemaClass.class))) {
                return SchemaRegistryTest.EMPTY_SCHEMA;
            }
            return null;
        }

        @Override
        public <T> SerializableFunction<T, Row> toRowFunction(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestSchemaClass.class))) {
                return ( v) -> Row.withSchema(org.apache.beam.sdk.schemas.EMPTY_SCHEMA).build();
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> SerializableFunction<Row, T> fromRowFunction(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestSchemaClass.class))) {
                return ( r) -> ((T) (new org.apache.beam.sdk.schemas.TestSchemaClass()));
            }
            return null;
        }
    }

    /**
     * A @link SchemaProviderRegistrar} for testing.
     */
    @AutoService(SchemaProviderRegistrar.class)
    public static class TestSchemaProviderRegistrar implements SchemaProviderRegistrar {
        @Override
        public List<SchemaProvider> getSchemaProviders() {
            return ImmutableList.of(new SchemaRegistryTest.TestAutoProvider());
        }
    }

    @Test
    public void testAutoSchemaProvider() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Assert.assertEquals(SchemaRegistryTest.EMPTY_SCHEMA, registry.getSchema(SchemaRegistryTest.TestSchemaClass.class));
    }

    @DefaultSchema(SchemaRegistryTest.TestDefaultSchemaProvider.class)
    static class TestDefaultSchemaClass {}

    /**
     * A test schema provider.
     */
    public static final class TestDefaultSchemaProvider implements SchemaProvider {
        @Override
        public <T> Schema schemaFor(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestDefaultSchemaClass.class))) {
                return SchemaRegistryTest.EMPTY_SCHEMA;
            }
            return null;
        }

        @Override
        public <T> SerializableFunction<T, Row> toRowFunction(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestDefaultSchemaClass.class))) {
                return ( v) -> Row.withSchema(org.apache.beam.sdk.schemas.EMPTY_SCHEMA).build();
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> SerializableFunction<Row, T> fromRowFunction(TypeDescriptor<T> typeDescriptor) {
            if (typeDescriptor.equals(TypeDescriptor.of(SchemaRegistryTest.TestDefaultSchemaClass.class))) {
                return ( r) -> ((T) (new org.apache.beam.sdk.schemas.TestSchemaClass()));
            }
            return null;
        }
    }

    @Test
    public void testDefaultSchemaProvider() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Assert.assertEquals(SchemaRegistryTest.EMPTY_SCHEMA, registry.getSchema(SchemaRegistryTest.TestDefaultSchemaClass.class));
    }

    @Test
    public void testRegisterPojo() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        registry.registerPOJO(TestPOJOs.SimplePOJO.class);
        Schema schema = registry.getSchema(TestPOJOs.SimplePOJO.class);
        Assert.assertTrue(TestPOJOs.SIMPLE_POJO_SCHEMA.equivalent(schema));
    }

    @Test
    public void testRegisterJavaBean() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        registry.registerJavaBean(TestJavaBeans.SimpleBean.class);
        Schema schema = registry.getSchema(TestJavaBeans.SimpleBean.class);
        Assert.assertTrue(TestJavaBeans.SIMPLE_BEAN_SCHEMA.equivalent(schema));
    }
}

