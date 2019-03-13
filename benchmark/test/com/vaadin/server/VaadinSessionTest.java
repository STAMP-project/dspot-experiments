package com.vaadin.server;


import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class VaadinSessionTest implements Serializable {
    private transient VaadinSession session;

    private transient VaadinServlet mockServlet;

    private transient VaadinServletService mockService;

    private transient ServletConfig mockServletConfig;

    private transient HttpSession mockHttpSession;

    private transient WrappedSession mockWrappedSession;

    private transient VaadinServletRequest vaadinRequest;

    private transient UI ui;

    private transient Lock httpSessionLock;

    /**
     * This reproduces #14452 situation with deadlock - see diagram
     */
    @Test
    public void testInvalidationDeadlock() {
        // this simulates servlet container's session invalidation from another
        // thread
        new Thread(() -> {
            try {
                Thread.sleep(150);// delay selected so that VaadinSession

                // will be already locked by the main
                // thread
                // when we get here
                httpSessionLock.lock();// simulating servlet container's

                // session lock
                try {
                    mockService.fireSessionDestroy(session);
                } finally {
                    httpSessionLock.unlock();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        try {
            mockService.findVaadinSession(vaadinRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void threadLocalsAfterUnderlyingSessionTimeout() throws InterruptedException {
        final AtomicBoolean detachCalled = new AtomicBoolean(false);
        ui.addDetachListener((DetachEvent event) -> {
            detachCalled.set(true);
            assertEquals(ui, UI.getCurrent());
            assertEquals(ui.getPage(), Page.getCurrent());
            assertEquals(session, VaadinSession.getCurrent());
            assertEquals(mockService, VaadinService.getCurrent());
            assertEquals(mockServlet, VaadinServlet.getCurrent());
        });
        session.valueUnbound(EasyMock.createMock(HttpSessionBindingEvent.class));
        // as soon as we changed session.accessSynchronously
        // to session.access in VaadinService.fireSessionDestroy,
        // we need to run the pending task ourselves
        mockService.runPendingAccessTasks(session);
        Assert.assertTrue(detachCalled.get());
    }

    @Test
    public void threadLocalsAfterSessionDestroy() throws InterruptedException {
        final AtomicBoolean detachCalled = new AtomicBoolean(false);
        ui.addDetachListener((DetachEvent event) -> {
            detachCalled.set(true);
            assertEquals(ui, UI.getCurrent());
            assertEquals(ui.getPage(), Page.getCurrent());
            assertEquals(session, VaadinSession.getCurrent());
            assertEquals(mockService, VaadinService.getCurrent());
            assertEquals(mockServlet, VaadinServlet.getCurrent());
        });
        CurrentInstance.clearAll();
        session.close();
        mockService.cleanupSession(session);
        // as soon as we changed session.accessSynchronously
        // to session.access in VaadinService.fireSessionDestroy,
        // we need to run the pending task ourselves
        mockService.runPendingAccessTasks(session);
        Assert.assertTrue(detachCalled.get());
    }

    @Test
    public void testValueUnbound() {
        MockVaadinSession vaadinSession = new MockVaadinSession(mockService);
        vaadinSession.valueUnbound(EasyMock.createMock(HttpSessionBindingEvent.class));
        Assert.assertEquals("'valueUnbound' method doesn't call 'close' for the session", 1, vaadinSession.getCloseCount());
        vaadinSession.valueUnbound(EasyMock.createMock(HttpSessionBindingEvent.class));
        Assert.assertEquals(("'valueUnbound' method may not call 'close' " + "method for closing session"), 1, vaadinSession.getCloseCount());
    }

    // Can't define as an anonymous class since it would have a reference to
    // VaadinSessionTest.this which isn't serializable
    private static class MockPageUI extends UI {
        Page page = new Page(this, getState(false).pageState) {};

        @Override
        protected void init(VaadinRequest request) {
        }

        @Override
        public Page getPage() {
            return page;
        }
    }

    private static class SerializationTestLabel extends Label {
        private transient VaadinSession session = VaadinSession.getCurrent();

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            session = VaadinSession.getCurrent();
        }
    }

    @Test
    public void threadLocalsWhenDeserializing() throws Exception {
        VaadinSession.setCurrent(session);
        session.lock();
        VaadinSessionTest.SerializationTestLabel label = new VaadinSessionTest.SerializationTestLabel();
        Assert.assertEquals("Session should be set when instance is created", session, label.session);
        ui.setContent(label);
        int uiId = ui.getUIId();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(session);
        }
        session.unlock();
        CurrentInstance.clearAll();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
        VaadinSession deserializedSession = ((VaadinSession) (in.readObject()));
        Assert.assertNull("Current session shouldn't leak from deserialisation", VaadinSession.getCurrent());
        Assert.assertNotSame("Should get a new session", session, deserializedSession);
        // Restore http session and service instance so the session can be
        // locked
        deserializedSession.refreshTransients(mockWrappedSession, mockService);
        deserializedSession.lock();
        UI deserializedUi = deserializedSession.getUIById(uiId);
        VaadinSessionTest.SerializationTestLabel deserializedLabel = ((VaadinSessionTest.SerializationTestLabel) (deserializedUi.getContent()));
        Assert.assertEquals("Current session should be available in SerializationTestLabel.readObject", deserializedSession, deserializedLabel.session);
        deserializedSession.unlock();
    }

    @Test
    public void lockedDuringSerialization() throws IOException {
        final AtomicBoolean lockChecked = new AtomicBoolean(false);
        ui.setContent(new Label() {
            private void writeObject(ObjectOutputStream out) throws IOException {
                Assert.assertTrue(session.hasLock());
                lockChecked.set(true);
                out.defaultWriteObject();
            }
        });
        session.unlock();
        Assert.assertFalse(session.hasLock());
        ObjectOutputStream out = new ObjectOutputStream(new ByteArrayOutputStream());
        out.writeObject(session);
        Assert.assertFalse(session.hasLock());
        Assert.assertTrue(lockChecked.get());
    }
}

