package org.jabref.model.entry;


import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FileFieldWriterTest {
    @Test
    public void emptyListForEmptyInput() {
        String emptyInput = "";
        String nullInput = null;
        Assertions.assertEquals(Collections.emptyList(), FileFieldParser.parse(emptyInput));
        Assertions.assertEquals(Collections.emptyList(), FileFieldParser.parse(nullInput));
    }

    @Test
    public void parseCorrectInput() {
        String input = "Desc:File.PDF:PDF";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("Desc", "File.PDF", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void ingoreMissingDescription() {
        String input = ":wei2005ahp.pdf:PDF";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("", "wei2005ahp.pdf", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void interpreteLinkAsOnlyMandatoryField() {
        String single = "wei2005ahp.pdf";
        String multiple = "wei2005ahp.pdf;other.pdf";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("", "wei2005ahp.pdf", "")), FileFieldParser.parse(single));
        Assertions.assertEquals(Arrays.asList(new LinkedFile("", "wei2005ahp.pdf", ""), new LinkedFile("", "other.pdf", "")), FileFieldParser.parse(multiple));
    }

    @Test
    public void escapedCharactersInDescription() {
        String input = "test\\:\\;:wei2005ahp.pdf:PDF";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("test:;", "wei2005ahp.pdf", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void handleXmlCharacters() {
        String input = "test&#44\\;st\\:\\;:wei2005ahp.pdf:PDF";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("test&#44;st:;", "wei2005ahp.pdf", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void handleEscapedFilePath() {
        String input = "desc:C\\:\\\\test.pdf:PDF";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("desc", "C:\\test.pdf", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void subsetOfFieldsResultsInFileLink() {
        String descOnly = "file.pdf::";
        String fileOnly = ":file.pdf";
        String typeOnly = "::file.pdf";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("", "file.pdf", "")), FileFieldParser.parse(descOnly));
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("", "file.pdf", "")), FileFieldParser.parse(fileOnly));
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("", "file.pdf", "")), FileFieldParser.parse(typeOnly));
    }

    @Test
    public void tooManySeparators() {
        String input = "desc:file.pdf:PDF:asdf";
        Assertions.assertEquals(Collections.singletonList(new LinkedFile("desc", "file.pdf", "PDF")), FileFieldParser.parse(input));
    }

    @Test
    public void testQuoteStandard() {
        Assertions.assertEquals("a", FileFieldWriter.quote("a"));
    }

    @Test
    public void testQuoteAllCharacters() {
        Assertions.assertEquals("a\\:\\;\\\\", FileFieldWriter.quote("a:;\\"));
    }

    @Test
    public void testQuoteEmpty() {
        Assertions.assertEquals("", FileFieldWriter.quote(""));
    }

    @Test
    public void testQuoteNull() {
        Assertions.assertNull(FileFieldWriter.quote(null));
    }

    @Test
    public void testEncodeStringArray() {
        Assertions.assertEquals("a:b;c:d", FileFieldWriter.encodeStringArray(new String[][]{ new String[]{ "a", "b" }, new String[]{ "c", "d" } }));
        Assertions.assertEquals("a:;c:d", FileFieldWriter.encodeStringArray(new String[][]{ new String[]{ "a", "" }, new String[]{ "c", "d" } }));
        Assertions.assertEquals((("a:" + null) + ";c:d"), FileFieldWriter.encodeStringArray(new String[][]{ new String[]{ "a", null }, new String[]{ "c", "d" } }));
        Assertions.assertEquals("a:\\:b;c\\;:d", FileFieldWriter.encodeStringArray(new String[][]{ new String[]{ "a", ":b" }, new String[]{ "c;", "d" } }));
    }

    @Test
    public void testFileFieldWriterGetStringRepresentation() {
        LinkedFile file = new LinkedFile("test", "X:\\Users\\abc.pdf", "PDF");
        Assertions.assertEquals("test:X\\:/Users/abc.pdf:PDF", FileFieldWriter.getStringRepresentation(file));
    }
}

