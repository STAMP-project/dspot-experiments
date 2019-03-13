package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Regular click cases already covered by @LabelEmbeddedClickThroughForTableTest
 * Testing cases when mouse down and mouse up positions are different
 *
 * @author Vaadin Ltd
 */
public class TableMatchesMouseDownMouseUpElementTest extends MultiBrowserTest {
    TableElement table;

    @Test
    public void testClick() {
        openTestURL();
        table = $(TableElement.class).first();
        testMoveOut(getBoldTag(0, 2));
        testMoveIn(getBoldTag(0, 2));
        testMoveOut(getLabel(0, 1));
        testMoveIn(getLabel(0, 1));
        testClickOnDifferentRows();
    }
}

