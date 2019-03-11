package com.commafeed.backend.opml;


import java.io.IOException;
import org.junit.Test;


public class OPMLImporterTest {
    @Test
    public void testOpmlV10() throws IOException {
        testOpmlVersion("/opml/opml_v1.0.xml");
    }

    @Test
    public void testOpmlV11() throws IOException {
        testOpmlVersion("/opml/opml_v1.1.xml");
    }

    @Test
    public void testOpmlV20() throws IOException {
        testOpmlVersion("/opml/opml_v2.0.xml");
    }

    @Test
    public void testOpmlNoVersion() throws IOException {
        testOpmlVersion("/opml/opml_noversion.xml");
    }
}

