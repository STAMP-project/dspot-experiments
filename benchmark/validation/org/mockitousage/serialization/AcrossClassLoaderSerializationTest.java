/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.serialization;


import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.mock.SerializableMode;
import org.mockitousage.IMethods;
import org.mockitoutil.SimpleSerializationUtil;


public class AcrossClassLoaderSerializationTest {
    public IMethods mock;

    @Test
    public void check_that_mock_can_be_serialized_in_a_classloader_and_deserialized_in_another() throws Exception {
        byte[] bytes = create_mock_and_serialize_it_in_class_loader_A();
        Object the_deserialized_mock = read_stream_and_deserialize_it_in_class_loader_B(bytes);
        assertThat(the_deserialized_mock.getClass().getName()).startsWith("org.mockito.codegen.AClassToBeMockedInThisTestOnlyAndInCallablesOnly");
    }

    // see create_mock_and_serialize_it_in_class_loader_A
    public static class CreateMockAndSerializeIt implements Callable<byte[]> {
        public byte[] call() throws Exception {
            AcrossClassLoaderSerializationTest.AClassToBeMockedInThisTestOnlyAndInCallablesOnly mock = Mockito.mock(AcrossClassLoaderSerializationTest.AClassToBeMockedInThisTestOnlyAndInCallablesOnly.class, Mockito.withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS));
            // use MethodProxy before
            mock.returningSomething();
            return SimpleSerializationUtil.serializeMock(mock).toByteArray();
        }
    }

    // see read_stream_and_deserialize_it_in_class_loader_B
    public static class ReadStreamAndDeserializeIt implements Callable<Object> {
        private byte[] bytes;

        public ReadStreamAndDeserializeIt(byte[] bytes) {
            this.bytes = bytes;
        }

        public Object call() throws Exception {
            ByteArrayInputStream to_unserialize = new ByteArrayInputStream(bytes);
            return SimpleSerializationUtil.deserializeMock(to_unserialize, AcrossClassLoaderSerializationTest.AClassToBeMockedInThisTestOnlyAndInCallablesOnly.class);
        }
    }

    public static class AClassToBeMockedInThisTestOnlyAndInCallablesOnly {
        List returningSomething() {
            return Collections.emptyList();
        }
    }
}

