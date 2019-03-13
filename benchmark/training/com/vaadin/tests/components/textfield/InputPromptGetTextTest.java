package com.vaadin.tests.components.textfield;


import InputPromptGetText.BUTTON;
import InputPromptGetText.FIELD;
import InputPromptGetText.LABEL2;
import Keys.BACK_SPACE;
import Keys.CONTROL;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;


public class InputPromptGetTextTest extends MultiBrowserTest {
    @Test
    public void test() {
        openTestURL();
        WebElement field = getDriver().findElement(By.id(FIELD));
        WebElement button = getDriver().findElement(By.id(BUTTON));
        String string = getRandomString();
        field.sendKeys((string + "\n"));
        String selectAll = Keys.chord(CONTROL, "a");
        field.sendKeys(selectAll);
        field.sendKeys(BACK_SPACE);
        button.click();
        WebElement label = getDriver().findElement(By.id(LABEL2));
        Assert.assertEquals("Your input was:", label.getText().trim());
    }
}

