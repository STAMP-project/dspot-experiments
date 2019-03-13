package com.vaadin.tests.server.component.colorpicker;


import ColorUtil.HSL_PATTERN;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.components.colorpicker.ColorUtil;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class HSLPatternParsingTest {
    @Parameterized.Parameter(0)
    public String input;

    @Parameterized.Parameter(1)
    public Color expectedColor;

    @Parameterized.Parameter(2)
    public boolean expectedMatches;

    @Test
    public void testHSLData() {
        Matcher m = HSL_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color c1 = ColorUtil.getHSLPatternColor(m);
            Assert.assertTrue(expectedColor.equals(c1));
        } else {
            Assert.assertTrue((!matches));
        }
    }
}

