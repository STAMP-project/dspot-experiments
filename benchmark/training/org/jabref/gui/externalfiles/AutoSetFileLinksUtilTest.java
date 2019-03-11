package org.jabref.gui.externalfiles;


import java.util.Collections;
import java.util.List;
import org.jabref.gui.externalfiletype.ExternalFileTypes;
import org.jabref.logic.util.io.AutoLinkPreferences;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexEntryTypes;
import org.jabref.model.entry.LinkedFile;
import org.jabref.model.metadata.FilePreferences;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class AutoSetFileLinksUtilTest {
    private final FilePreferences fileDirPrefs = Mockito.mock(FilePreferences.class);

    private final AutoLinkPreferences autoLinkPrefs = new AutoLinkPreferences(false, "", true, ';');

    private final BibDatabaseContext databaseContext = Mockito.mock(BibDatabaseContext.class);

    private final ExternalFileTypes externalFileTypes = Mockito.mock(ExternalFileTypes.class);

    private final BibEntry entry = new BibEntry(BibtexEntryTypes.ARTICLE);

    @Test
    public void test() throws Exception {
        // Due to mocking the externalFileType class, the file extension will not be found
        List<LinkedFile> expected = Collections.singletonList(new LinkedFile("", "CiteKey.pdf", ""));
        AutoSetFileLinksUtil util = new AutoSetFileLinksUtil(databaseContext, fileDirPrefs, autoLinkPrefs, externalFileTypes);
        List<LinkedFile> actual = util.findAssociatedNotLinkedFiles(entry);
        Assertions.assertEquals(expected, actual);
    }
}

