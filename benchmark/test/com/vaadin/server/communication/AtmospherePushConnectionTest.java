package com.vaadin.server.communication;


import State.CONNECTED;
import State.DISCONNECTED;
import com.vaadin.ui.UI;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.atmosphere.cpr.AtmosphereResource;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AtmospherePushConnectionTest {
    @Test
    public void testSerialization() throws Exception {
        UI ui = EasyMock.createNiceMock(UI.class);
        AtmosphereResource resource = EasyMock.createNiceMock(AtmosphereResource.class);
        AtmospherePushConnection connection = new AtmospherePushConnection(ui);
        connection.connect(resource);
        Assert.assertEquals(CONNECTED, connection.getState());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject(connection);
        connection = ((AtmospherePushConnection) (new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject()));
        Assert.assertEquals(DISCONNECTED, connection.getState());
    }
}

