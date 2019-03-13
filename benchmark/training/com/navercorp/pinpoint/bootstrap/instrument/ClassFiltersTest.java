package com.navercorp.pinpoint.bootstrap.instrument;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ClassFiltersTest {
    @Test
    public void name() {
        InstrumentClass clazz = Mockito.mock(InstrumentClass.class);
        Mockito.when(clazz.getName()).thenReturn("com.navercorp.mock.TestObjectNestedClass$InstanceInner");
        Assert.assertTrue(ClassFilters.name("com.navercorp.mock.TestObjectNestedClass$InstanceInner").accept(clazz));
        Assert.assertFalse(ClassFilters.name("com.navercorp.mock.InvalidClassName").accept(clazz));
        Assert.assertFalse(ClassFilters.name(((String[]) (null))).accept(clazz));
        Assert.assertFalse(ClassFilters.name(null, null).accept(clazz));
    }

    @Test
    public void enclosingMethod() {
        InstrumentClass clazz = Mockito.mock(InstrumentClass.class);
        Mockito.when(clazz.hasEnclosingMethod("call", "int")).thenReturn(Boolean.TRUE);
        Assert.assertTrue(ClassFilters.enclosingMethod("call", "int").accept(clazz));
        Assert.assertFalse(ClassFilters.enclosingMethod("invalid", "int").accept(clazz));
    }

    @Test
    public void interfaze() {
        InstrumentClass clazz = Mockito.mock(InstrumentClass.class);
        Mockito.when(clazz.getInterfaces()).thenReturn(new String[]{ "java.util.concurrent.Callable" });
        Assert.assertTrue(ClassFilters.interfaze("java.util.concurrent.Callable").accept(clazz));
        Assert.assertFalse(ClassFilters.interfaze("java.lang.Runnable").accept(clazz));
    }

    @Test
    public void chain() {
        InstrumentClass clazz = Mockito.mock(InstrumentClass.class);
        Mockito.when(clazz.hasEnclosingMethod("call", "int")).thenReturn(Boolean.TRUE);
        Mockito.when(clazz.getInterfaces()).thenReturn(new String[]{ "java.util.concurrent.Callable" });
        Assert.assertTrue(ClassFilters.chain(ClassFilters.enclosingMethod("call", "int"), ClassFilters.interfaze("java.util.concurrent.Callable")).accept(clazz));
        Assert.assertFalse(ClassFilters.chain(ClassFilters.enclosingMethod("invalid", "int"), ClassFilters.interfaze("java.lang.Runnable")).accept(clazz));
    }
}

