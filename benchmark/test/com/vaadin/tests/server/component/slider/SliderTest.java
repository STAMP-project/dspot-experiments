package com.vaadin.tests.server.component.slider;


import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SliderTest {
    @Test
    public void minCannotBeLargerThanMax() {
        Slider slider = new Slider();
        slider.setMax(100);
        slider.setMin(101);
        MatcherAssert.assertThat(slider.getMin(), Is.is(101.0));
        MatcherAssert.assertThat(slider.getMax(), Is.is(101.0));
    }

    @Test
    public void maxCannotBeSmallerThanMin() {
        Slider slider = new Slider();
        slider.setMin(50);
        slider.setMax(10);
        MatcherAssert.assertThat(slider.getMax(), Is.is(10.0));
        MatcherAssert.assertThat(slider.getMin(), Is.is(10.0));
    }

    @Test
    public void valueOutOfBoundsExceptionMessageContainsBounds() {
        Slider slider = new Slider();
        try {
            slider.setValue((-1.0));
        } catch (Slider e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.containsString("Value -1.0 is out of bounds: [0.0, 100.0]"));
        }
    }

    @Test
    public void valueIsSet() {
        Slider slider = new Slider();
        slider.setValue(5.0);
        MatcherAssert.assertThat(slider.getValue(), Is.is(5.0));
    }

    @Test(expected = ValueOutOfBoundsException.class)
    public void valueCannotBeOutOfBounds() {
        Slider s = new Slider(0, 10);
        s.setValue(20.0);
    }

    @Test
    public void valueCanHaveLargePrecision() {
        Slider slider = new Slider();
        slider.setResolution(20);
        slider.setValue(99.01234567891234);
        MatcherAssert.assertThat(slider.getValue(), Is.is(99.01234567891234));
    }

    @Test
    public void doublesCanBeUsedAsLimits() {
        Slider slider = new Slider(1.5, 2.5, 1);
        MatcherAssert.assertThat(slider.getMin(), Is.is(1.5));
        MatcherAssert.assertThat(slider.getValue(), Is.is(1.5));
        MatcherAssert.assertThat(slider.getMax(), Is.is(2.5));
    }

    @Test
    public void valuesGreaterThanIntMaxValueCanBeUsed() {
        double minValue = ((double) (Integer.MAX_VALUE)) + 1;
        Slider s = new Slider(minValue, (minValue + 1), 0);
        MatcherAssert.assertThat(s.getValue(), Is.is(minValue));
    }

    @Test
    public void negativeValuesCanBeUsed() {
        Slider slider = new Slider((-0.7), 1.0, 0);
        slider.setValue((-0.4));
        MatcherAssert.assertThat(slider.getValue(), Is.is((-0.0)));
    }

    @Test
    public void boundariesAreRounded() {
        Slider slider = new Slider(1.5, 2.5, 0);
        slider.setValue(1.0);
        MatcherAssert.assertThat(slider.getValue(), Is.is(1.0));
        MatcherAssert.assertThat(slider.getMin(), Is.is(1.0));
        MatcherAssert.assertThat(slider.getMax(), Is.is(2.0));
    }

    @Test
    public void valueWithSmallerPrecisionCanBeUsed() {
        Slider slider = new Slider(0, 100, 10);
        slider.setValue(1.2);
        MatcherAssert.assertThat(slider.getValue(), Is.is(1.2));
    }

    @Test
    public void valueWithLargerPrecisionCanBeUsed() {
        Slider slider = new Slider(0, 100, 2);
        slider.setValue(1.2345);
        MatcherAssert.assertThat(slider.getValue(), Is.is(1.23));
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwNPE() {
        Slider slider = new Slider();
        slider.setValue(null);
    }
}

