/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitoinline;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.junit.Test;
import org.mockito.Mockito;


public class RecursionTest {
    @Test
    public void testMockConcurrentHashMap() {
        ConcurrentMap<String, String> map = Mockito.spy(new ConcurrentHashMap<String, String>());
        map.putIfAbsent("a", "b");
    }
}

