package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Test;


public class GridSidebarThemeTest extends GridBasicFeaturesTest {
    @Test
    public void testValo() throws Exception {
        runTestSequence("valo");
    }

    @Test
    public void testValoDark() throws Exception {
        runTestSequence("tests-valo-dark");
    }
}

