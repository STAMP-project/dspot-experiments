package com.vaadin.tests.server.component.abstractsinglecomponentcontainer;


import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.HasComponents;
import org.easymock.EasyMock;
import org.junit.Test;


/**
 * Tests for avoiding set parent as child for
 * {@link AbstractSingleComponentContainer#setContent(Component)}
 *
 * @author Vaadin Ltd
 */
public class SetParentAsContentTest {
    @Test(expected = IllegalArgumentException.class)
    public void testSetContent() {
        AbstractSingleComponentContainer container = new AbstractSingleComponentContainer() {};
        HasComponents hasComponentsMock = EasyMock.createMock(HasComponents.class);
        container.setParent(hasComponentsMock);
        container.setContent(hasComponentsMock);
    }
}

