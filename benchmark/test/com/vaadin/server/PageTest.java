package com.vaadin.server;


import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.server.Page.BrowserWindowResizeListener;
import com.vaadin.shared.ui.ui.PageState;
import com.vaadin.ui.UI;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link Page}
 *
 * @author Vaadin Ltd
 */
public class PageTest {
    @Test
    public void removeBrowserWindowResizeListener_listenerIsAttached_listenerRemoved() {
        Page page = new Page(EasyMock.createMock(UI.class), EasyMock.createMock(PageState.class));
        PageTest.TestBrowserWindowResizeListener listener = new PageTest.TestBrowserWindowResizeListener();
        page.addBrowserWindowResizeListener(listener);
        page.removeBrowserWindowResizeListener(listener);
        page.updateBrowserWindowSize(0, 0, true);
        Assert.assertFalse("Listener is called after removal", listener.isCalled());
    }

    @Test
    public void removeBrowserWindowResizeListener_listenerIsNotAttached_stateIsUpdated() {
        PageTest.TestPage page = new PageTest.TestPage(EasyMock.createMock(UI.class), EasyMock.createMock(PageState.class));
        BrowserWindowResizeListener listener = EasyMock.createMock(BrowserWindowResizeListener.class);
        page.removeBrowserWindowResizeListener(listener);
        Assert.assertFalse("Page state 'hasResizeListeners' property has wrong value", page.getState(false).hasResizeListeners);
    }

    private static class TestPage extends Page {
        public TestPage(UI uI, PageState state) {
            super(uI, state);
        }

        @Override
        protected PageState getState(boolean markAsDirty) {
            return super.getState(markAsDirty);
        }
    }

    private static class TestBrowserWindowResizeListener implements BrowserWindowResizeListener {
        @Override
        public void browserWindowResized(BrowserWindowResizeEvent event) {
            isCalled = true;
        }

        public boolean isCalled() {
            return isCalled;
        }

        private boolean isCalled;
    }
}

