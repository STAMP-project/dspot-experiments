package com.vaadin.tests.components.radiobuttongroup;


import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DisabledRadioButtonGroupTest extends MultiBrowserTest {
    @Test
    public void initialDataInDisabledCheckBoxGroup() {
        openTestURL();
        List<String> options = $(RadioButtonGroupElement.class).first().getOptions();
        Assert.assertEquals(3, options.size());
        Assert.assertEquals("a", options.get(0));
        Assert.assertEquals("b", options.get(1));
        Assert.assertEquals("c", options.get(2));
    }
}

