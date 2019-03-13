package com.vaadin.server.communication;


import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.UI;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MetadataWriterTest {
    private UI ui;

    private VaadinSession session;

    private StringWriter writer;

    private SystemMessages messages;

    @Test
    public void writeAsyncTag() throws Exception {
        new MetadataWriter().write(ui, writer, false, true, messages);
        Assert.assertEquals("{\"async\":true}", writer.getBuffer().toString());
    }

    @Test
    public void writeRepaintTag() throws Exception {
        new MetadataWriter().write(ui, writer, true, false, messages);
        Assert.assertEquals("{\"repaintAll\":true}", writer.getBuffer().toString());
    }

    @Test
    public void writeRepaintAndAsyncTag() throws Exception {
        new MetadataWriter().write(ui, writer, true, true, messages);
        Assert.assertEquals("{\"repaintAll\":true, \"async\":true}", writer.getBuffer().toString());
    }

    @Test
    public void writeRedirectWithExpiredSession() throws Exception {
        disableSessionExpirationMessages(messages);
        new MetadataWriter().write(ui, writer, false, false, messages);
        Assert.assertEquals("{}", writer.getBuffer().toString());
    }

    @Test
    public void writeRedirectWithActiveSession() throws Exception {
        WrappedSession wrappedSession = Mockito.mock(WrappedSession.class);
        Mockito.when(session.getSession()).thenReturn(wrappedSession);
        disableSessionExpirationMessages(messages);
        new MetadataWriter().write(ui, writer, false, false, messages);
        Assert.assertEquals("{\"timedRedirect\":{\"interval\":15,\"url\":\"\"}}", writer.getBuffer().toString());
    }

    @Test
    public void writeAsyncWithSystemMessages() throws IOException {
        WrappedSession wrappedSession = Mockito.mock(WrappedSession.class);
        Mockito.when(session.getSession()).thenReturn(wrappedSession);
        disableSessionExpirationMessages(messages);
        new MetadataWriter().write(ui, writer, false, true, messages);
        Assert.assertEquals("{\"async\":true,\"timedRedirect\":{\"interval\":15,\"url\":\"\"}}", writer.getBuffer().toString());
    }
}

