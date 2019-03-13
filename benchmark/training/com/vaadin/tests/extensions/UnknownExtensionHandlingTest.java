package com.vaadin.tests.extensions;


import com.vaadin.tests.extensions.UnknownExtensionHandling.MyExtension;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class UnknownExtensionHandlingTest extends SingleBrowserTest {
    @Test
    public void testUnknownExtensionHandling() {
        setDebug(true);
        openTestURL();
        openDebugLogTab();
        Assert.assertTrue(hasMessageContaining(MyExtension.class.getCanonicalName()));
        Assert.assertFalse(hasMessageContaining("Hierachy claims"));
    }
}

