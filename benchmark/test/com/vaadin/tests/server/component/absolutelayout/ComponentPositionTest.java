package com.vaadin.tests.server.component.absolutelayout;


import Sizeable.Unit.CM;
import Sizeable.Unit.EM;
import Sizeable.Unit.EX;
import Sizeable.Unit.INCH;
import Sizeable.Unit.MM;
import Sizeable.Unit.PERCENTAGE;
import Sizeable.Unit.PICAS;
import Sizeable.Unit.PIXELS;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import org.junit.Assert;
import org.junit.Test;


public class ComponentPositionTest {
    private static final String CSS = "top:7.0px;right:7.0%;bottom:7.0pc;left:7.0em;z-index:7;";

    private static final String PARTIAL_CSS = "top:7.0px;left:7.0em;";

    private static final Float CSS_VALUE = Float.valueOf(7);

    private static final Unit UNIT_UNSET = Unit.PIXELS;

    /**
     * Add component w/o giving positions, assert that everything is unset
     */
    @Test
    public void testNoPosition() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);
        Assert.assertNull(layout.getPosition(b).getTopValue());
        Assert.assertNull(layout.getPosition(b).getBottomValue());
        Assert.assertNull(layout.getPosition(b).getLeftValue());
        Assert.assertNull(layout.getPosition(b).getRightValue());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getLeftUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getRightUnits());
        Assert.assertEquals((-1), layout.getPosition(b).getZIndex());
        Assert.assertEquals("", layout.getPosition(b).getCSSString());
    }

    /**
     * Add component, setting all attributes using CSS, assert getter agree
     */
    @Test
    public void testFullCss() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, ComponentPositionTest.CSS);
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getTopValue());
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getBottomValue());
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getLeftValue());
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getRightValue());
        Assert.assertEquals(PIXELS, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(PICAS, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(EM, layout.getPosition(b).getLeftUnits());
        Assert.assertEquals(PERCENTAGE, layout.getPosition(b).getRightUnits());
        Assert.assertEquals(7, layout.getPosition(b).getZIndex());
        Assert.assertEquals(ComponentPositionTest.CSS, layout.getPosition(b).getCSSString());
    }

    /**
     * Add component, setting some attributes using CSS, assert getters agree
     */
    @Test
    public void testPartialCss() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, ComponentPositionTest.PARTIAL_CSS);
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getTopValue());
        Assert.assertNull(layout.getPosition(b).getBottomValue());
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getLeftValue());
        Assert.assertNull(layout.getPosition(b).getRightValue());
        Assert.assertEquals(PIXELS, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(EM, layout.getPosition(b).getLeftUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getRightUnits());
        Assert.assertEquals((-1), layout.getPosition(b).getZIndex());
        Assert.assertEquals(ComponentPositionTest.PARTIAL_CSS, layout.getPosition(b).getCSSString());
    }

    /**
     * Add component setting all attributes using CSS, then reset using partial
     * CSS; assert getters agree and the appropriate attributes are unset.
     */
    @Test
    public void testPartialCssReset() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, ComponentPositionTest.CSS);
        layout.getPosition(b).setCSSString(ComponentPositionTest.PARTIAL_CSS);
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getTopValue());
        Assert.assertNull(layout.getPosition(b).getBottomValue());
        Assert.assertEquals(ComponentPositionTest.CSS_VALUE, layout.getPosition(b).getLeftValue());
        Assert.assertNull(layout.getPosition(b).getRightValue());
        Assert.assertEquals(PIXELS, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(EM, layout.getPosition(b).getLeftUnits());
        Assert.assertEquals(ComponentPositionTest.UNIT_UNSET, layout.getPosition(b).getRightUnits());
        Assert.assertEquals((-1), layout.getPosition(b).getZIndex());
        Assert.assertEquals(ComponentPositionTest.PARTIAL_CSS, layout.getPosition(b).getCSSString());
    }

    /**
     * Add component, then set all position attributes with individual setters
     * for value and units; assert getters agree.
     */
    @Test
    public void testSetPosition() {
        final Float SIZE = Float.valueOf(12);
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);
        layout.getPosition(b).setTopValue(SIZE);
        layout.getPosition(b).setRightValue(SIZE);
        layout.getPosition(b).setBottomValue(SIZE);
        layout.getPosition(b).setLeftValue(SIZE);
        layout.getPosition(b).setTopUnits(CM);
        layout.getPosition(b).setRightUnits(EX);
        layout.getPosition(b).setBottomUnits(INCH);
        layout.getPosition(b).setLeftUnits(MM);
        Assert.assertEquals(SIZE, layout.getPosition(b).getTopValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getRightValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getBottomValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getLeftValue());
        Assert.assertEquals(CM, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(EX, layout.getPosition(b).getRightUnits());
        Assert.assertEquals(INCH, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(MM, layout.getPosition(b).getLeftUnits());
    }

    /**
     * Add component, then set all position attributes with combined setters for
     * value and units; assert getters agree.
     */
    @Test
    public void testSetPosition2() {
        final Float SIZE = Float.valueOf(12);
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b);
        layout.getPosition(b).setTop(SIZE, CM);
        layout.getPosition(b).setRight(SIZE, EX);
        layout.getPosition(b).setBottom(SIZE, INCH);
        layout.getPosition(b).setLeft(SIZE, MM);
        Assert.assertEquals(SIZE, layout.getPosition(b).getTopValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getRightValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getBottomValue());
        Assert.assertEquals(SIZE, layout.getPosition(b).getLeftValue());
        Assert.assertEquals(CM, layout.getPosition(b).getTopUnits());
        Assert.assertEquals(EX, layout.getPosition(b).getRightUnits());
        Assert.assertEquals(INCH, layout.getPosition(b).getBottomUnits());
        Assert.assertEquals(MM, layout.getPosition(b).getLeftUnits());
    }

    /**
     * Add component, set all attributes using CSS, unset some using method
     * calls, assert getters agree.
     */
    @Test
    public void testUnsetPosition() {
        AbsoluteLayout layout = new AbsoluteLayout();
        Button b = new Button();
        layout.addComponent(b, ComponentPositionTest.CSS);
        layout.getPosition(b).setTopValue(null);
        layout.getPosition(b).setRightValue(null);
        layout.getPosition(b).setBottomValue(null);
        layout.getPosition(b).setLeftValue(null);
        layout.getPosition(b).setZIndex((-1));
        Assert.assertNull(layout.getPosition(b).getTopValue());
        Assert.assertNull(layout.getPosition(b).getBottomValue());
        Assert.assertNull(layout.getPosition(b).getLeftValue());
        Assert.assertNull(layout.getPosition(b).getRightValue());
        Assert.assertEquals("", layout.getPosition(b).getCSSString());
    }
}

