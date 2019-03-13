package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ComboBoxSelectingNewItemValueChangeTest extends MultiBrowserTest {
    protected enum SelectionType {

        ENTER,
        TAB,
        CLICK_OUT;}

    private ComboBoxElement comboBoxElement;

    private LabelElement valueLabelElement;

    private LabelElement changeLabelElement;

    private String[] defaultInputs = new String[]{ "foo", "bar", "baz", "fie" };

    private String[] shortInputs = new String[]{ "a", "b", "c", "d" };

    @Test
    public void newItemHandlingWithEnter() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.ENTER, defaultInputs);
    }

    @Test
    public void newItemHandlingWithTab() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.TAB, defaultInputs);
    }

    @Test
    public void newItemHandlingWithClickingOut() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.CLICK_OUT, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithEnter() {
        delay(true);
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.ENTER, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithTab() {
        delay(true);
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.TAB, defaultInputs);
    }

    @Test
    public void slowNewItemHandlingWithClickingOut() {
        delay(true);
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.CLICK_OUT, defaultInputs);
    }

    @Test
    public void shortNewItemHandlingWithEnter() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.ENTER, shortInputs);
    }

    @Test
    public void shortNewItemHandlingWithTab() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.TAB, shortInputs);
    }

    @Test
    public void shortNewItemHandlingWithClickingOut() {
        itemHandling(ComboBoxSelectingNewItemValueChangeTest.SelectionType.CLICK_OUT, shortInputs);
    }
}

