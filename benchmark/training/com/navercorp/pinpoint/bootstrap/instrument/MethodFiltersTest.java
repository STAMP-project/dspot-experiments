package com.navercorp.pinpoint.bootstrap.instrument;


import java.lang.reflect.Modifier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MethodFiltersTest {
    @Test
    public void name() {
        InstrumentMethod method = Mockito.mock(InstrumentMethod.class);
        Mockito.when(method.getName()).thenReturn("call");
        Assert.assertTrue(MethodFilters.name("call").accept(method));
        Assert.assertFalse(MethodFilters.name("execute").accept(method));
        Assert.assertFalse(MethodFilters.name().accept(method));
        Assert.assertFalse(MethodFilters.name(((String[]) (null))).accept(method));
        Assert.assertFalse(MethodFilters.name(null, null).accept(method));
    }

    @Test
    public void modifier() {
        InstrumentMethod method = Mockito.mock(InstrumentMethod.class);
        // modifier is public abstract.
        Mockito.when(method.getModifiers()).thenReturn(1025);
        Assert.assertTrue(MethodFilters.modifier(Modifier.PUBLIC).accept(method));
        Assert.assertTrue(MethodFilters.modifier(Modifier.ABSTRACT).accept(method));
        Assert.assertFalse(MethodFilters.modifier(Modifier.FINAL).accept(method));
    }
}

