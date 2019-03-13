package com.vaadin.tests.server.component.abstractcomponent;


import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HasComponents;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractComponentSetParentTest {
    private static class TestComponent extends AbstractComponent {}

    @Test
    public void setParent_marks_old_parent_as_dirty() {
        HasComponents hasComponents = Mockito.mock(HasComponents.class);
        AbstractComponentSetParentTest.TestComponent testComponent = new AbstractComponentSetParentTest.TestComponent();
        testComponent.setParent(hasComponents);
        setParent(null);
        Mockito.verify(hasComponents, Mockito.times(1)).markAsDirty();
    }
}

