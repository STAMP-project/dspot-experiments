package org.jabref.logic.bibtex.comparator;


import java.util.Collections;
import java.util.Optional;
import org.jabref.model.database.BibDatabaseContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BibDatabaseDiffTest {
    @Test
    public void compareOfEmptyDatabasesReportsNoDifferences() throws Exception {
        BibDatabaseDiff diff = BibDatabaseDiff.compare(new BibDatabaseContext(), new BibDatabaseContext());
        Assertions.assertEquals(Optional.empty(), diff.getPreambleDifferences());
        Assertions.assertEquals(Optional.empty(), diff.getMetaDataDifferences());
        Assertions.assertEquals(Collections.emptyList(), diff.getBibStringDifferences());
        Assertions.assertEquals(Collections.emptyList(), diff.getEntryDifferences());
    }
}

