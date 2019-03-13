package org.jabref.model.entry;


import BibEntry.ID_FIELD;
import FieldName.CROSSREF;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jabref.model.database.BibDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class BibEntryTest {
    private BibEntry entry;

    @Test
    public void notOverrideReservedFields() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> entry.setField(ID_FIELD, "somevalue"));
    }

    @Test
    public void notClearReservedFields() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> entry.clearField(ID_FIELD));
    }

    @Test
    public void getFieldIsCaseInsensitive() throws Exception {
        entry.setField("TeSt", "value");
        Assertions.assertEquals(Optional.of("value"), entry.getField("tEsT"));
    }

    @Test
    public void clonedBibentryHasUniqueID() throws Exception {
        BibEntry entry = new BibEntry();
        BibEntry entryClone = ((BibEntry) (entry.clone()));
        Assertions.assertNotEquals(entry.getId(), entryClone.getId());
    }

    @Test
    public void testGetAndAddToLinkedFileList() {
        List<LinkedFile> files = entry.getFiles();
        files.add(new LinkedFile("", "", ""));
        entry.setFiles(files);
        Assertions.assertEquals(Arrays.asList(new LinkedFile("", "", "")), entry.getFiles());
    }

    @Test
    public void testGetEmptyKeywords() {
        KeywordList actual = entry.getKeywords(',');
        Assertions.assertEquals(new KeywordList(), actual);
    }

    @Test
    public void testGetSingleKeywords() {
        entry.addKeyword("kw", ',');
        KeywordList actual = entry.getKeywords(',');
        Assertions.assertEquals(new KeywordList(new Keyword("kw")), actual);
    }

    @Test
    public void testGetKeywords() {
        entry.addKeyword("kw", ',');
        entry.addKeyword("kw2", ',');
        entry.addKeyword("kw3", ',');
        KeywordList actual = entry.getKeywords(',');
        Assertions.assertEquals(new KeywordList(new Keyword("kw"), new Keyword("kw2"), new Keyword("kw3")), actual);
    }

    @Test
    public void testGetEmptyResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry);
        KeywordList actual = entry.getResolvedKeywords(',', database);
        Assertions.assertEquals(new KeywordList(), actual);
    }

    @Test
    public void testGetSingleResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        entry2.addKeyword("kw", ',');
        database.insertEntry(entry2);
        database.insertEntry(entry);
        KeywordList actual = entry.getResolvedKeywords(',', database);
        Assertions.assertEquals(new KeywordList(new Keyword("kw")), actual);
    }

    @Test
    public void testGetResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        entry2.addKeyword("kw", ',');
        entry2.addKeyword("kw2", ',');
        entry2.addKeyword("kw3", ',');
        database.insertEntry(entry2);
        database.insertEntry(entry);
        KeywordList actual = entry.getResolvedKeywords(',', database);
        Assertions.assertEquals(new KeywordList(new Keyword("kw"), new Keyword("kw2"), new Keyword("kw3")), actual);
    }
}

