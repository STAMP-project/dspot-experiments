package com.vaadin.tests.components.menubar;


import MenuBarChangeFromEventListener.MENU_CLICKED;
import MenuBarChangeFromEventListener.MENU_CLICKED_BLUR;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class MenuBarChangeFromEventListenerTest extends MultiBrowserTest {
    @Test
    public void eventFired() {
        openTestURL();
        findElement(By.className("v-menubar-menuitem")).click();
        assertLogRow(0, 1, MENU_CLICKED);
        findElement(By.id("textField")).click();
        findElement(By.className("v-menubar-menuitem")).click();
        sleep(300);
        assertLogRow(1, 2, MENU_CLICKED_BLUR);
        assertLogRow(0, 3, MENU_CLICKED);
    }
}

