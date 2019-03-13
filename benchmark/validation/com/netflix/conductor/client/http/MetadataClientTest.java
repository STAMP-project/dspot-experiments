package com.netflix.conductor.client.http;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author fjhaveri
 */
public class MetadataClientTest {
    private MetadataClient metadataClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWorkflowDelete() {
        MetadataClient mockClient = Mockito.mock(MetadataClient.class);
        mockClient.unregisterWorkflowDef("hello", 1);
        Mockito.verify(mockClient, Mockito.times(1)).unregisterWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void testWorkflowDeleteThrowException() {
        MetadataClient mockClient = Mockito.mock(MetadataClient.class);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Invalid Workflow name");
        Mockito.doThrow(new RuntimeException("Invalid Workflow name")).when(mockClient).unregisterWorkflowDef(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        mockClient.unregisterWorkflowDef("hello", 1);
    }

    @Test
    public void testWorkflowDeleteVersionMissing() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Version cannot be null");
        metadataClient.unregisterWorkflowDef("hello", null);
    }

    @Test
    public void testWorkflowDeleteNameMissing() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Workflow name cannot be blank");
        metadataClient.unregisterWorkflowDef(null, 1);
    }
}

