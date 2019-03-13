package com.vaadin.tests.server.components;


import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;
import org.easymock.EasyMock;
import org.junit.Test;


public class WindowTest {
    private Window window;

    @Test
    public void testCloseListener() {
        CloseListener cl = EasyMock.createMock(CloseListener.class);
        // Expectations
        cl.windowClose(EasyMock.isA(CloseEvent.class));
        // Start actual test
        EasyMock.replay(cl);
        // Add listener and send a close event -> should end up in listener once
        Registration windowCloseListenerRegistration = window.addCloseListener(cl);
        WindowTest.sendClose(window);
        // Ensure listener was called once
        EasyMock.verify(cl);
        // Remove the listener and send close event -> should not end up in
        // listener
        windowCloseListenerRegistration.remove();
        WindowTest.sendClose(window);
        // Ensure listener still has been called only once
        EasyMock.verify(cl);
    }

    @Test
    public void testResizeListener() {
        ResizeListener rl = EasyMock.createMock(ResizeListener.class);
        // Expectations
        rl.windowResized(EasyMock.isA(ResizeEvent.class));
        // Start actual test
        EasyMock.replay(rl);
        // Add listener and send a resize event -> should end up in listener
        // once
        Registration windowResizeListenerRegistration = window.addResizeListener(rl);
        sendResize(window);
        // Ensure listener was called once
        EasyMock.verify(rl);
        // Remove the listener and send close event -> should not end up in
        // listener
        windowResizeListenerRegistration.remove();
        sendResize(window);
        // Ensure listener still has been called only once
        EasyMock.verify(rl);
    }
}

