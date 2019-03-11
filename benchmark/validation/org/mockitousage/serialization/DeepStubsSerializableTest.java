/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.serialization;


import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockitoutil.SimpleSerializationUtil;


public class DeepStubsSerializableTest {
    @Test
    public void should_serialize_and_deserialize_mock_created_with_deep_stubs() throws Exception {
        // given
        DeepStubsSerializableTest.SampleClass sampleClass = Mockito.mock(DeepStubsSerializableTest.SampleClass.class, Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS).serializable());
        Mockito.when(sampleClass.getSample().isFalse()).thenReturn(true);
        Mockito.when(sampleClass.getSample().number()).thenReturn(999);
        // when
        DeepStubsSerializableTest.SampleClass deserializedSample = SimpleSerializationUtil.serializeAndBack(sampleClass);
        // then
        assertThat(deserializedSample.getSample().isFalse()).isEqualTo(true);
        assertThat(deserializedSample.getSample().number()).isEqualTo(999);
    }

    @Test
    public void should_serialize_and_deserialize_parameterized_class_mocked_with_deep_stubs() throws Exception {
        // given
        DeepStubsSerializableTest.ListContainer deep_stubbed = Mockito.mock(DeepStubsSerializableTest.ListContainer.class, Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS).serializable());
        Mockito.when(deep_stubbed.iterator().next().add("yes")).thenReturn(true);
        // when
        DeepStubsSerializableTest.ListContainer deserialized_deep_stub = SimpleSerializationUtil.serializeAndBack(deep_stubbed);
        // then
        assertThat(deserialized_deep_stub.iterator().next().add("not stubbed but mock already previously resolved")).isEqualTo(false);
        assertThat(deserialized_deep_stub.iterator().next().add("yes")).isEqualTo(true);
    }

    @Test
    public void should_discard_generics_metadata_when_serialized_then_disabling_deep_stubs_with_generics() throws Exception {
        // given
        DeepStubsSerializableTest.ListContainer deep_stubbed = Mockito.mock(DeepStubsSerializableTest.ListContainer.class, Mockito.withSettings().defaultAnswer(Mockito.RETURNS_DEEP_STUBS).serializable());
        Mockito.when(deep_stubbed.iterator().hasNext()).thenReturn(true);
        DeepStubsSerializableTest.ListContainer deserialized_deep_stub = SimpleSerializationUtil.serializeAndBack(deep_stubbed);
        try {
            // when stubbing on a deserialized mock
            // then revert to the default RETURNS_DEEP_STUBS and the code will raise a ClassCastException
            Mockito.when(deserialized_deep_stub.iterator().next().get(42)).thenReturn("no");
            fail("Expected an exception to be thrown as deep stubs and serialization does not play well together");
        } catch (NullPointerException e) {
            assertThat(e).hasMessage(null);
        }
    }

    static class SampleClass implements Serializable {
        DeepStubsSerializableTest.SampleClass2 getSample() {
            return new DeepStubsSerializableTest.SampleClass2();
        }
    }

    static class SampleClass2 implements Serializable {
        boolean isFalse() {
            return false;
        }

        int number() {
            return 100;
        }
    }

    static class Container<E> implements Serializable , Iterable<E> {
        private E e;

        public Container(E e) {
            this.e = e;
        }

        public E get() {
            return e;
        }

        public Iterator<E> iterator() {
            return new Iterator<E>() {
                public boolean hasNext() {
                    return true;
                }

                public E next() {
                    return e;
                }

                public void remove() {
                }
            };
        }
    }

    static class ListContainer extends DeepStubsSerializableTest.Container<List<String>> {
        public ListContainer(List<String> list) {
            super(list);
        }
    }
}

