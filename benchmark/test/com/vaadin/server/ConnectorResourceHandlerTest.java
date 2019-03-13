package com.vaadin.server;


import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import java.io.IOException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class ConnectorResourceHandlerTest {
    VaadinRequest request;

    VaadinResponse response;

    VaadinSession session;

    UI ui;

    @Test
    public void testErrorHandling() throws IOException {
        ErrorHandler errorHandler = EasyMock.createMock(ErrorHandler.class);
        errorHandler.error(EasyMock.anyObject(ErrorEvent.class));
        EasyMock.replay(errorHandler);
        Button button = new Button() {
            @Override
            public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) {
                throw new RuntimeException();
            }
        };
        button.setErrorHandler(errorHandler);
        session.lock();
        try {
            ui.setContent(button);
        } finally {
            session.unlock();
        }
        ConnectorResourceHandler handler = new ConnectorResourceHandler();
        Assert.assertTrue(handler.handleRequest(session, request, response));
        EasyMock.verify(errorHandler);
    }
}

