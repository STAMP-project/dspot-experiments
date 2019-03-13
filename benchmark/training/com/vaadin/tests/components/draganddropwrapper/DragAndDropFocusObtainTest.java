package com.vaadin.tests.components.draganddropwrapper;


import com.vaadin.testbench.elements.AbstractTextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for text area inside {@link DragAndDropWrapper}: text area should obtain
 * focus on click.
 *
 * @author Vaadin Ltd
 */
public class DragAndDropFocusObtainTest extends MultiBrowserTest {
    @Test
    public void testTextAreaDndImage() {
        openTestURL();
        int index = 1;
        for (AbstractTextFieldElement ta : $(AbstractTextFieldElement.class).all()) {
            String caption = ta.getCaption();
            ta.click();
            Assert.assertEquals((((index + ". Field '") + caption) + "' focused"), getLogRow(0));
            index++;
        }
        // Make sure we checked all fields
        Assert.assertEquals((8 + 1), index);
    }
}

