package com.vaadin.tests.components.checkboxgroup;


import com.vaadin.tests.tb3.MultiBrowserTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.By;


public class CheckBoxGroupReadOnlyTest extends MultiBrowserTest {
    @Test
    public void itemsAreReadOnly() {
        openTestURL();
        // Initially components are read-only
        TestCase.assertTrue(getSelect().isReadOnly());
        TestCase.assertEquals(4, findReadOnlyCheckboxes().size());
        // Should not contain v-readonly
        findElement(By.id("changeReadOnly")).click();
        TestCase.assertEquals(0, findReadOnlyCheckboxes().size());
        // Should not contain v-readonly
        findElement(By.id("changeEnabled")).click();
        TestCase.assertEquals(0, findReadOnlyCheckboxes().size());
        // make read-only
        findElement(By.id("changeReadOnly")).click();
        // enable
        findElement(By.id("changeEnabled")).click();
        // Should contain v-readonly
        TestCase.assertEquals(4, findReadOnlyCheckboxes().size());
    }
}

