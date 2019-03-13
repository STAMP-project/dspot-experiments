package com.vaadin.tests.themes.valo;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ValoThemeUITest extends MultiBrowserTest {
    @Test
    public void labels() throws Exception {
        openTestURL("test");
        open("Labels");
        compareScreen("labels");
    }

    @Test
    public void buttonsLinks() throws Exception {
        openTestURL("test");
        open("Buttons & Links", "Buttons");
        compareScreen("buttonsLinks_with_disabled");
    }

    @Test
    public void textFields() throws Exception {
        openTestURL("test");
        open("Text Fields <span class=\"valo-menu-badge\">123</span>", "Text Fields");
        compareScreen("textFields");
    }

    @Test
    public void common() throws Exception {
        openTestURL("test");
        open("Common UI Elements");
        compareScreen("common");
    }

    @Test
    public void datefields() throws Exception {
        openTestURL("test");
        open("Date Fields");
        // Note that this can look broken in IE9 because of some browser
        // rendering issue... The problem seems to be in the customized
        // horizontal layout in the test app
        compareScreen("datefields-localdate-with-range");
    }

    @Test
    public void comboboxes() throws Exception {
        openTestURL("test");
        open("Combo Boxes");
        compareScreen("comboboxes");
    }

    @Test
    public void selects() throws Exception {
        openTestURL("test");
        open("Selects");
        compareScreen("selects");
    }

    @Test
    public void checkboxes() throws Exception {
        openTestURL("test");
        open("Check Boxes & Option Groups", "Check Boxes");
        compareScreen("checkboxes_with_readonly");
    }

    @Test
    public void sliders() throws Exception {
        openTestURL("test");
        open("Sliders & Progress Bars", "Sliders");
        compareScreen("sliders");
    }

    @Test
    public void colorpickers() throws Exception {
        openTestURL("test");
        open("Color Pickers");
        compareScreen("colorpickers");
    }

    @Test
    public void menubars() throws Exception {
        openTestURL("test");
        open("Menu Bars");
        compareScreen("menubars");
    }

    @Test
    public void trees() throws Exception {
        openTestURL("test");
        open("Trees");
        selectTreeNodeByCaption("Quid securi");
        compareScreen("trees");
    }

    @Test
    public void tables() throws Exception {
        openTestURL("test");
        open("Tables");
        check("Components in Cells");
        compareScreen("tables");
    }

    @Test
    public void treeTables() throws Exception {
        openTestURL("test");
        open("Tables");
        check("Hierarchical");
        check("Footer");
        compareScreen("treetables");
    }

    @Test
    public void dragging() throws Exception {
        openTestURL("test");
        open("Drag and Drop", "Dragging Components");
        compareScreen("dragging");
    }

    @Test
    public void panels() throws Exception {
        openTestURL("test");
        open("Panels", "Panels & Layout panels");
        compareScreen("panels");
    }

    @Test
    public void splitpanels() throws Exception {
        openTestURL("test");
        open("Split Panels");
        compareScreen("splitpanels");
    }

    @Test
    public void tabs() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        sleep(200);
        compareScreen("tabs");
    }

    @Test
    public void tabsClosable() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        check("Disable tabs");
        check("Overflow");
        sleep(200);
        compareScreen("tabs-closable-disabled");
    }

    @Test
    public void tabsClosableUnframed() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Closable");
        // Framed option is checked by default so we are actually unchecking
        check("Framed");
        check("Overflow");
        sleep(200);
        compareScreen("tabs-closable-unframed");
    }

    @Test
    public void tabsAlignRight() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Right-aligned tabs");
        sleep(200);
        compareScreen("tabs-align-right");
    }

    @Test
    public void tabsAlignCenter() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Centered tabs");
        sleep(200);
        compareScreen("tabs-align-center");
    }

    @Test
    public void tabsIconsOnTop() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Icons on top");
        sleep(200);
        compareScreen("tabs-icons-on-top");
    }

    @Test
    public void tabsEqualCompactPadded() throws Exception {
        openTestURL("test");
        open("Tabs <span class=\"valo-menu-badge\">123</span>", "Tabs");
        check("Equal-width tabs");
        check("Padded tabbar");
        check("Compact");
        compareScreen("tabs-equal-compact-padded");
    }

    @Test
    public void accordions() throws Exception {
        openTestURL("test");
        open("Accordions");
        compareScreen("accordions");
    }

    @Test
    public void popupviews() throws Exception {
        openTestURL("test");
        open("Popup Views");
        scrollTo(500, 0);
        compareScreen("popupviews");
    }

    @Test
    public void calendar() throws Exception {
        openTestURL("test");
        scrollTo(500, 0);
        open("Calendar");
        compareScreen("calendar");
    }

    @Test
    public void forms() throws Exception {
        openTestURL("test");
        scrollTo(500, 0);
        open("Forms");
        compareScreen("forms");
    }
}

