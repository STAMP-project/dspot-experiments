package liquibase.dbtest.mariadb;


import CatalogAndSchema.DEFAULT;
import LogType.LOG;
import java.sql.SQLSyntaxErrorException;
import liquibase.database.DatabaseFactory;
import liquibase.dbtest.AbstractIntegrationTest;
import liquibase.exception.DatabaseException;
import liquibase.executor.ExecutorService;
import liquibase.logging.LogService;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.statement.core.RawSqlStatement;
import liquibase.structure.core.Column;
import liquibase.structure.core.Schema;
import liquibase.structure.core.Table;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class MariaDBIntegrationTest extends AbstractIntegrationTest {
    public MariaDBIntegrationTest() throws Exception {
        super("mariadb", DatabaseFactory.getInstance().getDatabase("mariadb"));
    }

    @Test
    @Override
    public void testRunChangeLog() throws Exception {
        super.testRunChangeLog();// To change body of overridden methods use File | Settings | File Templates.

    }

    @Test
    public void snapshot() throws Exception {
        if ((getDatabase()) == null) {
            return;
        }
        runCompleteChangeLog();
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(getDatabase().getDefaultSchema(), getDatabase(), new liquibase.snapshot.SnapshotControl(getDatabase()));
        System.out.println(snapshot);
    }

    @Test
    public void dateDefaultValue() throws Exception {
        if ((getDatabase()) == null) {
            return;
        }
        ExecutorService.getInstance().getExecutor(getDatabase()).execute(new RawSqlStatement(("DROP TABLE IF " + "EXISTS ad")));
        try {
            ExecutorService.getInstance().getExecutor(getDatabase()).execute(new RawSqlStatement(("CREATE TABLE ad (\n" + ((((((((((("ad_id int(10) unsigned NOT NULL AUTO_INCREMENT,\n" + "advertiser_id int(10) unsigned NOT NULL,\n") + "ad_type_id int(10) unsigned NOT NULL,\n") + "name varchar(155) NOT NULL DEFAULT \'\',\n") + "label varchar(155)NOT NULL DEFAULT \'\',\n") + "description text NOT NULL,\n") + "active tinyint(1) NOT NULL DEFAULT \'0\',\n") + "created datetime NOT NULL DEFAULT \'0000-00-00 00:00:00\',\n") + "updated datetime DEFAULT \'0000-00-00 00:00:00\',\n") + "PRIMARY KEY (ad_id),\n") + "KEY active (active)\n") + ")"))));
        } catch (DatabaseException e) {
            if ((e.getCause()) instanceof SQLSyntaxErrorException) {
                LogService.getLog(getClass()).warning(LOG, "MariaDB returned DatabaseException", e);
                Assume.assumeTrue(("MariaDB seems to run in strict mode (no datetime literals with 0000-00-00 allowed). " + "Cannot run this test"), false);
            } else {
                throw e;
            }
        }
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(DEFAULT, getDatabase(), new liquibase.snapshot.SnapshotControl(getDatabase()));
        Column createdColumn = snapshot.get(new Column().setRelation(new Table().setName("ad").setSchema(new Schema())).setName("created"));
        Object defaultValue = createdColumn.getDefaultValue();
        Assert.assertNotNull(defaultValue);
        Assert.assertEquals("0000-00-00 00:00:00", defaultValue);
    }
}

