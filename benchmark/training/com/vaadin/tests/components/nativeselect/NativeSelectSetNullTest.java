package com.vaadin.tests.components.nativeselect;


import NativeSelectSetNull.EMPTY_SELECTION_TEXT;
import com.vaadin.tests.tb3.MultiBrowserTest;
import junit.framework.TestCase;
import org.junit.Test;


public class NativeSelectSetNullTest extends MultiBrowserTest {
    @Test
    public void testCaptionSelected() {
        getButtonOnId("setNull");
        TestCase.assertEquals(EMPTY_SELECTION_TEXT, getSelect().getValue());
    }

    @Test
    public void changeSelectedValue() {
        getButtonOnId("changeSelect").click();
        TestCase.assertEquals(3, Integer.valueOf(getSelect().getValue()).intValue());
    }

    @Test
    public void clearSelection() {
        getButtonOnId("clear").click();
        TestCase.assertEquals(EMPTY_SELECTION_TEXT, getSelect().getValue());
    }

    @Test
    public void valuePreservedAfterAllowEmptySelectionChanged() {
        getSelect().setValue("2");
        getButtonOnId("disable").click();
        TestCase.assertEquals(2, Integer.valueOf(getSelect().getValue()).intValue());
        getButtonOnId("disable").click();
        getButtonOnId("setNull").click();
        TestCase.assertEquals(EMPTY_SELECTION_TEXT, getSelect().getValue());
    }
}

