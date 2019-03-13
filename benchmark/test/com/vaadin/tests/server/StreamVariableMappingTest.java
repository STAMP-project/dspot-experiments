package com.vaadin.tests.server;


import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.StreamVariable;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Upload;
import org.junit.Assert;
import org.junit.Test;


public class StreamVariableMappingTest {
    private static final String variableName = "myName";

    private Upload owner;

    private StreamVariable streamVariable;

    private LegacyCommunicationManager cm;

    @Test
    public void testAddStreamVariable() {
        owner.getUI().getConnectorTracker().registerConnector(owner);
        String targetUrl = cm.getStreamVariableTargetUrl(owner, StreamVariableMappingTest.variableName, streamVariable);
        Assert.assertTrue(targetUrl.startsWith((("app://APP/UPLOAD/-1/" + (owner.getConnectorId())) + "/myName/")));
        ConnectorTracker tracker = owner.getUI().getConnectorTracker();
        StreamVariable streamVariable2 = tracker.getStreamVariable(owner.getConnectorId(), StreamVariableMappingTest.variableName);
        Assert.assertSame(streamVariable, streamVariable2);
    }

    @Test
    public void testRemoveVariable() {
        ConnectorTracker tracker = owner.getUI().getConnectorTracker();
        tracker.registerConnector(owner);
        cm.getStreamVariableTargetUrl(owner, StreamVariableMappingTest.variableName, streamVariable);
        Assert.assertNotNull(tracker.getStreamVariable(owner.getConnectorId(), StreamVariableMappingTest.variableName));
        tracker.cleanStreamVariable(owner.getConnectorId(), StreamVariableMappingTest.variableName);
        Assert.assertNull(tracker.getStreamVariable(owner.getConnectorId(), StreamVariableMappingTest.variableName));
    }
}

