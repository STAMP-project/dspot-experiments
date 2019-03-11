package com.ctrip.framework.apollo.configservice.wrapper;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseInsensitiveMapWrapperTest {
    private CaseInsensitiveMapWrapper<Object> caseInsensitiveMapWrapper;

    @Mock
    private Map<String, Object> someMap;

    @Test
    public void testGet() throws Exception {
        String someKey = "someKey";
        Object someValue = Mockito.mock(Object.class);
        Mockito.when(someMap.get(someKey.toLowerCase())).thenReturn(someValue);
        Assert.assertEquals(someValue, caseInsensitiveMapWrapper.get(someKey));
        Mockito.verify(someMap, Mockito.times(1)).get(someKey.toLowerCase());
    }

    @Test
    public void testPut() throws Exception {
        String someKey = "someKey";
        Object someValue = Mockito.mock(Object.class);
        Object anotherValue = Mockito.mock(Object.class);
        Mockito.when(someMap.put(someKey.toLowerCase(), someValue)).thenReturn(anotherValue);
        Assert.assertEquals(anotherValue, caseInsensitiveMapWrapper.put(someKey, someValue));
        Mockito.verify(someMap, Mockito.times(1)).put(someKey.toLowerCase(), someValue);
    }

    @Test
    public void testRemove() throws Exception {
        String someKey = "someKey";
        Object someValue = Mockito.mock(Object.class);
        Mockito.when(someMap.remove(someKey.toLowerCase())).thenReturn(someValue);
        Assert.assertEquals(someValue, caseInsensitiveMapWrapper.remove(someKey));
        Mockito.verify(someMap, Mockito.times(1)).remove(someKey.toLowerCase());
    }
}

