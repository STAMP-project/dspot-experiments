package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class UISerializationTest extends SingleBrowserTest {
    @Test
    public void uiIsSerialized() throws Exception {
        openTestURL();
        serialize();
        Assert.assertThat(getLogRow(0), Matchers.startsWith("3. Diff states match, size: "));
        Assert.assertThat(getLogRow(1), Matchers.startsWith("2. Deserialized UI in "));
        Assert.assertThat(getLogRow(2), Matchers.allOf(Matchers.startsWith("1. Serialized UI in"), Matchers.containsString(" into "), Matchers.endsWith(" bytes")));
    }
}

