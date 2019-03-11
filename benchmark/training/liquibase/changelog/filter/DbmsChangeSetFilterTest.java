package liquibase.changelog.filter;


import liquibase.changelog.ChangeSet;
import liquibase.database.core.MySQLDatabase;
import org.junit.Assert;
import org.junit.Test;


// @Test
// public void multiContexts() {
// DbmsChangeSetFilter filter = new DbmsChangeSetFilter("mysql", "oracle");
// 
// assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "mysql")));
// assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "oracle")));
// assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "oracle, mysql")));
// assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "db2, oracle")));
// assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, null, "db2")));
// assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null)));
// }
public class DbmsChangeSetFilterTest {
    // @Test
    // public void emptyDbms() {
    // DbmsChangeSetFilter filter = new DbmsChangeSetFilter();
    // 
    // assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "mysql")));
    // assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "oracle")));
    // assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "oracle, mysql")));
    // assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null)));
    // }
    @Test
    public void singleDbms() {
        DbmsChangeSetFilter filter = new DbmsChangeSetFilter(new MySQLDatabase());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "mysql", null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, "mysql, oracle", null)).isAccepted());
        Assert.assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, null, "oracle", null)).isAccepted());
        Assert.assertTrue(filter.accepts(new ChangeSet(null, null, false, false, null, null, null, null)).isAccepted());
        Assert.assertFalse(filter.accepts(new ChangeSet(null, null, false, false, null, null, "h2,!mysql", null)).isAccepted());
    }
}

