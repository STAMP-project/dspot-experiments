package org.jabref.cli;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.jabref.logic.importer.ImportFormatPreferences;
import org.jabref.logic.importer.ParserResult;
import org.jabref.model.database.BibDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AuxCommandLineTest {
    private ImportFormatPreferences importFormatPreferences;

    @Test
    public void test() throws IOException, URISyntaxException {
        InputStream originalStream = AuxCommandLineTest.class.getResourceAsStream("origin.bib");
        File auxFile = Paths.get(AuxCommandLineTest.class.getResource("paper.aux").toURI()).toFile();
        try (InputStreamReader originalReader = new InputStreamReader(originalStream, StandardCharsets.UTF_8)) {
            ParserResult result = parse(originalReader);
            AuxCommandLine auxCommandLine = new AuxCommandLine(auxFile.getAbsolutePath(), result.getDatabase());
            BibDatabase newDB = auxCommandLine.perform();
            Assertions.assertNotNull(newDB);
            Assertions.assertEquals(2, newDB.getEntries().size());
        }
    }
}

