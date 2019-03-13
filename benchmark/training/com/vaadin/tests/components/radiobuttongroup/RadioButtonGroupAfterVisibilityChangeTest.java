package com.vaadin.tests.components.radiobuttongroup;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class RadioButtonGroupAfterVisibilityChangeTest extends MultiBrowserTest {
    @Test
    public void verifyOptionIsSelectable() {
        openTestURL();
        getRadioButtonGroupElement().selectByText("false");
        findElement(By.id("hideB")).click();
        findElement(By.id("setAndShow")).click();
        isSelectedOnClientSide("true");
        getRadioButtonGroupElement().selectByText("false");
        isSelectedOnClientSide("false");
    }
}

