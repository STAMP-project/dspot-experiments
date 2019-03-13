package liquibase.changelog.visitor;


import ChangeSet.ExecType.EXECUTED;
import java.util.Collections;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.filter.ChangeSetFilterResult;
import liquibase.database.Database;
import liquibase.exception.LiquibaseException;
import org.junit.Test;
import org.mockito.Mockito;


public class ChangeLogSyncVisitorTest {
    private ChangeSet changeSet;

    private DatabaseChangeLog databaseChangeLog;

    @Test
    public void testVisitDatabaseConstructor() throws LiquibaseException {
        Database mockDatabase = Mockito.mock(Database.class);
        ChangeLogSyncVisitor visitor = new ChangeLogSyncVisitor(mockDatabase);
        visitor.visit(changeSet, databaseChangeLog, mockDatabase, Collections.<ChangeSetFilterResult>emptySet());
        Mockito.verify(mockDatabase).markChangeSetExecStatus(changeSet, EXECUTED);
    }

    @Test
    public void testVisitListenerConstructor() throws LiquibaseException {
        Database mockDatabase = Mockito.mock(Database.class);
        ChangeLogSyncListener mockListener = Mockito.mock(ChangeLogSyncListener.class);
        ChangeLogSyncVisitor visitor = new ChangeLogSyncVisitor(mockDatabase, mockListener);
        visitor.visit(changeSet, databaseChangeLog, mockDatabase, Collections.<ChangeSetFilterResult>emptySet());
        Mockito.verify(mockDatabase).markChangeSetExecStatus(changeSet, EXECUTED);
        Mockito.verify(mockListener).markedRan(changeSet, databaseChangeLog, mockDatabase);
    }
}

