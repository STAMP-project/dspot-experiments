package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class GridRowHeightChangeTest extends MultiBrowserTest {
    private final List<String> themes = Arrays.asList("valo", "reindeer", "runo", "chameleon", "base");

    @Test
    public void changeThemeAndMeasureGridHeight() {
        // Initial check
        verifyGridSize();
        for (String theme : themes) {
            // select theme
            $(NativeSelectElement.class).first().selectByText(theme);
            verifyGridSize();
        }
    }
}

