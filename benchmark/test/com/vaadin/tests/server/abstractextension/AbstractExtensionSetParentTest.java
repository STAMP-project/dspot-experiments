package com.vaadin.tests.server.abstractextension;


import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;
import org.junit.Test;
import org.mockito.Mockito;


public class AbstractExtensionSetParentTest {
    private static class TestExtension extends AbstractExtension {}

    @Test
    public void setParent_marks_old_parent_as_dirty() {
        ClientConnector connector = Mockito.mock(ClientConnector.class);
        AbstractExtensionSetParentTest.TestExtension extension = new AbstractExtensionSetParentTest.TestExtension();
        extension.setParent(connector);
        setParent(null);
        Mockito.verify(connector, Mockito.times(1)).markAsDirty();
    }
}

