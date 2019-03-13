package com.vaadin.ui;


import com.vaadin.server.ServerRpcManager;
import com.vaadin.shared.data.selection.SelectionServerRpc;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class RadioButtonGroupTest {
    private RadioButtonGroup<String> radioButtonGroup;

    @Test
    public void apiSelectionChange_notUserOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);
        radioButtonGroup.addSelectionListener(( event) -> {
            listenerCount.incrementAndGet();
            assertFalse(event.isUserOriginated());
        });
        radioButtonGroup.setValue("First");
        radioButtonGroup.setValue("Second");
        radioButtonGroup.setValue(null);
        radioButtonGroup.setValue(null);
        Assert.assertEquals(3, listenerCount.get());
    }

    @Test
    public void rpcSelectionChange_userOriginated() {
        AtomicInteger listenerCount = new AtomicInteger(0);
        radioButtonGroup.addSelectionListener(( event) -> {
            listenerCount.incrementAndGet();
            assertTrue(event.isUserOriginated());
        });
        SelectionServerRpc rpc = ServerRpcManager.getRpcProxy(radioButtonGroup, SelectionServerRpc.class);
        rpc.select(getItemKey("First"));
        rpc.select(getItemKey("Second"));
        rpc.deselect(getItemKey("Second"));
        Assert.assertEquals(3, listenerCount.get());
    }
}

