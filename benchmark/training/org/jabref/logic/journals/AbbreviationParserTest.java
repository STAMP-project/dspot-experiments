package org.jabref.logic.journals;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class AbbreviationParserTest {
    @Test
    public void testReadJournalListFromResource() {
        AbbreviationParser ap = new AbbreviationParser();
        ap.readJournalListFromResource("/journals/journalList.txt");
        Assertions.assertFalse(ap.getAbbreviations().isEmpty());
    }
}

