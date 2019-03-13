package liquibase.diff.output.changelog;


import java.util.List;
import liquibase.database.core.MySQLDatabase;
import liquibase.diff.compare.CompareControl;
import liquibase.structure.DatabaseObject;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DiffToChangeLogTest {
    @Test
    public void getOrderedOutputTypes_isConsistant() throws Exception {
        MySQLDatabase database = new MySQLDatabase();
        DiffToChangeLog obj = new DiffToChangeLog(new liquibase.diff.DiffResult(new liquibase.snapshot.EmptyDatabaseSnapshot(database), new liquibase.snapshot.EmptyDatabaseSnapshot(database), new CompareControl()), null);
        for (Class<? extends ChangeGenerator> type : new Class[]{ UnexpectedObjectChangeGenerator.class, MissingObjectChangeGenerator.class, ChangedObjectChangeGenerator.class }) {
            List<Class<? extends DatabaseObject>> orderedOutputTypes = obj.getOrderedOutputTypes(type);
            for (int i = 0; i < 50; i++) {
                Assert.assertThat(("Error checking " + (type.getName())), orderedOutputTypes, Matchers.contains(obj.getOrderedOutputTypes(type).toArray()));
            }
        }
    }
}

