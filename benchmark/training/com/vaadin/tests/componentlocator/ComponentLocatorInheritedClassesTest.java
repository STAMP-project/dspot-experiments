package com.vaadin.tests.componentlocator;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ComponentLocatorInheritedClassesTest extends SingleBrowserTest {
    @ServerClass("com.vaadin.tests.componentlocator.ComponentLocatorInheritedClasses.DefaultLabel")
    public static class DefaultLabelElement extends LabelElement {}

    @ServerClass("com.vaadin.tests.componentlocator.ComponentLocatorInheritedClasses.MyCustomLabel")
    public static class MyCustomLabelElement extends ComponentLocatorInheritedClassesTest.DefaultLabelElement {}

    @Test
    public void label_finds_all_three() {
        openTestURL();
        Assert.assertEquals(3, $(LabelElement.class).all().size());
    }

    @Test
    public void defaultlabel_finds_two() {
        openTestURL();
        Assert.assertEquals(2, $(ComponentLocatorInheritedClassesTest.DefaultLabelElement.class).all().size());
    }

    @Test
    public void mycustomlabel_finds_one() {
        openTestURL();
        Assert.assertEquals(1, $(ComponentLocatorInheritedClassesTest.MyCustomLabelElement.class).all().size());
    }
}

