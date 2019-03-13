package com.vaadin.tests.components.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.client.ui.VFilterSelect;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;


/**
 * ComboBox suggestion popup should not obscure the text input box.
 *
 * @author Vaadin Ltd
 */
public class ComboBoxOnSmallScreenTest extends MultiBrowserTest {
    private static final Dimension TARGETSIZE = new Dimension(600, 300);

    private static final String POPUPCLASSNAME = (VFilterSelect.CLASSNAME) + "-suggestpopup";

    ComboBoxElement combobox;

    WebElement popup;

    @Test
    public void testSuggestionPopupOverlayPosition() {
        final int popupTop = popup.getLocation().y;
        final int popupBottom = popupTop + (popup.getSize().getHeight());
        final int cbTop = combobox.getLocation().y;
        final int cbBottom = cbTop + (combobox.getSize().getHeight());
        MatcherAssert.assertThat("Popup overlay overlaps with the textbox", ((popupTop >= cbBottom) || (popupBottom <= cbTop)), Matchers.is(true));
    }

    @Test
    public void testSuggestionPopupOverlaySize() {
        final int popupTop = popup.getLocation().y;
        final int popupBottom = popupTop + (popup.getSize().getHeight());
        final int rootHeight = findElement(By.tagName("body")).getSize().height;
        MatcherAssert.assertThat("Popup overlay out of the screen", ((popupTop < 0) || (popupBottom > rootHeight)), Matchers.is(false));
    }
}

