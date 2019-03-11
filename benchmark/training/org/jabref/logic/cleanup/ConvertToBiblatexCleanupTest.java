package org.jabref.logic.cleanup;


import FieldName.DATE;
import FieldName.JOURNAL;
import FieldName.JOURNALTITLE;
import FieldName.MONTH;
import FieldName.YEAR;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConvertToBiblatexCleanupTest {
    private ConvertToBiblatexCleanup worker;

    @Test
    public void cleanupMovesYearMonthToDate() {
        BibEntry entry = new BibEntry();
        entry.setField("year", "2011");
        entry.setField("month", "#jan#");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.empty(), entry.getField(YEAR));
        Assertions.assertEquals(Optional.empty(), entry.getField(MONTH));
        Assertions.assertEquals(Optional.of("2011-01"), entry.getField(DATE));
    }

    @Test
    public void cleanupWithDateAlreadyPresentDoesNothing() {
        BibEntry entry = new BibEntry();
        entry.setField("year", "2011");
        entry.setField("month", "#jan#");
        entry.setField("date", "2012");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.of("2011"), entry.getField(YEAR));
        Assertions.assertEquals(Optional.of("#jan#"), entry.getField(MONTH));
        Assertions.assertEquals(Optional.of("2012"), entry.getField(DATE));
    }

    @Test
    public void cleanupMovesJournalToJournaltitle() {
        BibEntry entry = new BibEntry().withField("journal", "Best of JabRef");
        worker.cleanup(entry);
        Assertions.assertEquals(Optional.empty(), entry.getField(JOURNAL));
        Assertions.assertEquals(Optional.of("Best of JabRef"), entry.getField(JOURNALTITLE));
    }
}

