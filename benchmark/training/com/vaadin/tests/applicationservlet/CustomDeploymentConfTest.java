package com.vaadin.tests.applicationservlet;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CustomDeploymentConfTest extends MultiBrowserTest {
    @Test
    public void testCustomDeploymentConf() {
        openTestURL();
        LabelElement cacheTimeLabel = $$(LabelElement.class).first();
        LabelElement customParamLabel = $$(LabelElement.class).get(1);
        Assert.assertEquals("Resource cache time: 3599", cacheTimeLabel.getText());
        Assert.assertEquals("Custom config param: customValue", customParamLabel.getText());
    }
}

