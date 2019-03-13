package liquibase.changelog;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import liquibase.Contexts;
import liquibase.RuntimeEnvironment;
import liquibase.changelog.filter.ChangeSetFilterResult;
import liquibase.changelog.visitor.ChangeSetVisitor;
import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.exception.LiquibaseException;
import org.junit.Assert;
import org.junit.Test;

import static ChangeSetVisitor.Direction.FORWARD;
import static ChangeSetVisitor.Direction.REVERSE;


public class ChangeLogIteratorTest {
    private DatabaseChangeLog changeLog;

    @Test
    public void runChangeSet_emptyFiltersIterator() throws Exception {
        ChangeLogIteratorTest.TestChangeSetVisitor testChangeLogVisitor = new ChangeLogIteratorTest.TestChangeSetVisitor();
        ChangeLogIterator iterator = new ChangeLogIterator(changeLog);
        iterator.run(testChangeLogVisitor, new RuntimeEnvironment(null, null, null));
        Assert.assertEquals(6, testChangeLogVisitor.visitedChangeSets.size());
    }

    @Test
    public void runChangeSet_singleFilterIterator() throws Exception {
        ChangeLogIteratorTest.TestChangeSetVisitor testChangeLogVisitor = new ChangeLogIteratorTest.TestChangeSetVisitor();
        ChangeLogIterator iterator = new ChangeLogIterator(changeLog, new liquibase.changelog.filter.ContextChangeSetFilter(new Contexts("test1")));
        iterator.run(testChangeLogVisitor, new RuntimeEnvironment(null, null, null));
        Assert.assertEquals(4, testChangeLogVisitor.visitedChangeSets.size());
    }

    @Test
    public void runChangeSet_doubleFilterIterator() throws Exception {
        ChangeLogIteratorTest.TestChangeSetVisitor testChangeLogVisitor = new ChangeLogIteratorTest.TestChangeSetVisitor();
        ChangeLogIterator iterator = new ChangeLogIterator(changeLog, new liquibase.changelog.filter.ContextChangeSetFilter(new Contexts("test1")), new liquibase.changelog.filter.DbmsChangeSetFilter(new MySQLDatabase()));
        iterator.run(testChangeLogVisitor, new RuntimeEnvironment(null, null, null));
        Assert.assertEquals(3, testChangeLogVisitor.visitedChangeSets.size());
        Assert.assertEquals("1", testChangeLogVisitor.visitedChangeSets.get(0).getId());
        Assert.assertEquals("4", testChangeLogVisitor.visitedChangeSets.get(1).getId());
        Assert.assertEquals("5", testChangeLogVisitor.visitedChangeSets.get(2).getId());
    }

    @Test
    public void runChangeSet_reverseVisitor() throws Exception {
        ChangeLogIteratorTest.TestChangeSetVisitor testChangeLogVisitor = new ChangeLogIteratorTest.ReverseChangeSetVisitor();
        ChangeLogIterator iterator = new ChangeLogIterator(changeLog, new liquibase.changelog.filter.ContextChangeSetFilter(new Contexts("test1")), new liquibase.changelog.filter.DbmsChangeSetFilter(new MySQLDatabase()));
        iterator.run(testChangeLogVisitor, new RuntimeEnvironment(null, null, null));
        Assert.assertEquals(3, testChangeLogVisitor.visitedChangeSets.size());
        Assert.assertEquals("5", testChangeLogVisitor.visitedChangeSets.get(0).getId());
        Assert.assertEquals("4", testChangeLogVisitor.visitedChangeSets.get(1).getId());
        Assert.assertEquals("1", testChangeLogVisitor.visitedChangeSets.get(2).getId());
    }

    private static class TestChangeSetVisitor implements ChangeSetVisitor {
        public List<ChangeSet> visitedChangeSets = new ArrayList<ChangeSet>();

        @Override
        public Direction getDirection() {
            return FORWARD;
        }

        @Override
        public void visit(ChangeSet changeSet, DatabaseChangeLog databaseChangeLog, Database database, Set<ChangeSetFilterResult> filterResults) throws LiquibaseException {
            visitedChangeSets.add(changeSet);
        }
    }

    private static class ReverseChangeSetVisitor extends ChangeLogIteratorTest.TestChangeSetVisitor {
        @Override
        public Direction getDirection() {
            return REVERSE;
        }
    }
}

