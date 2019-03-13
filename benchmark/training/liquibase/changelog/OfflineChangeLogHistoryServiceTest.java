package liquibase.changelog;


import ChangeSet.ExecType.EXECUTED;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @see https://liquibase.jira.com/browse/CORE-2334
 */
public class OfflineChangeLogHistoryServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Test ChangeLog table update SQL generation with outputLiquibaseSql=true and outputLiquibaseSql=true
     */
    @Test
    public void testInitOfflineWithOutputLiquibaseSql() throws Exception {
        // Given
        StringWriter writer = new StringWriter();
        OfflineChangeLogHistoryService service = createService(writer, "true");
        ChangeSet changeSet = createChangeSet();
        // When
        service.init();
        service.setExecType(changeSet, EXECUTED);
        writer.close();
        // Assert
        Assert.assertTrue(writer.toString().contains("CREATE TABLE PUBLIC.DATABASECHANGELOG"));
        Assert.assertTrue(writer.toString().contains("INSERT INTO PUBLIC.DATABASECHANGELOG"));
    }

    /**
     * Test ChangeLog table update SQL generation with outputLiquibaseSql=true and outputLiquibaseSql=data_only
     */
    @Test
    public void testInitOfflineWithOutputLiquibaseSqlAndNoDdl() throws Exception {
        // Given
        StringWriter writer = new StringWriter();
        OfflineChangeLogHistoryService service = createService(writer, "data_only");
        ChangeSet changeSet = createChangeSet();
        // When
        service.init();
        service.setExecType(changeSet, EXECUTED);
        writer.close();
        // Assert
        Assert.assertFalse(writer.toString().contains("CREATE TABLE PUBLIC.DATABASECHANGELOG"));
        Assert.assertTrue(writer.toString().contains("INSERT INTO PUBLIC.DATABASECHANGELOG"));
    }
}

