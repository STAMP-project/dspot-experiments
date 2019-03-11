package de.plushnikov.intellij.plugin.thirdparty;


import de.plushnikov.intellij.plugin.processor.field.AccessorsInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LombokUtilsPrefixedFluentTest {
    private static final AccessorsInfo DEFAULT_ACCESSORS = AccessorsInfo.build(true, false, false, "m", "");

    @Test
    public void testToGetterNames_mValue() {
        String result = makeResults("mValue", false);
        Assert.assertThat(result, CoreMatchers.equalTo("value"));
    }

    @Test
    public void testToGetterNames_Value() {
        String result = makeResults("Value", false);
        Assert.assertThat(result, CoreMatchers.equalTo("Value"));
    }

    @Test
    public void testToGetterNames_NonBoolean() {
        String result = makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("myField"));
    }

    @Test
    public void testToGetterNames_NonBoolean_Uppercase() {
        String result = makeResults("mYField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("yField"));
    }

    @Test
    public void testToGetterNames_NonBoolean_Uppercase_Multiple() {
        String result = makeResults("MYField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("MYField"));
    }

    @Test
    public void testToGetterNames_Boolean() {
        String result = makeResults("myField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("myField"));
    }

    @Test
    public void testToGetterNames_Boolean_Uppercase() {
        String result = makeResults("MYField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("MYField"));
    }

    @Test
    public void testToGetterNames_Boolean_is_Lowercase() {
        String result = makeResults("ismyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("ismyField"));
    }

    @Test
    public void testToGetterNames_Boolean_is_Uppercase() {
        String result = makeResults("isMyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isMyField"));
    }

    @Test
    public void testToGetterNames_Boolean_IS() {
        String result = makeResults("ISmyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("ISmyField"));
    }
}

