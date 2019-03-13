package com.vaadin.tests.design;


import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.DesignException;
import org.junit.Assert;
import org.junit.Test;


public class InvalidTagNamesTest {
    @Test(expected = DesignException.class)
    public void tagWithoutDash() {
        readDesign("<vbutton>foo</vbutton>");
    }

    @Test
    public void emptyTag() {
        // JSoup parses empty tags into text nodes
        Component c = readDesign("<>foo</>");
        Assert.assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void onlyPrefix() {
        readDesign("<vaadin->foo</vaadin->");
    }

    @Test
    public void onlyClass() {
        // JSoup will refuse to parse tags starting with - and convert them into
        // text nodes instead
        Component c = readDesign("<-v>foo</-v>");
        Assert.assertNull(c);
    }

    @Test(expected = DesignException.class)
    public void unknownClass() {
        readDesign("<vaadin-unknownbutton>foo</vaadin-unknownbutton>");
    }

    @Test(expected = DesignException.class)
    public void unknownTag() {
        readDesign("<x-button></x-button>");
    }

    @Test(expected = DesignException.class)
    public void specialCharacters() {
        readDesign("<vaadin-button-&!#></vaadin-button-&!#>");
    }
}

