package com.vaadin.tests.server.component.flash;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Flash;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link AbstractEmbedded} and
 * {@link Embedded}.
 *
 * @author Vaadin Ltd
 */
public class FlashDeclarativeTest extends DeclarativeTestBase<Flash> {
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
        testRead("<vaadin-flash />", new Flash());
    }
}

