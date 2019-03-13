package liquibase.changelog.filter;


import liquibase.ContextExpression;
import liquibase.Contexts;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.sql.visitor.AbstractSqlVisitor;
import org.junit.Assert;
import org.junit.Test;


public class ContextChangeSetFilterTest {
    private static final class TestSqlVisitor extends AbstractSqlVisitor {
        public TestSqlVisitor(final String... contexts) {
            setContexts(new ContextExpression(contexts));
        }

        @Override
        public String modifySql(String sql, Database database) {
            throw new UnsupportedOperationException("modifySql has not been implemented");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("getName has not been implemented");
        }

        @Override
        public String getSerializedObjectNamespace() {
            return STANDARD_CHANGELOG_NAMESPACE;
        }
    }

    @Test
    public void emptyContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter();
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void nullContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter();
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void reallyNullContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(null);
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void nullListContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter();
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void singleContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("TEST1"));
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void multiContexts() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("test1", "test2"));
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, "test3", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test3, test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test3, TEST1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void multiContextsSingeParameter() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("test1, test2"));
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test2", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test1, test2", null, null)).isAccepted());
        Assert.assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, "test3", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test3, test1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, "test3, TEST1", null, null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
    }

    @Test
    public void visitorContextFilterLowerLower() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("test1"));
        ChangeSet changeSet = new ChangeSet(null, null, false, false, null, null, null, null);
        changeSet.addSqlVisitor(new ContextChangeSetFilterTest.TestSqlVisitor("test1"));
        Assert.assertTrue(filter.accepts(changeSet).isAccepted());
        Assert.assertEquals(1, changeSet.getSqlVisitors().size());
    }

    @Test
    public void visitorContextFilterUpperLower() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("TEST1"));
        ChangeSet changeSet = new ChangeSet(null, null, false, false, null, null, null, null);
        changeSet.addSqlVisitor(new ContextChangeSetFilterTest.TestSqlVisitor("test1"));
        Assert.assertTrue(filter.accepts(changeSet).isAccepted());
        Assert.assertEquals(1, changeSet.getSqlVisitors().size());
    }

    @Test
    public void visitorContextFilterUpperUpper() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("TEST1"));
        ChangeSet changeSet = new ChangeSet(null, null, false, false, null, null, null, null);
        changeSet.addSqlVisitor(new ContextChangeSetFilterTest.TestSqlVisitor("TEST1"));
        Assert.assertTrue(filter.accepts(changeSet).isAccepted());
        Assert.assertEquals(1, changeSet.getSqlVisitors().size());
    }

    @Test
    public void visitorContextFilterLowerUpper() {
        ContextChangeSetFilter filter = new ContextChangeSetFilter(new Contexts("test1"));
        ChangeSet changeSet = new ChangeSet(null, null, false, false, null, null, null, null);
        changeSet.addSqlVisitor(new ContextChangeSetFilterTest.TestSqlVisitor("TEST1"));
        Assert.assertTrue(filter.accepts(changeSet).isAccepted());
        Assert.assertEquals(1, changeSet.getSqlVisitors().size());
    }
}

