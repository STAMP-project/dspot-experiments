package org.jabref.logic.shared;


import BibDatabaseMode.BIBTEX;
import EntryEventSource.SHARED;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jabref.logic.exporter.MetaDataSerializer;
import org.jabref.logic.formatter.casechanger.LowerCaseFormatter;
import org.jabref.logic.shared.exception.OfflineLockException;
import org.jabref.model.bibtexkeypattern.GlobalBibtexKeyPattern;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.shared.DBMSType;
import org.jabref.model.database.shared.DatabaseNotSupportedException;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.metadata.MetaData;
import org.jabref.testutils.category.DatabaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runners.Parameterized;


@DatabaseTest
public class DBMSSynchronizerTest {
    @Parameterized.Parameter
    public DBMSType dbmsType;

    private DBMSSynchronizer dbmsSynchronizer;

    private DBMSConnection dbmsConnection;

    private DBMSProcessor dbmsProcessor;

    private BibDatabase bibDatabase;

    private GlobalBibtexKeyPattern pattern;

    @Test
    public void testEntryAddedEventListener() {
        BibEntry expectedEntry = getBibEntryExample(1);
        BibEntry furtherEntry = getBibEntryExample(1);
        bibDatabase.insertEntry(expectedEntry);
        // should not add into shared database.
        bibDatabase.insertEntry(furtherEntry, SHARED);
        List<BibEntry> actualEntries = dbmsProcessor.getSharedEntries();
        Assertions.assertEquals(1, actualEntries.size());
        Assertions.assertEquals(expectedEntry, actualEntries.get(0));
    }

    @Test
    public void testFieldChangedEventListener() {
        BibEntry expectedEntry = getBibEntryExample(1);
        expectedEntry.registerListener(dbmsSynchronizer);
        bibDatabase.insertEntry(expectedEntry);
        expectedEntry.setField("author", "Brad L and Gilson");
        expectedEntry.setField("title", "The micro multiplexer", SHARED);
        List<BibEntry> actualEntries = dbmsProcessor.getSharedEntries();
        Assertions.assertEquals(1, actualEntries.size());
        Assertions.assertEquals(expectedEntry.getField("author"), actualEntries.get(0).getField("author"));
        Assertions.assertEquals("The nano processor1", actualEntries.get(0).getField("title").get());
    }

    @Test
    public void testEntryRemovedEventListener() {
        BibEntry bibEntry = getBibEntryExample(1);
        bibDatabase.insertEntry(bibEntry);
        List<BibEntry> actualEntries = dbmsProcessor.getSharedEntries();
        Assertions.assertEquals(1, actualEntries.size());
        Assertions.assertEquals(bibEntry, actualEntries.get(0));
        bibDatabase.removeEntry(bibEntry);
        actualEntries = dbmsProcessor.getSharedEntries();
        Assertions.assertEquals(0, actualEntries.size());
        bibDatabase.insertEntry(bibEntry);
        bibDatabase.removeEntry(bibEntry, SHARED);
        actualEntries = dbmsProcessor.getSharedEntries();
        Assertions.assertEquals(1, actualEntries.size());
        Assertions.assertEquals(bibEntry, actualEntries.get(0));
    }

    @Test
    public void testMetaDataChangedEventListener() {
        MetaData testMetaData = new MetaData();
        testMetaData.registerListener(dbmsSynchronizer);
        dbmsSynchronizer.setMetaData(testMetaData);
        testMetaData.setMode(BIBTEX);
        Map<String, String> expectedMap = MetaDataSerializer.getSerializedStringMap(testMetaData, pattern);
        Map<String, String> actualMap = dbmsProcessor.getSharedMetaData();
        Assertions.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testInitializeDatabases() throws SQLException, DatabaseNotSupportedException {
        clear();
        dbmsSynchronizer.initializeDatabases();
        Assertions.assertTrue(dbmsProcessor.checkBaseIntegrity());
        dbmsSynchronizer.initializeDatabases();
        Assertions.assertTrue(dbmsProcessor.checkBaseIntegrity());
    }

    @Test
    public void testSynchronizeLocalDatabaseWithEntryRemoval() {
        List<BibEntry> expectedBibEntries = Arrays.asList(getBibEntryExample(1), getBibEntryExample(2));
        dbmsProcessor.insertEntry(expectedBibEntries.get(0));
        dbmsProcessor.insertEntry(expectedBibEntries.get(1));
        Assertions.assertTrue(bibDatabase.getEntries().isEmpty());
        dbmsSynchronizer.synchronizeLocalDatabase();
        Assertions.assertEquals(expectedBibEntries, bibDatabase.getEntries());
        dbmsProcessor.removeEntry(expectedBibEntries.get(0));
        dbmsProcessor.removeEntry(expectedBibEntries.get(1));
        expectedBibEntries = new ArrayList();
        dbmsSynchronizer.synchronizeLocalDatabase();
        Assertions.assertEquals(expectedBibEntries, bibDatabase.getEntries());
    }

    @Test
    public void testSynchronizeLocalDatabaseWithEntryUpdate() throws SQLException, OfflineLockException {
        BibEntry bibEntry = getBibEntryExample(1);
        bibDatabase.insertEntry(bibEntry);
        Assertions.assertEquals(1, bibDatabase.getEntries().size());
        BibEntry modifiedBibEntry = getBibEntryExample(1);
        modifiedBibEntry.setField("custom", "custom value");
        modifiedBibEntry.clearField("title");
        modifiedBibEntry.setType("article");
        dbmsProcessor.updateEntry(modifiedBibEntry);
        // testing point
        dbmsSynchronizer.synchronizeLocalDatabase();
        Assertions.assertEquals(bibDatabase.getEntries(), dbmsProcessor.getSharedEntries());
    }

    @Test
    public void testApplyMetaData() {
        BibEntry bibEntry = getBibEntryExample(1);
        bibDatabase.insertEntry(bibEntry);
        MetaData testMetaData = new MetaData();
        testMetaData.setSaveActions(new org.jabref.model.cleanup.FieldFormatterCleanups(true, Collections.singletonList(new org.jabref.model.cleanup.FieldFormatterCleanup("author", new LowerCaseFormatter()))));
        dbmsSynchronizer.setMetaData(testMetaData);
        dbmsSynchronizer.applyMetaData();
        Assertions.assertEquals("wirthlin, michael j1", bibEntry.getField("author").get());
    }
}

