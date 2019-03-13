package com.vaadin.tests.components.datefield;


import Keys.ENTER;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class DateFieldBinderCrossValidationTest extends SingleBrowserTest {
    private static final String EXPECTED_ERROR = "from field is Date is out of allowed range. To field is Date is out of allowed range";

    private static final String EXPECTED_NULL_ERROR = "from field is null. To field is null";

    @Test
    public void makeBothFieldInvalidThenValid() {
        openTestURL();
        DateFieldElement fromField = $(DateFieldElement.class).id("from-field");
        WebElement fromFieldText = fromField.findElement(By.tagName("input"));
        DateFieldElement toField = $(DateFieldElement.class).id("to-field");
        WebElement toFieldText = toField.findElement(By.tagName("input"));
        LabelElement label = $(LabelElement.class).id("status");
        fromFieldText.sendKeys("2019/01/01", ENTER);
        toFieldText.sendKeys("2018/02/02", ENTER);
        Assert.assertEquals("Error message should contain the information", DateFieldBinderCrossValidationTest.EXPECTED_ERROR, label.getText());
        fromFieldText.clear();
        fromFieldText.sendKeys("2018/01/01", ENTER);
        Assert.assertEquals("Error message should be null", DateFieldBinderCrossValidationTest.EXPECTED_NULL_ERROR, label.getText());
    }
}

