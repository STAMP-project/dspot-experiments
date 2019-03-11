/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.serialization;


import java.io.Serializable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockitoutil.SimpleSerializationUtil;


@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class StrictStubsSerializableTest {
    @Mock(serializable = true)
    private StrictStubsSerializableTest.SampleClass sampleClass;

    @Test
    public void should_serialize_and_deserialize_mock_created_with_serializable_and_strict_stubs() throws Exception {
        // given
        Mockito.when(sampleClass.isFalse()).thenReturn(true);
        // when
        StrictStubsSerializableTest.SampleClass deserializedSample = SimpleSerializationUtil.serializeAndBack(sampleClass);
        // to satisfy strict stubbing
        deserializedSample.isFalse();
        Mockito.verify(deserializedSample).isFalse();
        Mockito.verify(sampleClass, Mockito.never()).isFalse();
        // then
        assertThat(deserializedSample.isFalse()).isEqualTo(true);
        assertThat(sampleClass.isFalse()).isEqualTo(true);
    }

    static class SampleClass implements Serializable {
        boolean isFalse() {
            return false;
        }
    }
}

