package com.vaadin.tests.server.component.slider;


import SliderOrientation.VERTICAL;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Slider;
import org.junit.Test;


/**
 * Tests declarative support for implementations of {@link Slider}.
 *
 * @author Vaadin Ltd
 */
public class SliderDeclarativeTest extends DeclarativeTestBase<Slider> {
    @Test
    public void testDefault() {
        String design = "<vaadin-slider>";
        Slider expected = new Slider();
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testHorizontal() {
        String design = "<vaadin-slider min=10 max=20 resolution=1 value=12.3>";
        Slider expected = new Slider();
        expected.setMin(10.0);
        expected.setMax(20.0);
        expected.setResolution(1);
        expected.setValue(12.3);
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testVertical() {
        String design = "<vaadin-slider vertical>";
        Slider expected = new Slider();
        expected.setOrientation(VERTICAL);
        testRead(design, expected);
        testWrite(design, expected);
    }

    @Test
    public void testReadOnlyValue() {
        String design = "<vaadin-slider readonly min=10 max=20 resolution=1 value=12.3>";
        Slider expected = new Slider();
        expected.setMin(10.0);
        expected.setMax(20.0);
        expected.setResolution(1);
        expected.setValue(12.3);
        expected.setReadOnly(true);
        testRead(design, expected);
        testWrite(design, expected);
    }
}

