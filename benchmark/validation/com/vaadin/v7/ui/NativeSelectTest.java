package com.vaadin.v7.ui;


import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.shared.ui.select.AbstractSelectState;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class NativeSelectTest {
    @Test
    public void rpcRegisteredConstructorNoArg() {
        assertFocusRpcRegistered(new NativeSelect());
    }

    @Test
    public void rpcRegisteredConstructorString() {
        assertFocusRpcRegistered(new NativeSelect("foo"));
    }

    @Test
    public void rpcRegisteredConstructorStringCollection() {
        assertFocusRpcRegistered(new NativeSelect("foo", Collections.singleton("Hello")));
    }

    @Test
    public void rpcRegisteredConstructorStringContainer() {
        assertFocusRpcRegistered(new NativeSelect("foo", new IndexedContainer()));
    }

    @Test
    public void getState_listSelectHasCustomState() {
        NativeSelectTest.TestNativeSelect select = new NativeSelectTest.TestNativeSelect();
        AbstractSelectState state = select.getState();
        Assert.assertEquals("Unexpected state class", AbstractSelectState.class, state.getClass());
    }

    private static class TestNativeSelect extends NativeSelect {
        @Override
        public AbstractSelectState getState() {
            return super.getState();
        }
    }
}

