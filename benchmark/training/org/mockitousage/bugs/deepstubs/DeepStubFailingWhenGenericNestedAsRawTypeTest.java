/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs.deepstubs;


import org.junit.Test;
import org.mockito.Mockito;


public class DeepStubFailingWhenGenericNestedAsRawTypeTest {
    interface MyClass1<MC2 extends DeepStubFailingWhenGenericNestedAsRawTypeTest.MyClass2> {
        MC2 getNested();
    }

    interface MyClass2<MC3 extends DeepStubFailingWhenGenericNestedAsRawTypeTest.MyClass3> {
        MC3 getNested();
    }

    interface MyClass3 {
        String returnSomething();
    }

    @Test
    public void discoverDeepMockingOfGenerics() {
        DeepStubFailingWhenGenericNestedAsRawTypeTest.MyClass1 myMock1 = Mockito.mock(DeepStubFailingWhenGenericNestedAsRawTypeTest.MyClass1.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(myMock1.getNested().getNested().returnSomething()).thenReturn("Hello World.");
    }
}

