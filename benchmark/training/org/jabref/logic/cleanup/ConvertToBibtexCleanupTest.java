package org.jabref.logic.cleanup;


import FieldName.DATE;
import FieldName.FILE;
import FieldName.JOURNAL;
import FieldName.JOURNALTITLE;
import FieldName.MONTH;
import FieldName.YEAR;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConvertToBibtexCleanupTest {
    private ConvertToBibtexCleanup worker;

    @Test
    public void cleanupMovesDateToYearAndMonth() {
        BibEntry entry = new BibEntry().withField("date", "2011-01");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.empty(), entry.getField(DATE));
        Assertions.assertEquals(Optional.of("2011"), entry.getField(YEAR));
        Assertions.assertEquals(Optional.of("#jan#"), entry.getField(MONTH));
    }

    @Test
    public void cleanupWithYearAlreadyPresentDoesNothing() {
        BibEntry entry = new BibEntry();
        entry.setField("year", "2011");
        entry.setField("date", "2012");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.of("2011"), entry.getField(YEAR));
        Assertions.assertEquals(Optional.of("2012"), entry.getField(DATE));
    }

    @Test
    public void cleanupMovesJournaltitleToJournal() {
        BibEntry entry = new BibEntry().withField("journaltitle", "Best of JabRef");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.empty(), entry.getField(JOURNALTITLE));
        Assertions.assertEquals(Optional.of("Best of JabRef"), entry.getField(JOURNAL));
    }

    @Test
    public void cleanUpDoesntMoveFileField() {
        String fileField = ":Ambriola2006 - On the Systematic Analysis of Natural Language Requirements with CIRCE.pdf:PDF";
        BibEntry entry = new BibEntry().withField(FILE, fileField);
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.of(fileField), entry.getField(FILE));
    }
}

