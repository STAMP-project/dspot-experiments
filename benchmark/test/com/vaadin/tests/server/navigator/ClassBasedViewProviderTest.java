package com.vaadin.tests.server.navigator;


import com.vaadin.navigator.Navigator.ClassBasedViewProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import org.junit.Assert;
import org.junit.Test;


public class ClassBasedViewProviderTest {
    public static class TestView extends Label implements View {
        public String parameters = null;

        @Override
        public void enter(ViewChangeEvent event) {
            parameters = event.getParameters();
        }
    }

    public static class TestView2 extends ClassBasedViewProviderTest.TestView {}

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderWithNullName() throws Exception {
        new ClassBasedViewProvider(null, ClassBasedViewProviderTest.TestView.class);
        Assert.fail("Should not be able to create view provider with null name");
    }

    @Test
    public void testCreateProviderWithEmptyStringName() throws Exception {
        new ClassBasedViewProvider("", ClassBasedViewProviderTest.TestView.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProviderNullViewClass() throws Exception {
        new ClassBasedViewProvider("test", null);
        Assert.fail("Should not be able to create view provider with null view class");
    }

    @Test
    public void testViewNameGetter() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("", ClassBasedViewProviderTest.TestView.class);
        Assert.assertEquals("View name should be empty", "", provider1.getViewName());
        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        Assert.assertEquals("View name does not match", "test", provider2.getViewName());
    }

    @Test
    public void testViewClassGetter() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        Assert.assertEquals("Incorrect view class returned by getter", ClassBasedViewProviderTest.TestView.class, provider.getViewClass());
    }

    @Test
    public void testGetViewNameForNullString() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        Assert.assertNull("Received view name for null view string", provider.getViewName(null));
    }

    @Test
    public void testGetViewNameForEmptyString() throws Exception {
        ClassBasedViewProvider provider1 = new ClassBasedViewProvider("", ClassBasedViewProviderTest.TestView.class);
        Assert.assertEquals("Did not find view name for empty view string in a provider with empty string registered", "", provider1.getViewName(""));
        ClassBasedViewProvider provider2 = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        Assert.assertNull("Found view name for empty view string when none registered", provider2.getViewName(""));
    }

    @Test
    public void testGetViewNameWithParameters() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        Assert.assertEquals("Incorrect view name found for view string", "test", provider.getViewName("test"));
        Assert.assertEquals("Incorrect view name found for view string ending with slash", "test", provider.getViewName("test/"));
        Assert.assertEquals("Incorrect view name found for view string with parameters", "test", provider.getViewName("test/params/are/here"));
    }

    @Test
    public void testGetView() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        View view = provider.getView("test");
        Assert.assertNotNull("Did not get view from a provider", view);
        Assert.assertEquals("Incorrect view type", ClassBasedViewProviderTest.TestView.class, view.getClass());
    }

    @Test
    public void testGetViewIncorrectViewName() throws Exception {
        ClassBasedViewProvider provider = new ClassBasedViewProvider("test", ClassBasedViewProviderTest.TestView.class);
        View view = provider.getView("test2");
        Assert.assertNull("Got view from a provider for incorrect view name", view);
    }
}

