package com.vaadin.tests.components.menubar;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for top level menu item which should close its sub-menus each time when
 * it's clicked. Also it checks sub-menu item which should not close its
 * sub-menus if they are opened on click.
 *
 * @author Vaadin Ltd
 */
public class MenuBarClickOpenedMenuTest extends MultiBrowserTest {
    @Test
    public void testTopLevelMenuClickClosesSubMenus() {
        click("v-menubar-menuitem-first-level");
        checkSubMenus(false);
    }

    @Test
    public void testSubMenuClickDoesNotCloseSubMenus() {
        click("v-menubar-menuitem-second-level");
        checkSubMenus(true);
    }
}

