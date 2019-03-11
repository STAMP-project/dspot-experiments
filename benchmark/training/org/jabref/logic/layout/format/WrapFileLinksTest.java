package org.jabref.logic.layout.format;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class WrapFileLinksTest {
    private WrapFileLinks formatter;

    @Test
    public void testEmpty() {
        Assertions.assertEquals("", formatter.format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertEquals("", formatter.format(null));
    }

    @Test
    public void testFileExtension() {
        formatter.setArgument("\\x");
        Assertions.assertEquals("pdf", formatter.format("test.pdf"));
    }

    @Test
    public void testFileExtensionNoExtension() {
        formatter.setArgument("\\x");
        Assertions.assertEquals("", formatter.format("test"));
    }

    @Test
    public void testPlainTextString() {
        formatter.setArgument("x");
        Assertions.assertEquals("x", formatter.format("test.pdf"));
    }

    @Test
    public void testDescription() {
        formatter.setArgument("\\d");
        Assertions.assertEquals("Test file", formatter.format("Test file:test.pdf:PDF"));
    }

    @Test
    public void testDescriptionNoDescription() {
        formatter.setArgument("\\d");
        Assertions.assertEquals("", formatter.format("test.pdf"));
    }

    @Test
    public void testType() {
        formatter.setArgument("\\f");
        Assertions.assertEquals("PDF", formatter.format("Test file:test.pdf:PDF"));
    }

    @Test
    public void testTypeNoType() {
        formatter.setArgument("\\f");
        Assertions.assertEquals("", formatter.format("test.pdf"));
    }

    @Test
    public void testIterator() {
        formatter.setArgument("\\i");
        Assertions.assertEquals("1", formatter.format("Test file:test.pdf:PDF"));
    }

    @Test
    public void testIteratorTwoItems() {
        formatter.setArgument("\\i\n");
        Assertions.assertEquals("1\n2\n", formatter.format("Test file:test.pdf:PDF;test2.pdf"));
    }

    @Test
    public void testEndingBracket() {
        formatter.setArgument("(\\d)");
        Assertions.assertEquals("(Test file)", formatter.format("Test file:test.pdf:PDF"));
    }

    @Test
    public void testPath() throws IOException {
        FileLinkPreferences preferences = new FileLinkPreferences(Collections.emptyList(), Collections.singletonList("src/test/resources/pdfs/"));
        formatter = new WrapFileLinks(preferences);
        formatter.setArgument("\\p");
        Assertions.assertEquals(new File("src/test/resources/pdfs/encrypted.pdf").getCanonicalPath(), formatter.format("Preferences:encrypted.pdf:PDF"));
    }

    @Test
    public void testPathFallBackToGeneratedDir() throws IOException {
        FileLinkPreferences preferences = new FileLinkPreferences(Collections.singletonList("src/test/resources/pdfs/"), Collections.emptyList());
        formatter = new WrapFileLinks(preferences);
        formatter.setArgument("\\p");
        Assertions.assertEquals(new File("src/test/resources/pdfs/encrypted.pdf").getCanonicalPath(), formatter.format("Preferences:encrypted.pdf:PDF"));
    }

    @Test
    public void testPathReturnsRelativePathIfNotFound() {
        FileLinkPreferences preferences = new FileLinkPreferences(Collections.emptyList(), Collections.singletonList("src/test/resources/pdfs/"));
        formatter = new WrapFileLinks(preferences);
        formatter.setArgument("\\p");
        Assertions.assertEquals("test.pdf", formatter.format("Preferences:test.pdf:PDF"));
    }

    @Test
    public void testRelativePath() {
        formatter.setArgument("\\r");
        Assertions.assertEquals("test.pdf", formatter.format("Test file:test.pdf:PDF"));
    }
}

