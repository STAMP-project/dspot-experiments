package com.vaadin.tests.design.nested;


import org.hamcrest.CoreMatchers;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test case for writing nested templates
 *
 * @author Vaadin Ltd
 */
public class WriteNestedTemplatesTest {
    private MyDesignRoot root;

    private Element design;

    @Test
    public void testChildRendered() {
        Assert.assertEquals("Root layout must have one child", 1, design.children().size());
        Assert.assertEquals("com_vaadin_tests_design_nested-my-extended-child-design", design.child(0).tagName());
    }

    @Test
    public void rootCaptionIsWritten() {
        Assert.assertTrue(design.hasAttr("caption"));
        Assert.assertThat(design.attr("caption"), CoreMatchers.is("root caption"));
    }

    @Test
    public void childCaptionIsWritten() {
        Assert.assertTrue(design.child(0).hasAttr("caption"));
        Assert.assertThat(design.child(0).attr("caption"), CoreMatchers.is("child caption"));
    }

    // The default caption is read from child template
    @Test
    public void defaultCaptionIsNotOverwritten() {
        setCaption("Default caption for child design");
        design = createDesign();
        Assert.assertFalse(design.child(0).hasAttr("caption"));
    }

    @Test
    public void childDesignChildrenIsNotWrittenInRootTemplate() {
        Assert.assertThat(design.child(0).children().size(), CoreMatchers.is(0));
    }
}

