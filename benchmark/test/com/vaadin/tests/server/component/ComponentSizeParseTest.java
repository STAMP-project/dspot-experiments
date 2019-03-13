package com.vaadin.tests.server.component;


import Unit.CM;
import Unit.EM;
import Unit.INCH;
import Unit.MM;
import Unit.PERCENTAGE;
import Unit.PICAS;
import Unit.PIXELS;
import Unit.POINTS;
import Unit.REM;
import com.vaadin.shared.ui.label.LabelState;
import com.vaadin.ui.Label;
import org.junit.Test;


public class ComponentSizeParseTest {
    private final class LabelWithPublicState extends Label {
        @Override
        protected LabelState getState() {
            return super.getState();
        }
    }

    @Test
    public void testAllTheUnit() {
        testUnit("10.0px", 10, PIXELS);
        testUnit("10.0pt", 10, POINTS);
        testUnit("10.0pc", 10, PICAS);
        testUnit("10.0em", 10, EM);
        testUnit("10.0rem", 10, REM);
        testUnit("10.0mm", 10, MM);
        testUnit("10.0cm", 10, CM);
        testUnit("10.0in", 10, INCH);
        testUnit("10.0%", 10, PERCENTAGE);
    }
}

