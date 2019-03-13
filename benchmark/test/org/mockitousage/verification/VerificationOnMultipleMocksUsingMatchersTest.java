/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.verification;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


@SuppressWarnings("unchecked")
public class VerificationOnMultipleMocksUsingMatchersTest extends TestBase {
    @Test
    public void shouldVerifyUsingMatchers() throws Exception {
        List<Object> list = Mockito.mock(List.class);
        HashMap<Object, Object> map = Mockito.mock(HashMap.class);
        list.add("test");
        list.add(1, "test two");
        map.put("test", 100);
        map.put("test two", 200);
        Mockito.verify(list).add(ArgumentMatchers.anyObject());
        Mockito.verify(list).add(ArgumentMatchers.anyInt(), ArgumentMatchers.eq("test two"));
        Mockito.verify(map, Mockito.times(2)).put(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        Mockito.verify(map).put(ArgumentMatchers.eq("test two"), ArgumentMatchers.eq(200));
        Mockito.verifyNoMoreInteractions(list, map);
    }

    @Test
    public void shouldVerifyMultipleMocks() throws Exception {
        List<String> list = Mockito.mock(List.class);
        Map<Object, Integer> map = Mockito.mock(Map.class);
        Set<?> set = Mockito.mock(Set.class);
        list.add("one");
        list.add("one");
        list.add("two");
        map.put("one", 1);
        map.put("one", 1);
        Mockito.verify(list, Mockito.times(2)).add("one");
        Mockito.verify(list, Mockito.times(1)).add("two");
        Mockito.verify(list, Mockito.times(0)).add("three");
        Mockito.verify(map, Mockito.times(2)).put(ArgumentMatchers.anyObject(), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(list, map);
        Mockito.verifyZeroInteractions(set);
    }
}

