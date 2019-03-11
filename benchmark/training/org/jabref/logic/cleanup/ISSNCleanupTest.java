package org.jabref.logic.cleanup;


import CleanupPreset.CleanupStep;
import java.util.Optional;
import org.jabref.model.entry.BibEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ISSNCleanupTest {
    private CleanupWorker worker;

    @Test
    public void cleanupISSNReturnsCorrectISSN() {
        CleanupPreset preset = new CleanupPreset(CleanupStep.CLEAN_UP_ISSN);
        BibEntry entry = new BibEntry();
        entry.setField("issn", "0123-4567");
        worker.cleanup(preset, entry);
        Assertions.assertEquals(Optional.of("0123-4567"), entry.getField("issn"));
    }

    @Test
    public void cleanupISSNAddsMissingDash() {
        CleanupPreset preset = new CleanupPreset(CleanupStep.CLEAN_UP_ISSN);
        BibEntry entry = new BibEntry();
        entry.setField("issn", "01234567");
        worker.cleanup(preset, entry);
        Assertions.assertEquals(Optional.of("0123-4567"), entry.getField("issn"));
    }

    @Test
    public void cleanupISSNJunkStaysJunk() {
        CleanupPreset preset = new CleanupPreset(CleanupStep.CLEAN_UP_ISSN);
        BibEntry entry = new BibEntry();
        entry.setField("issn", "Banana");
        worker.cleanup(preset, entry);
        Assertions.assertEquals(Optional.of("Banana"), entry.getField("issn"));
    }
}

