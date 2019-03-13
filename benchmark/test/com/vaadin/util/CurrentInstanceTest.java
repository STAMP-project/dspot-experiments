package com.vaadin.util;


import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CurrentInstanceTest {
    @Test
    public void testInitiallyCleared() throws Exception {
        assertCleared();
    }

    @Test
    public void testClearedAfterRemove() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        Assert.assertEquals(this, CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.set(CurrentInstanceTest.class, null);
        assertCleared();
    }

    @Test
    public void testClearedWithClearAll() throws Exception {
        CurrentInstance.set(CurrentInstanceTest.class, this);
        Assert.assertEquals(this, CurrentInstance.get(CurrentInstanceTest.class));
        CurrentInstance.clearAll();
        assertCleared();
    }

    private static class UIStoredInCurrentInstance extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    private static class SessionStoredInCurrentInstance extends VaadinSession {
        public SessionStoredInCurrentInstance(VaadinService service) {
            super(service);
        }
    }

    @Test
    public void testRestoringNullUIWorks() throws Exception {
        // First make sure current instance is empty
        CurrentInstance.clearAll();
        // Then store a new UI in there
        Map<Class<?>, CurrentInstance> old = CurrentInstance.setCurrent(new CurrentInstanceTest.UIStoredInCurrentInstance());
        // Restore the old values and assert that the UI is null again
        CurrentInstance.restoreInstances(old);
        Assert.assertNull(CurrentInstance.get(UI.class));
    }

    @Test
    public void testRestoringNullSessionWorks() throws Exception {
        // First make sure current instance is empty
        CurrentInstance.clearAll();
        // Then store a new session in there
        Map<Class<?>, CurrentInstance> old = CurrentInstance.setCurrent(new CurrentInstanceTest.SessionStoredInCurrentInstance(EasyMock.createNiceMock(VaadinService.class)));
        // Restore the old values and assert that the session is null again
        CurrentInstance.restoreInstances(old);
        Assert.assertNull(CurrentInstance.get(VaadinSession.class));
        Assert.assertNull(CurrentInstance.get(VaadinService.class));
    }

    @Test
    public void testRestoreWithGarbageCollectedValue() throws InterruptedException {
        VaadinSession session1 = new VaadinSession(null) {
            @Override
            public String toString() {
                return "First session";
            }
        };
        VaadinSession session2 = new VaadinSession(null) {
            @Override
            public String toString() {
                return "Second session";
            }
        };
        VaadinSession.setCurrent(session1);
        Map<Class<?>, CurrentInstance> previous = CurrentInstance.setCurrent(session2);
        // Use weak ref to verify object is collected
        WeakReference<VaadinSession> ref = new WeakReference(session1);
        session1 = null;
        CurrentInstanceTest.waitUntilGarbageCollected(ref);
        CurrentInstance.restoreInstances(previous);
        Assert.assertNull(VaadinSession.getCurrent());
    }

    @Test
    public void testFallbackResolvers() throws Exception {
        CurrentInstanceTest.TestFallbackResolver<UI> uiResolver = new CurrentInstanceTest.TestFallbackResolver<UI>(new CurrentInstanceTest.FakeUI());
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);
        CurrentInstanceTest.TestFallbackResolver<VaadinSession> sessionResolver = new CurrentInstanceTest.TestFallbackResolver<VaadinSession>(new CurrentInstanceTest.FakeSession());
        CurrentInstance.defineFallbackResolver(VaadinSession.class, sessionResolver);
        CurrentInstanceTest.TestFallbackResolver<VaadinService> serviceResolver = new CurrentInstanceTest.TestFallbackResolver<VaadinService>(new CurrentInstanceTest.FakeService(new CurrentInstanceTest.FakeServlet()));
        CurrentInstance.defineFallbackResolver(VaadinService.class, serviceResolver);
        Assert.assertThat(UI.getCurrent(), CoreMatchers.instanceOf(CurrentInstanceTest.FakeUI.class));
        Assert.assertThat(VaadinSession.getCurrent(), CoreMatchers.instanceOf(CurrentInstanceTest.FakeSession.class));
        Assert.assertThat(VaadinService.getCurrent(), CoreMatchers.instanceOf(CurrentInstanceTest.FakeService.class));
        Assert.assertEquals("The UI fallback resolver should have been called exactly once", 1, uiResolver.getCalled());
        Assert.assertEquals("The VaadinSession fallback resolver should have been called exactly once", 1, sessionResolver.getCalled());
        Assert.assertEquals("The VaadinService fallback resolver should have been called exactly once", 1, serviceResolver.getCalled());
        // the VaadinServlet.getCurrent() resolution uses the VaadinService type
        Assert.assertThat(VaadinServlet.getCurrent(), CoreMatchers.instanceOf(CurrentInstanceTest.FakeServlet.class));
        Assert.assertEquals("The VaadinService fallback resolver should have been called exactly twice", 2, serviceResolver.getCalled());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallbackResolversWithAlreadyDefinedResolver() {
        CurrentInstanceTest.TestFallbackResolver<UI> uiResolver = new CurrentInstanceTest.TestFallbackResolver<UI>(new CurrentInstanceTest.FakeUI());
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);
        CurrentInstance.defineFallbackResolver(UI.class, uiResolver);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFallbackResolversWithNullResolver() {
        CurrentInstance.defineFallbackResolver(UI.class, null);
    }

    @Test
    public void nonInheritableThreadLocals() throws InterruptedException, ExecutionException {
        CurrentInstance.clearAll();
        CurrentInstance.set(CurrentInstanceTest.class, this);
        Assert.assertNotNull(CurrentInstance.get(CurrentInstanceTest.class));
        Callable<Void> runnable = () -> {
            Assert.assertNull(CurrentInstance.get(CurrentInstanceTest.class));
            return null;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Void> future = service.submit(runnable);
        future.get();
    }

    private static class TestFallbackResolver<T> implements CurrentInstanceFallbackResolver<T> {
        private int called;

        private final T instance;

        public TestFallbackResolver(T instance) {
            this.instance = instance;
        }

        @Override
        public T resolve() {
            (called)++;
            return instance;
        }

        public int getCalled() {
            return called;
        }
    }

    private static class FakeUI extends UI {
        @Override
        protected void init(VaadinRequest request) {
        }
    }

    private static class FakeServlet extends VaadinServlet {}

    private static class FakeService extends VaadinServletService {
        public FakeService(VaadinServlet servlet) throws ServiceException {
            super(servlet, new DefaultDeploymentConfiguration(CurrentInstanceTest.FakeService.class, new Properties()));
        }
    }

    private static class FakeSession extends VaadinSession {
        public FakeSession() {
            super(null);
        }
    }
}

