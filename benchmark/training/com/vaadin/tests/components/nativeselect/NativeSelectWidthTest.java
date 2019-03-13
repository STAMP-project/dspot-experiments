package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class NativeSelectWidthTest extends MultiBrowserTest {
    @Test
    public void testWidthIs200Px() {
        WebElement nativeSelect = $(NativeSelectElement.class).first().findElement(By.tagName("select"));
        Assert.assertEquals(200, nativeSelect.getSize().getWidth());
        if (!(BrowserUtil.isPhantomJS(getDesiredCapabilities()))) {
            // PhantomJS does not support explicit <select> height
            Assert.assertEquals(120, nativeSelect.getSize().getHeight());
        }
    }

    @Test
    public void testUndefinedWidth() {
        WebElement nativeSelect = $(NativeSelectElement.class).get(1).findElement(By.tagName("select"));
        AbstractTB3Test.assertLessThan("Undefined width NativeSelect should be narrower than 200px", nativeSelect.getSize().getWidth(), 200);
    }
}

