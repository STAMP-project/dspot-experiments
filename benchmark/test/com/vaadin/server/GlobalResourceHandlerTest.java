package com.vaadin.server;


import java.io.IOException;
import org.junit.Test;


public class GlobalResourceHandlerTest {
    @Test
    public void globalResourceHandlerShouldWorkWithEncodedFilename() throws IOException {
        assertEncodedFilenameIsHandled("simple.txt", "simple.txt");
        assertEncodedFilenameIsHandled("with spaces.txt", "with+spaces.txt");
        assertEncodedFilenameIsHandled("with # hash.txt", "with+%23+hash.txt");
        assertEncodedFilenameIsHandled("with ; semicolon.txt", "with+%3B+semicolon.txt");
        assertEncodedFilenameIsHandled("with , comma.txt", "with+%2C+comma.txt");
        // ResourceReference.encodeFileName does not encode slashes and
        // backslashes
        // See comment inside2 method for more details
        assertEncodedFilenameIsHandled("with \\ backslash.txt", "with+\\+backslash.txt");
        assertEncodedFilenameIsHandled("with / slash.txt", "with+/+slash.txt");
    }
}

