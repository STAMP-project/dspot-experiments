package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Tests that clicking on active fields doesn't change Table selection, nor does
 * dragging rows.
 *
 * @author Vaadin Ltd
 */
public class TableClickAndDragOnIconAndComponentsTest extends MultiBrowserTest {
    @Test
    public void clickOnTextFieldDoesNotSelectRow() {
        selectRow(1);
        clickOnTextField(2);
        assertThatFocusTextFieldHasText("foo 2foo");
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(1));
    }

    @Test
    public void clickOnReadOnlyTextFieldSelectsRow() {
        selectRow(1);
        clickOnReadOnlyTextField(2);
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(2));
    }

    @Test
    public void clickOnLabelSelectsRow() {
        selectRow(1);
        clickOnLabel(2);
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(2));
    }

    @Test
    public void clickOnEmbeddedIconSelectsRow() {
        selectRow(1);
        clickOnEmbeddedIcon(2);
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(2));
    }

    @Test
    public void dragAndDroppingRowDoesNotSelectRow() {
        selectRow(1);
        moveRow(0, 3);
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(1));
        MatcherAssert.assertThat(getSelectedRowIndex(), CoreMatchers.is(0));
    }

    @Test
    public void dragAndDroppingSelectedRowStaysSelected() {
        selectRow(1);
        moveRow(1, 4);
        MatcherAssert.assertThat(getSelectedRowTextValue(), CoreMatchers.is(1));
        MatcherAssert.assertThat(getSelectedRowIndex(), CoreMatchers.is(4));
    }
}

