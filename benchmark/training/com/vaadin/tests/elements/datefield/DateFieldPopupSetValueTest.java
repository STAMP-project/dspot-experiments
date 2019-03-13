package com.vaadin.tests.elements.datefield;


import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PopupDateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

import static DateFieldPopupSetValue.changedDate;


public class DateFieldPopupSetValueTest extends MultiBrowserTest {
    LabelElement counter;

    PopupDateFieldElement dfPopup;

    DateFieldElement df;

    @Test
    public void testGetValue() {
        String value = df.getValue();
        Assert.assertEquals("04/12/15", value);
    }

    @Test
    public void testSetValue() {
        Date date = changedDate;
        String value = new SimpleDateFormat("MM/dd/yy").format(date);
        df.setValue(value);
        Assert.assertEquals("06/11/15", df.getValue());
    }

    @Test
    public void testValueChanged() {
        Date date = changedDate;
        String value = new SimpleDateFormat("MM/dd/yy").format(date);
        df.setValue(value);
        counter.waitForVaadin();
        Assert.assertEquals("1", counter.getText());
    }
}

