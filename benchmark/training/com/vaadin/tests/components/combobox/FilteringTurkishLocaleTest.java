package com.vaadin.tests.components.combobox;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FilteringTurkishLocaleTest extends MultiBrowserTest {
    @Test
    public void testEnglishLocale() {
        openTestURL();
        setLocale("en");
        List<String> suggestions = getFilterSuggestions("i");
        Assert.assertEquals("Both suggestions should be present", 2, suggestions.size());
    }

    @Test
    public void testTurkishLocaleWithDot() {
        openTestURL();
        setLocale("tr");
        List<String> suggestions = getFilterSuggestions("i");
        Assert.assertEquals("There should be only one suggestion", 1, suggestions.size());
        Assert.assertEquals("? dotted", suggestions.get(0));
    }

    @Test
    public void testTurkishLocaleWithoutDot() {
        openTestURL();
        setLocale("tr");
        List<String> suggestions = getFilterSuggestions("?");
        Assert.assertEquals("There should be only one suggestion", 1, suggestions.size());
        Assert.assertEquals("I dotless", suggestions.get(0));
    }
}

