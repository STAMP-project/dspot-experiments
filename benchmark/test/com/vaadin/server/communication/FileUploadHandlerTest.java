package com.vaadin.server.communication;


import com.vaadin.server.ClientConnector;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import java.io.IOException;
import java.io.OutputStream;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class FileUploadHandlerTest {
    private FileUploadHandler handler;

    @Mock
    private VaadinResponse response;

    @Mock
    private StreamVariable streamVariable;

    @Mock
    private ClientConnector clientConnector;

    @Mock
    private VaadinRequest request;

    @Mock
    private UI ui;

    @Mock
    private ConnectorTracker connectorTracker;

    @Mock
    private VaadinSession session;

    @Mock
    private OutputStream responseOutput;

    private final int uiId = 123;

    private final String connectorId = "connectorId";

    private final String variableName = "name";

    private final String expectedSecurityKey = "key";

    /**
     * Tests whether we get infinite loop if InputStream is already read
     * (#10096)
     */
    @Test(expected = IOException.class)
    public void exceptionIsThrownOnUnexpectedEnd() throws IOException {
        Mockito.when(request.getInputStream()).thenReturn(createInputStream(""));
        Mockito.when(request.getHeader("Content-Length")).thenReturn("1");
        handler.doHandleSimpleMultipartFileUpload(null, request, null, null, null, null, null);
    }

    @Test
    public void responseIsSentOnCorrectSecurityKey() throws IOException {
        Mockito.when(connectorTracker.getSeckey(streamVariable)).thenReturn(expectedSecurityKey);
        handler.handleRequest(session, request, response);
        Mockito.verify(responseOutput).close();
    }

    @Test
    public void responseIsNotSentOnIncorrectSecurityKey() throws IOException {
        Mockito.when(connectorTracker.getSeckey(streamVariable)).thenReturn("another key expected");
        handler.handleRequest(session, request, response);
        Mockito.verifyZeroInteractions(responseOutput);
    }

    @Test
    public void responseIsNotSentOnMissingSecurityKey() throws IOException {
        Mockito.when(connectorTracker.getSeckey(streamVariable)).thenReturn(null);
        handler.handleRequest(session, request, response);
        Mockito.verifyZeroInteractions(responseOutput);
    }
}

