package com.vaadin.tests.server.component.colorpicker;


import ColorUtil.HSLA_PATTERN;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.components.colorpicker.ColorUtil;
import java.util.regex.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class HSLAPatternParsingTest {
    @Parameterized.Parameter(0)
    public String input;

    @Parameterized.Parameter(1)
    public int expectedHue;

    @Parameterized.Parameter(2)
    public int expectedSaturation;

    @Parameterized.Parameter(3)
    public int expectedLight;

    @Parameterized.Parameter(4)
    public int expectedAlpha;

    @Parameterized.Parameter(5)
    public boolean expectedMatches;

    @Test
    public void testHSLAData() {
        Matcher m = HSLA_PATTERN.matcher(input);
        boolean matches = m.matches();
        if (expectedMatches) {
            Color expectedColor = new Color(Color.HSLtoRGB(expectedHue, expectedSaturation, expectedLight));
            expectedColor.setAlpha(expectedAlpha);
            Color c1 = ColorUtil.getHSLAPatternColor(m);
            Assert.assertTrue(expectedColor.equals(c1));
        } else {
            Assert.assertTrue((!matches));
        }
    }
}

