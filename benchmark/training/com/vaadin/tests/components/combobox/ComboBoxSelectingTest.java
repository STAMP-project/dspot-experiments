package com.vaadin.tests.components.combobox;


import Keys.ARROW_DOWN;
import Keys.BACK_SPACE;
import Keys.DOWN;
import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxSelectingTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxElement;

    @Test
    public void ensureOldFilterIsCleared() {
        comboBoxElement.openPopup();
        int initialVisibleOptions = countVisibleOptions();
        clearInputAndType("b11");
        int visibleOptionsAfterFiltering = countVisibleOptions();
        Assert.assertEquals(1, visibleOptionsAfterFiltering);
        clickOnLabel();
        sleep(1000);
        // no selection was made, clicking on arrow should show all options
        // again
        comboBoxElement.openPopup();
        int visibleOptions = countVisibleOptions();
        Assert.assertEquals(initialVisibleOptions, visibleOptions);
    }

    @Test
    public void firstSuggestionIsSelectedWithEnter() {
        typeInputAndHitEnter("a");
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void firstSuggestionIsSelectedWithTab() {
        typeInputAndHitTab("a");
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void nullIsSelected() {
        typeInputAndHitEnter("a");
        assertThatSelectedValueIs("a0");
        clearInputAndHitEnter();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void itemFromSecondPageIsSelected() {
        typeInputAndHitEnter("a20");
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void selectingNullFromSecondPage() {
        typeInputAndHitEnter("a20");
        assertThatSelectedValueIs("a20");
        clearInputAndHitEnter();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void selectionRemainsAfterOpeningPopup() {
        typeInputAndHitEnter("a20");
        assertThatSelectedValueIs("a20");
        openPopup();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void noSelectionAfterMouseOut() {
        typeInputAndHitEnter("a20");
        comboBoxElement.sendKeys(ARROW_DOWN, ARROW_DOWN);
        findElement(By.className("v-app")).click();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void cancelResetsSelection() {
        sendKeysToInput("a20");
        cancelSelection();
        assertThatSelectedValueIs("");
    }

    @Test
    public void inputFieldResetsToSelectedText() {
        typeInputAndHitEnter("z5");
        sendKeysToInput(BACK_SPACE, BACK_SPACE);
        cancelSelection();
        assertThatSelectedValueIs("z5");
    }

    @Test
    public void emptyValueIsSelectedWithTab() {
        typeInputAndHitEnter("z5");
        assertThatSelectedValueIs("z5");
        // longer delay for this one because otherwise it keeps failing when run
        // on local machine
        int delay = 200;
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            delay = 500;
        }
        comboBoxElement.sendKeys(delay, BACK_SPACE, BACK_SPACE, TAB);
        assertThatSelectedValueIs("", "null");
        sendKeysToInput("z5");
        cancelSelection();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithEnter() {
        sendKeysToInput("z");
        sendKeysToInput(DOWN, DOWN, getReturn());
        assertThatSelectedValueIs("z2");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithTab() {
        sendKeysToInput("z");
        sendKeysToInput(DOWN, DOWN, TAB);
        assertThatSelectedValueIs("z2");
    }
}

