/**
 * Copyright (C) 2014 Google Inc.
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
package com.google.auto.value.gwt;


import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests that the generated GWT serializer for GwtValueType serializes fields in the expected way.
 *
 * @author emcmanus@google.com (?amonn McManus)
 */
@RunWith(JUnit4.class)
public class CustomFieldSerializerTest {
    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class ValueType implements Serializable {
        abstract String string();

        abstract int integer();

        @Nullable
        abstract CustomFieldSerializerTest.ValueType other();

        abstract List<CustomFieldSerializerTest.ValueType> others();

        static CustomFieldSerializerTest.ValueType create(String string, int integer, @Nullable
        CustomFieldSerializerTest.ValueType other) {
            return CustomFieldSerializerTest.ValueType.create(string, integer, other, Collections.<CustomFieldSerializerTest.ValueType>emptyList());
        }

        static CustomFieldSerializerTest.ValueType create(String string, int integer, @Nullable
        CustomFieldSerializerTest.ValueType other, List<CustomFieldSerializerTest.ValueType> others) {
            return new AutoValue_CustomFieldSerializerTest_ValueType(string, integer, other, others);
        }
    }

    private static final CustomFieldSerializerTest.ValueType SIMPLE = CustomFieldSerializerTest.ValueType.create("anotherstring", 1729, null);

    private static final CustomFieldSerializerTest.ValueType CONS = CustomFieldSerializerTest.ValueType.create("whatever", 1296, CustomFieldSerializerTest.SIMPLE);

    private static final CustomFieldSerializerTest.ValueType WITH_LIST = CustomFieldSerializerTest.ValueType.create("blim", 11881376, CustomFieldSerializerTest.SIMPLE, ImmutableList.of(CustomFieldSerializerTest.SIMPLE, CustomFieldSerializerTest.CONS));

    @Mock
    SerializationStreamWriter streamWriter;

    @Test
    public void testCustomFieldSerializer() throws SerializationException {
        AutoValue_CustomFieldSerializerTest_ValueType withList = ((AutoValue_CustomFieldSerializerTest_ValueType) (CustomFieldSerializerTest.WITH_LIST));
        AutoValue_CustomFieldSerializerTest_ValueType_CustomFieldSerializer.serialize(streamWriter, withList);
        Mockito.verify(streamWriter).writeString("blim");
        Mockito.verify(streamWriter).writeInt(11881376);
        Mockito.verify(streamWriter).writeObject(CustomFieldSerializerTest.SIMPLE);
        Mockito.verify(streamWriter).writeObject(ImmutableList.of(CustomFieldSerializerTest.SIMPLE, CustomFieldSerializerTest.CONS));
        Mockito.verifyNoMoreInteractions(streamWriter);
    }

    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class ValueTypeWithGetters implements Serializable {
        abstract String getPackage();

        abstract boolean isDefault();

        static CustomFieldSerializerTest.ValueTypeWithGetters create(String pkg, boolean dflt) {
            return new AutoValue_CustomFieldSerializerTest_ValueTypeWithGetters(pkg, dflt);
        }
    }

    @Test
    public void testCustomFieldSerializerWithGetters() throws SerializationException {
        AutoValue_CustomFieldSerializerTest_ValueTypeWithGetters instance = ((AutoValue_CustomFieldSerializerTest_ValueTypeWithGetters) (CustomFieldSerializerTest.ValueTypeWithGetters.create("package", true)));
        AutoValue_CustomFieldSerializerTest_ValueTypeWithGetters_CustomFieldSerializer.serialize(streamWriter, instance);
        Mockito.verify(streamWriter).writeString("package");
        Mockito.verify(streamWriter).writeBoolean(true);
        Mockito.verifyNoMoreInteractions(streamWriter);
    }

    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class GenericValueType<K extends Comparable<K>, V extends K> implements Serializable {
        abstract Map<K, V> map();

        static <K extends Comparable<K>, V extends K> CustomFieldSerializerTest.GenericValueType<K, V> create(Map<K, V> map) {
            return new AutoValue_CustomFieldSerializerTest_GenericValueType<K, V>(map);
        }
    }

    @Test
    public void testCustomFieldSerializerGeneric() throws SerializationException {
        Map<Integer, Integer> map = ImmutableMap.of(2, 2);
        AutoValue_CustomFieldSerializerTest_GenericValueType<Integer, Integer> instance = ((AutoValue_CustomFieldSerializerTest_GenericValueType<Integer, Integer>) (CustomFieldSerializerTest.GenericValueType.create(map)));
        AutoValue_CustomFieldSerializerTest_GenericValueType_CustomFieldSerializer.serialize(streamWriter, instance);
        Mockito.verify(streamWriter).writeObject(map);
        Mockito.verifyNoMoreInteractions(streamWriter);
    }

    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class ValueTypeWithBuilder implements Serializable {
        abstract String string();

        abstract ImmutableList<String> strings();

        static CustomFieldSerializerTest.ValueTypeWithBuilder.Builder builder() {
            return new AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilder.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            CustomFieldSerializerTest.ValueTypeWithBuilder.Builder string(String x);

            CustomFieldSerializerTest.ValueTypeWithBuilder.Builder strings(ImmutableList<String> x);

            CustomFieldSerializerTest.ValueTypeWithBuilder build();
        }
    }

    @Test
    public void testCustomFieldSerializerWithBuilder() throws SerializationException {
        AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilder instance = ((AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilder) (CustomFieldSerializerTest.ValueTypeWithBuilder.builder().string("s").strings(ImmutableList.of("a", "b")).build()));
        AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilder_CustomFieldSerializer.serialize(streamWriter, instance);
        Mockito.verify(streamWriter).writeString("s");
        Mockito.verify(streamWriter).writeObject(ImmutableList.of("a", "b"));
        Mockito.verifyNoMoreInteractions(streamWriter);
    }

    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class ValueTypeWithBuilderAndGetters implements Serializable {
        abstract String getPackage();

        abstract boolean isDefault();

        static CustomFieldSerializerTest.ValueTypeWithBuilderAndGetters.Builder builder() {
            return new AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilderAndGetters.Builder();
        }

        @AutoValue.Builder
        interface Builder {
            CustomFieldSerializerTest.ValueTypeWithBuilderAndGetters.Builder setPackage(String x);

            CustomFieldSerializerTest.ValueTypeWithBuilderAndGetters.Builder setDefault(boolean x);

            CustomFieldSerializerTest.ValueTypeWithBuilderAndGetters build();
        }
    }

    @Test
    public void testCustomFieldSerializerWithBuilderAndGetters() throws SerializationException {
        AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilderAndGetters instance = ((AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilderAndGetters) (CustomFieldSerializerTest.ValueTypeWithBuilderAndGetters.builder().setPackage("s").setDefault(false).build()));
        AutoValue_CustomFieldSerializerTest_ValueTypeWithBuilderAndGetters_CustomFieldSerializer.serialize(streamWriter, instance);
        Mockito.verify(streamWriter).writeString("s");
        Mockito.verify(streamWriter).writeBoolean(false);
        Mockito.verifyNoMoreInteractions(streamWriter);
    }

    @AutoValue
    @GwtCompatible(serializable = true)
    abstract static class GenericValueTypeWithBuilder<K extends Comparable<K>, V extends K> implements Serializable {
        abstract Map<K, V> map();

        static <K extends Comparable<K>, V extends K> CustomFieldSerializerTest.GenericValueTypeWithBuilder.Builder<K, V> builder() {
            return new AutoValue_CustomFieldSerializerTest_GenericValueTypeWithBuilder.Builder<K, V>();
        }

        @AutoValue.Builder
        interface Builder<K extends Comparable<K>, V extends K> {
            CustomFieldSerializerTest.GenericValueTypeWithBuilder.Builder<K, V> map(Map<K, V> map);

            CustomFieldSerializerTest.GenericValueTypeWithBuilder<K, V> build();
        }
    }

    @Test
    public void testCustomFieldSerializerGenericWithBuilder() throws SerializationException {
        Map<Integer, Integer> map = ImmutableMap.of(2, 2);
        AutoValue_CustomFieldSerializerTest_GenericValueTypeWithBuilder<Integer, Integer> instance = ((AutoValue_CustomFieldSerializerTest_GenericValueTypeWithBuilder<Integer, Integer>) (CustomFieldSerializerTest.GenericValueTypeWithBuilder.<Integer, Integer>builder().map(map).build()));
        AutoValue_CustomFieldSerializerTest_GenericValueTypeWithBuilder_CustomFieldSerializer.serialize(streamWriter, instance);
        Mockito.verify(streamWriter).writeObject(map);
        Mockito.verifyNoMoreInteractions(streamWriter);
    }
}

