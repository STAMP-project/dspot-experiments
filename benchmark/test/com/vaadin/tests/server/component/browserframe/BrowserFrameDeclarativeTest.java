package com.vaadin.tests.server.component.browserframe;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.BrowserFrame;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link BrowserFrame}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class BrowserFrameDeclarativeTest extends DeclarativeTestBase<BrowserFrame> {
    @Test
    public void read() {
        testRead(getDesign(), getExpectedResult());
    }

    @Test
    public void write() {
        testWrite(getDesign(), getExpectedResult());
    }

    @Test
    public void testEmpty() {
        testRead("<vaadin-browser-frame/>", new BrowserFrame());
    }
}

