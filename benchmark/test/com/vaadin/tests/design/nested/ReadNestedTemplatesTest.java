package com.vaadin.tests.design.nested;


import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for reading nested templates
 *
 * @author Vaadin Ltd
 */
public class ReadNestedTemplatesTest {
    private MyDesignRoot root;

    @Test
    public void rootContainsOneChild() {
        Assert.assertThat(getComponentCount(), CoreMatchers.is(1));
        Assert.assertThat(iterator().next(), CoreMatchers.instanceOf(MyExtendedChildDesign.class));
    }

    @Test
    public void rootContainsTwoGrandChildren() {
        Assert.assertThat(getComponentCount(), CoreMatchers.is(2));
    }

    @Test
    public void childComponentIsNotNull() {
        Assert.assertThat(root.childDesign, CoreMatchers.is(IsNot.not(CoreMatchers.nullValue())));
    }

    @Test
    public void childLabelIsNotNull() {
        Assert.assertThat(root.childDesign.childLabel, CoreMatchers.is(IsNot.not(CoreMatchers.nullValue())));
        Assert.assertThat(root.childDesign.childLabel.getValue(), CoreMatchers.is("test content"));
    }

    @Test
    public void childCustomComponentsIsNotNull() {
        Assert.assertThat(root.childDesign.childCustomComponent, CoreMatchers.is(IsNot.not(CoreMatchers.nullValue())));
        Assert.assertThat(getCaption(), CoreMatchers.is("custom content"));
    }
}

