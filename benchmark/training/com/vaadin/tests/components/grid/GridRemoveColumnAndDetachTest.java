package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class GridRemoveColumnAndDetachTest extends SingleBrowserTest {
    @Test
    public void gridDetachesWithoutErrors() {
        openTestURL("debug");
        $(ButtonElement.class).id("detach").click();
        assertElementNotPresent(By.className("v-grid"));
        assertNoErrorNotifications();
    }

    @Test
    public void frozenColumnCountAfterRemovingHiddenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove1").click();
        assertVisibleFrozenColumns(2);
    }

    @Test
    public void frozenColumnCountAfterWhenRemovingFrozenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove0").click();
        assertVisibleFrozenColumns(1);
    }

    @Test
    public void frozenColumnCountAfterWhenRemovingNonFrozenColumn() {
        openTestURL("debug");
        assertVisibleFrozenColumns(2);
        $(ButtonElement.class).id("remove3").click();
        assertVisibleFrozenColumns(2);
    }

    @Test
    public void allColumnsFrozenRemoveLast() {
        openTestURL("debug");
        $(ButtonElement.class).id("remove3").click();
        $(ButtonElement.class).id("remove2").click();
        assertVisibleFrozenColumns(1);
        assertNoErrorNotifications();
    }
}

