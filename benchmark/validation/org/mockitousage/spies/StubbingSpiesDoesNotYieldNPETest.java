/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.spies;


import java.util.Collection;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


public class StubbingSpiesDoesNotYieldNPETest extends TestBase {
    class Foo {
        public int len(String text) {
            return text.length();
        }

        public int size(Map<?, ?> map) {
            return map.size();
        }

        public int size(Collection<?> collection) {
            return collection.size();
        }
    }

    @Test
    public void shouldNotThrowNPE() throws Exception {
        StubbingSpiesDoesNotYieldNPETest.Foo foo = new StubbingSpiesDoesNotYieldNPETest.Foo();
        StubbingSpiesDoesNotYieldNPETest.Foo spy = Mockito.spy(foo);
        spy.len(ArgumentMatchers.anyString());
        spy.size(ArgumentMatchers.anyMap());
        spy.size(ArgumentMatchers.anyList());
        spy.size(ArgumentMatchers.anyCollection());
        spy.size(ArgumentMatchers.anySet());
    }
}

