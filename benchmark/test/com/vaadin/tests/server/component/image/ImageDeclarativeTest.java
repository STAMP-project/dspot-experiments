package com.vaadin.tests.server.component.image;


import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Image;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link Image}.
 *
 * @author Vaadin Ltd
 */
public class ImageDeclarativeTest extends DeclarativeTestBase<Image> {
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
        testRead("<vaadin-image />", new Image());
    }
}

