package org.jabref.logic.importer;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.jabref.model.database.BibDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibDatabaseTestsWithFiles {
    private ImportFormatPreferences importFormatPreferences;

    @Test
    public void resolveStrings() throws IOException {
        try (FileInputStream stream = new FileInputStream("src/test/resources/org/jabref/util/twente.bib");InputStreamReader fr = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            ParserResult result = parse(fr);
            BibDatabase db = result.getDatabase();
            Assertions.assertEquals("Arvind", db.resolveForStrings("#Arvind#"));
            Assertions.assertEquals("Patterson, David", db.resolveForStrings("#Patterson#"));
            Assertions.assertEquals("Arvind and Patterson, David", db.resolveForStrings("#Arvind# and #Patterson#"));
            // Strings that are not found return just the given string.
            Assertions.assertEquals("#unknown#", db.resolveForStrings("#unknown#"));
        }
    }
}

