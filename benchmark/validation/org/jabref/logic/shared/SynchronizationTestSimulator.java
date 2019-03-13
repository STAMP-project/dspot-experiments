package org.jabref.logic.shared;


import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.shared.DBMSType;
import org.jabref.model.entry.BibEntry;
import org.jabref.testutils.category.DatabaseTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runners.Parameterized;


@DatabaseTest
public class SynchronizationTestSimulator {
    @Parameterized.Parameter
    public DBMSType dbmsType;

    private BibDatabaseContext clientContextA;

    private BibDatabaseContext clientContextB;

    private SynchronizationTestEventListener eventListenerB;// used to monitor occurring events


    private DBMSConnection dbmsConnection;

    @Test
    public void simulateEntryInsertionAndManualPull() {
        // client A inserts an entry
        clientContextA.getDatabase().insertEntry(getBibEntryExample(1));
        // client A inserts another entry
        clientContextA.getDatabase().insertEntry(getBibEntryExample(2));
        // client B pulls the changes
        clientContextB.getDBMSSynchronizer().pullChanges();
        Assertions.assertEquals(clientContextA.getDatabase().getEntries(), clientContextB.getDatabase().getEntries());
    }

    @Test
    public void simulateEntryUpdateAndManualPull() {
        BibEntry bibEntry = getBibEntryExample(1);
        // client A inserts an entry
        clientContextA.getDatabase().insertEntry(bibEntry);
        // client A changes the entry
        bibEntry.setField("custom", "custom value");
        // client B pulls the changes
        bibEntry.clearField("author");
        clientContextB.getDBMSSynchronizer().pullChanges();
        Assertions.assertEquals(clientContextA.getDatabase().getEntries(), clientContextB.getDatabase().getEntries());
    }

    @Test
    public void simulateEntryDelitionAndManualPull() {
        BibEntry bibEntry = getBibEntryExample(1);
        // client A inserts an entry
        clientContextA.getDatabase().insertEntry(bibEntry);
        // client B pulls the entry
        clientContextB.getDBMSSynchronizer().pullChanges();
        Assertions.assertFalse(clientContextA.getDatabase().getEntries().isEmpty());
        Assertions.assertFalse(clientContextB.getDatabase().getEntries().isEmpty());
        Assertions.assertEquals(clientContextA.getDatabase().getEntries(), clientContextB.getDatabase().getEntries());
        // client A removes the entry
        clientContextA.getDatabase().removeEntry(bibEntry);
        // client B pulls the change
        clientContextB.getDBMSSynchronizer().pullChanges();
        Assertions.assertTrue(clientContextA.getDatabase().getEntries().isEmpty());
        Assertions.assertTrue(clientContextB.getDatabase().getEntries().isEmpty());
    }

    @Test
    public void simulateUpdateOnNoLongerExistingEntry() {
        BibEntry bibEntryOfClientA = getBibEntryExample(1);
        // client A inserts an entry
        clientContextA.getDatabase().insertEntry(bibEntryOfClientA);
        // client B pulls the entry
        clientContextB.getDBMSSynchronizer().pullChanges();
        Assertions.assertFalse(clientContextA.getDatabase().getEntries().isEmpty());
        Assertions.assertFalse(clientContextB.getDatabase().getEntries().isEmpty());
        Assertions.assertEquals(clientContextA.getDatabase().getEntries(), clientContextB.getDatabase().getEntries());
        // client A removes the entry
        clientContextA.getDatabase().removeEntry(bibEntryOfClientA);
        Assertions.assertFalse(clientContextB.getDatabase().getEntries().isEmpty());
        Assertions.assertNull(eventListenerB.getSharedEntryNotPresentEvent());
        // client B tries to update the entry
        BibEntry bibEntryOfClientB = clientContextB.getDatabase().getEntries().get(0);
        bibEntryOfClientB.setField("year", "2009");
        // here a new SharedEntryNotPresentEvent has been thrown. In this case the user B would get an pop-up window.
        Assertions.assertNotNull(eventListenerB.getSharedEntryNotPresentEvent());
        Assertions.assertEquals(bibEntryOfClientB, eventListenerB.getSharedEntryNotPresentEvent().getBibEntry());
    }

    @Test
    public void simulateEntryChangeConflicts() {
        BibEntry bibEntryOfClientA = getBibEntryExample(1);
        // client A inserts an entry
        clientContextA.getDatabase().insertEntry(bibEntryOfClientA);
        // client B pulls the entry
        clientContextB.getDBMSSynchronizer().pullChanges();
        // A now increases the version number
        bibEntryOfClientA.setField("year", "2001");
        // B does nothing here, so there is no event occurrence
        // B now tries to update the entry
        Assertions.assertFalse(clientContextB.getDatabase().getEntries().isEmpty());
        Assertions.assertNull(eventListenerB.getUpdateRefusedEvent());
        BibEntry bibEntryOfClientB = clientContextB.getDatabase().getEntries().get(0);
        // B also tries to change something
        bibEntryOfClientB.setField("year", "2016");
        // B now cannot update the shared entry, due to optimistic offline lock.
        // In this case an BibEntry merge dialog pops up.
        Assertions.assertNotNull(eventListenerB.getUpdateRefusedEvent());
    }
}

