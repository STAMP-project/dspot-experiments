package com.vaadin.tests.widgetset.server;


import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Class for unit testing that @DelegateToWidget works on derived widget states.
 *
 * @author Vaadin Ltd
 */
public class OverriddenDecendantsTest extends MultiBrowserTest {
    @Test
    public void allExtendingFieldsShouldGetRowsFromTextAreaStateAnnotation() throws InterruptedException {
        openTestURL();
        List<TextAreaElement> textAreas = $(TextAreaElement.class).all();
        Assert.assertEquals("Did not contain all 3 text areas", 3, textAreas.size());
        for (TextAreaElement area : textAreas) {
            Assert.assertEquals("Text area was missing rows", "10", area.getAttribute("rows"));
        }
    }
}

