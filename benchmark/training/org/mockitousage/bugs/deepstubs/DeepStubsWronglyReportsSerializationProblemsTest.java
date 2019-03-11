/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.deepstubs;


import org.junit.Test;
import org.mockito.Mockito;


/**
 * In GH issue 99 : https://github.com/mockito/mockito/issues/99
 */
public class DeepStubsWronglyReportsSerializationProblemsTest {
    @Test
    public void should_not_raise_a_mockito_exception_about_serialization_when_accessing_deep_stub() {
        DeepStubsWronglyReportsSerializationProblemsTest.NotSerializableShouldBeMocked the_deep_stub = Mockito.mock(DeepStubsWronglyReportsSerializationProblemsTest.ToBeDeepStubbed.class, Mockito.RETURNS_DEEP_STUBS).getSomething();
        assertThat(the_deep_stub).isNotNull();
    }

    public static class ToBeDeepStubbed {
        public ToBeDeepStubbed() {
        }

        public DeepStubsWronglyReportsSerializationProblemsTest.NotSerializableShouldBeMocked getSomething() {
            return null;
        }
    }

    public static class NotSerializableShouldBeMocked {
        NotSerializableShouldBeMocked(String mandatory_param) {
        }
    }
}

