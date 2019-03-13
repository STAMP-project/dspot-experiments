package com.vaadin.tests.server.component.colorpicker;


import ColorUtil.HEX_PATTERN;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.components.colorpicker.ColorUtil;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class HexPatternParsingTest {
    @Parameterized.Parameter(0)
    public String input;

    @Parameterized.Parameter(1)
    public int expectedRed;

    @Parameterized.Parameter(2)
    public int expectedGreen;

    @Parameterized.Parameter(3)
    public int expectedBlue;

    @Parameterized.Parameter(4)
    public boolean expectedMatches;

    @Test
    public void testValidHEX() {
        Matcher m = HEX_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c = new Color(expectedRed, expectedGreen, expectedBlue);
            Color c1 = ColorUtil.getHexPatternColor(m);
            Assert.assertTrue(c.equals(c1));
        } else {
            Assert.assertTrue((!matches));
        }
    }
}

