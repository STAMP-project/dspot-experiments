package com.vaadin.tests.components.menubar;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * This class tests that MenuBar and its contents have all the required WAI-ARIA
 * attributes.
 *
 * @author Vaadin Ltd
 */
public class MenuBarsWithWaiAriaTest extends MultiBrowserTest {
    private MenuBarElement firstMenuBar;

    @Test
    public void testMenuBar() {
        Assert.assertEquals("menubar", firstMenuBar.getAttribute("role"));
        Assert.assertNull(firstMenuBar.getAttribute("aria-haspopup"));
        Assert.assertNull(firstMenuBar.getAttribute("aria-disabled"));
        Assert.assertEquals("0", firstMenuBar.getAttribute("tabindex"));
    }

    @Test
    public void testSubMenu() {
        WebElement fileMenu = firstMenuBar.findElement(By.vaadin("#File"));
        fileMenu.click();
        WebElement submenu = findElement(By.className("v-menubar-submenu"));
        Assert.assertEquals("menu", submenu.getAttribute("role"));
        Assert.assertNull(submenu.getAttribute("aria-haspopup"));
        Assert.assertNull(submenu.getAttribute("aria-disabled"));
        Assert.assertEquals("-1", submenu.getAttribute("tabindex"));
    }

    @Test
    public void testEnabledMenuItems() {
        WebElement fileMenu = firstMenuBar.findElement(By.vaadin("#File"));
        Assert.assertEquals("menuitem", fileMenu.getAttribute("role"));
        Assert.assertEquals("true", fileMenu.getAttribute("aria-haspopup"));
        Assert.assertNull(fileMenu.getAttribute("aria-disabled"));
        Assert.assertEquals("-1", fileMenu.getAttribute("tabindex"));
        fileMenu.click();
        WebElement open = fileMenu.findElement(By.vaadin("#Open"));
        Assert.assertEquals("menuitem", open.getAttribute("role"));
        Assert.assertNull(open.getAttribute("aria-haspopup"));
        Assert.assertNull(open.getAttribute("aria-disabled"));
        Assert.assertEquals("-1", open.getAttribute("tabindex"));
        WebElement separator = findElement(By.className("v-menubar-separator"));
        Assert.assertEquals("separator", separator.getAttribute("role"));
        Assert.assertNull(separator.getAttribute("aria-haspopup"));
        Assert.assertNull(separator.getAttribute("aria-disabled"));
        Assert.assertEquals("-1", separator.getAttribute("tabindex"));
    }

    @Test
    public void testDisabledMenuItem() {
        WebElement disabledMenu = firstMenuBar.findElement(By.vaadin("#Disabled"));
        Assert.assertEquals("menuitem", disabledMenu.getAttribute("role"));
        Assert.assertEquals("true", disabledMenu.getAttribute("aria-haspopup"));
        Assert.assertEquals("true", disabledMenu.getAttribute("aria-disabled"));
        Assert.assertEquals("-1", disabledMenu.getAttribute("tabindex"));
    }
}

