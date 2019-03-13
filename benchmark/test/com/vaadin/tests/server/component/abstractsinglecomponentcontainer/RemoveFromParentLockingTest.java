package com.vaadin.tests.server.component.abstractsinglecomponentcontainer;


import com.vaadin.server.VaadinSession;
import com.vaadin.ui.VerticalLayout;
import org.junit.Assert;
import org.junit.Test;


public class RemoveFromParentLockingTest {
    @Test
    public void attachNoSessionLocked() {
        VerticalLayout testComponent = RemoveFromParentLockingTest.createTestComponent();
        VerticalLayout target = new VerticalLayout();
        try {
            target.addComponent(testComponent);
            throw new AssertionError("Moving component when not holding its sessions's lock should throw");
        } catch (IllegalStateException e) {
            Assert.assertEquals("Cannot remove from parent when the session is not locked.", e.getMessage());
        }
    }

    @Test
    public void attachSessionLocked() {
        VerticalLayout testComponent = RemoveFromParentLockingTest.createTestComponent();
        VerticalLayout target = new VerticalLayout();
        testComponent.getUI().getSession().getLockInstance().lock();
        target.addComponent(testComponent);
        // OK if we get here without any exception
    }

    @Test
    public void crossAttachOtherSessionLocked() {
        VerticalLayout notLockedComponent = RemoveFromParentLockingTest.createTestComponent();
        VerticalLayout lockedComponent = RemoveFromParentLockingTest.createTestComponent();
        // Simulate the situation when attaching cross sessions
        lockedComponent.getUI().getSession().getLockInstance().lock();
        VaadinSession.setCurrent(lockedComponent.getUI().getSession());
        try {
            lockedComponent.addComponent(notLockedComponent);
            throw new AssertionError("Moving component when not holding its sessions's lock should throw");
        } catch (IllegalStateException e) {
            Assert.assertEquals(("Cannot remove from parent when the session is not locked." + " Furthermore, there is another locked session, indicating that the component might be about to be moved from one session to another."), e.getMessage());
        }
    }

    @Test
    public void crossAttachThisSessionLocked() {
        VerticalLayout notLockedComponent = RemoveFromParentLockingTest.createTestComponent();
        VerticalLayout lockedComponent = RemoveFromParentLockingTest.createTestComponent();
        // Simulate the situation when attaching cross sessions
        lockedComponent.getUI().getSession().getLockInstance().lock();
        VaadinSession.setCurrent(lockedComponent.getUI().getSession());
        try {
            notLockedComponent.addComponent(lockedComponent);
        } catch (AssertionError e) {
            // All is fine, don't care about the exact wording in this case
        }
    }
}

