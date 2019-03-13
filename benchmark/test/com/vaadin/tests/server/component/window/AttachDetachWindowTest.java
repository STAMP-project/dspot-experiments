package com.vaadin.tests.server.component.window;


import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.tests.util.AlwaysLockedVaadinSession;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.junit.Assert;
import org.junit.Test;


public class AttachDetachWindowTest {
    private final VaadinSession testApp = new AlwaysLockedVaadinSession(null);

    private interface TestContainer {
        public boolean attachCalled();

        public boolean detachCalled();

        public AttachDetachWindowTest.TestContent getTestContent();

        public VaadinSession getSession();
    }

    private class TestWindow extends Window implements AttachDetachWindowTest.TestContainer {
        boolean windowAttachCalled = false;

        boolean windowDetachCalled = false;

        private final AttachDetachWindowTest.TestContent testContent = new AttachDetachWindowTest.TestContent();

        TestWindow() {
            setContent(testContent);
        }

        @Override
        public void attach() {
            super.attach();
            windowAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            windowDetachCalled = true;
        }

        @Override
        public boolean attachCalled() {
            return windowAttachCalled;
        }

        @Override
        public boolean detachCalled() {
            return windowDetachCalled;
        }

        @Override
        public AttachDetachWindowTest.TestContent getTestContent() {
            return testContent;
        }

        @Override
        public VaadinSession getSession() {
            return super.getSession();
        }
    }

    private class TestContent extends VerticalLayout {
        boolean contentDetachCalled = false;

        boolean childDetachCalled = false;

        boolean contentAttachCalled = false;

        boolean childAttachCalled = false;

        private final Label child = new Label() {
            @Override
            public void attach() {
                super.attach();
                childAttachCalled = true;
            }

            @Override
            public void detach() {
                super.detach();
                childDetachCalled = true;
            }
        };

        public TestContent() {
            addComponent(child);
        }

        @Override
        public void attach() {
            super.attach();
            contentAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            contentDetachCalled = true;
        }
    }

    private class TestUI extends UI implements AttachDetachWindowTest.TestContainer {
        boolean rootAttachCalled = false;

        boolean rootDetachCalled = false;

        private final AttachDetachWindowTest.TestContent testContent = new AttachDetachWindowTest.TestContent();

        public TestUI() {
            setContent(testContent);
        }

        @Override
        protected void init(VaadinRequest request) {
            // Do nothing
        }

        @Override
        public boolean attachCalled() {
            return rootAttachCalled;
        }

        @Override
        public boolean detachCalled() {
            return rootDetachCalled;
        }

        @Override
        public AttachDetachWindowTest.TestContent getTestContent() {
            return testContent;
        }

        @Override
        public void attach() {
            super.attach();
            rootAttachCalled = true;
        }

        @Override
        public void detach() {
            super.detach();
            rootDetachCalled = true;
        }
    }

    AttachDetachWindowTest.TestUI main = new AttachDetachWindowTest.TestUI();

    AttachDetachWindowTest.TestWindow sub = new AttachDetachWindowTest.TestWindow();

    @Test
    public void addSubWindowBeforeAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);
        addWindow(sub);
        assertUnattached(main);
        assertUnattached(sub);
        // attaching main should recurse to sub
        main.setSession(testApp);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void addSubWindowAfterAttachingMainWindow() {
        assertUnattached(main);
        assertUnattached(sub);
        main.setSession(testApp);
        assertAttached(main);
        assertUnattached(sub);
        // main is already attached, so attach should be called for sub
        addWindow(sub);
        assertAttached(main);
        assertAttached(sub);
    }

    @Test
    public void removeSubWindowBeforeDetachingMainWindow() {
        main.setSession(testApp);
        addWindow(sub);
        // sub should be detached when removing from attached main
        removeWindow(sub);
        assertAttached(main);
        assertDetached(sub);
        // main detach should recurse to sub
        setSession(null);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void removeSubWindowAfterDetachingMainWindow() {
        main.setSession(testApp);
        addWindow(sub);
        // main detach should recurse to sub
        setSession(null);
        assertDetached(main);
        assertDetached(sub);
        removeWindow(sub);
        assertDetached(main);
        assertDetached(sub);
    }

    @Test
    public void addWindow_attachEventIsFired() {
        AttachDetachWindowTest.TestUI ui = new AttachDetachWindowTest.TestUI();
        final Window window = new Window();
        final boolean[] eventFired = new boolean[1];
        addComponentAttachListener(( event) -> eventFired[0] = event.getAttachedComponent().equals(window));
        ui.addWindow(window);
        Assert.assertTrue("Attach event is not fired for added window", eventFired[0]);
    }

    @Test
    public void removeWindow_detachEventIsFired() {
        AttachDetachWindowTest.TestUI ui = new AttachDetachWindowTest.TestUI();
        final Window window = new Window();
        final boolean[] eventFired = new boolean[1];
        addComponentDetachListener(( event) -> eventFired[0] = event.getDetachedComponent().equals(window));
        ui.addWindow(window);
        ui.removeWindow(window);
        Assert.assertTrue("Detach event is not fired for removed window", eventFired[0]);
    }
}

