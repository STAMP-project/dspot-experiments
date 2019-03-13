package com.vaadin.tests.components.gridlayout;


import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class GridLayoutExtraSpacingTest extends MultiBrowserTest {
    @Test
    public void componentRowFour() throws IOException, Exception {
        openTestURL();
        CssLayoutElement component = $(CssLayoutElement.class).first();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        // Spacing on, not hiding empty rows/columns
        // There should be 3 * 6px spacing (red) above the csslayout
        verifySpacingAbove((3 * 6), gridLayout, component);
        CheckBoxElement spacingCheckbox = $(CheckBoxElement.class).caption("spacing").first();
        check(spacingCheckbox);
        // Spacing off, not hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);
        CheckBoxElement hideRowsColumnsCheckbox = $(CheckBoxElement.class).caption("hide empty rows/columns").first();
        check(hideRowsColumnsCheckbox);
        // Spacing off, hiding empty rows/columns
        // There should not be any spacing (red) above the csslayout
        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);
        check(spacingCheckbox);
        // Spacing on, hiding empty rows/columns
        // There should not be any spacing (red) above or below the csslayout
        verifySpacingAbove(0, gridLayout, component);
        verifySpacingBelow(0, gridLayout, component);
    }
}

