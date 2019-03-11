package com.example.lint.detectors;


import com.example.lint.util.AbstractDetectorTest;
import org.junit.Test;


public class HardcodedColorsDetectorTest extends AbstractDetectorTest {
    @Test
    public void test_should_not_trigger_when_there_is_no_hardcoded_color() throws Exception {
        String file = "layout/hardcoded_colors_not_existing_test_case.xml";
        assertThat(lintFiles(file)).isEqualTo(AbstractDetectorTest.NO_WARNINGS);
    }

    @Test
    @SuppressWarnings("checkstyle:LineLength")
    public void test_should_trigger_when_there_is_a_hardcoded_color() throws Exception {
        String file = "layout/hardcoded_colors_existing_test_case.xml";
        String expectedOutcome = "layout/hardcoded_colors_existing_test_case.xml:15: Error: Don\'t use harcoded colors, link through colors file [HardcodedColors]\n" + (("        android:textColor=\"#FFFFFFFF\" />\n" + "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n") + "1 errors, 0 warnings\n");
        String outcome = lintFiles(file);
        // There's a weird invisible char that makes equalTo fail :/
        assertThat(outcome).isEqualToIgnoringWhitespace(expectedOutcome);
    }

    @Test
    public void test_should_not_trigger_when_there_is_a_hardcoded_color_in_the_palette_file() throws Exception {
        String file = "layout/colors__material_design_palette.xml";
        assertThat(lintFiles(file)).isEqualTo(AbstractDetectorTest.NO_WARNINGS);
    }
}

