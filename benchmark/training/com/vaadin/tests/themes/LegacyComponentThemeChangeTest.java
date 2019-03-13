package com.vaadin.tests.themes;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class LegacyComponentThemeChangeTest extends MultiBrowserTest {
    @Test
    public void legacyComponentThemeResourceChange() {
        openTestURL();
        String theme = "reindeer";
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);
        theme = "runo";
        changeTheme(theme);
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);
        theme = "reindeer";
        changeTheme(theme);
        assertMenubarTheme(theme);
        assertCombobBoxTheme(theme);
        assertTableTheme(theme);
        assertEmbeddedTheme(theme);
    }
}

