package de.plushnikov.intellij.plugin.thirdparty;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LombokUtilsAllSetterTest {
    private final List<String> lombokResult = new ArrayList<>();

    private final List<String> result = new ArrayList<>();

    @Test
    public void testToAllSetterNames_NonBoolean() {
        makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setMyField")));
    }

    @Test
    public void testToAllSetterNames_NonBoolean_Uppercase() {
        makeResults("myField", false);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setMyField")));
    }

    @Test
    public void testToAllSetterNames_NonBoolean_Uppercase_Multiple() {
        makeResults("MYField", false);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setMYField")));
    }

    @Test
    public void testToAllSetterNames_Boolean() {
        makeResults("myField", true);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setMyField")));
    }

    @Test
    public void testToAllSetterNames_Boolean_Uppercase() {
        makeResults("MyField", true);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setMyField")));
    }

    @Test
    public void testToAllSetterNames_Boolean_is_Lowercase() {
        makeResults("ismyField", true);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setIsmyField")));
    }

    @Test
    public void testToAllSetterNames_Boolean_is_Uppercase() {
        makeResults("isMyField", true);
        Assert.assertThat(result, CoreMatchers.is(Arrays.asList("setMyField", "setIsMyField")));
    }

    @Test
    public void testToAllSetterNames_Boolean_IS() {
        makeResults("ISmyField", true);
        Assert.assertThat(result, CoreMatchers.is(Collections.singletonList("setISmyField")));
    }
}

