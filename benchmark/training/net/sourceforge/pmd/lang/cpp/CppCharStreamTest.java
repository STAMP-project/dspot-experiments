/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.cpp;


import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;


public class CppCharStreamTest {
    @Test
    public void testContinuationUnix() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testContinuationWindows() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\\r\nb"));
        assertStream(stream, "ab");
    }

    @Test
    public void testBackup() throws IOException {
        CppCharStream stream = new CppCharStream(new StringReader("a\\b\\\rc"));
        assertStream(stream, "a\\b\\\rc");
    }
}

