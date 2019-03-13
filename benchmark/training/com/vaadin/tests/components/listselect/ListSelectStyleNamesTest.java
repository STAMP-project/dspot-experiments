package com.vaadin.tests.components.listselect;


import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ListSelectStyleNamesTest extends SingleBrowserTest {
    private NativeSelectElement nativeSelect;

    private TestBenchElement nativeSelectSelect;

    private ListSelectElement listSelect;

    private TestBenchElement listSelectSelect;

    @Test
    public void correctInitialStyleNames() {
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial", "v-select-custominitial");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial", "v-select-custominitial");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void addStyleName() {
        $(ButtonElement.class).id("add").click();
        assertStyleNames(nativeSelect, "v-select", "v-widget", "custominitial", "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(nativeSelectSelect, "v-select-select");
        assertStyleNames(listSelect, "v-select", "v-widget", "custominitial", "v-select-custominitial", "new", "v-select-new");
        assertStyleNames(listSelectSelect, "v-select-select");
    }

    @Test
    public void changePrimaryStyleName() {
        $(ButtonElement.class).id("add").click();
        $(ButtonElement.class).id("changeprimary").click();
        assertStyleNames(nativeSelect, "newprimary", "v-widget", "custominitial", "newprimary-custominitial", "new", "newprimary-new");
        assertStyleNames(nativeSelectSelect, "newprimary-select");
        assertStyleNames(listSelect, "newprimary", "v-widget", "custominitial", "newprimary-custominitial", "new", "newprimary-new");
        assertStyleNames(listSelectSelect, "newprimary-select");
    }
}

