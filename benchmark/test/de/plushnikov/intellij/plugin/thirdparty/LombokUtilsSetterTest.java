package de.plushnikov.intellij.plugin.thirdparty;


import de.plushnikov.intellij.plugin.processor.field.AccessorsInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LombokUtilsSetterTest {
    private static final AccessorsInfo DEFAULT_ACCESSORS = AccessorsInfo.build(false, false, false);

    @Test
    public void testToSetterNames_dValue() {
        String result = makeResults("dValue", false);
        Assert.assertThat(result, CoreMatchers.equalTo("setDValue"));
    }

    @Test
    public void testToSetterNames_Value() {
        String result = makeResults("Value", false);
        Assert.assertThat(result, CoreMatchers.equalTo("setValue"));
    }

    @Test
    public void testToSetterNames_NonBoolean() {
        String result = makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("setMyField"));
    }

    @Test
    public void testToSetterNames_NonBoolean_Uppercase() {
        String result = makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("setMyField"));
    }

    @Test
    public void testToSetterNames_NonBoolean_Uppercase_Multiple() {
        String result = makeResults("MYField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("setMYField"));
    }

    @Test
    public void testToSetterNames_Boolean() {
        String result = makeResults("myField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("setMyField"));
    }

    @Test
    public void testToSetterNames_Boolean_Uppercase() {
        String result = makeResults("MyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("setMyField"));
    }

    @Test
    public void testToSetterNames_Boolean_is_Lowercase() {
        String result = makeResults("ismyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("setIsmyField"));
    }

    @Test
    public void testToSetterNames_Boolean_is_Uppercase() {
        String result = makeResults("isMyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("setMyField"));
    }

    @Test
    public void testToSetterNames_Boolean_IS() {
        String result = makeResults("ISmyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("setISmyField"));
    }
}

