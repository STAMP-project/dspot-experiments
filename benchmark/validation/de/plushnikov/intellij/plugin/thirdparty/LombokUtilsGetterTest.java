package de.plushnikov.intellij.plugin.thirdparty;


import de.plushnikov.intellij.plugin.processor.field.AccessorsInfo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LombokUtilsGetterTest {
    private static final AccessorsInfo DEFAULT_ACCESSORS = AccessorsInfo.build(false, false, false);

    @Test
    public void testToGetterNames_dValue() {
        String result = makeResults("dValue", false);
        Assert.assertThat(result, CoreMatchers.equalTo("getDValue"));
    }

    @Test
    public void testToGetterNames_Value() {
        String result = makeResults("Value", false);
        Assert.assertThat(result, CoreMatchers.equalTo("getValue"));
    }

    @Test
    public void testToGetterNames_NonBoolean() {
        String result = makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("getMyField"));
    }

    @Test
    public void testToGetterNames_NonBoolean_Uppercase() {
        String result = makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("getMyField"));
    }

    @Test
    public void testToGetterNames_NonBoolean_Uppercase_Multiple() {
        String result = makeResults("MYField", false);
        Assert.assertThat(result, CoreMatchers.equalTo("getMYField"));
    }

    @Test
    public void testToGetterNames_Boolean() {
        String result = makeResults("myField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isMyField"));
    }

    @Test
    public void testToGetterNames_Boolean_Uppercase() {
        String result = makeResults("MyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isMyField"));
    }

    @Test
    public void testToGetterNames_Boolean_is_Lowercase() {
        String result = makeResults("ismyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isIsmyField"));
    }

    @Test
    public void testToGetterNames_Boolean_is_Uppercase() {
        String result = makeResults("isMyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isMyField"));
    }

    @Test
    public void testToGetterNames_Boolean_IS() {
        String result = makeResults("ISmyField", true);
        Assert.assertThat(result, CoreMatchers.equalTo("isISmyField"));
    }
}

