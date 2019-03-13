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
package org.apache.beam.sdk.coders;


import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import org.apache.beam.sdk.coders.DefaultCoder.DefaultCoderProviderRegistrar.DefaultCoderProvider;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DefaultCoder}.
 */
@RunWith(JUnit4.class)
public class DefaultCoderTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DefaultCoder(AvroCoder.class)
    private static class AvroRecord {}

    private static class SerializableBase implements Serializable {}

    @DefaultCoder(SerializableCoder.class)
    private static class SerializableRecord extends DefaultCoderTest.SerializableBase {}

    @DefaultCoder(DefaultCoderTest.CustomSerializableCoder.class)
    private static class CustomRecord extends DefaultCoderTest.SerializableBase {}

    @DefaultCoder(DefaultCoderTest.OldCustomSerializableCoder.class)
    private static class OldCustomRecord extends DefaultCoderTest.SerializableBase {}

    private static class Unknown {}

    private static class CustomSerializableCoder extends SerializableCoder<DefaultCoderTest.CustomRecord> {
        // Extending SerializableCoder isn't trivial, but it can be done.
        @SuppressWarnings("unchecked")
        public static <T extends Serializable> SerializableCoder<T> of(TypeDescriptor<T> recordType) {
            checkArgument(recordType.isSupertypeOf(new TypeDescriptor<DefaultCoderTest.CustomRecord>() {}));
            return ((SerializableCoder<T>) (new DefaultCoderTest.CustomSerializableCoder()));
        }

        protected CustomSerializableCoder() {
            super(DefaultCoderTest.CustomRecord.class, TypeDescriptor.of(DefaultCoderTest.CustomRecord.class));
        }

        @SuppressWarnings("unused")
        public static CoderProvider getCoderProvider() {
            return new CoderProvider() {
                @Override
                public <T> Coder<T> coderFor(TypeDescriptor<T> typeDescriptor, List<? extends Coder<?>> componentCoders) throws CannotProvideCoderException {
                    return DefaultCoderTest.CustomSerializableCoder.of(((TypeDescriptor) (typeDescriptor)));
                }
            };
        }
    }

    private static class OldCustomSerializableCoder extends SerializableCoder<DefaultCoderTest.OldCustomRecord> {
        // Extending SerializableCoder isn't trivial, but it can be done.
        // Old form using a Class.
        @SuppressWarnings("unchecked")
        public static <T extends Serializable> SerializableCoder<T> of(Class<T> recordType) {
            checkArgument(DefaultCoderTest.OldCustomRecord.class.isAssignableFrom(recordType));
            return ((SerializableCoder<T>) (new DefaultCoderTest.OldCustomSerializableCoder()));
        }

        protected OldCustomSerializableCoder() {
            super(DefaultCoderTest.OldCustomRecord.class, TypeDescriptor.of(DefaultCoderTest.OldCustomRecord.class));
        }

        @SuppressWarnings("unused")
        public static CoderProvider getCoderProvider() {
            return new CoderProvider() {
                @Override
                public <T> Coder<T> coderFor(TypeDescriptor<T> typeDescriptor, List<? extends Coder<?>> componentCoders) throws CannotProvideCoderException {
                    return DefaultCoderTest.OldCustomSerializableCoder.of(((Class) (typeDescriptor.getRawType())));
                }
            };
        }
    }

    @Test
    public void testCodersWithoutComponents() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        registry.registerCoderProvider(new DefaultCoderProvider());
        Assert.assertThat(registry.getCoder(DefaultCoderTest.AvroRecord.class), Matchers.instanceOf(AvroCoder.class));
        Assert.assertThat(registry.getCoder(DefaultCoderTest.SerializableRecord.class), Matchers.instanceOf(SerializableCoder.class));
        Assert.assertThat(registry.getCoder(DefaultCoderTest.CustomRecord.class), Matchers.instanceOf(DefaultCoderTest.CustomSerializableCoder.class));
        Assert.assertThat(registry.getCoder(DefaultCoderTest.OldCustomRecord.class), Matchers.instanceOf(DefaultCoderTest.OldCustomSerializableCoder.class));
    }

    @Test
    public void testDefaultCoderInCollection() throws Exception {
        CoderRegistry registry = CoderRegistry.createDefault();
        registry.registerCoderProvider(new DefaultCoderProvider());
        Coder<List<DefaultCoderTest.AvroRecord>> avroRecordCoder = registry.getCoder(new TypeDescriptor<List<DefaultCoderTest.AvroRecord>>() {});
        Assert.assertThat(avroRecordCoder, Matchers.instanceOf(ListCoder.class));
        Assert.assertThat(getElemCoder(), Matchers.instanceOf(AvroCoder.class));
        Assert.assertThat(registry.getCoder(new TypeDescriptor<List<DefaultCoderTest.SerializableRecord>>() {}), Matchers.equalTo(ListCoder.of(SerializableCoder.of(DefaultCoderTest.SerializableRecord.class))));
    }

    @Test
    public void testUnknown() throws Exception {
        thrown.expect(CannotProvideCoderException.class);
        new DefaultCoderProvider().coderFor(TypeDescriptor.of(DefaultCoderTest.Unknown.class), Collections.emptyList());
    }
}

