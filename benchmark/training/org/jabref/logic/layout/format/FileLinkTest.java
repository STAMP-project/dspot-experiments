package org.jabref.logic.layout.format;


import org.jabref.logic.layout.ParamLayoutFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FileLinkTest {
    private FileLinkPreferences prefs;

    @Test
    public void testEmpty() {
        Assertions.assertEquals("", format(""));
    }

    @Test
    public void testNull() {
        Assertions.assertEquals("", new FileLink(prefs).format(null));
    }

    @Test
    public void testOnlyFilename() {
        Assertions.assertEquals("test.pdf", format("test.pdf"));
    }

    @Test
    public void testCompleteRecord() {
        Assertions.assertEquals("test.pdf", format("paper:test.pdf:PDF"));
    }

    @Test
    public void testMultipleFiles() {
        ParamLayoutFormatter a = new FileLink(prefs);
        Assertions.assertEquals("test.pdf", a.format("paper:test.pdf:PDF;presentation:pres.ppt:PPT"));
    }

    @Test
    public void testMultipleFilesPick() {
        ParamLayoutFormatter a = new FileLink(prefs);
        a.setArgument("ppt");
        Assertions.assertEquals("pres.ppt", a.format("paper:test.pdf:PDF;presentation:pres.ppt:PPT"));
    }

    @Test
    public void testMultipleFilesPickNonExistant() {
        ParamLayoutFormatter a = new FileLink(prefs);
        a.setArgument("doc");
        Assertions.assertEquals("", a.format("paper:test.pdf:PDF;presentation:pres.ppt:PPT"));
    }
}

