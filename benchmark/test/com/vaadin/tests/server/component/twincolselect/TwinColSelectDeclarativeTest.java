package com.vaadin.tests.server.component.twincolselect;


import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.TwinColSelect;
import org.junit.Test;


/**
 * TwinColSelectt declarative test.
 * <p>
 * There are only TwinColSelect specific properties explicit tests. All other
 * tests are in the super class ( {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 * @author Vaadin Ltd
 */
public class TwinColSelectDeclarativeTest extends AbstractMultiSelectDeclarativeTest<TwinColSelect> {
    @Test
    public void rowsPropertySerialization() {
        int rows = 7;
        String design = String.format("<%s rows='%s'/>", getComponentTag(), rows);
        TwinColSelect<String> select = new TwinColSelect();
        select.setRows(rows);
        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void rightColumnCaptionPropertySerialization() {
        String rightColumnCaption = "foo";
        String design = String.format("<%s right-column-caption='%s'/>", getComponentTag(), rightColumnCaption);
        TwinColSelect<String> select = new TwinColSelect();
        select.setRightColumnCaption(rightColumnCaption);
        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void leftColumnCaptionPropertySerialization() {
        String leftColumnCaption = "foo";
        String design = String.format("<%s left-column-caption='%s'/>", getComponentTag(), leftColumnCaption);
        TwinColSelect<String> select = new TwinColSelect();
        select.setLeftColumnCaption(leftColumnCaption);
        testRead(design, select);
        testWrite(design, select);
    }
}

