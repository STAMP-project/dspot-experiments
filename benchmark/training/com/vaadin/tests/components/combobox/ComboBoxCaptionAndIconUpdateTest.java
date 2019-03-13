package com.vaadin.tests.components.combobox;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ComboBoxCaptionAndIconUpdateTest extends SingleBrowserTest {
    @Test
    public void testInitialData() {
        openTestURL();
        assertDisplayValues("fi.gif", "Commit 1");
    }

    @Test
    public void testChangeIconProvider() {
        openTestURL();
        changeIconGenerator();
        assertDisplayValues("m.gif", "Commit 1");
    }

    @Test
    public void testChangeCaptionProvider() {
        openTestURL();
        changeCaptionGenerator();
        assertDisplayValues("fi.gif", "Commit ID 1");
    }

    @Test
    public void testItemAndCaptionProvider() {
        openTestURL();
        changeCaptionGenerator();
        changeIconGenerator();
        assertDisplayValues("m.gif", "Commit ID 1");
    }

    @Test
    public void testEditCaption() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();
        clickButton("editMsg");
        assertDisplayValues("m.gif", "Edited message");
    }

    @Test
    public void testEditIcon() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();
        clickButton("editIcon");
        assertDisplayValues("fi.gif", "Commit ID 1");
    }

    @Test
    public void testEditIconAndCaption() {
        openTestURL();
        changeIconGenerator();
        changeCaptionGenerator();
        clickButton("editAll");
        assertDisplayValues("fi.gif", "Edited message and icon");
    }
}

