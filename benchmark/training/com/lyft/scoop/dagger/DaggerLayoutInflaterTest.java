package com.lyft.scoop.dagger;


import dagger.ObjectGraph;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DaggerLayoutInflaterTest {
    private static final String TEST_STRING = "testString";

    @Mock
    DaggerInjector mockDaggerInjector;

    @Mock
    ObjectGraph mockObjectGraph;

    private DaggerInjector daggerInjector;

    @Test
    public void testInject() {
        daggerInjector.inject(DaggerLayoutInflaterTest.TEST_STRING);
        Mockito.verify(mockObjectGraph, Mockito.times(1)).inject(ArgumentMatchers.eq(DaggerLayoutInflaterTest.TEST_STRING));
    }

    @Test
    public void testGet() {
        Mockito.when(mockObjectGraph.get(ArgumentMatchers.eq(String.class))).thenReturn(DaggerLayoutInflaterTest.TEST_STRING);
        Assert.assertEquals(DaggerLayoutInflaterTest.TEST_STRING, daggerInjector.get(String.class));
    }

    @Test
    public void testExtend() {
        Mockito.when(mockObjectGraph.plus(ArgumentMatchers.eq(DaggerLayoutInflaterTest.TEST_STRING))).thenReturn(mockObjectGraph);
        Assert.assertNotNull(daggerInjector.extend(DaggerLayoutInflaterTest.TEST_STRING));
        Mockito.verify(mockObjectGraph, Mockito.times(1)).plus(ArgumentMatchers.eq(DaggerLayoutInflaterTest.TEST_STRING));
    }
}

