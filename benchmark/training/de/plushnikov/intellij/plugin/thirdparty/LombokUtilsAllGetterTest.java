package de.plushnikov.intellij.plugin.thirdparty;


import de.plushnikov.intellij.plugin.processor.field.AccessorsInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LombokUtilsAllGetterTest {
    private static final AccessorsInfo DEFAULT_ACCESSORS = AccessorsInfo.build(false, false, false);

    private final List<String> lombokResult = new ArrayList<>();

    private final List<String> result = new ArrayList<>();

    @Test
    public void testToAllGetterNames_NonBoolean() {
        makeResults("myField", false, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("getMyField")));
    }

    @Test
    public void testToAllGetterNames_NonBoolean_Uppercase() {
        makeResults("myField", false, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("getMyField")));
    }

    @Test
    public void testToAllGetterNames_NonBoolean_Uppercase_Multiple() {
        makeResults("MYField", false, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("getMYField")));
    }

    @Test
    public void testToAllGetterNames_Boolean() {
        makeResults("myField", true, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("getMyField", "isMyField")));
    }

    @Test
    public void testToAllGetterNames_Boolean_Uppercase() {
        makeResults("MyField", true, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("getMyField", "isMyField")));
    }

    @Test
    public void testToAllGetterNames_Boolean_is_Lowercase() {
        makeResults("ismyField", true, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("isIsmyField", "getIsmyField")));
    }

    @Test
    public void testToAllGetterNames_Boolean_is_Uppercase() {
        makeResults("isMyField", true, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("isIsMyField", "getIsMyField", "getMyField", "isMyField")));
    }

    @Test
    public void testToAllGetterNames_Boolean_IS() {
        makeResults("ISmyField", true, LombokUtilsAllGetterTest.DEFAULT_ACCESSORS);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("getISmyField", "isISmyField")));
    }

    @Test
    public void testToAllGetterNames_NonBoolean_Fluent() {
        makeResults("myField", false, AccessorsInfo.build(true, false, false));
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("myField")));
    }
}

