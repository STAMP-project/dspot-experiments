/**
 * @VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.server.clientconnector;


import com.vaadin.event.ConnectorEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import org.easymock.IArgumentMatcher;
import org.easymock.IMocksControl;
import org.junit.Test;


public class AttachDetachListenersTest {
    private IMocksControl control;

    private VaadinSession session;

    private UI ui;

    private Layout content;

    private Component component;

    AttachListener attachListener;

    DetachListener detachListener;

    @Test
    public void attachListeners_setSessionLast() {
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(component)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(content)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(ui)));
        control.replay();
        ui.addAttachListener(attachListener);
        content.addAttachListener(attachListener);
        component.addAttachListener(attachListener);
        ui.setContent(content);
        content.addComponent(component);
        ui.setSession(session);
        control.verify();
    }

    @Test
    public void attachListeners_setSessionFirst() {
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(ui)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(content)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(component)));
        control.replay();
        ui.addAttachListener(attachListener);
        content.addAttachListener(attachListener);
        component.addAttachListener(attachListener);
        ui.setSession(session);
        ui.setContent(content);
        content.addComponent(component);
        control.verify();
    }

    @Test
    public void attachListeners_setSessionBetween() {
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(content)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(ui)));
        attachListener.attach(AttachDetachListenersTest.eventEquals(new com.vaadin.server.ClientConnector.AttachEvent(component)));
        control.replay();
        ui.addAttachListener(attachListener);
        content.addAttachListener(attachListener);
        component.addAttachListener(attachListener);
        ui.setContent(content);
        ui.setSession(session);
        content.addComponent(component);
        control.verify();
    }

    @Test
    public void detachListeners_setSessionNull() {
        setupDetachListeners();
        ui.setContent(content);
        content.addComponent(component);
        ui.setSession(null);
        control.verify();
    }

    @Test
    public void detachListeners_removeComponent() {
        setupDetachListeners();
        ui.setContent(content);
        content.addComponent(component);
        content.removeAllComponents();
        ui.setSession(null);
        control.verify();
    }

    @Test
    public void detachListeners_setContentNull() {
        setupDetachListeners();
        ui.setContent(content);
        content.addComponent(component);
        ui.setContent(null);
        ui.setSession(null);
        control.verify();
    }

    public static class EventEquals<E extends ConnectorEvent> implements IArgumentMatcher {
        private final E expected;

        public EventEquals(E expected) {
            this.expected = expected;
        }

        @Override
        public void appendTo(StringBuffer buffer) {
            buffer.append("EventEquals(");
            buffer.append("expected ").append(expected.getClass().getSimpleName()).append(" with connector ").append(getConnector());
            buffer.append(')');
        }

        @Override
        public boolean matches(Object argument) {
            return (expected.getClass().isInstance(argument)) && ((getConnector()) == (getConnector()));
        }
    }
}

