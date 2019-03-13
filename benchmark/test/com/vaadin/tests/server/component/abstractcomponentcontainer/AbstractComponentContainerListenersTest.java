package com.vaadin.tests.server.component.abstractcomponentcontainer;


import com.vaadin.tests.server.component.AbstractListenerMethodsTestBase;
import com.vaadin.ui.HasComponents.ComponentAttachEvent;
import com.vaadin.ui.HasComponents.ComponentAttachListener;
import com.vaadin.ui.HasComponents.ComponentDetachEvent;
import com.vaadin.ui.HasComponents.ComponentDetachListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import org.junit.Test;


public class AbstractComponentContainerListenersTest extends AbstractListenerMethodsTestBase {
    @Test
    public void testComponentDetachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(HorizontalLayout.class, ComponentDetachEvent.class, ComponentDetachListener.class);
    }

    @Test
    public void testComponentAttachListenerAddGetRemove() throws Exception {
        testListenerAddGetRemove(VerticalLayout.class, ComponentAttachEvent.class, ComponentAttachListener.class);
    }
}

