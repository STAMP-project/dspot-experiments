package com.example.lint.detectors;


import com.example.lint.util.AbstractDetectorTest;
import org.junit.Test;


public class DirectMaterialPaletteColorUsageDetectorTest extends AbstractDetectorTest {
    @Test
    @SuppressWarnings("checkstyle:LineLength")
    public void test_should_trigger_when_material_palette_color_referenced_directly_in_code() throws Exception {
        String file = "layout/direct_material_palette_color_usage_test_case.xml";
        String expectedOutcome = "layout/direct_material_palette_color_usage_test_case.xml:15: Error: Attempting to use a palette color directly. [DirectMaterialColorUsage]\n" + (("        android:textColor=\"@color/material_red_400\" />\n" + "        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n") + "1 errors, 0 warnings\n");
        String outcome = lintFiles(file);
        // There's a weird invisible char that makes equalTo fail :/
        assertThat(outcome).isEqualToIgnoringWhitespace(expectedOutcome);
    }

    @Test
    public void test_should_not_trigger_when_material_palette_color_referenced_indirectly_in_code() throws Exception {
        String file = "layout/indirect_material_palette_color_usage_test_case.xml";
        assertThat(lintFiles(file)).isEqualTo(AbstractDetectorTest.NO_WARNINGS);
    }
}

