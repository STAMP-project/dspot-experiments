package com.vaadin.server.communication;


import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;
import java.io.IOException;
import org.junit.Test;
import org.mockito.Mock;


public class HugeFileUploadTest {
    private static final String SEC_KEY = "4";

    private static final String CONN_ID = "2";

    private static final int UI_ID = 1;

    @Mock
    private VaadinSession session;

    @Mock
    private VaadinResponse response;

    @Mock
    private VaadinRequest request;

    @Mock
    private UI ui;

    @Mock
    private StreamVariable streamVariable;

    @Mock
    private ConnectorTracker connectorTracker;

    @Test(expected = IOException.class, timeout = 60000)
    public void testHugeFileWithoutNewLine() throws IOException {
        FileUploadHandler fileUploadHandler = new FileUploadHandler();
        fileUploadHandler.handleRequest(session, request, response);
    }
}

