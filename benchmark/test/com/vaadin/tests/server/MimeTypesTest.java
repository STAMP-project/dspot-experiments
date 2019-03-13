package com.vaadin.tests.server;


import com.vaadin.server.ClassResource;
import com.vaadin.ui.Embedded;
import org.junit.Assert;
import org.junit.Test;


public class MimeTypesTest {
    @Test
    public void testEmbeddedPDF() {
        Embedded e = new Embedded("A pdf", new ClassResource("file.pddf"));
        Assert.assertEquals("Invalid mimetype", "application/octet-stream", e.getMimeType());
        e = new Embedded("A pdf", new ClassResource("file.pdf"));
        Assert.assertEquals("Invalid mimetype", "application/pdf", e.getMimeType());
    }
}

