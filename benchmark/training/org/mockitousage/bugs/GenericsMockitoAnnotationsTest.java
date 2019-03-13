/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mock;


/**
 * This was an issue reported in #1174.
 */
public class GenericsMockitoAnnotationsTest {
    @Mock
    private GenericsMockitoAnnotationsTest.TestCollectionSourceProvider testCollectionSourceProvider;

    @Test
    public void should_not_throw_class_cast_exception() {
        BDDMockito.given(testCollectionSourceProvider.getCollection(new ArrayList<Integer>())).willReturn(new ArrayList<Integer>());
    }

    static class TestCollectionSourceProvider {
        <T extends Collection<E>, E> T getCollection(T collection) {
            return collection;
        }
    }
}

