package org.robolectric.res;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ThemeStyleSetTest {
    private ThemeStyleSet themeStyleSet;

    @Test
    public void shouldFindAttributesFromAnAppliedStyle() throws Exception {
        themeStyleSet = new ThemeStyleSet();
        themeStyleSet.apply(createStyle("style1", createAttribute("string1", "string1 value from style1"), createAttribute("string2", "string2 value from style1")), false);
        themeStyleSet.apply(createStyle("style2", createAttribute("string2", "string2 value from style2")), false);
        assertThat(themeStyleSet.getAttrValue(attrName("string1")).value).isEqualTo("string1 value from style1");
        assertThat(themeStyleSet.getAttrValue(attrName("string2")).value).isEqualTo("string2 value from style1");
    }

    @Test
    public void shouldFindAttributesFromAnAppliedFromForcedStyle() throws Exception {
        themeStyleSet.apply(createStyle("style1", createAttribute("string1", "string1 value from style1"), createAttribute("string2", "string2 value from style1")), false);
        themeStyleSet.apply(createStyle("style2", createAttribute("string1", "string1 value from style2")), true);
        assertThat(themeStyleSet.getAttrValue(attrName("string1")).value).isEqualTo("string1 value from style2");
        assertThat(themeStyleSet.getAttrValue(attrName("string2")).value).isEqualTo("string2 value from style1");
    }
}

