package org.jabref.logic.pdf;


import FieldName.FILE;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.CustomEntryType;
import org.jabref.model.metadata.FilePreferences;
import org.jabref.model.pdf.FileAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class EntryAnnotationImporterTest {
    private final BibDatabaseContext databaseContext = Mockito.mock(BibDatabaseContext.class);

    private final BibEntry entry = new BibEntry(new CustomEntryType("EntryKey", "required", "optional"));

    @Test
    public void readEntryExampleThesis() {
        // given
        entry.setField(FILE, ":thesis-example.pdf:PDF");
        EntryAnnotationImporter entryAnnotationImporter = new EntryAnnotationImporter(entry);
        // when
        Map<Path, List<FileAnnotation>> annotations = entryAnnotationImporter.importAnnotationsFromFiles(databaseContext, Mockito.mock(FilePreferences.class));
        // then
        int fileCounter = 0;
        int annotationCounter = 0;
        for (List<FileAnnotation> annotationsOfFile : annotations.values()) {
            fileCounter++;
            annotationCounter += annotationsOfFile.size();
        }
        Assertions.assertEquals(1, fileCounter);
        Assertions.assertEquals(2, annotationCounter);
    }
}

