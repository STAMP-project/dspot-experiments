package com.vaadin.v7.tests.components.grid.basicfeatures.client;


import org.junit.Test;


public class GridStylingTest extends GridStaticSectionTest {
    @Test
    public void testGridPrimaryStyle() throws Exception {
        openTestURL();
        validateStylenames("v-grid");
    }

    @Test
    public void testChangingPrimaryStyleName() throws Exception {
        openTestURL();
        selectMenuPath("Component", "State", "Primary Stylename", "v-custom-style");
        validateStylenames("v-custom-style");
    }
}

