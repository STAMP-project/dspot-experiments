package com.vaadin.tests.elements.combobox;


import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ComboBoxGetSuggestionsTest extends MultiBrowserTest {
    @Test
    public void testSuggestions() {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).get(0);
        List<String> suggestions = cb.getPopupSuggestions();
        List<String> expectedSuggestions = new ArrayList<String>();
        for (int i = 1; i < 11; i++) {
            expectedSuggestions.add(("item" + i));
        }
        Assert.assertEquals(expectedSuggestions, suggestions);
    }
}

