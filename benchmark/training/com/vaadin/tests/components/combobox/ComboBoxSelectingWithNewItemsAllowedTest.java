package com.vaadin.tests.components.combobox;


import Keys.ARROW_DOWN;
import Keys.BACK_SPACE;
import Keys.DOWN;
import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ComboBoxSelectingWithNewItemsAllowedTest extends MultiBrowserTest {
    private ComboBoxElement comboBoxElement;

    private LabelElement labelElement;

    @Test
    public void checkDefaults() {
        assertInitialItemCount();
    }

    @Test
    public void itemIsAddedWithEnter() {
        typeInputAndHitEnter("a");
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void itemIsAddedWithTab() {
        typeInputAndHitTab("a");
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void itemIsAddedWithClickOut() {
        typeInputAndClickOut("a");
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
    }

    @Test
    public void matchingSuggestionIsSelectedWithEnter() {
        typeInputAndHitEnter("a0");
        assertInitialItemCount();
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void matchingSuggestionIsSelectedWithTab() {
        typeInputAndHitTab("a0");
        assertInitialItemCount();
        assertThatSelectedValueIs("a0");
    }

    @Test
    public void nullIsSelected() {
        typeInputAndHitEnter("a");
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("a");
        clearInputAndHitEnter();
        assertOneMoreThanInitial();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void itemFromSecondPageIsSelected() {
        typeInputAndHitEnter("a20");
        assertInitialItemCount();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void selectingNullFromSecondPage() {
        typeInputAndHitEnter("a20");
        assertInitialItemCount();
        assertThatSelectedValueIs("a20");
        clearInputAndHitEnter();
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void selectionRemainsAfterOpeningPopup() {
        typeInputAndHitEnter("a20");
        assertInitialItemCount();
        assertThatSelectedValueIs("a20");
        openPopup();
        assertThatSelectedValueIs("a20");
    }

    @Test
    public void selectionOnMouseOut() {
        typeInputAndHitEnter("a20");
        comboBoxElement.sendKeys(ARROW_DOWN, ARROW_DOWN);
        findElement(By.className("v-app")).click();
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void cancelResetsSelection() {
        sendKeysToInput("a20");
        cancelSelection();
        assertInitialItemCount();
        assertThatSelectedValueIs("");
    }

    @Test
    public void inputFieldResetsToSelectedText() {
        typeInputAndHitEnter("z5");
        sendKeysToInput(BACK_SPACE, BACK_SPACE);
        cancelSelection();
        assertInitialItemCount();
        assertThatSelectedValueIs("z5");
    }

    @Test
    public void emptyValueIsSelectedWithTab() {
        typeInputAndHitEnter("z5");
        assertInitialItemCount();
        assertThatSelectedValueIs("z5");
        // longer delay for this one because otherwise it keeps failing when run
        // on local machine
        comboBoxElement.sendKeys(200, BACK_SPACE, BACK_SPACE, TAB);
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
        sendKeysToInput("z5");
        cancelSelection();
        assertInitialItemCount();
        assertThatSelectedValueIs("", "null");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithEnter() {
        sendKeysToInput("z");
        sendKeysToInput(DOWN, DOWN, getReturn());
        assertInitialItemCount();
        assertThatSelectedValueIs("z1");
    }

    @Test
    public void arrowNavigatedValueIsSelectedWithTab() {
        sendKeysToInput("z");
        sendKeysToInput(DOWN, DOWN, TAB);
        assertInitialItemCount();
        assertThatSelectedValueIs("z1");
    }
}

