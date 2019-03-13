package com.vaadin.tests.tooltip;


import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.themes.ChameleonTheme;
import com.vaadin.v7.ui.themes.Reindeer;
import com.vaadin.v7.ui.themes.Runo;
import org.junit.Test;


public class MenuBarTooltipTest extends MultiBrowserTest {
    @Test
    public void toolTipShouldBeOnTopOfMenuItem() {
        String[] themes = new String[]{ ValoTheme.THEME_NAME, Reindeer.THEME_NAME, Runo.THEME_NAME, ChameleonTheme.THEME_NAME };
        for (String theme : themes) {
            assertZIndices(theme);
        }
    }
}

