package com.vaadin.server;


import com.vaadin.shared.Registration;
import com.vaadin.util.CurrentInstance;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionBindingEvent;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class VaadinServiceTest {
    private class TestSessionDestroyListener implements SessionDestroyListener {
        int callCount = 0;

        @Override
        public void sessionDestroy(SessionDestroyEvent event) {
            (callCount)++;
        }
    }

    private class TestServiceDestroyListener implements ServiceDestroyListener {
        int callCount = 0;

        @Override
        public void serviceDestroy(ServiceDestroyEvent event) {
            (callCount)++;
        }
    }

    @Test
    public void testFireSessionDestroy() throws ServletException {
        VaadinService service = VaadinServiceTest.createService();
        VaadinServiceTest.TestSessionDestroyListener listener = new VaadinServiceTest.TestSessionDestroyListener();
        service.addSessionDestroyListener(listener);
        MockVaadinSession vaadinSession = new MockVaadinSession(service);
        service.fireSessionDestroy(vaadinSession);
        Assert.assertEquals("'fireSessionDestroy' method doesn't call 'close' for the session", 1, vaadinSession.getCloseCount());
        vaadinSession.valueUnbound(EasyMock.createMock(HttpSessionBindingEvent.class));
        Assert.assertEquals(("'fireSessionDestroy' method may not call 'close' " + "method for closing session"), 1, vaadinSession.getCloseCount());
        Assert.assertEquals("SessionDestroyListeners not called exactly once", 1, listener.callCount);
    }

    @Test
    public void captionIsSetToACriticalNotification() {
        String notification = createCriticalNotification("foobar", "message", "details", "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"caption\":\"foobar\""));
    }

    @Test
    public void nullCaptionIsSetToACriticalNotification() {
        String notification = createCriticalNotification(null, "message", "details", "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"caption\":null"));
    }

    @Test
    public void messageWithDetailsIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "foo", "bar", "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"details\":\"bar\""));
    }

    @Test
    public void nullMessageSentAsNullInACriticalNotification() {
        String notification = createCriticalNotification("caption", null, "foobar", "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"message\":null"));
    }

    @Test
    public void nullMessageIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", null, null, "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"message\":null"));
    }

    @Test
    public void messageSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "foobar", null, "url");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"message\":\"foobar\""));
    }

    @Test
    public void urlIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "message", "details", "foobar");
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"url\":\"foobar\""));
    }

    @Test
    public void nullUrlIsSetToACriticalNotification() {
        String notification = createCriticalNotification("caption", "message", "details", null);
        MatcherAssert.assertThat(notification, CoreMatchers.containsString("\"url\":null"));
    }

    @Test
    public void currentInstancesAfterPendingAccessTasks() {
        VaadinService service = VaadinServiceTest.createService();
        MockVaadinSession session = new MockVaadinSession(service);
        session.lock();
        service.accessSession(session, () -> CurrentInstance.set(.class, "Set in task"));
        CurrentInstance.set(String.class, "Original value");
        service.runPendingAccessTasks(session);
        Assert.assertEquals("Original CurrentInstance should be set after the task has been run", "Original value", CurrentInstance.get(String.class));
    }

    @Test
    public void fireServiceDestroy() {
        VaadinService service = VaadinServiceTest.createService();
        VaadinServiceTest.TestServiceDestroyListener listener = new VaadinServiceTest.TestServiceDestroyListener();
        VaadinServiceTest.TestServiceDestroyListener listener2 = new VaadinServiceTest.TestServiceDestroyListener();
        service.addServiceDestroyListener(listener);
        Registration remover2 = service.addServiceDestroyListener(listener2);
        service.destroy();
        Assert.assertEquals(1, listener.callCount);
        Assert.assertEquals(1, listener2.callCount);
        service.removeServiceDestroyListener(listener);
        remover2.remove();
        service.destroy();
        Assert.assertEquals(1, listener.callCount);
        Assert.assertEquals(1, listener2.callCount);
    }
}

